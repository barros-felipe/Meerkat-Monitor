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

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

import org.apache.log4j.Logger;

public class DialogBoxAbout extends JFrame {
	private static Logger log = Logger.getLogger(DialogBoxAbout.class);
	private static final long serialVersionUID = -6537445587806159092L;
	private JPanel contentPane;

	private String version = "";
	private String header = "<body><div align=\"center\"><strong>Meerkat Monitor</font> - Network Monitor Tool</strong>";
	private String content = "Copyright (C) 2012 Merkat-Monitor.org<br>"+
			"<a href=\"mailto:contact@meerkat-monitor.org\">contact@meerkat-monitor.org</a><br>"+ 
			"<a href=\"http://meerkat-monitor.org/\">http://meerkat-monitor.org/</a><br>"+
			"</div><br>"+
			"Meerkat Monitor is free software: you can redistribute it and/or modify "+
			"it under the terms of the GNU Lesser General Public License as published by "+
			"the Free Software Foundation, either version 3 of the License, or "+
			"(at your option) any later version."+
			"<br><br>"+
			"Meerkat Monitor is distributed in the hope that it will be useful, "+
			"but WITHOUT ANY WARRANTY; without even the implied warranty of "+
			"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the "+
			"GNU Lesser General Public License for more details."+
			"<br><br>"+ 
			"You should have received a copy of the GNU Lesser General Public License "+
			"along with Meerkat Monitor.  If not, see http://www.gnu.org/licenses/.</body>";

	/**
	 * Launch the application.
	 */
	public void showUp() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DialogBoxAbout frame = new DialogBoxAbout(version);
					frame.setLocationRelativeTo(null);
					frame.setResizable(false);
					frame.setAlwaysOnTop(true);
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
	public DialogBoxAbout(String version) {
		setTitle("About");
		this.version = version;
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				SimpleTextEditor.class.getResource("/resources/tray_icon.gif")));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel meerkatLogo = new JLabel("");
		meerkatLogo.setBounds(43, 0, 45, 65);
		meerkatLogo.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/meerkat-small.png")));
		contentPane.add(meerkatLogo);

		JLabel meerkatLogoText = new JLabel("");
		meerkatLogoText.setBounds(86, 17, 260, 30);
		meerkatLogoText.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/meerkat.png")));
		contentPane.add(meerkatLogoText);

		// OK button
		JButton btnNewButton = new JButton("OK");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnNewButton.setBounds(143, 339, 91, 23);
		contentPane.add(btnNewButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBounds(10, 64, 372, 264);
		contentPane.add(scrollPane);
		scrollPane.setBorder(null);
		scrollPane.setBackground(Color.WHITE);

		JEditorPane editorPane = new JEditorPane();
		editorPane.setFont(new Font("Arial", Font.PLAIN, 12));
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");

		// Some CSS
		Font font = UIManager.getFont("Label.font");
		String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }";
		((HTMLDocument)editorPane.getDocument()).getStyleSheet().addRule(bodyRule);
		editorPane.setOpaque(false);
		editorPane.setBorder(null);

		editorPane.setText(header+" v."+version+"<br>"+content);
		scrollPane.setViewportView(editorPane);
		
		// Handle hyperlinks
		HyperlinkListener l = new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
		          if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
		                try {
		                	if( !java.awt.Desktop.isDesktopSupported() ) {
		                        log.error("Desktop is not supported. Cannot open browser with URL");
		                    }else{
		                    	java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
		                    	desktop.browse(e.getURL().toURI());
		                    }
		                } catch (Exception e1) {
		                    log.error("Failed to open link!", e1);
		                }
		            }
			}

	    };
	    editorPane.addHyperlinkListener(l);

		
	}
}
