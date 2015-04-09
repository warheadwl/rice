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
package org.kuali.rice.kew.edl.extract;

import java.util.Iterator;

import org.kuali.rice.kew.edl.extract.dao.ExtractDAO;


public class ExtractServiceImpl implements ExtractService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExtractServiceImpl.class);

	private ExtractDAO extractDAO;

	public Dump getDumpByDocumentId(Long noteId) {
		return getExtractDAO().getDumpByRouteHeaderId(noteId);
	}

	public void saveDump(Dump dump) {
		try {
			getExtractDAO().saveDump(dump);
			if (! dump.getFields().isEmpty()){
				for (Iterator iter = dump.getFields().iterator(); iter.hasNext();) {
					Fields field = (Fields) iter.next();
					getExtractDAO().saveField(field);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setExtractDAO(ExtractDAO extractDAO) {
		this.extractDAO = extractDAO;
	}


	public void deleteDump(Long docId) {
		try {
			getExtractDAO().deleteDump(docId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ExtractDAO getExtractDAO() {
		return extractDAO;
	}
}