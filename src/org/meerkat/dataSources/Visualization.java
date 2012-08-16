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

package org.meerkat.dataSources;

import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.meerkat.httpServer.HTMLComponents;
import org.meerkat.services.WebApp;
import org.meerkat.util.FileUtil;
import org.meerkat.webapp.WebAppEvent;

public class Visualization {

	private static Logger log = Logger.getLogger(Visualization.class);

	private String tempDir;
	private String appVersion;

	public Visualization() {
	}

	/**
	 * getJSAnnotatedTimeLine
	 * 
	 * @return JSAnnotatedTimeLine
	 */
	public final String getAnnotatedTimeLine(WebApp webApp) {
		WebAppEvent webAppEvent;
		String timeLineData = "";
		String timeLineBegin = "<script type='text/javascript'>\n"
				+ "google.load('visualization', '1', {'packages':['annotatedtimeline']});\n"
				+ "google.setOnLoadCallback(drawChart);\n"
				+ "function drawChart() {\n"
				+ "var data = new google.visualization.DataTable();\n"
				+ "data.addColumn('datetime', 'Date');\n"
				+ "data.addColumn('number', 'Network Latency');\n"
				+ "data.addColumn('number', 'Load Time');\n"
				+ "data.addColumn('string', 'status_desc');\n"
				+ "data.addRows([\n";

		String timeLineEnd = "]);\n"
				+ "var chart = new google.visualization.AnnotatedTimeLine(document.getElementById('chart_div'));\n"
				+ "chart.draw(data, {displayAnnotations: true, fill: 30});\n"
				+ "}\n" + "</script>\n";

		Iterator<WebAppEvent> ie = webApp.getEventListIterator();
		String latency = "";

		while (ie.hasNext()) {
			webAppEvent = ie.next();

			if (webAppEvent.getLatency().equalsIgnoreCase("N/A")) {
				latency = "undefined";
			} else {
				latency = webAppEvent.getLatency();
			}

			timeLineData += "\n[new Date(" + webAppEvent.getDateFormatedGWT()
					+ "), " + latency + "," + webAppEvent.getPageLoadTime()
					+ ", '" + webAppEvent.getDescription() + "'],";
		}

		/**
		 * IE FIX - remove the last comma NOK - [new Date(2010, 10, 17, 18, 20),
		 * 10,1.0, ''],]); OK - [new Date(2010, 10, 17, 18, 20), 10,1.0, '']]);
		 */
		String finalString = timeLineBegin + timeLineData + timeLineEnd;
		String temp = finalString.replace("],]);", "]]);");
		finalString = temp;

		return finalString;

	}

	/**
	 * getJSDataTable
	 * 
	 * @return JSDataTable
	 */
	public final String getDataTable(WebApp webApp) {
		WebAppEvent webAppEvent;
		String statusText = "";
		String dataTableData = "";
		String iconStatus = "";

		if (webApp.getlastStatus().equalsIgnoreCase("ONLINE")) {
			iconStatus = "<img src=\"resources/tango_blue.gif\" alt=\"Online\" align=\"absmiddle\" />&nbsp;"
					+ webApp.getName() + "\n";
		} else {
			iconStatus = "<img src=\"resources/tango_red_anime.gif\" alt=\"Offline\" align=\"absmiddle\" />&nbsp;"
					+ webApp.getName() + "\n";
		}

		String dataTableBegin = "<script type=\"text/javascript\" language=\"javascript\" src=\"resources/jquery.js\"></script>\n"
				+ "<script type=\"text/javascript\" language=\"javascript\" src=\"resources/jquery.dataTables.js\"></script>\n"
				+ "<script type=\"text/javascript\" charset=\"utf-8\">\n"
				+ "$(document).ready(function() {\n"
				+ "oTable = $('#example').dataTable({\n"
				+ "\"bJQueryUI\": true,\n"
				+ "\"sPaginationType\": \"full_numbers\",\n"
				+ "\"bStateSave\": true\n"
				+ "});\n"
				+ "} );\n"
				+ "</script>\n"
				+ "</head>\n"
				+ "<body id=\"dt_example\">"
				+ "<div id=\"container\">\n"
				+ "<a href=\"javascript:history.go(-1)\"><img src=\"resources/tango-previous.png\" border=\"0\"></a>\n"
				+ "<h1>"
				+ iconStatus
				+ "</h1>"
				+ "<div id=\"chart_div\" style=\"width: 900px; height: 300px;\"></div>\n"
				+ "\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"display\" id=\"example\">\n"
				+ "<thead>\n"
				+ "<tr>\n"
				+ "<th>Id</th>\n"
				+ "<th>Date</th>\n"
				+ "<th>Status</th>\n"
				+ "<th>Availability (%)</th>\n"
				+ "<th>Latency (ms)</th>\n" + "<th>Load Time (s)</th>\n";

		if (!webApp.getType().equals(WebApp.TYPE_SQL)
				&& !webApp.getType().equals(WebApp.TYPE_SOCKET)
				&& !webApp.getType().equals(WebApp.TYPE_SSH)) {
			dataTableBegin += "<th>HTTP Status</th>\n";
		}

		dataTableBegin += "<th>Description</th>\n" + "<th>Response</th>\n"
				+ "</tr>\n" + "</thead>\n" + "<tbody>\n";

		String dataTableEnd = "</tbody>\n" + "</table>\n";

		Iterator<WebAppEvent> ie = webApp.getEventListIterator();
		String latency = "";
		int eventID = 0;

		while (ie.hasNext()) {
			webAppEvent = ie.next();

			if (webAppEvent.getStatus().equalsIgnoreCase("0")) {
				statusText = "OFFLINE";
			} else {
				statusText = "ONLINE";
			}

			if (webAppEvent.getLatency().equalsIgnoreCase("N/A")) {
				latency = "undefined";
			} else {
				latency = webAppEvent.getLatency();
			}

			dataTableData += "<tr class=\"gradeA\">\n" + "<td>" + eventID
					+ "</td>\n" + "<td>" + webAppEvent.getDate() + "</td>\n"
					+ "<td class=\"center\">" + statusText + "</td>\n"
					+ "<td class=\"center\">" + webAppEvent.getAvailability()
					+ "</td>\n" + "<td class=\"center\">" + latency + "</td>\n"
					+ "<td class=\"center\">" + webAppEvent.getPageLoadTime()
					+ "</td>\n";

			if (!webApp.getType().equals(WebApp.TYPE_SQL)
					&& !webApp.getType().equals(WebApp.TYPE_SOCKET)
					&& !webApp.getType().equals(WebApp.TYPE_SSH)) {
				dataTableData += "<td class=\"center\">"
						+ webAppEvent.getHttpStatusCode() + "</td>\n";
			}

			dataTableData += "<td class=\"center\">"
					+ webAppEvent.getDescription()
					+ "</td>\n"
					+ "<td class=\"center\">"
					+ "<a href=\""
					+ webAppEvent.getCurrentResponseFile()
					+ "\" onclick=\"return popitup('"
					+ webAppEvent.getCurrentResponseFile()
					+ "')\"><img src=\"resources/tango_edit-find.png\" border=\"0\" alt=\"\" /></a>"
					+ "</td>\n" + "</tr>\n";

			eventID++;
		}
		return dataTableBegin + dataTableData + dataTableEnd;
	}

	/**
	 * writeWebAppDataFile
	 */
	public final void writeWebAppVisualizationDataFile(WebApp webApp) {
		this.tempDir = webApp.getTempDir();
		FileUtil fu = new FileUtil();
		HTMLComponents htmlc = new HTMLComponents(appVersion);

		String htmlFileContentsTop = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n"
				+"<html>\n"
				+ "<head>\n"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n"
				+ "<title>Meerkat - "
				+ webApp.getName()
				+ "</title>\n"
				+ "<link rel=\"icon\"  href=\"/favicon.ico\"  type=\"image/x-icon\" />\n"
				+ "<script type='text/javascript' src='http://www.google.com/jsapi'></script>\n"

				+ "<style type=\"text/css\" title=\"currentStyle\">\n"
				+ "	@import \"resources/demo_page.css\";\n"
				+ "	@import \"resources/demo_table_jui.css\";\n"
				+ "	@import \"resources/jquery-ui-1.8.4.custom.css\";\n"
				+ "</style>\n"

				+ "<script language=\"javascript\" type=\"text/javascript\">\n"
				+ "function popitup(url) {\n"
				+ "var width = 1000;\n"
				+ "var height = 600;\n"
				+ "var left = parseInt((screen.availWidth/2) - (width/2));\n"
				+ "var top = parseInt((screen.availHeight/2) - (height/2));\n"
				+ "var windowFeatures = \"width=\" + width + \",height=\" + height + \",status,scrollbars,resizable,left=\" + left + \",top=\" + top + \"screenX=\" + left + \",screenY=\" + top;\n"
				+ "newwindow = window.open(url, \"name\", windowFeatures);\n"
				+ "if (window.focus) {newwindow.focus()}\n"
				+ "return false;\n"
				+ "}\n"
				+ "</script>\n"

				+ "<style type=\"text/css\">\n"
				+ "#container {\n"
				+ "	width: 1000px;\n"
				+ "	margin: 10px auto;\n"
				+ "	padding: 0;\n"
				+ "}\n"
				+ "</style>\n";

		String htmlFileContentsEnd = "\n" + htmlc.getFooter()
				+ "</div></body>\n" + "</html>\n";

		String htmlFileContents = htmlFileContentsTop
				+ this.getAnnotatedTimeLine(webApp) + this.getDataTable(webApp)
				+ htmlFileContentsEnd;

		File tmp = new File(tempDir);
		if (!tmp.exists()) {
			if (!tmp.mkdirs()) {
				log.error("ERROR creating temporary file: " + tempDir);
			}
		}
		fu.removeFile(tmp + "/" + webApp.getDataFileName());
		fu.writeToFile(tmp + "/" + webApp.getDataFileName(), htmlFileContents);

	}

	/**
	 * setAppVersion
	 * 
	 * @param version
	 */
	public final void setAppVersion(String version) {
		appVersion = version;
	}
}
