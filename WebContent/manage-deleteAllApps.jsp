<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@include file="shared/validate-session.jsp" %>
<!DOCTYPE HTML>
<html>
	<head>
		<title>Meerkat-Monitor - Delete All Applications</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<%@include file="shared/javascript-imports.txt" %>
		<%@include file="shared/css-imports.txt" %>
		
		<script type="text/javascript">
			$(document).ready(function(){
				<!-- handle the submit -->
				$("#confirm").click(function(){
					$("#buttons").hide();
					$("#msgbox").removeClass().addClass('info').html('Deleting applications...<img src="images/loading.gif"> ').fadeIn(1000);
					this.timer = setTimeout(function () {
						$.ajax({
							url: '_manage_oper.jsp',
							data: 'deleteAllApplications=true',
							type: 'post',
							success: function(msg){
								$("#msgbox").fadeTo(200,0.1,function(){
									$(this).html(msg).removeClass().fadeTo(900,1);
								});
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
				<%@include file="shared/menu-items.txt" %>
			</ul>
		</nav>
		
		<div class="wrapper wrapper-style2">
				<article class="5grid-layout" id="work">
					<header>
						<h2><strong>Meerkat-Monitor</strong></h2>
						<span>Delete All Applications</span>
					</header>
					<div class="5grid-layout">
						<section class="box box-style1">
							<h4><strong>Really want to delete all applications?</strong></h4>
							<div>
								<span>
									<p>All applications and events will be removed from Meerkat-Monitor!<br>
									(This operation may take several minutes to complete)</p>
								</span>
							</div>		
							
							<div id="buttons">			
								<input type="submit" class="button" id="confirm" value="Confirm" />	
								<input type="reset" class="button button-alt" id="cancel" value="Cancel" onclick="history.back(-1)"/>
							</div>
							
							<div id="msgbox"></div>						
						</section>
					</div>
				</article>
			</div>

			<!--  Footer -->
			<%@include file="shared/footer.txt" %>		

	</body>
</html>