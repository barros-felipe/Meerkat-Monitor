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

package org.meerkat.util;

import java.io.PrintStream;

import org.apache.log4j.Logger;

/**
 * Allows append stderr and stdout to log4j
 * 
 * @author pgnunes
 * 
 */
public class StdOutErrLog4j {

	private static final Logger logger = Logger.getLogger(StdOutErrLog4j.class);

	/**
	 * appendSystemOutAndErrToLog
	 */
	public static void appendSystemOutAndErrToLog() {
		System.setOut(createLoggingProxy(System.out));
		System.setErr(createLoggingProxy(System.err));
	}

	/**
	 * createLoggingProxy
	 * 
	 * @param realPrintStream
	 * @return
	 */
	public static PrintStream createLoggingProxy(final PrintStream realPrintStream) {
		return new PrintStream(realPrintStream) {
			public void print(final String string) {
				realPrintStream.print(string);
				logger.info(string);
			}
		};
	}

}
