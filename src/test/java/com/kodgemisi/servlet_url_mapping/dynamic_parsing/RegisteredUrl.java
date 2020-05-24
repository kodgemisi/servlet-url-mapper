package com.kodgemisi.servlet_url_mapping.dynamic_parsing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class RegisteredUrl {

	private String name;

	private String url;

	private Class<?>[] classes;

	public Class<?>[] getClasses() {
		return classes == null ? new Class[]{} : classes;
	}
}