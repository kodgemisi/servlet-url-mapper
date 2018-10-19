package com.kodgemisi.servlet_url_mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created on October, 2018
 *
 * @author destan
 */

class FilterTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void getAdminUrl() throws ServletException, IOException {
		final SampleAuthorizationFilter filter = prepareFor("GET", "/admin/dashboard");

		filter.doFilter(request, response, filterChain);

		verify(filter).isCurrentUserAdmin();
		verify(filterChain).doFilter(any(), any());
	}

	@Test
	void postAdminUrl() throws ServletException, IOException {
		final SampleAuthorizationFilter filter = prepareFor("POST", "/admin/dashboard");

		filter.doFilter(request, response, filterChain);

		verify(filter).isCurrentUserAdmin();
		verify(filterChain).doFilter(any(), any());
	}

	@Test
	void getLogoutUrl() throws ServletException, IOException {
		final SampleAuthorizationFilter filter = prepareFor("GET", "/logout");

		filter.doFilter(request, response, filterChain);

		verify(filter).preLogoutWork();
		verify(filterChain).doFilter(any(), any());
	}

	@Test
	void postLogoutUrl() throws ServletException, IOException {
		final SampleAuthorizationFilter filter = prepareFor("POST", "/logout");

		filter.doFilter(request, response, filterChain);

		verify(filter).preLogoutWork();
		verify(filterChain).doFilter(any(), any());
	}

	@Test
	void postLogoutUrlWithTrailingSlash() throws ServletException, IOException {
		final SampleAuthorizationFilter filter = prepareFor("POST", "/logout/");

		filter.doFilter(request, response, filterChain);

		verify(filter).preLogoutWork();
		verify(filterChain).doFilter(any(), any());
	}

	@Test
	void postLogoutUrlWithTrailingSlashWhileUseTrailingSlashMatchIsFalse() throws ServletException, IOException {
		prepareFor("POST", "/logout/");

		final SampleAuthorizationFilter filter = spy(new SampleAuthorizationFilter(false));

		filter.doFilter(request, response, filterChain);

		verify(filter, never()).preLogoutWork();
		verify(filterChain).doFilter(any(), any());
	}

	private SampleAuthorizationFilter prepareFor(String method, String url) throws IOException {
		when(request.getPathInfo()).thenReturn(url);
		when(request.getMethod()).thenReturn(method);

		return spy(new SampleAuthorizationFilter());
	}

}
