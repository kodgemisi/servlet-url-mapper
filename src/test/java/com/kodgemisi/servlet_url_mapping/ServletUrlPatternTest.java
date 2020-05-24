package com.kodgemisi.servlet_url_mapping;

import com.kodgemisi.servlet_url_mapping.dynamic_parsing.ParsingTestCase;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created on May, 2020
 *
 * @author destan
 */
class ServletUrlPatternTest {

	@Test
	@DisplayName("register(String name, String urlPattern, Class<?>... type) throws IllegalArgumentException when name parameter is null.")
	void register() {

		final ServletUrlPattern servletUrlPattern = new ServletUrlPattern(false);
		assertThrows(IllegalArgumentException.class, () -> servletUrlPattern.register(null, ""));
	}

	@Test
	@DisplayName("register(String name, String urlPattern, Class<?>... type) throws IllegalArgumentException when name parameter is null.")
	void register1() {

		final ServletRequestHandler servletRequestHandler = (request, response, servletUrl) -> {};
		final ServletUrlPattern servletUrlPattern = new ServletUrlPattern(false);
		servletUrlPattern.register("/users", servletRequestHandler, URL.class);
	}

	@Test
	@DisplayName("Overloaded parse() method calls request.getPathInfo() to extract url path")
	void parse() {

		final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

		final ServletUrlPattern servletUrlPattern = new ServletUrlPattern(false);
		servletUrlPattern.parse(httpServletRequest);

		verify(httpServletRequest, times(1)).getPathInfo();
	}

	/**
	 * Picks all json files with prefix {@code ServletUrlPattern} and suffix {@code .json} and creates {@link DynamicTest} out of them.
	 *
	 * @return Collection of {@link DynamicTest}s
	 */
	@TestFactory
	Collection<DynamicTest> dynamicTestsFromJsonFiles() throws URISyntaxException {

		final URI resourcesUri = ParsingTestCase.class.getClassLoader().getResource("").toURI();
		final File[] jsonTestFiles = Paths.get(resourcesUri).toFile().listFiles((dir, name) -> name.startsWith("ServletUrlPattern") && name.endsWith(".json"));

		return Arrays.stream(jsonTestFiles).map(File::toPath).map(ParsingTestCase::from).map(ParsingTestCase::toDynamicTests).flatMap(Collection::stream).collect(Collectors.toList());
	}

	@Test
	@DisplayName("Null url parameter is replaced with an empty string")
	void arrangeUrlForTrailingSlash0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		final ServletUrlPattern servletUrlPattern = new ServletUrlPattern(false);
		final Method arrangeUrlForTrailingSlash = ServletUrlPattern.class.getDeclaredMethod("arrangeUrlForTrailingSlash", String.class, boolean.class);
		arrangeUrlForTrailingSlash.setAccessible(true);

		final Object resultingUrl = arrangeUrlForTrailingSlash.invoke(servletUrlPattern, null, false);
		Assertions.assertEquals("", resultingUrl);

		final Object resultingUrl2 = arrangeUrlForTrailingSlash.invoke(servletUrlPattern, null, true);
		Assertions.assertEquals("", resultingUrl2);
	}

	@Test
	@DisplayName("useTrailingSlashMatch true, patternHasTrailingSlash true")
	void arrangeUrlForTrailingSlash1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		final ServletUrlPattern servletUrlPattern = new ServletUrlPattern(true);
		final Method arrangeUrlForTrailingSlash = ServletUrlPattern.class.getDeclaredMethod("arrangeUrlForTrailingSlash", String.class, boolean.class);
		arrangeUrlForTrailingSlash.setAccessible(true);

		final Object resultingUrl = arrangeUrlForTrailingSlash.invoke(servletUrlPattern, "/users", true);
		Assertions.assertEquals("/users/", resultingUrl);

		final Object resultingUrl2 = arrangeUrlForTrailingSlash.invoke(servletUrlPattern, "/users/", true);
		Assertions.assertEquals("/users/", resultingUrl2);
	}

	@Test
	@DisplayName("useTrailingSlashMatch true, patternHasTrailingSlash false")
	void arrangeUrlForTrailingSlash2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		final ServletUrlPattern servletUrlPattern = new ServletUrlPattern(true);
		final Method arrangeUrlForTrailingSlash = ServletUrlPattern.class.getDeclaredMethod("arrangeUrlForTrailingSlash", String.class, boolean.class);
		arrangeUrlForTrailingSlash.setAccessible(true);

		final Object resultingUrl = arrangeUrlForTrailingSlash.invoke(servletUrlPattern, "/users", false);
		Assertions.assertEquals("/users", resultingUrl);

		final Object resultingUrl2 = arrangeUrlForTrailingSlash.invoke(servletUrlPattern, "/users/", false);
		Assertions.assertEquals("/users", resultingUrl2);
	}

	@Test
	@DisplayName("useTrailingSlashMatch false, patternHasTrailingSlash true")
	void arrangeUrlForTrailingSlash3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		final ServletUrlPattern servletUrlPattern = new ServletUrlPattern(false);
		final Method arrangeUrlForTrailingSlash = ServletUrlPattern.class.getDeclaredMethod("arrangeUrlForTrailingSlash", String.class, boolean.class);
		arrangeUrlForTrailingSlash.setAccessible(true);

		final Object resultingUrl = arrangeUrlForTrailingSlash.invoke(servletUrlPattern, "/users", true);
		Assertions.assertEquals("/users", resultingUrl);

		final Object resultingUrl2 = arrangeUrlForTrailingSlash.invoke(servletUrlPattern, "/users/", true);
		Assertions.assertEquals("/users/", resultingUrl2);
	}

	@Test
	@DisplayName("useTrailingSlashMatch false, patternHasTrailingSlash false")
	void arrangeUrlForTrailingSlash4() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		final ServletUrlPattern servletUrlPattern = new ServletUrlPattern(false);
		final Method arrangeUrlForTrailingSlash = ServletUrlPattern.class.getDeclaredMethod("arrangeUrlForTrailingSlash", String.class, boolean.class);
		arrangeUrlForTrailingSlash.setAccessible(true);

		final Object resultingUrl = arrangeUrlForTrailingSlash.invoke(servletUrlPattern, "/users", false);
		Assertions.assertEquals("/users", resultingUrl);

		final Object resultingUrl2 = arrangeUrlForTrailingSlash.invoke(servletUrlPattern, "/users/", false);
		Assertions.assertEquals("/users/", resultingUrl2);
	}
}