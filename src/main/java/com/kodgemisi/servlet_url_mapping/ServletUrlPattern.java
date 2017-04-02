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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * <p>If you are in a Servlet please prefer {@link ServletUrlPatternRegistrar} instead of this class because this class is low level.</p>
 * <p>Intended to be used one instance per filter (or servlet).</p>
 *
 * @author destan
 */
public class ServletUrlPattern {

	private static final Logger log = LoggerFactory.getLogger(ServletUrlPattern.class);

	//	private final List<ServletUrl> urls;

	private final Map<ServletUrl, ServletRequestHandler> urlMappings;

	/**
	 * http://docs.spring.io/spring/docs/5.0.x/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.html#setUseTrailingSlashMatch-boolean-
	 */
	private boolean useTrailingSlashMatch = true;

	public ServletUrlPattern() {
		urlMappings = new HashMap<>();
	}

	public ServletUrlPattern(boolean useTrailingSlashMatch) {
		this();
		this.useTrailingSlashMatch = useTrailingSlashMatch;
	}

	/**
	 * <p>For a Servlet extending {@link com.kodgemisi.servlet_url_mapping.MappingServlet} you normally should use {@link
	 * com.kodgemisi.servlet_url_mapping.ServletUrlPatternRegistrar}'s {@code get}, {@code post}, {@code put} etc. methods.</p>
	 *
	 * <p>This method is NOT thread-safe. Should only be called from a {@code constructor} or {@link javax.servlet.http.HttpServlet#init()} or called
	 * in
	 * a synchronized block.</p>
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
		if (urlPattern.startsWith("/") == false) {
			urlPattern = '/' + urlPattern;
		}

		urlMappings.put(new ServletUrl(name, urlPattern, type), requestHandler);
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
					"Name cannot be null registering without a 'requestHandler' function. How will you check whether the request matches your pattern or not?");
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
	 * @return The matched ServletUrl or a special instance of ServletUrl whose name is {@link ServletUrl#NOT_FOUND_404}
	 */
	public ServletUrl parse(final String url) {
		try {
			return parseInternal(url, false, null, null);
		}
		catch (ServletException | IOException e) {
			// this is expected to never happen
			throw new RuntimeException(e);
		}
	}

	public ServletUrl handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return parseInternal(request.getPathInfo(), true, request, response);
	}

	private ServletUrl parseInternal(final String url, boolean invokeHandler, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		for (final ServletUrl servletUrl : urlMappings.keySet()) {

			String processedUrl = arrangeUrlForTrailingSlash(url, servletUrl.hasTrailingSlash());
			Matcher matcher = servletUrl.getPattern().matcher(processedUrl);

			if (matcher.matches()) {
				ServletUrl result = new ServletUrl(servletUrl);

				for (int i = 0; i < matcher.groupCount(); i++) {
					String variable = matcher.group(i + 1);
					result.addVariable(variable);
				}

				if (invokeHandler && urlMappings.get(servletUrl) != null) {
					urlMappings.get(servletUrl).handleRequest(request, response, result);
				}

				return result;
			}
		}

		// make this exception a checked one or find another solution
		log.trace("URL {} didn't match any registered urls!", url);
		return ServletUrl.notFound;
	}

	/**
	 * <p>
	 * When url consists of only servlet's root url, request.getPathInfo() returns null
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

