/* © 2017 Kod Gemisi Ltd.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.kodgemisi.servlet_url_mapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>Every servlet which needs automatic servlet mapping should extend from this servlet.</p>
 *
 * <blockquote><pre>
 * {@code @WebServlet(urlPatterns = {"/products", "/products/*"})}
 * public class MyServlet extends MappingServlet {
 *
 *   public MyServlet() {
 *       this.urlMappingRegistrar =
 *              // matches GET request to "host/context-root/products"
 *              .get("list", "/", this::list)
 *
 *              // matches GET request to "host/context-root/products/all"
 *              .get("all", "/all", this::list) // note that the same method can be used for multiple url mappings
 *
 *              // matches GET request to "host/context-root/products/{id}"
 *              .get("show", "/{id}", this::show)
 *
 *              // matches POST request to "host/context-root/products"
 *              .post("create", "/", this::create)
 *
 *              // matches POST request to "host/context-root/products/{id}/address"
 *              .post("add address", "/{id}/address", this::addAddress);
 *       // and so on...
 *   }
 *
 *   // any access modifier can be used
 *   private void list(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws ServletException, IOException {
 *       // your code...
 *   }
 *
 *   private void show(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws ServletException, IOException {
 *       Integer id = servletUrl.variable("id"); // this is parsed from url: /{id}
 *       // your code...
 *   }
 *
 *   // and rest of your methods...
 * }
 * </pre></blockquote>
 *
 * <p>When a request matching your registered HTTP method and url pattern is made then your given method will be invoked automatically.</p>
 *
 * @author destan
 */
public class MappingServlet extends HttpServlet {

	/**
	 * Every servlet needs a copy of {@link ServletUrlPatternRegistrar}. Servlets extending this servlet should use this field to register url
	 * mappings.
	 */
	protected final ServletUrlPatternRegistrar urlMappingRegistrar;

	/**
	 * Default is {@link LoggingExceptionHandler}
	 */
	protected final ExceptionHandler exceptionHandler;

	public MappingServlet() {
		this(true);
	}

	/**
	 * @param useTrailingSlashMatch Whether to match to URLs irrespective of the presence of a trailing slash.
	 *                              If enabled a method mapped to "/users" also matches to "/users/". The default value is true.
	 *                              This behavior is consistent with Spring's. See corresponding section in
	 *                              <a href="http://docs.spring.io/spring/docs/5.0.x/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.html#setUseTrailingSlashMatch-boolean-">Spring API</a>
	 */
	public MappingServlet(boolean useTrailingSlashMatch) {
		this(new LoggingExceptionHandler(), useTrailingSlashMatch);
	}

	public MappingServlet(ExceptionHandler exceptionHandler, boolean useTrailingSlashMatch) {
		this.urlMappingRegistrar = new ServletUrlPatternRegistrar(useTrailingSlashMatch);
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doCommon(request, response);
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doCommon(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doCommon(request, response);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doCommon(request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doCommon(request, response);
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doCommon(request, response);
	}

	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doCommon(request, response);
	}

	private void doCommon(HttpServletRequest request, HttpServletResponse response) {
		final ServletUrl servletUrl;
		try {
			servletUrl = urlMappingRegistrar.handle(request, response);

			if (servletUrl.is404()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}
		catch (Exception e) {
			this.exceptionHandler.handleException(request, response, e);
		}

	}

}
