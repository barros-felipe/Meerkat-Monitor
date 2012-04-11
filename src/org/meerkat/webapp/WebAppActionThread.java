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

package org.meerkat.webapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.meerkat.services.WebApp;

public class WebAppActionThread extends Thread {
	private static Logger log = Logger.getLogger(WebAppActionThread.class);
	private String command;
	private String webAppName = "";
	private String commandExecOutput = "";

	/**
	 * WebAppActionThread
	 * 
	 * @param webApp
	 */
	public WebAppActionThread(WebApp webApp) {
		this.command = webApp.getExecuteOnOffline();
		this.webAppName = webApp.getName();
	}

	/**
	 * run
	 */
	public final void run() {
		File commandFile = new File(command);
		if (commandFile.exists() && commandFile.canRead()
				&& commandFile.canExecute()) {
			int exitVal = 0;
			Runtime rt = Runtime.getRuntime();
			Process pr;
			BufferedReader input = null;
			try {
				pr = rt.exec(command);
				input = new BufferedReader(new InputStreamReader(
						pr.getInputStream()));

				// Grab the output
				String line = null;
				while ((line = input.readLine()) != null) {
					commandExecOutput += line + "\n";
				}
				exitVal = pr.waitFor();
			} catch (IOException e1) {
				log.error("ERROR in action IO ", e1);
			} catch (InterruptedException e2) {
				log.error("ERROR in action Interrupt ", e2);
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					log.error("ERROR closing input stream!", e);
				}
			}
			log.info("Action finished with code: " + exitVal + " on "
					+ webAppName);
		} else {
			log.error("Cannot execute: " + command + " for offline action of "
					+ webAppName);
		}
	}

	/**
	 * getExecutedCommandOutput
	 * 
	 * @return commandExecOutput
	 */
	public final String getExecutedCommandOutput() {
		return commandExecOutput;

	}

	/**
	 * getCommandExecOutput
	 * 
	 * @return commandExecOutput
	 */
	public final String getCommandExecOutput() {
		return commandExecOutput;
	}

	/**
	 * setCommandExecOutput
	 * 
	 * @param commandExecOutput
	 */
	public final void setCommandExecOutput(String commandExecOutput) {
		this.commandExecOutput = commandExecOutput;
	}
}
