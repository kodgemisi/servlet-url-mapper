package com.kodgemisi.servlet_url_mapping.dynamic_parsing;

import com.kodgemisi.servlet_url_mapping.ServletUrl;
import com.kodgemisi.servlet_url_mapping.ServletUrlPattern;
import jodd.json.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.DynamicTest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.kodgemisi.servlet_url_mapping.ServletUrl.NOT_FOUND_404;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * This class represents a test json file.
 */
@Getter
@Setter
public class ParsingTestCase {

	private boolean useTrailingSlashMatch;

	/**
	 * This list represents a ServletUrlPattern
	 */
	private List<RegisteredUrl> registeredUrls;

	private List<TestCondition> testConditions;

	/**
	 * The file name of which this class is parsed from;
	 */
	@Setter(AccessLevel.PRIVATE)
	private String fileName;

	public static ParsingTestCase from(Path jsonPath) {
		try {
			final String json = new String(Files.readAllBytes(jsonPath));
			final ParsingTestCase parsingTestCase = new JsonParser().parse(json, ParsingTestCase.class);
			parsingTestCase.setFileName(jsonPath.getFileName().toString());
			return parsingTestCase;
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static DynamicTest toDynamicTest(String fileName, TestCondition testCondition, ServletUrlPattern servletUrlPattern) {

		final String testName = "[" + fileName + "] Expect pattern '" + testCondition.getName() + "' for " + testCondition.getUrl();

		return dynamicTest(testName, () -> {

			// simulate request.getPathInfo()
			final String pathInfo = URI.create(testCondition.getUrl()).getPath();
			final ServletUrl servletUrl = servletUrlPattern.parse(pathInfo);

			assertEquals(testCondition.getName(), servletUrl.getName(), testCondition.message("Url pattern mismatch"));

			if (testCondition.getName().equals(NOT_FOUND_404)) {
				return; // if expected result is 404 there won't be any parsed parameters
			}

			if (testCondition.hasParameters()) {
				for (final Parameter parameter : testCondition.getParameters()) {

					final Object parsedParameter = servletUrl.variable(parameter.getName());
					// Above line restrains us using primitive types in json files.
					// The primitives always will be boxed and always will fail the "class equality check" below.

					assertNotNull(parsedParameter, testCondition.message("Parameter is null"));
					assertEquals(parsedParameter.getClass(), parameter.getType(), testCondition.message("Type mismatch"));
					assertEquals(parsedParameter.toString(), parameter.getValue(), testCondition.message("Value mismatch"));
				}
			}
		});
	}

	public Collection<DynamicTest> toDynamicTests() {

		final ServletUrlPattern servletUrlPattern = new ServletUrlPattern(useTrailingSlashMatch);
		for (RegisteredUrl registeredUrl : registeredUrls) {
			servletUrlPattern.register(registeredUrl.getName(), registeredUrl.getUrl(), registeredUrl.getClasses());
		}

		return testConditions.stream().map(testCondition -> toDynamicTest(fileName, testCondition, servletUrlPattern)).collect(Collectors.toList());
	}
}

