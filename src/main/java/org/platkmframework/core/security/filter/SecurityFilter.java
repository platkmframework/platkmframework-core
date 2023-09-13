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
package org.platkmframework.core.security.filter;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.platkmframework.annotation.security.SecurityCofing;
import org.platkmframework.comon.service.exception.ServiceException;
import org.platkmframework.content.ioc.ObjectContainer;
import org.platkmframework.core.request.CorePropertyConstant;
import org.platkmframework.core.request.exception.CustomServletException;
import org.platkmframework.core.response.util.ResponseUtil;
import org.platkmframework.core.security.PlatkmPrincipal;
import org.platkmframework.core.security.core.xss.XSSRequestWrapper;
import org.platkmframework.core.security.service.PlatkmSecurity;
import org.platkmframework.core.session.SessionContentManager;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse; 

/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public class SecurityFilter implements Filter {
	
	private static final Logger logger = LogManager.getLogger(SecurityFilter.class);
	

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		XSSRequestWrapper req = new XSSRequestWrapper((HttpServletRequest) request);
		HttpServletResponse resp = (HttpServletResponse) response;
 
		String uri = req.getRequestURI();
		if(StringUtils.isBlank(uri)){
			logger.error(HttpStatus.BAD_REQUEST_400 + "no se puede reconocer la acción, por favor, consulte a su administrdor");
			throw new CustomServletException(new ServiceException(HttpStatus.BAD_REQUEST_400, "no se puede reconocer la acción, por favor, consulte a su administrdor")); 
		}
		
		List<Object>  list = ObjectContainer.instance().getListObjectByAnnontationAndInstance(SecurityCofing.class, PlatkmSecurity.class);
		if(list == null ||  list.isEmpty()) chain.doFilter(req, resp);
		PlatkmSecurity platkmSecurity = (PlatkmSecurity) list.get(0);
		if(platkmSecurity == null) chain.doFilter(req, resp);
		
		try {
			if(platkmSecurity.isLogingRequet(req)) {
				ResponseUtil.createResponse(resp, platkmSecurity.login(req, resp), HttpStatus.OK_200); 
			}else if(platkmSecurity.isLogoutRequest(req)) {
				platkmSecurity.logout(req);
			}else {
				
				if (isPublicApiCall(uri)) {
					createPublicSession();
					chain.doFilter(req, resp);
				}else {
					platkmSecurity.authentication(req, resp);
					chain.doFilter(req, resp); 
				}
			} 
		} catch (ServiceException e) { 
			throw new CustomServletException(e);
		} 
	}
											
	private boolean isPublicApiCall(String uri) { 
		return ObjectContainer.instance().getPropertyValue(CorePropertyConstant.ORG_PLATKMFRAMEWORK_SERVER_PUBLIC_PATH, "").contains(uri);
	} 
	
	private void createPublicSession() {  
		PlatkmPrincipal platkmPrincipal = new  PlatkmPrincipal();
		platkmPrincipal.setLogin("public");
		platkmPrincipal.setName("public");
		platkmPrincipal.getRoles().add("PUBLIC");  
		platkmPrincipal.setToken(RandomStringUtils.random(255,"ABCDEFGHIJQLMNOPQRSTUVXYZabcdifghijklmnuvyxyz1234567890"));
		SessionContentManager.instance().setPrincipal(platkmPrincipal); 
	}
	
}
