/**
 * Meerkat Monitor - Network Monitor Tool
 * Copyright (C) 2013 Merkat-Monitor
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

package org.meerkat.config;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionUtils {

	/**
	 * verifyUserSession
	 * @param session
	 * @return
	 */
	public static String verifyUserSession(HttpSession session){
		UserInfo userInfo = (UserInfo)session.getAttribute("UserInfo");
		if(userInfo == null || !userInfo.isValid()){
			session.invalidate();
			return "Your session is invalid!<br>Please logout/login to create a new one.";
		}

		SettingsManager setManag = new SettingsManager(userInfo);
		if(!setManag.getErrorString().equalsIgnoreCase("")){
			return (setManag.getErrorString());
		}
		
		return "OK";
	}
	
	/**
	 * invalidateSession
	 * @param session
	 * @param resp
	 */
	public static void invalidateSession(HttpSession session, HttpServletResponse resp){
		session.setAttribute("currentSessionUser", "");
		session.setAttribute("currentSessionKey", "");
		session.setAttribute("currentSessionWSDL", "");
		session.setAttribute("active", "");
		session.invalidate();
		try {
			resp.sendRedirect("index.html");
		} catch (IOException e) {

		}
	}
}
