package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;




public class KeynoteParse {

	private ArrayList<Keynote> knList = new ArrayList<Keynote>();
	
	
	public ArrayList<Keynote> getKeynoteData() {
		
		InputStreamReader isr=null;
		InputStream stream=null;
		try {
			//URL url = new URL("http://halley.exp.sis.pitt.edu/cn3mobile/allSessionsAndPresentations.jsp?eventid=86");
			
			//Use Post Method
			String urlString = new String("halley.exp.sis.pitt.edu/cn3mobile/allSessionsAndPapers.jsp?conferenceID=130&noAbstract=1");
			
			URL url = new URL(urlString);
			    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			    conn.setReadTimeout(10000 /* milliseconds */);
			    conn.setConnectTimeout(15000 /* milliseconds */);
			    conn.setRequestMethod("POST");
			    conn.setDoInput(true);
			    // Starts the query
			    conn.connect();
			    stream = conn.getInputStream(); 
			
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xr = saxParser.getXMLReader();

			KeynoteParseHandler shandler = new KeynoteParseHandler();
			xr.setContentHandler(shandler);
			isr = new InputStreamReader(stream, "iso-8859-1");
			//InputStreamReader isr = new InputStreamReader(entity.getContent(),"UTF-8");

			xr.parse(new InputSource(isr));
			stream.close();
			isr.close();
		} catch (Exception ee) {
			System.out.print(ee.toString());
		}
		finally{
			try {
				if(stream != null)
				stream.close();
				if(isr != null)
				isr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return knList;
	}

	
	private class KeynoteParseHandler extends DefaultHandler {
		private int state = 0;
		private Keynote ke;
		private boolean keynoteStart = false;
		
		public void startDocument() throws SAXException {
		}

		public void endDocument() throws SAXException {
		}

		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (localName.equals("Items")) {
				keynoteStart=true;
				return;
			}
			if (localName.equals("Item")) {
				ke = new Keynote();
				ke.ID= atts.getValue("eventSessionID").toString();
				return;
			}
			if (localName.equals("sessionName")&&localName.contains("Keynote")) {
				state = 1;
				return;
			}
			if (localName.equals("sessionDate")) {
				state = 2;
				return;
			}
			if (localName.equals("beginTime")&& keynoteStart==true) {
				state = 3;
				return;
			}
			if (localName.equals("endTime")&& keynoteStart == true) {
				state = 4;
				return;
			}
			if (localName.equals("location")) {
				state = 5;
				return;
			}
		}

		public void endElement(String namespaceURI, String localName,
				String qName) throws SAXException {
			if (localName.equals("Item")) {
				knList.add(ke);
				return;
			}
			if (localName.equals("Items")) {
				keynoteStart=false;
				return;
			}
		}

		public void characters(char ch[], int start, int length) {

			String content = new String(ch, start, length);
			switch (state) {
			case 1:
				//ke.name = content;				
				state=0;
				break;
			case 2:
				SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy-MM-dd");      
				Date date=new Date();
				try {
					date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(content);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String   str   =   formatter.format(date);
				//se.date = Datetrans.get(str);
				//se.day_id= Dtrans.get(str);
				state=0;
				break;
			case 3:
				SimpleDateFormat   formatter1   =   new   SimpleDateFormat   ("HH:mm");      
				Date date1=new Date();
				try {
					date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(content);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String   str1   =   formatter1.format(date1);
				ke.beginTime = str1;
				state=0;
				break;
			case 4:
				SimpleDateFormat   formatter2   =   new   SimpleDateFormat   ("HH:mm");      
				Date date2=new Date();
				try {
					date2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(content);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String   str2   =   formatter2.format(date2);
				ke.endTime = str2;
				state=0;
				break;
			case 5:
				ke.room = content;
				state=0;
				break;
			default:
				state=0;
				return;
			}
		}
	}

}
