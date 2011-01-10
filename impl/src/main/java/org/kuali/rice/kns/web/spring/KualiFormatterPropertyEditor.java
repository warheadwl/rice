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
package org.kuali.rice.kns.web.spring;

import java.beans.PropertyEditorSupport;

import org.kuali.rice.kns.web.format.FormatException;
import org.kuali.rice.kns.web.format.Formatter;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KualiFormatterPropertyEditor extends PropertyEditorSupport {

	Formatter formatter;

	/**
     * This constructs a ...
     * 
     * @param formatter
     */
    public KualiFormatterPropertyEditor(Class formatterClass) {
	    super();
        try {
            formatter = (Formatter) formatterClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new FormatException("Couldn't create an instance of class " + formatterClass, e);
        }
        catch (IllegalAccessException e) {
            throw new FormatException("Couldn't create an instance of class " + formatterClass, e);
        }

//        if (settings != null)
//            formatter.setSettings(Collections.unmodifiableMap(settings));
        formatter.setPropertyType(formatterClass);
    }

	@Override
	public String getAsText() {
		return (String) formatter.formatForPresentation(getValue());
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		this.setValue(formatter.convertFromPresentationFormat(text));
//		String input = null;
//		
//		if(text != null) {
//			StringBuilder builder = new StringBuilder();
//			builder.append("/").append(text.toLowerCase()).append("/");
//			input = builder.toString();
//			
//			if(TRUE_VALUES.contains(input)) {
//				this.setValue(Boolean.TRUE);
//			}
//			else if(FALSE_VALUES.contains(input)) {
//				this.setValue(Boolean.FALSE);
//			}
//			else {
//				input = null;
//			}
//		}
//
//		if(input == null) {
//			throw new IllegalArgumentException("Invalid boolean input: " + text);
//		}
	}


}
