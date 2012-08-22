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

package org.meerkat.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.meerkat.services.WebApp;
import org.meerkat.webapp.WebAppCollection;

public class DialogBoxDeleteResetApp extends JDialog {

	private static Logger log = Logger.getLogger(DialogBoxDeleteResetApp.class);
	private static final long serialVersionUID = 8757798906283071588L;
	private final JPanel contentPanel = new JPanel();

	private WebAppCollection wAppCollection;
	private WebApp webApp;
	private MainWindow mainMAppWindow;
	private boolean removeOnlyEvents;

	/**
	 * Launch the application.
	 */
	public final void showUp() {
		try {
			DialogBoxDeleteResetApp dialog = new DialogBoxDeleteResetApp(mainMAppWindow, wAppCollection, webApp, removeOnlyEvents);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		} catch (Exception e) {
			log.error("failed to show frame", e);
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogBoxDeleteResetApp(final MainWindow mainMAppWindow, final WebAppCollection wAppCollection, final WebApp webApp, final boolean removeOnlyEvents) {
		setResizable(false);
		setAlwaysOnTop(true);
		setModal(true); 
		this.wAppCollection = wAppCollection;
		this.mainMAppWindow = mainMAppWindow;
		this.webApp = webApp;
		this.removeOnlyEvents = removeOnlyEvents;

		setTitle("Confirm Delete");
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(
						DialogBoxDeleteResetApp.class
						.getResource("/resources/tray_icon.gif")));
		setBounds(100, 100, 285, 120);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		JLabel lblExitMeerkatMonitor = null;
		
		if(removeOnlyEvents){
			lblExitMeerkatMonitor = new JLabel("Really want to remove all events from application?");
		}else{
			lblExitMeerkatMonitor = new JLabel("Really want to delete application?");
		}
		lblExitMeerkatMonitor.setBounds(10, 23, 259, 14);
		contentPanel.add(lblExitMeerkatMonitor);
		{
			JPanel buttonPane = new JPanel();
			final JButton cancelButton;
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
			{
				final JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						okButton.setEnabled(false);
						cancelButton.setEnabled(false);
						
						if(removeOnlyEvents){
							webApp.removeAllEvents();
							wAppCollection.writeWebAppCollectionDataFile();
							webApp.writeWebAppVisualizationDataFile();
						}else{
							webApp.removeAllEvents();
							wAppCollection.removeWebApp(webApp);
							wAppCollection.writeWebAppCollectionDataFile();
							wAppCollection.saveConfigXMLFile();
							mainMAppWindow.removeSelectNodeElementFromTree();
						}
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			
		}
	}
}
