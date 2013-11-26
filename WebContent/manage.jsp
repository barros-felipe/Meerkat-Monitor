<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@include file="shared/validate-session.jsp" %>
<%@ page import="org.meerkat.config.UserInfo"%>
<%@ page import="org.meerkat.config.AppsManagement"%>
<!DOCTYPE HTML>
<html>
	<head>
		<title>Meerkat-Monitor - Manage</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<%@include file="shared/javascript-imports.txt" %>
		<%@include file="shared/css-imports.txt" %>
		<link rel="stylesheet" type="text/css" href="css/onoffswitch.css">
		<link rel="stylesheet" type="text/css" href="css/buttons-custom.css">
		
		
		<script type="text/javascript">
			function setActiveApp(appName, active){
			   this.timer = setTimeout(function () {
					$.ajax({
						url: '_manage_oper_active_apps.jsp',
						data: 'appName='+appName+
							'&active='+active+
							'&setStatus=true',
						type: 'post',
						success: function(msg){
							if(msg.indexOf("OK") == -1){
								alert("setActiveApp(): "+msg);
							}
						}
					});
				}, 200);
			}
		</script>	
		
		<script type="text/javascript">
			$(document).ready(function(){
				/** Add new listener */
				$("#addnew").change(function() {
					var addnewType = $("#addnew").val();
					if(addnewType.indexOf("WEBAPP") !== -1){
						window.location = "add-webapp.jsp";
					}else if(addnewType.indexOf("SSH") !== -1){
						window.location = "add-ssh.jsp";
					}else if(addnewType.indexOf("SOCKET") !== -1){
						window.location = "add-socket.jsp";
					}else if(addnewType.indexOf("DATABASE") !== -1){
						window.location = "add-database.jsp";
					}else if(addnewType.indexOf("WEBSERVICE") !== -1){
						window.location = "add-webservice.jsp";
					}
				});
				
				<!-- Change Masterkey -->
				$("#changeMasterKey").click(function(e){
					window.location = "manage-changemasterkey.jsp";
				});
				<!-- Delete all application button -->
				$("#deleteAllApps").click(function(e){
					window.location = "manage-deleteAllApps.jsp";
				});
				<!-- Delete all events button -->
				$("#deleteAllEvents").click(function(e){
					window.location = "manage-deleteAllEvents.jsp";
				});
				<!-- Shutdown Meerkat-Monitor instance -->
				$("#shutdown").click(function(e){
					window.location = "manage-shutdown.jsp";
				});
				
				/** Enable/Disable switch */
				<%
					UserInfo userInfo = (UserInfo)session.getAttribute("UserInfo");
					if(userInfo == null || !userInfo.isValid()){
						session.invalidate();
						return;
					}
					AppsManagement appManager = new AppsManagement(userInfo);
					String appList = appManager.getAppList();
									
					String[] apps = appList.split("\\,");
					for(int i=0; i< apps.length; i++){
						String currAppName = apps[i].trim().replaceAll(" ", "_");
						out.println("				$(\"#active_"+currAppName+"\").change(function() {");
						out.println("					var app_active_"+currAppName+" = $(\"#active_"+currAppName+"\").is(\":checked\");");
						out.println("					setActiveApp('"+currAppName+"', app_active_"+currAppName+");");
						out.println("				});");
						out.println("				$(\"#active_"+currAppName+"\").attr('checked', "+appManager.isAppActive(apps[i].trim())+");");
					}
				%>
				
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
						<span>Manage Applications</span>
					</header>
					<div class="5grid-layout">
						<section class="box box-style1">

							<!--  General Settings -->
							<section class="box box-style1">
								<form name="settings" id="settings" action="" method="post">
									<h4><strong>General Operations</strong></h4>
									
									<!-- Add new -->
									<div class="row">
										<div class="4u">
											<span>Add new application</span>
										</div>
										<div class="2u">
											<fieldset>
												<select name="addnew" id="addnew" placeholder="" selected="-select-"/>
													<option value = "-select-">- select -</option>
													<option value = "WEBAPP">WEB APP</option>
													<option value = "SSH">SSH</option>
													<option value = "SOCKET">SOCKET</option>
													<option value = "DATABASE">DATABASE</option>
													<option value = "WEBSERVICE">WEBSERVICE</option>
												</select>
								       		</fieldset>
										</div>
									</div>
								
									<!-- Change Masterkey -->
									<div class="row">
										<div class="4u">
											<span></span>
										</div>
										<div class="2u">
											<input type="reset" class="button" id="changeMasterKey" value="Change MasterKey       " />
										</div>
									</div>
									
									<!-- Delete all applications -->
									<div class="row">
										<div class="4u">
											<span></span>
										</div>
										<div class="2u">
											<input type="reset" class="button" id="deleteAllApps" value="Delete all applications" />
										</div>
									</div>
										
									<!-- Delete all records -->
									<div class="row">
										<div class="4u">
											<span></span>
										</div>
										<div class="2u">
											<input type="reset" class="button" id="deleteAllEvents" value="Delete all events           " />
										</div>
									</div>	
									
									<!-- Shutdown -->
									<div class="row">
										<div class="4u">
											<span></span>
										</div>
										<div class="2u">
											<input type="reset" class="button-alt button" id="shutdown" value="Shutdown" />
										</div>
									</div>						
								</form>
								<div id="msgbox"></div>				
							</section>
								
								
							<!--  Manage Existing Applications -->
							<section class="box box-style1">
								<h4><strong>Manage Applications</strong></h4>
								
								<!--  <div class="row"> -->
									<!-- <div class="6u"> -->
										
										<table class="zebra">
										    <thead>
											    <tr>
											    	<th>#</th>
											        <th>App</th>        
											        <th>Active</th>
											        <th>Clear events</th>
											        <th>Delete</th>
											    </tr>
										    </thead>
										    <tfoot>
											    <tr>
											        <td>&nbsp;</td> 
											        <td></td>       
											        <td></td>
											        <td></td>
											        <td></td>
											    </tr>
										    </tfoot>    
										    	<%
													for(int z=0; z< apps.length; z++){
													String appName = apps[z].trim().replaceAll(" ", "_");
														out.println("												<tr>");
														out.println("													<td>"+(z+1)+"</td>");
														out.println("													<td>");
														out.println("														"+apps[z].trim());
														out.println("													</td>");
											
														out.println("													<td>");
														out.println("														<div class=\"onoffswitch\">");
												   	 	out.println("															<input type=\"checkbox\" name=\"appType\" class=\"onoffswitch-checkbox\" id=\"active_"+appName+"\" checked >");
												    	out.println("															<label class=\"onoffswitch-label\" for=\"active_"+appName+"\">");
												        out.println("																<div class=\"onoffswitch-inner\"></div>");
												        out.println("																<div class=\"onoffswitch-switch\"></div>");
												    	out.println("															</label>");
														out.println("														</div>");
														out.println("													</td>");
										
														out.println("													<td><input type=\"button\" value=\"\" class=\"button_clear_events\" onclick=\"JavaScript:window.location.href = 'manage-app-deleteEvents.jsp?appname="+apps[z].trim()+"'\"/></td>");
														out.println("													<td><input type=\"button\" value=\"\" class=\"button_delete\" onclick=\"JavaScript:window.location.href = 'manage-app-deleteApp.jsp?appname="+apps[z].trim()+"'\"/></td>");
														out.println("												</tr>");
													}
												%>       
										</table>
																				
									<!--  </div> -->
								<!--  </div> -->
							</section>
								
												
						</section>
						
					</div>
				</article>
			</div>
			
			<!--  Footer -->
			<%@include file="shared/footer.txt" %>		

	</body>
</html>