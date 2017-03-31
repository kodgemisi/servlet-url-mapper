package com.kodgemisi.servlet_url_mapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * Convenience class to ease usage of {@link ServletUrlPattern}.<br>
 * Intended usage is as follows:
 * </p>
 * <blockquote><pre>
 * {@code @WebServlet(urlPatterns = {"/products", "/products/*"})}
 * public class MyServlet extends MappingServlet {
 *   public MyServlet() {
 *       this.patternRegistrar = new ServletUrlPatternRegistrar()
 *
 *              // matches GET request to "host/context-root/products"
 *              .get("list", "/", this::list)
 *
 *              // matches GET request to "host/context-root/products/all"
 *              .get("list", "/all", this::list) // note that the same method can be used for multiple url mappings
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
 * <p>When a request matching your registered HTTP method and url pattern comes your given method will be invoked automatically.</p>
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

	private final ServletUrlPattern TRACE = new ServletUrlPattern();

	public ServletUrlPatternRegistrar get(String name, String urlPattern, Class<?>... type) {
		GET.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar get(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		GET.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar post(String name, String urlPattern, Class<?>... type) {
		POST.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar post(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		POST.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar put(String name, String urlPattern, Class<?>... type) {
		PUT.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar put(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		PUT.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar delete(String name, String urlPattern, Class<?>... type) {
		DELETE.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar delete(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		DELETE.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar head(String name, String urlPattern, Class<?>... type) {
		HEAD.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar head(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		HEAD.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar options(String name, String urlPattern, Class<?>... type) {
		OPTIONS.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar options(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		OPTIONS.register(name, urlPattern, requestHandler, type);
		return this;
	}

	public ServletUrlPatternRegistrar trace(String name, String urlPattern, Class<?>... type) {
		TRACE.register(name, urlPattern, type);
		return this;
	}

	public ServletUrlPatternRegistrar treace(String name, String urlPattern, ServletRequestHandler requestHandler, Class<?>... type) {
		TRACE.register(name, urlPattern, requestHandler, type);
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
		case "trace":
			return TRACE.parse(request);
		default:
			throw new IllegalArgumentException(request.getMethod().toLowerCase() + " is not supported.");
		}
	}

	/**
	 * <p>Normally you are not supposed to used this method.</p>
	 *
	 * <p>If the request matches one of your registered HTTP method and url pattern then corresponding method will be invoked automatically.</p>
	 *
	 * @param request
	 * @param response
	 * @return
	 *
	 * @throws ServletException
	 * @throws IOException
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
