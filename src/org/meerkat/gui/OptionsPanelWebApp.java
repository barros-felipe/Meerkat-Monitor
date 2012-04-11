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

import org.meerkat.services.WebApp;
import org.meerkat.webapp.WebAppCollection;
import org.meerkat.webapp.WebAppResponse;

public class OptionsPanelWebApp extends JPanel {

	private static final long serialVersionUID = 797044938033930930L;

	private WebApp webApp;
	private WebAppCollection wAppCollection;
	private MainWindow mainMAppWindow;
	JButton button_save;

	private JLabel label_app_name;
	private JTextField textField_name;
	private JTextField textField_url;
	private JTextField textField_expected_string;
	private JTextField textField_executeOnOffline;
	private JTextField textField_groups;
	private JButton button;
	WebAppResponse testCurrentWebAppResponse;
	private JButton button_test;

	/**
	 * Create the panel.
	 */
	public OptionsPanelWebApp(final WebApp webApplication,
			final WebAppCollection wapCollection, MainWindow mw) {
		this.webApp = webApplication;
		this.wAppCollection = wapCollection;
		this.mainMAppWindow = mw;

		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(null);
		setBounds(260, 11, 524, 545);

		// Name
		label_app_name = new JLabel(webApp.getName());
		label_app_name.setForeground(new Color(128, 0, 0));
		label_app_name.setFont(new Font("Tahoma", Font.PLAIN, 16));
		label_app_name.setBounds(10, 8, 504, 20);
		add(label_app_name);

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

		// Expected String
		JLabel label_expected_string = new JLabel("Expected string");
		label_expected_string.setBounds(10, 89, 102, 14);
		add(label_expected_string);

		textField_expected_string = new JTextField(webApp.getExpectedString());
		textField_expected_string.setBounds(122, 86, 392, 20);
		add(textField_expected_string);
		textField_expected_string.setColumns(10);

		// Execute on offline
		JLabel lblExecuteonoffline = new JLabel("Execute on offline");
		lblExecuteonoffline.setBounds(10, 114, 102, 14);
		add(lblExecuteonoffline);

		textField_executeOnOffline = new JTextField(
				webApp.getExecuteOnOffline());
		textField_executeOnOffline.setBounds(122, 111, 392, 20);
		add(textField_executeOnOffline);
		textField_executeOnOffline.setColumns(10);

		// Groups
		JLabel label_groups = new JLabel("Groups");
		label_groups.setBounds(10, 139, 102, 14);
		add(label_groups);

		textField_groups = new JTextField(webApp.getGroupsListString());
		textField_groups.setToolTipText("Groups separated by comma \",\" ");
		textField_groups.setBounds(122, 136, 392, 20);
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
				String new_expected_string = textField_expected_string
						.getText();
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
					webApp.setExpectedString(new_expected_string);
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
				mainMAppWindow.removeSelectNodeElementFromTree();
				// TODO Remove from file XML
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
				if (testCurrentWebAppResponse.isOnline()) {
					p = new SimplePopup("Result for " + webApp.getName()
							+ "\n\nStatus: Online");
					p.showMsg();
				} else {
					trw = new TestResultWindow("Result for " + webApp.getName()
							+ ": FAILED!", webApp.getCurrentResponse());
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

	public OptionsPanelWebApp() {
	}

}
