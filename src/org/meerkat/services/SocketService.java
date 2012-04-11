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

package org.meerkat.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.meerkat.util.Counter;
import org.meerkat.webapp.WebAppResponse;

public class SocketService extends WebApp {

	private static final long serialVersionUID = -7175499037523917260L;
	private static Logger log = Logger.getLogger(SocketService.class);

	private String server;
	private int port;
	private String dataToSend;

	/**
	 * SocketService
	 * 
	 * @param name
	 * @param server
	 * @param port
	 * @param sendString
	 * @param expectedString
	 * @param executeOnOffline
	 */
	public SocketService(String name, String server, String port,
			String sendString, String expectedString, String executeOnOffline) {

		super(name, server, expectedString, executeOnOffline);
		this.server = server;
		this.port = Integer.valueOf(port);
		this.dataToSend = sendString;

		this.setTypeSocketService();
	}

	/**
	 * SocketService
	 */
	public SocketService() {
		super();
		this.setTypeSocketService();
	}

	@Override
	public final WebAppResponse checkWebAppStatus() {
		WebAppResponse response = new WebAppResponse();
		response.setResponseSocketService();
		Socket socket;

		// Measure the response time
		Counter c = new Counter();
		c.startCounter();

		// Create socket and attempt to connect
		try {
			socket = new Socket(server, port);
			response.setPortListening(true);

		} catch (UnknownHostException e) {
			log.error("Cannot connect to " + server, e);
			response.setHttpTextResponse(e.toString());
			response.setPageLoadTime("N/A");
			setCurrentResponse(e.toString());

			c.stopCounter();
			response.setPageLoadTime(c.getDurationSeconds());
			return response;
		} catch (IOException e) {
			log.error("IOException connecting to " + server, e);
			response.setHttpTextResponse(e.toString());
			response.setPageLoadTime("N/A");
			setCurrentResponse(e.toString());

			c.stopCounter();
			response.setPageLoadTime(c.getDurationSeconds());
			return response;
		}

		// Write data to the socket
		String encoding = "8859_1";
		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), encoding));
			out = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), encoding));
		} catch (UnsupportedEncodingException e) {
			log.error("UnsupportedEncodingException (" + encoding
					+ ") creating IO stream from " + server, e);

		} catch (IOException e) {
			log.error("IOException creating IO stream from " + server, e);
			response.setHttpTextResponse(e.toString());
			setCurrentResponse(e.toString());

			c.stopCounter();
			response.setPageLoadTime(c.getDurationSeconds());
			return response;
		}

		try {
			out.write(dataToSend);
			log.debug("Socket sent: " + dataToSend);
			out.flush();
			socket.shutdownOutput();
		} catch (IOException e) {
			log.error("IOException writing data to " + server, e);
			response.setHttpTextResponse(e.toString());
			setCurrentResponse(e.toString());

			c.stopCounter();
			response.setPageLoadTime(c.getDurationSeconds());
			return response;
		}

		String line;
		String returnedResponse = "";
		try {
			while ((line = in.readLine()) != null) {
				returnedResponse += line + "\n";
			}
		} catch (IOException e) {
			log.error("IOException reading data from " + server, e);
		}

		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			log.error("IOException closing socket (IO streams) to " + server, e);
		}
		log.debug("Socket receive: " + returnedResponse + "\n");
		response.setHttpTextResponse(returnedResponse);
		setCurrentResponse(returnedResponse);

		// Stop the counter
		c.stopCounter();
		response.setPageLoadTime(c.getDurationSeconds());

		// Check if the response is expected
		if (getCurrentResponse().contains(this.getExpectedString())) {
			response.setContainsSocketServiceExpectedResponse(true);
		}

		return response;

	}

	/**
	 * setDataToSend to socket
	 * 
	 * @param dataToSend
	 */
	public final void setDataToSend(String dataToSend) {
		this.dataToSend = dataToSend;
	}

	/**
	 * getServer
	 * 
	 * @return
	 */
	public final String getServer() {
		return server;
	}

	/**
	 * setServer
	 * 
	 * @param srv
	 */
	public final void setServer(String srv) {
		server = srv;
	}

	/**
	 * getPort
	 * 
	 * @return
	 */
	public final String getPort() {
		return String.valueOf(port);
	}

	/**
	 * setPort
	 * 
	 * @param p
	 */
	public final void setPort(String p) {
		port = Integer.valueOf(p);
	}

	/**
	 * getDataToSend
	 * 
	 * @return
	 */
	public final String getDataToSend() {
		return dataToSend;
	}

}
