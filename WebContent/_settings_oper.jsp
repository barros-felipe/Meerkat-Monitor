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
	
	// Get current server properties and update with user defined from post
	Properties postedProps = setManag.getProperties();
	postedProps.put("meerkat.webserver.showapptype", String.valueOf(request.getParameter("meerkat.webserver.showapptype")));
	postedProps.put("meerkat.dashboard.gauge", String.valueOf(request.getParameter("meerkat.dashboard.gauge")));
	postedProps.put("meerkat.webserver.rconfig", String.valueOf(request.getParameter("meerkat.webserver.rconfig")));
	postedProps.put("meerkat.webserver.logaccess", String.valueOf(request.getParameter("meerkat.webserver.logaccess")));
	postedProps.put("meerkat.monit.test.time", String.valueOf(request.getParameter("meerkat.monit.test.time")));
	postedProps.put("meerkat.app.timeline.maxrecords", String.valueOf(request.getParameter("meerkat.app.timeline.maxrecords")));
	//postedProps.put("meerkat.webserver.port", String.valueOf(request.getParameter("meerkat.webserver.port")));
	postedProps.put("meerkat.email.send.emails", String.valueOf(request.getParameter("meerkat.email.send.emails")));
	postedProps.put("meerkat.email.sending.test", String.valueOf(request.getParameter("meerkat.email.sending.test")));
	postedProps.put("meerkat.email.smtp.server", String.valueOf(request.getParameter("meerkat.email.smtp.server")));
	postedProps.put("meerkat.email.smtp.port", String.valueOf(request.getParameter("meerkat.email.smtp.port")));
	postedProps.put("meerkat.email.smtp.user", String.valueOf(request.getParameter("meerkat.email.smtp.user")));
	postedProps.put("meerkat.email.smtp.password", String.valueOf(request.getParameter("meerkat.email.smtp.password")));
	postedProps.put("meerkat.email.smtp.security", String.valueOf(request.getParameter("meerkat.email.smtp.security")));
	postedProps.put("meerkat.email.subjectPrefix", String.valueOf(request.getParameter("meerkat.email.subjectPrefix")));
	postedProps.put("meerkat.email.from", String.valueOf(request.getParameter("meerkat.email.from")));
	postedProps.put("meerkat.email.to", String.valueOf(request.getParameter("meerkat.email.to")));
	
	// Send to server for validation and update
	String operationResult = setManag.updateProperties(postedProps);
	out.print(operationResult);
	
%>
