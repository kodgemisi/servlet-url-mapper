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
	 * http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.html#setUseTrailingSlashMatch-boolean-
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
	 * This method is NOT thread-safe. Should only be called from a {@code constructor} or {@link javax.servlet.http.HttpServlet#init()}.
	 *
	 * @param name       The name of your choice for this url pattern. This name will be used to check if the parsed {@link ServletUrl} matches this
	 *                   particular pattern.
	 * @param urlPattern Similar to Spring's or JAX-RS's url patterns but only supports variables through {@literal { }}
	 * @param type       optional type information for path variables i.e {@literal { }}. All Path variables considered as {@code int} by default. You
	 *                   should give types in the same order as you type {@literal { }} expressions in {@code urlPattern}
	 * @return
	 */
	public ServletUrlPattern register(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {

		// be tolerant :)
		if (urlPattern.startsWith("/") == false) {
			urlPattern = '/' + urlPattern;
		}

		urlMappings.put(new ServletUrl(name, urlPattern, type), requestHandler);
		return this;
	}

	public void register(String name, String urlPattern, Class<?>... type) {
		this.register(name, urlPattern, null, type);
	}

	/**
	 * Equivalent of calling {@code parse(request.getPathInfo())}
	 *
	 * @param request
	 * @return
	 *
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
	 *
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

