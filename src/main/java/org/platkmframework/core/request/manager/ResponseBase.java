/*******************************************************************************
 * Copyright(c) 2023 the original author Eduardo Iglesias Taylor.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	 https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * 	Eduardo Iglesias Taylor - initial API and implementation
 *******************************************************************************/
package org.platkmframework.core.request.manager;

import java.util.HashMap;
import java.util.Map;


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public abstract class ResponseBase {

	private int status;
	private Map<String, Object> header;
	private Map<String, Long> headerDate;
	
	public ResponseBase() { 
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Map<String, Object> getHeader() {
		if(header == null) this.header = new HashMap<>();
		return header;
	}

	public void setHeader(String key, Object value) {
		getHeader().put(key, value); 
	} 
	
	public void setDateHeader(String key, long dateValue) {
		getDateHeader().put(key, dateValue);
		
	}

	public Map<String, Long> getDateHeader() {
		if(headerDate == null) this.headerDate = new HashMap<>();
		return headerDate;
	}

}
