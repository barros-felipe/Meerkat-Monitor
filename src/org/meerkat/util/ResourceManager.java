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

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceManager {

	private static org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(ResourceManager.class);
	private String resourceName;
	private URI uri;
	private File tempDir;

	public ResourceManager(String resourceName, String tempDir) {
		this.tempDir = new File(tempDir);
		this.resourceName = resourceName;
		uri = getJarURI();
	}

	/**
	 * getResource
	 * 
	 * @return
	 */
	public final String getResource() {
		URI theFileResource = URI.create("");
		theFileResource = getFile(uri, resourceName);
		return theFileResource.toString().replace("file:/", "");
	}

	/**
	 * getFile
	 * 
	 * @param where
	 * @param fileName
	 * @return
	 */
	private URI getFile(final URI where, final String fileName) {
		File location;
		URI fileURI = null;
		ZipFile zipFile = null;

		location = new File(where);

		// not in a JAR, just return the path on disk
		if (location.isDirectory()) {
			fileURI = URI.create(where.toString() + fileName);
		} else {
			try {
				zipFile = new ZipFile(location);
				fileURI = extract(zipFile, fileName);
			} catch (Exception e) {
				log.error("Error extracting resource from jar\n", e);
			} finally {
				try {
					zipFile.close();
				} catch (Exception e) {
					log.error("Error closing jar after extract resource", e);
				}
			}
		}
		return fileURI;
	}

	/**
	 * 
	 * @param zipFile
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private URI extract(final ZipFile zipFile, final String fileName)
			throws IOException {
		String filePath = "";
		File tempFile = null;
		ZipEntry entry;
		InputStream zipStream;
		OutputStream fileStream;

		try {
			tempFile = new File(tempDir + "/" + fileName);
		} catch (Exception e) {
			log.error("ERROR creating creating resource file!", e);
		}

		// tempFile.deleteOnExit();
		entry = zipFile.getEntry(fileName);

		if (entry == null) {
			throw new FileNotFoundException("cannot find file: " + fileName
					+ " in archive: " + zipFile.getName());
		}

		zipStream = zipFile.getInputStream(entry);
		fileStream = null;

		try {
			final byte[] buf;
			int i;

			fileStream = new FileOutputStream(tempFile);
			buf = new byte[1024];
			i = 0;

			while ((i = zipStream.read(buf)) != -1) {
				fileStream.write(buf, 0, i);
			}
		} finally {
			close(zipStream);
			close(fileStream);
		}

		// Remove the URI "file:/" and save the full path and URI spaces
		filePath = tempFile.toURI().toString();
		filePath = filePath.replaceFirst("file:/", "");
		filePath = filePath.replaceAll("%20", " ");

		return (tempFile.toURI());
	}

	/**
	 * getJarURI
	 * 
	 * @return
	 */
	private URI getJarURI() {
		ProtectionDomain domain;
		CodeSource source;
		URL url;
		URI localURI = null;

		domain = ResourceManager.class.getProtectionDomain();
		source = domain.getCodeSource();
		url = source.getLocation();
		try {
			localURI = url.toURI();
		} catch (URISyntaxException e) {
			log.error("Error getting jar URI - URISyntaxException", e);
		}
		return localURI;
	}

	/**
	 * close
	 * 
	 * @param stream
	 */
	private void close(final Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (final IOException ex) {
				log.error("Error closing stream ", ex);
			}
		}
	}

}
