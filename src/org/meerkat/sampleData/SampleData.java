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
import org.meerkat.services.SocketService;
import org.meerkat.services.WebApp;
import org.meerkat.services.WebServiceApp;
import org.meerkat.util.xml.XmlFormatter;

public class SampleData {
	private static NetworkUtil nu = new NetworkUtil();

	/**
	 * getSampleWebApp
	 * @return WebApp
	 */
	public static WebApp getSampleWebApp_SelfTestWSDL(){
		WebApp wa = new WebApp("Meerkat Monitor WSDL", "http://"+nu.getHostname()+":6778/api?wsdl", "MeerkatWebService");
		wa.addGroups("Web");

		return wa;
	}

	/**
	 * getSampleWebService_SelfWSgetVersion
	 * @return WebServiceApp
	 */
	public static WebServiceApp getSampleWebService_SelfWSgetVersion(String version){
		WebServiceApp ws = new WebServiceApp("Meerkat Monitor WebService getVersion", "http://"+nu.getHostname()+":6778/api", "getversion", "");
		XmlFormatter xmlf = new XmlFormatter();
		String postXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.meerkat.org/\">"+
				"<soapenv:Header/>"+
				"<soapenv:Body>"+
				"<ws:getVersion/>"+
				"</soapenv:Body>"+
				"</soapenv:Envelope>";
		ws.setPostXML(xmlf.format(postXML));

		String responseXML = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
				"<soap:Body>"+
				"<ns1:getVersionResponse xmlns:ns1=\"http://ws.meerkat.org/\">"+
				"<return>"+version+"</return>"+
				"</ns1:getVersionResponse>"+
				"</soap:Body>"+
				"</soap:Envelope>";
		ws.setResponseXML(responseXML);

		ws.addGroups("WebServices");

		return ws;
	}

	/**
	 * getSampleSocketService_SelfHTTP_Port
	 * @param port
	 * @return SocketService
	 */
	public static SocketService getSampleSocketService_SelfHTTP_Port(){
		SocketService ss = new SocketService("Meerkat Monitor HTTP Socket", nu.getHostname(), "6777", "", "", "");
		ss.addGroups("Sockets");

		return ss;
	}



}
