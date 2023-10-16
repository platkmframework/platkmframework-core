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
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.lang3.StringUtils;
import org.platkmframework.annotation.UIFilterToSearchConverter;
import org.platkmframework.annotation.exception.UIFilterToSearchConverterException;
import org.platkmframework.common.domain.filter.criteria.FilterCriteria;
import org.platkmframework.common.domain.filter.criteria.SearchCriteria;
import org.platkmframework.common.domain.filter.criteria.WhereCriteria;
import org.platkmframework.common.domain.filter.ui.Filter;
import org.platkmframework.comon.service.exception.ServiceException;
import org.platkmframework.comon.service.validator.ValidatorUtil;
import org.platkmframework.content.ioc.ObjectContainer;
import org.platkmframework.core.domain.base.converter.UIFilterToSearchCriteriaConverter;
import org.platkmframework.core.request.exception.ResourceNotFoundException;
import org.platkmframework.core.request.multipart.MultiPart;
import org.platkmframework.util.JsonException;
import org.platkmframework.util.JsonUtil;
import org.platkmframework.util.Util;
import org.platkmframework.util.error.InvocationException;
import org.platkmframework.util.reflection.ReflectionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;



/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 -LIST<?>
     .DESDE FORM
        .Cuando se usan tablas, En java el listado es de objetos complejos o personalizados
          por el cliente
        .Cuando se usa un select multiple, En java el listado es de objetos no complejos
     .DESDE API
        La informacion estar� en el cuerpo de POST

 */
public class ObjetTypeConverter 
{
	
	//prefix to html table row, to know if it is new
	public static final String C_TABLE_NEW_ROW_CHECKER 	= "html.table.newrow_checker";
	
	private static Map<String, Class<?>> primitive = new HashMap<>();
	private static UIFilterToSearchCriteriaConverter uiFilterToSearchCriteriaConverter = new UIFilterToSearchCriteriaConverter();
	
	static {
		primitive.put("byte", Byte.class);
		primitive.put("short", Short.class);
		primitive.put("int", Integer.class);
		primitive.put("long", Long.class);
		primitive.put("float", Float.class);
		primitive.put("double", Double.class);
		primitive.put("boolean", Boolean.class);
		primitive.put("char", Character.class);
	}
	 
	
	/**
	 * type
	 *     .object attribute
	 *        ,simmple object is processed from request parameter
	 *        ,List<simple object> is processed from request parameter array[1..n]
	 *        ,List<complex objet>, is processed as a table
	 *     . method parameter
	 *        ,simple object are processed from request parameter
	 *        ,complex object is processed from request BODY
	 * @param required 
	 * @param name
	 * @param type
	 * @param param
	 * @param req
	 * @param pageCall
	 * @return
	 * @throws ParseException
	 * @throws ResourceNotFoundException
	 * @throws IOException 
	 * @throws JsonException 
	 * @throws InvocationException 
	 * @throws ServletException 
	 * @throws ServiceException 
	 * @throws FileUploadException 
	 */
	public static Object converParamValueToObjectType(String customParamName, boolean required, Parameter parameter,
			HttpServletRequest req,
			boolean isAttribute, Object paramValue) throws ParseException, ResourceNotFoundException, IOException, JsonException, InvocationException, ServletException, ServiceException 
	{ 
		if(parameter.getType().getName().startsWith("java.lang"))
			return convertToJavaLang(customParamName, parameter.getType(), paramValue, required);
		else if(isPrimitive(parameter.getType().getName()))
			return convertToJavaLang(customParamName, primitive.get(parameter.getType().getName()), paramValue, required);
		else if( parameter.getType().getName().startsWith("java.time"))
			return covertToTimeObject(parameter.getType(), paramValue, required);
		else if( parameter.getType().equals(MultiPart.class)) 
			return MultiparReader.read(req);else
		{
			//api or macro call
			//red de request body as a JSON and conver to type required
			String strJson = Util.inputSteamToString(req.getInputStream());
			if(StringUtils.isBlank(strJson))
				return null;
			else 
				return  JsonUtil.jsonToObject(strJson,parameter.getType());
		}
			
	}
	 
	private static Object covertToTimeObject(Class<?> type, Object value, boolean required) throws ResourceNotFoundException {
		if( value == null || StringUtils.isEmpty(value.toString()) ) {
			if(required) throw new ResourceNotFoundException("required  field value not found -> " + type.toString());
			else return null;
		}
		 
		if(LocalDate.class.equals(type)) {
			return LocalDate.parse(value.toString(), DateTimeFormatter.ofPattern(ObjectContainer.instance().getPropertyValue("platform.format.date")));
		
		}else if(LocalDateTime.class.equals(type)) {
			return LocalDateTime.parse(value.toString(), DateTimeFormatter.ofPattern(ObjectContainer.instance().getPropertyValue("platform.format.datetime")));
		
		}else if(LocalTime.class.equals(type)) {
			return LocalTime.parse(value.toString(), DateTimeFormatter.ofPattern(ObjectContainer.instance().getPropertyValue("platform.format.time")));
		}
		
		throw new ResourceNotFoundException(type.getName() +  " not implemented"); 
	}

	public static Object converBodyValueToObjectType(Parameter parameter, boolean required, Class<? extends UIFilterToSearchConverter> uiFilterToSearchConverter, HttpServletRequest req) throws ResourceNotFoundException, IOException, ParseException, JsonException, ServletException, ServiceException {
		  
		if( parameter.getType().equals(MultiPart.class)) 
			return MultiparReader.read(req);
		else
		{
			String strBody = Util.inputSteamToString(req.getInputStream());
			if(StringUtils.isBlank(strBody))
				if(required) throw new ResourceNotFoundException("required field value not found"); else return null;
			
			if(uiFilterToSearchConverter != null) {
				UIFilterToSearchConverter uIFilterToSearchConverter =(UIFilterToSearchConverter) ObjectContainer.instance().geApptScopeObj(uiFilterToSearchConverter);
				 try {
					
					 return uIFilterToSearchConverter.convert(strBody, parameter);
				
				 } catch (UIFilterToSearchConverterException e) {
					 throw new ServiceException(e.getMessage());
				}
			}
			
			if(parameter.getType().equals(List.class) && parameter.getParameterizedType() instanceof ParameterizedType
					&& ((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments().length>0) {
			
				return JsonUtil.jsonToListObject(strBody, (Class) ((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments()[0]);
			
			}else if(parameter.getType().equals(SearchCriteria.class)){
				Filter<?> filter = JsonUtil.jsonToObject(strBody, Filter.class);
				ValidatorUtil.checkValidation(filter);
				Object objBean = uiFilterToSearchCriteriaConverter.convertToSearchCriteria(filter, filter.getCode());
				ValidatorUtil.checkValidation(objBean);
				return objBean;
			
			}else if(parameter.getType().equals(FilterCriteria.class)){
				Filter<?> filter = JsonUtil.jsonToObject(strBody, Filter.class);
				ValidatorUtil.checkValidation(filter);
				Object objBean = uiFilterToSearchCriteriaConverter.convertToFilterCriteria(filter, filter.getCode());
				ValidatorUtil.checkValidation(objBean);
				return objBean;
			
			}else if(parameter.getType().equals(WhereCriteria.class)){
				Filter<?> filter = JsonUtil.jsonToObject(strBody, Filter.class);
				ValidatorUtil.checkValidation(filter);
				Object objBean = uiFilterToSearchCriteriaConverter.convertToQueryCriteria(filter, filter.getCode());
				ValidatorUtil.checkValidation(objBean);
				return objBean;
			
			}else if(parameter.getType().getName().startsWith("java.lang"))
				return convertToJavaLang(parameter.getName(), parameter.getType(), strBody, required);
			
			else {
				Object objBean = JsonUtil.jsonToObject(strBody, parameter.getType());
				ValidatorUtil.checkValidation(objBean);
				return objBean;
			}
		}
	}
	
	
	/**
	 * 
	 * @param name
	 * @param field
	 * @param obj
	 * @param param
	 * @param req
	 * @throws ParseException
	 * @throws ResourceNotFoundException
	 * @throws IOException
	 * @throws JsonException
	 * @throws InvocationException
	 * @throws ServiceException 
	 */
	public static  void converFieldValueToObjectType(String htmlFieldName,Field field, Object obj,
							Map<String, Object> param,
							HttpServletRequest req) throws ParseException, ResourceNotFoundException, 
														   IOException, JsonException, InvocationException, ServiceException 
	{ 
		if(field.getType().getName().startsWith("java.lang"))
		{
			Object value = convertToJavaLang(htmlFieldName, field.getType(),param.get(htmlFieldName),false);
			ReflectionUtil.setAttributeValue(obj, field, value, false);
		}
		else
		{
			if(!List.class.equals(field.getType()))
				throw new ResourceNotFoundException("Data type->" + field.getType() + " not supported from page request");
			_processAttributeList(htmlFieldName, obj, field, param, req); 
		}
		 
	}
	
	/**
	 * 
	 * @param type
	 * @param required 
	 * @param object
	 * @return
	 * @throws ParseException 
	 * @throws ResourceNotFoundException 
	 */
	private static Object convertToJavaLang(String customParamName, Class<?> type, Object value, boolean required) throws ParseException, ResourceNotFoundException, ServiceException 
	{
		//if empty value
		if( value == null || StringUtils.isEmpty(value.toString()) )
		  if(required)
			  throw new ResourceNotFoundException("El valor para el parámetro " + customParamName + "es requerido");
		  else
			  return null;
	
		if(int.class.equals(type))
			return Integer.valueOf(value.toString());
		else if(Integer.class.equals(type))
			return Integer.valueOf(value.toString());
		else if(long.class.equals(type))
			return Long.valueOf(value.toString());
		else if(Long.class.equals(type))
			return Long.valueOf(value.toString());
		else if(float.class.equals(type))
			return Float.valueOf(value.toString());
		else if(Float.class.equals(type))
			return Float.valueOf(value.toString());
		else if(boolean.class.equals(type))
			return Boolean.valueOf(value.toString());
		else if(Boolean.class.equals(type))
			return Boolean.valueOf(value.toString());
		else if(Date.class.equals(type))
			return Util.stringToDate(value.toString()); 
		else if(String.class.equals(type))
			return new String(value.toString()); 
		else
			throw new ResourceNotFoundException("Data type->" + type + " not found to convert");
	}


	/**
	 * 
	 * @param name
	 * @param type
	 * @param param
	 * @return
	 * @throws ResourceNotFoundException 
	 * @throws ParseException 
	 * @throws InvocationException 
	 * @throws ServiceException 
	 */
	private static  List _processAttributeList(String htmlFieldName,
								Object obj,
								Field field,
								Map<String, Object> param, 
								HttpServletRequest req) throws ResourceNotFoundException, ParseException, InvocationException, ServiceException 
	{
	 	
		Type t = field.getType().getGenericSuperclass();
		ParameterizedType p = (ParameterizedType)t;
		Type genericType = p.getActualTypeArguments()[0];
		
		if(genericType.getClass().getName().startsWith("java.lang."))
		{	
			String[] parameterValues = req.getParameterValues(htmlFieldName);
			if(parameterValues == null || parameterValues.length ==0)
				return null;
			
			if(genericType.getClass().equals(Long.class))  
				return _fillByClassType(new ArrayList<Integer>(), parameterValues, Long.class);
			else if(genericType.getClass().equals(Integer.class)) 
				return _fillByClassType(new ArrayList<Integer>(), parameterValues, Integer.class);
			else if(genericType.getClass().equals(Float.class)) 
				return _fillByClassType(new ArrayList<Integer>(), parameterValues, Float.class);
			else if(genericType.getClass().equals(Date.class)) 
				return _fillByClassType(new ArrayList<Integer>(), parameterValues, Date.class);
			else if(genericType.getClass().equals(Boolean.class)) 
				return _fillByClassType(new ArrayList<Boolean>(), parameterValues, Boolean.class);
			else 
				throw new ResourceNotFoundException("The class type-> " + genericType.getClass().getName() + " is not included to process");
		}else
		{
		  return _fillByComplexTable(htmlFieldName, req, obj,field, genericType.getClass());
		}
		 
			
	}

	
	/**
	 * it's sopused a table from html
	 *            column1              column2              column3
	 * row1     field_column1_1     field_column2_1      field_column3_1
	 * row2     field_column1_2     field_column2_2      field_column3_2
	 * row3     field_column1_3     field_column2_3      field_column3_3       
	 *  
	 *  create while until no element are found in any column
	 *  onlye the html tag to enter data will be take in care
	 *  
	 *  firt, 
	 *  try to find the object by an Id field
	 *  insert object if does not find it
	 *  remove the entry that do not exists
	 * @param parameterValues
	 * @param class1 It suposed is a simple bean with simple contructor
	 * @return
	 * @throws InvocationException 
	 * @throws ResourceNotFoundException 
	 * @throws ParseException 
	 * @throws ServiceException 
	 */	
	private static List _fillByComplexTable(String htmlFieldName, HttpServletRequest req, Object obj ,  Field field, Class classBean) throws InvocationException, ParseException, ResourceNotFoundException, ServiceException 
	{
		List  list = (List )ReflectionUtil.getAttributeValue(obj, field);
		List<Field> fields = ReflectionUtil.getAllFieldHeritage(obj.getClass());
		if(fields != null)
		{ 
			int row = 1; 
			String htmlFielNameId = _getTableCellName(htmlFieldName, C_TABLE_NEW_ROW_CHECKER, row);
			if (!req.getParameterMap().containsKey(htmlFielNameId))
				return list;
			
			//there are rows then check list
			if(list == null)
				list = (List)ReflectionUtil.createInstance(field.getClass());
			
			boolean exists = true; 
			Object objRow;
			Object value;
			while (exists)
			{ 
				if(StringUtils.isEmpty(htmlFielNameId))
				{
					//new record
					objRow = ReflectionUtil.createInstance(classBean);
					list.add(objRow);
				}else 
					objRow = list.get(_getCurrentRow(htmlFielNameId)); 
				
				for (int i = 0; i < fields.size(); i++) 
				{ 
					htmlFielNameId = _getTableCellName(htmlFieldName, fields.get(i+1).getName(), row);
					if (req.getParameterMap().containsKey(htmlFielNameId))
					{
						value = convertToJavaLang(htmlFielNameId, fields.get(i+1).getType(), req.getParameter(htmlFielNameId), false);
						ReflectionUtil.setAttributeValue(objRow, fields.get(i+1), value, false);
					}
				}
				//next row
				row++; 
				htmlFielNameId = _getTableCellName(htmlFieldName, C_TABLE_NEW_ROW_CHECKER, row);
				if (!req.getParameterMap().containsKey(htmlFielNameId))
					return list;	
			}
		}
		return list;
	}
  

	private static int _getCurrentRow(String newFieldChecker) 
	{ 
		return Integer.parseInt(newFieldChecker.split("_")[2]);
	}


	/**
	 * table cell name
	 * @param classFieldName
	 * @param beanFieldName
	 * @param row
	 * @param column
	 * @return
	 */
	private static String _getTableCellName(String classFieldName, String beanFieldName, int row) 
	{ 
		return classFieldName + "_" + C_TABLE_NEW_ROW_CHECKER + "_" +  row;
	}


	/**
	 * convert java.lang...type to List<type>
	 * @param list
	 * @param parameterValues
	 * @param class1
	 * @return
	 * @throws ParseException
	 * @throws ResourceNotFoundException
	 */
	private static List _fillByClassType(ArrayList list, String[] parameterValues, Class class1) throws ParseException, ResourceNotFoundException 
	{
		for (int i = 0; i < parameterValues.length; i++)   
			list.add(_getValue(parameterValues[i], class1)); 
		return list;
	}	
  

	/**
	 * convert value to the java.lang....type object
	 * @param value
	 * @param class1
	 * @return
	 * @throws ParseException
	 * @throws ResourceNotFoundException
	 */
	private static Object _getValue(String value, Class<?> class1) throws ParseException, ResourceNotFoundException 
	{
		if(class1.equals(Long.class))  
			return Long.parseLong(value);
		else if(class1.equals(Integer.class)) 
			return Integer.parseInt(value);
		else if(class1.equals(Float.class)) 
			return Float.parseFloat(value);
		else if(class1.equals(Date.class)) 
			return Util.stringToDate(value);
		else 
			throw new ResourceNotFoundException("The class type-> " + class1.getClass().getName() + " is not included to process");
	
	}

	private static boolean isPrimitive(String type) { 
		return primitive.containsKey(type);
	} 

}
