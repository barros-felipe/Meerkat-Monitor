<%@ page import="org.meerkat.config.UserInfo"%>
<%
	String username = request.getParameter("name");
	String key = request.getParameter("key");
	String wsdlURL = request.getParameter("wsdlurl");

	if (username == null || key == null || wsdlURL == null) {
		session.invalidate();
		out.print("");
	} else {
		UserInfo userInfo = new UserInfo(username, key, wsdlURL);
		session = request.getSession(true);
		if (userInfo.isValid()) {
			session.setMaxInactiveInterval(600); // Timeout 10 minutes
			session.setAttribute("active", "true");
			session.setAttribute("UserInfo", userInfo);
			out.print("OK");
		} else {
			session.invalidate();
			out.print(userInfo.getErrorString());
		}
	}
%>
