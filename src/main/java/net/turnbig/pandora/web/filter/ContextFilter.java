/**
 * @(#)GlobalAttributeFilter.java 2015年6月11日
 *
 * Copyright 2008-2015 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.collect.Lists;
import net.turnbig.pandora.web.Servlets;

/**
 * 
 * inject common contexts to request
 * 
 * <li>ctx -- project base path</li>
 * 
 * @author Woo Cupid
 * @date 2015年6月11日
 * @version $Revision$
 */
@Component(value = "ContextFilter")
public class ContextFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(ContextFilter.class);

	private static final String ATTR_CTX = "ctx";
	private static final String ATTR_ENV = "env";
	private static final String ATTR_VERSION = "v";
	private static final String ATTR_PRO = "PRO";

	private static final String ATTR_QUERY_PART = "Q_";
	private static final String ATTR_REQUEST_URL = "url_";
	private static final String ATTR_HREF = "href_";

	public static final String PAGINATION_ATTRNAME_PAGE = "page";
	public static final String PAGINATION_ATTRNAME_PAGESIZE = "pagesize";

	private String excludes = null;
	private Pattern excludePattern = null;

	String env;
	String version;

	@Resource(name = "properties")
	Properties properties;

	private Map<String, String> project = new HashMap<String, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.filter.GenericFilterBean#afterPropertiesSet()
	 */
	@Override
	public void initFilterBean() throws ServletException {
		FilterConfig filterConfig = this.getFilterConfig();
		if (filterConfig != null && filterConfig.getInitParameter("excludes") != null) {
			excludes = filterConfig.getInitParameter("excludes");
		}

		if (excludes != null) {
			this.excludePattern = Pattern.compile(excludes);
		}
		env = properties.getProperty("pro.env", "prod");
		version = properties.getProperty("pro.version", String.valueOf(new Date().getTime()));

		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			Object nextElement = keys.nextElement();
			if (nextElement.toString().startsWith("pro.")) {
				project.put(StringUtils.removeStart(nextElement.toString(), "pro."),
						properties.get(nextElement).toString());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String relatedpath = Servlets.getRelatedpath(request);
			if (excludePattern == null || !excludePattern.matcher(relatedpath).find()) {
				String context = Servlets.getBasePath(request);

				request.setAttribute(ATTR_CTX, context);
				request.setAttribute(ATTR_ENV, env);
				request.setAttribute(ATTR_VERSION, "dev".equals(env) ? String.valueOf(new Date().getTime()) : version);

				// build query part string without pagination
				String queryPartString = getQueryPartString(request, PAGINATION_ATTRNAME_PAGE,
						PAGINATION_ATTRNAME_PAGESIZE);
				request.setAttribute(ATTR_QUERY_PART, queryPartString);
				request.setAttribute(ATTR_REQUEST_URL, request.getRequestURL().toString());
				String q = StringUtils.isBlank(queryPartString) ? "" : "?" + queryPartString;
				request.setAttribute(ATTR_HREF, request.getRequestURL().toString() + q);

				request.setAttribute(ATTR_PRO, project);
			}
		} catch (Exception e) {
			logger.error("Failed to setup common context", e);
		} finally {
			filterChain.doFilter(request, response);
		}

	}

	/**
	 * @param request
	 * @return 
	 */
	public static String getQueryPartString(HttpServletRequest request, String... excludes) {
		StringBuilder queryBuilder = new StringBuilder();
		Map<String, String[]> parameterMap = request.getParameterMap();
		ArrayList<String> excludeList = Lists.newArrayList(excludes);
		for (Entry<String, String[]> entry : parameterMap.entrySet()) {
			String name = entry.getKey();
			if (excludeList.contains(name)) {
				continue;
			}
			String[] values = entry.getValue();
			if (values == null || values.length == 0) {
				if (queryBuilder.length() != 0) {
					queryBuilder.append('&');
				}
				queryBuilder.append(name);
			} else {
				for (String value : values) {
					if (queryBuilder.length() != 0) {
						queryBuilder.append('&');
					}
					queryBuilder.append(name);

					if (value != null) {
						queryBuilder.append('=');
						queryBuilder.append(value.toString());
					}
				}
			}
		}

		return queryBuilder.toString();
	}

	/**
	 * @param excludes the excludes to set
	 */
	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}

}
