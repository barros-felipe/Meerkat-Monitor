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
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.meerkat.services.WebApp;
import org.meerkat.services.WebService;
import org.meerkat.util.XmlFormatter;

public class SimpleTextEditor extends JFrame {

	private static Logger log = Logger.getLogger(SimpleTextEditor.class);
	private static final long serialVersionUID = -8094713322132865605L;
	private JPanel contentPane;
	private String text;
	private boolean xmlValid = true;
	private WebApp wa;
	private boolean editWebServicePost = false;
	private boolean editWebServiceResponse = false;

	/**
	 * Launch the application.
	 */
	public final void showUp() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimpleTextEditor frame = new SimpleTextEditor(wa, text,
							editWebServicePost, editWebServiceResponse);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					log.error("failed to show frame.", e);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SimpleTextEditor(WebApp webApp, String textToEdit,
			final Boolean editWebServicePost,
			final Boolean editWebServiceResponse) {
		this.wa = webApp;
		this.editWebServicePost = editWebServicePost;
		this.editWebServiceResponse = editWebServiceResponse;

		setAlwaysOnTop(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				SimpleTextEditor.class.getResource("/resources/tray_icon.gif")));
		this.text = textToEdit;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(5, 5, 774, 646);
		contentPane.add(panel);
		panel.setLayout(null);

		// App editing
		JLabel lblAppPostresponsexml = new JLabel("");
		lblAppPostresponsexml.setBounds(10, 11, 601, 14);
		panel.add(lblAppPostresponsexml);

		XmlFormatter xmlf = new XmlFormatter();
		String formattedXML;
		try {
			formattedXML = xmlf.format(text);
		} catch (Exception e) {
			formattedXML = text;
			xmlValid = false;
		}

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 36, 754, 564);
		panel.add(scrollPane);

		final JEditorPane editorPane = new JEditorPane();
		scrollPane.setViewportView(editorPane);
		editorPane.setText(formattedXML);

		// Save button
		JButton button_1 = new JButton("Save");
		button_1.setBounds(576, 612, 89, 23);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String editorContents = editorPane.getText();
				if (editWebServicePost) {
					((WebService) wa).setPostXML(editorContents);
				} else if (editWebServiceResponse) {
					((WebService) wa).setResponseXML(editorContents);
				}

				dispose();
			}
		});
		panel.add(button_1);

		// Simple top notification area
		JLabel label = new JLabel("Valid XML");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setForeground(Color.LIGHT_GRAY);
		label.setBounds(621, 11, 143, 14);
		panel.add(label);
		if (!xmlValid) {
			label.setText("Invalid XML file!");
			label.setForeground(Color.RED);
			label.repaint();
		}

		// Cancel button
		JButton button = new JButton("Cancel");
		button.setBounds(675, 612, 89, 23);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel.add(button);

	}
}
