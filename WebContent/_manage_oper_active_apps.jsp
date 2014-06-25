<%@ page import="org.meerkat.config.UserInfo"%>
<%@ page import="org.meerkat.config.AppsManagement"%>
<%
	
	UserInfo userInfo = (UserInfo)session.getAttribute("UserInfo");
	if(userInfo == null || !userInfo.isValid()){
		session.invalidate();
		out.print("Your session is invalid!\nPlease logout/login to create a new one.");
		return;
	}

	String operationResult = "";
	AppsManagement appsManag = new AppsManagement(userInfo);
	
	
	String appName = request.getParameter("appName").toString().replaceAll("_", " ");
	boolean active = Boolean.parseBoolean(request.getParameter("active"));
	boolean setStatus = Boolean.parseBoolean(request.getParameter("setStatus"));
	boolean getCurrStatus = Boolean.parseBoolean(request.getParameter("getStatus"));
	
	if(setStatus){ // Set/get active
		operationResult = appsManag.setAppActive(appName, active);
	}else if(getCurrStatus){ // get current app status
		operationResult = String.valueOf(appsManag.isAppActive(appName));
	}
	
	
	// return the operation result
	out.print(operationResult);
		
%>
