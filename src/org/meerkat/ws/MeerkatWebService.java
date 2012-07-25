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

import javax.jws.WebService;

import org.meerkat.httpServer.HttpServer;
import org.meerkat.services.SecureShellSSH;
import org.meerkat.services.WebApp;
import org.meerkat.util.MasterKeyManager;
import org.meerkat.webapp.WebAppCollection;
import org.meerkat.webapp.WebAppResponse;

@WebService
public class MeerkatWebService implements MeerkatWebServiceManager{

	private WebAppCollection wapc;
	private HttpServer httpServer;
	private MasterKeyManager mkm;
	
	/**
	 * MeerkatWebService
	 * @param masterKey
	 * @param webAppsCollection
	 * @param httpServer
	 */
	public MeerkatWebService(final MasterKeyManager mkm, WebAppCollection webAppsCollection, HttpServer httpServer){
		this.mkm = mkm;
		this.httpServer = httpServer;
		this.wapc = webAppsCollection;
	}

	/**
	 * checkKey
	 * @param givenKey
	 * @return
	 */
	private boolean checkKey(String givenKey){
		if(givenKey.equals(mkm.getMasterKey())){
			return true;
		}
		return false;
	}


	@Override
	public String getVersion() {
		return wapc.getAppVersion();
	}

	@Override
	public String addSSH(String masterKey, String name, String user, String passwd, String host, String port, String expectedResponse, String cmdToExecute){
		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}

		SecureShellSSH sshApp = new SecureShellSSH(mkm, name, user, passwd, host, port, expectedResponse, cmdToExecute);
		WebAppResponse currentStatus = sshApp.checkWebAppStatus();
		String online = "OFFLINE";
		if(currentStatus.isOnline()){
			online = "ONLINE";
		}
		wapc.addWebApp(sshApp);
		wapc.getWebAppByName(name).writeWebAppVisualizationDataFile();
		wapc.writeWebAppCollectionDataFile();
		wapc.saveConfigXMLFile();
		httpServer.refreshIndex();

		return "SSH application added! Current status: "+online+".";
	}


	@Override
	public String removeAppByName(String masterKey, String name) {
		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}

		if(!wapc.isWebAppByNamePresent(name)){
			return "Application "+name+" is not present!";
		}else{
			WebApp tmp = wapc.getWebAppByName(name);
			wapc.removeWebApp(tmp);
			wapc.saveConfigXMLFile();
			httpServer.refreshIndex();

			return "Removed application "+name+".";
		}
	}

	@Override
	public String changeMasterKey(String currentMasterKey, String newMasterKey) {
		if(!checkKey(currentMasterKey)){
			return "Incorrect current key!";
		}

		if(newMasterKey.length() <=5){
			return "Please choose a key with at least 6 characters.";
		}
		
		mkm.changeMasterKey(String.valueOf(newMasterKey));
				
		return "Master key changed successfully.";


	}











}
