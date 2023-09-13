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
package org.platkmframework.core.domain.base.converter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.platkmframework.annotation.db.SearchFilterColumnInfo;
import org.platkmframework.content.ioc.ObjectContainer;
import org.platkmframework.util.reflection.ReflectionUtil; 


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public class SearchFilterManager {
	 
	private static SearchFilterManager searchFilterConfigurationContainer;
	 
	
	
	private SearchFilterManager() { 
	}
	
	public static SearchFilterManager instance() {
		if (searchFilterConfigurationContainer == null) {
			searchFilterConfigurationContainer = new SearchFilterManager();
		} 
		return searchFilterConfigurationContainer;
	}

	public Map<String, SearchFilterFieldInfo>  getFilterInfo(String code) {
		
		Class filerClass = ObjectContainer.instance().getFilterInfoClassByCode(code);
		if (filerClass == null) return null;
		
		Map<String, SearchFilterFieldInfo>  map = new HashMap<>();
		List<Field> fields = ReflectionUtil.getAllFieldHeritage(filerClass);
		Field f;
		SearchFilterColumnInfo filterColumnInfo;
		SearchFilterFieldInfo searchFilterFieldInfo;
		for (int i = 0; i < fields.size(); i++) 
		{
			f = fields.get(i);
			if(f.isAnnotationPresent(SearchFilterColumnInfo.class)){
				filterColumnInfo = f.getAnnotation(SearchFilterColumnInfo.class);
				searchFilterFieldInfo = new SearchFilterFieldInfo();
				searchFilterFieldInfo.setCode(filterColumnInfo.code());
				searchFilterFieldInfo.setColumn(filterColumnInfo.column());
				searchFilterFieldInfo.setLabel(filterColumnInfo.label());
				map.put(filterColumnInfo.code(), searchFilterFieldInfo);
			}
		}
		return map;
	} 

}
