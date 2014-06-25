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

import org.eclipse.jetty.util.log.Logger;

public class JettyCustomLogger implements Logger{

	@Override
	public void debug(Throwable arg0) {
	}

	@Override
	public void debug(String arg0, Object... arg1) {	
	}

	@Override
	public void debug(String arg0, Throwable arg1) {
	}

	@Override
	public Logger getLogger(String arg0) {
		return this;
	}

	@Override
	public String getName() {
		return "JettyCustomLogger";
	}

	@Override
	public void ignore(Throwable arg0) {
	}

	@Override
	public void info(Throwable arg0) {	
	}

	@Override
	public void info(String arg0, Object... arg1) {	
	}

	@Override
	public void info(String arg0, Throwable arg1) {	
	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void setDebugEnabled(boolean arg0) {
	}

	@Override
	public void warn(Throwable arg0) {
	}

	@Override
	public void warn(String arg0, Object... arg1) {
	}

	@Override
	public void warn(String arg0, Throwable arg1) {	
	}

}
