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
package org.platkmframework.core.ioc.listener;
   

import org.platkmframework.content.ioc.ContentPropertiesConstant;
import org.platkmframework.content.ioc.ObjectContainer;
import org.platkmframework.content.ioc.SearchClasses;
import org.platkmframework.content.project.ProjectContent;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
 
 


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 * @author Eduardo Iglesias Taylor
 *
 */
public class IOListener implements ServletContextListener
{

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent)  
	{
		try 
		{ 
			String javaClassPath  = System.getProperty("java.class.path");
			String packagesPrefix = ProjectContent.instance().getAppProperties().getProperty(ContentPropertiesConstant.ORG_PLATKMFRAMEWORK_CONFIGURATION_PACKAGE_PREFIX);
			packagesPrefix+=",org.platkmframework";
			ObjectContainer.instance().process(javaClassPath, packagesPrefix.split(","), 
													ProjectContent.instance().getAppProperties(),
													new SearchClasses());
			
		} catch (Exception e) 
		{ 
			e.printStackTrace();
			System.exit(-1);
		}  
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {}

}
