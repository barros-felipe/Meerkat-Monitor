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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class SQL_Statement_Executor {
	private static Logger log = Logger.getLogger(SQL_Statement_Executor.class);
	Connection conn;
	String query;

	public SQL_Statement_Executor(Connection conn, String query){
		this.conn = conn;
		this.query = query;
	}

	/**
	 * getResultQueryString
	 * @return
	 */
	public final String getResultQueryString(){
		PreparedStatement statement = null;
		ResultSet rs = null;
		String result = "";
		try {
			statement = conn.prepareStatement(query);
			statement.setMaxRows(1);
			rs = statement.executeQuery();
		} catch (SQLException e) {
			log.error("Cannot create Statement: "+e.getMessage());
			result += e.getMessage();
		} 

		try {
			while (rs.next()) {
				result = String.valueOf(rs.getObject(1));
			}

			rs.close();
			statement.close();
			conn.close();
		} catch (SQLException e) {
			log.error("Cannot read statement: "+e.getMessage());
			result += e.getMessage();
		}
		
		return result;
	}
}
