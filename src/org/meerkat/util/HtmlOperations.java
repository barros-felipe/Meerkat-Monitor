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

public class HtmlOperations {

	/**
	 * addPrettifier
	 * @param originalCode
	 * @return
	 */
	public static final String addPrettifier(String originalCode){
		String css = "<link href=\"resources/prettify.css\" type=\"text/css\" rel=\"stylesheet\" />\n";
		String js = "<script type=\"text/javascript\" src=\"resources/prettify.js\"></script>\n";

		String header = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
				"<html>\n"+
				"<head>\n"+
				"<title></title>\n"+
				css+js+
				"</head>\n"+
				"<body onload=\"prettyPrint()\" bgcolor=\"white\">\n"+
				"<pre class=\"prettyprint\">\n";

		String footer = "</pre>\n"+
				"</body>\n"+
				"</html>";

		return header+"\n\n"+originalCode+"\n\n"+footer;
	}





}
