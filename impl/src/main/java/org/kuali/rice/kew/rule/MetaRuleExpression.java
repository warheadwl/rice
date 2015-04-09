/*
 * Copyright 2007-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.rule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.WorkflowException;


/**
 * Expression implementation for "meta rules".  A "meta rule" consists of a sequence of
 * subordinate rule evaluations each processed according to an associated modifier:
 * <dl>
 *   <dt>next<dt>
 *   <dd>proceed with rule evaluation</dd>
 *   <dt>true</dt>
 *   <dd>if this rule evaluates to true, then return the responsibilities associated with the rule</dd>
 *   <dt>false</dt>
 *   <dd>if this rule evaluates to false, then return the responsibilities associated with the rule</dd>
 * </dl>
 * E.g.
 * <div><tt>bizRule1: next; bizRule2: true; bizRule3: false</tt></div>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MetaRuleExpression extends AccumulatingBSFRuleExpression {
    private static final Logger LOG = Logger.getLogger(MetaRuleExpression.class);

    @Override
    public RuleExpressionResult evaluate(Rule rule, RouteContext context) throws WorkflowException {
        RuleBaseValues ruleDefinition = rule.getDefinition();
        RuleExpressionDef exprDef = ruleDefinition.getRuleExpressionDef();
        if (exprDef == null) {
            throw new WorkflowException("No expression defined in rule definition: " + ruleDefinition);
        }
        String expression = exprDef.getExpression();
        if (StringUtils.isEmpty(expression)) {
            throw new WorkflowException("Empty expression in rule definition: " + ruleDefinition);
        }

        String lang = parseLang(ruleDefinition.getRuleExpressionDef().getType(), null);
        if (lang == null) {
            // if no language qualifier is specified, parse it as a built-in meta rule expression
            return evaluateBuiltinExpression(expression, rule, context);
        } else {
            return super.evaluate(rule, context);
        }
    }

    /**
     * Evaluates the builtin "meta" rule expression
     * @param expression the builtin meta rule expression
     * @param rule the rule
     * @param context the route context
     * @return RuleExpressionResult the result
     * @throws WorkflowException
     */
    private RuleExpressionResult evaluateBuiltinExpression(String expression, Rule rule, RouteContext context) throws WorkflowException {
        try {
            KRAMetaRuleEngine engine = new KRAMetaRuleEngine(expression);

            int responsibilityPriority = 0; // responsibility priority, lower value means higher priority (due to sort)...increment as we go
            RuleExpressionResult result = null;
            boolean success = false;
            List<RuleResponsibility> responsibilities = new ArrayList<RuleResponsibility>();
            while (!engine.isDone()) {
                result = engine.processSingleStatement(context);
                if (result.isSuccess() && result.getResponsibilities() != null) {
                    // accumulate responsibilities if the evaluation was successful
                    // make sure to reduce priority for each subsequent rule in order for sequential activation to work as desired
                    for (RuleResponsibility responsibility: result.getResponsibilities()) {
                        responsibility.setPriority(Integer.valueOf(responsibilityPriority));
                        responsibilities.add(responsibility);
                    }
                    // decrement responsibilityPriority for next rule expression result responsibilities
                    responsibilityPriority++;
                    success = true;
                }
            }
            result = new RuleExpressionResult(rule, success, responsibilities);
            LOG.debug("KRAMetaRuleExpression returning result: " + result);
            return result;
        } catch (ParseException pe) {
            throw new WorkflowException("Error parsing expression", pe);
        }
    }
}