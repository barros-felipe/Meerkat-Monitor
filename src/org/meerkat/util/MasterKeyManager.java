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
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;
import org.meerkat.services.SQLService;
import org.meerkat.services.SecureShellSSH;
import org.meerkat.services.WebApp;
import org.meerkat.webapp.WebAppCollection;

public class MasterKeyManager {
	private static Logger log = Logger.getLogger(MasterKeyManager.class);

	private WebAppCollection wac;
	private PropertiesLoader pl;
	private String propertiesFile;

	/**
	 * MasterKeyManager
	 * @param propertiesFile
	 */
	public MasterKeyManager(String propertiesFile, WebAppCollection wac){
		this.propertiesFile = propertiesFile;
		this.wac = wac;
	}

	/**
	 * 
	 */
	public MasterKeyManager(){
		this.propertiesFile = "meerkat.properties";
	}

	/**
	 * getMasterKey
	 * @return
	 */
	public final String getMasterKey(){
		pl = new PropertiesLoader(propertiesFile);
		Properties prop = pl.getPropetiesFromFile();
		return prop.getProperty("meerkat.password.master");
	}

	/**
	 * changeMasterKey
	 * @param newMasterKey
	 */
	public synchronized void changeMasterKey(String newMasterKey){
		String currMasterPasswd = getMasterKey();

		// Change value in properties file first
		pl = new PropertiesLoader(propertiesFile);
		Properties prop = pl.getPropetiesFromFile();
		prop.setProperty("meerkat.password.master", newMasterKey);
		pl.writePropertiesToFile(prop, propertiesFile);

		// Update the password for all applications
		BasicTextEncryptor oldTextEncrypt = new BasicTextEncryptor();
		oldTextEncrypt.setPassword(currMasterPasswd);

		Iterator<WebApp> it = wac.getWebAppCollectionIterator();
		WebApp curr;
		String passwd, currPasswd;
		while(it.hasNext()){
			curr = it.next();
			String type = curr.getType();

			if(type.equals(WebApp.TYPE_SSH)){ // If SSH we need to update the encrypted password
				SecureShellSSH app = (SecureShellSSH)curr;
				passwd = app.getPassword();
				currPasswd = oldTextEncrypt.decrypt(passwd);
				app.setPasswd(currPasswd);

			}else if(type.equals(WebApp.TYPE_DATABASE)){ // If SQL we need to update the encrypted password
				SQLService app = (SQLService)curr;
				passwd = app.getPassword();
				currPasswd = oldTextEncrypt.decrypt(passwd);
				app.setPassword(currPasswd);
			}
		}
		wac.saveConfigXMLFile();
	}

	/**
	 * getEncryptedPassord
	 * @return
	 */
	public final String getEncryptedPassword(String decPasswd){
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(getMasterKey());

		return textEncryptor.encrypt(decPasswd);
	}

	/**
	 * getDecryptedPassord
	 * @param encPasswd
	 * @return
	 */
	public final String getDecryptedPassword(String encPasswd){
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(getMasterKey());
		String devPasswd = "";
		try{
			devPasswd = textEncryptor.decrypt(encPasswd);
		}catch(Exception e){
			log.error("Failed to decrypt password. (Wrong master key?)");
			log.error(e.getCause());
		}

		return devPasswd;

	}



}
