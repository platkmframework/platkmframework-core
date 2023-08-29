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
package org.platkmframework.core.domain.base.converter;

import org.platkmframework.annotation.Service;
import org.platkmframework.common.domain.filter.criteria.CriteriaBase;
import org.platkmframework.common.domain.filter.criteria.SearchCriteria;
import org.platkmframework.common.domain.filter.criteria.vo.SearchCriteriaVO; 



/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 
 * to TRANSFORM SearchCriteria to SearchCriteriaVO and vs, send via API call search criteria info in  simple vo object,with getter and setter
 
 *
 */
@Service
public class SearhCriteriaVOConverter {
	
	
	public   SearchCriteriaVO toVO(CriteriaBase searchCriteria) {
		
		SearchCriteriaVO searchCriteriaVO = new SearchCriteriaVO();
		/**searchCriteriaVO.getList().addAll(searchCriteria.getSql()); 
		
		searchCriteriaVO.setOffSetInfo(new FilterData().addOffSetInfo(searchCriteria.getRecordPerPage(), searchCriteria.getPage()));
		searchCriteriaVO.setFastSearchInfo(new FilterData().addFastSearchInfo(searchCriteria.getFastsearch(), searchCriteria.getFastSearchList()));
		searchCriteriaVO.setAddtionalDataInfo(new FilterData().addAddtionalDataInfo(searchCriteria.getAdditional()));
		searchCriteriaVO.setOrderByInfo(new FilterData().addOrderByInfo(searchCriteria.getOrderColumn(),searchCriteria.getOrderType()));
		*/
		return searchCriteriaVO;
		
	}
	
 
	
	
	public   SearchCriteria toSearchCriteria(SearchCriteriaVO searchCriteriaVO) {
		
		SearchCriteria searchCriteria = new SearchCriteria();
		
		/**
		searchCriteria.getSql().addAll(searchCriteriaVO.getList());
		
		searchCriteria.setRecordPerPage(searchCriteriaVO.getOffSetInfo().getRecordPerPage());
		searchCriteria.setPage(searchCriteriaVO.getOffSetInfo().getPage());
		
		searchCriteria.setFastsearch(searchCriteriaVO.getFastSearchInfo().getFastsearch());
		searchCriteria.setFastSearchList(searchCriteriaVO.getFastSearchInfo().getFastSearchList());
		
		searchCriteria.setAdditional(searchCriteriaVO.getAddtionalDataInfo().getAdditional());
		
		searchCriteria.setOrderBy(searchCriteriaVO.getOrderByInfo().getOrderColumn(), searchCriteriaVO.getOrderByInfo().getOrderType()); 
		*/
		return searchCriteria;
	}
	

}
