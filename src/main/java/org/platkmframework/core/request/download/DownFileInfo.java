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
package org.platkmframework.core.request.download;

import java.io.Serializable;


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public class DownFileInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5654887189697320038L;
	
	private String filename;
	private String fileType;
	private String content;
	private long fileZise;
	
	public DownFileInfo(String filename, String fileType, String content, long fileZise) {
		super();
		this.filename = filename;
		this.fileType = fileType;
		this.content = content;
		this.fileZise  = fileZise;
	}
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public long getFileZise() {
		return fileZise;
	}

	public void setFileZise(long fileZise) {
		this.fileZise = fileZise;
	}
	
	

}
