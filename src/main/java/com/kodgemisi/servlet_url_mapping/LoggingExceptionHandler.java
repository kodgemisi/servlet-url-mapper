package com.kodgemisi.servlet_url_mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Logs and rethrows any uncaught exception wrapped in a RuntimeException.
 */
public class LoggingExceptionHandler implements ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(LoggingExceptionHandler.class);

    @Override
    public void handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.error(e.getMessage(), e);
        throw new RuntimeException(e);
    }
}
