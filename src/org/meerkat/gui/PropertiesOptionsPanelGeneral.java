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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.meerkat.httpServer.HttpServer;
import org.meerkat.util.MasterKeyManager;
import org.meerkat.util.PropertiesLoader;

public class PropertiesOptionsPanelGeneral extends JPanel {

	private static final long serialVersionUID = -4770982518031397455L;
	private static Logger log = Logger.getLogger(PropertiesOptionsPanelGeneral.class);
		
	private Properties prop;
	private JTextField textField_pauseMinutes;
	private JTextField textField_webServerPort;
	private JPasswordField textField_masterPasswd;
	private String propertiesFile = "meerkat.properties";

	private String test_time;
	private String serverPort;
	private boolean saveSessionExit;
	private boolean loadSessionStart;
	private boolean showGroupsGauge;

	private HttpServer httpServer;

	/**
	 * Create the panel.
	 */
	public PropertiesOptionsPanelGeneral(final MasterKeyManager mkm, final JFrame jfather, final HttpServer hserver) {
		this.httpServer = hserver;
		final PropertiesLoader pl = new PropertiesLoader();
		prop = pl.getPropetiesFromFile(propertiesFile);

		setBounds(10, 11, 594, 396);
		setLayout(null);

		JLabel lblMinutesBetweenTests = new JLabel("Check interval (minutes)");
		lblMinutesBetweenTests.setBounds(10, 11, 200, 14);
		add(lblMinutesBetweenTests);

		test_time = prop.getProperty("meerkat.monit.test.time");
		textField_pauseMinutes = new JTextField(test_time);
		textField_pauseMinutes.setBounds(220, 8, 86, 20);
		add(textField_pauseMinutes);
		textField_pauseMinutes.setColumns(10);

		// Webserver Port
		JLabel lblWebServerPort = new JLabel("Web Server Port (must be > 1024)");
		lblWebServerPort.setBounds(10, 36, 200, 14);
		add(lblWebServerPort);

		serverPort = prop.getProperty("meerkat.webserver.port");
		textField_webServerPort = new JTextField(serverPort);
		textField_webServerPort.setBounds(220, 33, 86, 20);
		add(textField_webServerPort);
		textField_webServerPort.setColumns(10);

		JLabel lblNewLabel = new JLabel("(Requires restart!)");
		lblNewLabel.setBounds(316, 36, 105, 14);
		add(lblNewLabel);

		// Save session on Exit
		JLabel lblAutoSaveOn = new JLabel("Save session on Exit");
		lblAutoSaveOn.setBounds(10, 63, 200, 14);
		add(lblAutoSaveOn);

		final JCheckBox checkBox_saveSessionExit = new JCheckBox("");
		checkBox_saveSessionExit.setBounds(216, 59, 97, 23);

		saveSessionExit = Boolean.parseBoolean(prop
				.getProperty("meerkat.autosave.exit"));
		if (saveSessionExit) {
			checkBox_saveSessionExit.setSelected(true);
		}
		add(checkBox_saveSessionExit);

		// Load session on Start
		JLabel lblLoadSessionOn = new JLabel("Load session on Start");
		lblLoadSessionOn.setBounds(10, 88, 200, 14);
		add(lblLoadSessionOn);

		final JCheckBox checkBox_loadSessionStart = new JCheckBox();
		checkBox_loadSessionStart.setBounds(216, 84, 97, 23);
		add(checkBox_loadSessionStart);

		loadSessionStart = Boolean.parseBoolean(prop.getProperty("meerkat.autoload.start"));
		if (loadSessionStart) {
			checkBox_loadSessionStart.setSelected(true);
		}
		add(checkBox_loadSessionStart);

		// Groups Gauge
		JLabel lblGroupsGauge = new JLabel("Groups Gauge");
		lblGroupsGauge.setBounds(10, 113, 200, 14);
		add(lblGroupsGauge);

		final JCheckBox checkBox_showGroupsGauge = new JCheckBox();
		checkBox_showGroupsGauge.setBounds(216, 109, 97, 23);
		add(checkBox_showGroupsGauge);

		showGroupsGauge = Boolean.parseBoolean(prop.getProperty("meerkat.dashboard.gauge"));
		if (showGroupsGauge) {
			checkBox_showGroupsGauge.setSelected(true);
		}
		add(checkBox_showGroupsGauge);

		// Allow remote access config
		JLabel lblAllowRemoteAccess = new JLabel("Allow remote access config");
		lblAllowRemoteAccess.setBounds(10, 138, 200, 14);
		add(lblAllowRemoteAccess);

		final JCheckBox checkBox_remoteconfig = new JCheckBox();
		checkBox_remoteconfig.setBounds(216, 134, 97, 23);
		add(checkBox_remoteconfig);

		boolean remoteconfig = Boolean.parseBoolean(prop.getProperty("meerkat.webserver.rconfig"));
		if (remoteconfig) {
			checkBox_remoteconfig.setSelected(true);
		}
		add(checkBox_remoteconfig);

		// Master Key (encrypt passwords)
		JLabel lblMasterPasswordencrypt = new JLabel("Master Key (encrypt passwords)");
		lblMasterPasswordencrypt.setBounds(10, 163, 200, 14);
		add(lblMasterPasswordencrypt);

		//textField_masterPasswd = new JPasswordField(prop.getProperty("meerkat.password.master"));
		textField_masterPasswd = new JPasswordField(mkm.getMasterKey());
		textField_masterPasswd.setBounds(220, 160, 86, 20);
		add(textField_masterPasswd);
		textField_masterPasswd.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("(Visible in meerkat.properties)");
		lblNewLabel_1.setBounds(316, 163, 159, 14);
		add(lblNewLabel_1);

		final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		// Save button
		final JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				btnSave.setEnabled(false);
				setCursor(WAIT_CURSOR);
				
				// TODO validate input!
				prop.setProperty("meerkat.monit.test.time", textField_pauseMinutes.getText());
				prop.setProperty("meerkat.webserver.port", textField_webServerPort.getText());
				prop.setProperty("meerkat.autosave.exit", String.valueOf(checkBox_saveSessionExit.isSelected()));
				prop.setProperty("meerkat.autoload.start", String.valueOf(checkBox_loadSessionStart.isSelected()));
				prop.setProperty("meerkat.dashboard.gauge", String.valueOf(checkBox_showGroupsGauge.isSelected()));
				prop.setProperty("meerkat.webserver.rconfig", String.valueOf(checkBox_remoteconfig.isSelected()));
				//prop.setProperty("meerkat.password.master", String.valueOf(textField_masterPasswd.getPassword()));
				log.info("PREPARING TO SAVE MKM...");
				mkm.changeMasterKey(String.valueOf(textField_masterPasswd.getPassword()));
				log.info("DONE!");	
				
				httpServer.refreshIndex();
				jfather.setAlwaysOnTop(false);
				
				btnSave.setEnabled(true);
				setCursor(DEFAULT_CURSOR);
				
				SimplePopup p = new SimplePopup("Saved!");
				p.show();
				jfather.setAlwaysOnTop(true);

			}
		});
		btnSave.setBounds(396, 362, 89, 23);
		add(btnSave);

		// Close button
		JButton btnClose = new JButton("Close");
		btnClose.setBounds(495, 362, 89, 23);
		add(btnClose);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jfather.dispose();
			}
		});

	}
}
