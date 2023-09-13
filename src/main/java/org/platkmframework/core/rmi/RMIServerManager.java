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
package org.platkmframework.core.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.platkmframework.annotation.rmi.RMIServer;
import org.platkmframework.content.ioc.ObjectContainer;
import org.platkmframework.content.project.ProjectContent;


public class RMIServerManager {
	
	private static final Logger logger = LogManager.getLogger(RMIServerManager.class);
	
	private static RMIServerManager schedulerManager;
 
	Map<String, Object> map;
	
	private RMIServerManager() { 
		map = new HashMap<>();
	}
	
	public static RMIServerManager instance() {
		if(schedulerManager == null) schedulerManager = new RMIServerManager();
		return schedulerManager;
	}
	
	
	public void run(String name) throws RMIException {
		
		try {
			
			Object rmiServerObj = map.get(name);
			RMIServer rmiServer = rmiServerObj.getClass().getAnnotation(RMIServer.class);
			String port = ProjectContent.instance().parseValue(rmiServer.port());
			
			Remote stub = (Remote) UnicastRemoteObject.exportObject((Remote)rmiServerObj,Integer.valueOf(port));
			Registry registry = LocateRegistry.createRegistry(8087);
			registry.rebind(name, stub);
			
		} catch (RemoteException e) { 
			throw new RMIException(e);
		}
	}
	
	
	public void runAllOnStart() throws RMIException {
			 
		try {
			
			RMIServer rmiServer;
			String runOnStart;
			String name;
			String port;
			List<Object> list = ObjectContainer.instance().getListObjectByAnnontation(RMIServer.class);
			for (Object rmiServerObj : list) {
				rmiServer = rmiServerObj.getClass().getAnnotation(RMIServer.class);
				runOnStart = ProjectContent.instance().parseValue(rmiServer.runOnStart());
				name = ProjectContent.instance().parseValue(rmiServer.name());
				if(isRunOnStart(runOnStart)){
					port = ProjectContent.instance().parseValue(rmiServer.port());
					
					Remote stub = (Remote) UnicastRemoteObject.exportObject((Remote)rmiServerObj,Integer.valueOf(port));
					Registry registry = LocateRegistry.createRegistry(8087);
					registry.rebind(name, stub);
				}else {
					map.put(name, rmiServerObj);
				}
			}
			
		} catch (RemoteException e) { 
			throw new RMIException(e);
		}
			
			
	} 
	
	private boolean isRunOnStart(String runOnStart) {
		return StringUtils.isNotBlank(runOnStart) && Boolean.TRUE.toString().toLowerCase().equals(runOnStart.toLowerCase().trim());
	}
	
}
