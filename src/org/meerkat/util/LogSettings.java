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

package org.meerkat.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class LogSettings {

	private static Logger log = Logger.getLogger(LogSettings.class);

	public LogSettings(){

	}

	/**
	 * setupLogGeneralOptions
	 */
	public final void setupLogGeneralOptions(){
		// Set up embedded jetty log
		Properties systemProperties = System.getProperties();
		systemProperties.setProperty("org.eclipse.jetty.LEVEL", "WARN");

		// Append stdout and stderr to log4j
		FileOutputStream fileOutputStream = null;
		try {
			File logDir = new File("log");
			logDir.mkdir();
			fileOutputStream = new FileOutputStream("log/meerkat-internal.log");
		} catch (FileNotFoundException e) {
			log.error("Failed to write internal application log file.");
		}
		PrintStream printStream = new PrintStream(fileOutputStream);
		System.setErr(printStream); //to redirect stdout
	}

}
