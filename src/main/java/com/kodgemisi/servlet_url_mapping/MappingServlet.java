package com.kodgemisi.servlet_url_mapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Every servlet which needs servlet mapping should extend from this servlet.
 *
 * @author destan
 */
public class MappingServlet extends HttpServlet {

	/**
	 * Every servlet needs a copy of {@link ServletUrlPatternRegistrar}. Servlets extending this servlet should use this field to register url
	 * mappings.
	 */
	protected final ServletUrlPatternRegistrar patternRegistrar;

	public MappingServlet() {
		this.patternRegistrar = new ServletUrlPatternRegistrar();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletUrl servletUrl = patternRegistrar.handle(request, response);

		if (servletUrl.is404()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletUrl servletUrl = patternRegistrar.handle(request, response);

		if (servletUrl.is404()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletUrl servletUrl = patternRegistrar.handle(request, response);

		if (servletUrl.is404()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletUrl servletUrl = patternRegistrar.handle(request, response);

		if (servletUrl.is404()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletUrl servletUrl = patternRegistrar.handle(request, response);

		if (servletUrl.is404()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletUrl servletUrl = patternRegistrar.handle(request, response);

		if (servletUrl.is404()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}

	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletUrl servletUrl = patternRegistrar.handle(request, response);

		if (servletUrl.is404()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.service(request, response);
	}
}
