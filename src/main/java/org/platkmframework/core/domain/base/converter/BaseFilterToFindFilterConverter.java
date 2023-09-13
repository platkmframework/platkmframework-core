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
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.platkmframework.common.domain.filter.criteria.base.ConditionFilterBase;
import org.platkmframework.common.domain.filter.enumerator.ConditionOperator;
import org.platkmframework.common.domain.filter.enumerator.GroupOperator;
import org.platkmframework.common.domain.filter.enumerator.MathOperator;
import org.platkmframework.common.domain.filter.enumerator.SqlOperator;
import org.platkmframework.common.domain.filter.ui.BaseFilter;
import org.platkmframework.common.domain.filter.ui.FilterCondition;
import org.platkmframework.comon.service.exception.ServiceException;
import org.platkmframework.database.manager.exception.DatabaseConnectionException; 


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public abstract class BaseFilterToFindFilterConverter {
 
	
	private static final Logger logger = LogManager.getLogger(BaseFilterToFindFilterConverter.class);
	  
	
	protected ConverterResultData convertByTableCode(BaseFilter filter, ConditionFilterBase findFilter, String code) throws ServiceException {
 
		return convert(filter, findFilter, code);
	}
  
	protected ConverterResultData convert(BaseFilter filter, ConditionFilterBase findFilter, String code) throws ServiceException {
		   
		ConverterResultData converterResultData = new ConverterResultData();
		if(filter == null) return converterResultData;
		 
		processConditions(converterResultData, filter, findFilter, code); 
		
		return converterResultData;
	}  
	
	protected void processConditions(ConverterResultData converterResultData, BaseFilter filter, ConditionFilterBase findFilter, String code) throws ServiceException {
		
		try { 
			    
			Map<String, SearchFilterFieldInfo> filterFieldInfoMap = SearchFilterManager.instance().getFilterInfo(code);
			if(!filter.getConditions().isEmpty() && filterFieldInfoMap != null && !filterFieldInfoMap.isEmpty() ){
					
					List<String> columns = new ArrayList<>();
					List<String> groupsby = new ArrayList<>();
					String columnName = "";
					MathOperator mathOperator; 
					SearchFilterFieldInfo searchFilterFieldInfo;
					for (FilterCondition filterCondition : filter.getConditions()){
						
						 checkCustomFormat(filterCondition);
						
						if(StringUtils.isNotEmpty(filterCondition.getFieldCode()))
						{
							searchFilterFieldInfo = filterFieldInfoMap.get(filterCondition.getFieldCode());
							if(searchFilterFieldInfo == null)
								throw new DatabaseConnectionException("filter variable name not found");
							if(StringUtils.isBlank(filterCondition.getTcode())) {
								filterCondition.setColumnName(searchFilterFieldInfo.getColumn());
							}else {
								filterCondition.setColumnName(filterCondition.getTcode() + "." + searchFilterFieldInfo.getColumn());
							}
							
							if(StringUtils.isNotBlank(filterCondition.getMathOperation())) {
								mathOperator = MathOperator.valueOf(filterCondition.getMathOperation()); 
								if(mathOperator == null)
									throw new ServiceException("no se reconoce la operaci�n matem�tica");
								
								if("sum".equals(mathOperator.name())) {
									columnName = "SUM(" + searchFilterFieldInfo.getColumn() + ") AS " + searchFilterFieldInfo.getColumn();
								}else if("max".equals(mathOperator.name())) {
									columnName = "MAX(" + searchFilterFieldInfo.getColumn() + ") AS " + searchFilterFieldInfo.getColumn();
								}else if("count".equals(mathOperator.name())) {
									columnName = "COUNT(" + searchFilterFieldInfo.getColumn() + ") AS " + searchFilterFieldInfo.getColumn();
								}else if("min".equals(mathOperator.name())) {
									columnName = "MIN(" + searchFilterFieldInfo.getColumn() + ") AS " + searchFilterFieldInfo.getColumn();
								}
								columns.add(columnName);
								groupsby.add(searchFilterFieldInfo.getColumn());
							}
							
							addCondition(findFilter, filterCondition );
						}  
					} 
					converterResultData.setSelect(columns.stream().collect(Collectors.joining(",")));
					converterResultData.setGroupBy(groupsby.stream().collect(Collectors.joining(",")));
					
					//@TODO
					//converterResultData.setHaving(columnName);
				} 
	
			 
		}catch (Exception e) {
			logger.error(e,e);
			throw new ServiceException("no se ha podido devolver informacion");
		}
	}
 

	public void addCondition(ConditionFilterBase findFilter, FilterCondition filterCondition) throws ServiceException 
	{   
		
		SqlOperator sqlOperator;
		GroupOperator groupOperator;
		ConditionOperator conditionOperator;
		if(StringUtils.isNotBlank(filterCondition.getSoperator())) {
			sqlOperator = SqlOperator.valueOf(filterCondition.getSoperator());
			if(sqlOperator == null) {
				 throw new DatabaseConnectionException("no se encontr� el operador");
			}
			
			if(SqlOperator.and.name().equalsIgnoreCase(sqlOperator.name())) {
				findFilter.and();
			}else if(SqlOperator.or.name().equalsIgnoreCase(sqlOperator.name())) {
				findFilter.or();
			} 
			 
		}
		if(StringUtils.isNotBlank(filterCondition.getInitGroup())){
			groupOperator = GroupOperator.valueOf(filterCondition.getInitGroup());
			if(groupOperator == null) throw new DatabaseConnectionException("no se encontr� el operador"); 
			if(GroupOperator.open.name().equalsIgnoreCase(groupOperator.name())) {
				findFilter.op();
			}else if(GroupOperator.close.name().equalsIgnoreCase(groupOperator.name())) {
				findFilter.cp();
			}  
		}
		
		if(StringUtils.isNotBlank(filterCondition.getOperator())){
			conditionOperator = ConditionOperator.valueOf(filterCondition.getOperator());
			if(conditionOperator == null) throw new DatabaseConnectionException("no se encontr� el operador");
			findFilter.valexp( filterCondition.getColumnName(), conditionOperator, filterCondition.getValue());
		}

		//filter.getSql().add(new FindFilterExpressionInfo(ConditionOperator.valueOf(filterCondition.getOperator()), filterCondition.getColumnName(), validateValue(filterCondition.getValue(), mdTableFields, typeLanguages))); 
		
		//filter.column(filterCondition.getColumnName()).castOperator(filterCondition.getOperator()).value(validateValue(filterCondition.getValue(), mdTableFields, typeLanguages));
		
		if(StringUtils.isNotBlank(filterCondition.getEndGroup())){
			groupOperator = GroupOperator.valueOf(filterCondition.getEndGroup());
			if(groupOperator == null) throw new DatabaseConnectionException("no se encontr� el operador"); 
			if(GroupOperator.open.name().equalsIgnoreCase(groupOperator.name())) {
				findFilter.op();
			}else if(GroupOperator.close.name().equalsIgnoreCase(groupOperator.name())) {
				findFilter.cp();
			} 
		} 	 
	}



	
	
	protected String checkCustomFormat(FilterCondition filterCondition) 
	{
		if(filterCondition.getValue() != null && filterCondition.getValue().toString().contains(":"))
		{
			String[] arrayFecha = filterCondition.getValue().toString().split(":");
			String formato = arrayFecha[0].toString().trim();
			
			if("y".equals(formato)) 
			{
				filterCondition.setValue(arrayFecha[1]);
				return " YEAR(" + filterCondition.getColumnName() + ") ";
			}else if("m".equals(formato)) 
			{
				filterCondition.setColumnName(arrayFecha[1]);
				return " MONTH(" + filterCondition.getColumnName()  + ") ";
			}else if("d".equals(formato)) 
			{
				filterCondition.setColumnName(arrayFecha[1]);
				return " DAY(" + filterCondition.getColumnName() + ") ";
			} 
			
		}
		return filterCondition.getColumnName();
	}
	
	protected List<String> getFieldsCode(BaseFilter filter){
		List<String> list = new ArrayList<>();
		if(filter != null)
			for (FilterCondition filterCondition : filter.getConditions()) 
				if(StringUtils.isNotEmpty(filterCondition.getFieldCode()))
					list.add(filterCondition.getFieldCode());
					  
		return list;
	}
 
	
	
}
