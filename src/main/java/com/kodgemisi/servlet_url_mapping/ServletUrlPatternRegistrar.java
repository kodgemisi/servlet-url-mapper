package com.kodgemisi.servlet_url_mapping;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Convenience class to ease usage of {@link ServletUrlPattern}.<br>
 * Intended usage is as follows:
 * </p>
 * <p>
 * <ol>
 * <li>
 * Initialize an instance in your servlet's constructor and cofigure your URL patterns.
 * <blockquote><pre>
 * public MyServlet() {
 *     this.patternRegistrar = new ServletUrlPatternRegistrar()
 *     		.get("list", "/");
 *     		.get("show", "/{id}");
 *     		.post("create", "/");
 *     		.post("add address", "/{id}/address");
 *     // and so on...
 * }
 *         </pre></blockquote>
 * </li>
 * <li>
 * Use corresponding {@code ServletUrlPattern} instance in each {@code doXXX} method.
 * <blockquote><pre>
 * 	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
 * 		patternRegistrar.GET.parse(request);
 * 		final ServletUrl servletUrl = patternRegistrar.parse(request);
 * <p>
 * 		switch (servletUrl.getName()) {
 * 		    case "list": {
 * 		        //...
 * 		        break;
 *            }
 * 		    case "show": {
 * 		        //...
 * 		        break;
 *            }
 * 		    case ServletUrl.NOT_FOUND_404: {
 * 		        response.sendError(HttpServletResponse.SC_NOT_FOUND);
 * 		        return;
 *            }
 * 		    default:
 * 		    	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
 * 		    	return;
 *        }
 *    }
 *         </pre></blockquote>
 * </li>
 * </ol>
 *
 * Created on December, 2016
 *
 * @author destan
 */
public class ServletUrlPatternRegistrar {

	private final ServletUrlPattern GET = new ServletUrlPattern();

	private final ServletUrlPattern POST = new ServletUrlPattern();

	private final ServletUrlPattern PUT = new ServletUrlPattern();

	private final ServletUrlPattern DELETE = new ServletUrlPattern();

	private final ServletUrlPattern HEAD = new ServletUrlPattern();

	private final ServletUrlPattern OPTIONS = new ServletUrlPattern();

	public ServletUrlPatternRegistrar get(String name, String urlPattern, Class<?>... type) {
		GET.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar post(String name, String urlPattern, Class<?>... type) {
		POST.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar put(String name, String urlPattern, Class<?>... type) {
		PUT.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar delete(String name, String urlPattern, Class<?>... type) {
		DELETE.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar head(String name, String urlPattern, Class<?>... type) {
		HEAD.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar option(String name, String urlPattern, Class<?>... type) {
		OPTIONS.register(name, urlPattern, type);
		return this;
	}

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
		default:
			throw new IllegalArgumentException(request.getMethod().toLowerCase() + " is not supported.");
		}
	}

}
