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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.meerkat.services.WebService;
import org.meerkat.util.XmlFormatter;
import org.meerkat.webapp.WebAppCollection;
import org.meerkat.webapp.WebAppResponse;

public class OptionsPanelWebService extends JPanel {

	private static final long serialVersionUID = 797044938033930930L;

	private WebService webApp;
	private WebAppCollection wAppCollection;
	private MainWindow mainMAppWindow;
	JButton button_save;

	private JLabel label_webService_name;
	private JTextField textField_name;
	private JTextField textField_url;
	private JTextField textField_soap_action;
	private JTextField textField_executeOnOffline;
	private JTextField textField_groups;
	private JButton button;
	private JButton button_edit_response;
	private JButton button_edit_post;
	WebAppResponse testCurrentWebAppResponse;
	private JButton button_test;

	/**
	 * Create the panel.
	 */
	public OptionsPanelWebService(final WebService webService,
			final WebAppCollection wapCollection, MainWindow mw) {
		this.webApp = webService;
		this.wAppCollection = wapCollection;
		this.mainMAppWindow = mw;

		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(null);
		setBounds(260, 11, 524, 545);

		// Name
		label_webService_name = new JLabel(webApp.getName());
		label_webService_name.setForeground(new Color(128, 0, 0));
		label_webService_name.setFont(new Font("Tahoma", Font.PLAIN, 16));
		label_webService_name.setBounds(10, 8, 504, 20);
		add(label_webService_name);

		JLabel label_name = new JLabel("Name");
		label_name.setBounds(10, 39, 102, 14);
		add(label_name);

		textField_name = new JTextField(webApp.getName());
		textField_name.setBounds(122, 36, 392, 20);
		add(textField_name);
		textField_name.setColumns(10);

		// URL
		JLabel label_url = new JLabel("URL");
		label_url.setBounds(10, 64, 102, 14);
		add(label_url);

		textField_url = new JTextField(webApp.getUrl());
		textField_url.setBounds(122, 61, 392, 20);
		add(textField_url);
		textField_url.setColumns(10);

		// Soap Action
		JLabel label_soap_action = new JLabel("SOAP Action");
		label_soap_action.setBounds(10, 89, 102, 14);
		add(label_soap_action);

		textField_soap_action = new JTextField(webApp.getSOAPAction());
		textField_soap_action.setBounds(122, 86, 392, 20);
		add(textField_soap_action);
		textField_soap_action.setColumns(10);

		// Post XML
		JLabel lbl_post_xml = new JLabel("Post XML");
		lbl_post_xml.setBounds(10, 114, 102, 14);
		add(lbl_post_xml);

		button_edit_post = new JButton("Edit Post");
		button_edit_post.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SimpleTextEditor teditor = new SimpleTextEditor(webApp, webApp
						.getPostXML(), true, false);
				teditor.showUp();
			}
		});
		button_edit_post.setBounds(122, 110, 110, 23);
		add(button_edit_post);

		// Response XML
		JLabel lbl_response_xml = new JLabel("Response XML");
		lbl_response_xml.setBounds(10, 139, 102, 14);
		add(lbl_response_xml);

		button_edit_response = new JButton("Edit Response");
		button_edit_response.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SimpleTextEditor teditor = new SimpleTextEditor(webApp, webApp
						.getResponseXML(), false, true);
				teditor.showUp();
			}
		});
		button_edit_response.setBounds(122, 135, 110, 23);
		add(button_edit_response);

		// Execute on offline
		JLabel lblExecuteonoffline = new JLabel("Execute on offline");
		lblExecuteonoffline.setBounds(10, 164, 102, 14);
		add(lblExecuteonoffline);

		textField_executeOnOffline = new JTextField(
				webApp.getExecuteOnOffline());
		textField_executeOnOffline.setBounds(122, 161, 392, 20);
		add(textField_executeOnOffline);
		textField_executeOnOffline.setColumns(10);

		// Groups
		JLabel label_groups = new JLabel("Groups");
		label_groups.setBounds(10, 189, 102, 14);
		add(label_groups);

		textField_groups = new JTextField(webApp.getGroupsListString());
		textField_groups.setToolTipText("Groups separated by comma \",\" ");
		textField_groups.setBounds(122, 186, 392, 20);
		add(textField_groups);
		textField_groups.setColumns(10);

		// Save button
		button_save = new JButton("Save");
		button_save.setBounds(10, 511, 89, 23);
		add(button_save);

		button_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				button_save.setEnabled(false);

				String new_name = textField_name.getText();
				String new_url = textField_url.getText();
				String new_soap_action = textField_soap_action.getText();
				String new_executeOnOffline = textField_executeOnOffline
						.getText();
				String new_groups = textField_groups.getText();

				if (new_name.equals("") || new_url.equals("")) {
					SimplePopup p = new SimplePopup(
							"Please fill in all required fields!");
					p.show();
					button_save.setEnabled(true);
				} else {
					boolean needHardRefresh = false;
					if (!webApp.getName().equalsIgnoreCase(new_name)) {
						needHardRefresh = true;
					}
					// Save webApp fields
					webApp.setName(new_name);
					webApp.setUrl(new_url);
					webApp.setSOAPAction(new_soap_action);
					webApp.setExecuteOnOffline(new_executeOnOffline);

					new_groups = new_groups.replaceAll(" ", ",");
					webApp.addGroups(new_groups);
					mainMAppWindow.refreshGroupsList();
					if (needHardRefresh) {
						mainMAppWindow.refresh();
					}
					wapCollection.saveConfigXMLFile();
					SimplePopup p = new SimplePopup("Saved!");
					webApp.setActive(true);
					button_test.setEnabled(true);
					p.show();
				}
				button_save.setEnabled(true);
				webApp.writeWebAppVisualizationDataFile();
			}
		});

		// Delete button
		button = new JButton("Delete");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				wAppCollection.removeWebApp(webApp);
				wAppCollection.writeWebAppCollectionDataFile();
				wAppCollection.saveConfigXMLFile();
				mainMAppWindow.removeSelectNodeElementFromTree();
			}
		});
		button.setBounds(425, 511, 89, 23);
		add(button);

		// Test button
		button_test = new JButton("Test");
		final Cursor WAIT_CURSOR = Cursor
				.getPredefinedCursor(Cursor.WAIT_CURSOR);
		final Cursor DEFAULT_CURSOR = Cursor
				.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		button_test.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Disable components
				Component[] components = getComponents();
				for (int i = 0; i < components.length; i++) {
					components[i].setEnabled(false);
				}
				button_test.setText("Running...");
				button_test.setEnabled(false);
				setCursor(WAIT_CURSOR);

				testCurrentWebAppResponse = webApp.checkWebAppStatus();

				SimplePopup p;
				TestResultWindow trw;
				XmlFormatter xmlf = new XmlFormatter();
				if (testCurrentWebAppResponse.isOnline()) {
					p = new SimplePopup("Result for " + webApp.getName()
							+ "\n\nStatus: Online");
					p.showMsg();
				} else {
					String formattedResponse = xmlf.format(webApp.getCurrentResponse());
					trw = new TestResultWindow("Result for " + webApp.getName()
							+ ": FAILED!", formattedResponse);
					trw.showUp();
				}

				testCurrentWebAppResponse = null;

				// Enable components
				button_test.setText("Test");
				button_test.setEnabled(true);
				for (int i = 0; i < components.length; i++) {
					components[i].setEnabled(true);
				}
				button_save.setEnabled(true);
				setCursor(DEFAULT_CURSOR);

			}
		});
		button_test.setBounds(109, 511, 89, 23);
		add(button_test);

		if (!webApp.isActive()) {
			button_test.setEnabled(false);
		}
	}

	public OptionsPanelWebService() {
	}

}
