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

package org.meerkat.webapp;

import org.apache.log4j.Logger;
import org.meerkat.services.WebApp;

public class WebAppActionResultThread extends Thread {

	private static Logger log = Logger
			.getLogger(WebAppActionResultThread.class);

	/**
	 * WebAppActionThread
	 * 
	 * @param webApp
	 */
	public WebAppActionResultThread() {

	}

	/**
	 * run
	 * 
	 * @param wT
	 * @param wA
	 */
	public final void run(WebAppActionThread wT, WebApp wA) {
		while (wT.isAlive()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error("InterruptedException in thread.", e);
			}
		}
		wA.setActionExecOutput(wT.getCommandExecOutput());
	}
}