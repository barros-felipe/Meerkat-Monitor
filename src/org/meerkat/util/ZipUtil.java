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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class ZipUtil {
	private static Logger log = Logger.getLogger(ZipUtil.class);
	private int dataBuffer = 1024;

	public ZipUtil() {
	}

	/**
	 * createZip
	 * 
	 * @param zipFile
	 * @param filenames
	 */
	public final void createZip(String zipFile, String[] filenames) {

		try {
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(zipFile)));
			BufferedInputStream in = null;
			byte[] data = new byte[dataBuffer];

			for (int i = 0; i < filenames.length; i++) {
				if (filenames[i] != null) {
					in = new BufferedInputStream(new FileInputStream(
							filenames[i]), dataBuffer);
					File temp = new File(filenames[i]);
					out.putNextEntry(new ZipEntry(temp.getName()));
					int count;
					while ((count = in.read(data, 0, dataBuffer)) != -1) {
						out.write(data, 0, count);
					}
					out.closeEntry();
				}
			}
			out.flush();
			out.close();
			in.close();
		} catch (Exception e) {
			log.error("Error creating file: " + zipFile, e);
		}

	}

	/**
	 * unzip
	 * 
	 * @param zipFile
	 * @param destDir
	 */
	public final void unzip(String zipFile, String destDir) {
		ZipFile zip = null;
		try {
			zip = new ZipFile(zipFile);
		} catch (IOException e1) {
			log.error("Error accessing data file: " + zipFile, e1);
		}
		File destination = new File(destDir);
		if (!destination.exists() || !destination.isDirectory()) {
			if (!destination.mkdirs()) {
				log.error("Cannot create/access dir: " + destDir);
			}
		}

		Enumeration<? extends ZipEntry> files = zip.entries();
		File f = null;
		FileOutputStream fos = null;

		while (files.hasMoreElements()) {
			try {
				ZipEntry entry = files.nextElement();
				InputStream eis = zip.getInputStream(entry);
				byte[] buffer = new byte[dataBuffer];
				int bytesRead = 0;

				f = new File(destination.getAbsolutePath() + File.separator
						+ entry.getName());

				if (entry.isDirectory()) {
					if (!f.mkdirs()) {
						log.warn("Cannot remove " + f.toString());
					}
					continue;
				} else {
					if (!f.getParentFile().mkdirs()) {
						// log.error("Cannot create "+f.toString());
					}

					if (!f.createNewFile()) {
						// log.error("Cannot create "+f.toString());
					}
				}

				fos = new FileOutputStream(f);

				while ((bytesRead = eis.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				log.error("Error reading datafile: " + zipFile, e);
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						log.error("Error closing datafile: " + zipFile, e);
					}
				}
			}
		}
	}

}
