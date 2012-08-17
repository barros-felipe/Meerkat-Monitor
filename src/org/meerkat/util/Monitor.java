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

package org.meerkat.util;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.meerkat.group.AppGroupCollection;
import org.meerkat.gui.SysTrayIcon;
import org.meerkat.httpServer.HttpServer;
import org.meerkat.network.MailManager;
import org.meerkat.network.RSS;
import org.meerkat.services.WebApp;
import org.meerkat.webapp.WebAppCollection;
import org.meerkat.webapp.WebAppEvent;
import org.meerkat.webapp.WebAppResponse;

public class Monitor {
	private static Logger log = Logger.getLogger(Monitor.class);
	WebAppCollection webAppsCollection;
	AppGroupCollection appGroupCollection;
	HttpServer httpWebServer;
	SysTrayIcon systray;
	RSS rssFeed;
	PropertiesLoader pL;
	WebApp currentWebApp;
	Properties properties;
	WebAppResponse currentWebAppResponse;
	DateUtil dateUtil;
	WebAppEvent ev;
	String eventNewMonitoringStart = "";
	String tempWorkingDir;
	boolean sendEmails = false;
	MailManager mailManager;
	String subject;
	String eventStandard = "";
	String eventGoOffline = "Offline";
	String eventBackOnline = "Back Online";
	int testPause;
	long pauseTime;


	public Monitor(WebAppCollection webAppsCollection, AppGroupCollection appGroupCollection, 
			HttpServer httpWebServer, SysTrayIcon systray, RSS rssFeed, String propertiesFile){
		this.webAppsCollection = webAppsCollection;
		this.appGroupCollection = appGroupCollection;
		this.httpWebServer = httpWebServer;
		this.systray = systray;
		this.rssFeed = rssFeed;
		dateUtil = new DateUtil();
		tempWorkingDir = webAppsCollection.getTmpDir();
		pL = new PropertiesLoader(propertiesFile);
	}

	/**
	 * startMonitor
	 */
	public final void startMonitor(){
		webAppsCollection.saveConfigXMLFile();
		httpWebServer.refreshIndex();

		// Autoload last session if enabled in properties
		/**
		autoLoadOnStart = Boolean.parseBoolean(properties.getProperty("meerkat.autoload.start"));
		if (autoLoadOnStart) {
			log.info("Autoload on start enable. Loading last session..");
			loadSession();
			log.info("Session loaded!");
		}
		 */

		List<WebApp> webAppListCopy = webAppsCollection.getCopyWebApps();
		Iterator<WebApp> i = webAppListCopy.iterator();

		// Prevent dashboard link from giving 404 at first round
		while (i.hasNext()) {
			currentWebApp = i.next();
			currentWebApp.writeWebAppVisualizationDataFile(); 
		}

		// Launch the monitor process
		while (true) {
			// Check for updated user defined new properties
			properties = pL.getPropetiesFromFile();
			sendEmails = Boolean.parseBoolean(properties.getProperty("meerkat.email.send.emails"));
			subject = properties.getProperty("meerkat.email.subjectPrefix");
			if(sendEmails){
				mailManager = new MailManager(pL.getPropertiesFile());
			}

			log.info("[Starting test round]");

			// We iterate over a copy of the collection so we can prevent
			// ConcurrentModificationException from the GUI
			webAppListCopy = webAppsCollection.getCopyWebApps();
			i = webAppListCopy.iterator();
			int numberOfApps = webAppsCollection.getWebAppCollectionSize();
			int percent = 0;
			int roundCompletedApps = 1;

			while (i.hasNext()) {
				percent = roundCompletedApps * 100 / numberOfApps;
				currentWebApp = i.next();
				log.info("["+roundCompletedApps+"/"+numberOfApps+" "+percent+"%]\t"+currentWebApp.getName());

				// check if the webApp is ready to monit or not - temp created in the gui
				if (currentWebApp.isActive()) {
					currentWebAppResponse = new WebAppResponse();

					// Check the status
					currentWebAppResponse = currentWebApp.checkWebAppStatus();

					// First test to set the lastStatus
					if (currentWebAppResponse.isOnline()
							&& currentWebApp.getlastStatus().equalsIgnoreCase("NA")) {
						currentWebApp.setlastStatus("online");
						currentWebApp.increaseNumberOfTests();

						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								false,
								now,
								"100",
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventNewMonitoringStart, tempWorkingDir);
						// Save load time and latency
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());
						ev.setLatency(currentWebApp.getLatency());

						// Set the response
						ev.setCurrentResponseGlobal(currentWebApp.getCurrentResponse(), currentWebApp);
						currentWebApp.addEvent(ev);

					} else if (!currentWebAppResponse.isOnline()
							&& currentWebApp.getlastStatus().equalsIgnoreCase("NA")) {
						currentWebApp.setlastStatus("offline");

						log.warn("OFFLINE\t| " + currentWebApp.getName()+ "\t | " + currentWebApp.getUrl());
						currentWebApp.increaseNumberOfTests();
						currentWebApp.increaseNumberOfOfflines();

						if (sendEmails) {
							mailManager.sendEmail(subject + " - "+ currentWebApp.getName() + " is OFFLINE", 
									"The webapp "+ currentWebApp.getName() + " on "+ currentWebApp.getUrl() + " is OFFLINE!");
						}
						systray.showMessageError(currentWebApp.getName(), currentWebApp.getName() + " is OFFLINE!");

						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								true,
								now,
								"0",
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventNewMonitoringStart, tempWorkingDir);
						// Save load time and latency
						ev.setLatency(currentWebApp.getLatency());
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());

						// Set the response
						ev.setCurrentResponseGlobal(currentWebApp.getCurrentResponse(), currentWebApp);
						currentWebApp.addEvent(ev);
						// httpWebServer.addEventResponse(currentWebApp);

						// Add RSS item
						rssFeed.addItem(currentWebApp.getName(), currentWebApp.getDataFileName(), now, currentWebApp.getName() + " is OFFLINE");

						// Execute the executeOnOffline
						if (!currentWebApp.getExecuteOnOffline().equalsIgnoreCase("")) {
							systray.showMessage(currentWebApp.getName(), "Taking action on offline: "+ currentWebApp.getName());
							currentWebApp.executeOfflineAction();
						}
					}

					// If last status was online
					else if (currentWebAppResponse.isOnline() && currentWebApp.getlastStatus().equalsIgnoreCase("online")) {
						currentWebApp.setlastStatus("online");
						currentWebApp.increaseNumberOfTests();

						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								false,
								now,
								"100",
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventStandard, tempWorkingDir);
						// Save load time and latency
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());
						ev.setLatency(currentWebApp.getLatency());

						// Set the response
						ev.setCurrentResponseGlobal(currentWebApp.getCurrentResponse(), currentWebApp);
						currentWebApp.addEvent(ev);

					} else if (!currentWebAppResponse.isOnline()
							&& currentWebApp.getlastStatus().equalsIgnoreCase("online")) {
						currentWebApp.setlastStatus("offline");

						log.warn("OFFLINE\t| " + currentWebApp.getName()+ "\t | " + currentWebApp.getUrl());
						currentWebApp.increaseNumberOfTests();
						currentWebApp.increaseNumberOfOfflines();

						if (sendEmails) {
							mailManager.sendEmail(subject + " - "+ currentWebApp.getName() + " is OFFLINE", 
									"The webapp "+ currentWebApp.getName() + " on "+ currentWebApp.getUrl() + " is OFFLINE!");
						}
						systray.showMessageError(currentWebApp.getName(),currentWebApp.getName() + " is OFFLINE!");

						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								true,
								now,
								"0",
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventGoOffline, tempWorkingDir);
						// Save load time and latency
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());
						ev.setLatency(currentWebApp.getLatency());

						// Set the response
						ev.setCurrentResponseGlobal(currentWebApp.getCurrentResponse(),currentWebApp);
						currentWebApp.addEvent(ev);

						// Add RSS item
						rssFeed.addItem(currentWebApp.getName(),currentWebApp.getDataFileName(), now, currentWebApp.getName() + " is OFFLINE");

						// Execute the executeOnOffline
						if (!currentWebApp.getExecuteOnOffline().equalsIgnoreCase("")) {
							systray.showMessageError(currentWebApp.getName(), "Taking action on offline: "+ currentWebApp.getName());
							currentWebApp.executeOfflineAction();
						}
					}

					// If last status was offline
					else if (currentWebAppResponse.isOnline() && currentWebApp.getlastStatus().equalsIgnoreCase("offline")) {
						currentWebApp.setlastStatus("online");
						currentWebApp.increaseNumberOfTests();

						if (sendEmails) {
							mailManager.sendEmail(subject + " - "+ currentWebApp.getName() + " is BACK ONLINE", 
									"The webapp "+ currentWebApp.getName() + " on "+ currentWebApp.getUrl() + " iis BACK ONLINE!!");
						}

						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								true,
								now,
								"100",
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventBackOnline, tempWorkingDir);
						// Save load time and latency
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());
						ev.setLatency(currentWebApp.getLatency());

						// Set the response
						ev.setCurrentResponseGlobal(currentWebApp.getCurrentResponse(),currentWebApp);
						currentWebApp.addEvent(ev);
						// httpWebServer.addEventResponse(currentWebApp);

						systray.showMessage(currentWebApp.getName(),currentWebApp.getName() + " is BACK ONLINE!");

						// Add RSS item
						rssFeed.addItem(currentWebApp.getName(),currentWebApp.getDataFileName(), now,currentWebApp.getName() + " is BACK ONLINE!");

					} else if (!currentWebAppResponse.isOnline()&& currentWebApp.getlastStatus().equalsIgnoreCase("offline")) {
						currentWebApp.setlastStatus("offline");

						log.warn("STILL OFFLINE\t| " + currentWebApp.getName()+ "\t | " + currentWebApp.getUrl());
						currentWebApp.increaseNumberOfTests();
						currentWebApp.increaseNumberOfOfflines();

						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								false,
								now,
								"0",
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventStandard, tempWorkingDir);
						// Save load time and latency
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());
						ev.setLatency(currentWebApp.getLatency());

						// Set the response
						ev.setCurrentResponseGlobal(currentWebApp.getCurrentResponse(),currentWebApp);
						currentWebApp.addEvent(ev);

						// Execute the executeOnOffline
						if (!currentWebApp.getExecuteOnOffline().equalsIgnoreCase("")) {
							systray.showMessageError(currentWebApp.getName(),"Taking action on still offline: "+ currentWebApp.getName());
							currentWebApp.executeOfflineAction();
						}
					}
				}
				// Refresh dashboard and app in every app cycle
				webAppsCollection.writeWebAppCollectionDataFile();
				httpWebServer.refreshIndex();

				// increase checked apps
				roundCompletedApps++;
			}
			// reset percentage
			percent = 0;
			roundCompletedApps = 0;

			httpWebServer.setDataSources(webAppsCollection, appGroupCollection);

			// Get the time between rounds
			properties = pL.getPropetiesFromFile();
			testPause = Integer.parseInt(properties.getProperty("meerkat.monit.test.time"));
			pauseTime = TimeUnit.MINUTES.toMillis(testPause); // Convert seconds to milliseconds

			log.info("Next round in: " + testPause + " minute(s) [" + pauseTime+ "ms]");
			log.info("");
			try {
				Thread.sleep(pauseTime);
			} catch (InterruptedException e) {
				log.fatal("Error in the running thread.", e);
			}

			// Refresh systray
			systray.reloadSystray();
		}
	}

}
