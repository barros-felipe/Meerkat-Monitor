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

package org.meerkat.httpServer;

public class HTMLComponents {

	private final String footer;

	/**
	 * HTMLComponents
	 * 
	 * @param appVersion
	 */
	public HTMLComponents(String appVersion) {
		footer = "<div class=\"spacer\">"
				+ "</div>\n<div id=\"ftlinks\">"
				+ "- <a href=\"http://meerkat-monitor.org/\" style=\"text-decoration:none\" target=\"_blank\">Meerkat Monitor v"
				+ appVersion
				+ " <img src=\"resources/tango-slink.png\" alt=\"link\" border=\"0\" width=\"10\" height=\"10\"/></a>"
				+ " - <a href=\"http://www.gnu.org/licenses/gpl.html\" style=\"text-decoration:none\" target=\"_blank\">Open Source GPL <img src=\"resources/tango-slink.png\" alt=\"link\" border=\"0\" width=\"10\" height=\"10\"/></a>"
				+ " - <a href=\"http://meerkat-monitor.org/documentation/\" style=\"text-decoration:none\" target=\"_blank\">Documentation <img src=\"resources/tango-slink.png\" alt=\"link\" border=\"0\" width=\"10\" height=\"10\"/></a>"
				+ " - </div>";
	}

	/**
	 * getFooter
	 * 
	 * @return
	 */
	public final String getFooter() {
		return footer;
	}

}
