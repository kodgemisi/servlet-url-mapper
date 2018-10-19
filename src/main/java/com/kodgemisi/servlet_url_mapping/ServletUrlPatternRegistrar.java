/* © 2017 Kod Gemisi Ltd.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.kodgemisi.servlet_url_mapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>Convenience class to ease usage of {@link ServletUrlPattern}.</p>
 *
 * <p>Note that methods of this class is NOT thread safe unless explicitly stated otherwise in their Javadocs.</p>
 *
 * @author destan
 * @see com.kodgemisi.servlet_url_mapping.MappingServlet
 */
public class ServletUrlPatternRegistrar {

	private final ServletUrlPattern GET;

	private final ServletUrlPattern POST;

	private final ServletUrlPattern PUT;

	private final ServletUrlPattern DELETE;

	private final ServletUrlPattern HEAD;

	private final ServletUrlPattern OPTIONS;

	private final ServletUrlPattern TRACE;

	public ServletUrlPatternRegistrar() {
		this(true);
	}

	/**
	 * @param useTrailingSlashMatch Whether to match to URLs irrespective of the presence of a trailing slash.
	 *                              If enabled a method mapped to "/users" also matches to "/users/". The default value is true.
	 *                              This behavior is consistent with Spring's. See corresponding section in
	 *                              <a href="http://docs.spring.io/spring/docs/5.0.x/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.html#setUseTrailingSlashMatch-boolean-">Spring API</a>
	 */
	public ServletUrlPatternRegistrar(boolean useTrailingSlashMatch) {
		GET = new ServletUrlPattern(useTrailingSlashMatch);
		POST = new ServletUrlPattern(useTrailingSlashMatch);
		PUT = new ServletUrlPattern(useTrailingSlashMatch);
		DELETE = new ServletUrlPattern(useTrailingSlashMatch);
		HEAD = new ServletUrlPattern(useTrailingSlashMatch);
		OPTIONS = new ServletUrlPattern(useTrailingSlashMatch);
		TRACE = new ServletUrlPattern(useTrailingSlashMatch);
	}

	public ServletUrlPatternRegistrar get(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		GET.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar get(String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		GET.register(urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar post(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		POST.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar post(String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		POST.register(urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar put(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		PUT.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar put(String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		PUT.register(urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar delete(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		DELETE.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar delete(String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		DELETE.register(urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar head(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		HEAD.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar head(String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		HEAD.register(urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar options(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		OPTIONS.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar options(String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		OPTIONS.register(urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar trace(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		TRACE.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar trace(String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		TRACE.register(urlPattern, requestHandler, type);
		return this;
	}

	/**
	 * <p>Only parses the url and returns a {@link com.kodgemisi.servlet_url_mapping.ServletUrl} object representing matched url mapping. This method
	 * doesn't invoke the {@code requestHandler} even there is a match.</p>
	 *
	 * <p>This method IS thread-safe</p>
	 *
	 * @param request
	 * @return a {@link com.kodgemisi.servlet_url_mapping.ServletUrl} object representing matched url mapping.
	 */
	public ServletUrl parse(HttpServletRequest request) {
		switch (request.getMethod().toLowerCase()) {
		case "get":
			return GET.parse(request);
		case "post":
			return POST.parse(request);
		case "put":
			return PUT.parse(request);
		case "delete":
			return DELETE.parse(request);
		case "head":
			return HEAD.parse(request);
		case "options":
			return OPTIONS.parse(request);
		case "trace":
			return TRACE.parse(request);
		default:
			throw new IllegalArgumentException(request.getMethod().toLowerCase() + " is not supported.");
		}
	}

	/**
	 * <p><strong>You are not supposed to use this method</strong> when your servlet extends {@link com.kodgemisi.servlet_url_mapping.MappingServlet}.</p>
	 *
	 * <p>If the request matches one of your registered HTTP method and url pattern then corresponding method will be invoked automatically.</p>
	 *
	 * <p>This method IS thread-safe</p>
	 *
	 * @param request
	 * @param response
	 * @return a {@link com.kodgemisi.servlet_url_mapping.ServletUrl} object representing matched url mapping.
	 * @throws ServletException It might be thrown from {@link com.kodgemisi.servlet_url_mapping.ServletUrlPattern#handle}
	 * @throws IOException      It might be thrown from {@link com.kodgemisi.servlet_url_mapping.ServletUrlPattern#handle}
	 */
	public ServletUrl handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		switch (request.getMethod().toLowerCase()) {
		case "get":
			return GET.handle(request, response);
		case "post":
			return POST.handle(request, response);
		case "put":
			return PUT.handle(request, response);
		case "delete":
			return DELETE.handle(request, response);
		case "head":
			return HEAD.handle(request, response);
		case "options":
			return OPTIONS.handle(request, response);
		case "trace":
			return TRACE.handle(request, response);
		default:
			throw new IllegalArgumentException(request.getMethod().toUpperCase() + " is not supported.");
		}
	}

}
