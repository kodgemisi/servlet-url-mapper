package com.kodgemisi.servlet_url_mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 *
 * We don't verify calls of {@code handleRequest} methods because Mockito can't verify them as they are called via method
 * references. Instead we verify the operations in {@code handleRequest} methods.
 *
 * Created on October, 2018
 *
 * @author destan
 */

class SampleServletTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void exceptionThrowing() {

		final SampleServlet sampleServlet = new SampleServlet(true);
		Assertions.assertThrows(RuntimeException.class, () -> sampleServlet.doGet(request, response));
	}

	@Test
	@DisplayName("Ensure custom exception handler is used")
	void exceptionHandler() throws IOException, ServletException {

		final StringWriter sw = prepareFor("GET", "/exception");

		final ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);
		final SampleServlet sampleServlet = new SampleServlet(exceptionHandler, true);

		sampleServlet.doGet(request, response);
		verify(exceptionHandler, times(1)).handleException(any(), any(), any());
	}

	@Test
	void getMappingSimple() throws ServletException, IOException {
		final StringWriter sw = prepareFor("GET", "/products");

		final SampleServlet sampleServlet = new SampleServlet(true);
		sampleServlet.doGet(request, response);

		String result = sw.getBuffer().toString().trim();
		assertEquals( "list", result);

		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	@Test
	void getMappingWithPathVariable() throws ServletException, IOException {
		final StringWriter sw = prepareFor("GET", "/products/13");

		final SampleServlet sampleServlet = new SampleServlet(true);
		sampleServlet.doGet(request, response);

		String result = sw.getBuffer().toString().trim();
		assertEquals( "13", result);

		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	@Test
	void imagesWithTraillingSlash() throws ServletException, IOException {
		final StringWriter sw = prepareFor("GET", "/products/13/images/35/");

		final SampleServlet sampleServlet = new SampleServlet(true);
		sampleServlet.doPost(request, response);

		String result = sw.getBuffer().toString().trim();
		assertEquals( "13 35", result);

		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	@Test
	void toggleDiscountWithTraillingSlash() throws ServletException, IOException {
		final StringWriter sw = prepareFor("PUT", "/products/13/discounts/true/");

		final SampleServlet sampleServlet = new SampleServlet(true);
		sampleServlet.doPost(request, response);

		String result = sw.getBuffer().toString().trim();
		assertEquals( "13 true", result);

		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	@Test
	void makeDiscountWithTraillingSlash() throws ServletException, IOException {
		final StringWriter sw = prepareFor("POST", "/products/13/discounts/35.5/");

		final SampleServlet sampleServlet = new SampleServlet(true);
		sampleServlet.doPost(request, response);

		String result = sw.getBuffer().toString().trim();
		assertEquals( "13 35.5", result);

		verify(response).setStatus(HttpServletResponse.SC_CREATED);
	}

	@Test
	void postWithTraillingSlash() throws ServletException, IOException {
		final StringWriter sw = prepareFor("POST", "/products");

		final SampleServlet sampleServlet = new SampleServlet(true);
		sampleServlet.doPost(request, response);

		String result = sw.getBuffer().toString().trim();
		assertEquals( "created", result);

		verify(response).setStatus(HttpServletResponse.SC_CREATED);
	}

	@Test
	void postWithoutTraillingSlash() throws ServletException, IOException {
		prepareFor("POST", "/products");

		final SampleServlet sampleServlet = new SampleServlet(false);
		sampleServlet.doPost(request, response);

		verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private StringWriter prepareFor(String method, String url) throws IOException {
		when(request.getPathInfo()).thenReturn(url);
		when(request.getMethod()).thenReturn(method);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		when(response.getWriter()).thenReturn(pw);

		return sw;
	}

}
