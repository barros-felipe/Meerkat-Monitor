<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@include file="shared/validate-session.jsp" %>
<%@ page import="org.meerkat.config.UserInfo"%>
<%@ page import="org.meerkat.config.SettingsManager"%>
<%@ page import="java.util.Properties"%>
<%
	/** Get properties from server */
	UserInfo userInfo = (UserInfo)session.getAttribute("UserInfo");
	if(userInfo == null || !userInfo.isValid()){
		session.invalidate();
		out.print("Your session is invalid. Plase logout/login to create a new one.");
		return;
	}
	
	SettingsManager setManag = new SettingsManager(userInfo);
	if(!setManag.getErrorString().equalsIgnoreCase("")){
		out.print(setManag.getErrorString());
		return;
	}
	
	Properties prop = setManag.getProperties();
%>

<!DOCTYPE HTML>
<html>
	<head>
		<title>Meerkat-Monitor - Settings</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<%@include file="shared/javascript-imports.txt" %>
		<%@include file="shared/css-imports.txt" %>
		<link rel="stylesheet" type="text/css" href="css/onoffswitch.css">
		
		<script type="text/javascript">
			<!-- check boxes control -->
			var status_appType = <%= Boolean.valueOf(prop.getProperty("meerkat.webserver.showapptype")) %>;
			var status_availGauge = <%= Boolean.valueOf(prop.getProperty("meerkat.dashboard.gauge")) %>;
			var status_configAccess = <%= Boolean.valueOf(prop.getProperty("meerkat.webserver.rconfig")) %>;
			var status_logAccess = <%= Boolean.valueOf(prop.getProperty("meerkat.webserver.logaccess")) %>;
			var status_sendEmailNotif = <%= Boolean.valueOf(prop.getProperty("meerkat.email.send.emails")) %>;
			var status_sendTestEmail = <%= Boolean.valueOf(prop.getProperty("meerkat.email.sending.test")) %>;
		</script>	
		
		<script type="text/javascript">
			$(document).ready(function(){
				<!-- Update the checkboxes status -->
				$("#appType").attr('checked', status_appType);
				$("#availGauge").attr('checked', status_availGauge);
				$("#configAccess").attr('checked', status_configAccess);
				$("#logAccess").attr('checked', status_logAccess);
				$("#sendEmailNotif").attr('checked', status_sendEmailNotif);
				$("#sendTestEmail").attr('checked', status_sendTestEmail);

				<!-- handle the submit -->
				$("#settings").submit(function(){
					$("#msgbox").removeClass().addClass('info').text('Saving settings...').fadeIn(1000);
					this.timer = setTimeout(function () {
						<!-- Update all vars with user input -->
						var status_appType = $("#appType").is(":checked");
						var status_availGauge = $("#availGauge").is(":checked");
						var status_configAccess = $("#configAccess").is(":checked");
						var status_logAccess = $("#logAccess").is(":checked");
						var status_pause = $("#pause").val(); 
						var status_timelineMaxRecords = $("#timelineMaxRecords").val(); 
						/** var status_webserverPort = $("#webserverPort").val(); */ 
						var status_sendEmailNotif = $("#sendEmailNotif").is(":checked");
						var status_sendTestEmail = $("#sendTestEmail").is(":checked");
						var status_smtp_server = $("#smtp_server").val(); 
						var status_smtp_port = $("#smtp_port").val(); 
						var status_smtp_user = $("#smtp_user").val(); 
						var status_smtp_password = $("#smtp_password").val(); 
						var status_smtp_security = $("#smtp_security").val(); 
						var status_smtp_prefix = $("#smtp_prefix").val(); 
						var status_smtp_from = $("#smtp_from").val(); 
						var status_smtp_to = $("#smtp_to").val(); 

						$.ajax({
							url: '_settings_oper.jsp',
							data: 'meerkat.webserver.showapptype='+status_appType+
								'&meerkat.dashboard.gauge='+status_availGauge+
								'&meerkat.webserver.rconfig='+status_configAccess+
								'&meerkat.webserver.logaccess='+status_logAccess+
								'&meerkat.monit.test.time='+status_pause+
								'&meerkat.app.timeline.maxrecords='+status_timelineMaxRecords+
								/** '&meerkat.webserver.port='+status_webserverPort+ */
								'&meerkat.email.send.emails='+status_sendEmailNotif+
								'&meerkat.email.sending.test='+status_sendTestEmail+
								'&meerkat.email.smtp.server='+status_smtp_server+
								'&meerkat.email.smtp.port='+status_smtp_port+
								'&meerkat.email.smtp.user='+status_smtp_user+
								'&meerkat.email.smtp.password='+status_smtp_password+
								'&meerkat.email.smtp.security='+status_smtp_security+
								'&meerkat.email.subjectPrefix='+status_smtp_prefix+
								'&meerkat.email.from='+status_smtp_from+
								'&meerkat.email.to='+status_smtp_to,
							type: 'post',
							success: function(msg){
								if(msg.indexOf("OK") !== -1){
									$("#msgbox").html('Saved! New settings will take effect in a moment.').fadeTo(900,1,function(){
									});
								}else{
									$("#msgbox").fadeTo(200,0.1,function(){
										$(this).html("ERROR - "+msg).removeClass().fadeTo(900,1);
									});
								}
							}
						});
					}, 200);

					return false;
				});
				
				<!-- Handle Test email -->
				$("#testEmail").click(function(e){
					$("#msgbox").removeClass().addClass('info').text('Sending test email...').fadeIn(1000);
					this.timer = setTimeout(function () {
						<!-- Update all vars with user input -->
						var status_smtp_server = $("#smtp_server").val(); 
						var status_smtp_port = $("#smtp_port").val(); 
						var status_smtp_user = $("#smtp_user").val(); 
						var status_smtp_password = $("#smtp_password").val(); 
						var status_smtp_security = $("#smtp_security").val(); 
						var status_smtp_from = $("#smtp_from").val(); 
						var status_smtp_to = $("#smtp_to").val(); 

						$.ajax({
							url: '_settings_test_email.jsp',
							data: 'meerkat.email.smtp.server='+status_smtp_server+
								'&meerkat.email.smtp.port='+status_smtp_port+
								'&meerkat.email.smtp.user='+status_smtp_user+
								'&meerkat.email.smtp.password='+status_smtp_password+
								'&meerkat.email.smtp.security='+status_smtp_security+
								'&meerkat.email.from='+status_smtp_from+
								'&meerkat.email.to='+status_smtp_to,
							type: 'post',
							success: function(msg){
								if(msg.indexOf("OK") !== -1){
									$("#msgbox").html('Test email sent successfully!').fadeTo(900,1,function(){
									});
								}else{
									$("#msgbox").fadeTo(200,0.1,function(){
										$(this).html("ERROR - "+msg).removeClass().fadeTo(900,1);
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
				<%@include file="shared/menu-items.txt" %>
			</ul>
		</nav>
		
		<div class="wrapper wrapper-style2">
				<article class="5grid-layout" id="work">
					<header>
						<h2><strong>Meerkat-Monitor</strong></h2>
						<span>Settings</span>
					</header>
					<div class="5grid-layout">
						<section class="box box-style1">
						
							<form name="settings" id="settings" action="" method="post">
								<!--  General Settings -->
								<section class="box box-style1">
									<h4><strong>Configuration</strong></h4>
									<!-- Aplication type -->
									<div class="row">
										<div class="4u">
											<span>Show Aplication type on dashboard</span>
										</div>
										<div class="8u">
											<div class="onoffswitch">
							   	 				<input type="checkbox" name="appType" class="onoffswitch-checkbox" id="appType" checked >
							    				<label class="onoffswitch-label" for="appType">
							        				<div class="onoffswitch-inner"></div>
							        				<div class="onoffswitch-switch"></div>
							    				</label>
											</div>
										</div>
									</div>
									
									<!--  Availability Gauge -->
									<div class="row">
										<div class="4u">
											<span>Show Availability Gauge on dashboard</span>
										</div>
										<div class="8u">
											<div class="onoffswitch">
							   	 				<input type="checkbox" name="availGauge" class="onoffswitch-checkbox" id="availGauge" checked >
							    				<label class="onoffswitch-label" for="availGauge">
							        				<div class="onoffswitch-inner"></div>
							        				<div class="onoffswitch-switch"></div>
							    				</label>
											</div>
										</div>
									</div>
									
									<!--  Remote Access to Config -->
									<div class="row">
										<div class="4u">
											<span>Allow remote access to config</span>
										</div>
										<div class="8u">
											<div class="onoffswitch">
							   	 				<input type="checkbox" name="configAccess" class="onoffswitch-checkbox" id="configAccess" checked >
							    				<label class="onoffswitch-label" for="configAccess">
							        				<div class="onoffswitch-inner"></div>
							        				<div class="onoffswitch-switch"></div>
							    				</label>
											</div>
										</div>
									</div>
									
									<!--  Remote Access to Log -->
									<div class="row">
										<div class="4u">
											<span>Allow remote access to log</span>
										</div>
										<div class="8u">
											<div class="onoffswitch">
							   	 				<input type="checkbox" name="logAccess" class="onoffswitch-checkbox" id="logAccess"  checked>
							    				<label class="onoffswitch-label" for="logAccess">
							        				<div class="onoffswitch-inner"></div>
							        				<div class="onoffswitch-switch"></div>
							    				</label>
											</div>
										</div>
									</div>
									
									<!-- Pause between rounds -->
									<div class="row">
										<div class="4u">
											<span>Pause between rounds (minutes)</span>
										</div>
										<div class="1u">
											<input type="text" name="pause" id="pause" placeholder="" value="<%= prop.getProperty("meerkat.monit.test.time") %>"/>
										</div>
									</div>
									
									<!--  Timeline Max. Records -->
									<div class="row">
										<div class="4u">
											<span>Aplication Timeline Max. records</span>
										</div>
										<div class="2u">
											<input type="text" name="timelineMaxRecords" id="timelineMaxRecords" placeholder="" value="<%= prop.getProperty("meerkat.app.timeline.maxrecords") %>"/>
										</div>
									</div>
									
									<!--  Embedded Web Server Port -->
									<!-- 
									<div class="row">
										<div class="4u">
											<span>Embedded web server port<br>(requires restart!)</span>
										</div>
										<div class="1u">
											<input type="text" name="webserverPort" id="webserverPort" placeholder="" value="<%= prop.getProperty("meerkat.webserver.port") %>"/>
										</div>
									</div>
									-->
													
								</section>
							
							
							
								<!--  Email Notifications Settings -->
								<section class="box box-style1">
									<h4><strong>Email Notifications</strong></h4>
									<!-- Email Notifications -->
									<div class="row">
										<div class="4u">
											<span>Email notifications</span>
										</div>
										<div class="8u">
											<div class="onoffswitch">
							   	 				<input type="checkbox" name="sendEmailNotif" class="onoffswitch-checkbox" id="sendEmailNotif" checked>
							    				<label class="onoffswitch-label" for="sendEmailNotif">
							        				<div class="onoffswitch-inner"></div>
							        				<div class="onoffswitch-switch"></div>
							    				</label>
											</div>
										</div>
									</div>
									
									<!-- Email Notification Test -->
									<div class="row">
										<div class="4u">
											<span>Test email at startup</span>
										</div>
										<div class="8u">
											<div class="onoffswitch">
							   	 				<input type="checkbox" name="sendTestEmail" class="onoffswitch-checkbox" id="sendTestEmail" checked>
							    				<label class="onoffswitch-label" for="sendTestEmail">
							        				<div class="onoffswitch-inner"></div>
							        				<div class="onoffswitch-switch"></div>
							    				</label>
											</div>
										</div>
									</div>
									
									<!-- SMTP Server -->
									<div class="row">
										<div class="4u">
											<span>SMTP Server</span>
										</div>
										<div class="4u">
											<input type="text" name="smtp_server" id="smtp_server" placeholder="" value="<%= prop.getProperty("meerkat.email.smtp.server") %>"/>
										</div>
									</div>
									
									<!-- SMTP Port -->
									<div class="row">
										<div class="4u">
											<span>SMTP Port</span>
										</div>
										<div class="1u">
											<input type="text" name="smtp_port" id="smtp_port" placeholder="" value="<%= prop.getProperty("meerkat.email.smtp.port") %>"/>
										</div>
									</div>
									
									<!-- SMTP User -->
									<div class="row">
										<div class="4u">
											<span>SMTP User</span>
										</div>
										<div class="3u">
											<input type="text" name="smtp_user" id="smtp_user" placeholder="" value="<%= prop.getProperty("meerkat.email.smtp.user") %>"/>
										</div>
									</div>
									
									<!-- SMTP Password -->
									<div class="row">
										<div class="4u">
											<span>SMTP Password</span>
										</div>
										<div class="3u">
											<input type="password" name="smtp_password" id="smtp_password" placeholder="" value="<%= prop.getProperty("meerkat.email.smtp.password") %>"/>
										</div>
									</div>
									
									<!-- SMTP Security -->
									<div class="row">
										<div class="4u">
											<span>SMTP Security</span>
										</div>
										<div class="2u">
											<fieldset>
								         		<select name="smtp_security" id="smtp_security" placeholder="" selected="<%= prop.getProperty("meerkat.email.smtp.security").toUpperCase() %>"/>
													<option value = "NONE">NONE</option>
													<option value = "STARTTLS">STARTTLS</option>
													<option value = "SSLTLS">SSL/TLS</option>
												</select>
			       							</fieldset>
										</div>
									</div>
									
									<!-- Email subject prefix -->
									<div class="row">
										<div class="4u">
											<span>Email subject prefix</span>
										</div>
										<div class="4u">
											<input type="text" name="smtp_prefix" id="smtp_prefix" placeholder="" value="<%= prop.getProperty("meerkat.email.subjectPrefix") %>"/>
										</div>
									</div>
									
									<!-- Email from -->
									<div class="row">
										<div class="4u">
											<span>Email FROM address</span>
										</div>
										<div class="4u">
											<input type="text" name="smtp_from" id="smtp_from" placeholder="" value="<%= prop.getProperty("meerkat.email.from") %>"/>
										</div>
									</div>
									
									<!-- Email to -->
									<div class="row">
										<div class="4u">
											<span>Email TO address(es)</span>
										</div>
										<div class="4u">
											<input type="text" name="smtp_to" id="smtp_to" placeholder="" value="<%= prop.getProperty("meerkat.email.to") %>"/>
										</div>
									</div>

								</section>
								
								<input type="submit" class="button" value="Save Settings" />	
								<input type="reset" class="button button-alt" id="testEmail" value="Test Email" />
								<div id="msgbox"></div>
							</form>							
						</section>
						
					</div>
				</article>
			</div>

			<!--  Footer -->
			<%@include file="shared/footer.txt" %>		

	</body>
</html>