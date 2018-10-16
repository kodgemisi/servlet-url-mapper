/* © 2017 Kod Gemisi Ltd.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.kodgemisi.servlet_url_mapping;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Used to represent a request parsing result.<br>
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

	static {
		NOT_FOUND = new ServletUrl(NOT_FOUND_404, "<not applicable>", new Class[0]);
	}

	private final String name;

	private final Pattern pattern;

	private final List<String> variableNames;

	private final List<Class<?>> variableTypes;

	private final boolean hasTrailingSlash;

	private final Map<String, Object> pathVariables;

	private int index = 0;

	/**
	 *
	 * @param name May be null or empty
	 * @param urlPattern
	 * @param types
	 */
	ServletUrl(String name, String urlPattern, Class<?>[] types) {
		this.name = name;
		this.variableTypes = Collections.unmodifiableList(Arrays.asList(types));
		this.pathVariables = Collections.emptyMap();// prevent accidental use

		List<String> names = new ArrayList<String>();
		this.pattern = Pattern.compile(urlPatternToRegex(urlPattern, names, variableTypes));

		this.hasTrailingSlash = urlPattern.length() > 0 && urlPattern.substring(urlPattern.length() - 1, urlPattern.length()).equals("/");
		this.variableNames = Collections.unmodifiableList(names);
	}

	ServletUrl(ServletUrl copy) {
		this.name = copy.name;
		this.variableTypes = copy.variableTypes;
		this.pattern = copy.pattern;
		this.hasTrailingSlash = copy.hasTrailingSlash;
		this.variableNames = copy.variableNames;
		this.pathVariables = new HashMap<String, Object>();
	}

	private static Object getObjectAs(Class<?> clazz, String value) {

		if (String.class.equals(clazz)) {
			return value;
		}
		else if (Number.class.isAssignableFrom(clazz)) {
			return Integer.valueOf(value);
		}
		else if (Boolean.class.equals(clazz)) {
			return Boolean.valueOf(value);
		}

		throw new IllegalArgumentException("Unsupported Type " + clazz.getName());
	}

	/**
	 * <strong>Caveat</strong> <p>Prefer primitive wrapper classes like Integer, Long etc. to avoid {@link NullPointerException} when the value is
	 * null.</p> <p>Note that casting {@code null} to {@code int} throws {@link NullPointerException} however casting {@code null} to {@link Integer}
	 * doesn't!</p>
	 *
	 * @param variable
	 * @param <T>
	 * @return
	 */
	public <T> T variable(String variable) {
		return (T) pathVariables.get(variable);
	}

	void addVariable(String value) {
		//TODO size validation

		Class<?> clazz = index >= variableTypes.size() ? Integer.class : variableTypes.get(index);//default is Integer
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

			Class<?> clazz = names.size() > types.size() ? Integer.class : types.get(names.size() - 1);//default is Integer
			result = result.replace(variable, getRegexGroupByType(clazz));
		}
		return result;
	}

	private String getRegexGroupByType(Class<?> clazz) {
		if (String.class.equals(clazz)) {
			return "(\\w+)";
		}
		if (Number.class.isAssignableFrom(clazz)) {
			return "(\\d+)";
		}
		if (Boolean.class.equals(clazz)) {
			return "(true|false|True|False|TRUE|FALSE)";
		}
		throw new IllegalArgumentException("Unsupported Type " + clazz.getName());
	}

	/**
	 * The method to use to decide the parsed URL. See {@link ServletUrlPatternRegistrar} for usage details.
	 *
	 * @param name
	 * @return true if the given name matches {@code this.name}
	 */
	public boolean is(String name) {
		return this.name.equals(name);
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

	@Override
	public String toString() {
		return "ServletUrl{" + "uniqueName='" + name + '\'' + ", pathVariables=" + pathVariables + '}';
	}

}