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
package org.meerkat.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

//Service Endpoint Interface
@WebService(endpointInterface = "org.meerkat.ws.MeerkatWebServiceManager", serviceName = "MeerkatMonitorWS", portName = "MeerkatMonitorPort")
@SOAPBinding(style = Style.RPC, use = Use.LITERAL)

public interface MeerkatWebServiceManager{

	@WebMethod(operationName = "getVersion", action = "getversion")
	String getVersion();

	@WebMethod(operationName = "changeMasterKey", action = "changeMasterKey")
	String changeMasterKey(
			@WebParam(name="currentMasterKey") String currentMasterKey, 
			@WebParam(name="newMasterKey") String newMasterKey);

	@WebMethod(operationName = "removeAppByName", action = "removeAppByName")
	String removeAppByName(
			@WebParam(name="masterKey") String masterKey, 
			@WebParam(name="name") String name);

	@WebMethod(operationName = "addSSH", action = "addSSH")
	String addSSH(
			@WebParam(name="masterKey") String masterKey, 
			@WebParam(name="name") String name, 
			@WebParam(name="user") String user, 
			@WebParam(name="passwd") String passwd, 
			@WebParam(name="host") String host, 
			@WebParam(name="port") String port, 
			@WebParam(name="expectedResponse") String expectedResponse, 
			@WebParam(name="cmdToExecute") String cmdToExecute);

	@WebMethod(operationName = "addSocket", action = "addSocket")
	String addSocket (
			@WebParam(name="masterKey") String masterKey, 
			@WebParam(name="name") String name, 
			@WebParam(name="host") String host, 
			@WebParam(name="port") String port,
			@WebParam(name="sendString") String sendString, 
			@WebParam(name="expectedString") String expectedString, 
			@WebParam(name="executeOnOffline") String executeOnOffline);

	@WebMethod(operationName = "addDB", action = "addDB")
	String addDB(
			@WebParam(name="masterKey") String masterKey, 
			@WebParam(name="name") String name,
			@WebParam(name="host") String host,
			@WebParam(name="port") String port,
			@WebParam(name="instanceName") String instanceName, 
			@WebParam(name="username") String username,
			@WebParam(name="password") String password,
			@WebParam(name="dbType") String dbType,
			@WebParam(name="query") String query, 
			@WebParam(name="expectedResponse") String expectedResponse,
			@WebParam(name="executeOnOffline") String executeOnOffline);

	@WebMethod(operationName = "addWeb", action = "addWeb")
	String addWeb(
			@WebParam(name="masterKey") String masterKey, 
			@WebParam(name="name") String name, 
			@WebParam(name="url") String url, 
			@WebParam(name="expectedString") String expectedString,
			@WebParam(name="executeOnOffline") String executeOnOffline);

	@WebMethod(operationName = "addWebService", action = "addWebService")
	String addWebService(
			@WebParam(name="masterKey") String masterKey, 
			@WebParam(name="name") String name,
			@WebParam(name="url") String url,
			@WebParam(name="soapAction") String soapAction,
			@WebParam(name="sendXML") String sendXML,
			@WebParam(name="responseXML") String responseXML,
			@WebParam(name="executeOnOffline") String executeOnOffline);

	@WebMethod(operationName = "resetAllData", action = "resetAllData")
	String resetAllData(
			@WebParam(name="masterKey") String masterKey);
	
	@WebMethod(operationName = "resetAllAppDataFromName", action = "resetAllAppDataFromName")
	String resetAllAppDataFromName(
			@WebParam(name="masterKey") String masterKey,
			@WebParam(name="appName") String name);
	
	@WebMethod(operationName = "shutdown", action = "shutdown")
	String shutdown(
			@WebParam(name="masterKey") String masterKey);
	
}

