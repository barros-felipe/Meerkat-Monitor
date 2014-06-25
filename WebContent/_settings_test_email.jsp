<%@ page import="org.meerkat.config.UserInfo"%>
<%@ page import="org.meerkat.config.SettingsManager"%>
<%@ page import="java.util.Properties"%>
<%
	
	UserInfo userInfo = (UserInfo)session.getAttribute("UserInfo");
	if(userInfo == null || !userInfo.isValid()){
		session.invalidate();
		out.print("Your session is invalid!<br>Please logout/login to create a new one.");
		return;
	}

	SettingsManager setManag = new SettingsManager(userInfo);
	if(!setManag.getErrorString().equalsIgnoreCase("")){
		out.print(setManag.getErrorString());
		return;
	}
	
	// Get current email settings
	String smtpServer = String.valueOf(request.getParameter("meerkat.email.smtp.server"));
	String smtpPort = String.valueOf(request.getParameter("meerkat.email.smtp.port"));
	String smtpUser = String.valueOf(request.getParameter("meerkat.email.smtp.user"));
	String smtpPassword = String.valueOf(request.getParameter("meerkat.email.smtp.password"));
	String smtpSecurity = String.valueOf(request.getParameter("meerkat.email.smtp.security"));
	String smtpFrom = String.valueOf(request.getParameter("meerkat.email.from"));
	String smtpTo = String.valueOf(request.getParameter("meerkat.email.to"));
	
	// Send to server for validation and update
	String operationResult = setManag.testEmailSettings(smtpFrom, smtpTo, smtpServer, smtpPort, smtpSecurity, smtpUser, smtpPassword);
	out.print(operationResult);
	
%>
