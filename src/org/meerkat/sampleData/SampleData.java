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

package org.meerkat.sampleData;

import org.meerkat.network.NetworkUtil;
import org.meerkat.services.WebApp;

public class SampleData {
	private static NetworkUtil nu = new NetworkUtil();
	
	/**
	 * getSampleWebApp
	 * @return
	 */
	public static WebApp getSampleWebApp(){
		WebApp wa = new WebApp("[DEMO] Meerkat Self Test WSDL", "http://"+nu.getHostname()+":6778/ws/manager?wsdl", "MeerkatWebService");
		wa.addGroups("Demo Group");
		
		return wa;
	}
	
	
	
}
