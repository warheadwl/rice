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
package org.kuali.rice.kew.exception;

/**
 * This error is thrown whenever a child document type is trying to be processed before its 
 * parent document type has been parsed; this provides a means for delaying the processing
 * of child doc types until their parents are parsed.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class InvalidParentDocTypeException extends InvalidXmlException {
	
	/** The name of the parent document that still needs to be parsed. */
	private final String parentName;
	/** The name of the child document that was expecting the parentName document to exist. */
	private final String childName;
	
	/**
	 * Constructs an InvalidParentDocTypeException with null document type parent & child names.
	 */
	public InvalidParentDocTypeException() {
		super();
		parentName = null;
		childName = null;
	}
	
	/**
	 * Constructs an InvalidParentDocTypeException, given a document type parent name and a child name.
	 * 
	 * @param docParent The name of the unprocessed document type parent.
	 * @param docChild The name of the unprocessed document type child.
	 */
	public InvalidParentDocTypeException(String docParent, String docChild) {
		super();
		parentName = docParent;
		childName = docChild;
	}
	
	/**
	 * Constructs an InvalidParentDocTypeException, given a document type parent name, a child name, and an error message.
	 * 
	 * @param docParent The name of the unprocessed document type parent.
	 * @param docChild The name of the unprocessed document type child.
	 * @param message The error message.
	 */
	public InvalidParentDocTypeException(String docParent, String docChild, String message) {
		super(message);
		parentName = docParent;
		childName = docChild;
	}

	/**
	 * Constructs an InvalidParentDocTypeException, given a document type parent name, a child name, an error message, and a cause.
	 * 
	 * @param docParent The name of the unprocessed document type parent.
	 * @param docChild The name of the unprocessed document type child.
	 * @param message The error message.
	 * @param throwable The cause.
	 */
	public InvalidParentDocTypeException(String docParent, String docChild, String message, Throwable throwable) {
		super(message, throwable);
		parentName = docParent;
		childName = docChild;
	}

	/**
	 * Constructs an InvalidParentDocTypeException, given a document type parent name, a child name, and a cause.
	 * 
	 * @param docParent The name of the unprocessed document type parent.
	 * @param docChild The name of the unprocessed document type child.
	 * @param throwable The cause.
	 */
	public InvalidParentDocTypeException(String docParent, String docChild, Throwable throwable) {
		super(throwable);
		parentName = docParent;
		childName = docChild;
	}
	
	/**
	 * Retrieves the name of the parent document type that has not been processed yet.
	 * 
	 * @return The name of the unprocessed document type parent, which may or may not be null.
	 */
	public String getParentName() {
		return parentName;
	}
	
	/**
	 * Retrieves the name of the child document type that depends on the given parent.
	 * 
	 * @return The name of the unprocessed document type child, which may or may not be null.
	 */
	public String getChildName() {
		return childName;
	}
}