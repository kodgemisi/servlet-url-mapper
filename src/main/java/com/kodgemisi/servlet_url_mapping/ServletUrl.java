/* © 2017 Kod Gemisi Ltd.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.kodgemisi.servlet_url_mapping;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * This class represents a request URL parsing result.<br>
 * i.e {@code ServletUrl servletUrl = servletUrlPattern.parse(request)}<br>
 * i.e {@code ServletUrl servletUrl = servletUrlPatternRegistrar.parse(request)}
 * </p>
 * <p>
 * Also used internally to represent registered URL patterns<br>
 * i.e {@code servletUrlPattern.register(...)} this method parses url patterns into {@code ServletUrl}s and stores them in an array internally.
 * </p>
 *
 * @author destan
 * @see ServletUrlPattern#parse(javax.servlet.http.HttpServletRequest)
 * @see ServletUrlPattern#register(String, String, Class[])
 */
public class ServletUrl {

	public static final String NOT_FOUND_404 = "404_NOT_FOUND";

	static final ServletUrl NOT_FOUND;

	private static Pattern pathVariablePattern = Pattern.compile("\\{[A-Za-z_$]\\w*\\}");

	static final Class<?> DEFAULT_PATH_VARIABLE_TYPE = String.class;

	static {
		NOT_FOUND = new ServletUrl(NOT_FOUND_404, "<not applicable>", new Class[0], null);
	}

	private final String name;

	private final Pattern pattern;

	private final List<String> variableNames;

	private final List<Class<?>> variableTypes;

	private final boolean hasTrailingSlash;

	private final Map<String, Object> pathVariables;

	private final ServletRequestHandler requestHandler;

	private int index = 0;

	/**
	 * @param name       May be null or empty
	 * @param urlPattern
	 * @param types
	 */
	ServletUrl(@Nullable String name, @NotNull String urlPattern, @NotNull Class<?>[] types, ServletRequestHandler requestHandler) {
		this.name = name;
		this.variableTypes = Collections.unmodifiableList(Arrays.asList(types));
		this.pathVariables = Collections.emptyMap();// prevent accidental use
		this.requestHandler = requestHandler;

		final List<String> names = new ArrayList<>();
		this.pattern = Pattern.compile(urlPatternToRegex(urlPattern, names, variableTypes));

		this.hasTrailingSlash = urlPattern.endsWith("/");
		this.variableNames = Collections.unmodifiableList(names);

		//TODO check if variableNames & variableTypes sizes are consistent (only if variableTypes is not empty)
	}

	ServletUrl(ServletUrl copy) {
		this.name = copy.name;
		this.variableTypes = copy.variableTypes;
		this.pattern = copy.pattern;
		this.hasTrailingSlash = copy.hasTrailingSlash;
		this.variableNames = copy.variableNames;
		this.pathVariables = new HashMap<>();
		this.requestHandler = copy.requestHandler;
		this.index = copy.index;
	}

	private static Object getObjectAs(Class<?> clazz, String value) {

		if (String.class.equals(clazz)) {
			return value;
		}
		if (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)) {
			return Integer.valueOf(value);
		}
		if (Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz)) {
			return Long.valueOf(value);
		}
		if (BigDecimal.class.isAssignableFrom(clazz)) {
			return new BigDecimal(value);
		}
		if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
			return Boolean.valueOf(value);
		}

		throw new IllegalArgumentException("Unsupported Type " + clazz.getName());
	}

	/**
	 * <strong>Caveat</strong>
	 * <p>Prefer primitive wrapper classes like Integer, Long etc. to avoid {@link NullPointerException} when the value might be null.</p>
	 * <p>Note that casting {@code null} to {@code int} throws {@link NullPointerException} however casting {@code null} to {@link Integer} doesn't!</p>
	 *
	 * @param variable path variable name
	 * @return T
	 */
	public <T> T variable(String variable) {
		return (T) pathVariables.get(variable);//TODO return optional
	}

	void addVariable(String value) {

		// Cannot use simply variableTypes.isEmpty() because user may give only first parameter type out of total two
		// E.g. servletUrlPattern.register("example", "/users/{id}/addresses/{addrId}", Long.class) here the second parameter is String (the default)
		final Class<?> clazz = index >= variableTypes.size() ? DEFAULT_PATH_VARIABLE_TYPE : variableTypes.get(index);
		pathVariables.put(variableNames.get(index++), getObjectAs(clazz, value));
	}

	Pattern getPattern() {
		return pattern;
	}

	boolean hasTrailingSlash() {
		return hasTrailingSlash;
	}

	private String urlPatternToRegex(final String urlPattern, List<String> names, List<Class<?>> types) {
		Matcher matcher = pathVariablePattern.matcher(urlPattern);
		String result = urlPattern.replaceAll("/", "\\\\/");
		while (matcher.find()) {
			String variable = matcher.group();
			names.add(variable.substring(1, variable.length() - 1));// get rid of {}

			final Class<?> clazz = names.size() > types.size() ? DEFAULT_PATH_VARIABLE_TYPE : types.get(names.size() - 1);
			result = result.replace(variable, getRegexGroupByType(clazz));
		}
		return result;
	}

	private String getRegexGroupByType(Class<?> clazz) {

		if (String.class.equals(clazz)) {
			return "([^/]+)";
		}
		if (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz)) {
			return "(\\d+)";
		}
		if (BigDecimal.class.isAssignableFrom(clazz)) {
			return "(\\d+\\.*\\d*)";
		}
		if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
			return "(true|false|True|False|TRUE|FALSE)";
		}

		throw new IllegalArgumentException("Unsupported Type " + clazz.getName());
	}

	/**
	 * The method to use to decide the parsed URL.
	 *
	 * @param name cannot be null.
	 * @return true if the given name matches {@code this.name}
	 */
	public boolean is(String name) {
		return name.equals(this.name);
	}

	/**
	 * The method to use to decide the parsed URL.
	 *
	 * @param name cannot be null.
	 * @return true if the given name doesn't matches {@code this.name}
	 */
	public boolean isNot(String name) {
		return !name.equals(this.name);
	}

	/**
	 * Convenience method. Equivalent of calling {@code servletUrl.is(ServletUrl.NOT_FOUND_404)}
	 *
	 * @return
	 */
	public boolean is404() {
		return this.name.equals(NOT_FOUND_404);
	}

	public String getName() {
		return name;
	}

	public ServletRequestHandler getRequestHandler() {
		return requestHandler;
	}

	@Override
	public int hashCode() {
		int result = pattern.hashCode();
		result = 31 * result + (requestHandler != null ? requestHandler.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ServletUrl that = (ServletUrl) o;

		if (!pattern.equals(that.pattern))
			return false;
		return requestHandler != null ? requestHandler.equals(that.requestHandler) : that.requestHandler == null;
	}

	@Override
	public String toString() {
		return "ServletUrl{" + "name='" + name + '\'' + ", pattern=" + pattern + '}';
	}
}