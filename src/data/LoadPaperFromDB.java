package data;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;




public class LoadPaperFromDB {

	private ArrayList<Paper> pList;
	private Hashtable<String,String> Datetrans, Dtrans;
	

	
	public LoadPaperFromDB()
	{
		this.daytoDate();
		this.daytoid();
	}
	public ArrayList<Paper> getPaperData() {
		
		InputStreamReader isr=null;
		InputStream stream=null;
		try {
			//URL url = new URL("http://halley.exp.sis.pitt.edu/cn3mobile/allSessionsAndPresentations.jsp?eventID=86");
			
			//Use Post Method
				String urlString = new String("http://halley.exp.sis.pitt.edu/cn3mobile/allSessionsAndPresentations.jsp?conferenceID=130");
			
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

			PaperParseHandler shandler = new PaperParseHandler();
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
		
		return pList;
	}
		private class PaperParseHandler extends DefaultHandler {
			private int state = 0;
			private Paper p;
			private boolean presentationStart = false;
			
			public void startDocument() throws SAXException {
				
			}

			public void endDocument() throws SAXException {
			}

			public void startElement(String namespaceURI, String localName,
					String qName, Attributes atts) throws SAXException {
				if (localName.equals("PRESENTATIONS")) {
					presentationStart = true;
					pList = new ArrayList<Paper>();
					return;
				}

				if (localName.equals("PRESENTATION")) {					
					p = new Paper();
					p.presentationID = atts.getValue("presentationID").toString();
					p.id= atts.getValue("contentID").toString();
					p.belongToSessionID=atts.getValue("eventSessionID").toString();
					return;
				}
				if (localName.equals("presentationDate")) {
					state = 1;
					return;
				}
				if (localName.equals("beginTime")&& presentationStart == true) {
					state = 2;
					return;
				}
				if (localName.equals("endTime")&& presentationStart == true) {
					state = 3;
					return;
				}
				if (localName.equals("presentationType")) {
					state = 4;
					return;
				}
			}

			public void endElement(String namespaceURI, String localName,
					String qName) throws SAXException {
				if (localName.equals("PRESENTATION")) {
					pList.add(p);
					return;
				}
				if (localName.equals("PRESENTATIONS")) {
					presentationStart=false;
					return;
				}
			}

			public void characters(char ch[], int start, int length) {

				String content = new String(ch, start, length);
				switch (state) {
				case 1:
					SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy-MM-dd");      
					Date date=new Date();
					try {
						date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(content);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String   str   =   formatter.format(date);
					p.date = getDate(str);
					p.day_id= getDayid(str);
					state=0;
					break;
				case 2:
					SimpleDateFormat   formatter1   =   new   SimpleDateFormat   ("HH:mm");      
					Date date1=new Date();
					try {
						date1=new SimpleDateFormat("yy-MM-dd HH:mm:ss.S").parse(content);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String   str1   =   formatter1.format(date1);
					p.exactbeginTime = str1;
					state=0;
					break;
				case 3:
					SimpleDateFormat   formatter2   =   new   SimpleDateFormat   ("HH:mm");      
					Date date2=new Date();
					try {
						date2=new SimpleDateFormat("yy-MM-dd HH:mm:ss.S").parse(content);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String   str2   =   formatter2.format(date2);
					p.exactendTime = str2;
					state=0;
					break;
				case 4:
					p.type=content;
					state=0;
					break;
				default:
					state=0;
					return;
				}
			}
		}
		/*public String getTitle(String id){
			String title;
			if(Titletrans.containsKey(id))
				title = Titletrans.get(id).toString();
			else
				title ="No title information available.";
				
			return title;
		}
		public String getAbstract(String id){
			String Abstract;
			if(Abstracttrans.containsKey(id))
				Abstract = Abstracttrans.get(id).toString();
			else
				Abstract ="No abstract information available.";
				
			return Abstract;
		}
		public String getAuthor(String id){
			String Author;
			if(Authortrans.containsKey(id))
				Author = Authortrans.get(id).toString();
			else
				Author ="No author information available.";
				
			return Author;
		}*/
		public String getDate(String date){
			String date_string;
			if(Datetrans.containsKey(date))
				date_string = Datetrans.get(date);
			else
				date_string ="Saturday, Feb 23";
				
			return date_string;
		}
		public String getDayid(String date){
			String dayid;
			if(Dtrans.containsKey(date))
				dayid = Dtrans.get(date);
			else
				dayid ="0";
				
			return dayid;
		}
		public void daytoDate(){
			Datetrans = new Hashtable<String, String>();
			Datetrans.put("2014-09-01", "Monday, Sept.1");
			Datetrans.put("2014-09-02", "Tuesday, Sept.2");
			Datetrans.put("2014-09-03", "Wednesday, Sept.3");
			Datetrans.put("2014-09-04", "Thursday, Sept.4");
			//Datetrans.put("2014-07-11", "Friday, July 11");//new one
			
		}
		public void daytoid(){
			Dtrans = new Hashtable<String, String>();
			Dtrans.put("2014-09-01", "1");
			Dtrans.put("2014-09-02", "2");
			Dtrans.put("2014-09-03", "3");
			Dtrans.put("2014-09-04", "4");
			//Dtrans.put("2014-07-11", "5");// new one
			
		
		}            
}
