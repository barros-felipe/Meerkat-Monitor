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

import java.math.BigDecimal;

import org.meerkat.services.WebApp;

public class Availability {

	/**
	 * Availability
	 */
	public Availability() {
		super();
	}

	/**
	 * getAvailability
	 * 
	 * @param webApp
	 * @return availability
	 */
	public final double getAvailability(WebApp webApp) {
		int numberOfTests = 0;
		int numberOfOfflines = 0;
		numberOfTests = webApp.getNumberOfTests();
		numberOfOfflines = webApp.getNumberOfOfflines();

		Double avail = 0.0;
		Double offlines = Double.valueOf(numberOfOfflines);
		Double numTests = Double.valueOf(numberOfTests);
		int decimalPlaces = 2;
		BigDecimal bd;

		if (numberOfTests > 0) {
			avail = 100 - (offlines * 100 / numTests);
			bd = new BigDecimal(avail);
			bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_DOWN);
			avail = bd.doubleValue();
		}
		return avail;
	}

}
