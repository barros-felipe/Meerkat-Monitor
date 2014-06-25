/**
 * Meerkat Monitor - Network Monitor Tool
 * Copyright (C) 2013 Merkat-Monitor
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
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.meerkat.MeerkatMonitor;

public class SplashScreen extends JWindow {

	private static final long serialVersionUID = 1L;
	private BorderLayout borderLayout;
	private JLabel imageLabel;
	private JLabel lblNewLabel;
	private JProgressBar progressBar = new JProgressBar(0, 100);
	private ImageIcon imageIcon = new ImageIcon(MeerkatMonitor.class.getClass().getResource("/resources/splashscreen.png"));

	public SplashScreen(String version) {
		imageLabel = new JLabel();
		imageLabel.setIcon(imageIcon);
		borderLayout = new BorderLayout();
		lblNewLabel = new JLabel(version, SwingConstants.RIGHT);
        lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblNewLabel.setBounds(182, 74, 126, 15);
		setLayout(borderLayout);
		add(imageLabel, BorderLayout.CENTER);
		add(lblNewLabel, BorderLayout.NORTH);
		add(progressBar, BorderLayout.SOUTH);
        
		pack();
		setLocationRelativeTo(null);
	}


	/**
	 * showScreen
	 */
	public void showScreen() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setVisible(true);
			}
		});
	}

	/**
	 * close
	 */
	public void close() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setVisible(false);
				dispose();
			}
		});
	}

	/**
	 * setProgress
	 * @param message
	 * @param progress
	 */
	public void setProgress(final String message, final int progress) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue(progress);
				if (message == null) {
					progressBar.setStringPainted(false);
				} else {
					progressBar.setStringPainted(true);
				}
				progressBar.setString(message);
			}
		});
	}
}