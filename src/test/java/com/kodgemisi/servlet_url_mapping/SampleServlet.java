package com.kodgemisi.servlet_url_mapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created on October, 2018
 *
 * @author destan
 */
class SampleServlet extends MappingServlet {

	SampleServlet(boolean useTrailingSlashMatch) {
		this(new LoggingExceptionHandler(), useTrailingSlashMatch);
	}

	SampleServlet(ExceptionHandler exceptionHandler, boolean useTrailingSlashMatch) {
		super(exceptionHandler, useTrailingSlashMatch);
		this.urlMappingRegistrar
				.get("/exception", this::exception)
				.get("/products", this::list)
				.get("/products/{id}", this::get)
				.get("/products/{id}/images/{imgId}", this::images, int.class, long.class)
				.put("/products/{id}/discounts/{isEnabled}", this::toggleDiscount, BigDecimal.class, boolean.class)
				.post("/products/{id}/discounts/{amount}", this::makeDiscount, Long.class, BigDecimal.class)
				.post("/products/", this::create);
	}

	private void exception(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws IOException {
		String some = null;
		some.isEmpty(); // intentional NPE
	}

	private void get(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws IOException {
		final String id = servletUrl.variable("id");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(id);
	}

	private void list(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write("list");
	}

	private void images(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws IOException {
		final int id = servletUrl.variable("id");
		final long imageId = servletUrl.variable("imgId");

		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(id + " " + imageId);
	}

	private void toggleDiscount(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws IOException {
		final BigDecimal id = servletUrl.variable("id");
		final boolean enabled = servletUrl.variable("isEnabled");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(id + " " + enabled);
	}

	private void makeDiscount(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws IOException {
		final Long id = servletUrl.variable("id");
		final BigDecimal amount = servletUrl.variable("amount");

		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().write(id + " " + amount);
	}

	private void create(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws IOException {
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().write("created");
	}
}
