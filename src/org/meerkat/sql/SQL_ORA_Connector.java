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

package org.meerkat.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class SQL_ORA_Connector {

	private static Logger log = Logger.getLogger(SQL_ORA_Connector.class);
	private String dbMachine;
	private String port;
	private String sid;
	private String username;
	private String password;

	Connection conn;

	/**
	 * SQL_ORA_Connector
	 * 
	 * @param dbMachine
	 * @param port
	 * @param sid
	 * @param username
	 * @param password
	 */
	public SQL_ORA_Connector(String dbMachine, String port, String sid,
			String username, String password) {
		this.dbMachine = dbMachine;
		this.port = port;
		this.sid = sid;
		this.username = username;
		this.password = password;

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			log.error("Cannot find Oracle JDBC", e);
		}

	}

	/**
	 * executeQuery
	 * 
	 * @param query
	 * @return result
	 */
	public final String executeQuery(String query) {
		String result = "";
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@" + dbMachine
					+ ":" + port + ":" + sid + "", username, password);
		} catch (SQLException e) {
			log.error("SQL Exception getting connection: " + dbMachine + ":"
					+ port + ":" + sid + " "+e.getMessage());
			result += e.getMessage();
			return result;
		}

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			log.error("Cannot create Statement: " + dbMachine + ":" + port
					+ ":" + sid + " "+e.getMessage());
			result += e.getMessage();
			return result;
		}

		try {
			ResultSet rs = stmt.executeQuery(query);
			try {
				rs.next();
				result = String.valueOf(rs.getObject(1));
			} finally {
				try {
					rs.close();
				} catch (SQLException ignore) { /*
												 * Propagate the original
												 * exception instead of this
												 */
				}
			}
		} catch (SQLException e) {
			log.error("Cannot execute query (" + dbMachine + ":" + port + ":"
					+ sid + " "+e.getMessage());
			result += e.getMessage();
			return result;
		} finally {
			try {
				stmt.close();
			} catch (SQLException ignore) { /*
											 * Propagate the original exception
											 * instead of this
											 */
			}
		}

		return result;
	}

}
