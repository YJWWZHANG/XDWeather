package com.zqbweather.app.util;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.content.SharedPreferences;

import com.zqbweather.app.activity.MyApplication;

public class SAXUtil extends DefaultHandler{
//	<guangzhou dn="nay">
//	<city cityX="333" cityY="158" cityname="�ӻ���" centername="�ӻ���" fontColor="FFFFFF" pyName="" state1="23" state2="8" stateDetailed="�󵽱���ת����" tem1="12" tem2="17" temNow="13" windState="΢��" windDir="����" windPower="1��" humidity="99%" time="19:10" url="101280103"/>
//	<city cityX="247" cityY="193" cityname="����" centername="����" fontColor="FFFFFF" pyName="" state1="23" state2="8" stateDetailed="�󵽱���ת����" tem1="13" tem2="17" temNow="13" windState="΢��" windDir="������" windPower="1��" humidity="99%" time="19:10" url="101280105"/>
//	<city cityX="397" cityY="197" cityname="������" centername="������" fontColor="FFFFFF" pyName="" state1="23" state2="8" stateDetailed="�󵽱���ת����" tem1="14" tem2="18" temNow="15" windState="΢��" windDir="����" windPower="2��" humidity="99%" time="19:10" url="101280104"/>
//	<city cityX="289" cityY="255" cityname="������" centername="������" fontColor="FFFF00" pyName="" state1="23" state2="8" stateDetailed="�󵽱���ת����" tem1="13" tem2="17" temNow="14" windState="΢��" windDir="���Ϸ�" windPower="1��" humidity="95%" time="19:10" url="101280101"/>
//	<city cityX="321"0 cityY="331"1 cityname="��خ"2 centername="��خ"3 
//	fontColor="FFFFFF"4 pyName=""5 state1="23"6 state2="8"7 
//			stateDetailed="�󵽱���ת����"8 tem1="14"9 tem2="18"10 temNow="15"11 
//			windState="΢��"12 windDir="����"13 windPower="1��"14 humidity="98%"15 
//			time="19:10"16 url="101280102"17/>
//	</guangzhou>
	
//	private String nodeName;
//	private StringBuilder id;
//	private StringBuilder name;
//	private StringBuilder version;
	
	private static String tem1;
	private static String tem2;
	private static String temNow;
	private static String time;
	private static String stateDetailed;
	private static String url;
	
	private static String county;
	
	private boolean selection = true;
	
//	private String a;
	
	@Override
	public void startDocument() throws SAXException{
		selection = true;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException{
		if(selection){
			stateDetailed = attributes.getValue(8);
			tem1 = attributes.getValue(9);
			tem2 = attributes.getValue(10);
			temNow = attributes.getValue(11);
			time = attributes.getValue(16);
			url = attributes.getValue(17);
			if(county.equals(url)){
				selection = false;
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException{

	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException{

	}
	
	@Override
	public void endDocument()throws SAXException{

	}	
	
	public static void saxHandle(String countyCode, String response){
		county = countyCode;
		try{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader xmlReader = factory.newSAXParser().getXMLReader();
			ContentHandler handler = new SAXUtil();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(new StringReader(response)));					
		}catch(Exception e){
			e.printStackTrace();
		}

		SharedPreferences.Editor editor = MyApplication.getContext().
				getSharedPreferences("flash.weather.com.cn_wmaps_xml_china", Context.MODE_PRIVATE).edit();

//		editor.putBoolean("city_selected", true);
		editor.putString("tem1", tem1);
		editor.putString("tem2", tem2);
		editor.putString("temNow", temNow);
		editor.putString("time", time);
		editor.putString("stateDetailed", stateDetailed);
		editor.putString("url", url);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",
				Locale.CHINA);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
	
	private static void saveWeatherInfo(String tem1,
			String tem2, String temNow, String time, String stateDetailed,
			String url) {
		// TODO Auto-generated method stub
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", 
//				Locale.CHINA);

	}
	
}
