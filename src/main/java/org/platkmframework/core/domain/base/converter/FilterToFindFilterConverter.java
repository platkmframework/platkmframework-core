package org.platkmframework.core.domain.base.converter;

import org.platkmframework.common.domain.filter.criteria.FilterCriteria;
import org.platkmframework.common.domain.filter.criteria.SearchCriteria;
import org.platkmframework.common.domain.filter.criteria.WhereCriteria;
import org.platkmframework.common.domain.filter.ui.Filter;
import org.platkmframework.comon.service.exception.ServiceException;

public interface FilterToFindFilterConverter {
	
	public SearchCriteria convertToSearchCriteria(Filter<?> filter, String searchMapCode) throws ServiceException;
	
	public FilterCriteria convertToFilterCriteria(Filter<?> filter, String searchMapCode) throws ServiceException;
	
	public WhereCriteria convertToQueryCriteria(Filter<?> filter,  String searchMapCode ) throws ServiceException;

}
