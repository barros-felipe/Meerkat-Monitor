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

import java.io.Serializable;
import java.math.BigDecimal;

public class Counter implements Serializable {

	private static final long serialVersionUID = 3274944762080008166L;
	private long startTime;
	private long endTime;

	/**
	 * Counter
	 */
	public Counter() {
		super();
	}

	/**
	 * startCounter
	 */
	public final void startCounter() {
		startTime = System.nanoTime();
	}

	/**
	 * stopCounter
	 */
	public final void stopCounter() {
		endTime = System.nanoTime();
	}

	/**
	 * getstartTime
	 * 
	 * @return startTime
	 */
	public final long getstartTime() {
		return startTime;
	}

	/**
	 * getstopTime
	 * 
	 * @return stopTime
	 */
	public final long getstopTime() {
		return endTime;
	}

	/**
	 * getDuration
	 * 
	 * @return duration
	 */
	public final String getDuration() {
		String result;
		// Duration in ns
		long secondsTime = (endTime - startTime);
		if (secondsTime / 1000000000 == 0) {
			// duration in ms
			result = (endTime - startTime) / 1000000 + "ms";
		} else {
			// duration in s
			result = secondsTime / 1000000000 + "s";
		}
		return result;
	}

	/**
	 * getDurationSeconds
	 * 
	 * @return DurationSeconds
	 */
	public final String getDurationSeconds() {
		int decimalPlaces = 4;
		BigDecimal bd;

		Double durationSeconds = 0.0;
		String duration = this.getDuration();
		int endIndex = duration.getBytes().length;

		if (duration.contains("ms")) {
			Double durationMS = Double.valueOf(duration.substring(0,
					endIndex - 2));
			durationSeconds = (durationMS * 0.001);
			bd = new BigDecimal(durationSeconds);
			bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_UP);
			durationSeconds = bd.doubleValue();
		} else {
			Double durationS = Double.valueOf(duration.substring(0,
					endIndex - 1));
			bd = new BigDecimal(durationS);
			bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_UP);
			durationSeconds = bd.doubleValue();
		}
		return String.valueOf(durationSeconds);
	}

	/**
	 * getDurationMSeconds
	 * 
	 * @return DurationMSeconds
	 */
	public final double getDurationMSeconds() {
		int decimalPlaces = 4;
		BigDecimal bd = null;

		bd = new BigDecimal((endTime - startTime) / 1000000);
		bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_DOWN);

		return bd.doubleValue();
	}

}
