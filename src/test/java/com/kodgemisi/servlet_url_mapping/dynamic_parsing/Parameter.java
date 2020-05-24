package com.kodgemisi.servlet_url_mapping.dynamic_parsing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Parameter {

	private String name;

	private String value;

	private Class<?> type; //TODO should add default type?

	@Override
	public String toString() {
		return "{" + "name='" + name + '\'' + ", value='" + value + '\'' + ", type=" + type + '}';
	}
}