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

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.meerkat.services.WebApp;
import org.meerkat.util.DateUtil;
import org.meerkat.util.FileUtil;
import org.meerkat.util.XmlFormatter;

public class WebAppEvent implements Serializable {

	private static Logger log = Logger.getLogger(WebAppEvent.class);
	private static final long serialVersionUID = 1L;
	private String date;
	private String status;
	private String availability;
	private String description;
	private String pageLoadTime;
	private String latency;
	private boolean critical;
	private int httpStatusCode;
	private String noValueString = "undefined";
	private UUID id;
	private File tempContentsFile;
	private FileUtil fu;
	private String fileExtension = ".txt";
	private String tempDir;

	/**
	 * WebAppEvent
	 * 
	 * @param description
	 * @param date
	 */
	public WebAppEvent(final boolean critical, final String date,
			final String status, final String availability,
			final int httpStatusCode, final String description,
			final String tempWorkingDir) {
		this.setCritical(critical);
		this.date = date;
		this.status = status;
		this.availability = availability;
		this.httpStatusCode = httpStatusCode;
		this.description = description;
		this.tempDir = tempWorkingDir;
		id = UUID.randomUUID();

	}

	/**
	 * getDescription
	 * 
	 * @return Description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * setDescription
	 * 
	 * @param description
	 */
	public final void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * getDate
	 * 
	 * @return Date
	 */
	public final String getDate() {
		return date;
	}

	/**
	 * getDateFormatedGWT
	 * 
	 * @return DateFormatedGWT
	 */
	public final String getDateFormatedGWT() {
		DateUtil d = new DateUtil();
		return d.getFormatedDateGWT(date);
	}

	/**
	 * setDate
	 * 
	 * @param date
	 */
	public final void setDate(final String date) {
		this.date = date;
	}

	/**
	 * getStatus
	 * 
	 * @return Status
	 */
	public final String getStatus() {
		return status;
	}

	/**
	 * setStatus
	 * 
	 * @param status
	 */
	public final void setStatus(String status) {
		this.status = status;
	}

	/**
	 * getAvailability
	 * 
	 * @return Availability
	 */
	public final String getAvailability() {
		return availability;
	}

	/**
	 * setAvailability
	 * 
	 * @param availability
	 */
	public final void setAvailability(String availability) {
		this.availability = availability;
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
	 * setPageLoadTime
	 * 
	 * @param pageLoadTime
	 */
	public final void setPageLoadTime(String pageLoadTime) {
		this.pageLoadTime = pageLoadTime;
	}

	/**
	 * @param critical
	 *            the critical to set
	 */
	public final void setCritical(boolean critical) {
		this.critical = critical;
	}

	/**
	 * @return the critical
	 */
	public final boolean isCritical() {
		return critical;
	}

	/**
	 * getHttpStatusCode
	 * 
	 * @return HttpStatusCode
	 */
	public final int getHttpStatusCode() {
		return httpStatusCode;
	}

	/**
	 * setHttpStatusCode
	 * 
	 * @param httpStatusCode
	 */
	public final void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	/**
	 * setLatency
	 * 
	 * @param latency
	 */
	public final void setLatency(String latency) {
		this.latency = latency;
	}

	/**
	 * getLatency
	 * 
	 * @return Latency
	 */
	public final String getLatency() {
		NumberFormat nf = new DecimalFormat("#");
		String result;
		if (!latency.equalsIgnoreCase(noValueString)) {
			Double formatedLatency = Double.valueOf(latency);
			result = nf.format(formatedLatency);
		} else {
			result = noValueString;
		}

		return result;
	}

	/**
	 * setCurrentResponse
	 * 
	 * @param setCurrentResponse
	 */
	private final void setCurrentResponse(String currentResponse) {
		String response = currentResponse;
		fu = new FileUtil();
		String path = tempDir + id.toString();
		path = path.replace("\\", "/");
		tempContentsFile = new File(path + fileExtension);
		tempContentsFile.deleteOnExit();

		// Try to format (idented output) if file is XML
		if (currentResponse.contains("<?xml")) {
			XmlFormatter xf = new XmlFormatter();
			response = xf.format(currentResponse);
		}

		fu.writeToFile(tempContentsFile.getAbsolutePath(), response);
	}

	/**
	 * setCurrentResponseGlobal
	 * 
	 * @param setCurrentResponse
	 */
	public final void setCurrentResponseGlobal(String currentResponse,
			WebApp wApp) {
		String response = currentResponse;
		if (currentResponse == null) {
			response = "";
		}

		// Find if the webapp already has an identical response
		// If so use it and avoid creating a new one
		Iterator<WebAppEvent> it = wApp.getEventListIterator();
		WebAppEvent ev;
		boolean equalsExistsAndSet = false;
		while (it.hasNext()) {
			ev = it.next();
			// If exist event with equal response, set the response to that one
			try {
				if (currentResponse.equals(ev.getCurrentResponseString())) {
					String path = tempDir + ev.getWebAppResponseId().toString();
					path = path.replace("\\", "/");
					tempContentsFile = new File(path + fileExtension);
					equalsExistsAndSet = true;
					break;
				}
			} catch (Exception e) {
				log.warn("Failed to search for equal responses. Considered none.");
			}
		}

		if (!equalsExistsAndSet) {
			this.setCurrentResponse(response);
		}

	}

	/**
	 * getCurrentError
	 * 
	 */
	public final String getCurrentResponseFile() {
		return tempContentsFile.getName().toString();
	}

	/**
	 * getCurrentResponseString
	 * 
	 * @return
	 */
	private final String getCurrentResponseString() {
		fu = new FileUtil();
		return fu.readFileContents(tempContentsFile.getAbsolutePath());
	}

	/**
	 * getWebAppResponseId
	 * 
	 * @return
	 */
	public final UUID getWebAppResponseId() {
		return id;
	}

}
