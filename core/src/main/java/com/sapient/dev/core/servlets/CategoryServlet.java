/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.sapient.dev.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.sapient.dev.core.constants.CommonConstants;
import com.sapient.dev.core.dto.ProductDTO;
import com.sapient.dev.core.exception.ProductException;
import com.sapient.dev.core.utils.SearchUtils;
import com.sapient.dev.core.utils.ServletUtils;

/**
 * Servlet that returns the product information as a response.
 * 
 */
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Get Product Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.resourceTypes=" + "sling/servlet/default",
		"sling.servlet.selectors=" + "category", "sling.servlet.extensions=" + "json" })
public class CategoryServlet extends SlingSafeMethodsServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1947838500043638271L;
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(CategoryServlet.class);
	
	/** The Constant PROPERTY_JCR_CONSTANTS. */
	private static final String PROPERTY_JCR_CONSTANTS = "jcr:content";
	
	/** The Constant PROPERTY_PRODUCT_ID. */
	private static final String PROPERTY_PRODUCT_ID = "productId";

	/** The Constant PROPERTY_PRODUCT_ID. */
	private static final String CATEGORY = "category";

	/** The query builder. */
	@Reference
	private QueryBuilder builder;

	/* (non-Javadoc)
	 * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
	 */
	@Override
	public void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		final ResourceResolver resourceResolver = request.getResourceResolver();
		final List<String> selectors = ServletUtils.parseSelectors(request);
		List<ProductDTO> productList = new ArrayList<ProductDTO>();
		Stopwatch stopwatch = Stopwatch.createStarted();
		try {
			//if selectors are empty throw exception
			if (selectors.isEmpty()) {
				throw new ProductException("Selectors for category are missing from the request");
			} else {
				LOG.debug("Parsed Selectors are: {}", selectors.toString());
				String category = selectors.get(0);
				productList = getProducts(category, resourceResolver);
				
				response.setContentType(CommonConstants.CONTENT_TYPE_JSON);
				response.setCharacterEncoding(CommonConstants.UTF_8);
				response.getWriter().write(new Gson().toJson(productList));
				response.setStatus(SlingHttpServletResponse.SC_OK);
			}
		} catch (ProductException e) {
			LOG.error("Exception occured", e);
			response.setContentType(CommonConstants.CONTENT_TYPE_JSON);
			response.setCharacterEncoding(CommonConstants.UTF_8);
			response.getWriter().write(e.getMessage());
			response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		//stop the stopwatch and log the execution time
		stopwatch.stop();
		LOG.debug("Time elapsed for Get Product Servlet is: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
	}

	/**
	 * Gets the product.
	 *
	 * @param category the category
	 * @param resolver the resolver
	 * @return the product
	 * @throws ProductException the product exception
	 */
	private List<ProductDTO> getProducts(String category, ResourceResolver resolver) throws ProductException {
		List<ProductDTO> products = new ArrayList<ProductDTO>();
		
		//Get the results from the Search Utils
		final SearchResult result = SearchUtils.getSearchResult(resolver, PROPERTY_JCR_CONSTANTS + "/" + CATEGORY, category);
		final List<Hit> hits = result.getHits();
		LOG.debug("Query for {} returned with {} records", category, hits.size());
		try {
			if (!hits.isEmpty()) {
				for (Hit hit : hits) {
					ProductDTO product = new ProductDTO();
					ValueMap properties = hit.getProperties();
					product.setId(properties.get(PROPERTY_PRODUCT_ID, String.class));
					product.setTitle(properties.get(JcrConstants.JCR_TITLE, String.class));
					products.add(product);
					LOG.debug("****** Result Path: {} ********", hit.getPath());
				}
			}
		} catch (RepositoryException e) {
			LOG.error("Error getting data for {}", category, e);
			throw new ProductException("Error getting data for: " + category);
		}
		return products;
	}
}
