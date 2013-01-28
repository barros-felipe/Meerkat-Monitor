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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesLoader {

	private static Logger log = Logger.getLogger(PropertiesLoader.class);
	private String propertiesFile;
	private String[] expectedProperties;
	
	/**
	 * PropertiesLoader
	 */
	public PropertiesLoader(String propertiesFile){
		this.propertiesFile = propertiesFile;
		
		// NOTE: all properties are also referenced in 
		//		 in function generateDefaultPropertiesFile()
		expectedProperties = new String[23];
		expectedProperties[0] = "meerkat.email.send.emails";
		expectedProperties[1] = "meerkat.email.smtp.server";
		expectedProperties[2] = "meerkat.email.smtp.security";
		expectedProperties[3] = "meerkat.email.smtp.port";
		expectedProperties[4] = "meerkat.email.smtp.user";
		expectedProperties[5] = "meerkat.email.smtp.password";
		expectedProperties[6] = "meerkat.email.to";
		expectedProperties[7] = "meerkat.email.from";
		expectedProperties[8] = "meerkat.email.subjectPrefix";
		expectedProperties[9] = "meerkat.email.sending.test";
		expectedProperties[10] = "meerkat.monit.test.time";
		expectedProperties[11] = "meerkat.webserver.port";
		expectedProperties[12] = "meerkat.ssl.keystore";
		expectedProperties[13] = "meerkat.ssl.password";
		expectedProperties[14] = "meerkat.dashboard.gauge";
		expectedProperties[15] = "meerkat.webserver.rconfig";
		expectedProperties[16] = "meerkat.password.master";
		expectedProperties[17] = "meerkat.webserver.logaccess";
		expectedProperties[18] = "meerkat.webserver.showapptype";
		expectedProperties[19] = "meerkat.embeddeddb.user";
		expectedProperties[20] = "meerkat.embeddeddb.passwd";
		expectedProperties[21] = "meerkat.embeddeddb.dbname";
		expectedProperties[22] = "meerkat.app.timeline.maxrecords";
		
		File tmpPropFile = new File(propertiesFile);
		if(!tmpPropFile.exists()){
			log.warn("- Properties not found. Generating a default one...");
			generateDefaultPropertiesFile(propertiesFile);
		}
	}
	
	
	/**
	 * Load properties from file
	 * 
	 * @param propertiesFile
	 * @return properties
	 */
	public synchronized Properties getPropetiesFromFile() {
		// Read properties file.
		Properties properties;
		properties = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propertiesFile);
			properties.load(fis);
		} catch (IOException e) {
			log.error("Properties file unavailable! " + propertiesFile, e);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				log.error("ERROR closing properties file!", e);
			}
		}

		return properties;

	}

	/**
	 * propertiesValidator
	 * 
	 * @param propertiesList
	 */
	public final boolean validateProperties() {
		FileInputStream stream = null;
		String propertiesFileContents = "";
		try {
			stream = new FileInputStream(new File(propertiesFile));
		} catch (FileNotFoundException e1) {
			log.fatal("Properties file not found.", e1);
			return false;
		}
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			/* Instead of using default, pass in a decoder. */
			propertiesFileContents = Charset.defaultCharset().decode(bb)
					.toString();
		} catch (IOException e) {
			log.error("Error validating properties file.", e);
			return false;
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				log.error("Error closing validation of properties file.", e);
				return false;
			}
		}

		String missingProperties = validateStringProperties(propertiesFileContents);
		if(!missingProperties.equals("")){	
			log.error(missingProperties);
			log.fatal("Required properties missing!");
			return false;
		} else {
			missingProperties = "";
			log.info("Validated required properties");
			return true;
		}

	}

	/**
	 * validateStringProperties
	 * @param strProperties
	 */
	public final String validateStringProperties(String strProperties){
		String missingProperties = "Following properties are missing:";
		
		int numberOfMissingProperties = 0;
		for (int i = 0; i < expectedProperties.length; i++) {
			if (!strProperties.contains(expectedProperties[i])) {
				missingProperties += "\n - " + expectedProperties[i];
				numberOfMissingProperties++;
			}
		}
		
		if (numberOfMissingProperties > 0) {
			log.error(missingProperties);
		} else {
			missingProperties = "";
		}
		
		return missingProperties;
	}
	
	
	/**
	 * writePropertiesToFile
	 * 
	 * @param p
	 * @param file
	 */
	public final void writePropertiesToFile(Properties p) {
		try {
			p.store(new FileOutputStream(propertiesFile), "Meerkat-Monitor\n meerkat-monitor.org");
		} catch (FileNotFoundException e) {
			log.error("Failed to write properties file (not found)!", e);
		} catch (IOException e) {
			log.error("Failed to write properties file!", e);
		}
	}
	
	/**
	 * generateDefaultPropertiesFile
	 * 
	 * @param p
	 * @param file
	 */
	public final void generateDefaultPropertiesFile(String file) {
		Map<String, String> prop = new HashMap<String, String>();

		prop.put("meerkat.email.send.emails", "false");
		prop.put("meerkat.email.smtp.server", "not_defined");
		prop.put("meerkat.email.smtp.security", "none");
		prop.put("meerkat.email.smtp.port", "25");
		prop.put("meerkat.email.smtp.user", "not_defined");
		prop.put("meerkat.email.smtp.password", "not_defined");
		prop.put("meerkat.email.to", "not_defined@domain");
		prop.put("meerkat.email.from", "not_defined@domain");
		prop.put("meerkat.email.subjectPrefix", "Meerkat-Monitor");
		prop.put("meerkat.email.sending.test", "false");
		prop.put("meerkat.monit.test.time", "5");
		prop.put("meerkat.webserver.port", "6777");
		prop.put("meerkat.ssl.keystore", "meerkatKeystore.jks");
		prop.put("meerkat.ssl.password", "meerkatKeystorePassword");
		prop.put("meerkat.dashboard.gauge", "true");
		prop.put("meerkat.webserver.rconfig", "true");
		prop.put("meerkat.password.master", "changeMe");
		prop.put("meerkat.webserver.logaccess", "true");
		prop.put("meerkat.webserver.showapptype", "true");
		prop.put("meerkat.embeddeddb.user", "meerkat");
		prop.put("meerkat.embeddeddb.passwd", "meerkatmonitor");
		prop.put("meerkat.embeddeddb.dbname", "db");
		prop.put("meerkat.app.timeline.maxrecords", "2500");

		Properties defaultProperties = new Properties();
		defaultProperties.putAll(prop);

		writePropertiesToFile(defaultProperties);

	}
	
	/**
	 * getPropertiesFile
	 * @return propertiesFile
	 */
	public final String getPropertiesFile(){
		return propertiesFile;
	}

}
