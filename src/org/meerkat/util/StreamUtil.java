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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * 
 * @author pgnunes
 * 
 */
public class StreamUtil {

	private static Logger log = Logger.getLogger(StreamUtil.class);
	private InputStream is;

	/**
	 * StreamUtil
	 * 
	 * @param is
	 */
	public StreamUtil(InputStream is) {
		this.is = is;
	}

	/**
	 * convertStreamToString
	 * 
	 * @return InputStream as String
	 */
	public final String convertStreamToString() {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e1) {
			log.info("IOException ", e1);
		} finally {
			try {
				is.close();
			} catch (IOException e2) {
				log.info("IOException ", e2);
			}
		}
		return sb.toString();
	}

	/**
	 * setInputStream
	 * 
	 * @return InputStream
	 */
	public final InputStream getInputStream() {
		return is;
	}

	/**
	 * setInputStream
	 * 
	 * @param is
	 *            InputStream
	 */
	public final void setInputStream(InputStream is) {
		this.is = is;
	}

}
