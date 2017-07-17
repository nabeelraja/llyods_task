package com.sapient.dev.core.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class ServletUtils.
 */
public final class ServletUtils {

	/**
	 * Instantiates a new servlet utility.
	 */
	private ServletUtils() {
		// To disable instantiation.
	}

	/**
	 * Parses the selectors.
	 *
	 * @param request the request
	 * @return the list
	 */
	public static List<String> parseSelectors(final SlingHttpServletRequest request) {
		List<String> selectors = new LinkedList<String>();
		final RequestPathInfo requestPathInfo = request.getRequestPathInfo();
		selectors = new LinkedList<String>(Arrays.asList(requestPathInfo.getSelectors()));
		if (!selectors.isEmpty()) {
			selectors.remove(0);
		}
		return selectors;
	}

}
