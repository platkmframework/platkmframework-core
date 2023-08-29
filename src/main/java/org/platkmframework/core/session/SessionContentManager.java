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
  
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.platkmframework.core.security.PlatkmPrincipal;



/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 * information about the user session 
 *
 */
public class SessionContentManager 
{

	private ThreadLocal<SessionBean> userSessionThreadLocal = new ThreadLocal<SessionBean>();
	 
	private static SessionContentManager sessionContentManager;
	
	private SessionContentManager(){}
	
	public static SessionContentManager instance()
	{
		if(sessionContentManager == null)
			sessionContentManager = new SessionContentManager();
		
		return sessionContentManager;
	}

	public void clean()
	{
		userSessionThreadLocal.remove(); 
	}	
	
	public PlatkmPrincipal getPrincipal() {
		return getCurrentSessionBean().getPlatkmPrincipal();
	}


	public boolean isInactiveTimeOver(LocalDateTime now, LocalDateTime loginDateTim, long minutes) 
	{  
		return Duration.between(loginDateTim, now).toMinutes() > minutes;
	}

	public void setPrincipal(PlatkmPrincipal platkmPrincipal) {
		getCurrentSessionBean().setPlatkmPrincipal(platkmPrincipal);
	}

	public void setParameter(Map<String, Object> param) {
		getCurrentSessionBean().setParameters(param);
	}
	
	private SessionBean getCurrentSessionBean() {
		if(userSessionThreadLocal.get() == null) userSessionThreadLocal.set(new SessionBean());
		
		return userSessionThreadLocal.get();
	}
	
}
