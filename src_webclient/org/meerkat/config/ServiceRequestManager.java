/**
 * Meerkat Monitor - Network Monitor Tool
 * Copyright (C) 2013 Merkat-Monitor
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

package org.meerkat.config;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.log4j.Logger;
import org.meerkat.ws.MeerkatWebServiceManager;

public class ServiceRequestManager {

	private static Logger log = Logger.getLogger(ServiceRequestManager.class);
	private String serviceURIwsdl = "http://ws.meerkat.org/";
	private String serviceName = "MeerkatWebServiceService";
	private URL url = null;
	private QName qname = null;
	private MeerkatWebServiceManager mmwsm = null;

	public ServiceRequestManager(String wsdlURL){
		try {
			//url = new URL("http://localhost:6778/api?wsdl");
			url = new URL(wsdlURL);
		} catch (MalformedURLException e) {
			log.error("Error creating URL. "+e.getMessage());
		}

		qname = new QName(serviceURIwsdl, serviceName);
		Service service = Service.create(url, qname);
		mmwsm = service.getPort(MeerkatWebServiceManager.class);
	}

	/**
	 * getWebServiceManager
	 * @return
	 */
	public MeerkatWebServiceManager getWebServiceManager(){
		return mmwsm;
	}

}
