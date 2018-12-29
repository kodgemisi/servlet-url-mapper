/* © 2017 Kod Gemisi Ltd.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.kodgemisi.servlet_url_mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;

/**
 * <p>If you are in a Servlet please prefer {@link ServletUrlPatternRegistrar} instead of this class because this is a low level API.</p>
 *
 * <p>However you can go manuel and instead of using {@link MappingServlet} and {@link ServletUrlPatternRegistrar} you can use {@link
 * com.kodgemisi.servlet_url_mapping.ServletUrlPattern} directly to register url patterns.</p>
 *
 * <p>When you register url patterns <strong>with</strong> a {@code requestHandler} then you need to use
 * {@link #handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)} as follows:</p>
 *
 * <blockquote><pre>
 * void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 *      // Note that it's "ServletUrlPattern" and not "ServletUrlPatternRegistrar"
 *      // You would use this.servletUrlPattern.register in your Servlet's constructor or init
 *      final ServletUrl servletUrl = this.servletUrlPattern.handle(request, response);
 *
 *      if (servletUrl.is404()) {
 *          response.sendError(HttpServletResponse.SC_NOT_FOUND);
 *      }
 * }
 * </pre></blockquote>
 * <p>
 * When you call {@link #handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)} method, your matching
 * {@code requestHandler} method will be called. If there is no matching url mappings then {@code ServletUrl.NOT_FOUND} is returned.
 * You should check for {@code ServletUrl.NOT_FOUND} and send {@link javax.servlet.http.HttpServletResponse.SC_NOT_FOUND} manually.
 * <p>
 * <hr/>
 *
 * <p>When you register url patterns <strong>without</strong> a {@code requestHandler} then you need to manually check to see which pattern is matched with the request
 * as follows:</p>
 *
 * <blockquote><pre>
 * void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 *      // Note that it's "ServletUrlPattern" and not "ServletUrlPatternRegistrar"
 *      // You would use this.servletUrlPattern.register in your Servlet's constructor or init
 *      ServletUrl servletUrl = this.servletUrlPattern.parse(request);
 *
 *      switch (servletUrl.getName()) {
 *        case "list": {
 *            // handle request and write to response
 *            return;
 *        }
 *
 *        // other cases
 *
 *        case ServletUrl.NOT_FOUND_404:
 *            response.sendError(HttpServletResponse.SC_NOT_FOUND);
 *            return;
 *
 *        default:
 *            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
 *            return;
 *      }
 * }
 * </pre></blockquote>
 *
 * @author destan
 */
public class ServletUrlPattern {

	private static final Logger log = LoggerFactory.getLogger(ServletUrlPattern.class);

	private final LinkedHashSet<ServletUrl> urlMappings;

	/**
	 * <p>Whether to match to URLs irrespective of the presence of a trailing slash. If enabled a method mapped to "/users" also matches to "/users/".</p>
	 * <p>The default value is true.</p>
	 *
	 * @see <a href="http://docs.spring.io/spring/docs/5.0.x/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.html#setUseTrailingSlashMatch-boolean-">Spring Documentation</a>
	 */
	private boolean useTrailingSlashMatch = true;

	public ServletUrlPattern() {
		urlMappings = new LinkedHashSet<>();
	}

	public ServletUrlPattern(boolean useTrailingSlashMatch) {
		this();
		this.useTrailingSlashMatch = useTrailingSlashMatch;
	}

	/**
	 * <p>For a Servlet extending {@link com.kodgemisi.servlet_url_mapping.MappingServlet} you normally should use {@link
	 * com.kodgemisi.servlet_url_mapping.ServletUrlPatternRegistrar}'s {@code get}, {@code post}, {@code put} etc. methods.
	 * However when you want to manually check url mappings, for example in a Servlet Filter, you can use this method.</p>
	 *
	 * <p>This method is NOT thread-safe. Should only be called from a {@code constructor} or {@link javax.servlet.http.HttpServlet#init()} or called
	 * in a synchronized block.</p>
	 *
	 * @param name           (optional, maybe null or empty) The name of your choice for this url pattern. This parameter is optional when using this
	 *                       version of {@code register} method.
	 *                       Only useful when you need to know the matched {@link ServletUrl} in {@code requestHandler}.
	 * @param requestHandler A lambda function or function reference which will be used as the handler of matching requests
	 * @param urlPattern     Similar to Spring's or JAX-RS's url patterns but only supports variables through {@literal { }}
	 * @param type           optional type information for path variables i.e {@literal { }}. All Path variables considered as {@code Integer} by
	 *                       default. You
	 *                       should give types in the same order as you type {@literal { }} expressions in {@code urlPattern}
	 * @return
	 * @throws IllegalArgumentException if type is not one of those: {@link String}, {@link java.lang.Number}, {@link java.lang.Boolean}
	 */
	public ServletUrlPattern register(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type)
			throws IllegalArgumentException {

		// be tolerant :)
		if (!urlPattern.startsWith("/")) {
			urlPattern = '/' + urlPattern;
		}

		urlMappings.add(new ServletUrl(name, urlPattern, type, requestHandler));
		return this;
	}

	/**
	 * <p>For a Servlet extending {@link com.kodgemisi.servlet_url_mapping.MappingServlet} you normally should use {@link
	 * com.kodgemisi.servlet_url_mapping.ServletUrlPatternRegistrar}'s {@code get}, {@code post}, {@code put} etc. methods.
	 * However when you want to manually check url mappings, for example in a Servlet Filter, you can use this method.</p>
	 *
	 * <p>This method is NOT thread-safe. Should only be called from a {@code constructor} or {@link javax.servlet.http.HttpServlet#init()} or called
	 * in a synchronized block.</p>
	 *
	 * @param requestHandler A lambda function or function reference which will be used as the handler of matching requests
	 * @param urlPattern     Similar to Spring's or JAX-RS's url patterns but only supports variables through {@literal { }}
	 * @param type           optional type information for path variables i.e {@literal { }}. All Path variables considered as {@code Integer} by
	 *                       default. You
	 *                       should give types in the same order as you type {@literal { }} expressions in {@code urlPattern}
	 * @return
	 * @throws IllegalArgumentException if type is not one of those: {@link String}, {@link java.lang.Number}, {@link java.lang.Boolean}
	 */
	public ServletUrlPattern register(String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		//FIXME check if this really throws IllegalArgumentException, if so add to javadoc, if not remove from overloaded version's javadoc

		this.register("", urlPattern, requestHandler, type);
		return this;
	}

	/**
	 * <p>This is for low level usage, prefer {@link #register(String, String, ServletRequestHandler, Class[])} method for automatic request
	 * handling.</p>
	 *
	 * <p>You can go manuel and instead of using {@link MappingServlet} and {@link ServletUrlPatternRegistrar} you can use {@link
	 * com.kodgemisi.servlet_url_mapping.ServletUrlPattern} directly to register url patterns. When you register url patterns without a {@code
	 * requestHandler} then you need to manually check to see which pattern is matched with the
	 * request as follows:</p>
	 *
	 * <blockquote><pre>
	 *     void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	 *
	 *          // Note that it's "ServletUrlPattern" and not "ServletUrlPatternRegistrar"
	 *          // You would use this.servletUrlPattern.register in your Servlet's constructor or init
	 *          ServletUrl servletUrl = this.servletUrlPattern.parse(request);
	 *
	 *          switch (servletUrl.getName()) {
	 *            case "list": {
	 *                // handle request and write to response
	 *                return;
	 *            }
	 *
	 *            // other cases
	 *
	 *            case ServletUrl.NOT_FOUND_404:
	 *                response.sendError(HttpServletResponse.SC_NOT_FOUND);
	 *                return;
	 *
	 *            default:
	 *                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	 *                return;
	 *          }
	 *     }
	 * </pre></blockquote>
	 *
	 * <p>This method is NOT thread-safe. Should only be called from a {@code constructor} or {@link javax.servlet.http.HttpServlet#init()} or called
	 * in a synchronized block.</p>
	 *
	 * @param name       (mandatory) The name of your choice for this url pattern. This name will be used to check if the parsed {@link ServletUrl}
	 *                   matches this
	 *                   particular pattern.
	 * @param urlPattern Similar to Spring's or JAX-RS's url patterns but only supports variables through {@literal { }}
	 * @param type       optional type information for path variables i.e {@literal { }}. All Path variables considered as {@code int} by default.
	 *                   You
	 *                   should give types in the same order as you type {@literal { }} expressions in {@code urlPattern}
	 * @return
	 * @throws IllegalArgumentException if type is not one of those: {@link String}, {@link java.lang.Number}, {@link java.lang.Boolean}
	 * @throws IllegalArgumentException if name is null
	 */
	public ServletUrlPattern register(String name, String urlPattern, Class<?>... type) {
		if (name == null) {
			throw new IllegalArgumentException(
					"Name cannot be null when registering without a 'requestHandler' function. How will you check whether the request matches your pattern or not?");
		}
		return this.register(name, urlPattern, null, type);
	}

	/**
	 * Equivalent of calling {@code parse(request.getPathInfo())}
	 *
	 * @param request
	 * @return
	 * @see ServletUrlPattern#parse(String)
	 */
	public ServletUrl parse(HttpServletRequest request) {
		return parse(request.getPathInfo());
	}

	/**
	 * This method is thread-safe and intended to be used in Servlet's {@code doXxx} methods.
	 *
	 * @param url It should be {@link javax.servlet.http.HttpServletRequest#getPathInfo()}
	 * @return Copy of the matched ServletUrl or a special instance of ServletUrl whose name is {@link ServletUrl#NOT_FOUND_404}
	 */
	public ServletUrl parse(final String url) {
		for (final ServletUrl servletUrl : urlMappings) {

			final String processedUrl = arrangeUrlForTrailingSlash(url, servletUrl.hasTrailingSlash());
			final Matcher matcher = servletUrl.getPattern().matcher(processedUrl);

			if (matcher.matches()) {
				final ServletUrl result = new ServletUrl(servletUrl);

				for (int i = 0; i < matcher.groupCount(); i++) {
					final String variable = matcher.group(i + 1);
					result.addVariable(variable);
				}

				return result;
			}
		}

		if (log.isTraceEnabled()) {
			log.trace("URL {} didn't match any registered urls!", url);
		}
		return ServletUrl.NOT_FOUND; // TODO Consider making this case a checked exception.
	}

	/**
	 * This method is thread-safe and intended to be used in Servlet's {@code doXxx} methods.
	 *
	 * @param request  {@link javax.servlet.http.HttpServletRequest}
	 * @param response {@link javax.servlet.http.HttpServletResponse}
	 * @return Copy of the matched ServletUrl or a special instance of ServletUrl whose name is {@link ServletUrl#NOT_FOUND_404}
	 * @throws IOException      It might be thrown from
	 *                          {@link com.kodgemisi.servlet_url_mapping.ServletRequestHandler#handleRequest}
	 * @throws ServletException It might be thrown from {@link com.kodgemisi.servlet_url_mapping.ServletRequestHandler#handleRequest}
	 */
	public ServletUrl handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		final ServletUrl servletUrl = this.parse(request);
		final ServletRequestHandler servletRequestHandler = servletUrl.getRequestHandler();
		if (servletRequestHandler != null) {
			if (log.isTraceEnabled()) {
				log.trace("Handling request for {}, {}", servletUrl.getName(), servletUrl.getPattern());
			}
			servletRequestHandler.handleRequest(request, response, servletUrl);
		}
		return servletUrl;
	}

	/**
	 * <p>
	 * When url consists of only servlet's root url, request.getPathInfo() returns null,
	 * url shouldn't be null for our usage because we assume empty string for root url
	 * </p>
	 * <pre>
	 * url		pattern		action
	 * ----------------------------
	 *  1			1		nothing
	 *  1			0		remove
	 *  0			1		add
	 *  0			0		nothing
	 * </pre>
	 *
	 * @param url
	 * @param patternHasTrailingSlash
	 * @return
	 * @see javax.servlet.http.HttpServletRequest#getPathInfo()
	 */
	private String arrangeUrlForTrailingSlash(String url, boolean patternHasTrailingSlash) {

		if (url == null) {
			url = "";
		}

		if (useTrailingSlashMatch) {
			boolean urlHasTrailingSlash = url.length() > 0 && url.substring(url.length() - 1, url.length()).equals("/");

			if (urlHasTrailingSlash == true && patternHasTrailingSlash == false) {
				return url.substring(0, url.length() - 1);
			}
			if (urlHasTrailingSlash == false && patternHasTrailingSlash == true) {
				return url + '/';
			}
		}
		return url;
	}

}

