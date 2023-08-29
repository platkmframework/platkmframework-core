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
package org.platkmframework.core.session;

import java.util.Map;

import org.platkmframework.core.security.PlatkmPrincipal; 


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public class SessionBean 
{
	private PlatkmPrincipal platkmPrincipal;
	private Map<String,Object> param;

	public PlatkmPrincipal getPlatkmPrincipal() {
		return platkmPrincipal;
	}

	public void setPlatkmPrincipal(PlatkmPrincipal platkmPrincipal) {
		this.platkmPrincipal = platkmPrincipal;
	}

	public void setParameters(Map<String, Object> param) {
		this.param = param;
	}

	public Map<String, Object> getParamters() {
		return param;
	}

	public void setParamters(Map<String, Object> param) {
		this.param = param;
	} 

	
}
