/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package net.turnbig.pandora.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import com.google.common.collect.Lists;
import net.turnbig.pandora.utils.Collections3;
import net.turnbig.pandora.utils.Encodes;

/**
 * Http与Servlet工具类.
 * 
 * @author calvin
 */
public class Servlets {

	// -- 常用数值定义 --//
	public static final long ONE_YEAR_SECONDS = 60 * 60 * 24 * 365;

	/**
	 * 设置客户端缓存过期时间 的Header.
	 */
	public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
		// Http 1.0 header, set a fix expires date.
		response.setDateHeader(HttpHeaders.EXPIRES, System.currentTimeMillis() + (expiresSeconds * 1000));
		// Http 1.1 header, set a time after now.
		response.setHeader(HttpHeaders.CACHE_CONTROL, "private, max-age=" + expiresSeconds);
	}

	/**
	 * 设置禁止客户端缓存的Header.
	 */
	public static void setNoCacheHeader(HttpServletResponse response) {
		// Http 1.0 header
		response.setDateHeader(HttpHeaders.EXPIRES, 1L);
		response.addHeader(HttpHeaders.PRAGMA, "no-cache");
		// Http 1.1 header
		response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0");
	}

	/**
	 * 设置LastModified Header.
	 */
	public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
		response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModifiedDate);
	}

	/**
	 * 设置Etag Header.
	 */
	public static void setEtag(HttpServletResponse response, String etag) {
		response.setHeader(HttpHeaders.ETAG, etag);
	}

	/**
	 * 根据浏览器If-Modified-Since Header, 计算文件是否已被修改.
	 * 
	 * 如果无修改, checkIfModify返回false ,设置304 not modify status.
	 * 
	 * @param lastModified 内容的最后修改时间.
	 */
	public static boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response,
			long lastModified) {
		long ifModifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
		if ((ifModifiedSince != -1) && (lastModified < (ifModifiedSince + 1000))) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return false;
		}
		return true;
	}

	/**
	 * 根据浏览器 If-None-Match Header, 计算Etag是否已无效.
	 * 
	 * 如果Etag有效, checkIfNoneMatch返回false, 设置304 not modify status.
	 * 
	 * @param etag 内容的ETag.
	 */
	public static boolean checkIfNoneMatchEtag(HttpServletRequest request, HttpServletResponse response, String etag) {
		String headerValue = request.getHeader(HttpHeaders.IF_NONE_MATCH);
		if (headerValue != null) {
			boolean conditionSatisfied = false;
			if (!"*".equals(headerValue)) {
				StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");

				while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
					String currentToken = commaTokenizer.nextToken();
					if (currentToken.trim().equals(etag)) {
						conditionSatisfied = true;
					}
				}
			} else {
				conditionSatisfied = true;
			}

			if (conditionSatisfied) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				response.setHeader(HttpHeaders.ETAG, etag);
				return false;
			}
		}
		return true;
	}

	/**
	 * 设置让浏览器弹出下载对话框的Header.
	 * 
	 * @param fileName 下载后的文件名.
	 */
	public static void setFileDownloadHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
		try {
			String agent = request.getHeader("User-Agent");
			boolean isMSIE = ((agent != null) && (agent.indexOf("MSIE") != -1));
			String encoded = isMSIE ? URLEncoder.encode(fileName, "UTF-8") : new String(fileName.getBytes("UTF-8"),
					"ISO-8859-1");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encoded + "\"");
		} catch (UnsupportedEncodingException e) {

		}
	}

	/**
	 * 取得带相同前缀的Request Parameters, copy from spring WebUtils.
	 * 
	 * 返回的结果的Parameter名已去除前缀.
	 */
	public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
		Enumeration paramNames = request.getParameterNames();
		Map<String, Object> params = new TreeMap<String, Object>();

		String prefix_ = prefix == null ? "" : prefix;
		while ((paramNames != null) && paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if ("".equals(prefix_) || paramName.startsWith(prefix_)) {
				String unprefixed = paramName.substring(prefix_.length());
				String[] values = request.getParameterValues(paramName);
				if ((values == null) || (values.length == 0)) {
					// Do nothing, no values found at all.
				} else if (values.length > 1) {
					params.put(unprefixed, values);
				} else {
					params.put(unprefixed, values[0]);
				}
			}
		}
		return params;
	}

	/**
	 * 取得Request Parameters, copy from spring WebUtils.
	 * 
	 * 返回的结果的Parameter
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getParameters(ServletRequest request) {
		Enumeration paramNames = request.getParameterNames();
		Map<String, Object> params = new TreeMap<String, Object>();
		while ((paramNames != null) && paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] values = request.getParameterValues(paramName);
			if ((values == null) || (values.length == 0)) {
				// Do nothing, no values found at all.
			} else if (values.length > 1) {
				params.put(paramName, values);
			} else {
				params.put(paramName, values[0]);
			}
		}
		return params;
	}

	/**
	 * 组合Parameters生成Query String的Parameter部分, 并在paramter name上加上prefix.
	 * 
	 * @see #getParametersStartingWith
	 */
	public static String encodeParameterStringWithPrefix(Map<String, Object> params, String prefix) {
		if ((params == null) || (params.size() == 0)) {
			return "";
		}

		String prefix_ = prefix == null ? "" : prefix;
		List<String> list = Lists.newArrayList();
		Iterator<Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			Object values = entry.getValue();
			if (values instanceof String[]) {
				for (String v : (String[]) values) {
					list.add(String.format("%s%s=%s", prefix_, entry.getKey(), v));
				}
			} else if (values instanceof String) {
				list.add(String.format("%s%s=%s", prefix_, entry.getKey(), values));
			}
		}

		return Collections3.convertToString(list, "&");
	}

	/**
	 * 客户端对Http Basic验证的 Header进行编码.
	 */
	public static String encodeHttpBasic(String userName, String password) {
		String encode = userName + ":" + password;
		return "Basic " + Encodes.encodeBase64(encode.getBytes());
	}

	/**
	 * http://domain:port/context/related-path
	 * 
	 * @param request
	 * @return http://domain:port/context
	 */
	public static String getBasePath(HttpServletRequest request) {
		String requestURL = request.getRequestURL().toString(); // whole URL
		String requestURI = request.getRequestURI(); // context-path + path
		String host = StringUtils.substringBeforeLast(requestURL, requestURI);
		String contextPath = request.getContextPath();
		return StringUtils.removeEnd(host + contextPath, "/");
	}

	/**
	 * 
	 * http://domain:port/context/related-path
	 * 
	 * @param request
	 * @return related-path
	 */
	public static String getRelatedpath(HttpServletRequest request) {
		String requestURI = request.getRequestURI(); // context-path + path
		String contextPath = request.getContextPath();
		return StringUtils.removeStart(requestURI, contextPath);
	}

	public static String getHost(HttpServletRequest request) {
		String requestURL = request.getRequestURL().toString();
		String requestURI = request.getRequestURI();
		String host = StringUtils.substringBefore(requestURL, requestURI);
		return host;
	}

	public static String getIp(HttpServletRequest request) {
		String xforward = request.getHeader("x-forwarded-for");
		// TODO shall we use a more reliable solution?
		// compare xforward with haproxy ip
		if (StringUtils.isNotEmpty(xforward)) {
			String[] split = StringUtils.split(xforward, ".");
			if (split.length == 4) {
				return xforward;
			}
		}
		return request.getRemoteAddr();
	}

	public static void output(HttpServletResponse response, String contentType, String content) throws IOException {
		PrintWriter writer = response.getWriter();
		try {
			response.setContentType(contentType);
			writer.write(content);
		} finally {
			writer.flush();
			writer.close();
		}
	}
}
