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
package org.platkmframework.comon.service.exception;
 

import org.platkmframework.annotation.TruslyException;

import jakarta.servlet.ServletException;


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
@TruslyException
public class CustomServletException extends ServletException {

	private static final long serialVersionUID = 1L;
	
	private int status =  -1;
 
	public CustomServletException() {
		super(); 
	}
	
	public CustomServletException(String message) {
		super(message); 
	}
	
	public CustomServletException(int status, String message) { 
		super(message);
		this.status = status;
	}

	public CustomServletException(String message, Throwable rootCause) {
		super(message, rootCause); 
		
		if(rootCause instanceof StatusException)
			this.status = ((StatusException)rootCause).getStatus();
	}
	 

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


}