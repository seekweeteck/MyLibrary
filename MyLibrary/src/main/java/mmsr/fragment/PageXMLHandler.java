package mmsr.fragment;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PageXMLHandler extends DefaultHandler {

	Boolean currentElement = false;
	String currentValue = null;
	public static PageList sitesList = null;

	public static PageList getSitesList() {
		return sitesList;
	}

	public static void setSitesList(PageList sitesList) {
		PageXMLHandler.sitesList = sitesList;
	}

	/** Called when tag starts ( ex:- <name>AndroidPeople</name> 
	 * -- <name> )*/
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		currentElement = true;

		if (localName.equals("book"))
		{
			/** Start */ 
			sitesList = new PageList();
		} 

	}

	/** Called when tag closing ( ex:- <name>AndroidPeople</name> 
	 * -- </name> )*/
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		currentElement = false;

		/** set value */ 
		if (localName.equalsIgnoreCase("en"))
			sitesList.setEN(currentValue);
		if (localName.equalsIgnoreCase("bm"))
			sitesList.setBM(currentValue);
		if (localName.equalsIgnoreCase("cn"))
			sitesList.setCN(currentValue);
		if (localName.equalsIgnoreCase("tm"))
			sitesList.setTM(currentValue);
		else if (localName.equalsIgnoreCase("picture"))
			sitesList.setPicture(currentValue);

	}

	/** Called to get tag characters ( ex:- <name>AndroidPeople</name> 
	 * -- to get AndroidPeople Character ) */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (currentElement) {
			currentValue = new String(ch, start, length);
			currentElement = false;
		}

	}

}
