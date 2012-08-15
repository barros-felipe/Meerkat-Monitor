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
package org.meerkat.util.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

public class SQLDriverJar implements Driver  {
	private Driver driver;
	
	SQLDriverJar(Driver d) {
		this.driver = d;
	}
	
	/**
	 * acceptsURL
	 */
	public boolean acceptsURL(String u) throws SQLException {
		return this.driver.acceptsURL(u);
	}
	
	/**
	 * connect
	 */
	public Connection connect(String u, Properties p) throws SQLException {
		return this.driver.connect(u, p);
	}
	
	/**
	 * getMajorVersion
	 */
	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}
	
	/**
	 * getMinorVersion
	 */
	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}
	
	/**
	 * getPropertyInfo
	 */
	public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
		return this.driver.getPropertyInfo(u, p);
	}
	
	/**
	 * jdbcCompliant
	 */
	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}

}
