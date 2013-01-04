/**
 * Meerkat Monitor - Network Monitor Tool
 * Copyright (C) 2012 Merkat-Monitor
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.meerkat.db.EmbeddedDB;
import org.meerkat.services.WebApp;

public class WebAppEventListIterator implements Iterator<WebAppEvent>{

	private static Logger log = Logger.getLogger(WebAppEventListIterator.class);
	private int currId, lastId;
	private WebApp webApp;
	private Connection conn;
	private EmbeddedDB embDB;

	public WebAppEventListIterator(WebApp webApp){
		currId = 0;
		lastId = 0;
		this.webApp = webApp;

		// Get the first element ID
		if(conn == null){
			embDB = new EmbeddedDB();
			conn = embDB.getConnForQueries();
		}

		PreparedStatement ps, ps1;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			ps = conn.prepareStatement("SELECT ID FROM MEERKAT.EVENTS WHERE APPNAME LIKE '"+this.webApp.getName()+"' " +
					"ORDER BY ID ASC FETCH FIRST 1 ROWS ONLY");
			rs = ps.executeQuery();

			while(rs.next()) {
				currId = rs.getInt(1);

			}
			
			ps1 = conn.prepareStatement("SELECT ID FROM MEERKAT.EVENTS WHERE APPNAME LIKE '"+this.webApp.getName()+"' " +
					"ORDER BY ID DESC FETCH FIRST 1 ROWS ONLY");
			rs1 = ps1.executeQuery();

			while(rs1.next()) {
				lastId = rs1.getInt(1);

			}
			
			rs.close();
			ps.close();
			rs1.close();
			ps1.close();


		} catch (SQLException e) {
			log.error("Failed get first event id of application "+webApp.getName());
			log.error("", e);
		}

	}

	@Override
	public boolean hasNext() {
		if(currId < lastId){
			return true;
		}
		return false;
	}

	@Override
	public WebAppEvent next() {
		WebAppEvent currEv = null;
		boolean critical;
		String date;
		boolean online;
		String availability;
		String loadTime;
		String latency;
		int httStatusCode;
		String description;
		String response;

		PreparedStatement ps;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM MEERKAT.EVENTS WHERE ID = "+currId);
			rs = ps.executeQuery();

			while(rs.next()) {
				critical = rs.getBoolean(3);
				date = rs.getTimestamp(4).toString();
				online = rs.getBoolean(5);
				availability = String.valueOf(rs.getDouble(6));
				loadTime = String.valueOf(rs.getDouble(7));
				latency = String.valueOf(rs.getDouble(8));
				httStatusCode = rs.getInt(9);
				description = rs.getString(10);
				response = rs.getString(11);

				currEv = new WebAppEvent(critical, date, online, availability, httStatusCode, description);
				currEv.setID(rs.getInt(1));
				currEv.setPageLoadTime(loadTime);
				currEv.setLatency(latency);
				currEv.setCurrentResponse(response);
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			log.error("Failed query events from application "+webApp.getName());
			log.error("", e);
		}

		// Set the next Event ID
		PreparedStatement ps1;
		ResultSet rs1 = null;
		try {
			ps1 = conn.prepareStatement("SELECT ID FROM MEERKAT.EVENTS WHERE ID > "+currId+" AND APPNAME LIKE '"+this.webApp.getName()+"' FETCH FIRST 1 ROWS ONLY");
			rs1 = ps1.executeQuery();

			while(rs1.next()) {
				currId = rs1.getInt(1);
			}
			rs1.close();
			ps1.close();
		} catch (SQLException e) {
			log.error("Failed query events from application "+webApp.getName());
			log.error("", e);
		}

		return currEv;
	}


	@Override
	public void remove() {
		// Do nothing
	}

}
