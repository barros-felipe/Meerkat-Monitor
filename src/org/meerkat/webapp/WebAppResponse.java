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

package org.meerkat.webapp;

public class WebAppResponse {

	private int httpStatus;
	private String httpTextResponse;
	private String emptyString = "";
	private boolean isWebAppOnline = false;
	private boolean containsWebAppExpectedString = false;
	private boolean containsWebServiceExpectedResponse = false;
	private boolean containsSQLExpectedResponse = false;
	private boolean containsSocketserviceExpectedResponse = false;
	private boolean containsSSHExpectedResponse = false;
	private boolean isPortListening = false;

	private String pageLoadTime;
	private int httpOkStatus = 200;
	private String appType = ""; // webapp, webservice, sql, service

	/**
	 * WebAppResponse
	 * 
	 * @param httpStatus
	 *            HTTP Code status
	 * @param httpTextResponse
	 *            HTTP body response
	 */
	public WebAppResponse() {
		super();
		httpStatus = 0;
		httpTextResponse = emptyString;
	}

	/**
	 * WebAppResponse
	 * 
	 * @param httpStatus
	 *            HTTP Code status
	 * @param httpTextResponse
	 *            HTTP body response
	 */
	public WebAppResponse(int httpStatus, String httpTextResponse) {
		super();
		this.httpStatus = httpStatus;
		this.httpTextResponse = httpTextResponse;
	}

	/**
	 * getContainsWebAppExpectedString
	 * 
	 * @return true if contains webapp expected string
	 */
	public final boolean getContainsWebAppExpectedString() {
		return containsWebAppExpectedString;
	}

	/**
	 * getContainsWebServiceExpectedResponse
	 * 
	 * @return
	 */
	public final boolean getContainsWebServiceExpectedResponse() {
		return containsWebServiceExpectedResponse;
	}

	/**
	 * getContainsWebServiceExpectedResponse
	 * 
	 * @return
	 */
	public final boolean getContainsSQLExpectedResponse() {
		return containsSQLExpectedResponse;
	}

	/**
	 * getHttpStatus
	 * 
	 * @return HTTP Status
	 */
	public final int getHttpStatus() {
		return httpStatus;
	}

	/**
	 * getHttpTextResponse
	 * 
	 * @return HTTP body response
	 */
	public final String getHttpTextResponse() {
		return httpTextResponse;
	}

	/**
	 * isOnline
	 * 
	 * @return true if online false otherwise
	 */
	public final boolean isOnline() {
		// Check WebApp
		if (appType.equalsIgnoreCase("webapp") && isWebAppOnline
				&& containsWebAppExpectedString) {
			return true;
		}

		// Check WebService
		if (appType.equalsIgnoreCase("webservice") && isWebAppOnline
				&& containsWebServiceExpectedResponse) {
			return true;
		}

		// Check SQL
		if (appType.equals("sql") && containsSQLExpectedResponse) {
			return true;
		}

		// Check RAW Service
		if (appType.equals("socketservice")
				&& containsSocketserviceExpectedResponse && isPortListening()) {
			return true;
		}

		// Check SSH
		if (appType.equals("ssh") && containsSSHExpectedResponse) {
			return true;
		}

		return false;
	}

	/**
	 * setContainsWebAppExpectedString
	 * 
	 * @param containsWebAppExpectedString
	 */
	public final void setContainsWebAppExpectedString(
			boolean containsWebAppExpectedString) {
		this.containsWebAppExpectedString = containsWebAppExpectedString;
	}

	/**
	 * setContainsWebServiceExpectedResponse
	 * 
	 * @param containsWebServiceExpectedResponse
	 */
	public final void setContainsWebServiceExpectedResponse(
			boolean containsWebServiceExpectedResponse) {
		this.containsWebServiceExpectedResponse = containsWebServiceExpectedResponse;
	}

	/**
	 * setContainsSQLServiceExpectedResponse
	 * 
	 * @param containsSQLExpectedResponse
	 */
	public final void setContainsSQLServiceExpectedResponse(
			boolean containsSQLExpectedResponse) {
		this.containsSQLExpectedResponse = containsSQLExpectedResponse;
	}

	/**
	 * containsSocketServiceExpectedResponse
	 * 
	 * @param containsSocketServiceExpectedResponse
	 */
	public final void setContainsSocketServiceExpectedResponse(
			boolean containsSocketServiceExpectedResponse) {
		this.containsSocketserviceExpectedResponse = containsSocketServiceExpectedResponse;
	}

	/**
	 * setContainsSSHExpectedResponse
	 * 
	 * @param setContainsSSHExpectedResponse
	 */
	public final void setContainsSSHExpectedResponse(
			boolean containsSSHExpectedResponse) {
		this.containsSSHExpectedResponse = containsSSHExpectedResponse;
	}

	/**
	 * setHttpStatus
	 * 
	 * @param httpStatus
	 *            HTTP Status
	 */
	public final void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;

		// Set the online status
		if (httpStatus == httpOkStatus) {
			isWebAppOnline = true;
		}

		// If SQL Service ignore (doesn't apply)
		if (this.appType.equals("sql")) {
			isWebAppOnline = true;
		}

		// If SocketService ignore (doesn't apply)
		if (this.appType.equals("socketservice")) {
			isWebAppOnline = true;
		}

		// If SSH ignore (doesn't apply)
		if (this.appType.equals("ssh")) {
			isWebAppOnline = true;
		}

	}

	/**
	 * setHttpTextResponse
	 * 
	 * @param httpTextResponse
	 *            body response
	 */
	public final void setHttpTextResponse(String httpTextResponse) {
		this.httpTextResponse = httpTextResponse;
	}

	/**
	 * getPageLoadTime
	 * 
	 * @return PageLoadTime
	 */
	public final String getPageLoadTime() {
		return pageLoadTime;
	}

	/**
	 * pageLoadTime
	 * 
	 * @param pageLoadTime
	 */
	public final void setPageLoadTime(String pageLoadTime) {
		this.pageLoadTime = pageLoadTime;
	}

	/**
	 * setAppTypeResponse
	 * 
	 * @param type
	 */
	public final void setResponseAppType() {
		appType = "webapp";
	}

	/**
	 * setAppTypeWebService
	 * 
	 * @param type
	 */
	public final void setResponseWebService() {
		appType = "webservice";
	}

	/**
	 * setAppTypeSQL
	 * 
	 * @param type
	 */
	public final void setResponseSQL() {
		appType = "sql";
	}

	/**
	 * setResponseSSH
	 */
	public final void setResponseSSH() {
		appType = "ssh";
	}

	/**
	 * setResponseSocketService
	 */
	public final void setResponseSocketService() {
		appType = "socketservice";
	}

	/**
	 * isPortListening
	 * 
	 * @return
	 */
	public final boolean isPortListening() {
		return isPortListening;
	}

	/**
	 * setPortListening
	 * 
	 * @param isPortListening
	 */
	public final void setPortListening(boolean isPortListening) {
		this.isPortListening = isPortListening;
	}
}
