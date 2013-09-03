// $codepro.audit.disable logExceptions
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

import java.util.regex.Pattern;


public class DataTypePropertiesValidator {

	/**
	 * isBoolean
	 * @param value
	 * @return
	 */
	public static boolean isBoolean(String value){
		if(value.equals("true") || value.equals("false")){
			return true;
		}
		return false;
	}

	/**
	 * isInteger
	 * @param value
	 * @return
	 */
	public static boolean isInteger(String value){
		try{
			Integer.parseInt(value);
		}catch(Exception e){
			return false;
		}
		return true;
	}

	/**
	 * isEmailAddress
	 * @param value
	 * @return
	 */
	public static boolean isEmailAddress(String value){
		Pattern rfc2822 = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
		if (!rfc2822.matcher(value).matches()) {
			return false; // Invalid email address
		}
		return true;
	}


}
