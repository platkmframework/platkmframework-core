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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.platkmframework.core.request.manager.ResponseBase; 


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public class MultipartFile extends ResponseBase implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String fileName;
	private Long id;
	private String code;
	private String filetype;
	private int error;
	private String contentType;
	private Long filesize;
	
	private InputStream file; 
	private boolean multipart;
	
	List<Range> ranges;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
 
	public String getFiletype() {
		return filetype;
	}
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
 
	public int getError() {
		return error;
	}
	public void setError(int error) {
		this.error = error;
	}
	public void setRange(Range r) {
		getRanges().add(r);
	} 
 
	public void setContentType(String contentType) {
		this.contentType = contentType; 
	}
	public String getContentType() {
		return contentType;
	}
	public void setMultipart(boolean multipart) {
		this.multipart = multipart; 
	}
	public boolean isMultipart() {
		return multipart;
	}
	public void setRanges(List<Range> ranges) {
		getRanges().addAll(ranges);
		
	}
	public List<Range> getRanges() {
		if(ranges == null) ranges = new ArrayList<Range>();
		return ranges;
	}
	public InputStream getFile() {
		return file;
	}
	public void setFile(InputStream file) {
		this.file = file;
	}
	public Long getFilesize() {
		return filesize;
	}
	public void setFilesize(Long filesize) {
		this.filesize = filesize;
	}
 
}
