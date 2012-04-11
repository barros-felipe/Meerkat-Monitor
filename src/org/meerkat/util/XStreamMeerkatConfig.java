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

import org.meerkat.services.SQLService;
import org.meerkat.services.SecureShellSSH;
import org.meerkat.services.SocketService;
import org.meerkat.services.WebApp;
import org.meerkat.services.WebService;
import org.meerkat.webapp.WebAppCollection;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XStreamMeerkatConfig {
	XStream xstream;

	public XStreamMeerkatConfig() {
		xstream = new XStream(new DomDriver("ISO-8859-1"));
		xstream.autodetectAnnotations(true);
		xstream.alias("webapp", WebApp.class);
		xstream.alias("socketservice", SocketService.class);
		xstream.alias("ssh", SecureShellSSH.class);
		xstream.alias("database", SQLService.class);
		xstream.alias("webservice", WebService.class);

		xstream.alias("meerkat-monitor", WebAppCollection.class);
		xstream.addImplicitCollection(WebAppCollection.class,
				"webAppsCollection");
	}

	/**
	 * getXstream
	 * 
	 * @return Configurated XStream
	 */
	public final XStream getXstream() {
		return xstream;
	}

}
