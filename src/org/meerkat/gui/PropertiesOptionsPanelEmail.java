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
 
package org.meerkat.gui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.meerkat.util.PropertiesLoader;

public class PropertiesOptionsPanelEmail extends JPanel {

	private static final long serialVersionUID = -4770982518031397455L;
	private Properties prop;
	private String propertiesFile = "meerkat.properties";

	private JTextField textField_smtpServer;
	private JTextField textField_subPrefix;
	private JTextField textField_username;
	private JPasswordField textField_password;
	private JTextField textField_to;
	private JTextField textField_from;
	private JComboBox comboBox_security;

	final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	final Cursor DEFAULT_CURSOR = Cursor
			.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private JTextField textField_port;

	private String[] securityTypes = { " None", " STARTTLS", " SSL/TLS", };

	int selectedOpt;
	String securityOption;

	/**
	 * Create the panel.
	 */
	public PropertiesOptionsPanelEmail(final JFrame jfather) {
		final PropertiesLoader pl = new PropertiesLoader();
		prop = pl.getPropetiesFromFile(propertiesFile);

		setBounds(10, 11, 594, 396);
		setLayout(null);

		// Send Emails Notifications
		JLabel lbl_sendEmails = new JLabel("Send Emails Notifications");
		lbl_sendEmails.setBounds(10, 11, 200, 14);
		add(lbl_sendEmails);

		final JCheckBox checkBox_sendEmails = new JCheckBox("");
		checkBox_sendEmails.setBounds(216, 7, 97, 23);
		add(checkBox_sendEmails);

		boolean sendEmailsCheck = Boolean.parseBoolean(prop
				.getProperty("meerkat.email.send.emails"));
		if (sendEmailsCheck) {
			checkBox_sendEmails.setSelected(true);
		}

		// SMTP Server
		JLabel lbl_smtpServer = new JLabel("SMTP Server");
		lbl_smtpServer.setBounds(10, 36, 200, 14);
		add(lbl_smtpServer);

		textField_smtpServer = new JTextField(
				prop.getProperty("meerkat.email.smtp.server"));
		textField_smtpServer.setBounds(216, 33, 200, 20);
		add(textField_smtpServer);
		textField_smtpServer.setColumns(10);

		// Port
		JLabel lbl_Port = new JLabel("Port");
		lbl_Port.setBounds(10, 61, 46, 14);
		add(lbl_Port);

		textField_port = new JTextField(
				prop.getProperty("meerkat.email.smtp.port"));
		textField_port.setBounds(216, 58, 34, 20);
		add(textField_port);
		textField_port.setColumns(10);

		// Security
		comboBox_security = new JComboBox(securityTypes);
		comboBox_security.setBounds(260, 58, 77, 20);
		String stype = prop.getProperty("meerkat.email.smtp.security");

		if (stype.equalsIgnoreCase(securityTypes[0].trim())) {
			comboBox_security.setSelectedIndex(0);
		} else if (stype.equalsIgnoreCase(securityTypes[1].trim())) {
			comboBox_security.setSelectedIndex(1);
		}
		if (stype.equalsIgnoreCase(securityTypes[2].trim())) {
			comboBox_security.setSelectedIndex(2);
		}

		add(comboBox_security);

		// Username
		JLabel lbl_username = new JLabel("Username");
		lbl_username.setBounds(10, 86, 200, 14);
		add(lbl_username);

		textField_username = new JTextField(
				prop.getProperty("meerkat.email.smtp.user"));
		textField_username.setBounds(216, 83, 200, 20);
		add(textField_username);
		textField_username.setColumns(10);

		// Password
		JLabel lbl_password = new JLabel("Password");
		lbl_password.setBounds(10, 111, 200, 14);
		add(lbl_password);

		textField_password = new JPasswordField(
				prop.getProperty("meerkat.email.smtp.password"));
		textField_password.setBounds(216, 108, 200, 20);
		textField_password.setColumns(10);
		add(textField_password);

		// To
		JLabel lbl_to = new JLabel("To");
		lbl_to.setBounds(10, 136, 200, 14);
		add(lbl_to);

		textField_to = new JTextField(prop.getProperty("meerkat.email.to"));
		textField_to.setBounds(216, 133, 200, 20);
		add(textField_to);
		textField_to.setColumns(10);

		// From
		JLabel lbl_from = new JLabel("From");
		lbl_from.setBounds(10, 161, 200, 14);
		add(lbl_from);

		textField_from = new JTextField(prop.getProperty("meerkat.email.from"));
		textField_from.setBounds(216, 158, 200, 20);
		add(textField_from);
		textField_from.setColumns(10);

		// Subject prefix
		JLabel lbl_subPrefix = new JLabel("Subject Prefix");
		lbl_subPrefix.setBounds(10, 186, 200, 14);
		add(lbl_subPrefix);

		textField_subPrefix = new JTextField(
				prop.getProperty("meerkat.email.subjectPrefix"));
		textField_subPrefix.setBounds(216, 183, 200, 20);
		add(textField_subPrefix);
		textField_subPrefix.setColumns(10);

		// Save button
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO validate input!
				prop.setProperty("meerkat.email.send.emails",
						String.valueOf(checkBox_sendEmails.isSelected()));

				selectedOpt = comboBox_security.getSelectedIndex();
				if (selectedOpt == 1) {
					securityOption = "STARTTLS";
				} else if (selectedOpt == 2) {
					securityOption = "SSL/TLS";
				} else {
					securityOption = "none";
				}

				prop.setProperty("meerkat.email.smtp.security", securityOption);

				prop.setProperty("meerkat.email.smtp.port",
						String.valueOf(textField_port.getText()));
				prop.setProperty("meerkat.email.smtp.server",
						textField_smtpServer.getText());
				prop.setProperty("meerkat.email.smtp.user",
						textField_username.getText());
				prop.setProperty("meerkat.email.smtp.password",
						String.valueOf(textField_password.getPassword()));
				prop.setProperty("meerkat.email.to", textField_to.getText());
				prop.setProperty("meerkat.email.from", textField_from.getText());
				prop.setProperty("meerkat.email.subjectPrefix",
						textField_subPrefix.getText());

				pl.writePropertiesToFile(prop, propertiesFile);

				jfather.setAlwaysOnTop(false);
				SimplePopup p = new SimplePopup("Saved!");
				p.show();
				jfather.setAlwaysOnTop(true);

			}
		});
		btnSave.setBounds(396, 362, 89, 23);
		add(btnSave);

		// Cancel button
		JButton btnClose = new JButton("Cancel");
		btnClose.setBounds(495, 362, 89, 23);
		add(btnClose);

		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jfather.dispose();
			}
		});

		// Send email test button
		JButton btnSendTestEmail = new JButton("Send test email");
		btnSendTestEmail.setBounds(10, 362, 105, 23);
		add(btnSendTestEmail);

		btnSendTestEmail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				setCursor(WAIT_CURSOR);
				SimplePopup p;
				boolean sentOK = true;
				try {
					HtmlEmail testEmail = new HtmlEmail();
					testEmail.setHostName(textField_smtpServer.getText());
					testEmail.setSmtpPort(Integer.parseInt(String
							.valueOf(textField_port.getText())));

					selectedOpt = comboBox_security.getSelectedIndex();
					if (selectedOpt == 1) {
						// STARTTLS
						testEmail.setTLS(true);
					} else if (selectedOpt == 2) {
						// SSL/TLS
						testEmail.setSSL(true);
						testEmail.setSslSmtpPort(String.valueOf(textField_port
								.getText()));
					}

					testEmail.setAuthentication(textField_username.getText(),
							String.valueOf(textField_password.getPassword()));

					testEmail.addTo(textField_to.getText());
					testEmail.setFrom(textField_from.getText());

					String subject = "Meerkat Monitor Notification - Email test";
					String message = "This is an test email to check email availability.<br>"
							+ "If you're reading this is email, means your email settings are OK.<br>"
							+ "<br><br>- Meerkat Monitor -";

					testEmail.setMsg(message);
					testEmail.setSocketConnectionTimeout(5000);
					testEmail.setSocketTimeout(5000);
					testEmail.setSubject(subject);

					testEmail.send();

				} catch (EmailException e) {
					sentOK = false;
					jfather.setAlwaysOnTop(false);
					p = new SimplePopup(e.getCause().toString());
					p.setMessageTypeError();
					setCursor(DEFAULT_CURSOR);
					p.showMsg();
					jfather.setAlwaysOnTop(true);
				}

				if (sentOK) {
					p = new SimplePopup("Email sent. Check your inbox.");
					jfather.setAlwaysOnTop(false);
					setCursor(DEFAULT_CURSOR);
					p.showMsg();
					jfather.setAlwaysOnTop(true);
				}

			}
		});

	}
}
