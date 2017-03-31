package com.kodgemisi.servlet_url_mapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>Servlets extending {@link MappingServlet} provide methods matching {@link ServletRequestHandler#handleRequest} signature so that those methods
 * can be used as request handlers.</p> <p>See {@link ServletUrlPatternRegistrar} for examples.</p>
 *
 * @author destan
 */
@FunctionalInterface
public interface ServletRequestHandler {

	void handleRequest(HttpServletRequest req, HttpServletResponse resp, ServletUrl servletUrl) throws ServletException, IOException;

}
