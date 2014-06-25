<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@include file="shared/validate-session.jsp" %>
<!DOCTYPE HTML>
<html>
	<head>
		<title>Meerkat-Monitor - Logout</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<%@include file="shared/javascript-imports.txt" %>
		<%@include file="shared/css-imports.txt" %>
	</head>
	<body>

		<!-- Nav -->
			<nav id="nav">
				<%@include file="shared/menu-items.txt" %>
			</nav>

		<!-- Work -->
			<div class="wrapper wrapper-style2">
				<article id="work">
					<header>
						<h2><strong>Meerkat-Monitor</strong></h2>
						<span>Confirm exit?</span>
					</header>
					<div class="5grid-layout">
						<div class="row">
							<div>
								<section class="box box-style1">
									<h3>Do you really want to log out?</h3>
									<a href="_logout_oper.jsp" class="button">Confirm</a>
									<a href="javascript:javascript:history.go(-1)" class="button button-alt">Cancel</a>
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