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
package org.platkmframework.core.request.filter;

import java.io.IOException; 
 
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.platkmframework.content.ioc.ObjectContainer;
import org.platkmframework.core.request.exception.CustomServletException;
import org.platkmframework.core.response.util.ResponseUtil;
import org.platkmframework.core.session.SessionContentManager;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse; 
  


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public class ExceptionFilter implements Filter
{
	
	private static final Logger logger = LogManager.getLogger(ExceptionFilter.class);
	
	@Override
	public void destroy() {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException 
	{
	   
		HttpServletResponse resp = (HttpServletResponse)response;
		boolean throwNoControlledError = true;
		try{
			
			chain.doFilter(request, resp);  
			throwNoControlledError = false;
		} catch (CustomServletException e) 
		{
			throwNoControlledError = false;
			processException(resp, e); 
		
		} catch (Exception e) 
		{
			logger.error(e,e);
			sendErrorInfo(resp, HttpStatus.INTERNAL_SERVER_ERROR_500,  "No se pudo realizar el proceso, inténtelo más tarde"); 
		}finally {
			SessionContentManager.instance().clean();
			if(throwNoControlledError)
				sendErrorInfo(resp, HttpStatus.INTERNAL_SERVER_ERROR_500,  "No se pudo realizar el proceso, inténtelo más tarde"); 
		}  
		
	}
	
	private void processException(HttpServletResponse resp, CustomServletException e) throws ServletException {
		
		if(ObjectContainer.instance().containsException(e.getCause())){
			
			if(e.getStatus() > 0) {
				sendErrorInfo(resp, e.getStatus(), e.getCause().getMessage());
			}else {
				sendErrorInfo(resp, HttpStatus.BAD_REQUEST_400, e.getCause().getMessage());
			}
			
		}else {
			sendErrorInfo(resp, HttpStatus.INTERNAL_SERVER_ERROR_500, "No se pudo realizar el proceso, int�ntelo más tarde");
		}  
	}	
	
	private void sendErrorInfo(HttpServletResponse resp, int status, String msg) { 
		try {
			ResponseUtil.createResponse(resp, status, msg);
		
		} catch (CustomServletException e) { 
			logger.error(e);
			resp.setStatus(status);
		} 
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {}

	 
}
