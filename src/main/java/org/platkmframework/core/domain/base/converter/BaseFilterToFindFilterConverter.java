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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.platkmframework.annotation.db.SearchFilter;
import org.platkmframework.annotation.db.SearchFilterColumn;
import org.platkmframework.common.domain.filter.criteria.base.ConditionFilterBase;
import org.platkmframework.common.domain.filter.enumerator.ConditionOperator;
import org.platkmframework.common.domain.filter.enumerator.GroupOperator;
import org.platkmframework.common.domain.filter.enumerator.MathOperator;
import org.platkmframework.common.domain.filter.enumerator.SqlOperator;
import org.platkmframework.common.domain.filter.ui.BaseFilter;
import org.platkmframework.common.domain.filter.ui.FilterCondition;
import org.platkmframework.comon.service.exception.ServiceException;
import org.platkmframework.content.ioc.ObjectContainer;
import org.platkmframework.database.manager.exception.DatabaseConnectionException;
import org.platkmframework.domain.base.service.base.SearchFilterConditionData;
import org.platkmframework.domain.base.service.base.SearchFilterData; 


/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
public abstract class BaseFilterToFindFilterConverter {
 
	
	private static final Logger logger = LogManager.getLogger(BaseFilterToFindFilterConverter.class);
	  
	
	protected ConverterResultData convertByTableCode(BaseFilter filter, ConditionFilterBase findFilter, String searchMapCode) throws ServiceException {
 
		return convert(filter, findFilter, searchMapCode);
	}
  
	protected ConverterResultData convert(BaseFilter filter, ConditionFilterBase findFilter, String searchMapCode) throws ServiceException {
		   
		ConverterResultData converterResultData = new ConverterResultData();
		if(filter == null) return converterResultData;
		 
		processConditions(converterResultData, filter, findFilter, searchMapCode); 
		
		return converterResultData;
	}  
	
	protected void processConditions(ConverterResultData converterResultData, BaseFilter filter, ConditionFilterBase findFilter, String searchMapCode) throws ServiceException {
		
		try {
			
			if(StringUtils.isNotBlank(searchMapCode)) {
				SearchFilterData searchFilterData = getSearchMapByCode(searchMapCode);
				if(searchFilterData != null){ 
					if(!filter.getConditions().isEmpty() && searchFilterData.getSearchMap() != null && searchFilterData.getSearchMap().size() >0){
						Map<String, SearchFilterConditionData>  colMap =  searchFilterData.getSearchMap();
						List<String> columns = new ArrayList<>();
						List<String> groupsby = new ArrayList<>();
						String columnName = "";
						MathOperator mathOperator;  
						FilterCondition filterCondition;
						for (int i = 0; i <filter.getConditions().size(); i++)
						{
							filterCondition = filter.getConditions().get(i);
							columnName      = filterCondition.getColumnName();
							
							if(StringUtils.isEmpty(columnName)){
								logger.error("El campo del la condición de búsqueda está vacío searchCode-> " + searchMapCode + " indice -> " + i);
								throw new ServiceException("No están correctas las condiciones de búsqueda");
							}
							 
							if(!colMap.containsKey(columnName)){
								logger.error("El código de la condición de búsqueda no se encuetra en la configuración -> " + searchMapCode + " codigo -> " + columnName + "indice->" +i);
								throw new ServiceException("No están correctas las condiciones de búsqueda");
							}
							
							columnName = colMap.get(columnName).getColumn();
							
							if(StringUtils.isBlank(filterCondition.getColumnPrefix())){
								filterCondition.setColumnName(columnName);
							}else {
								filterCondition.setColumnName(filterCondition.getColumnPrefix() + "." + columnName);
							}
								
							checkCustomFormat(filterCondition);
							
							if(StringUtils.isNotBlank(filterCondition.getMathOperation())) {
								mathOperator = MathOperator.valueOf(filterCondition.getMathOperation()); 
								if(mathOperator == null) throw new ServiceException("no se reconoce la operación matemática");
								
								if("sum".equals(mathOperator.name())) {
									columns.add("SUM(" + columnName + ") AS " + columnName);
								}else if("max".equals(mathOperator.name())) {
									columns.add("MAX(" + columnName + ") AS " + columnName);
								}else if("count".equals(mathOperator.name())) {
									columns.add("COUNT(" + columnName + ") AS " + columnName);
								}else if("min".equals(mathOperator.name())) {
									columns.add("MIN(" + columnName + ") AS " + columnName);
								}
								//columns.add(columnName);
								groupsby.add(columnName);
							}
							
							addCondition(findFilter, filterCondition );
						 
						} 
						converterResultData.setSelect(columns.stream().collect(Collectors.joining(",")));
						converterResultData.setGroupBy(groupsby.stream().collect(Collectors.joining(",")));
						//@TODO
						//converterResultData.setHaving(columnName);
					} 
					
					if(StringUtils.isNotBlank(searchFilterData.getFastsearch())) {
						converterResultData.setFastSearchValue(filter.getFastsearch());
						converterResultData.setFastSearchColumns(searchFilterData.getFastsearch().split(","));
					}
					converterResultData.setDefaultOrderType(searchFilterData.getDefaultOrderType());
					converterResultData.setDefaultOrderColumn(searchFilterData.getDefaultOrderColumn());
				}
			}
			 
		}catch (Exception e) {
			logger.error(e,e);
			throw new ServiceException("no se ha podido devolver informacion");
		}
	}
  

	protected SearchFilterData getSearchMapByCode(String searchMapCode) throws ServiceException {
		
		SearchFilter searchFilter = ObjectContainer.instance().getSearchMapInfo(searchMapCode);
		if(searchFilter == null) return null;
		
		SearchFilterData searchFilterData = new SearchFilterData(searchFilter.fastsearch(), searchFilter.orderColumn(), searchFilter.orderType());
		
		Map<String, SearchFilterConditionData> searchMap = new HashMap<>();
		for (SearchFilterColumn searchFilterColumn : searchFilter.columns()){
			searchMap.put(searchFilterColumn.column(), new SearchFilterConditionData(searchFilterColumn.label(),
																					searchFilterColumn.code(), 
																					searchFilterColumn.column()));
		}
		searchFilterData.setSearchMap(searchMap);
		return searchFilterData;
	}

	public void addCondition(ConditionFilterBase findFilter, FilterCondition filterCondition) throws ServiceException 
	{   
		
		SqlOperator sqlOperator;
		GroupOperator groupOperator;
		ConditionOperator conditionOperator;
		if(StringUtils.isNotBlank(filterCondition.getSoperator())) {
			sqlOperator = SqlOperator.valueOf(filterCondition.getSoperator());
			if(sqlOperator == null) {
				 throw new DatabaseConnectionException("no se encontró el operador");
			}
			
			if(SqlOperator.and.name().equalsIgnoreCase(sqlOperator.name())) {
				findFilter.and();
			}else if(SqlOperator.or.name().equalsIgnoreCase(sqlOperator.name())) {
				findFilter.or();
			} 
			 
		}
		if(StringUtils.isNotBlank(filterCondition.getInitGroup())){
			groupOperator = GroupOperator.valueOf(filterCondition.getInitGroup());
			if(groupOperator == null) throw new DatabaseConnectionException("no se encontró el operador"); 
			if(GroupOperator.open.name().equalsIgnoreCase(groupOperator.name())) {
				findFilter.op();
			}else if(GroupOperator.close.name().equalsIgnoreCase(groupOperator.name())) {
				findFilter.cp();
			}  
		}
		
		if(StringUtils.isNotBlank(filterCondition.getOperator())){
			conditionOperator = ConditionOperator.valueOf(filterCondition.getOperator());
			if(conditionOperator == null) throw new DatabaseConnectionException("no se encontró el operador");
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

	
	protected void checkCustomFormat(FilterCondition filterCondition) 
	{
		if(filterCondition.getValue() != null && filterCondition.getValue().toString().contains(":"))
		{
			String[] arrayFecha = filterCondition.getValue().toString().split(":");
			String   formato    = arrayFecha[0].toString().trim();
			
			if("y".equals(formato)) 
			{
				filterCondition.setValue(arrayFecha[1]);
				filterCondition.setColumnName("YEAR(" + filterCondition.getColumnName() + ")");
			}else if("m".equals(formato)) 
			{
				filterCondition.setValue(arrayFecha[1]);
				filterCondition.setColumnName("MONTH(" + filterCondition.getColumnName() + ")");
			}else if("d".equals(formato)) 
			{
				filterCondition.setValue(arrayFecha[1]);
				filterCondition.setColumnName("DAY(" + filterCondition.getColumnName() + ")");
			} 
		} 
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
