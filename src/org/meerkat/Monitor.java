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

package org.meerkat;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.meerkat.db.EmbeddedDB;
import org.meerkat.group.AppGroupCollection;
import org.meerkat.httpServer.HttpServer;
import org.meerkat.network.MailManager;
import org.meerkat.network.RSS;
import org.meerkat.services.WebApp;
import org.meerkat.util.DateUtil;
import org.meerkat.util.PropertiesLoader;
import org.meerkat.webapp.WebAppCollection;
import org.meerkat.webapp.WebAppEvent;
import org.meerkat.webapp.WebAppResponse;

public class Monitor {
	private static Logger log = Logger.getLogger(Monitor.class);
	EmbeddedDB ebd;
	WebAppCollection webAppsCollection;
	AppGroupCollection appGroupCollection;
	HttpServer httpWebServer;
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

	public Monitor(EmbeddedDB ebd, WebAppCollection webAppsCollection, AppGroupCollection appGroupCollection, 
			HttpServer httpWebServer, RSS rssFeed, String propertiesFile){
		this.ebd = ebd;
		this.webAppsCollection = webAppsCollection;
		this.appGroupCollection = appGroupCollection;
		this.httpWebServer = httpWebServer;
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

		List<WebApp> webAppListCopy = webAppsCollection.getCopyWebApps();
		Iterator<WebApp> i = webAppListCopy.iterator();
		
		// Prevent dashboard link from giving 404 at first round
		while (i.hasNext()) {
			currentWebApp = i.next();
			currentWebApp.setTempWorkingDir(tempWorkingDir);
			currentWebApp.writeWebAppVisualizationInfoWorkingOn(); // shows user that's working on data
			currentWebApp.writeWebAppVisualizationDataFile(); // this takes time - executed in new thread
		}
		log.info("");
		
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
					if (currentWebAppResponse.isOnline() && currentWebApp.getlastStatus().equalsIgnoreCase("NA")) {
						currentWebApp.setlastStatus("online");

						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								false,
								now,
								true,
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventNewMonitoringStart);
						// Save load time and latency
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());
						ev.setLatency(currentWebApp.getLatency());

						// Set the response
						ev.setCurrentResponse(currentWebApp.getCurrentResponse());
						currentWebApp.addEvent(ev);

					} else if (!currentWebAppResponse.isOnline() && currentWebApp.getlastStatus().equalsIgnoreCase("NA")) {
						currentWebApp.setlastStatus("offline");

						log.warn("OFFLINE\t| " + currentWebApp.getName()+ "\t | " + currentWebApp.getUrl());

						if (sendEmails) {
							mailManager.sendEmail(subject + " - "+ currentWebApp.getName() + " is OFFLINE", 
									"The webapp "+ currentWebApp.getName() + " on "+ currentWebApp.getUrl() + " is OFFLINE!");
						}
						//SimplePopup.showErrorMsg(currentWebApp.getName() + " is OFFLINE!");
						
						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								true,
								now,
								false,
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventNewMonitoringStart);
						// Save load time and latency
						ev.setLatency(currentWebApp.getLatency());
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());

						// Set the response
						ev.setCurrentResponse(currentWebApp.getCurrentResponse());
						currentWebApp.addEvent(ev);

						// Add RSS item
						rssFeed.addItem(currentWebApp.getName(), currentWebApp.getDataFileName(), now, currentWebApp.getName() + " is OFFLINE");

						// Execute the executeOnOffline
						if (!currentWebApp.getExecuteOnOffline().equalsIgnoreCase("")) {
							//SimplePopup.showErrorMsg("Taking action on offline: "+ currentWebApp.getName());
							currentWebApp.executeOfflineAction();
						}
					}

					// If last status was online
					else if (currentWebAppResponse.isOnline() && currentWebApp.getlastStatus().equalsIgnoreCase("online")) {
						currentWebApp.setlastStatus("online");

						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								false,
								now,
								true,
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventStandard);
						// Save load time and latency
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());
						ev.setLatency(currentWebApp.getLatency());

						// Set the response
						ev.setCurrentResponse(currentWebApp.getCurrentResponse());
						currentWebApp.addEvent(ev);

					} else if (!currentWebAppResponse.isOnline() && currentWebApp.getlastStatus().equalsIgnoreCase("online")) {
						currentWebApp.setlastStatus("offline");

						log.warn("OFFLINE\t| " + currentWebApp.getName()+ "\t | " + currentWebApp.getUrl());

						if (sendEmails) {
							mailManager.sendEmail(subject + " - "+ currentWebApp.getName() + " is OFFLINE", 
									"The webapp "+ currentWebApp.getName() + " on "+ currentWebApp.getUrl() + " is OFFLINE!");
						}
						//SimplePopup.showErrorMsg(currentWebApp.getName() + " is OFFLINE!");
						
						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								true,
								now,
								false,
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventGoOffline);
						// Save load time and latency
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());
						ev.setLatency(currentWebApp.getLatency());

						// Set the response
						ev.setCurrentResponse(currentWebApp.getCurrentResponse());
						currentWebApp.addEvent(ev);

						// Add RSS item
						rssFeed.addItem(currentWebApp.getName(),currentWebApp.getDataFileName(), now, currentWebApp.getName() + " is OFFLINE");

						// Execute the executeOnOffline
						if (!currentWebApp.getExecuteOnOffline().equalsIgnoreCase("")) {
							//SimplePopup.showErrorMsg("Taking action on offline: "+ currentWebApp.getName());
							currentWebApp.executeOfflineAction();
						}
					}

					// If last status was offline
					else if (currentWebAppResponse.isOnline() && currentWebApp.getlastStatus().equalsIgnoreCase("offline")) {
						currentWebApp.setlastStatus("online");

						if (sendEmails) {
							mailManager.sendEmail(subject + " - "+ currentWebApp.getName() + " is BACK ONLINE", 
									"The webapp "+ currentWebApp.getName() + " on "+ currentWebApp.getUrl() + " is BACK ONLINE!!");
						}

						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								true,
								now,
								true,
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventBackOnline);
						// Save load time and latency
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());
						ev.setLatency(currentWebApp.getLatency());

						// Set the response
						ev.setCurrentResponse(currentWebApp.getCurrentResponse());
						currentWebApp.addEvent(ev);
						// httpWebServer.addEventResponse(currentWebApp);

						//SimplePopup.showErrorMsg(currentWebApp.getName() + " is BACK ONLINE!");

						// Add RSS item
						rssFeed.addItem(currentWebApp.getName(),currentWebApp.getDataFileName(), now,currentWebApp.getName() + " is BACK ONLINE!");

					} else if (!currentWebAppResponse.isOnline()&& currentWebApp.getlastStatus().equalsIgnoreCase("offline")) {
						currentWebApp.setlastStatus("offline");

						log.warn("STILL OFFLINE\t| " + currentWebApp.getName()+ "\t | " + currentWebApp.getUrl());

						// Add event
						String now = dateUtil.now();
						ev = new WebAppEvent(
								false,
								now,
								false,
								Double.toString(currentWebApp.getAvailability()),
								currentWebAppResponse.getHttpStatus(),
								eventStandard);
						// Save load time and latency
						ev.setPageLoadTime(currentWebAppResponse.getPageLoadTime());
						ev.setLatency(currentWebApp.getLatency());

						// Set the response
						ev.setCurrentResponse(currentWebApp.getCurrentResponse());
						currentWebApp.addEvent(ev);

						// Execute the executeOnOffline
						if (!currentWebApp.getExecuteOnOffline().equalsIgnoreCase("")) {
							//SimplePopup.showErrorMsg("Taking action on still offline: "+ currentWebApp.getName());
							currentWebApp.executeOfflineAction();
						}
					}
				}
				
				// increase checked apps
				roundCompletedApps++;
				
				// Refresh dashboard and app in dashboard after each test
				httpWebServer.refreshIndex();
				
			}
			
			// Refresh time line at the end of every round
			webAppsCollection.writeWebAppCollectionTimeLine();
			
			
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

			
			
		}
	}

}
