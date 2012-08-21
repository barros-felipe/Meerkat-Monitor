/**
 * Meerkat Monitor - Network Monitor Tool
 * Copyright (C) 2011 Merkat-Monitor
 * mailto: contact AT meerkat-monitor DOT org
 * 
 * Meerkat Monitor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Meerkat Monitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with Meerkat Monitor.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.meerkat.httpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.ByteArrayISO8859Writer;
import org.eclipse.jetty.util.IO;
import org.meerkat.webapp.WebAppEvent;

public class CustomResourceHandler extends ResourceHandler {
	private static Logger log = Logger.getLogger(CustomResourceHandler.class);
	final long _faviconModified = (System.currentTimeMillis() / 1000) * 1000;
	byte[] _favicon;
	boolean _serveIcon = true;
	private String eventRequestID = "/event-id-";
	private String notFound = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n"
			+ "<html>\n"
			+ "<head>\n"
			+ " <meta content=\"text/html; charset=ISO-8859-1\"\n"
			+ " http-equiv=\"content-type\">\n"
			+ "  <style type=\"text/css\">\n"
			+ "   body {text-align: center;}"
			+ "   div.content {margin-left: auto; margin-right: auto; text-align: center; \n"
			+ "        background-color: #ffa200; width: 410px; height: 300} </style>\n"
			+ "  <title>404 Page Not Found</title>\n"
			+ "</head>\n"
			+ "<body>\n"
			+ "<h1>Whoops 404.&nbsp;Page not found.</h1>\n"
			+ "<div class=\"content\">\n"
			+ "<br>\n"
			+ "<div><big>"
			+ "<span style=\"color: rgb(0, 0, 102);\">"
			+ "And you've found a dead Meerkat!</span></big><br>\n"
			+ "</div>\n"
			+ "<br>\n"
			+ "<img style=\"width: 400px; height: 258px;\"\n"
			+ "alt=\"Meerkat in the sun\" src=\"resources/404_meerkat.png\"\n"
			+ " hspace=\"5\"><br>\n"
			+ "<br>\n"
			+ "<div><big><span style=\"color: rgb(0, 0, 102);\">\n"
			+ "(Not really. Just bathing in the sun...)</span></big><br>\n"
			+ "</div>\n"
			+ "<br>\n"
			+ "</div>\n"
			+ "</body>\n"
			+ "</html>\n";
	
	

	public CustomResourceHandler() {
		// Set the favicon
		try {
			URL fav = this.getClass().getClassLoader()
					.getResource("resources/favicon.ico");
			if (fav != null) {
				_favicon = IO.readBytes(fav.openStream());
			}
		} catch (Exception e) {
			log.warn("Unable to set favicon", e);
		}
	}

	@Override
	public final void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (response.isCommitted() || baseRequest.isHandled()) {
			return;
		} else {
			// little cheat for common request - favicon hack
			if (request.getRequestURI().equals("/favicon.ico")) {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("image/x-icon");
				response.setContentLength(_favicon.length);
				response.getOutputStream().write(_favicon);
				return;
				
			}else if(request.getRequestURI().contains(eventRequestID)){ // This is a request for event
				// Get request id
				String requestRef = request.getRequestURI();
				requestRef = requestRef.substring(eventRequestID.length(), requestRef.length());
				int id = Integer.valueOf(requestRef);
				WebAppEvent ev = WebAppEvent.getEventByID(id);
				
				ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(1500);
				response.setContentType(MimeTypes.TEXT_HTML);
				if(ev != null){
					response.setStatus(HttpServletResponse.SC_OK);
					writer.write(ev.getCurrentResponse());
				}else{
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					writer.write(notFound);
				}
				writer.flush();
				response.setContentLength(writer.size());
				OutputStream out = response.getOutputStream();
				writer.writeTo(out);
				out.close();
				writer.close();
				return;
			}

			// Otherwise give a not found 404
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setContentType(MimeTypes.TEXT_HTML);
			ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(1500);
			writer.write(notFound);
			writer.flush();
			response.setContentLength(writer.size());
			OutputStream out = response.getOutputStream();
			writer.writeTo(out);
			out.close();
			writer.close();
		}
	}

}
