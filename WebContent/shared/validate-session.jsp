<%
	boolean active = Boolean.valueOf((String)session.getAttribute("active"));
	String requestUrl = request.getRequestURL().toString();
	int lastpos = requestUrl.lastIndexOf("/");
	String requestedPage = requestUrl.substring(lastpos+1, requestUrl.length());
	
	if(!active){
		if(!requestedPage.equalsIgnoreCase("index.jsp") 
				&& !requestedPage.equalsIgnoreCase("login.jsp")
				&& !requestedPage.equalsIgnoreCase("logout.jsp")){
			response.sendRedirect("login.jsp?redir="+requestedPage);
		}else{
			response.sendRedirect("login.jsp");
		}
		return;
	}
%>