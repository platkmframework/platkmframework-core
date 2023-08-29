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
package org.platkmframework.core.request.caller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
 

import org.platkmframework.annotation.ClassAttribute;
import org.platkmframework.core.request.exception.ResourceNotFoundException;
import org.platkmframework.util.JsonException;
import org.platkmframework.util.error.InvocationException;
import org.platkmframework.util.reflection.ReflectionUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse; 



/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 * used on page 
 * //foreach declared field set information if it exists in param
		//ask if the attribute exist in requet param
		//ask for Field annotaion
 *
 */
public class AttributeSetter 
{ 
	
	public AttributeSetter()
	{ 
	}

	public void execute(Object obj, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> param) throws ResourceNotFoundException, ParseException, IOException, JsonException, InvocationException 
	{ 
		List<Field> fields = ReflectionUtil.getAllFieldHeritage(obj.getClass());
		
		if(fields != null)
		{
			String attributeName; 
			for (int i = 0; i < fields.size(); i++) 
			{
				Field field = fields.get(i);
				ClassAttribute classAttribute = field.getAnnotation(ClassAttribute.class);
				if(classAttribute != null)
					attributeName = classAttribute.name();
				else
					attributeName = field.getName();
				
				if(req.getParameterMap().containsKey(attributeName))  
					ObjetTypeConverter.converFieldValueToObjectType(attributeName, field, obj, param, req);
			}
		} 
	}

}
