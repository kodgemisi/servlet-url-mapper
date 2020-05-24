/* © 2017 Kod Gemisi Ltd.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.kodgemisi.servlet_url_mapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>Servlets extending {@link MappingServlet} should provide methods matching {@link ServletRequestHandler#handleRequest} signature so that those methods
 * can be used as request handlers.</p> <p>See {@link ServletUrlPattern} for examples.</p>
 *
 * @author destan
 */
@FunctionalInterface
public interface ServletRequestHandler {

	/**
	 * @param request
	 * @param response
	 * @param servletUrl
	 * @throws ServletException It may occur during the usage of {@code request.getServletContext().getRequestDispatcher(url).forward(req, resp)}
	 * @throws IOException      It may occur during the usage of {@code response#sendError}.
	 */
	void handleRequest(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws ServletException, IOException;

}
