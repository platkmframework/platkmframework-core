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
package org.platkmframework.core.request.multipart;
 
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List; 
 



/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public class MultiPart 
{
	 private List<MultipartData> list;
	 
	  
	 public void add(String name, String filename, String contentType, long size, InputStream content ) {
		 
		 MultipartData multipartData = new MultipartData();
		 multipartData.setContent(content);
		 multipartData.setContentType(contentType);
		 multipartData.setName(name);
		 multipartData.setFilename(filename);
		 multipartData.setSize(size);
		 
		 getList().add(multipartData);
	 }



	public List<MultipartData> getList() {
		if(list == null) list = new ArrayList<>();
		return list;
	}

	 
	
}
