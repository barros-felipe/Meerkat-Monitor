/**
 * Meerkat Monitor - Network Monitor Tool
 * Copyright (C) 2012 Merkat-Monitor
 * mailto: contact AT meerkat-monitor DOT org
 * 
 * Meerkat Monitor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Meerkat Monitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with Meerkat Monitor.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.meerkat.network;

import java.util.Properties;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.meerkat.util.PropertiesLoader;

public class MailManager {
	private static Logger log = Logger.getLogger(MailManager.class);
	private String propertiesFile;
	private Properties prop;
	
	private String testSubject = "Meerkat Monitor Notification - Email test";
	private String testMessage = "This is an test email to check email availability.\n"
			+ "If you're reading this is email, means your email settings are OK.\n"
			+ "\n\n - Meerkat Monitor - ";
	

	public MailManager(String propertiesFile){
		this.propertiesFile = propertiesFile;
		refreshSettings();
	}
	
	public MailManager(){
		
	}

	private final void refreshSettings(){
		PropertiesLoader pL = new PropertiesLoader(propertiesFile);
		prop = pL.getPropetiesFromFile();
	}

	private String getSMTPServer(){
		return prop.getProperty("meerkat.email.smtp.server");
	}

	private String getSMTPUser(){
		return prop.getProperty("meerkat.email.smtp.user");
	}

	private String getSMTPPassword(){
		return prop.getProperty("meerkat.email.smtp.password");
	}

	private String getSMTPPort(){
		return prop.getProperty("meerkat.email.smtp.port");
	}

	private String getSMTPSecurity(){
		return prop.getProperty("meerkat.email.smtp.security");
	}

	private String getTO(){
		return prop.getProperty("meerkat.email.to");
	}

	private String getFROM(){
		return prop.getProperty("meerkat.email.from");
	}

	private String getSubject(){
		return prop.getProperty("meerkat.email.subjectPrefix");
	}

	/**
	 * sendEmail
	 * @param subject
	 * @param message
	 */
	public final void sendEmail(String subject, String message){
		this.refreshSettings();

		HtmlEmail email = new HtmlEmail();
		email.setHostName(getSMTPServer());
		email.setSmtpPort(Integer.valueOf(getSMTPPort()));
		email.setSubject(subject);
		try {
			email.setHtmlMsg(message);
		} catch (EmailException e2) {
			log.error("Error in mail message. ", e2);
		}

		// SMTP security
		String security = getSMTPSecurity();
		if (security.equalsIgnoreCase("STARTTLS")) {
			email.setTLS(true);
		} else if (security.equalsIgnoreCase("SSL/TLS")) {
			email.setSSL(true);
			email.setSslSmtpPort(String.valueOf(getSMTPPort()));
		}
		email.setAuthentication(getSMTPUser(), getSMTPPassword());

		try {
			String[] toList = getTO().split(",");
			for(int i=0; i<toList.length; i++){
				email.addTo(toList[i].trim());
			}

		} catch (EmailException e1) {
			log.error("EmailException: addTo(" + getTO() + "). "+e1.getMessage());
		}

		try {
			email.setFrom(getFROM());
		} catch (EmailException e1) {
			log.error("EmailException: setFrom(" + getFROM() + "). "+e1.getMessage());
		}

		// Send the email
		try {
			email.send();
		} catch (EmailException e) {
			log.error("Failed to send email!", e);
		}
	}

	/**
	 * sendEmail
	 * @param message
	 */
	public final void sendEmail(String message){
		sendEmail(getSubject(), message);
	}

	/**
	 * testEmailSettings
	 */
	public final void sendTestEmail() {
		log.info("Sending test email to: "+getTO());
		sendEmail(testSubject, testMessage);
	}
	
	
	/**
	 * testEmailSettingsFromWebService
	 * @param from
	 * @param to
	 * @param smtpServer
	 * @param smtpPort
	 * @param smtpSecurity
	 * @param smtpUser
	 * @param smtpPassword
	 * @return
	 */
	public final String sendTestEmailSettingsFromWebService(String from, String to, String smtpServer, String smtpPort, String smtpSecurity,
			String smtpUser, String smtpPassword){
		String resultString = "OK";
		HtmlEmail email = new HtmlEmail();
		email.setHostName(smtpServer);
		email.setSmtpPort(Integer.valueOf(smtpPort));
		email.setSubject(testSubject);
		try {
			email.setHtmlMsg(testMessage);
		} catch (EmailException e2) {
			resultString = e2.getMessage();
			return resultString;
		}

		// SMTP security
		if (smtpSecurity.equalsIgnoreCase("STARTTLS")) {
			email.setTLS(true);
		} else if (smtpSecurity.equalsIgnoreCase("SSLTLS")) {
			email.setSSL(true);
			email.setSslSmtpPort(String.valueOf(smtpPort));
		}
		email.setAuthentication(smtpUser, smtpPassword);

		try {
			String[] toList = to.split(",");
			for(int i=0; i<toList.length; i++){
				email.addTo(toList[i].trim());
			}

		} catch (EmailException e1) {
			resultString = "TO: "+e1.getMessage();
			return resultString;
		}

		try {
			email.setFrom(from);
		} catch (EmailException e1) {
			resultString = "FROM: "+e1.getMessage();
			return resultString;
		}

		// Send the email
		try {
			email.send();
		} catch (EmailException e) {
			resultString = e.getMessage();
			return resultString;
		}
		
		return resultString;
	}

	
	
	
}
