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

package org.meerkat.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.meerkat.util.PropertiesLoader;

public class EmbeddedDB implements Runnable{
	private static Logger log = Logger.getLogger(EmbeddedDB.class);
	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private String protocol = "jdbc:derby:";

	private Properties dbProps;
	private String dbName = "";
	Connection connQueries = null;
	Connection connUpdates = null;

	public EmbeddedDB(){
		PropertiesLoader pL = new PropertiesLoader("meerkat.properties");
		Properties appProps = pL.getPropetiesFromFile();

		dbProps = new Properties();
		dbProps.put("user", appProps.get("meerkat.embeddeddb.user"));
		dbProps.put("password", appProps.get("meerkat.embeddeddb.passwd"));
		dbName = (String)appProps.get("meerkat.embeddeddb.dbname");

		this.loadDriver();
	}

	/**
	 * loadDriver
	 */
	public final void loadDriver(){
		try {
			Class.forName(driver).newInstance();
		} catch (ClassNotFoundException cnfe) {
			log.fatal("Unable to load the JDBC driver " + driver);
		} catch (InstantiationException ie) {
			log.fatal("Unable to instantiate the JDBC driver " + driver);
		} catch (IllegalAccessException iae) {
			log.fatal("Not allowed to access the JDBC driver " + driver);
		}
	}

	/**
	 * Create DB if not present
	 */
	public final void initializeDB(){
		Connection c = getConnForUpdates();
		PreparedStatement ps;
		Statement st, st1, st2, st3, st4, st5, st6, st7;
		ResultSet rs;
		try {
			ps = getConnForUpdates().prepareStatement("SELECT COUNT(*) FROM MEERKAT.EVENTS");
			rs = ps.executeQuery();
			rs.next();
		} catch (SQLException e) {
			String message = e.getMessage();
			if(message.contains(" does not exist")){ // Create the table
				try {
					st = c.createStatement();
					st.executeUpdate("CREATE TABLE MEERKAT.EVENTS( "+
							"ID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "+
							"APPNAME VARCHAR(200), "+
							"CRITICAL BOOLEAN NOT NULL, "+
							"DATEEV TIMESTAMP NOT NULL, "+
							"ONLINE BOOLEAN NOT NULL, "+
							"AVAILABILITY DOUBLE, "+
							"LOADTIME DOUBLE, "+
							"LATENCY DOUBLE, "+
							"HTTPSTATUSCODE INT, "+
							"DESCRIPTION VARCHAR(50), "+
							"RESPONSE VARCHAR(30000) "+
							")");

					st1 = c.createStatement();
					st2 = c.createStatement();
					st3 = c.createStatement();
					st4= c.createStatement();
					st5 = c.createStatement();
					st6 = c.createStatement();
					st7 = c.createStatement();

					st1.executeUpdate("CREATE INDEX MEERKAT.IDX_APPNAME ON MEERKAT.EVENTS (APPNAME)");
					st2.executeUpdate("CREATE INDEX MEERKAT.IDX_DATEEV ON MEERKAT.EVENTS (DATEEV)");
					st3.executeUpdate("CREATE INDEX MEERKAT.IDX_ONLINE ON MEERKAT.EVENTS (ONLINE)");
					st4.executeUpdate("CREATE INDEX MEERKAT.IDX_AVAILABILITY ON MEERKAT.EVENTS (AVAILABILITY)");
					st5.executeUpdate("CREATE INDEX MEERKAT.IDX_LATENCY ON MEERKAT.EVENTS (LATENCY)");
					st6.executeUpdate("CREATE INDEX MEERKAT.IDX_HTTPSTATUSCODE ON MEERKAT.EVENTS (HTTPSTATUSCODE)");
					st7.executeUpdate("CREATE INDEX MEERKAT.IDX_DESCRIPTION ON MEERKAT.EVENTS (DESCRIPTION)");

					st.close();
					st1.close();
					st2.close();
					st3.close();
					st4.close();
					st5.close();
					st6.close();
					st7.close();
					c.commit();
					c.close();
				} catch (SQLException e1) {
					log.error("Error creating database!", e1);
					logSQLException(e1);
				}
			}else{
				log.error("Error initializing database!", e);
				logSQLException(e);
			}
		}

	}

	@Override
	public void run() {
	}

	/**
	 * getConn
	 * @return
	 */
	public final Connection getConnForQueries(){
		try {
			connQueries = DriverManager.getConnection(protocol + dbName + ";create=true", dbProps);
		} catch (SQLException e) {
			log.fatal("Failed to create connection to embedded DB! "+e.getMessage());
			logSQLException(e);
		}

		try {
			connQueries.setAutoCommit(true);
		} catch (SQLException e) {
			log.error("Failed to set DB auto-commit to false! "+e.getMessage());
			logSQLException(e);
		}
		return connQueries;

	}

	public final Connection getConnForUpdates(){
		try {
			connUpdates = DriverManager.getConnection(protocol + dbName + ";create=true", dbProps);
		} catch (SQLException e) {
			log.fatal("Failed to create connection to embedded DB! "+e.getMessage());
			logSQLException(e);
		}

		// We want to control transactions manually
		try {
			connUpdates.setAutoCommit(false);
		} catch (SQLException e) {
			log.error("Failed to set DB auto-commit to false! "+e.getMessage());
			logSQLException(e);
		}
		return connUpdates;

	}

	/**
	 * shutdownDB
	 */
	public final void shutdownDB(){
		try{
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		}
		catch (SQLException e){
			if (( (e.getErrorCode() == 50000) && ("XJ015".equals(e.getSQLState()) ))) {
				// we got the expected exception
				log.info("Embedded DB shut down normally");
				// Note that for single database shutdown, the expected
				// SQL state is "08006", and the error code is 45000.
			} else {
				// if the error code or SQLState is different, we have
				// an unexpected exception (shutdown failed)
				System.err.println("Embedded DB did not shut down normally");
				logSQLException(e);
			}
		}
	}

	/**
	 * getMaxIDofApp 
	 * @param appName
	 * @return id
	 */
	public int getMaxIDofApp(String appName){
		PreparedStatement ps;
		ResultSet rs = null;

		int maxId = 0;
		try {
			ps = connQueries.prepareStatement("SELECT MAX(ID) "+ 
					"FROM MEERKAT.EVENTS "+
					"WHERE APPNAME LIKE '"+appName+"' ");

			rs = ps.executeQuery();
			while(rs.next()){
				maxId = rs.getInt(1);
			}
			ps.close();
			rs.close();
		} catch (SQLException e) {
			log.error("Failed query average availability from application "+appName);
			logSQLException(e);
		}

		return maxId;

	}



	/**
	 * logSQLException
	 * @param e SQL Exception
	 */
	public static void logSQLException(SQLException e){
		while (e != null){
			log.error(" ----- SQLException -----");
			log.error(" SQL State:  " + e.getSQLState());
			log.error(" Error Code: " + e.getErrorCode());
			log.error(" Message:    " + e.getMessage());
			e = e.getNextException();
		}
	}

}
