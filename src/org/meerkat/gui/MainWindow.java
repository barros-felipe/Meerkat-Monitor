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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.meerkat.group.AppGroupCollection;
import org.meerkat.httpServer.HttpServer;
import org.meerkat.services.SQLService;
import org.meerkat.services.SecureShellSSH;
import org.meerkat.services.SocketService;
import org.meerkat.services.WebApp;
import org.meerkat.services.WebServiceApp;
import org.meerkat.util.MasterKeyManager;
import org.meerkat.webapp.WebAppCollection;

public class MainWindow {

	private static Logger log = Logger.getLogger(MainWindow.class);
	private JFrame frmMeerkatMonitor;
	private WebAppCollection webCollection;
	private AppGroupCollection groupsCollection;
	private HttpServer httpServer;
	private MasterKeyManager mkm;
	private JTree appTree;
	private JTabbedPane tabbedPane;
	private JPanel appListJpanelContainer;
	private DefaultTreeModel meerkatModel;
	private DefaultMutableTreeNode selectedNode;
	private OptionsPanelWebApp appOptionsPanel = new OptionsPanelWebApp();
	private OptionsPanelWebService wsOptionsPanel = new OptionsPanelWebService();
	private OptionsPanelDataBase sqlOptionsPanel = new OptionsPanelDataBase();
	private OptionsPanelSocketService socketServiceOptionsPanel = new OptionsPanelSocketService();
	private OptionsPanelSSH sshOptionsPanel = new OptionsPanelSSH();
	private static MainWindow window;
	private DefaultMutableTreeNode root;

	DefaultMutableTreeNode groupWebApps;
	DefaultMutableTreeNode groupWebServices;
	DefaultMutableTreeNode groupDatabases;
	DefaultMutableTreeNode groupSocketServices;
	DefaultMutableTreeNode groupSSHServices;

	/**
	 * Launch the application.
	 */
	public final void showUp() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new MainWindow(mkm, webCollection, groupsCollection,httpServer);
					window.frmMeerkatMonitor.setLocationRelativeTo(null);
					window.frmMeerkatMonitor.setVisible(true);
				} catch (Exception e) {
					log.error("Failed to show GUI", e);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow(MasterKeyManager mkm, WebAppCollection appCollection, AppGroupCollection groupsCollection, HttpServer httpServer) {
		this.mkm = mkm;
		this.webCollection = appCollection;
		this.groupsCollection = groupsCollection;
		this.httpServer = httpServer;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private final void initialize() {

		frmMeerkatMonitor = new JFrame();
		frmMeerkatMonitor.setTitle("Meerkat Monitor - Network Monitor Tool");
		frmMeerkatMonitor.setResizable(false);
		frmMeerkatMonitor.setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/resources/tray_icon.gif")));
		frmMeerkatMonitor.setBounds(100, 100, 800, 700);
		frmMeerkatMonitor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmMeerkatMonitor.setJMenuBar(menuBar);
		frmMeerkatMonitor.getContentPane().setLayout(null);
		KeyStroke keystrokeF5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false);
		KeyStroke keystrokeF6 = KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0, false);
		KeyStroke keystrokeF10 = KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0, false);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		// Close window menu item
		JMenuItem menuItemClose = new JMenuItem("Close");
		menuItemClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmMeerkatMonitor.dispose();
			}
		});
		mnFile.add(menuItemClose);

		// Shutdown Meerkat
		JMenuItem menuItemShutdown = new JMenuItem("Shutdown Meerkat");
		menuItemShutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ShutdownDialogBox sdb = new ShutdownDialogBox();
				sdb.showUp();
			}
		});
		mnFile.add(menuItemShutdown);

		JMenu mnWindow = new JMenu("Window");
		menuBar.add(mnWindow);

		// Refresh menu item
		JMenuItem mntmRefreshAppList = new JMenuItem("Refresh applications");
		mntmRefreshAppList.setAccelerator(keystrokeF5);
		mntmRefreshAppList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		mnWindow.add(mntmRefreshAppList);

		// Open web dashboard
		JMenuItem mntmOpenWebDashboard = new JMenuItem("Open Dashboard");
		mntmOpenWebDashboard.setAccelerator(keystrokeF6);
		mntmOpenWebDashboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openUrl(httpServer.getServerUrl());
			}
		});
		mnWindow.add(mntmOpenWebDashboard);

		// Options
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);

		JMenuItem mntmProperties = new JMenuItem("Settings");
		mntmProperties.setAccelerator(keystrokeF10);
		mntmProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PropertiesOptionsMain ped = new PropertiesOptionsMain(mkm, httpServer);
				ped.showUp();
			}
		});
		mnOptions.add(mntmProperties);

		// Help
		JMenu mnAbout = new JMenu("Help");
		menuBar.add(mnAbout);

		// About
		JMenuItem mntmAbout = new JMenuItem("About");
		mnAbout.add(mntmAbout);

		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AboutDialog ab = new AboutDialog(webCollection.getAppVersion());
				ab.showUp();
			}
		});

		appListJpanelContainer = new JPanel();
		appListJpanelContainer.setBounds(0, 49, 794, 556);
		frmMeerkatMonitor.getContentPane().add(appListJpanelContainer);
		appListJpanelContainer.setLayout(null);

		// Tabbed pane to contain app views
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		// tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setBounds(10, 11, 245, 545);
		appListJpanelContainer.add(tabbedPane);

		appOptionsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,null));
		appOptionsPanel.setBounds(260, 11, 524, 545);
		appListJpanelContainer.add(appOptionsPanel);
		appOptionsPanel.setLayout(null);

		// Meerkat Monitor logo
		appOptionsPanel.add(mainFrameAppLogo());
		appOptionsPanel.add(mainFrameAppVersion());
		appOptionsPanel.add(mainFrameAppIcon());

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(10, 4, 774, 45);
		frmMeerkatMonitor.getContentPane().add(panel_1);
		panel_1.setLayout(null);

		// Button to web dashboard
		JButton btnDashboard = new JButton("Dashboard");
		btnDashboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openUrl(httpServer.getServerUrl());
			}
		});
		btnDashboard.setBounds(10, 11, 109, 23);
		panel_1.add(btnDashboard);

		JButton btnAddNew = new JButton("Add New");
		btnAddNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddNewApp addNew = new AddNewApp(webCollection, appTree);
				addNew.showUp();
			}
		});
		btnAddNew.setBounds(129, 11, 89, 23);
		panel_1.add(btnAddNew);

		JPanel panel = new JPanel();
		panel.setBounds(10, 616, 794, 24);
		frmMeerkatMonitor.getContentPane().add(panel);

		// Footer
		final JLabel footer = new JLabel("Meerkat Monitor v."+ webCollection.getAppVersion() + " - Open Source GPL");
		footer.setBounds(278, 0, 237, 14);
		footer.setForeground(new Color(0, 51, 153));
		footer.setFont(new Font("Arial", Font.PLAIN, 11));
		footer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				openUrl("http://meerkat-monitor.org/?utm_source=WebAppDashboard&utm_medium=WebApp&utm_content=Link&utm_campaign=WebAppDashboard");
			}

			@Override
			public void mouseEntered(MouseEvent arg1) {
				footer.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		});
		panel.setLayout(null);
		panel.add(footer);

		// Refresh and populate
		refresh();

		// Add keyboard shortcuts
		addKeysShortcuts();

	}

	/**
	 * refresh
	 */
	public final void refresh() {
		/** Get WebApps to populate list */
		Iterator<WebApp> it = webCollection.getWebAppCollectionIterator();

		root = new DefaultMutableTreeNode("Meerkat Monitor Applications");
		selectedNode = root; // Set default selected node
		groupWebApps = new DefaultMutableTreeNode("Web Applications");
		groupWebServices = new DefaultMutableTreeNode("Web Services");
		groupDatabases = new DefaultMutableTreeNode("Databases");
		groupSocketServices = new DefaultMutableTreeNode("Socket Services");
		groupSSHServices = new DefaultMutableTreeNode("SSH");
		root.add(groupWebApps);
		root.add(groupWebServices);
		root.add(groupDatabases);
		root.add(groupSocketServices);
		root.add(groupSSHServices);

		WebApp currentWebApp;
		while (it.hasNext()) {
			currentWebApp = it.next();
			DefaultMutableTreeNode webApp = new DefaultMutableTreeNode(
					currentWebApp.getName());

			if (currentWebApp.getType().equals(WebApp.TYPE_WEBAPP)) {
				groupWebApps.add(webApp);
			} else if (currentWebApp.getType().equals(WebApp.TYPE_WEBSERVICE)) {
				groupWebServices.add(webApp);
			} else if (currentWebApp.getType().equals(WebApp.TYPE_DATABASE)) {
				groupDatabases.add(webApp);
			} else if (currentWebApp.getType().equals(WebApp.TYPE_SOCKET)) {
				groupSocketServices.add(webApp);
			} else if (currentWebApp.getType().equals(WebApp.TYPE_SSH)) {
				groupSSHServices.add(webApp);
			} else {
				log.error("Failed to find type of app: "+ currentWebApp.getName());
			}

		}

		meerkatModel = new DefaultTreeModel(root);
		appTree = new JTree(meerkatModel);
		appTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION); // Control tree item selection
		appTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) appTree.getLastSelectedPathComponent();
				selectedNode = node;
				if (selectedNode == null) {
					selectedNode = node;
				} else
					// Check if is a main parent node (group) or a leaf (webApp)
					if (selectedNode.getPath().length <= 2) {
						// show the app logo
						appOptionsPanel.removeAll();
						appOptionsPanel.add(mainFrameAppLogo());
						appOptionsPanel.add(mainFrameAppVersion());
						appOptionsPanel.add(mainFrameAppIcon());
						appOptionsPanel.repaint();

						wsOptionsPanel.removeAll();
						wsOptionsPanel.add(mainFrameAppLogo());
						wsOptionsPanel.add(mainFrameAppVersion());
						wsOptionsPanel.add(mainFrameAppIcon());
						wsOptionsPanel.repaint();

						sqlOptionsPanel.removeAll();
						sqlOptionsPanel.add(mainFrameAppLogo());
						sqlOptionsPanel.add(mainFrameAppVersion());
						sqlOptionsPanel.add(mainFrameAppIcon());
						sqlOptionsPanel.repaint();

						socketServiceOptionsPanel.removeAll();
						socketServiceOptionsPanel.add(mainFrameAppLogo());
						socketServiceOptionsPanel.add(mainFrameAppVersion());
						socketServiceOptionsPanel.add(mainFrameAppIcon());
						socketServiceOptionsPanel.repaint();

						sshOptionsPanel.removeAll();
						sshOptionsPanel.add(mainFrameAppLogo());
						sshOptionsPanel.add(mainFrameAppVersion());
						sshOptionsPanel.add(mainFrameAppIcon());
						sshOptionsPanel.repaint();

					} else {
						/* retrieve the node that was selected */
						WebApp clickedWebAppLeaf = webCollection.getWebAppByName(node.getUserObject().toString());

						if (clickedWebAppLeaf != null) { // In case of select father node
							/* React to the node selection. */
							if (clickedWebAppLeaf.getType().equalsIgnoreCase(WebApp.TYPE_WEBAPP)) {
								appListJpanelContainer.remove(appOptionsPanel);
								appListJpanelContainer.remove(wsOptionsPanel);
								appListJpanelContainer.remove(sqlOptionsPanel);
								appListJpanelContainer.remove(socketServiceOptionsPanel);
								appListJpanelContainer.remove(sshOptionsPanel);

								appOptionsPanel = new OptionsPanelWebApp(clickedWebAppLeaf, webCollection, window);
								appListJpanelContainer.add(appOptionsPanel);

							} else if (clickedWebAppLeaf.getType().equalsIgnoreCase(WebApp.TYPE_WEBSERVICE)) {
								appListJpanelContainer.remove(appOptionsPanel);
								appListJpanelContainer.remove(wsOptionsPanel);
								appListJpanelContainer.remove(sqlOptionsPanel);
								appListJpanelContainer.remove(socketServiceOptionsPanel);
								appListJpanelContainer.remove(sshOptionsPanel);

								wsOptionsPanel = new OptionsPanelWebService(
										(WebServiceApp) clickedWebAppLeaf,
										webCollection, window);
								appListJpanelContainer.add(wsOptionsPanel);
								wsOptionsPanel.revalidate();
								wsOptionsPanel.repaint();
							} else if (clickedWebAppLeaf.getType().equalsIgnoreCase(WebApp.TYPE_DATABASE)) {
								appListJpanelContainer.remove(appOptionsPanel);
								appListJpanelContainer.remove(wsOptionsPanel);
								appListJpanelContainer.remove(sqlOptionsPanel);
								appListJpanelContainer
								.remove(socketServiceOptionsPanel);
								appListJpanelContainer.remove(sshOptionsPanel);

								sqlOptionsPanel = new OptionsPanelDataBase(
										(SQLService) clickedWebAppLeaf,
										webCollection, window);
								appListJpanelContainer.add(sqlOptionsPanel);
								sqlOptionsPanel.revalidate();
								sqlOptionsPanel.repaint();
							} else if (clickedWebAppLeaf.getType().equalsIgnoreCase(WebApp.TYPE_SOCKET)) {
								appListJpanelContainer.remove(appOptionsPanel);
								appListJpanelContainer.remove(wsOptionsPanel);
								appListJpanelContainer.remove(sqlOptionsPanel);
								appListJpanelContainer
								.remove(socketServiceOptionsPanel);
								appListJpanelContainer.remove(sshOptionsPanel);

								socketServiceOptionsPanel = new OptionsPanelSocketService(
										(SocketService) clickedWebAppLeaf,
										webCollection, window);
								appListJpanelContainer
								.add(socketServiceOptionsPanel);
								socketServiceOptionsPanel.revalidate();
								socketServiceOptionsPanel.repaint();
							} else if (clickedWebAppLeaf.getType().equalsIgnoreCase(WebApp.TYPE_SSH)) {
								appListJpanelContainer.remove(appOptionsPanel);
								appListJpanelContainer.remove(wsOptionsPanel);
								appListJpanelContainer.remove(sqlOptionsPanel);
								appListJpanelContainer
								.remove(socketServiceOptionsPanel);
								appListJpanelContainer.remove(sshOptionsPanel);

								sshOptionsPanel = new OptionsPanelSSH(
										(SecureShellSSH) clickedWebAppLeaf,
										webCollection, window);
								appListJpanelContainer.add(sshOptionsPanel);
								sshOptionsPanel.revalidate();
								sshOptionsPanel.repaint();
							}

							appOptionsPanel.repaint();
						}
					}
			}
		});
		appTree.setEditable(true);

		// Expand all nodes in the tree
		/**
		 * for (int i = 0; i < appTree.getRowCount(); i++) {
		 * appTree.expandRow(i); }
		 */

		// Refresh application groups
		refreshGroupsList();

		// Apps pane
		tabbedPane.removeAll();
		tabbedPane.addTab("Applications", null, new JScrollPane(appTree), null);

		// Set current appOptions Frame with logo and version
		appOptionsPanel.removeAll();
		appOptionsPanel.add(mainFrameAppLogo());
		appOptionsPanel.add(mainFrameAppVersion());
		appOptionsPanel.add(mainFrameAppIcon());
		appOptionsPanel.revalidate();
		appOptionsPanel.repaint();

		// Update Web Dashboard
		webCollection.writeWebAppCollectionDataFile();
		httpServer.refreshIndex();

		appTree.revalidate();

	}

	/**
	 * refreshGroupsList
	 */
	public final void refreshGroupsList() {
		groupsCollection.populateGroups(webCollection);
	}

	/**
	 * addKeysShortcuts
	 */
	private final void addKeysShortcuts() {
		// Add F5 to refresh
		KeyStroke keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false);
		ActionListener actionListenerMainPane = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				refresh();
			}
		};
		frmMeerkatMonitor.getRootPane().registerKeyboardAction(
				actionListenerMainPane, keystroke, JComponent.WHEN_FOCUSED);

	}

	/**
	 * refreshWebAppsJTabbedPane
	 * 
	 * @return
	 */
	public final JFrame getMainAppJFrame() {
		return frmMeerkatMonitor;
	}

	/**
	 * mainFrameAppLogo
	 * 
	 * @return
	 */
	private final JLabel mainFrameAppLogo() {
		JLabel meerkatLogo = new JLabel("");
		meerkatLogo.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/meerkat.png")));
		meerkatLogo.setBounds(142, 67, 260, 30);

		return meerkatLogo;
	}

	/**
	 * mainFrameAppVersion
	 * 
	 * @return
	 */
	private final JLabel mainFrameAppVersion() {
		JLabel version = new JLabel("v." + webCollection.getAppVersion());
		version.setBounds(249, 108, 79, 14);

		return version;
	}

	/**
	 * mainFrameAppIcon
	 * 
	 * @return
	 */
	private final JLabel mainFrameAppIcon() {
		JLabel icon = new JLabel("");
		icon.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/meerkat-small.png")));
		icon.setBounds(244, 144, 43, 93);

		return icon;
	}

	/**
	 * removeSelectNodeElementFromTree
	 */
	public final void removeSelectNodeElementFromTree() {
		// get the parent of the selected node
		MutableTreeNode parent = (MutableTreeNode) (selectedNode.getParent());

		// Get the previous or the next node
		MutableTreeNode nextSelectedNode = (MutableTreeNode) selectedNode
				.getPreviousSibling();
		if (nextSelectedNode == null) {
			// if previous sibling is null, get the next sibling
			nextSelectedNode = (MutableTreeNode) selectedNode.getNextSibling();
		}

		if (nextSelectedNode == null) {
			nextSelectedNode = parent;
		}

		// Remove the select node
		meerkatModel.removeNodeFromParent(selectedNode);

		// Make the node visible by scroll to it
		TreeNode[] nodes = meerkatModel.getPathToRoot(nextSelectedNode);
		TreePath path = new TreePath(nodes);
		appTree.scrollPathToVisible(path);
		appTree.setSelectionPath(path);

		appTree.revalidate();
		appTree.repaint();
	}

	/**
	 * openUrl
	 * 
	 * @param uri
	 */
	private void openUrl(String url) {
		URI openUrl = null;
		try {
			openUrl = new URI(url);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block

		}
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(openUrl);
			} catch (IOException e) {
				log.error("", e);
			}
		} else {
			log.error("Desktop window system is not supported.");
		}
	}

	/**
	 * searchNode
	 * 
	 * @param nodeStr
	 * @return
	 */
	public DefaultMutableTreeNode searchNode(String nodeStr) {
		DefaultMutableTreeNode node = null;

		Enumeration<?> en = root.breadthFirstEnumeration();

		while (en.hasMoreElements()) {
			node = (DefaultMutableTreeNode) en.nextElement();

			if (nodeStr.equals(node.getUserObject().toString())) {
				return node;
			}
		}
		return null;
	}

	/**
	 * AddNewApp
	 * 
	 * @author pgnunes
	 * 
	 */
	class AddNewApp extends JFrame {

		private static final long serialVersionUID = 3041202369110755683L;
		private JPanel contentPane;
		private WebAppCollection wac;
		private JTree appTree;

		private String[] appTypes = { " Web Application", " WebService",
				" Database", " Socket", " SSH", };

		private String selected = appTypes[0];

		/**
		 * Launch the application.
		 */
		public void showUp() {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						AddNewApp frame = new AddNewApp(wac, appTree);
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
		public AddNewApp(final WebAppCollection wac, final JTree appTree) {
			this.wac = wac;
			this.appTree = appTree;

			setAlwaysOnTop(true);
			setResizable(false);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setIconImage(Toolkit.getDefaultToolkit().getImage(
					SimpleTextEditor.class
					.getResource("/resources/tray_icon.gif")));
			setTitle("Add New");
			setBounds(100, 100, 239, 104);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			final JComboBox comboBox = new JComboBox(appTypes);
			comboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selected = (String) comboBox.getSelectedItem();
				}
			});
			comboBox.setBounds(10, 11, 214, 23);
			contentPane.add(comboBox);

			JButton button_1 = new JButton("Add");
			button_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DefaultMutableTreeNode dmtnApp = root;
					Random generator = new Random();
					int r = generator.nextInt(Integer.MAX_VALUE) + 1;

					if (selected.equalsIgnoreCase(appTypes[0])) {
						WebApp newWebApp = new WebApp("Untitled" + r, "", "",
								"");
						newWebApp.setActive(false);
						newWebApp.setTempWorkingDir(wac.getTmpDir());
						wac.addWebApp(newWebApp);
						dmtnApp = new DefaultMutableTreeNode(newWebApp
								.getName());
						groupWebApps.add(dmtnApp);
					} else if (selected.equalsIgnoreCase(appTypes[1])) {
						WebServiceApp newWebApp = new WebServiceApp("Untitled" + r,
								"");
						newWebApp.setActive(false);
						newWebApp.setTempWorkingDir(wac.getTmpDir());
						wac.addWebApp(newWebApp);
						dmtnApp = new DefaultMutableTreeNode(newWebApp
								.getName());
						groupWebServices.add(dmtnApp);
					} else if (selected.equalsIgnoreCase(appTypes[2])) {
						SQLService newWebApp = new SQLService("Untitled" + r, "", "", "", "", "", "", "");
						newWebApp.setActive(false);
						newWebApp.setTempWorkingDir(wac.getTmpDir());
						wac.addWebApp(newWebApp);
						dmtnApp = new DefaultMutableTreeNode(newWebApp
								.getName());
						groupDatabases.add(dmtnApp);
					} else if (selected.equalsIgnoreCase(appTypes[3])) {
						SocketService newWebApp = new SocketService("Untitled"
								+ r, "", "0", "", "", "");
						newWebApp.setActive(false);
						newWebApp.setTempWorkingDir(wac.getTmpDir());
						wac.addWebApp(newWebApp);
						dmtnApp = new DefaultMutableTreeNode(newWebApp
								.getName());
						groupSocketServices.add(dmtnApp);
					} else if (selected.equalsIgnoreCase(appTypes[4])) {
						SecureShellSSH newWebApp = new SecureShellSSH("Untitled" + r, "", "", "", "", "", "");
						newWebApp.setActive(false);
						newWebApp.setTempWorkingDir(wac.getTmpDir());
						wac.addWebApp(newWebApp);
						dmtnApp = new DefaultMutableTreeNode(newWebApp
								.getName());
						groupSSHServices.add(dmtnApp);
					}

					// Search and select the new node
					meerkatModel.reload();
					TreeNode[] nodes = meerkatModel.getPathToRoot(dmtnApp);
					TreePath path = new TreePath(nodes);
					appTree.scrollPathToVisible(path);
					appTree.setSelectionPath(path);

					appTree.revalidate();
					appTree.repaint();

					dispose();

				}
			});
			button_1.setBounds(10, 45, 89, 23);
			contentPane.add(button_1);

			JButton btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnCancel.setBounds(135, 45, 89, 23);
			contentPane.add(btnCancel);
		}
	}
}
