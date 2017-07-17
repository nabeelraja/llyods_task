package com.sapient.dev.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.sapient.dev.core.servlets.ProductServlet;

import io.wcm.testing.mock.aem.junit.AemContext;

@RunWith(MockitoJUnitRunner.class)
public class ProductServletTest {

	@Mock
	SlingHttpServletRequest _httpServletRequest = mock(SlingHttpServletRequest.class);
	SlingHttpServletResponse _httpServletResponse = mock(SlingHttpServletResponse.class);
	QueryBuilder queryBuilder = mock(QueryBuilder.class);
	Session session = mock(Session.class);
	
	@InjectMocks
	ProductServlet _productServlet;

	@Rule
	public final AemContext _context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

	@Before
	public void setUp() {
		try {
			_context.load().json("/sample-data.json", "/content/llyod/en/demo/product-123");
			_context.registerAdapter(ResourceResolver.class, QueryBuilder.class, queryBuilder);
			_context.registerAdapter(ResourceResolver.class, Session.class, session);
			//_httpServletResponse = new MockSlingHttpServletResponse();
			when(_httpServletRequest.getResourceResolver()).thenReturn(_context.resourceResolver());
			when(_httpServletRequest.getRequestPathInfo()).thenReturn(_context.requestPathInfo());
		} catch (Exception e) {
			assertTrue("error running setup: " + e.getMessage(), false);
		}
	}

	@Test
	public void testDoGet() {
		try {
			String TEST_CONTENT = IOUtils.toString(this.getClass().getResourceAsStream("testResponse.json"), "UTF-8");
			MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
			MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) _httpServletRequest.getRequestPathInfo();
			requestPathInfo.setSelectorString("product.123");
			when(_httpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);

			Query query = mock(Query.class);
			SearchResult searchResult = mock(SearchResult.class);
			Hit hit = mock(Hit.class);
			List<Hit> hits = new ArrayList<>();
			hits.add(hit);
			ValueMap properties = mock(ValueMap.class);
			
			when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query);
			when(query.getResult()).thenReturn(searchResult);
			when(searchResult.getHits()).thenReturn(hits);
			when(hit.getProperties()).thenReturn(properties);
			when(properties.get("productId", String.class)).thenReturn("123");
			when(properties.get("jcr:title", String.class)).thenReturn("product-123");
			
			_productServlet.doGet(_httpServletRequest, response);
			
			assertEquals("application/json;charset=UTF-8", response.getContentType());
			assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
			assertEquals(TEST_CONTENT, response.getOutputAsString());
		} catch (Exception e) {
			assertTrue("error running testDoGet: " + e.getMessage(), false);
		}
	}
}
