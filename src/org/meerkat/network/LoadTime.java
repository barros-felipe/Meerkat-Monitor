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
import java.util.Iterator;

import org.meerkat.services.WebApp;
import org.meerkat.webapp.WebAppEvent;

public class LoadTime {

	public LoadTime() {

	}

	/**
	 * getPageLoadsAverage
	 * 
	 * @return PageLoadsAverage
	 */
	public final String getLoadsAverage(WebApp webApp) {

		double pageLoadSum = 0.0;
		double average = 0.0;
		int decimalPlaces = 2;

		Iterator<WebAppEvent> it = webApp.getEventListIterator();
		WebAppEvent ev;
		while (it.hasNext()) {
			ev = it.next();
			pageLoadSum += Double.valueOf(ev.getPageLoadTime());
		}

		average = Double.valueOf(pageLoadSum / webApp.getNumberOfEvents());
		BigDecimal bd = new BigDecimal(average);
		bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_DOWN);
		average = bd.doubleValue();
		
		return String.valueOf(average);
	}
	
	

}
