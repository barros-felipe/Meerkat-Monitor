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

import org.meerkat.ws.MeerkatWebServiceManager;

public class AddAppsManager {
	private UserInfo userInfo;

	public AddAppsManager(UserInfo userInfo){
		this.userInfo = userInfo;
	}

	/**
	 * addWebApp
	 * @param masterKey
	 * @param name
	 * @param url
	 * @param expectedString
	 * @param executeOnOffline
	 * @return
	 */
	public final String addWebApp(String masterKey, String name, String url, 
			String expectedString, String executeOnOffline, String groups, String active){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		return wsManager.addWeb(masterKey, name.trim(), url.trim(), expectedString.trim(), executeOnOffline.trim(), groups.trim(), active);
	}

	/**
	 * addSSH
	 * @param masterKey
	 * @param name
	 * @param user
	 * @param passwd
	 * @param host
	 * @param port
	 * @param expectedResponse
	 * @param cmdToExecute
	 * @param executeOnOffline
	 * @param groups
	 * @param active
	 * @return
	 */
	public final String addSSH(String masterKey, String name, String user, String passwd, String host, String port, 
			String expectedResponse, String cmdToExecute, String executeOnOffline, String groups, String active){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		return wsManager.addSSH(masterKey, name.trim(), user.trim(), passwd.trim(), host.trim(), port.trim(), expectedResponse.trim(), cmdToExecute.trim(), 
				executeOnOffline.trim(), groups.trim(), active);
	}

	/**
	 * addSocket
	 * @param masterKey
	 * @param name
	 * @param host
	 * @param port
	 * @param sendString
	 * @param expectedString
	 * @param executeOnOffline
	 * @param groups
	 * @param active
	 * @return
	 */
	public final String addSocket(String masterKey, String name, String host, String port,
			String sendString, String expectedString, String executeOnOffline, 
			String groups, String active){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();

		return wsManager.addSocket(masterKey, name.trim(), host.trim(), port.trim(), sendString.trim(), expectedString.trim(), executeOnOffline.trim(),
				groups.trim(), active);
	}

	/**
	 * addDatabase
	 * @param masterKey
	 * @param name
	 * @param host
	 * @param port
	 * @param instanceName
	 * @param username
	 * @param password
	 * @param dbType
	 * @param query
	 * @param expectedResponse
	 * @param executeOnOffline
	 * @param groups
	 * @param active
	 * @return
	 */
	public final String addDatabase(String masterKey, String name, String host,
			String port, String instanceName, String username, String password,
			String dbType, String query, String expectedResponse, String executeOnOffline,
			String groups, String active){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();

		return wsManager.addDB(masterKey, name.trim(), host.trim(), port.trim(), instanceName.trim(), username.trim(), password.trim(), dbType, 
				query.trim(), expectedResponse.trim(), executeOnOffline.trim(), groups.trim(), active);
	}


	/**
	 * addWebService
	 * @param masterKey
	 * @param name
	 * @param url
	 * @param soapAction
	 * @param sendXML
	 * @param responseXML
	 * @param executeOnOffline
	 * @param groups
	 * @param active
	 * @return
	 */
	public final String addWebService(String masterKey, String name, String url,
			String soapAction, String sendXML, String responseXML, String executeOnOffline,
			String groups, String active){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();

		return wsManager.addWebService(masterKey, name.trim(), url.trim(), soapAction.trim(), sendXML.trim(), responseXML.trim(), 
				executeOnOffline.trim(), groups.trim(), active);
	}

}





