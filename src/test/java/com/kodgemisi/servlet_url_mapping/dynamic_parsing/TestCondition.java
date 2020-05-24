package com.kodgemisi.servlet_url_mapping.dynamic_parsing;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a given url and expected values (results)
 */
@Getter
@Setter
class TestCondition {

	private String name;

	private String url;

	private List<Parameter> parameters;

	boolean hasParameters() {
		return parameters != null && !parameters.isEmpty();
	}

	@Override
	public String toString() {
		return "Expected '" + name + "' for url '" + url + "' with parameters '" + parameters + "'.";
	}

	String message(String message) {
		return message + " : " + this;
	}
}