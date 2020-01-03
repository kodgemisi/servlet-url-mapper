package com.kodgemisi.servlet_url_mapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles any exception during doXXX methods of {@link MappingServlet}. {@link MappingServlet} uses
 * {@link LoggingExceptionHandler} as default implementation.
 *
 * @see LoggingExceptionHandler
 */
@FunctionalInterface
public interface ExceptionHandler {

    void handleException(HttpServletRequest request, HttpServletResponse response, Exception e);

}
