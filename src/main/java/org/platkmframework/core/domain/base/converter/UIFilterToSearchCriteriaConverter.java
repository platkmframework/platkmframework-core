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
  
import org.platkmframework.annotation.AutoWired;
import org.platkmframework.annotation.Service;
import org.platkmframework.common.domain.filter.criteria.CriteriaBase;
import org.platkmframework.common.domain.filter.criteria.CriteriaFunction;
import org.platkmframework.common.domain.filter.criteria.FilterCriteria;
import org.platkmframework.common.domain.filter.criteria.SearchCriteria;
import org.platkmframework.common.domain.filter.criteria.WhereCriteria;
import org.platkmframework.common.domain.filter.enumerator.SqlOrder;
import org.platkmframework.common.domain.filter.ui.Filter;
import org.platkmframework.comon.service.exception.ServiceException;
import org.platkmframework.database.manager.processor.util.DaoUtil; 



/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 **/
	@Service  
	public class UIFilterToSearchCriteriaConverter extends BaseFilterToFindFilterConverter {
	 
	@AutoWired
	private SearhCriteriaVOConverter searhCriteriaVOConverter;
 
 
	public SearchCriteria convertToSearchCriteria(Filter filter) throws ServiceException { 
		
		SearchCriteria searchCriteria = new SearchCriteria(); 
		ConverterResultData converterResultData =  this.convert(filter, searchCriteria, null ); 
		searchCriteria.select(converterResultData.getSelect());
		searchCriteria.groupBy(converterResultData.getGroupBy());
		searchCriteria.having(converterResultData.getHaving());
		updateAdditionals(searchCriteria, filter);
		
		return searchCriteria;
	}
	
	public FilterCriteria convertToFilterCriteria(Filter filter,  Class<?>  entityClass) throws ServiceException {
		FilterCriteria filterCriteria = new FilterCriteria();
		this.convert(filter, filterCriteria, DaoUtil.getTableName(entityClass)); 
		updateAdditionals(filterCriteria, filter);
		
		return filterCriteria;
	}
	
	public WhereCriteria convertToQueryCriteria(Filter filter,  Class<?>  entityClass) throws ServiceException {
		WhereCriteria whereCriteria = new WhereCriteria();
		this.convert(filter, whereCriteria, DaoUtil.getTableName(entityClass)); 
		updateAdditionals(whereCriteria, filter);
		
		return whereCriteria;
	}
	
	public WhereCriteria convertToQueryCriteria(Filter filter,  String tableCode) throws ServiceException {
		WhereCriteria queryCriteria = new WhereCriteria(); 
		this.convert(filter, queryCriteria, tableCode); 
		updateAdditionals(queryCriteria, filter);
		
		return queryCriteria;
	}
/**	
	public SearchCriteriaVO convertToSearchCriteriaVO (Filter filter, Class<EntityBase> entityClass) throws ServiceException {
		SearchCriteria searchCriteria = new SearchCriteria();
		this.convert(filter, searchCriteria, DaoUtil.getTableName(entityClass));  
		updateAdditionals(searchCriteria, filter);
		
		return searhCriteriaVOConverter.toVO(searchCriteria);
	}
	*/
	 /**
	public SearchCriteria converToCQueryName(BusinessRule businessRule ,Class<EntityBase> entityClass) throws ServiceException { 
		
		CTableVO cTableVO = mdTableRecoverService.getObjectInfo(businessRule.getCode(), null);
		if(cTableVO == null) throw new ServiceException("No se encontr� informaci�n de la entidad");
		
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.select(cTableVO.getName());
		
		//@TODO ??
		//ConverterResultData converterResultData =  this.convert(null, searchCriteria, DaoUtil.getTableName(entityClass)); 
		//cQueryName.select(converterResultData.getSelect());
		//cQueryName.groupBy(converterResultData.getGroupBy());
		return searchCriteria;
	}
	*/
	protected void updateAdditionals(CriteriaFunction<?> findFilter, Filter filter) {
		
		//order
		
		if(SqlOrder.asc.name().equals(filter.getOrderType()))
			findFilter.orderBy(filter.getOrderColumn(),SqlOrder.asc); 
		else if(SqlOrder.desc.name().equals(filter.getOrderType()))
			findFilter.orderBy(filter.getOrderColumn(),SqlOrder.desc); 
		
		findFilter.addOffSetInfo(filter.getRecordPerPage(), filter.getPage());
		//offset
		//findFilter.setPage(filter.getPage());
		//findFilter.setRecordPerPage(filter.getRecordPerPage());
		   
		//fastsearch
		findFilter.addFastSearchInfo(filter.getFastsearch());
 
	}
	
	protected void updateAdditionals(CriteriaBase findFilter, Filter filter) {
		
		//order
		
		if(SqlOrder.asc.name().equals(filter.getOrderType()))
			findFilter.orderBy(filter.getOrderColumn(),SqlOrder.asc); 
		else if(SqlOrder.desc.name().equals(filter.getOrderType()))
			findFilter.orderBy(filter.getOrderColumn(),SqlOrder.desc); 
		
		findFilter.addOffSetInfo(filter.getRecordPerPage(), filter.getPage());
		//offset
		//findFilter.setPage(filter.getPage());
		//findFilter.setRecordPerPage(filter.getRecordPerPage());
		   
		//fastsearch
		findFilter.addFastSearchInfo(filter.getFastsearch());
 
	}
     
     

}
