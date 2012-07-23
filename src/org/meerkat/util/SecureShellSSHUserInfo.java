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

import com.jcraft.jsch.UserInfo;

public class SecureShellSSHUserInfo implements UserInfo {

	private String passwd;

	/**
	 * SecureShellSSHUserInfo
	 * 
	 * @param password
	 */
	public SecureShellSSHUserInfo(String passwd) {
		this.passwd = passwd;
	}

	@Override
	public String getPassphrase() {
		return "";
	}

	@Override
	public String getPassword() {
		return passwd;
	}

	@Override
	public boolean promptPassphrase(String arg0) {
		return false;
	}

	@Override
	public boolean promptPassword(String arg0) {
		return false;
	}

	@Override
	public boolean promptYesNo(String arg0) {
		return false;
	}

	@Override
	public void showMessage(String arg0) {
	}

}
