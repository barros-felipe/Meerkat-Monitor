<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@include file="shared/validate-session.jsp" %>
<%@ page import="org.meerkat.config.UserInfo"%>
<%@ page import="org.meerkat.config.AppsManagement"%>
<%
	UserInfo userInfo = (UserInfo)session.getAttribute("UserInfo");
	AppsManagement appsManag = new AppsManagement(userInfo);
%>

<!DOCTYPE HTML>
<html>
	<head>
		<title>Meerkat-Monitor - Admin</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<%@include file="shared/javascript-imports.txt" %>
		<%@include file="shared/css-imports.txt" %>
	</head>
	<body>
		<!-- Nav -->
			<nav id="nav">
				<ul>
					<%@include file="shared/menu-items.txt" %>
				</ul>
			</nav>

		<!-- Work -->
			<div class="wrapper wrapper-style2">
				<article id="work">
					<header>
						<h2><strong>Meerkat-Monitor</strong></h2>
						<span>Configuration Tool</span>
					</header>
					<div class="5grid-layout">
						<div class="row">
							<div class="4u">
								<section class="box box-style1">
									<a style="text-decoration: none" target="_blank" href="<%= appsManag.getDashboardURL() %>">
										<span class="image image-centered"><img src="images/work02.png" alt="" /></span>
										<h3>Dashboard</h3>
										<p>Access instance dashboard</p>
									</a>
								</section>
							</div>
							<div class="4u">
								<section class="box box-style1">
									<a style="text-decoration: none" href="manage.jsp">
										<span class="image image-centered"><img src="images/work01.png" alt="" /></span>
										<h3>Manage</h3>
										<p>Manage applications and events</p>
									</a>
								</section>
							</div>
							<div class="4u">
								<section class="box box-style1">
									<a style="text-decoration: none" href="settings.jsp">
										<span class="image image-centered"><img src="images/work03.png" alt="" /></span>
										<h3>Settings</h3>
										<p>Configure settings and notifications</p>
									</a>
								</section>
							</div>
						</div>
					</div>
					
				</article>
			</div>

			<!--  Footer -->
			<%@include file="shared/footer.txt" %>

	</body>
</html>