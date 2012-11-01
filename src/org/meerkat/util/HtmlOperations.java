package org.meerkat.util;

public class HtmlOperations {

	/**
	 * addPrettifier
	 * @param originalCode
	 * @return
	 */
	public static final String addPrettifier(String originalCode){
		String css = "<link href=\"resources/prettify.css\" type=\"text/css\" rel=\"stylesheet\" />\n";
		String js = "<script type=\"text/javascript\" src=\"resources/prettify.js\"></script>\n";

		String header = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
				"<html>\n"+
				"<head>\n"+
				"<title></title>\n"+
				css+js+
				"</head>\n"+
				"<body onload=\"prettyPrint()\" bgcolor=\"white\">\n"+
				"<pre class=\"prettyprint\">\n";

		String footer = "</pre>\n"+
				"</body>\n"+
				"</html>";

		return header+"\n\n"+originalCode+"\n\n"+footer;
	}





}
