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
package org.platkmframework.core.request.caller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter; 
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.platkmframework.annotation.ClassMethod;
import org.platkmframework.annotation.HeaderParam;
import org.platkmframework.annotation.RequestBody;
import org.platkmframework.annotation.RequestParam;
import org.platkmframework.annotation.Service;
import org.platkmframework.annotation.UIFilterToSearchConverter;
import org.platkmframework.annotation.exception.UIFilterToSearchConverterException;
import org.platkmframework.comon.service.exception.ServiceException;
import org.platkmframework.content.ioc.ApiMethodInfo;
import org.platkmframework.content.ioc.ObjectContainer;
import org.platkmframework.core.request.exception.RequestProcessException;
import org.platkmframework.core.request.exception.ResourceNotFoundException;
import org.platkmframework.core.request.exception.ResourcePermissionException;
import org.platkmframework.core.request.exception.UnKnowProcessRequestException; 
import org.platkmframework.core.request.util.ValidateRequiredAttributeUtil;
import org.platkmframework.security.content.SecurityContent;
import org.platkmframework.util.JsonException;
import org.platkmframework.util.error.InvocationException;
import org.platkmframework.util.reflection.ReflectionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *  Call an object method
 *
 */
@Service
public class MethodCaller 
{
	
	private static final Logger logger = LogManager.getLogger(MethodCaller.class);


	/**
	 * 
	 * @param path
	 * @param req
	 * @param resp
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws ResourcePermissionException
	 * @throws UnKnowProcessRequestException
	 * @throws RequestProcessException
	 * @throws ServletException
	 * @throws ServiceException
	 * @throws UIFilterToSearchConverterException
	 */
	
	public Object execute(String path,
			  HttpServletRequest req, HttpServletResponse resp) throws ResourceNotFoundException, ResourcePermissionException, UnKnowProcessRequestException, RequestProcessException, ServletException, ServiceException, UIFilterToSearchConverterException{  
			
		 
		logger.info("searching object from -> " + path  + " - " +  req.getMethod());
		//String key = "{" + req.getMethod() + ":"  + path + "}";
		
		ApiMethodInfo apiMethodInfo = ObjectContainer.instance().getApiMethodInfo(path); // PathProccesor.getSessionObjectByPath(path, req.getMethod());
		if(apiMethodInfo == null)
			throw new ResourceNotFoundException("recurso no encontrado - " + req.getRequestURL().toString());
		
		List<Method> methods = apiMethodInfo.getMapMethod().
				entrySet().
				stream().
				filter((e)->(
						req.getMethod().equalsIgnoreCase(e.getKey())
						)
					).
				map(Map.Entry::getValue).
				findFirst().
				orElse(null);
		
		if(methods == null || methods.size() == 0) throw new ResourceNotFoundException("recurso no encontrado - " + req.getRequestURL().toString());
		
		String controllerPath = apiMethodInfo.getControllerPath();
		Method methodFound = methods.stream().filter( (m) ->(
			
				path.
				equalsIgnoreCase((controllerPath + "/" + m.getAnnotation(ClassMethod.class).name()))
			
		)).findFirst().orElse(null); 
		
		Map<String, Object> pathParameter = new HashMap<>();
		if(methodFound != null) return execute(apiMethodInfo, methodFound, pathParameter, req, resp);
		
		//path variable
		String[] arrayPath = path.split("/");
		String methodPath;
		String[] arrayMethodPath;
		Parameter[] parameters;
		String auxParam;
		String auxPath;
		
		for (Method method : methods){
			methodPath = method.getAnnotation(ClassMethod.class).name();
			if(StringUtils.isEmpty(methodPath) && path.equalsIgnoreCase(controllerPath)) return execute(apiMethodInfo, methodFound, pathParameter, req, resp);
			
			if(methodPath.contains("{")) {
				auxPath = controllerPath + "/" + methodPath;
				arrayMethodPath = auxPath.split("/");
				if(arrayPath.length == arrayMethodPath.length && method.getParameters().length >0) {
					
					pathParameter.clear();
					parameters = method.getParameters();
					 for (int i = 0; i < arrayMethodPath.length; i++) {
						if(arrayMethodPath[i].contains("{")) {
							auxParam = arrayMethodPath[i].replace("{","").replace("}","");
							
							for (Parameter parameter : parameters){
								if(parameter.isAnnotationPresent(RequestParam.class) && 
										parameter.getAnnotation(RequestParam.class).name().equalsIgnoreCase(auxParam)){  
									auxPath = auxPath.replace("{" + auxParam + "}", arrayPath[i]);
									pathParameter.put(auxPath, arrayPath[i]);
								} 
							}
						}
					}
					if(auxPath.equalsIgnoreCase(path)) return execute(apiMethodInfo, methodFound, pathParameter, req, resp);
				}
			}
		}		
		throw new ResourceNotFoundException("recurso no encontrado - " + req.getRequestURL().toString());
	}
	
	/**
	 * 
	 * @param apiMethodInfo
	 * @param method
	 * @param pathParameter
	 * @param req
	 * @param resp
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws ResourcePermissionException
	 * @throws UnKnowProcessRequestException
	 * @throws RequestProcessException
	 * @throws ServletException
	 * @throws ServiceException
	 * @throws UIFilterToSearchConverterException
	 */
	protected Object execute(ApiMethodInfo apiMethodInfo, Method method, Map<String, Object> pathParameter,
						  HttpServletRequest req, HttpServletResponse resp) throws ResourceNotFoundException, ResourcePermissionException, 
																UnKnowProcessRequestException, RequestProcessException, ServletException, ServiceException, UIFilterToSearchConverterException{ 
		try {
			
			checkAuthorizationPermission(apiMethodInfo, method);
 		
			Parameter[] parameters = method.getParameters();
			Object[] paramValue = null;
			if(parameters != null)
			{ 
				Object value; 
				paramValue = new Object[parameters.length];
				for (int i = 0; i < parameters.length; i++){
					
					Parameter parameter = parameters[i]; 
					value = null;
					
					logger.info("object parameter -> " + apiMethodInfo.getContObject().toString()  + " - " +  parameter.toString());
				 
					//is body json? 
					RequestBody requestBody;
					RequestParam requestParam;
					boolean required = false;
					String parameterName = null;
					if(parameter.isAnnotationPresent(RequestBody.class))
					{
						requestBody = parameter.getAnnotation(RequestBody.class);
						required = requestBody.required();
						Class<? extends UIFilterToSearchConverter>  uiFilterToSearchConverter = null;
						if(requestBody.converter().length>0) 
							uiFilterToSearchConverter = requestBody.converter()[0];
						
						
						value = ObjetTypeConverter.converBodyValueToObjectType(parameter, required, uiFilterToSearchConverter, req);
						
						if(value == null && requestBody.required())
							throw new ResourceNotFoundException("The body parameter value is required " );
						
						ValidateRequiredAttributeUtil.validate(value);
							
							//parameterName = "body";
							//strJson = Util.inputSteamToString(req.getInputStream());
							
							//if( (StringUtils.isEmpty(strJson)) && required)
							//	throw new RequestManagerException("The parameter value is required " + parameterName);
							 
							/**	if(parameter.getType().equals(List.class))
							{
							if(parameter.getParameterizedType() instanceof ParameterizedType
										&& ((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments().length>0)
								
									value = JsonUtil.jsonToListObject(strJson, (Class) ((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments()[0]); 
								else
									value = ObjetTypeConverter.converBodyValueToObjectType(strJson, parameter);
									 value = JsonUtil.jsonToObject(strJson, parameter.getType());
							}else 
								value = ObjetTypeConverter.converBodyValueToObjectType(strJson, parameter);
								//value = JsonUtil.jsonToObject(strJson, parameter.getType());
							*/ 
						
					}else if(parameter.isAnnotationPresent(RequestParam.class)){   
						requestParam = parameter.getAnnotation(RequestParam.class);
						required = requestParam.required();
						value = ObjetTypeConverter.converParamValueToObjectType(requestParam.name(),requestParam.required(),parameter, req, false, pathParameter.containsKey(parameterName)?pathParameter.get(parameterName):req.getParameter(requestParam.name()));
					
					}else if(parameter.isAnnotationPresent(HeaderParam.class)){
						Map<String, Object> header = new HashMap<>();
						HeaderParam headerParam = parameter.getAnnotation(HeaderParam.class); 
						String names = headerParam.names();
						if(StringUtils.isNotBlank(names)){
							String[] headerNames = names.split(",");
							String headerNameType; 
							String[] headerNameTypeArray; 
							if(headerNames != null && headerNames.length>0) {
								for (int j = 0; j < headerNames.length; j++) {
									headerNameType = headerNames[j];
									headerNameTypeArray = headerNameType.split(":");
									if(headerNameTypeArray.length == 1) {
										header.put(headerNameTypeArray[0], req.getHeader(headerNameTypeArray[0]));
									}else if(headerNameTypeArray.length > 1) {
										if(headerNameTypeArray[1] == "date") {
											header.put(headerNameTypeArray[0], req.getDateHeader(headerNameTypeArray[0]));
										}else {
											//TODO: No se reconoce otro tipo de datos
											header.put(headerNameTypeArray[0], req.getHeader(headerNameTypeArray[0]));
										}
									}
								}
							}
						}  
						value = header;
					} 
					if( (value == null || StringUtils.isEmpty(value.toString())) && required)
						throw new ResourceNotFoundException("The parameter value is required " + parameterName);
					  
					paramValue[i] = value;
				}  
			}
			
			return ReflectionUtil.invokeMethod(apiMethodInfo.getContObject(), method, paramValue); 
			
		} catch (InvocationException e){ 
			
			if(ObjectContainer.instance().containsException(e.getCause()))  
				throw new RequestProcessException(e.getCause());
			else
				throw new UnKnowProcessRequestException(e.getMessage());
			
		}  catch (IOException | JsonException | ParseException e) 
		{
			logger.error(e,e);
			throw new UnKnowProcessRequestException(e.getMessage());
		}   
	}

	private void checkAuthorizationPermission(ApiMethodInfo apiMethodInfo, Method method) throws ResourcePermissionException {
	  
		String[] controllRoles = apiMethodInfo.getControllerRoles();
		String[] methodRoles   = apiMethodInfo.getMethodRoles(method);
		
		if(methodRoles != null && methodRoles.length >0) {
			if(!containsAtLeastOneRole(methodRoles)) throw new ResourcePermissionException("no tiene permisos");
		
		}else if(controllRoles != null && controllRoles.length >0) {
			if(!containsAtLeastOneRole(controllRoles)) throw new ResourcePermissionException("no tiene permisos");
		}
	}

	private boolean containsAtLeastOneRole(String[] roles) { 
		if(roles == null || SecurityContent.instance().getPrincipal() == null) return false;
		for (int i = 0; i < roles.length; i++) {
			if(SecurityContent.instance().getPrincipal().credentials().contains(roles[i])) return true;
		} 
		return false;
	}
 
}
