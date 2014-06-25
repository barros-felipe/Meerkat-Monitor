<%@ page import="org.meerkat.config.UserInfo"%>
<%@ page import="org.meerkat.config.AppsManagement"%>
<%
	
	UserInfo userInfo = (UserInfo)session.getAttribute("UserInfo");
	if(userInfo == null || !userInfo.isValid()){
		session.invalidate();
		out.print("Your session is invalid!<br>Please logout/login to create a new one.");
		return;
	}

	String operationResult = "";
	AppsManagement appsManag = new AppsManagement(userInfo);
	
	/** Operation */
	boolean deleteAllApplications = Boolean.parseBoolean(request.getParameter("deleteAllApplications"));
	boolean deleteAllEvents = Boolean.parseBoolean(request.getParameter("deleteAllEvents"));
	boolean shutdown = Boolean.parseBoolean(request.getParameter("shutdown"));
	boolean deleteAppEvents = Boolean.parseBoolean(request.getParameter("deleteAppEvents"));
	boolean deleteApp = Boolean.parseBoolean(request.getParameter("deleteApp"));
	boolean changeKey = Boolean.parseBoolean(request.getParameter("changeMasterKey"));
	
	/** Settings */
	String appName = request.getParameter("appName");
	String currKey = request.getParameter("currKey");
	String newKey = request.getParameter("newKey");
	
	
	// Check the requested operation
	if(deleteAllApplications){ // Delete all applications
		operationResult = appsManag.deleteAllApplications();
	}
	else if(deleteAllEvents){ // Delete all events
		operationResult = appsManag.deleteAllEvents();
	}
	else if(shutdown){ // Shutdown
		operationResult = appsManag.shutdownMeerkatMonitor();
	}
	else if(deleteAppEvents){ // Delete all application events
		operationResult = appsManag.deleteAppEvents(appName);
	}
	else if(deleteApp){ // Delete application
		operationResult = appsManag.deleteApp(appName);
	}
	else if(changeKey){ // Change Key
		if(!userInfo.getKey().equals(currKey)){
			out.print("Invalid current key!");
			return;
		}
		operationResult = appsManag.changeMasterKey(newKey);
		session.invalidate();
	}
	
	// return the operation result
	out.print(operationResult);
		
%>
