/**
 * Meerkat Monitor - Network Monitor Tool
 * Copyright (C) 2011 Merkat-Monitor
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

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

public class Mail {

	private static Logger log = Logger.getLogger(Mail.class);
	private String smtp;
	private String security;
	private int port;
	private String smtpUser;
	private String smtpPass;
	private String to;
	private String from;
	private String subject;
	private String message;
	private HtmlEmail email;

	/**
	 * Mail
	 */
	public Mail() {
		super();
	}

	/**
	 * Mail
	 * 
	 * @param smtp
	 *            SMTP Server
	 * @param smtpUser
	 *            SMTP user
	 * @param smtpPass
	 *            SMTP password
	 * @param to
	 *            To
	 * @param from
	 *            From
	 */
	public Mail(String smtp, String smtpUser, String smtpPass, String smtpPort,
			String smtpSecurity, String to, String from) {
		super();
		this.smtp = smtp;
		this.port = Integer.valueOf(smtpPort);
		this.security = smtpSecurity;
		this.to = to;
		this.from = from;
		this.smtpUser = smtpUser;
		this.smtpPass = smtpPass;

		HtmlEmail e = new HtmlEmail();
		email = e;
		email.setHostName(smtp);
		email.setSmtpPort(port);

		// smtp security
		if (security.equalsIgnoreCase("STARTTLS")) {
			email.setTLS(true);
		} else if (security.equalsIgnoreCase("SSL/TLS")) {
			email.setSSL(true);
			email.setSslSmtpPort(String.valueOf(port));
		}

		email.setAuthentication(smtpUser, smtpPass);

		try {
			email.addTo(to);
		} catch (EmailException e1) {
			log.error("EmailException: addTo(" + to + "). ", e1);
		}

		try {
			email.setFrom(from);
		} catch (EmailException e1) {
			log.error("EmailException: setFrom(" + from + "). ", e1);
		}

	}

	/**
	 * getFrom
	 * 
	 * @return From
	 */
	public final String getFrom() {
		return from;
	}

	/**
	 * getMessage
	 * 
	 * @return Message
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * getSmtp
	 * 
	 * @return SMTP
	 */
	public final String getSmtp() {
		return smtp;
	}

	/**
	 * getSmtpPass
	 * 
	 * @return
	 */
	public final String getSmtpPass() {
		return smtpPass;
	}

	/**
	 * getSmtpUser
	 * 
	 * @return SMTP User
	 */
	public final String getSmtpUser() {
		return smtpUser;
	}

	/**
	 * getSubject
	 * 
	 * @return subject
	 */
	public final String getSubject() {
		return subject;
	}

	/**
	 * getTo
	 * 
	 * @return To
	 */
	public final String getTo() {
		return to;
	}

	/**
	 * sendEmail
	 */
	public final void sendEmail() {

		try {
			email.send();
		} catch (EmailException e) {
			log.error("EmailException: send(). ", e);
		}
	}

	/**
	 * setFrom
	 * 
	 * @param from
	 *            From
	 */
	public final void setFrom(String from) {
		this.from = from;
	}

	/**
	 * setMessage
	 * 
	 * @param message
	 *            Message
	 */
	public final void setMessage(String message) {
		this.message = message;
		try {
			email.setMsg(message);
		} catch (EmailException e) {
			log.error("EmailException: setMsg(). ", e);
		}

	}

	/**
	 * setSmtp
	 * 
	 * @param smtp
	 *            SMTP
	 */
	public final void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	/**
	 * setSmtpPass
	 * 
	 * @param smtpPass
	 *            SMTP Password
	 */
	public final void setSmtpPass(String smtpPass) {
		this.smtpPass = smtpPass;
	}

	/**
	 * setSmtpUser
	 * 
	 * @param smtpUser
	 *            SMTP User
	 */
	public final void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	/**
	 * setSubject
	 * 
	 * @param subject
	 *            Subject
	 */
	public final void setSubject(String subject) {
		this.subject = subject;
		email.setSubject(subject);
	}

	/**
	 * setTo
	 * 
	 * @param to
	 *            To
	 */
	public final void setTo(String to) {
		this.to = to;
	}

	/**
	 * testEmailServer
	 */
	public final void testEmailServer() {
		subject = "Meerkat Monitor Notification - Email test";
		message = "This is an test email to check email availability.\n"
				+ "If you're reading this is email, means your email settings are OK.\n"
				+ "\n\n - Meerkat Monitor - ";
		this.setSubject(subject);
		this.setMessage(message);
		this.sendEmail();
	}

}
