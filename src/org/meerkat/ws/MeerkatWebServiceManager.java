package org.meerkat.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

//Service Endpoint Interface
@WebService
@SOAPBinding(style = Style.RPC)
public interface MeerkatWebServiceManager{

	@WebMethod String getVersion();
	
	@WebMethod String removeAppByName(String masterKey, String name);
	
	@WebMethod String addSSH(String masterKey, String name, String user, String passwd, String host, String port, String expectedResponse, String cmdToExecute);
	
	
}