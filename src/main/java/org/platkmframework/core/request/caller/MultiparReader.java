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
import java.util.Collection;

import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.platkmframework.core.request.multipart.MultiPart;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
 


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public class MultiparReader 
{
	
/**	private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
	private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
*/
	
	/**
	 * description: read upload content
	 * @param req : http servlet request
	 * @return Muitipart
	 * @throws IOException - io exception
	 * @throws ServletException 
	 */
	public static MultiPart read(HttpServletRequest req) throws ServletException, IOException
	{
		MultiPart multiPart = null;
		
	 	if (JakartaServletFileUpload.isMultipartContent(req))
		{ 
	 	 
	 		Collection<Part> fileParts = req.getParts();
	 		
		    if (fileParts != null && fileParts.size() > 0) 
		    {
		    	multiPart = new MultiPart();
		        for (Part item : fileParts) 
	        		multiPart.add(item.getName(), 
	        					  item.getSubmittedFileName(),
	        					  item.getContentType(), 
	        					  item.getSize(),
	        					  item.getInputStream()); 
		        	 
		        return multiPart;
		    }
		}
		 
		return multiPart;
	}

}
