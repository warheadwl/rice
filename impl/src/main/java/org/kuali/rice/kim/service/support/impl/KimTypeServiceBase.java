/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.service.support.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeAttributeInfo;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.kns.datadictionary.RelationshipDefinition;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.datadictionary.validation.ValidationPattern;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.TypeUtils;
import org.kuali.rice.kns.web.comparator.StringValueComparator;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimTypeServiceBase implements KimTypeService {

	protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KimTypeServiceBase.class);

	private BusinessObjectService businessObjectService;
	private DictionaryValidationService dictionaryValidationService;
	private DataDictionaryService dataDictionaryService;
	private KimTypeInfoService typeInfoService;
	protected List<String> workflowRoutingAttributes = new ArrayList<String>();
	protected List<String> requiredAttributes = new ArrayList<String>();
	protected boolean checkRequiredAttributes = false;

	protected KimTypeInfoService getTypeInfoService() {
		if ( typeInfoService == null ) {
			typeInfoService = KIMServiceLocator.getTypeInfoService();
		}
		return typeInfoService;
	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	protected DictionaryValidationService getDictionaryValidationService() {
		if ( dictionaryValidationService == null ) {
			dictionaryValidationService = KNSServiceLocator.getDictionaryValidationService();
		}
		return dictionaryValidationService;
	}

	protected DataDictionaryService getDataDictionaryService() {
		if ( dataDictionaryService == null ) {
			dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
		}
		return this.dataDictionaryService;
	}

	/**
	 * Returns null, to indicate that there is no custom workflow document needed for this type.
	 *
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getWorkflowDocumentTypeName()
	 */
	public String getWorkflowDocumentTypeName() {
		return null;
	}

	/**
	 *
	 * This method matches input attribute set entries and standard attribute set entries using literal string match.
	 *
	 */
	protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
		if ( storedAttributeSet == null || inputAttributeSet == null ) {
			return true;
		}
		for ( Map.Entry<String, String> entry : storedAttributeSet.entrySet() ) {
			if (inputAttributeSet.containsKey(entry.getKey()) && !StringUtils.equals(inputAttributeSet.get(entry.getKey()), entry.getValue()) ) {
				return false;
			}
		}
		return true;
	}

	public AttributeSet translateInputAttributeSet(AttributeSet qualification){
		return qualification;
	}

	/**
	 *
	 * This method ...
	 */
	public boolean performMatches(AttributeSet inputAttributeSet, List<AttributeSet> storedAttributeSets){
		for ( AttributeSet storedAttributeSet : storedAttributeSets ) {
			// if one matches, return true
			if ( performMatch(inputAttributeSet, storedAttributeSet) ) {
				return true;
			}
		}
		return false;
	}

	protected Map<String, KimAttributeImpl> getAttributeImpls(AttributeSet attributes){
		Map<String, KimAttributeImpl> attributeImpls = new HashMap<String, KimAttributeImpl>();
		for ( String attributeName: attributes.keySet() ) {
			attributeImpls.put(attributeName, getAttributeImpl(attributeName));
		}
		return attributeImpls;
	}
	
	protected KimAttributeImpl getAttributeImpl(String attributeName){
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KNSPropertyConstants.ATTRIBUTE_NAME, attributeName);
		return (KimAttributeImpl) getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
	}
	
	/**
	 * This is the default implementation.  It calls into the service for each attribute to
	 * validate it there.  No combination validation is done.  That should be done
	 * by overriding this method.
	 *
	 * @see org.kuali.rice.kim.service.support.KimTypeService#validateAttributes(AttributeSet)
	 */
	public AttributeSet validateAttributes(String kimTypeId, AttributeSet attributes) {
		AttributeSet validationErrors = new AttributeSet();
		if ( attributes == null ) {
			return validationErrors;
		}
		Map<String, KimAttributeImpl> attributeImpls = getAttributeImpls(attributes);
		
		for ( String attributeName : attributes.keySet() ) {
            KimAttributeImpl attributeImpl = attributeImpls.get(attributeName);
			List<String> attributeErrors = null;
			try {
				if ( attributeImpl.getComponentName() == null) {
					attributeErrors = validateNonDataDictionaryAttribute(attributeName, attributes.get( attributeName ), true);
				} else {
					// create an object of the proper type per the component
		            Object componentObject = Class.forName( attributeImpl.getComponentName() ).newInstance();
		            // get the bean utils descriptor for accessing the attribute on that object
					PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(componentObject, attributeImpl.getAttributeName());
					if ( propertyDescriptor != null ) {
						// set the value on the object so that it can be checked
						propertyDescriptor.getWriteMethod().invoke( componentObject, attributes.get( attributeName ) );
						attributeErrors = validateDataDictionaryAttribute(kimTypeId, attributeImpl.getComponentName(), componentObject, propertyDescriptor);
					} else {
						LOG.warn( "Unable to obtain property descriptor for: " + attributeImpl.getClass().getName() + "/" + attributeImpl.getAttributeName() );
					}
				}
			} catch (Exception e) {
				LOG.error("Unable to validate attribute: " + attributeName, e);
			}

			if ( attributeErrors != null ) {
				for ( String err : attributeErrors ) {
					validationErrors.put(attributeName, err);
				}
			}
		}
		
		Map<String, List<String>> referenceCheckErrors = validateReferencesExistAndActive(attributes, validationErrors);
		for ( String attributeName : referenceCheckErrors.keySet() ) {
			List<String> attributeErrors = referenceCheckErrors.get(attributeName);
			for ( String err : attributeErrors ) {
				validationErrors.put(attributeName, err);
			}
		}
		
		return validationErrors;
	}

	
	protected Map<String, List<String>> validateReferencesExistAndActive(AttributeSet attributes, Map<String, String> previousValidationErrors) {
		Map<String, KimAttributeImpl> attributeImpls = new HashMap<String, KimAttributeImpl>();
		Map<String, BusinessObject> componentClassInstances = new HashMap<String, BusinessObject>();
		Map<String, List<String>> errors = new HashMap<String, List<String>>();
		
		for ( String attributeName : attributes.keySet() ) {
			Map<String,String> criteria = new HashMap<String,String>();
            criteria.put(KNSPropertyConstants.ATTRIBUTE_NAME, attributeName);
            KimAttributeImpl attributeImpl = (KimAttributeImpl) getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
			attributeImpls.put(attributeName, attributeImpl);
			
			if (StringUtils.isNotBlank(attributeImpl.getComponentName())) {
				if (!componentClassInstances.containsKey(attributeImpl.getComponentName())) {
					try {
						Class<?> componentClass = Class.forName( attributeImpl.getComponentName() );
						if (!BusinessObject.class.isAssignableFrom(componentClass)) {
							LOG.warn("Class " + componentClass.getName() + " does not implement BusinessObject.  Unable to perform reference existence and active validation");
							continue;
						}
						BusinessObject componentInstance = (BusinessObject) componentClass.newInstance();
						componentClassInstances.put(attributeImpl.getComponentName(), componentInstance);
					}
					catch (Exception e) {
						LOG.error("Unable to instantiate class for attribute: " + attributeName, e);
					}
				}
			}
		}
		
		// now that we have instances for each component class, try to populate them with any attribute we can, assuming there were no other validation errors associated with it
		for ( String attributeName : attributes.keySet() ) {
			if (!previousValidationErrors.containsKey(attributeName)) {
				for (Object componentInstance : componentClassInstances.values()) {
					try {
						ObjectUtils.setObjectProperty(componentInstance, attributeName, attributes.get(attributeName));
					} 
					catch (NoSuchMethodException e) {
						// this is expected since not all attributes will be in all components
					}
					catch (Exception e) {
						LOG.error("Unable to set object property class: " + componentInstance.getClass().getName() + " property: " + attributeName, e);
					}
				}
			}
		}
		
		for (String componentClass : componentClassInstances.keySet()) {
			BusinessObject componentInstance = componentClassInstances.get(componentClass);
			
			List<RelationshipDefinition> relationships = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(componentClass).getRelationships();
			if (relationships == null) {
				continue;
			}
			
			for (RelationshipDefinition relationshipDefinition : relationships) {
				List<PrimitiveAttributeDefinition> primitiveAttributes = relationshipDefinition.getPrimitiveAttributes();
				
				// this code assumes that the last defined primitiveAttribute is the attributeToHighlightOnFail
				String attributeToHighlightOnFail = primitiveAttributes.get(primitiveAttributes.size() - 1).getSourceName();
				
				// TODO: will this work for user ID attributes?
				
				if (!attributes.containsKey(attributeToHighlightOnFail)) {
					// if the attribute to highlight wasn't passed in, don't bother validating
					continue;
				}
				
				String attributeDisplayLabel;
				if (attributeImpls.containsKey(attributeToHighlightOnFail) && StringUtils.isNotBlank(attributeImpls.get(attributeToHighlightOnFail).getComponentName())) {
					attributeDisplayLabel = getDataDictionaryService().getAttributeLabel(attributeImpls.get(attributeToHighlightOnFail).getComponentName(), attributeToHighlightOnFail);
				}
				else {
					attributeDisplayLabel = attributeImpls.get(attributeToHighlightOnFail).getAttributeLabel();
				}
				
				getDictionaryValidationService().validateReferenceExistsAndIsActive(componentInstance, relationshipDefinition.getObjectAttributeName(),
						attributeToHighlightOnFail, attributeDisplayLabel);
				
				errors.put(attributeToHighlightOnFail, extractErrorsFromGlobalVariablesErrorMap(attributeToHighlightOnFail));
			}
		}
		return errors;
	}
	
    protected void validateAttributeRequired(String kimTypeId, String objectClassName, String attributeName, Object attributeValue, String errorKey) {
        // check if field is a required field for the business object
        if (attributeValue == null || (attributeValue instanceof String && StringUtils.isBlank((String) attributeValue))) {
        	AttributeDefinitionMap map = getAttributeDefinitions(kimTypeId);
        	AttributeDefinition definition = map.getByAttributeName(attributeName);
        	
            Boolean required = definition.isRequired();
            ControlDefinition controlDef = definition.getControl();

            if (required != null && required.booleanValue() && !(controlDef != null && controlDef.isHidden())) {

                // get label of attribute for message
                String errorLabel = getAttributeErrorLabel(definition);
                GlobalVariables.getErrorMap().putError(errorKey, RiceKeyConstants.ERROR_REQUIRED, errorLabel);
            }
        }
    }
    
	protected List<String> validateDataDictionaryAttribute(String kimTypeId, String entryName, Object object, PropertyDescriptor propertyDescriptor) {
		validatePrimitiveFromDescriptor(kimTypeId, entryName, object, propertyDescriptor);
		return extractErrorsFromGlobalVariablesErrorMap(propertyDescriptor.getName());
	}

    protected void validatePrimitiveFromDescriptor(String kimTypeId, String entryName, Object object, PropertyDescriptor propertyDescriptor) {
        // validate the primitive attributes if defined in the dictionary
        if (null != propertyDescriptor && getDataDictionaryService().isAttributeDefined(entryName, propertyDescriptor.getName())) {
            Object value = ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
            Class<? extends Object> propertyType = propertyDescriptor.getPropertyType();

            if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {

                // check value format against dictionary
                if (value != null && StringUtils.isNotBlank(value.toString())) {
                    if (!TypeUtils.isTemporalClass(propertyType)) {
                        validateAttributeFormat(kimTypeId, entryName, propertyDescriptor.getName(), value.toString(), propertyDescriptor.getName());
                    }
                }
                else {
                	// if it's blank, then we check whether the attribute should be required
                    validateAttributeRequired(kimTypeId, entryName, propertyDescriptor.getName(), value, propertyDescriptor.getName());
                }
            }
        }
    }
    
    /**
     * Constant defines a validation method for an attribute value.
     * <p>Value is "validate"
     */
    public static final String VALIDATE_METHOD="validate";
    
    protected String getAttributeErrorLabel(AttributeDefinition definition) {
        String longAttributeLabel = definition.getLabel();
        String shortAttributeLabel = definition.getShortLabel();
        return longAttributeLabel + " (" + shortAttributeLabel + ")";
    }
    
    protected Pattern getAttributeValidatingExpression(AttributeDefinition definition) {
    	Pattern regex = null;
        if (definition != null) {
            if (definition.hasValidationPattern()) {
                regex = definition.getValidationPattern().getRegexPattern();
            } else {
                // workaround for existing calls which don't bother checking for null return values
                regex = Pattern.compile(".*");
            }
        }

        return regex;
    }
    
    protected Class<? extends Formatter> getAttributeFormatter(AttributeDefinition definition) {
        Class<? extends Formatter> formatterClass = null;
        if (definition != null) {
            if (definition.hasFormatterClass()) {
                formatterClass = definition.getFormatterClass();
            }
        }
        return formatterClass;
    }
    
	public String getAttributeValidatingErrorMessageKey(AttributeDefinition definition) {
        if (definition != null) {
        	if (definition.hasValidationPattern()) {
        		ValidationPattern validationPattern = definition.getValidationPattern();
        		return validationPattern.getValidationErrorMessageKey();
        	}
        }
        return null;
	}
	
	public String[] getAttributeValidatingErrorMessageParameters(AttributeDefinition definition) {
        if (definition != null) {
        	if (definition.hasValidationPattern()) {
        		ValidationPattern validationPattern = definition.getValidationPattern();
        		String attributeLabel = getAttributeErrorLabel(definition);
        		return validationPattern.getValidationErrorMessageParameters(attributeLabel);
        	}
        }
        return null;
	}
    
	protected BigDecimal getAttributeExclusiveMin(AttributeDefinition definition) {
        return definition == null ? null : definition.getExclusiveMin();
    }

	protected BigDecimal getAttributeInclusiveMax(AttributeDefinition definition) {
        return definition == null ? null : definition.getInclusiveMax();
    }
	
    protected void validateAttributeFormat(String kimTypeId, String objectClassName, String attributeName, String attributeValue, String errorKey) {
    	AttributeDefinitionMap attributeDefinitions = getAttributeDefinitions(kimTypeId);
    	AttributeDefinition definition = attributeDefinitions.getByAttributeName(attributeName);
    	
        String errorLabel = getAttributeErrorLabel(definition);

        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("(bo, attributeName, attributeValue) = (" + objectClassName + "," + attributeName + "," + attributeValue + ")");
        }

        if (StringUtils.isNotBlank(attributeValue)) {
            Integer maxLength = definition.getMaxLength();
            if ((maxLength != null) && (maxLength.intValue() < attributeValue.length())) {
                GlobalVariables.getErrorMap().putError(errorKey, RiceKeyConstants.ERROR_MAX_LENGTH, new String[] { errorLabel, maxLength.toString() });
                return;
            }
            Pattern validationExpression = getAttributeValidatingExpression(definition);
            if (validationExpression != null && !validationExpression.pattern().equals(".*")) {
            	if ( LOG.isDebugEnabled() ) {
            		LOG.debug("(bo, attributeName, validationExpression) = (" + objectClassName + "," + attributeName + "," + validationExpression + ")");
            	}

                if (!validationExpression.matcher(attributeValue).matches()) {
                    boolean isError=true;
                    // Calling formatter class
                    Class<?> formatterClass=getAttributeFormatter(definition);
                    if (formatterClass != null) {
                        try {
                            Method validatorMethod=formatterClass.getDeclaredMethod(
                                    VALIDATE_METHOD, new Class<?>[] {String.class});
                            Object o=validatorMethod.invoke(
                                    formatterClass.newInstance(), attributeValue);
                            if (o instanceof Boolean) {
                                isError=!((Boolean)o).booleanValue();
                            }
                        } catch (Exception e) {
                            LOG.warn(e.getMessage(), e);
                        }
                    }
                    if (isError) {
                    	String errorMessageKey = getAttributeValidatingErrorMessageKey(definition);
                    	String[] errorMessageParameters = getAttributeValidatingErrorMessageParameters(definition);
                        GlobalVariables.getErrorMap().putError(errorKey, errorMessageKey, errorMessageParameters);
                    }
                    return;
                }
            }
            BigDecimal exclusiveMin = getAttributeExclusiveMin(definition);
            if (exclusiveMin != null) {
                try {
                    if (exclusiveMin.compareTo(new BigDecimal(attributeValue)) >= 0) {
                        GlobalVariables.getErrorMap().putError(errorKey, RiceKeyConstants.ERROR_EXCLUSIVE_MIN,
                        // todo: Formatter for currency?
                                new String[] { errorLabel, exclusiveMin.toString() });
                        return;
                    }
                }
                catch (NumberFormatException e) {
                    // quash; this indicates that the DD contained a min for a non-numeric attribute
                }
            }
            BigDecimal inclusiveMax = getAttributeInclusiveMax(definition);
            if (inclusiveMax != null) {
                try {
                    if (inclusiveMax.compareTo(new BigDecimal(attributeValue)) < 0) {
                        GlobalVariables.getErrorMap().putError(errorKey, RiceKeyConstants.ERROR_INCLUSIVE_MAX,
                        // todo: Formatter for currency?
                                new String[] { errorLabel, inclusiveMax.toString() });
                        return;
                    }
                }
                catch (NumberFormatException e) {
                    // quash; this indicates that the DD contained a max for a non-numeric attribute
                }
            }
        }
    }
    
	protected List<String> extractErrorsFromGlobalVariablesErrorMap(String attributeName) {
		Object results = GlobalVariables.getErrorMap().getErrorMessagesForProperty(attributeName);
		List<String> errors = new ArrayList<String>();
        if (results instanceof String) {
        	errors.add((String)results);
        } else if ( results != null) {
        	if (results instanceof List) {
	        	List<?> errorList = (List<?>)results;
	        	for (Object msg : errorList) {
	        		ErrorMessage errorMessage = (ErrorMessage)msg;
	        		String retVal = errorMessage.getErrorKey()+":";
	        		for (String param : errorMessage.getMessageParameters()) {
	        			retVal = retVal + param +";";
	        		}
	        		errors.add(retVal);
				}
	        } else {
	        	String [] temp = (String []) results;
	        	for (String string : temp) {
					errors.add(string);
				}
	        }
        }
        GlobalVariables.getErrorMap().removeAllErrorMessagesForProperty(attributeName);
        return errors;
	}
	
	protected List<String> validateNonDataDictionaryAttribute(String attributeName, String attributeValue, boolean validateRequired) {
		return new ArrayList<String>();
	}

	@SuppressWarnings("unchecked")
	protected List<KeyLabelPair> getDataDictionaryAttributeValues(KimAttributeImpl attributeImpl) {
		AttributeDefinition definition = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(attributeImpl.getComponentName()).getAttributeDefinition(attributeImpl.getAttributeName());
		List<KeyLabelPair> pairs = new ArrayList<KeyLabelPair>();
		Class<? extends KeyValuesFinder> keyValuesFinderName = (Class<? extends KeyValuesFinder>)definition.getControl().getValuesFinderClass();
		try {
			KeyValuesFinder finder = keyValuesFinderName.newInstance();
			pairs = finder.getKeyValues();
		} catch (Exception e) {
			LOG.error("Unable to build a KeyValuesFinder for " + attributeImpl.getAttributeName(), e);
		}
		return pairs;
	}

	protected List<KeyLabelPair> getNonDataDictionaryAttributeValues(String attributeName) {
		return new ArrayList<KeyLabelPair>();
	}

	// FIXME: the attributeName is not guaranteed to be unique!
	public List<KeyLabelPair> getAttributeValidValues(String attributeName) {
		Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("attributeName", attributeName);
        KimAttributeImpl attributeImpl = (KimAttributeImpl) getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
        return attributeImpl.getComponentName() == null ? getNonDataDictionaryAttributeValues(attributeName) : getDataDictionaryAttributeValues(attributeImpl);
	}

	@SuppressWarnings("unchecked")
	protected AttributeDefinition getDataDictionaryAttributeDefinition( String namespaceCode, KimTypeAttributeInfo typeAttribute) {
		KimDataDictionaryAttributeDefinition definition = new KimDataDictionaryAttributeDefinition();
		String componentClassName = typeAttribute.getComponentName();
		String attributeName = typeAttribute.getAttributeName();
		AttributeDefinition baseDefinition;
		try {
			baseDefinition = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(componentClassName).getAttributeDefinition(attributeName);
		} catch ( Exception ex ) {
			LOG.error( "Unable to get base DD definition for " + componentClassName + "." + attributeName, ex );
			return definition;
		}

		// copy all the base attributes
		definition.setName( baseDefinition.getName() );
		definition.setLabel( baseDefinition.getLabel() );
		definition.setShortLabel( baseDefinition.getShortLabel() );
		definition.setMaxLength( baseDefinition.getMaxLength() );
		definition.setRequired( baseDefinition.isRequired() );

		if (baseDefinition.getFormatterClass() != null) {
			definition.setFormatterClass(baseDefinition.getFormatterClass());
		}
		ControlDefinition control = copy(baseDefinition.getControl());
//		if (control.getValuesFinderClass() != null) {
//			control.setValuesFinderClass(KimAttributeValuesFinder.class);
//		}
		definition.setControl(control);
		definition.setSortCode(typeAttribute.getSortCode());
		definition.setKimAttrDefnId(typeAttribute.getKimAttributeId());
		definition.setApplicationUrl( Utilities.substituteConfigParameters( namespaceCode, typeAttribute.getApplicationUrl() ) );

		Map<String, String> lookupInputPropertyConversionsMap = new HashMap<String, String>();
		Map<String, String> lookupReturnPropertyConversionsMap = new HashMap<String, String>();
		try {
			Class<? extends BusinessObject> componentClass = (Class<? extends BusinessObject>)Class.forName(componentClassName);
			BusinessObject sampleComponent = componentClass.newInstance();
			List<String> displayedFieldNames = new ArrayList<String>( 1 );
			displayedFieldNames.add( attributeName );
			Field field = FieldUtils.getPropertyField(componentClass, attributeName, false);
			if ( field != null ) {
				field = LookupUtils.setFieldQuickfinder( sampleComponent, attributeName, field, displayedFieldNames );
				if ( StringUtils.isNotBlank( field.getQuickFinderClassNameImpl() ) ) {
					Class<? extends BusinessObject> lookupClass = (Class<? extends BusinessObject>)Class.forName( field.getQuickFinderClassNameImpl() );
					definition.setLookupBoClass( lookupClass );
					if ( field.getLookupParameters() != null ) {
						String [] lookupInputPropertyConversions = field.getLookupParameters().split(",");
						for (String string : lookupInputPropertyConversions) {
							String [] keyVal = string.split(":");
							lookupInputPropertyConversionsMap.put(keyVal[0], keyVal[1]);
						}
						definition.setLookupInputPropertyConversions(lookupInputPropertyConversionsMap);
					}
					if ( field.getFieldConversions() != null ) {
						String [] lookupReturnPropertyConversions = field.getFieldConversions().split(",");
						for (String string : lookupReturnPropertyConversions) {
							String [] keyVal = string.split(":");
							lookupReturnPropertyConversionsMap.put(keyVal[0], keyVal[1]);
						}
						definition.setLookupReturnPropertyConversions(lookupReturnPropertyConversionsMap);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Unable to get DD data for: " + typeAttribute, e);
		}
		return definition;
	}

	@SuppressWarnings("unchecked")
	private <T> T copy(final T original) {
		if ( original == null ) {
			return null;
		}
		T copy = null;
		try {
			copy = (T) original.getClass().newInstance();
			Class copyClass = copy.getClass();
	    	do {
	    		for (java.lang.reflect.Field copyField : copyClass.getDeclaredFields()) {
    				copyField.setAccessible(true);
    				int mods = copyField.getModifiers();
    				if (!Modifier.isFinal(mods) && !Modifier.isStatic(mods)) {
    					copyField.set(copy, copyField.get(original));
    				}
	    		}
	    		copyClass = copyClass.getSuperclass();
	    	} while (copyClass != null && !(copyClass.equals(Object.class)));
		} catch (Exception e) {
			LOG.error("Unable to copy " + original, e);
		}
		return copy;
	}

	protected AttributeDefinition getNonDataDictionaryAttributeDefinition(KimTypeAttributeInfo typeAttribute) {
		KimNonDataDictionaryAttributeDefinition definition = new KimNonDataDictionaryAttributeDefinition();
		definition.setName(typeAttribute.getAttributeName());
		definition.setLabel(typeAttribute.getAttributeLabel());
		definition.setSortCode(typeAttribute.getSortCode());
		definition.setKimAttrDefnId(typeAttribute.getKimAttributeId());
		return definition;
	}

	private Map<String,AttributeDefinitionMap> attributeDefinitionCache = new HashMap<String,AttributeDefinitionMap>();

	public AttributeDefinitionMap getAttributeDefinitions(String kimTypeId) {
		AttributeDefinitionMap definitions = attributeDefinitionCache.get( kimTypeId );
		if ( definitions == null ) {
			List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);
			definitions = new AttributeDefinitionMap();
	        KimTypeInfo kimType = getTypeInfoService().getKimType(kimTypeId);
	        if ( kimType != null ) {
				String nsCode = kimType.getNamespaceCode();	        
				for (KimTypeAttributeInfo typeAttribute : kimType.getAttributeDefinitions()) {
					AttributeDefinition definition = null;
					if (typeAttribute.getComponentName() == null) {
						definition = getNonDataDictionaryAttributeDefinition(typeAttribute);
					} else {
						definition = getDataDictionaryAttributeDefinition(nsCode,typeAttribute);
					}
					if(uniqueAttributes!=null && uniqueAttributes.contains(definition.getName())){
						definition.setUnique(true);
					}
					// Perform a parameterized substitution on the applicationUrl
//					String url = typeAttribute.getApplicationUrl();
//					url = Utilities.substituteConfigParameters(nsCode, url);
//					kai.setApplicationUrl(url);
					
					// TODO : use id for defnid ?
			//		definition.setId(typeAttribute.getKimAttributeId());
					// FIXME: I don't like this - if two attributes have the same sort code, they will overwrite each other
					definitions.put(typeAttribute.getSortCode(), definition);
				}
				// attributeDefinitionCache.put( kimTypeId, definitions );
	        } else {
	        	LOG.warn( "Unable to resolve KIM Type: " + kimTypeId + " - returning an empty AttributeDefinitionMap." );
	        }
		}
		return definitions;
	}

	protected final String COMMA_SEPARATOR = ", ";

	protected void validateRequiredAttributesAgainstReceived(AttributeSet receivedAttributes){
		// abort if type does not want the qualifiers to be checked
		if ( !isCheckRequiredAttributes() ) {
			return;
		}
		// abort if the list is empty, no attributes need to be checked
		if ( requiredAttributes == null || requiredAttributes.isEmpty() ) {
			return;
		}
		List<String> missingAttributes = new ArrayList<String>();
		// if attributes are null or empty, they're all missing
		if ( receivedAttributes == null || receivedAttributes.isEmpty() ) {
			missingAttributes.addAll(requiredAttributes);			
		} else {
			for( String requiredAttribute : requiredAttributes ) {
				if( !receivedAttributes.containsKey(requiredAttribute) ) {
					missingAttributes.add(requiredAttribute);
				}
			}
		}
        if(missingAttributes.size()>0) {
        	StringBuffer errorMessage = new StringBuffer();
        	Iterator<String> attribIter = missingAttributes.iterator();
        	while ( attribIter.hasNext() ) {
        		errorMessage.append( attribIter.next() );
        		if( attribIter.hasNext() ) {
        			errorMessage.append( COMMA_SEPARATOR );
        		}
        	}
        	errorMessage.append( " not found in required attributes for this type." );
            throw new KimTypeAttributeValidationException(errorMessage.toString());
        }
	}

	/**
	 * Returns an empty list, indicating that no attributes from this
     * type should be passed to workflow.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getWorkflowRoutingAttributes(java.lang.String)
	 */
	public List<String> getWorkflowRoutingAttributes(String routeLevel) {
		return workflowRoutingAttributes;
	}
	
	public boolean validateUniqueAttributes(String kimTypeId, AttributeSet newAttributes, AttributeSet oldAttributes){
		boolean areAttributesUnique = true;
		List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);
		if(uniqueAttributes==null || uniqueAttributes.isEmpty()){
			areAttributesUnique = true;
		} else{
			if(areAttributesEqual(uniqueAttributes, newAttributes, oldAttributes)){
				areAttributesUnique = false;
			}
		}
		return areAttributesUnique;
	}
	
	protected boolean areAttributesEqual(List<String> uniqueAttributeNames, AttributeSet aSet1, AttributeSet aSet2){
		String attrVal1;
		String attrVal2;
		StringValueComparator comparator = new StringValueComparator();
		for(String uniqueAttributeName: uniqueAttributeNames){
			attrVal1 = getAttributeValue(aSet1, uniqueAttributeName);
			attrVal2 = getAttributeValue(aSet2, uniqueAttributeName);
			if(comparator.compare(attrVal1, attrVal2)!=0){
				return false;
			}
		}
		return true;
	}
	
	protected String getAttributeValue(AttributeSet aSet, String attributeName){
		if(StringUtils.isEmpty(attributeName)) return null;
		for(String attributeNameKey: aSet.keySet()){
			if(attributeName.equals(attributeNameKey))
				return aSet.get(attributeNameKey);
		}
		return null;
	}
	
	public List<String> getUniqueAttributes(String kimTypeId){
        List<String> uniqueAttributes = new ArrayList<String>();
        KimTypeInfo kimType = KIMServiceLocator.getTypeInfoService().getKimType(kimTypeId);
        if ( kimType != null ) {
	        for(KimTypeAttributeInfo attributeDefinition: kimType.getAttributeDefinitions()){
	        	uniqueAttributes.add(attributeDefinition.getAttributeName());
	        }
        } else {
        	LOG.error("Unable to retrieve a KimTypeInfo for the given type in getUniqueAttributes(): " + kimType );
        }
        return uniqueAttributes;
	}

	public AttributeSet validateUnmodifiableAttributes(String kimTypeId, AttributeSet originalAttributeSet, AttributeSet newAttributeSet){
		AttributeSet validationErrors = new AttributeSet();
		List<String> attributeErrors = null;
		List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);
		String mainAttributeValue = "";
		String delegationAttributeValue = "";
		KimAttributeImpl attributeImpl;
		for(String attributeNameKey: uniqueAttributes){
			attributeImpl = getAttributeImpl(attributeNameKey);
			mainAttributeValue = getAttributeValue(originalAttributeSet, attributeNameKey);
			delegationAttributeValue = getAttributeValue(newAttributeSet, attributeNameKey);

			if(!StringUtils.equals(mainAttributeValue, delegationAttributeValue)){
				GlobalVariables.getErrorMap().putError(
					attributeNameKey, RiceKeyConstants.ERROR_CANT_BE_MODIFIED, 
					dataDictionaryService.getAttributeLabel(attributeImpl.getComponentName(), attributeNameKey));
				attributeErrors = extractErrorsFromGlobalVariablesErrorMap(attributeNameKey);
			}
			if(attributeErrors!=null){
				for(String err:attributeErrors){
					validationErrors.put(attributeNameKey, err);
				}
			}
		}
		return validationErrors;
	}

	public boolean isCheckRequiredAttributes() {
		return this.checkRequiredAttributes;
	}

	public void setCheckRequiredAttributes(boolean checkRequiredAttributes) {
		this.checkRequiredAttributes = checkRequiredAttributes;
	}

}