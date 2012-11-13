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

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;
import org.meerkat.httpServer.HttpServer;
import org.meerkat.services.SQLService;
import org.meerkat.services.SecureShellSSH;
import org.meerkat.services.SocketService;
import org.meerkat.services.WebApp;
import org.meerkat.services.WebServiceApp;
import org.meerkat.util.MasterKeyManager;
import org.meerkat.webapp.WebAppCollection;
import org.meerkat.webapp.WebAppResponse;

@WebService
public class MeerkatWebService implements MeerkatWebServiceManager{

	private static Logger log = Logger.getLogger(MeerkatWebService.class);
	private WebAppCollection wapc;
	private HttpServer httpServer;
	private MasterKeyManager mkm;

	@Resource WebServiceContext wsContext;

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
	 * getRequestClientIP
	 * @return
	 */
	public final String getRequestClientIP(){
		MessageContext mc = wsContext.getMessageContext();
		HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST); 
		return req.getRemoteAddr(); 
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
		log.info("WS request ["+getRequestClientIP()+"]: getVersion()");
		return wapc.getAppVersion();
	}

	@Override
	public String addSSH(String masterKey, String name, String user, String passwd, String host, String port, String expectedResponse, String cmdToExecute){
		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}

		if(wapc.isWebAppByNamePresent(name)){
			return "Invalid or duplicated name!";
		}

		SecureShellSSH sshApp = new SecureShellSSH(name, user, passwd, host, port, expectedResponse, cmdToExecute);
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

		log.info("WS request ["+getRequestClientIP()+"]: addSSH() with name: "+name);
		return "SSH application added! Current status: "+online+".";
	}


	@Override
	public String removeAppByName(String masterKey, String name) {
		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}

		if(!wapc.isWebAppByNamePresent(name)){
			return "Application "+name+" does not exist!";
		}else{
			WebApp tmp = wapc.getWebAppByName(name);
			wapc.removeWebApp(tmp);
			wapc.saveConfigXMLFile();
			httpServer.refreshIndex();

			log.info("WS request ["+getRequestClientIP()+"]: removeAppByName() named: "+name);
			return "Application "+name+" removed successfully.";
		}
	}

	@Override
	public String changeMasterKey(String currentMasterKey, String newMasterKey) {
		if(!checkKey(currentMasterKey)){
			log.info("WS BAD request ["+getRequestClientIP()+"]: changeMasterKey(). Bad attempt to change MasterKey!");
			return "Incorrect current key!";
		}

		if(newMasterKey.length() <=5){
			return "Please choose a key with at least 6 characters.";
		}

		mkm.changeMasterKey(String.valueOf(newMasterKey));

		log.info("WS request ["+getRequestClientIP()+"]: changeMasterKey(). (MasterKey changed!)");
		return "Master key changed successfully.";


	}

	@Override
	public String addSocket(String masterKey, String name, String host, String port,
			String sendString, String expectedString, String executeOnOffline) {

		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}

		if(wapc.isWebAppByNamePresent(name)){
			return "Invalid or duplicated name!";
		}

		SocketService socketService = new SocketService(name, host, port, sendString, expectedString, executeOnOffline);
		WebAppResponse currentStatus = socketService.checkWebAppStatus();
		String online = "OFFLINE";
		if(currentStatus.isOnline()){
			online = "ONLINE";
		}
		wapc.addWebApp(socketService);
		wapc.getWebAppByName(name).writeWebAppVisualizationDataFile();
		wapc.writeWebAppCollectionDataFile();
		wapc.saveConfigXMLFile();
		httpServer.refreshIndex();

		log.info("WS request ["+getRequestClientIP()+"]: addSocket() named: "+name);
		return "Socket application added! Current status: "+online+".";
	}

	@Override
	public String addDB(String masterKey, String name, String host,
			String port, String instanceName, String username, String password,
			String dbType, String query, String expectedResponse, String executeOnOffline) {

		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}

		if(wapc.isWebAppByNamePresent(name)){
			return "Invalid or duplicated name!";
		}

		// Check the database type
		if(!dbType.equalsIgnoreCase(SQLService.TYPE_MSSQL) && 
				!dbType.equalsIgnoreCase(SQLService.TYPE_MYSQL) &&
				!dbType.equalsIgnoreCase(SQLService.TYPE_ORA)){

			return "Please add a valid database type! " +
					SQLService.TYPE_MYSQL+", "+
					SQLService.TYPE_ORA+" or "+
					SQLService.TYPE_MSSQL;
		}

		SQLService sqlService = new SQLService(name, query, expectedResponse, host, port, instanceName, username, password);

		// Set the correct database type
		if(dbType.equals(SQLService.TYPE_MSSQL)){
			sqlService.setDBTypeMSSQL();
		}else if(dbType.equals(SQLService.TYPE_MYSQL)){
			sqlService.setDBTypeMySQL();
		}else if(dbType.equals(SQLService.TYPE_ORA)){
			sqlService.setDBTypeORA();
		}

		sqlService.setExecuteOnOffline(executeOnOffline);

		WebAppResponse currentStatus = sqlService.checkWebAppStatus();
		String online = "OFFLINE";
		if(currentStatus.isOnline()){
			online = "ONLINE";
		}
		wapc.addWebApp(sqlService);
		wapc.getWebAppByName(name).writeWebAppVisualizationDataFile();
		wapc.writeWebAppCollectionDataFile();
		wapc.saveConfigXMLFile();
		httpServer.refreshIndex();

		log.info("WS request ["+getRequestClientIP()+"]: addDB() named: "+name+", type: "+sqlService.getDBType());
		return "Database application added! Current status: "+online+".";
	}

	@Override
	public String addWeb(String masterKey, String name, String url, String expectedString,
			String executeOnOffline) {

		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}

		if(wapc.isWebAppByNamePresent(name)){
			return "Invalid or duplicated name!";
		}

		WebApp webApp = new WebApp(name, url, expectedString, executeOnOffline);

		WebAppResponse currentStatus = webApp.checkWebAppStatus();
		String online = "OFFLINE";
		if(currentStatus.isOnline()){
			online = "ONLINE";
		}
		wapc.addWebApp(webApp);
		wapc.getWebAppByName(name).writeWebAppVisualizationDataFile();
		wapc.writeWebAppCollectionDataFile();
		wapc.saveConfigXMLFile();
		httpServer.refreshIndex();

		log.info("WS request ["+getRequestClientIP()+"]: addWeb() named: "+name);
		return "Web application added! Current status: "+online+".";
	}

	@Override
	public String addWebService(String masterKey, String name, String url,
			String soapAction, String sendXML, String responseXML, String executeOnOffline) {

		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}

		if(wapc.isWebAppByNamePresent(name)){
			return "Invalid or duplicated name!";
		}

		WebServiceApp webServApp = new WebServiceApp(name, url, soapAction, executeOnOffline);
		webServApp.setPostXML(sendXML);
		webServApp.setResponseXML(responseXML);

		WebAppResponse currentStatus = webServApp.checkWebAppStatus();
		String online = "OFFLINE";
		if(currentStatus.isOnline()){
			online = "ONLINE";
		}
		wapc.addWebApp(webServApp);
		wapc.getWebAppByName(name).writeWebAppVisualizationDataFile();
		wapc.writeWebAppCollectionDataFile();
		wapc.saveConfigXMLFile();
		httpServer.refreshIndex();

		log.info("WS request ["+getRequestClientIP()+"]: addWebService() named: "+name);
		return "WebService added! Current status: "+online+".";
	}

	@Override
	public String resetAllData(String masterKey) {
		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}

		wapc.resetAllAppsData();
		log.info("WS request ["+getRequestClientIP()+"]: resetAllData()");

		return "Removed all applications data. (DB is now empty!)";

	}

	@Override
	public String resetAllAppDataFromName(String masterKey, String name) {
		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}

		if(!wapc.isWebAppByNamePresent(name)){
			return "Application: "+name+" - does not exist!";
		}

		wapc.resetAllAppDataFromName(name);
		log.info("WS request ["+getRequestClientIP()+"]: resetAllAppDataFromName("+name+")");

		return "Removed all data from application: "+name;
	}

	@Override
	public String shutdown(String masterKey) {
		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}

		log.info("Shutting down Meerkat-Monitor...");
		log.info("\tClosing Database...");
		wapc.getEmbeddedDB().shutdownDB();
		log.info("\tClosing application...");
		log.info("\tFinished. Meerkat-Monitor stopped!");
		log.info("");
		System.exit(0);
		
		return ""; // keep the compiler happy
	}











}
