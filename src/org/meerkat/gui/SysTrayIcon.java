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

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.meerkat.group.AppGroupCollection;
import org.meerkat.httpServer.HttpServer;
import org.meerkat.util.ImageUtil;
import org.meerkat.util.MasterKeyManager;
import org.meerkat.webapp.WebAppCollection;

public class SysTrayIcon {
	private static Logger log = Logger.getLogger(SysTrayIcon.class);
	private TrayIcon trayIcon = null;

	private SystemTray sysTray = SystemTray.getSystemTray();
	private Image image = ImageUtil.create("/resources/tray_icon.gif");

	private TrayIcon staticInfoTrayIcon = new TrayIcon(image, "", null);

	private int webserverPort;
	private String hostname = "localhost";
	private PopupMenu menu;
	private WebAppCollection webAppsCollection;
	private AppGroupCollection groupsCollection;
	private HttpServer httpServer;
	private MasterKeyManager mkm;
	private String version;

	/**
	 * SysTrayIcon
	 */
	public SysTrayIcon(final MasterKeyManager mKm, final HttpServer httpServer,	
			final WebAppCollection webAppsCollection, final AppGroupCollection groupsCollection) {
		this.mkm = mKm;
		this.httpServer = httpServer;
		this.webserverPort = httpServer.getPort();

		this.webAppsCollection = webAppsCollection;
		this.groupsCollection = groupsCollection;
		this.version = webAppsCollection.getAppVersion();

		InetAddress netAddr;
		try {
			netAddr = InetAddress.getLocalHost();
			hostname = netAddr.getHostName();
		} catch (UnknownHostException e) {
			log.error("Cannot get hostname. Some URL's may be invalid!", e);
		}

		menu = new PopupMenu();
		MenuItem webClientItem = new MenuItem("Dashboard");

		menu.add(webClientItem);
		menu.addSeparator();

		MenuItem AppWindowItem = new MenuItem("Settings");

		menu.add(AppWindowItem);

		trayIcon = new TrayIcon(image, "Meerkat Monitor", menu);
		trayIcon.setImageAutoSize(true);
		try {
			sysTray.add(trayIcon);
		} catch (AWTException e2) {
			log.error("ERROR creating system tray", e2);
		}

		ActionListener openBrowserClient = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					java.awt.Desktop.getDesktop().browse(
							new URI("http://" + hostname + ":" + webserverPort
									+ "/"));
				} catch (IOException e1) {
					log.error("IOException opening browser", e1);
				} catch (URISyntaxException e1) {
					log.error("URISyntaxException opening browser", e1);
				}
			}
		};

		// Double-click default event
		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainWindow appWin = new MainWindow(mkm, webAppsCollection, groupsCollection, httpServer);
				appWin.showUp();
			}
		});

		// Open main app window
		ActionListener openAppWindow = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainWindow appWin = new MainWindow(mkm, webAppsCollection, groupsCollection, httpServer);
				appWin.showUp();
			}
		};

		webClientItem.addActionListener(openBrowserClient);
		AppWindowItem.addActionListener(openAppWindow);

	}

	/**
	 * createSystrayIcon
	 */
	public final void createSystrayIcon(){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setWebServerPort(webserverPort);
				setWebAppCollection(webAppsCollection);
				setGroupsCollection(groupsCollection);
				setHttpServer(httpServer);

				// Exit
				ActionListener exitListener = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						DialogBoxShutdown sdb = new DialogBoxShutdown();
						sdb.showUp();
					}
				};

				// About button
				ActionListener aboutListener = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						DialogBoxAbout ab = new DialogBoxAbout(version);
						ab.showUp();
					}
				};

				MenuItem aboutm = new MenuItem("About");
				aboutm.addActionListener(aboutListener);
				addSeparator();
				addMenuItem("About", aboutListener);
				addSeparator();
				addMenuItem("Exit", exitListener);
			}
		});
	}

	/**
	 * setTooltip
	 * 
	 * @param tooltip
	 */
	public final void setTooltip(String tooltip) {
		trayIcon.setToolTip(tooltip);
	}

	/**
	 * addMenuItem
	 * 
	 * @param name
	 * @param actionList
	 */
	public final void addMenuItem(String name, ActionListener actionList) {
		MenuItem item = new MenuItem(name);
		item.addActionListener(actionList);
		menu.add(item);
	}

	/**
	 * addMenu
	 * @param m Menu
	 */
	public final void addMenu(Menu m) {
		menu.addSeparator();
		menu.add(m);
	}

	/**
	 * setWebServerPort
	 * 
	 * @param webserverPort
	 */
	public final void setWebServerPort(int webserverPort) {
		this.webserverPort = webserverPort;
	}

	/**
	 * showMessage
	 * 
	 * @param message
	 */
	public final void showMessage(String title, String message) {
		trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);

	}

	/**
	 * showMessage
	 * 
	 * @param message
	 */
	public final void showMessageError(String title, String message) {
		trayIcon.displayMessage(title, message, TrayIcon.MessageType.ERROR);

	}

	/**
	 * addSeparator
	 */
	public final void addSeparator() {
		menu.addSeparator();
	}

	/**
	 * setWebAppCollection
	 * 
	 * @param webAppsCollection
	 */
	public final void setWebAppCollection(WebAppCollection webAppsCollection) {
		this.webAppsCollection = webAppsCollection;
	}

	/**
	 * setWebAppCollection
	 * 
	 * @param groupsCollection
	 */
	public final void setGroupsCollection(AppGroupCollection groupsCollection) {
		this.groupsCollection = groupsCollection;
	}

	public final void setHttpServer(HttpServer httpServer) {
		this.httpServer = httpServer;
	}

	/**
	 * reloadSystray
	 * 
	 * If explorer.exe process crashes the icon is not created again With this
	 * method we can re-create
	 * 
	 */
	public final void reloadSystray() {
		// Get tray icons
		TrayIcon[] trayIcons = sysTray.getTrayIcons();

		boolean mmTrayIconIsPresent = false;
		for (int i = 0; i < trayIcons.length; i++) {
			if (trayIcons[i].equals(sysTray)) {
				mmTrayIconIsPresent = true;
				break;
			}
		}

		// Try to re-create the tray icon
		if (!mmTrayIconIsPresent) {
			try {
				sysTray.remove(trayIcon);
				sysTray.add(trayIcon);
			} catch (AWTException e) {
				log.error("Failed to reload systray!", e);
			}
		}
	}

	/**
	 * removeSystrayIcon
	 */
	public final void removeSystrayIcon() {
		TrayIcon[] trayIcons = sysTray.getTrayIcons();

		for (int i = 0; i < trayIcons.length; i++) {
			if (trayIcons[i].equals(trayIcon)) {
				sysTray.remove(trayIcons[i]);
				break;
			}
		}
	}

	/**
	 * addSystrayIcon
	 */
	public final void addSystrayIcon() {
		// Remove the static icon if exists
		TrayIcon[] trayIcons = sysTray.getTrayIcons();
		for (int i = 0; i < trayIcons.length; i++) {
			if (trayIcons[i].equals(staticInfoTrayIcon)) {
				sysTray.remove(trayIcons[i]);
				break;
			}
		}

		// Add the normal icon
		try {
			sysTray.add(trayIcon);
		} catch (AWTException e) {
			log.error("Error adding icon to tray!", e);
		}
	}

	/**
	 * showStaticInfoIcon
	 * 
	 * @param tooltip
	 */
	public final void showStaticInfoIcon(String tooltip) {
		staticInfoTrayIcon = new TrayIcon(image, tooltip, null);
		try {
			sysTray.add(staticInfoTrayIcon);
		} catch (AWTException e) {
			log.error("Error adding icon to tray!", e);
		}
	}

}
