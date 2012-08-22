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

package org.meerkat.util.xml;


public class XMLComparator {

	private String xmlBlock1; // The first block (which might contain the symbol
	// ?
	private String xmlBlock2; // The second block which we want to compare

	// against

	/**
	 * Compares two XML blocks, considering the symbol ? as any match so it can
	 * handle fields like dates (which are not static)
	 */
	public XMLComparator(String xmlFirstBlock, String xmlSecondBlock) {
		char startIDNewChar = '<';
		char cChar = '"';
		XmlFormatter formatter;
		this.xmlBlock1 = xmlFirstBlock;
		this.xmlBlock2 = xmlSecondBlock;

		// Format the XML's
		formatter = new XmlFormatter();
		xmlBlock1 = formatter.format(xmlBlock1);
		String xmlBlock1copy = xmlBlock1;
		String xmlBlock2copy = xmlBlock2;
		xmlBlock2 = formatter.format(xmlBlock2);

		int indexOfSymbol;
		// Handle values <parameter>?</parameter>
		while (xmlBlock1copy.contains(">?<")) {
			indexOfSymbol = xmlBlock1copy.indexOf(">?<");

			xmlBlock1copy = xmlBlock1copy.substring(0, indexOfSymbol + 1)
					+ "_"
					+ xmlBlock1copy.substring(indexOfSymbol + 2,
							xmlBlock1copy.length());

			// Replace in block 2 in the same index
			String tempPart2 = xmlBlock2copy.substring(indexOfSymbol + 2,
					xmlBlock2copy.length());
			// find the next closing " in block2
			int part2StartIndex = tempPart2.indexOf(startIDNewChar);

			xmlBlock2copy = xmlBlock2copy.substring(0, indexOfSymbol)
					+ ">?<"
					+ tempPart2.substring(part2StartIndex + 1,
							tempPart2.length());
		}
		this.xmlBlock2 = xmlBlock2copy;

		// Handle options <response requestID="?">
		while (xmlBlock1copy.contains("\"?\"")) {
			indexOfSymbol = xmlBlock1copy.indexOf("\"?\"");

			xmlBlock1copy = xmlBlock1copy.substring(0, indexOfSymbol + 1)
					+ "_"
					+ xmlBlock1copy.substring(indexOfSymbol + 2,
							xmlBlock1copy.length());

			// Replace in block 2 in the same index
			String tempPart2 = xmlBlock2copy.substring(indexOfSymbol + 2,
					xmlBlock2copy.length());
			// find the next closing " in block2
			int part2StartIndex = tempPart2.indexOf(cChar);

			xmlBlock2copy = xmlBlock2copy.substring(0, indexOfSymbol)
					+ "\"?\""
					+ tempPart2.substring(part2StartIndex + 1,
							tempPart2.length());
		}
		this.xmlBlock2 = xmlBlock2copy;
	}

	/**
	 * areXMLsEqual
	 * 
	 * @return
	 */
	public final boolean areXMLsEqual() {
		return xmlBlock1.equals(xmlBlock2) ? true : false;
	}

}
