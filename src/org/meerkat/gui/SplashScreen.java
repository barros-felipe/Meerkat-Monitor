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

import java.awt.Container;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

public class SplashScreen extends JWindow implements Runnable {

	private static final long serialVersionUID = 3796550626294085195L;
	private static Logger log = Logger.getLogger(SplashScreen.class);
	private String version;

	/**
	 * SplashScreen
	 */
	public SplashScreen(String version) {
		this.version = version;
	}

	@Override
	public void run() {
		Container container = getContentPane();
		getContentPane().setLayout(null);

		JLabel meerkatLogo = new JLabel("");
		meerkatLogo.setBounds(52, 33, 260, 30);
		meerkatLogo.setIcon(new ImageIcon(MainWindow.class
				.getResource("/resources/meerkat.png")));
		container.add(meerkatLogo);

		JLabel meerkatIco = new JLabel("");
		meerkatIco.setBounds(12, 12, 40, 77);
		meerkatIco.setIcon(new ImageIcon(MainWindow.class
				.getResource("/resources/meerkat-small.png")));
		container.add(meerkatIco);

		JLabel lblNewLabel = new JLabel(version, SwingConstants.RIGHT);

		lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblNewLabel.setBounds(182, 74, 126, 15);
		getContentPane().add(lblNewLabel);

		setSize(320, 100);
		setLocationRelativeTo(null);
		setVisible(true);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			log.error("SplashScreen thread sleep error!");
		}
		dispose();

	}

}
