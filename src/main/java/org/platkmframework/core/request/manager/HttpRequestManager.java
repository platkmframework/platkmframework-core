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
package org.platkmframework.core.request.manager;
  
import java.io.IOException; 
import java.text.ParseException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
 

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.platkmframework.content.ioc.ObjectContainer;
import org.platkmframework.core.request.caller.MethodCaller;
import org.platkmframework.core.request.download.DownFileInfo;
import org.platkmframework.core.request.download.FileDownload;
import org.platkmframework.core.request.exception.CustomServletException;
import org.platkmframework.core.request.exception.RequestProcessException;
import org.platkmframework.core.request.exception.ResourceNotFoundException;
import org.platkmframework.core.request.multipart.MultipartFile;
import org.platkmframework.core.response.util.ResponseUtil; 
import org.platkmframework.util.JsonException; 
import org.platkmframework.util.error.InvocationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse; 
 


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public class HttpRequestManager{
 

	private static final Logger logger = LogManager.getLogger(HttpRequestManager.class);
	
	//private static final String C_PAGE_CALL = "f";
	//private static final String C_API_CALL  = "ext";
	//private static final String C_ACH       = "ach";
	//private static final String C_P 		= "c"; 
	int maxMemSize = 5000 * 1024;
	int maxFileSize = 5000 * 1024;
	
	private static HttpRequestManager httpRequestManager;
	
	private MethodCaller methodCaller;
	//private MacroCaller  macroCaller;
	//private PageCaller   pageCaller; 
	 
	private HttpRequestManager() 
	{
		super();
		methodCaller = (MethodCaller)ObjectContainer.instance().geApptScopeObj(MethodCaller.class);
		//macroCaller  = (MacroCaller)ObjectContainer.instance().geApptScopeObj(MacroCaller.class);
		//pageCaller   = (PageCaller)ObjectContainer.instance().geApptScopeObj(PageCaller.class);
	}


	public static HttpRequestManager instance() 
	{
		if(httpRequestManager == null)
			httpRequestManager = new HttpRequestManager();
	
		return httpRequestManager;
	}

	public void process(HttpServletRequest req, HttpServletResponse resp) throws CustomServletException 
	{  
  
		//se lee los parametros y encabezados y se guardan en el map
		//Map<String,Object> param = _generateContentInfo(req);  
		//SessionContentManager.instance().setParameter(param);

		String path = req.getPathInfo(); 
		if(StringUtils.isEmpty(path)) { 
			ResponseUtil.throwError("recurso no encontrado"); 
		}
/**			try {
				resp.sendError(HttpStatus.NOT_FOUND_404, "recurso no encontrado");
			} catch (IOException e) { 
				e.printStackTrace();
				resp.setStatus(HttpStatus.NOT_FOUND_404);
			}
		}  */
		 
		//path = req.getPathInfo();
		//List<String> sPath = Util.cleanSprit(path, "/");
		//if(sPath.size() < 2)
		//	throw new RequestManagerException("method information incorrect");
		 
	   _processApiRequest(path.substring(1), req, resp);
		 
	}	 

	/**
	 * api call [a/apiname/methodname]
	 * @param sPath
	 * @param reqs
	 * @param resp
	 * @param param
	 * @return
	 * @throws ServletException 
	 * @throws IOException 
	 * @throws JsonException 
	 * @throws InvocationException 
	 * @throws ResourceNotFoundException 
	 * @throws ParseException 
	 */
	private void _processApiRequest(String path ,HttpServletRequest req, HttpServletResponse resp) throws CustomServletException{     
	  
		try {
			
			Object object = methodCaller.execute(path, req, resp);
			_writeResponse(object, req, resp);
			
		} catch (RequestProcessException e) {
			logger.error(e,e);
			throw new CustomServletException(e.getCause());
		} catch (Exception e) {
			logger.error(e,e);
			throw new CustomServletException(e);
		}
			 

		
	}
 

/**	private void processException(HttpServletResponse resp, Throwable e) throws CustomServletException { 
		
		int status = 0;
		String msg = "";
		if(e instanceof RequestProcessException) {
			e = e.getCause(); 
		}
		if(e == null) {
			status = HttpStatus.BAD_REQUEST_400;
			msg =  "error encontrado"; 
		}else{
			msg = e.getMessage();
			ResponseStatus responseStatus = e.getClass().getAnnotation(ResponseStatus.class);
			if(responseStatus == null) {
				status  = HttpStatus.BAD_REQUEST_400;
			}else {
				status = responseStatus.status();
			} 
		}
		//writeInfo(resp, new ErrorResponse(status,msg), status);
		try {
			resp.sendError(status, msg);
		} catch (IOException e1) { 
			e1.printStackTrace();
			resp.setStatus(status); 
		}
		throw new ServletException(msg);
	}
*/

	/**
	 * 
	 * @param callType
	 * @param object
	 * @param req
	 * @param resp
	 * @throws CustomServletException 
	 * @throws JsonException 
	 * @throws IOException 
	 * @throws ResourceNotFoundException 
	 */
	private void _writeResponse(Object object, HttpServletRequest req, HttpServletResponse resp) throws CustomServletException
	{ 
		 
		if(object != null && object instanceof MultipartFile){	
			try {
				DownFileInfo downFileList =  new FileDownload().process((MultipartFile)object);
				writeInfo(resp, downFileList, HttpStatus.OK_200);
			} catch (IOException e) {
				ResponseUtil.throwError( "No se pudo realizar el proceso, inténtelo más tarde"); 
			} 
		}
		else{ 
			writeInfo(resp, object, HttpStatus.OK_200);
		} 
		 
	}

 
	private void writeInfo(HttpServletResponse resp, Object object, int status) throws CustomServletException {
		ResponseUtil.createResponse(resp, object, status); 
	}


	/**
	 * 
	 * @param req
	 * @return
	 */
	public Map<String, Object> _generateContentInfo(HttpServletRequest req) 
	{
		 Enumeration<String> e = req.getParameterNames();
		 Map<String,Object> param  = new HashMap<>();
		 while (e.hasMoreElements())
		 {
			 String name = e.nextElement(); 
			 param.put(name, Jsoup.clean(req.getParameter(name), Whitelist.basic())); 
		 } 
		 //header
		 e = req.getHeaderNames(); 
		 while (e.hasMoreElements())
		 {
			 String name = e.nextElement(); 
			 param.put(name, req.getParameter(name)); 
		 }  
		 
		 //param.putAll(PageContent.instance().getMapDomainRealPath());
		 return param;
	}
	
		

}
