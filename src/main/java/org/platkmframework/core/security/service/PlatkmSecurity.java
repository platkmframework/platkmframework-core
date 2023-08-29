/*******************************************************************************
 *   Copyright(c) 2023 the original author or authors.
 *  
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  
 *        https://www.apache.org/licenses/LICENSE-2.0
 *  
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *******************************************************************************/
package org.platkmframework.core.security.service;

 

import org.platkmframework.comon.service.exception.ServiceException;
import org.platkmframework.core.security.core.xss.RequestInfo;

import jakarta.servlet.http.HttpServletResponse;


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public interface PlatkmSecurity {

	public boolean isLogingRequet(RequestInfo requestInfo);
	
	public LoginSuccessData login(RequestInfo requestInfo, HttpServletResponse resp) throws ServiceException;
	
	public void authentication(RequestInfo requestInfo, HttpServletResponse resp) throws ServiceException;
	
	public void logout(RequestInfo requestInfo)throws ServiceException;
	
	public boolean isLogoutRequest(RequestInfo requestInfo);
}
