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

import org.apache.log4j.Logger;
import org.meerkat.sql.SQL_MSSQL_Connector;
import org.meerkat.sql.SQL_MySQL_Connector;
import org.meerkat.sql.SQL_ORA_Connector;
import org.meerkat.util.Counter;
import org.meerkat.util.MasterKeyManager;
import org.meerkat.webapp.WebAppResponse;

public class SQLService extends WebApp {

	private static Logger log = Logger.getLogger(SQLService.class);
	public static String TYPE_MYSQL = "MYSQL";
	public static String TYPE_ORA = "ORA";
	public static String TYPE_MSSQL = "MSSQL";
	private String dbMachine;
	private String port;
	private String dbName;
	private String username;
	private String password;
	private String query;
	private String dbType = "";

	/**
	 * ORA SQLService
	 * 
	 * @param name
	 * @param query
	 * @param expectedResponse
	 * @param dbMachine
	 * @param port
	 * @param SID
	 * @param username
	 * @param password
	 */
	public SQLService(String name, String query, String expectedResponse,
			String dbMachine, String port, String dbName, String username,
			String password) {
		super(name, dbMachine, expectedResponse);
		this.dbMachine = dbMachine;
		this.query = query;
		this.port = port;
		this.dbName = dbName;
		this.username = username;
		setPassword(password);
		this.setTypeSQL();
	}

	/**
	 * MySQL SQLService
	 * 
	 * @param name
	 * @param query
	 * @param expectedResponse
	 * @param dbMachine
	 * @param port
	 * @param username
	 * @param password
	 */
	public SQLService(MasterKeyManager mkm, String name, String query, String expectedResponse,
			String dbMachine, String port, String username, String password) {
		super(name, dbMachine, expectedResponse);
		this.mkm = mkm;
		this.dbMachine = dbMachine;
		this.query = query;
		this.port = port;
		this.username = username;
		setPassword(password);
		this.setTypeSQL();
	}

	/**
	 * WebService
	 */
	public SQLService() {
		super();
		this.setTypeSQL();
	}
	
	/**
	 * checkWebAppStatus
	 */
	public final WebAppResponse checkWebAppStatus() {
		if(mkm == null){
			mkm = this.getMasterKeyManager();
		}

		// Set the response at this point to empty in case of no response at all
		setCurrentResponse("");

		Object connector = null;

		if (this.getDBType().equals(TYPE_ORA)) {
			// Create an instance of SQL_ORA_Connector
			connector = new SQL_ORA_Connector(dbMachine, port, dbName, username, mkm.getDecryptedPassword(password));
		} else if (this.getDBType().equals(TYPE_MYSQL)) {
			// Create an instance of SQL_MySQL_Connector
			connector = new SQL_MySQL_Connector(dbMachine, port, dbName, username, mkm.getDecryptedPassword(password));
		} else if (this.getDBType().equals(TYPE_MSSQL)) {
			// Create an instance of SQL_MySQL_Connector
			connector = new SQL_MSSQL_Connector(dbMachine, port, dbName, username, mkm.getDecryptedPassword(password));
		}

		WebAppResponse response = new WebAppResponse();
		response.setResponseSQL();

		// Measure the response time
		Counter c = new Counter();
		c.startCounter();

		// Set the http status
		response.setHttpStatus(0);

		// Get the response
		try {
			if (this.getDBType().equals(TYPE_ORA)) {
				setCurrentResponse(((SQL_ORA_Connector) connector).executeQuery(this.getQuery()));
			} else if (this.getDBType().equals(TYPE_MYSQL)) {
				setCurrentResponse(((SQL_MySQL_Connector) connector).executeQuery(this.getQuery()));
			} else if (this.getDBType().equals(TYPE_MSSQL)) {
				setCurrentResponse(((SQL_MSSQL_Connector) connector).executeQuery(this.getQuery()));
			}

		} catch (Exception e) {
			log.error("Cannot execute query! " + this.getName(), e);
		}

		// Check if the response contains the expectedString
		if (getCurrentResponse() != null
				&& getCurrentResponse().contains(this.getExpectedString())) {
			response.setContainsSQLServiceExpectedResponse(true);
		}

		if (getCurrentResponse() == null) {
			log.warn("Received null response from SQL query: " + this.getName());
			setCurrentResponse("null");
		}

		// Check for unavailable driver situation
		if(this.getCurrentResponse().contains("Exception") || 
				this.getCurrentResponse().contains("No suitable driver found")){
			response.setContainsSQLServiceExpectedResponse(false);
		}

		// Stop the counter
		c.stopCounter();
		response.setPageLoadTime(c.getDurationSeconds());

		return response;
	}

	/**
	 * getExpectedString
	 */
	/**
	 * @Override public final String getExpectedString() { String configFile =
	 *           this.getConfigXMLFile();
	 * 
	 *           XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	 *           FileInputStream in = null; try { in = new
	 *           FileInputStream(configFile); } catch (FileNotFoundException e)
	 *           { log.error("Cannot open XML config file!", e); }
	 *           XMLEventReader eventReader = null; try { eventReader =
	 *           inputFactory.createXMLEventReader(in); } catch
	 *           (XMLStreamException e) { log.error("Cannot open XML Stream!",
	 *           e); } boolean current = false; String expectedString = "";
	 * 
	 *           while (eventReader.hasNext()) { XMLEvent event = null; try {
	 *           event = eventReader.nextEvent(); } catch (XMLStreamException e)
	 *           { log.error("Cannot open XML Stream!", e); }
	 * 
	 *           if (event.isStartElement()) { if
	 *           (event.asStartElement().getName
	 *           ().getLocalPart().equals("name")) { try { event =
	 *           eventReader.nextEvent(); } catch (XMLStreamException e) {
	 *           log.error("Cannot open XML Stream!", e); } String name =
	 *           event.asCharacters().getData();
	 * 
	 *           // Check if is this SQL Service by name
	 *           if(name.equals(this.getName())){ current = true; } continue; }
	 * 
	 *           if (event.asStartElement().getName().getLocalPart().equals(
	 *           "expectedResponse")) { try { event = eventReader.nextEvent(); }
	 *           catch (XMLStreamException e) {
	 *           log.error("Cannot open XML Stream!", e); }
	 * 
	 *           // Get the expected response if(current){ expectedString =
	 *           event.asCharacters().getData(); return expectedString; }
	 *           continue; } } }
	 * 
	 *           return expectedString; }
	 */

	/**
	 * getQuery
	 */
	public final String getQuery() {
		/**
		 * String configFile = this.getConfigXMLFile();
		 * 
		 * XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		 * FileInputStream in = null; try { in = new
		 * FileInputStream(configFile); } catch (FileNotFoundException e) {
		 * log.error("Cannot open XML config file!", e); } XMLEventReader
		 * eventReader = null; try { eventReader =
		 * inputFactory.createXMLEventReader(in); } catch (XMLStreamException e)
		 * { log.error("Cannot open XML Stream!", e); } boolean current = false;
		 * String currQuery = "";
		 * 
		 * while (eventReader.hasNext()) { XMLEvent event = null; try { event =
		 * eventReader.nextEvent(); } catch (XMLStreamException e) {
		 * log.error("Cannot open XML Stream!", e); }
		 * 
		 * if (event.isStartElement()) { if
		 * (event.asStartElement().getName().getLocalPart().equals("name")) {
		 * try { event = eventReader.nextEvent(); } catch (XMLStreamException e)
		 * { log.error("Cannot open XML Stream!", e); } String name =
		 * event.asCharacters().getData();
		 * 
		 * // Check if is this SQL Service by name
		 * if(name.equals(this.getName())){ current = true; } continue; }
		 * 
		 * if (event.asStartElement().getName().getLocalPart().equals("query"))
		 * { try { event = eventReader.nextEvent(); } catch (XMLStreamException
		 * e) { log.error("Cannot open XML Stream!", e); }
		 * 
		 * // Get the expected response if(current){ currQuery =
		 * event.asCharacters().getData(); return currQuery; } continue; } } }
		 * 
		 * return currQuery;
		 */
		return query;
	}

	/**
	 * setDBTypeORA
	 */
	public final void setDBTypeORA() {
		this.dbType = TYPE_ORA;
	}

	/**
	 * setDBTypeMySQL
	 */
	public final void setDBTypeMySQL() {
		this.dbType = TYPE_MYSQL;
	}

	/**
	 * setDBTypeMSSQL
	 */
	public final void setDBTypeMSSQL() {
		this.dbType = TYPE_MSSQL;
	}

	/**
	 * getDBType
	 * 
	 * @return
	 */
	public final String getDBType() {
		return this.dbType;
	}

	/**
	 * getHost
	 * 
	 * @return
	 */
	public final String getHost() {
		return dbMachine;
	}

	/**
	 * setHost
	 * 
	 * @param host
	 */
	public final void setHost(String host) {
		dbMachine = host;
	}

	/**
	 * getPort
	 * 
	 * @return
	 */
	public final String getPort() {
		return port;
	}

	/**
	 * setPort
	 * 
	 * @param dbport
	 */
	public final void setPort(String dbport) {
		port = dbport;
	}

	/**
	 * getDB
	 * 
	 * @return
	 */
	public final String getDB() {
		return dbName;
	}

	/**
	 * setDB
	 * 
	 * @param dbs
	 */
	public final void setDB(String dbs) {
		dbName = dbs;
	}

	/**
	 * getUsername
	 * 
	 * @return
	 */
	public final String getUsername() {
		return username;
	}

	/**
	 * setUsername
	 * 
	 * @param user
	 */
	public final void setUsername(String user) {
		username = user;
	}

	/**
	 * getPassword
	 * @return Encrypted password
	 */
	public final String getPassword() {
		return password;
	}

	/**
	 * setPassword
	 * 
	 * @param passwd
	 */
	public final void setPassword(String passwd) {
		this.password = mkm.getEncryptedPassword(passwd);
	}

	/**
	 * setQuery
	 * 
	 * @param q
	 */
	public final void setQuery(String q) {
		this.query = q;
	}

}
