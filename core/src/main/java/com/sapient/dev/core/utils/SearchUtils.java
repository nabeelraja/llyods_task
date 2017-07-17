package com.sapient.dev.core.utils;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.sapient.dev.core.constants.SearchConstants;

/**
 * The Class SearchUtils.
 */
public final class SearchUtils {

	/**
	 * Instantiates a new search utility.
	 */
	private SearchUtils() {
		// To disable instantiation.
	}
	
	/**
	 * Gets the search result.
	 *
	 * @param resolver the resolver
	 * @param property the property
	 * @param propertyValue the property value
	 * @return the search result
	 */
	public static SearchResult getSearchResult(ResourceResolver resolver, String property, String propertyValue) {
		final Map<String, String> map = new HashMap<>();
		map.put(SearchConstants.PATH, SearchConstants.CONTENT_PATH);
		map.put(SearchConstants.TYPE, SearchConstants.CQ_PAGE);
		map.put(SearchConstants.PROPERTY, property);
		map.put(SearchConstants.PROPERTY_VALUE, propertyValue);

		final QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
		final PredicateGroup predicateGroup = PredicateGroup.create(map);
		final Query query = queryBuilder.createQuery(predicateGroup, resolver.adaptTo(Session.class));
		final SearchResult result = query.getResult();

		return result;
	}

}
