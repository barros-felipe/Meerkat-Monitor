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

public class AppsManagement {
	private UserInfo userInfo;
	
	public AppsManagement(UserInfo userInfo){
		this.userInfo = userInfo;
	}
	
	/**
	 * getAppList
	 * @return
	 */
	public String getAppList(){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		String result = wsManager.getAppList(userInfo.getKey());
		
		return result;	
	}
	
	/**
	 * deleteAllApplications
	 * @return
	 */
	public String deleteAllApplications(){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		return wsManager.removeAllApps(userInfo.getKey());
	}
	
	/**
	 * deleteAllEvents
	 * @return
	 */
	public String deleteAllEvents(){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		return wsManager.resetAllData(userInfo.getKey());
	}
	
	/**
	 * shutdownMeerkatMonitor
	 * @return
	 */
	public String shutdownMeerkatMonitor(){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		return wsManager.shutdown(userInfo.getKey());
	}
	
	/**
	 * isAppActive
	 * @param appName
	 * @return
	 */
	public boolean isAppActive(String appName){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		return Boolean.valueOf(wsManager.isActive(userInfo.getKey(), appName));
	}
	
	/**
	 * setAppActive
	 * @param appName
	 * @param active
	 * @return
	 */
	public String setAppActive(String appName, boolean active){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		return wsManager.setActive(userInfo.getKey(), appName, active);
		
	}
	
	/**
	 * deleteAppEvents
	 * @param appName
	 * @return
	 */
	public String deleteAppEvents(String appName){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		
		return wsManager.resetAllAppDataFromName(userInfo.getKey(), appName);
	}
	
	/**
	 * deleteApp
	 * @param appName
	 * @return
	 */
	public String deleteApp(String appName){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		
		return wsManager.removeAppByName(userInfo.getKey(), appName);
	}
	
	/**
	 * changeMasterKey
	 * @param newKey
	 * @return
	 */
	public String changeMasterKey(String newKey){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		
		return wsManager.changeMasterKey(userInfo.getKey(), newKey);
	}
	
	/**
	 * getDashboardURL
	 * @return
	 */
	public String getDashboardURL(){
		ServiceRequestManager serviceReq = new ServiceRequestManager(userInfo.getUserClientURLWSDL());
		MeerkatWebServiceManager wsManager = serviceReq.getWebServiceManager();
		
		return wsManager.getDashboardURL(userInfo.getKey());
	}
}
