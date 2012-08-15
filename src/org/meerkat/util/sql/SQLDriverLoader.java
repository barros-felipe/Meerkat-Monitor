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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class SQLDriverLoader {
	private static Logger log = Logger.getLogger(SQLDriverLoader.class);

	private String ORA_JAR_DIR = "lib/jdbc-driver/oracle/";
	private String ORA_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
	
	private String MSSQL_JAR_DIR = "lib/jdbc-driver/mssql/";
	private String MSSQL_CLASS_NAME = "net.sourceforge.jtds.jdbc.Driver";
	
	private String MYSQL_JAR_DIR = "lib/jdbc-driver/mysql/";
	private String MYSQL_CLASS_NAME = "com.mysql.jdbc.Driver";
	
	
	public SQLDriverLoader(){

	}

	/**
	 * loadDrivers
	 */
	public final void loadDrivers(){
		this.loadDriver(ORA_JAR_DIR, "Oracle", ORA_CLASS_NAME);
		this.loadDriver(MSSQL_JAR_DIR, "MSSQL", MSSQL_CLASS_NAME);
		this.loadDriver(MYSQL_JAR_DIR, "MySQL", MYSQL_CLASS_NAME);
	
	}
	
	/**
	 * loadDriver
	 * @param jarDir
	 * @param dbDescription
	 * @param driverClass
	 */
	public final void loadDriver(String jarDir, String dbDescription, String driverClass){
		// Get the jar name inside the dir
		String odir = jarDir;
		File ora_dir = new File(odir);
		String[] ext = new String[] { "jar" };
		List<File> files = (List<File>) FileUtils.listFiles(ora_dir, ext, true);

		if(files.size() == 0){
			log.debug(dbDescription+" JDBC driver jar not available.");
		}else{
			try{
				File fdriver = files.get(0);
				URL u = new URL("jar:file:"+odir+fdriver.getName()+"!/");
				String classname = driverClass;
				URLClassLoader ucl = new URLClassLoader(new URL[] { u });
				Driver d = (Driver)Class.forName(classname, true, ucl).newInstance();
				DriverManager.registerDriver(new SQLDriverJar(d));
			}catch(Exception e){
				log.error(dbDescription+" JDBC driver jar not available.");
			}
		}
	}


}
