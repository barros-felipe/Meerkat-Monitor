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

import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.meerkat.sampleData.SampleData;
import org.meerkat.services.WebApp;
import org.meerkat.util.xml.XStreamMeerkatConfig;
import org.meerkat.webapp.WebAppCollection;

import com.thoughtworks.xstream.XStream;

public class MeerkatGeneralOperations {

	private static Logger log = Logger.getLogger(MeerkatGeneralOperations.class);
	private String xmlConfigFile;
	private String version;
	private String tempWorkingDir = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "meerkat/";


	public MeerkatGeneralOperations(String xmlConfigFile, String version){
		this.xmlConfigFile = xmlConfigFile;
		this.version = version;
		this.setupTempWorkingDir();
	}

	/**
	 * createTempWorkingDir
	 */
	private final void setupTempWorkingDir(){
		File tmp = new File(tempWorkingDir);
		FileUtil fu = new FileUtil();
		if (tmp.exists()) {
			fu.deleteDirectory(tmp);
		}
		if (!tmp.mkdir()) {
			log.fatal("FATAL: Cannot create temp directory - " + tmp.toString());
		}
	}

	/**
	 * getTmpWorkingDir
	 * @return temporary working dir
	 */
	public final String getTmpWorkingDir(){
		return tempWorkingDir;
	}

	/**
	 * loadWebAppsXML
	 * @return Collection of applications
	 */
	public final WebAppCollection loadWebAppsXML(){
		WebAppCollection webAppsCollection = null;
		FileUtil fu = new FileUtil();
		XStreamMeerkatConfig xstreamConfig = new XStreamMeerkatConfig();
		XStream xstream = xstreamConfig.getXstream();

		// Create empty config file if not present
		File config = new File(xmlConfigFile);
		if (!config.exists()) {
			fu.createEmptyXMLConfigFile(xmlConfigFile);
		}

		try {
			webAppsCollection = (WebAppCollection) xstream.fromXML(fu.readFileContents(xmlConfigFile));

			// Validate if we got and invalid Meerkat-Monitor file
			if (webAppsCollection.getWebAppCollectionSize() == null) {
				log.fatal("The application XML file is invalid!");
				log.fatal("Create an empty one and restart application.");
			}

		} catch (Exception e) {
			log.warn("- Config XML not found. Generating a default one...");
			webAppsCollection = new WebAppCollection();

			// Add Meerkat Monitor self test demo data
			webAppsCollection.addWebApp(SampleData.getSampleWebApp_SelfTestWSDL(), false);
			webAppsCollection.addWebApp(SampleData.getSampleWebService_SelfWSgetVersion(version), false);
			webAppsCollection.addWebApp(SampleData.getSampleSocketService_SelfHTTP_Port(), false);
		}

		webAppsCollection.setConfigFile(xmlConfigFile);
		webAppsCollection.initialize(version, tempWorkingDir, xmlConfigFile);

		Iterator<WebApp> waI = webAppsCollection.getWebAppCollectionIterator();
		WebApp wapp;
		log.info("Present Applications:");
		while (waI.hasNext()) {
			wapp = waI.next();
			log.info("\t" + wapp.getName());
			wapp.initialize(tempWorkingDir, version);
		}

		return webAppsCollection;
	}

	/**
	 * extractWebResourcesResources
	 */
	public final void extractWebResourcesResources(){
		// Create temp dir to hold the resources
		File resourcesDir = new File(tempWorkingDir + "resources" + "/"+ "images");
		if (!resourcesDir.mkdirs()) {
			log.error("ERROR: Failed to create resources directory!");
		}

		// Register resources
		String[] resources = new String[25];
		resources[0] = "resources/demo_page.css";
		resources[1] = "resources/demo_table_jui.css";
		resources[2] = "resources/jquery-ui-1.8.4.custom.css";
		resources[3] = "resources/favicon.ico";
		resources[4] = "resources/tango_blue.gif";
		resources[5] = "resources/tango_red_anime.gif";
		resources[6] = "resources/jquery.dataTables.js";
		resources[7] = "resources/jquery.js";
		resources[8] = "resources/meerkat.png";
		resources[9] = "resources/meerkat-small.png";
		resources[10] = "resources/tango_rss.png";
		resources[11] = "resources/tango_timeline.png";
		resources[12] = "resources/tango_edit-find.png";
		resources[13] = "resources/tango-previous.png";
		resources[14] = "resources/down-green.png";
		resources[15] = "resources/up-red.png";
		resources[16] = "resources/down-red.png";
		resources[17] = "resources/up-green.png";
		resources[18] = "resources/tango-slink.png";
		resources[19] = "resources/404_meerkat.png";
		resources[20] = "resources/tango_wsdl.png";
		resources[21] = "resources/tango-xml-config.png";
		resources[22] = "resources/tango-find-log.png";
		resources[23] = "resources/prettify.css";
		resources[24] = "resources/prettify.js";

		String[] resourcesImages = new String[13];
		resourcesImages[0] = "resources/images/ui-bg_flat_0_aaaaaa_40x100.png";
		resourcesImages[1] = "resources/images/ui-bg_flat_75_ffffff_40x100.png";
		resourcesImages[2] = "resources/images/ui-bg_glass_55_fbf9ee_1x400.png";
		resourcesImages[3] = "resources/images/ui-bg_glass_65_ffffff_1x400.png";
		resourcesImages[4] = "resources/images/ui-bg_glass_75_dadada_1x400.png";
		resourcesImages[5] = "resources/images/ui-bg_glass_75_e6e6e6_1x400.png";
		resourcesImages[6] = "resources/images/ui-bg_glass_95_fef1ec_1x400.png";
		resourcesImages[7] = "resources/images/ui-bg_highlight-soft_75_cccccc_1x100.png";
		resourcesImages[8] = "resources/images/ui-icons_2e83ff_256x240.png";
		resourcesImages[9] = "resources/images/ui-icons_222222_256x240.png";
		resourcesImages[10] = "resources/images/ui-icons_454545_256x240.png";
		resourcesImages[11] = "resources/images/ui-icons_888888_256x240.png";
		resourcesImages[12] = "resources/images/ui-icons_cd0a0a_256x240.png";

		ResourceManager rm;
		// Extract resources
		for (int i = 0; i < resources.length; i++) {
			rm = new ResourceManager(resources[i], tempWorkingDir);
			rm.getResource();
			// move the favicon to the dir root
			if (resources[i].contains("favicon.ico")) {
				FileUtil fu = new FileUtil();
				fu.moveFileToDir(tempWorkingDir + resources[i], tempWorkingDir);
			}
		}

		// Extract resource/images
		for (int i = 0; i < resourcesImages.length; i++) {
			rm = new ResourceManager(resourcesImages[i], tempWorkingDir);
			rm.getResource();
		}
	}

}
