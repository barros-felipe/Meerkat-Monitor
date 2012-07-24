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

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@SuppressWarnings("deprecation")
public class XmlFormatter implements Serializable {
	private static Logger log = Logger.getLogger(XmlFormatter.class);
	private static final long serialVersionUID = 6285958678250898966L;

	public XmlFormatter() {

	}

	/**
	 * format
	 * 
	 * @param unformattedXml
	 * @return formatted XML
	 */
	public final String format(String unformattedXml) {
		String uXml = unformattedXml.trim();
		try {
			final Document document = parseXmlFile(uXml);

			OutputFormat format = new OutputFormat(document);
			format.setLineWidth(65);
			format.setIndenting(true);
			format.setIndent(2);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);

			return out.toString();
		} catch (IOException e) {
			log.error("Parsing XML ", e);
			return "";
		}
	}

	/**
	 * parseXmlFile
	 * 
	 * @param in
	 * @return XML file contents
	 */
	private Document parseXmlFile(String in) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			is.setEncoding("UTF-8");
			return db.parse(is);
		} catch (ParserConfigurationException e) {
			log.error("ParserConfigurationException Parsing XML ");
			return null;
		} catch (SAXException e) {
			log.error("SAXException Parsing XML");
			return null;
		} catch (IOException e) {
			log.error("IOException Parsing XML ");
			return null;
		}
	}

}