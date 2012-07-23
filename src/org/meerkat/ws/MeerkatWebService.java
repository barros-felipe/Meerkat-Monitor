package org.meerkat.ws;

import javax.jws.WebService;

import org.meerkat.httpServer.HttpServer;
import org.meerkat.services.SecureShellSSH;
import org.meerkat.services.WebApp;
import org.meerkat.webapp.WebAppCollection;
import org.meerkat.webapp.WebAppResponse;

@WebService(endpointInterface = "org.meerkat.ws.MeerkatWebServiceManager")

public class MeerkatWebService implements MeerkatWebServiceManager{

	WebAppCollection wapc;
	HttpServer httpServer;
	private String masterKey;
	
	/**
	 * MeerkatWebService
	 * @param masterKey
	 * @param webAppsCollection
	 * @param httpServer
	 */
	public MeerkatWebService(String masterKey, WebAppCollection webAppsCollection, HttpServer httpServer){
		this.masterKey = masterKey;
		this.httpServer = httpServer;
		this.wapc = webAppsCollection;
	}
	
	/**
	 * checkKey
	 * @param givenKey
	 * @return
	 */
	private boolean checkKey(String givenKey){
		if(givenKey.equals(masterKey)){
			return true;
		}
		return false;
	}
	
	
	@Override
	public String getVersion() {
		return wapc.getAppVersion();
	}


	@Override
	public String addSSH(String masterKey, String name, String user, String passwd, String host, String port, String expectedResponse, String cmdToExecute) {
		if(!checkKey(masterKey)){
			return "Incorrect key!";
		}
		
		SecureShellSSH sshApp = new SecureShellSSH(name, user, passwd, host, port, expectedResponse, cmdToExecute);
		WebAppResponse currentStatus = sshApp.checkWebAppStatus();
		String online = "OFFLINE";
		if(currentStatus.isOnline()){
			online = "ONLINE";
		}
		wapc.addWebApp(sshApp);
		sshApp.writeWebAppVisualizationDataFile();
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


	
	
	
	
	
	
	

}
