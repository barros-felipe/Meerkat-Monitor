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

import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.meerkat.httpServer.HttpServer;
import org.meerkat.util.MasterKeyManager;

public class PropertiesOptionsMain extends JFrame {

	private static final long serialVersionUID = -2793847259821897518L;
	private JPanel contentPane;
	private HttpServer httpServer;
	private MasterKeyManager mkm;

	/**
	 * Launch the application.
	 */
	public final void showUp() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PropertiesOptionsMain frame = new PropertiesOptionsMain(mkm, httpServer);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PropertiesOptionsMain(MasterKeyManager mkm, HttpServer httpServer) {
		this.mkm = mkm;
		this.httpServer = httpServer;
		setAlwaysOnTop(true);
		setTitle("Settings");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 620, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/resources/tray_icon.gif")));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 594, 430);
		contentPane.add(scrollPane);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		scrollPane.setViewportView(tabbedPane);

		tabbedPane.addTab("General", null, new PropertiesOptionsPanelGeneral(mkm, this, httpServer), null);
		tabbedPane.addTab("Email", null, new PropertiesOptionsPanelEmail(this), null);

	}

}
