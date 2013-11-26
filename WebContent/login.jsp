<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE HTML>
<html>
	<head>
		<title>Meerkat-Monitor Admin login</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<%@include file="shared/javascript-imports.txt" %>
		<%@include file="shared/css-imports.txt" %>
		
		<%
			String redirOrigPage = String.valueOf(request.getParameter("redir")).trim();
			String redirpage = "index.jsp";
			if(!redirOrigPage.equals("null") && !redirOrigPage.equals("")){
				redirpage = redirOrigPage;
			}
		%>
		
		<script type="text/javascript">
			$(document).ready(function(){
				$("#login").submit(function(){
					$("#msgbox").removeClass().addClass('info').text('Validating...').fadeIn(1000);
					this.timer = setTimeout(function () {
						$.ajax({
							url: '_login_oper.jsp',
							data: 'name='+ $('#name').val() +'&key=' + $('#key').val() +'&wsdlurl=' + $('#wsdlurl').val(),
							type: 'post',
							success: function(msg){
								if(msg.indexOf("OK") !== -1){
									$("#msgbox").html('Login Verified, Logging in.....').fadeTo(900,1,function(){
										document.location='<%= redirpage %>';
									});
								}else{
									$("#msgbox").fadeTo(200,0.1,function(){
										$(this).html('Sorry, failed to login.<br>'+msg).removeClass().fadeTo(900,1);
									});
								}
							}
						});
					}, 200);
					
					return false;
				});		
			});
	</script>
		
	</head>
	<body>

		<!-- Nav -->
			<nav id="nav">
				<ul>
					<li><a href="login.jsp">Login</a></li>
				</ul>
			</nav>

		<!-- Home -->
			<div class="wrapper wrapper-style3">
				<article class="5grid-layout" id="top">
					<div class="row">
						<div class="4u">
							<span class="me image image-full"><img src="images/meerkat.png" alt="" /></span>
						</div>
						<div class="8u">
							<header>
								<h1><strong>Meerkat-Monitor</strong></h1>
							</header>
						
							<div class="12u">
								<form name="login" id="login" action="" method="post">
									<div class="5grid">
										<div class="row">
											<div class="6u">
												<input type="text" onfocus="this.blur()" name="name" id="name" value="admin" placeholder="admin" />
											</div>
										</div>
										<div class="row">	
											<div class="6u">
												<input type="password" name="key" id="key" placeholder="User Key" />
											</div>
										</div>
										<div class="row">	
											<div class="6u">
												<input type="text" name="wsdlurl" id="wsdlurl" placeholder="" value="http://<%= java.net.InetAddress.getLocalHost().getHostName() %>:6778/api?wsdl"/>
											</div>
										</div>
										<div class="row">
											<input type="submit" class="button" value="submit" />
										</div>
									</div>
								</form>
								<div id="msgbox"></div>
							</div>
						</div>
					</div>
				</article>
			</div>

		<!--  Footer -->
		<%@include file="shared/footer.txt" %>


	</body>
</html>