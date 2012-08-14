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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class SQL_MySQL_Connector {

	private static Logger log = Logger.getLogger(SQL_MySQL_Connector.class);
	private String dbMachine;
	private String port;
	private String db;
	private String username;
	private String password;

	Connection conn;

	/**
	 * SQL_MySQL_Connector
	 * 
	 * @param dbMachine
	 * @param port
	 * @param db
	 * @param username
	 * @param password
	 */
	public SQL_MySQL_Connector(String dbMachine, String port, String db,
			String username, String password) {
		this.dbMachine = dbMachine;
		this.port = port;
		this.db = db;
		this.username = username;
		this.password = password;

		// Get the jar name inside the dir
		String ddir = "lib/jdbc-driver/mysql/";
		File sqld_dir = new File(ddir);
		String[] ext = new String[] { "jar" };
		List<File> files = (List<File>) FileUtils.listFiles(sqld_dir, ext, true);

		if(files.size() == 0){
			log.error("MySQL JDBC driver jar not available.");
		}else{
			try{
				File fdriver = files.get(0);
				URL u = new URL("jar:file:"+ddir+fdriver.getName()+"!/");
				String classname = "com.mysql.jdbc.Driver";
				URLClassLoader ucl = new URLClassLoader(new URL[] { u });
				Driver d = (Driver)Class.forName(classname, true, ucl).newInstance();
				DriverManager.registerDriver(new DriverJarLoader(d));
			}catch(Exception e){
				log.error("MySQL JDBC driver jar not available.");
			}
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
			conn = DriverManager.getConnection("jdbc:mysql://" + dbMachine
					+ ":" + port + "/" + db + "", username, password);
		} catch (SQLException e) {
			log.error("SQL Exception getting connection: " + dbMachine + ":"
					+ port + "/" + db + ". "+e.getMessage());
			result += e.getMessage();
			return result;
		}

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			log.error("Cannot create Statement: " + dbMachine + ":" + port
					+ "/" + db + ". "+e.getMessage());
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
			log.error("Cannot execute query (" + dbMachine + ":" + port + "/"
					+ db + ") "+e.getMessage());
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
