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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesLoader {

	// NOTE1: all properties are also referenced in 
	//		  in function generateDefaultPropertiesFile()
	// NOTE2: In case of new additions don't forget to
	// 		  add data type validation in function 
	//		  validateStringProperties()	
	private static String[] expectedProperties = {
		"meerkat.email.send.emails", 
		"meerkat.email.smtp.server", 
		"meerkat.email.smtp.security", 
		"meerkat.email.smtp.port", 
		"meerkat.email.smtp.user", 
		"meerkat.email.smtp.password", 
		"meerkat.email.to", 
		"meerkat.email.from", 
		"meerkat.email.subjectPrefix", 
		"meerkat.email.sending.test", 
		"meerkat.monit.test.time", 
		"meerkat.webserver.port", 
		"meerkat.ssl.keystore", 
		"meerkat.ssl.password", 
		"meerkat.dashboard.gauge", 
		"meerkat.webserver.rconfig", 
		"meerkat.password.master", 
		"meerkat.webserver.logaccess", 
		"meerkat.webserver.showapptype", 
		"meerkat.embeddeddb.user", 
		"meerkat.embeddeddb.passwd", 
		"meerkat.embeddeddb.dbname", 
		"meerkat.app.timeline.maxrecords"
	};
	
	private static Logger log = Logger.getLogger(PropertiesLoader.class);
	private String propertiesFile;
	private String missingProperties = "";
	
	/**
	 * PropertiesLoader
	 */
	public PropertiesLoader(String propertiesFile){
		this.propertiesFile = propertiesFile;
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

		this.missingProperties = validateStringProperties(propertiesFileContents);
		if(!this.missingProperties.equals("")){	
			log.error(this.missingProperties);
			log.fatal("Required properties missing!");
			return false;
		} else {
			this.missingProperties = "";
			return true;
		}

	}

	/**
	 * getMissingProperties
	 * @return
	 */
	public final String getMissingProperties(){
		return missingProperties;
	}
	
	
	/**
	 * validateStringProperties
	 * @param strProperties
	 */
	public static String validateStringProperties(String strProperties){
		String missingProperties = "Invalid data: ";
		
		int numberOfMissingProperties = 0;
		for (int i = 0; i < expectedProperties.length; i++) {
			if (!strProperties.contains(expectedProperties[i])) {
				missingProperties += "\n - " + expectedProperties[i];
				numberOfMissingProperties++;
			}
		}
		
		if (numberOfMissingProperties > 0) {
			log.error(missingProperties);
			return missingProperties;
		} else {
			// Validate data types
			boolean allValid = true;
			Properties props = getStringContentAsProperties(strProperties);
			
			if(!DataTypePropertiesValidator.isBoolean(props.getProperty("meerkat.email.send.emails"))){
				missingProperties += "Email Notifications, ";
				allValid = false;
			}
			
			if(!DataTypePropertiesValidator.isInteger(props.getProperty("meerkat.email.smtp.port"))){
				missingProperties += "SMTP Port, ";
				allValid = false;
			}
			
			//not validating TO address(es) for now... Just FROM
			
			if(!DataTypePropertiesValidator.isEmailAddress(props.getProperty("meerkat.email.from"))){
				missingProperties += "FROM Address, ";
				allValid = false;
			}
			
			if(!DataTypePropertiesValidator.isBoolean(props.getProperty("meerkat.email.sending.test"))){
				missingProperties += "Send test email at startup, ";
				allValid = false;
			}
			
			if(!DataTypePropertiesValidator.isInteger(props.getProperty("meerkat.monit.test.time"))){
				missingProperties += "Pause between rounds, ";
				allValid = false;
			}
			
			if(!DataTypePropertiesValidator.isInteger(props.getProperty("meerkat.webserver.port")) || 
					Integer.parseInt(props.getProperty("meerkat.webserver.port")) <= 1024){
				missingProperties += "Web server port (should be >1024), ";
				allValid = false;
			}
			
			if(!DataTypePropertiesValidator.isBoolean(props.getProperty("meerkat.dashboard.gauge"))){
				missingProperties += "Availability gauge, ";
				allValid = false;
			}
			
			if(!DataTypePropertiesValidator.isBoolean(props.getProperty("meerkat.webserver.rconfig"))){
				missingProperties += "Allow access to applications config, ";
				allValid = false;
			}
			
			if(!DataTypePropertiesValidator.isBoolean(props.getProperty("meerkat.webserver.logaccess"))){
				missingProperties += "Allow access to application log, ";
				allValid = false;
			}
			
			if(!DataTypePropertiesValidator.isBoolean(props.getProperty("meerkat.webserver.showapptype"))){
				missingProperties += "Show application type, ";
				allValid = false;
			}
			
			if(!DataTypePropertiesValidator.isInteger(props.getProperty("meerkat.app.timeline.maxrecords")) || 
					Integer.parseInt(props.getProperty("meerkat.webserver.port")) <= 0){
				missingProperties += "Timeline Max. records, ";
				allValid = false;
			}

			if(allValid){
				return "";
			}else{
				// Remove the last ", "
				StringBuilder b = new StringBuilder(missingProperties);
				b.replace(missingProperties.lastIndexOf(", "), missingProperties.lastIndexOf(", ") + 2, "" );
				missingProperties = b.toString();	
				
				return missingProperties;
			}
		}
		
		
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
		prop.put("meerkat.email.smtp.server", "smtp.domain");
		prop.put("meerkat.email.smtp.security", "none");
		prop.put("meerkat.email.smtp.port", "25");
		prop.put("meerkat.email.smtp.user", "user");
		prop.put("meerkat.email.smtp.password", "password");
		prop.put("meerkat.email.to", "user@domain");
		prop.put("meerkat.email.from", "user@domain");
		prop.put("meerkat.email.subjectPrefix", "Meerkat-Monitor Notification");
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
	 * getPropertiesAsContentString
	 * @param prop
	 * @return
	 */
	public static String getPropertiesAsContentString(Properties prop){
		String propAsContentString = "";
		Enumeration<?> keys = prop.keys();
		
		while (keys.hasMoreElements()) {
		  String key = (String)keys.nextElement();
		  String value = (String)prop.get(key);
		  propAsContentString += key+"="+value+"\n";
		}
		
		return propAsContentString;
	}
	

	/**
	 * getStringContentAsProperties
	 * @param strProps
	 * @return
	 */
	public static Properties getStringContentAsProperties(String strProps){
		Properties prop = new Properties();
		String[] rawProperties = strProps.split("\n");
		
		String[] currProp;
		for(int i=0; i<rawProperties.length; i++){
			if(!rawProperties[i].contains("#")){ // discard properties commments
				currProp = rawProperties[i].split("=");
				prop.put(currProp[0], currProp[1].replace("\n", "").replace("\r", ""));
			}
		}
		
		return prop;
	}
	
	
	
	/**
	 * getPropertiesFile
	 * @return propertiesFile
	 */
	public final String getPropertiesFile(){
		return propertiesFile;
	}

}
