<%
	session.setAttribute("currentSessionUser", "");
	session.setAttribute("currentSessionKey", "");
	session.setAttribute("currentSessionWSDL", "");
	session.setAttribute("active", "");
	session.invalidate();
	response.sendRedirect("index.jsp");
%>
