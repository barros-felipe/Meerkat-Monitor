<%@include file="shared/validate-session.jsp" %>
<!DOCTYPE HTML>
<html>
	<head>
		<title>Meerkat-Monitor - Add SSH</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<%@include file="shared/javascript-imports.txt" %>
		<%@include file="shared/css-imports.txt" %>
		<link rel="stylesheet" type="text/css" href="css/onoffswitch.css">
		
		<script type="text/javascript">
			$(document).ready(function(){
				<!-- handle the submit -->
				$("#settings").submit(function(){
					$("#msgbox").removeClass().addClass('info').html('Saving...<img src="images/loading.gif"> ').fadeIn(1000);
					this.timer = setTimeout(function () {
						<!-- Update all vars with user input -->
						var app_active = $("#active").is(":checked");
						var app_name = $("#name").val();
						var app_user = $("#user").val();
						var app_password = $("#password").val(); 
						var app_host = $("#host").val();
						var app_port = $("#port").val();
						var app_cmdToExecute = $("#cmdToExecute").val();
						var app_expectedstring = $("#expectedstring").val(); 
						var app_executeoffline = $("#executeoffline").val(); 
						var app_groups = $("#groups").val(); 
						
						$.ajax({
							url: '_add_ssh.jsp',
							data: 'active='+app_active+
								'&name='+app_name+
								'&user='+app_user+
								'&password='+app_password+
								'&host='+app_host+
								'&port='+app_port+
								'&cmdToExecute='+app_cmdToExecute+
								'&expectedstring='+app_expectedstring+
								'&executeoffline='+app_executeoffline+
								'&groups='+app_groups,
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
						<span>Add SSH</span>
					</header>
					
						<section class="box box-style1">
						
							<form name="settings" id="settings" action="" method="post">
								<!--  General Settings -->
								<section class="box box-style1">
									
									<!-- Aplication type -->
									<div class="row">
										<div class="4u">
											<span>Active</span>
										</div>
										<div class="8u">
											<div class="onoffswitch">
							   	 				<input type="checkbox" name="appType" class="onoffswitch-checkbox" id="active" checked >
							    				<label class="onoffswitch-label" for="active">
							        				<div class="onoffswitch-inner"></div>
							        				<div class="onoffswitch-switch"></div>
							    				</label>
											</div>
										</div>
									</div>
									
									<!-- Name -->
									<div class="row">
										<div class="4u">
											<span>Name</span>
										</div>
										<div class="4u">
											<input type="text" name="name" id="name" placeholder="Application Name" value=""/>
										</div>
									</div>
									
									<!-- HOST -->
									<div class="row">
										<div class="4u">
											<span>Host</span>
										</div>
										<div class="4u">
											<input type="text" name="host" id="host" placeholder="Hostname/IP" value=""/>
										</div>
									</div>
									
									<!-- PORT -->
									<div class="row">
										<div class="4u">
											<span>Port</span>
										</div>
										<div class="4u">
											<input type="text" name="port" id="port" placeholder="Service Port" value=""/>
										</div>
									</div>
									
									<!-- USER -->
									<div class="row">
										<div class="4u">
											<span>User</span>
										</div>
										<div class="4u">
											<input type="text" name="user" id="user" placeholder="Username" value=""/>
										</div>
									</div>
									
									<!-- PASSWD -->
									<div class="row">
										<div class="4u">
											<span>Password</span>
										</div>
										<div class="4u">
											<input type="password" name="password" id="password" placeholder="Password" value=""/>
										</div>
									</div>
									
									<!-- Cmd to execute -->
									<div class="row">
										<div class="4u">
											<span>Cmd to execute</span>
										</div>
										<div class="4u">
											<input type="text" name="cmdToExecute" id="cmdToExecute" placeholder="Remote shell cmd to execute" value=""/>
										</div>
									</div>
									
									<!-- Expected String -->
									<div class="row">
										<div class="4u">
											<span>Expected String</span>
										</div>
										<div class="4u">
											<input type="text" name="expectedstring" id="expectedstring" placeholder="Expected string in remote cmd result" value=""/>
										</div>
									</div>
									
									<!-- Execute on offline -->
									<div class="row">
										<div class="4u">
											<span>Execute on Offline</span>
										</div>
										<div class="4u">
											<input type="text" name="executeoffline" id="executeoffline" placeholder="Local cmd to execute when offline" value=""/>
										</div>
									</div>
									
									<!-- Groups -->
									<div class="row">
										<div class="4u">
											<span>Groups</span>
										</div>
										<div class="4u">
											<input type="text" name="groups" id="groups" placeholder="Groups (comma separated)" value=""/>
										</div>
									</div>
							
								</section>
							
								<input type="submit" class="button" value="Submit" />	
								<div id="msgbox"></div>
							</form>							
						</section>
						
				</article>
			</div>

			<!--  Footer -->
			<%@include file="shared/footer.txt" %>		

	</body>
</html>