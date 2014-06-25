<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@include file="shared/validate-session.jsp" %>
<!DOCTYPE HTML>
<html>
	<head>
		<title>Meerkat-Monitor - Change Master Key</title>
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
					
					var key_curr = $("#key").val();
					var key_new = $("#newkey").val(); 
					var key_new_confirm = $("#confirmnewkey").val(); 
					
					if(key_new !== key_new_confirm){
						$("#buttons").show();
						$("#msgbox").fadeTo(200,0.1,function(){
							$("#buttons").show();
							$(this).html("Confirmation key do not match!").removeClass().fadeTo(900,1);
						});
						return false;
					}
					
					if(key_new.length < 6){
						$("#buttons").show();
						$("#msgbox").fadeTo(200,0.1,function(){
							$("#buttons").show();
							$(this).html("New key is too short. Requires minimum of 6 chars.").removeClass().fadeTo(900,1);
						});
						return false;
					}
					
					$("#msgbox").removeClass().addClass('info').html('Changing key...<img src="images/loading.gif"> ').fadeIn(1000);
					this.timer = setTimeout(function () {
						$.ajax({
							url: '_manage_oper.jsp',
							data: 'changeMasterKey=true'+
								'&currKey='+key_curr+
								'&newKey='+key_new,
							type: 'post',
							success: function(msg){
								$("#msgbox").fadeTo(200,0.1,function(){
									$("#buttons").show();
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
						<span>Change Master Key</span>
					</header>
					<div class="5grid-layout">
						<form name="settings" id="settings" action="" method="post">
							<section class="box box-style1">
								<h4><strong>Insert current Master Key and the new one</strong></h4>
								
								<!-- Current Key -->
								<div class="row">
									<div class="4u">
										<span>Current Key</span>
									</div>
									<div class="4u">
										<input type="password" name="key" id="key" placeholder="Current Master Key" value=""/>
									</div>
								</div>
								
								<!-- New Key -->
								<div class="row">
									<div class="4u">
										<span>New Key</span>
									</div>
									<div class="4u">
										<input type="password" name="newkey" id="newkey" placeholder="New Master Key" value=""/>
									</div>
								</div>
								
								<!-- New Key Confirmation -->
								<div class="row">
									<div class="4u">
										<span>Confirm New Key</span>
									</div>
									<div class="4u">
										<input type="password" name="confirmnewkey" id="confirmnewkey" placeholder="Confirm New Master Key" value=""/>
									</div>
								</div>
								
								<div id="buttons">			
									<input type="submit" class="button" id="confirm" value="Confirm" />	
									<input type="reset" class="button button-alt" id="cancel" value="Cancel" onclick="history.back(-1)"/>
								</div>
								
								<div id="msgbox"></div>						
							</section>
						</form>
					</div>
				</article>
			</div>

			<!--  Footer -->
			<%@include file="shared/footer.txt" %>		

	</body>
</html>