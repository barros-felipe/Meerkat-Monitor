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

package org.meerkat.group;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.meerkat.services.WebApp;
import org.meerkat.util.FileUtil;
import org.meerkat.webapp.WebAppCollection;

public class AppGroupCollection {

	private static Logger log = Logger.getLogger(AppGroupCollection.class);
	private List<AppGroup> appGroupCollection = Collections
			.synchronizedList(new ArrayList<AppGroup>());
	private int numberOfGroups = 0;
	private String tempWorkingDir = "";
	private String dataFileName = "groupsAvailabilityGauge.html";

	/**
	 * 
	 */
	public AppGroupCollection() {

	}

	/**
	 * 
	 * @param appGroup
	 */
	public final void addAppGroup(AppGroup appGroup) {
		AppGroup tempAppGroup;
		Boolean create = true;

		Iterator<AppGroup> it = getAppGroupCollectionIterator();
		while (it.hasNext()) {
			tempAppGroup = it.next();
			if (tempAppGroup.getGroupName().equalsIgnoreCase(
					appGroup.getGroupName())) {
				create = false;
			}
		}

		if (create) {
			appGroupCollection.add(appGroup);
			numberOfGroups++;
		}
	}

	/**
	 * setTempWorkingDir
	 * 
	 * @param workingDir
	 */
	public final void setTempWorkingDir(String workingDir) {
		this.tempWorkingDir = workingDir;
	}

	/**
	 * getAppGroupCollectionIterator
	 * 
	 * @return
	 */
	public final Iterator<AppGroup> getAppGroupCollectionIterator() {
		return appGroupCollection.iterator();
	}

	/**
	 * printLogGroupMembers
	 */
	public final void printLogGroupMembers() {
		Iterator<AppGroup> it = getAppGroupCollectionIterator();
		AppGroup temp;
		log.info("Present Groups:");

		while (it.hasNext()) {
			temp = it.next();
			log.info("\t" + temp.getGroupName());
		}
	}

	/**
	 * populateGroups
	 * 
	 * @param wCollection
	 */
	public final void populateGroups(WebAppCollection wCollection) {
		// Clean current groups and generate
		appGroupCollection = new ArrayList<AppGroup>();

		Iterator<WebApp> it = wCollection.getWebAppCollectionIterator();
		WebApp tempApp;
		Iterator<String> tempIt;
		String tempGroupString;
		AppGroup tempGroup;

		// Create Groups from apps and add them to group collection
		while (it.hasNext()) {
			tempApp = it.next();

			if (tempApp.getNumberOfGroups() > 0) {
				// Get current groups of app
				tempIt = tempApp.getGroupIterator();

				while (tempIt.hasNext()) {
					tempGroupString = tempIt.next();
					// Add group to group list
					tempGroup = new AppGroup(tempGroupString);
					addAppGroup(tempGroup);
				}
			}
		}

		// Populate groups
		Iterator<AppGroup> groupIt = getAppGroupCollectionIterator();
		Iterator<WebApp> wIt;
		AppGroup currentGroup;

		while (groupIt.hasNext()) {
			currentGroup = groupIt.next();

			wIt = wCollection.getWebAppCollectionIterator();
			while (wIt.hasNext()) {
				tempApp = wIt.next();
				if (tempApp.hasGroup(currentGroup.getGroupName())) {
					currentGroup.addApp(tempApp);
				}
			}
		}

	}

	/**
	 * getGaugeData
	 * 
	 * @return
	 */
	public final String getAvailabilityGaugeData() {
		String gaugeData = "data.addColumn('string', 'Label');"
				+ "data.addColumn('number', 'Value');";

		// Number of Groups
		gaugeData += "data.addRows(" + numberOfGroups + ");\n";

		double groupAvail;
		BigDecimal bd;
		int decimalPlaces = 2;
		int id = 0;

		Iterator<AppGroup> it = this.getAppGroupCollectionIterator();
		AppGroup appGroupTemp;
		while (it.hasNext()) {
			appGroupTemp = it.next();

			bd = new BigDecimal(appGroupTemp.getGroupAvailabilityAvg());
			bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_DOWN);
			groupAvail = bd.doubleValue();

			gaugeData += "data.setValue(" + id + ", 0, " + "\'"
					+ appGroupTemp.getGroupName() + "\');\n";
			gaugeData += "data.setValue(" + id + ", 1, " + groupAvail + ");\n";
			id++;
		}

		return gaugeData;

	}

	/**
	 * writeAppGroupCollectionDataFileGauge
	 */
	public final void writeAppGroupCollectionAvailabilityDataFileGauge() {
		FileUtil fu = new FileUtil();

		String htmlFileContentsTop = "<html>\n"
				+ "<head>\n"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>"
				+ "<title>Group Collection Gauges</title>\n"
				+ "<link rel=\"icon\"  href=\"/resources/faviconM.gif\"  type=\"image/x-icon\"></link>\n"
				+ "<script type='text/javascript' src='http://www.google.com/jsapi'></script>\n"
				+ "<script type=\"text/javascript\">\n"
				+ "google.load('visualization', '1', {packages: ['gauge']});\n"
				+ "</script>\n"
				+ "<script type=\"text/javascript\">\n"
				+ "function drawVisualization() {\n"

				+ "var options = {width: 1000, height: 200, redFrom: 0, redTo: 95,\n"
				+ "               yellowFrom: 95, yellowTo: 98, greenFrom: 98, greenTo: 100, \n"
				+ "				  minorTicks: 5};\n"

				+ "var data = new google.visualization.DataTable();\n";

		String htmlFileContentsEnd = "new google.visualization.Gauge(document.getElementById('visualization')).\n"
				+ "draw(data, options);\n"
				+ " }\n"
				+ "google.setOnLoadCallback(drawVisualization);\n"
				+ "</script>\n"
				+ "</head>\n"
				+ "<body style=\"font-family: Arial;border: 0 none;\">\n"
				+ "<a href=\"javascript:history.go(-1)\"><img src=\"resources/tango-previous.png\" border=\"0\"></a>\n"
				+ "<h1>Group Availability</h1>\n"
				+ "<div id=\"visualization\"></div>\n"
				+ "</body>\n"
				+ "</html>";

		String htmlFileContents = htmlFileContentsTop
				+ this.getAvailabilityGaugeData() + htmlFileContentsEnd;

		File tmp = new File(tempWorkingDir);
		if (!tmp.exists()) {
			if (!tmp.mkdirs()) {
				log.error("ERROR creating temporary directory: "
						+ tempWorkingDir);
			}
		}

		fu.removeFile(tmp + "/" + this.dataFileName);
		fu.writeToFile(tmp + "/" + dataFileName, htmlFileContents);
	}

}
