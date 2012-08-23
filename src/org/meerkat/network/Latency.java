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

package org.meerkat.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.meerkat.util.Counter;

public class Latency {

	private static Logger log = Logger.getLogger(Latency.class);

	private String host;
	private boolean status;
	private int timeOut = 5000; // 3 seconds at least
	private Double latency;
	private Double latAverage;
	private Counter c;
	private String noValueString = null;

	/**
	 * Latency
	 */
	public Latency() {

	}

	/**
	 * Latency
	 * 
	 * @param host
	 */
	public Latency(String host) {
		this.host = host;
		this.status = false;
	}

	/**
	 * getLatency Tries 3 requests and return latency average or N/A if host not
	 * available
	 * 
	 * @return latency
	 */
	public final String getLatency() {
		Double duration;
		int numberOfRequests = 3;
		String result = "";
		int i;
		latency = 0.0;

		for (i = 0; i < numberOfRequests; i++) {
			c = new Counter();
			c.startCounter();
			try {
				status = InetAddress.getByName(host).isReachable(timeOut);
			} catch (UnknownHostException e) {
				log.error("Cannot resolve host: " + host + ". Latency unavailable - "+ e.getMessage());
				break;
			} catch (IOException e) {
				log.error("Cannot check host: " + host+ ". Latency unavailable - "+ e.getMessage());
				break;
			}
			c.stopCounter();

			if (status) {
				duration = Double.valueOf(c.getDurationMSeconds());
				latency += duration;
			} else {
				break;
			}
		}

		// If the 3 checks went ok, then calculate the average
		if (i == numberOfRequests && status) {
			// Calculate average
			latAverage = latency / numberOfRequests;
			result = String.valueOf(latAverage);
		} else {
			// Keep compatibility with GWT
			result = noValueString;
		}
		return result;
	}

}
