package com.kodgemisi.servlet_url_mapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created on October, 2018
 *
 * @author destan
 */
class SampleServlet extends MappingServlet {

	SampleServlet(boolean useTrailingSlashMatch) {
		super(useTrailingSlashMatch);
		this.urlMappingRegistrar
				.get("/products", this::list)
				.get("/products/{id}", this::get)
				.post("/products/", this::create);

	}

	void get(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws IOException {
		final int id = servletUrl.variable("id");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(String.valueOf(id));
	}

	void list(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write("list");
	}

	void create(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws IOException {
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().write("created");
	}
}
