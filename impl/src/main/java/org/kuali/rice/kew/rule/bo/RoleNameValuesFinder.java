/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kew.rule.bo;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kew.rule.Role;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiMaintenanceForm;

/**
 * A values finder for generating a list of Role names that can be selected for a given RuleTemplate.
 * 
 * This is dependant on the template selected on the maintenance document so it needs to use
 * GlobalVariables to get a reference to the KualiForm so it can examine the business object
 * and extract the role names from the RuleTemplate.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RoleNameValuesFinder extends KeyValuesBase {
	
	public List<KeyLabelPair> getKeyValues() {
		List<KeyLabelPair> roleNames = new ArrayList<KeyLabelPair>();
		if (GlobalVariables.getKualiForm() != null && GlobalVariables.getKualiForm() instanceof KualiMaintenanceForm) {
			KualiMaintenanceForm form = (KualiMaintenanceForm)GlobalVariables.getKualiForm();
			MaintenanceDocument document = (MaintenanceDocument)form.getDocument();
			Object businessObject = document.getDocumentBusinessObject();
			RuleBaseValues rule = null;
			if (businessObject instanceof RuleBaseValues) {
				rule = (RuleBaseValues)businessObject;
			} else if (businessObject instanceof RuleDelegation) {
				rule = ((RuleDelegation)businessObject).getDelegationRuleBaseValues();
			} else {
				throw new RiceRuntimeException("Cannot locate RuleBaseValues business object on maintenance document.  Business Object was " + businessObject);
			}
			RuleTemplate ruleTemplate = rule.getRuleTemplate();
			List<Role> roles = ruleTemplate.getRoles();
			for (Role role : roles) {
				roleNames.add(new KeyLabelPair(role.getName(), role.getLabel()));
			}
		}
		return roleNames;
	}

}