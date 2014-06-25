<%@ page import="org.meerkat.config.UserInfo"%>
<%@ page import="org.meerkat.config.AddAppsManager"%>
<%@ page import="java.util.Properties"%>
<%
	UserInfo userInfo = (UserInfo)session.getAttribute("UserInfo");
	if(userInfo == null || !userInfo.isValid()){
		session.invalidate();
		out.print("Your session is invalid!<br>Please logout/login to create a new one.");
		return;
	}
	
	// Get current settings
	String active = String.valueOf(request.getParameter("active"));
	String dbtype = String.valueOf(request.getParameter("dbtype"));
	String name = String.valueOf(request.getParameter("name"));
	String host = String.valueOf(request.getParameter("host"));
	String port = String.valueOf(request.getParameter("port"));
	String instance = String.valueOf(request.getParameter("instance"));
	String user = String.valueOf(request.getParameter("user"));
	String password = String.valueOf(request.getParameter("password"));
	String query = String.valueOf(request.getParameter("query"));
	String expectedstring = String.valueOf(request.getParameter("expectedstring"));
	String executeoffline = String.valueOf(request.getParameter("executeoffline"));
	String groups = String.valueOf(request.getParameter("groups"));
	
	// Send to server for validation and update
	AddAppsManager addwebapp = new AddAppsManager(userInfo);
	String operationResult = addwebapp.addDatabase(userInfo.getKey(), name, host, port, instance, user, password, 
			dbtype, query, expectedstring, executeoffline, groups, active);
	out.print(operationResult);
%>
