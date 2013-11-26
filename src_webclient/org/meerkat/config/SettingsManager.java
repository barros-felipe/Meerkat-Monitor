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

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.meerkat.util.PropertiesLoader;
import org.meerkat.ws.MeerkatWebServiceManager;

public class SettingsManager {

	private static Logger log = Logger.getLogger(SettingsManager.class);
	private UserInfo userInfo;
	private Properties prop;
	private ServiceRequestManager serviceReq;
	private String errorString = "";

	public SettingsManager(UserInfo userInfo){
		this.userInfo = userInfo;
		getPropertiesFromServer();
	}

	/**
	 * getPropertiesFromServer
	 * @return
	 */
	private final boolean getPropertiesFromServer(){
		serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		byte[] base64Properties = wsManager.getProperties(userInfo.getKey());

		// convert the string to properties
		String pr = "";
		try {
			pr = new String(base64Properties, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Failed to get properties from server: "+e.getMessage());
		}

		prop = PropertiesLoader.getStringContentAsProperties(pr);
		String valiPropString = PropertiesLoader.validateStringProperties(pr);

		if(valiPropString.length() > 0){ // Properties are not valid
			return false;
		}

		return false;

	}

	/**
	 * updateProperties
	 * @param newProp
	 * @return
	 */
	public final String updateProperties(Properties newProp){		
		// Send the properties to WS to be updated
		serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		String propUpdate = "";
		try{
			propUpdate = wsManager.updateProperties(userInfo.getKey(), PropertiesLoader.getPropertiesAsContentString(newProp).getBytes());
		}catch(Exception e){
			return e.getMessage();
		}

		if(propUpdate.contains("updated")){
			return "OK";
		}else{
			return propUpdate;
		}
	}

	/**
	 * getErrorString
	 * @return
	 */
	public final String getErrorString(){
		return errorString;
	}

	/**
	 * getProperties
	 * @return
	 */
	public final Properties getProperties(){
		return prop;
	}

	/**
	 * testEmailSettings
	 * @param from
	 * @param to
	 * @param smtpServer
	 * @param smtpPort
	 * @param smtpSecurity
	 * @param smtpUser
	 * @param smtpPassword
	 * @return
	 */
	public final String testEmailSettings(String from, String to, String smtpServer, String smtpPort, String smtpSecurity, String smtpUser, String smtpPassword){
		serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		String result = wsManager.sendTestEmail(userInfo.getKey(), from, to, smtpServer, smtpPort, smtpSecurity, smtpUser, smtpPassword);
		
		return result;
	}

}
