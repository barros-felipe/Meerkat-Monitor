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

package org.meerkat.network;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.meerkat.util.DateUtil;
import org.meerkat.util.FileUtil;

public class RSS {

	private static Logger log = Logger.getLogger(RSS.class);

	private String title = "";
	private String description = "";
	private String link = "";
	private String imageUrl = "/resources/meerkat.png";
	private File rssFile;
	private String rssFileName = "rss.xml";
	private NetworkUtil netUtil;
	private DateUtil dateUtil;
	private List<String> items;
	private FileUtil fu;
	private int serverPort = 80;

	private String rssHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<rss version=\"2.0\">\n" + "<channel>\n";
	private String rssFooter = "</channel>\n" + "</rss>\n";

	/**
	 * RSS
	 * 
	 * @param title
	 * @param description
	 * @param lastBuildDate
	 * @param link
	 * @param imageUrl
	 */
	public RSS(String title, String description, String link,
			String tempWorkingDir) {
		this.title = title;
		this.description = description;
		this.link = link;
		netUtil = new NetworkUtil();

		items = new LinkedList<String>();

		fu = new FileUtil();
		String path = tempWorkingDir.replace("\\", "/");
		rssFile = new File(path + "/" + rssFileName);
		rssFile.deleteOnExit();

	}

	public final void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * addItem
	 * 
	 * @param title
	 * @param link
	 * @param pubDate
	 * @param description
	 */
	public final void addItem(String title, String nameLink, String pubDate,
			String description) {
		dateUtil = new DateUtil();
		String nowDate = dateUtil.getFormatedDateRSS();

		InetAddress netAddr;
		String hostname = "localhost";
		try {
			netAddr = InetAddress.getLocalHost();
			hostname = netAddr.getHostName();
		} catch (UnknownHostException e) {
			log.error("Cannot get hostname!", e);
		}

		String itemLink = "http://" + hostname + ":" + serverPort + "/"
				+ nameLink;

		String item = "<item>\n" + "<title>" + title + "</title>\n" + "<link>"
				+ itemLink + "</link>\n" + "<pubDate>" + nowDate
				+ "</pubDate>\n" + "<lastBuildDate>" + nowDate
				+ "</lastBuildDate>\n" + "<description>" + description + "\n["
				+ nowDate + "]</description>\n" + "</item>\n\n";

		items.add(item);
		log.info("Added new RSS event");
		refreshRSSFeed();

	}

	/**
	 * Generate the feed
	 * 
	 * @return
	 */
	public final void refreshRSSFeed() {
		String rssFeed = rssHeader + "<title>" + title + "</title>\n"
				+ "<link>" + link + "</link>\n" + "<description>" + description
				+ "</description>\n\n";
		String image = "<image>\n" + "<link></link>\n" + "<title></title>\n"
				+ "<url>" + "http://" + netUtil.getHostname() + ":"
				+ this.serverPort + imageUrl + "</url>\n"
				+ "<description></description>\n" + "</image>\n";

		rssFeed += image;

		Iterator<String> it = items.iterator();
		String currItem = "";

		// Copy items to array in inverted order so the events show up ordered
		// by date (most recent at top)
		String[] orderEvents = new String[items.size()];
		int posBottomUp = items.size();

		while (it.hasNext()) {
			currItem = it.next();
			orderEvents[posBottomUp - 1] = currItem;
			posBottomUp--;
		}

		for (int i = 0; i < items.size(); i++) {
			rssFeed += orderEvents[i];
		}

		rssFeed += rssFooter;

		// Refresh the RSS file
		fu.writeToFile(rssFile.getAbsolutePath(), rssFeed);

	}

}
