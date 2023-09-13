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
package org.platkmframework.core.request.servlet;

import java.io.IOException;  
 

import org.platkmframework.core.request.exception.CustomServletException;
import org.platkmframework.core.request.manager.HttpRequestManager;
import org.platkmframework.core.security.core.xss.XSSRequestWrapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 



/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public class RequestManagerServlet extends HttpServlet{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req,resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req,resp);
	}
	
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		process(req,resp);
	}

	private void process(HttpServletRequest request, HttpServletResponse response) throws CustomServletException 
	{ 

	    //out.println("<title>Example</title><"); 
	    //out.println("<h2>Button Clicked</h2>");

	    /**String DATA = request.getParameter("DATA");

	    if(DATA != null){
	      out.println(DATA);
	    } else {
	      out.println("No text entered.");
	    }*/

	   // out.println("<P>Return to  <A HREF=>Form</A>");
	   //try 
	  // {
		   XSSRequestWrapper xssReq = new XSSRequestWrapper(request);
		   HttpRequestManager.instance().process(xssReq, response); 
	   //} catch (RequestManagerException e) 
	  // { 
		//   throw new ServletException(e.getMessage()); 
	  // } 
	}
}
