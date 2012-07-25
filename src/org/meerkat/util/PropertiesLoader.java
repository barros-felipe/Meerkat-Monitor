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

package org.meerkat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.meerkat.gui.SimplePopup;

public class PropertiesLoader implements Serializable{

	private static final long serialVersionUID = 1008089266551755831L;
	private static Logger log = Logger.getLogger(PropertiesLoader.class);
	private String propertiesFile;

	/**
	 * Load properties from file
	 * 
	 * @param propertiesFile
	 * @return properties
	 */
	public synchronized Properties getPropetiesFromFile(String propertiesFile) {
		this.propertiesFile = propertiesFile;
		// Read properties file.
		Properties properties;
		properties = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propertiesFile);
			properties.load(fis);
		} catch (IOException e) {
			log.error("Properties file unavailable! " + propertiesFile, e);
			SimplePopup sp = new SimplePopup("Properties file unavailable!");
			sp.show();

		} finally {
			try {
				fis.close();
			} catch (IOException e) {
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
	public final void propertiesValidator(String[] propertiesList) {
		FileInputStream stream = null;
		String propertiesFileContents = "";
		try {
			stream = new FileInputStream(new File(propertiesFile));
		} catch (FileNotFoundException e1) {
			log.fatal("Properties file not found.", e1);
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
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				log.error("Error closing validation of properties file.", e);
			}
		}

		// Check existance of properties inside the properties file
		String missingProperties = "Following properties are missing in properties file:";
		int numberOfMissingProperties = 0;
		for (int i = 0; i < propertiesList.length; i++) {
			if (!propertiesFileContents.contains(propertiesList[i])) {
				missingProperties += "\n - " + propertiesList[i];
				numberOfMissingProperties++;
			}
		}

		if (numberOfMissingProperties > 0) {
			log.fatal("Required properties missing in properties file");
			SimplePopup sp = new SimplePopup(missingProperties);
			sp.show();

		} else {
			log.info("Validated required properties");
		}

	}

	/**
	 * writePropertiesToFile
	 * 
	 * @param p
	 * @param file
	 */
	public final void writePropertiesToFile(Properties p, String file) {
		try {
			p.store(new FileOutputStream(file), "Meerkat-Monitor\n meerkat-monitor.org");
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
		prop.put("meerkat.email.subjectPrefix", "not_defined");
		prop.put("meerkat.email.sending.test", "false");
		prop.put("meerkat.monit.test.time", "5");
		prop.put("meerkat.webserver.port", "6777");
		prop.put("meerkat.autosave.exit", "false");
		prop.put("meerkat.autoload.start", "false");
		prop.put("meerkat.ssl.keystore", "meerkatKeystore.jks");
		prop.put("meerkat.ssl.password", "meerkatKeystorePassword");
		prop.put("meerkat.dashboard.gauge", "true");
		prop.put("meerkat.webserver.rconfig", "false");
		prop.put("meerkat.password.master", "changeMe");

		Properties defaultProperties = new Properties();
		defaultProperties.putAll(prop);

		writePropertiesToFile(defaultProperties, file);

	}

}
