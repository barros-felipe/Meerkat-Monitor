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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.log4j.Logger;
import org.meerkat.db.EmbeddedDB;
import org.meerkat.util.DateUtil;

public class WebAppEvent implements Serializable {

	private static Logger log = Logger.getLogger(WebAppEvent.class);
	private static final long serialVersionUID = 1L;
	private String date;
	private String status; // 0 represents offline, 100 online
	private String availability;
	private String description;
	private String pageLoadTime;
	private String latency;
	private boolean critical;
	private int httpStatusCode;
	private String noValueString = "undefined";
	private String currentResponse;
	private int id;

	/**
	 * WebAppEvent
	 * 
	 * @param description
	 * @param date
	 */
	public WebAppEvent(final boolean critical, final String date,
			final String status, final String availability,
			final int httpStatusCode, final String description) {
		this.setCritical(critical);
		this.date = date;
		this.status = status;
		this.availability = availability;
		this.httpStatusCode = httpStatusCode;
		this.description = description;
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
	 * setID
	 * @param id
	 */
	public final void setID(int id){
		this.id = id;
	}

	/**
	 * getID
	 */
	public final int getID(){
		return id;
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

		return d.getFormatedDateGWT(getDate());
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
	public final void setCurrentResponse(String currentResponse) {
		this.currentResponse = currentResponse;
	}

	/**
	 * getCurrentError
	 * 
	 */
	public final String getCurrentResponse() {
		return currentResponse;
	}

	/**
	 * getEventByID
	 * @param id
	 * @return event
	 */
	public final static WebAppEvent getEventByID(int id){
		EmbeddedDB embDB = new EmbeddedDB();
		Connection conn = embDB.getConn();

		WebAppEvent currEv = null;
		boolean critical;
		String date;
		String status;
		String availability;
		String loadTime;
		String latency;
		int httStatusCode;
		String description;
		String response;

		PreparedStatement ps;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM MEERKAT.EVENTS WHERE ID = "+id);
			rs = ps.executeQuery();

			rs.next();
			critical = rs.getBoolean(3);
			date = rs.getTimestamp(4).toString();
			status = rs.getString(5);
			availability = String.valueOf(rs.getDouble(6));
			loadTime = String.valueOf(rs.getDouble(7));
			latency = String.valueOf(rs.getDouble(8));
			httStatusCode = rs.getInt(9);
			description = rs.getString(10);
			response = rs.getString(11);

			currEv = new WebAppEvent(critical, date, status, availability, httStatusCode, description);
			currEv.setID(rs.getInt(1));
			currEv.setPageLoadTime(loadTime);
			currEv.setLatency(latency);
			currEv.setCurrentResponse(response);
		} catch (SQLException e) {
			log.error("Failed query event id "+id+". (Exists?)");
		}
		return currEv;
	}



}
