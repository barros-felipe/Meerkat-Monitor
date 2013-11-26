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

public class UserInfo {

	private String username;
	private String key;
	private boolean isValid = false;
	private String userClientURLWSDL = "";
	private ServiceRequestManager serviceReq;
	private String errorString = "";

	public UserInfo(String username, String key, String userClientURLWSDL){
		this.username = username;
		this.key = key;
		this.userClientURLWSDL = userClientURLWSDL;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setisValid(boolean isValid){
		this.isValid = isValid;
	}

	public void setUserClientURLWSDL(String userURLWSDL){
		userClientURLWSDL = userURLWSDL;
	}

	public String getUserClientURLWSDL(){
		return userClientURLWSDL;
	}

	public String getInstanceDashboardUrl(){
		int lastPos = userClientURLWSDL.lastIndexOf("/");

		return userClientURLWSDL.substring(0, lastPos);
	}

	/**
	 * isValid
	 * @return
	 */
	public boolean isValid(){
		try{
			serviceReq = new ServiceRequestManager(userClientURLWSDL);
			MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
			isValid = Boolean.valueOf(wsManager.checkKey(key));
			if(!isValid){
				errorString = "Invalid key (Default key is: \"meerkat\").";
			}
		}catch(Exception e){
			isValid = false;
			errorString = e.getMessage();
		}

		return isValid;
	}

	/**
	 * getErrorString
	 * @return
	 */
	public String getErrorString(){
		return errorString;
	}

}
