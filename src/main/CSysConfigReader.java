package main;

import header.CBlackListItem;
import header.CXMLElement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


import security.util.CCrypto;
import util.clogger.CLogger;
import util.clogger.LogBuffer;




public class CSysConfigReader {
	private StringBuffer buffer = new StringBuffer();
	CXMLElement XMLconfig = null;
	public CSysConfigReader(String resource) throws FileNotFoundException, 
													UnsupportedEncodingException
	{
		BufferedReader lineReader = new BufferedReader(new FileReader(resource));
		
		String line = null;
		try {
			
			while( (line = lineReader.readLine()) != null)
			{
				buffer.append(line);
			}
			
			lineReader.close();
			
			XMLconfig = new CXMLElement("Config");
			XMLconfig.addElement(CXMLElement.parse(buffer.toString()));
		} catch (IOException e) {}
		
	}
	
	private boolean XMLConfigGetBoolean(String Element,boolean default_value,LogBuffer buff)
	{
		CXMLElement element = XMLconfig.getElement(Element);
		String value = null;
		if(element != null)
		{
			value = element.value;
			CLogger.log(CLogger.LogType.SYSTEM,buff, "\tConfig \"{0}\" = {1}!",Element,value );
		}
		else
		{
			value = Boolean.toString(default_value);
			CLogger.log(CLogger.LogType.SYSTEM,buff, "\tConfig \"{0}\" not found in XML! will use default value = {1}",Element,value );
		}
		
		return Boolean.parseBoolean(value);
	}
	
	private int XMLConfigGetInteger(String Element,int default_value,LogBuffer buff)
	{
		CXMLElement element = XMLconfig.getElement(Element);
		String value = null;
		if(element != null)
		{
			value = element.value;
			CLogger.log(CLogger.LogType.SYSTEM,buff, "\tConfig \"{0}\" = {1}!",Element,value );
		}
		else
		{
			value = Integer.toString(default_value);
			CLogger.log(CLogger.LogType.SYSTEM,buff, "\tConfig \"{0}\" not found in XML! will use default value = {1}",Element,value );
		}
		
		return Integer.parseInt(value);
	}
	
	private String XMLConfigGetString(String Element,String default_value,LogBuffer buff)
	{
		CXMLElement element = XMLconfig.getElement(Element);
		String value = null;
		if(element != null)
		{
			value = element.value;
			if(value == null) value = default_value;
			CLogger.log(CLogger.LogType.SYSTEM,buff, "\tConfig \"{0}\" = {1}!",Element,value );
		}
		else
		{
			value = default_value;
			CLogger.log(CLogger.LogType.SYSTEM,buff, "\tConfig \"{0}\" not found in XML! will use default value = {1}",Element,value );
		}
		return value;
	}
	
	private String XMLConfigGetStringEncrypted(String Element,String default_value,LogBuffer buff)
	{
		CXMLElement element = XMLconfig.getElement(Element);
		String value = null;
		if(element != null)
		{
			value = element.value;
			CLogger.log(CLogger.LogType.SYSTEM,buff, "\tConfig \"{0}\" = {1}!",Element,value );
		}
		else
		{
			value = CCrypto.encrypt(default_value);
			CLogger.log(CLogger.LogType.SYSTEM,buff, "\tConfig \"{0}\" not found in XML! will use default value = {1}",Element,value );
		}
		return value;
	}
	
	@SuppressWarnings("static-access")
	public void extractConfiguration(CSysConfig system) throws Exception
	{
		LogBuffer buff = new LogBuffer();
		XMLconfig =  XMLconfig.getElement("SYSTEM");
		system.AKMAutoSaveEnabled 	= XMLConfigGetBoolean("AKMAutoSaveEnabled",system.AKMAutoSaveEnabled,buff);
		system.RemoteAccessEnabled 	= XMLConfigGetBoolean("RemoteAccessEnabled",system.RemoteAccessEnabled,buff);
		system.ProxyEnabled 		= XMLConfigGetBoolean("ProxyEnabled",system.ProxyEnabled,buff);
		system.ServerName 			= XMLConfigGetString("ServerName",system.ServerName,buff);
		system.HTTPProtocol 		= XMLConfigGetString("HTTPProtocol",system.HTTPProtocol,buff);
		system.sysUsername 			= CCrypto.decrypt(XMLConfigGetStringEncrypted("sysUsername",system.sysUsername,buff));
		system.sysPassword 			= CCrypto.decrypt(XMLConfigGetStringEncrypted("sysPassword",system.sysPassword,buff));
		system.RtrTelnetIP 			= XMLConfigGetString("RtrTelnetIP",system.RtrTelnetIP,buff);
		system.RtrTelnetUsername	= CCrypto.decrypt(XMLConfigGetStringEncrypted("RtrTelnetUsername",system.RtrTelnetUsername,buff));
		system.RtrTelnetPassword 	= CCrypto.decrypt(XMLConfigGetStringEncrypted("RtrTelnetPassword",system.RtrTelnetPassword,buff));
		system.ResourceDirectory 	= XMLConfigGetString("ResourceDirectory",system.ResourceDirectory,buff);
		system.MaxAccessKeyPool 	= XMLConfigGetInteger("MaxAccessKeyPool",system.MaxAccessKeyPool ,buff); 
		system.UnusedKeyDuration 	= XMLConfigGetInteger("UnusedKeyDuration",system.UnusedKeyDuration,buff); 
		system.AdminIdleDuration 	= XMLConfigGetInteger("AdminIdleDuration",system.AdminIdleDuration,buff); 
		system.AdsPopUpChance		= XMLConfigGetInteger("AdsPopUpChance",system.AdsPopUpChance,buff); 
		system.ProxyPort		 	= XMLConfigGetInteger("ProxyPort",system.ProxyPort,buff); 
		system.WebPort				= XMLConfigGetInteger("WebPort",system.WebPort,buff);
		
		int i=0;
		for(CXMLElement element : XMLconfig.getElement("BlackListed").elements)
		{
			if(i == 0) 
				CLogger.log(CLogger.LogType.SYSTEM,buff, "\tApplying Black List Item!");
			
			CBlackListItem item = new CBlackListItem(element.getElement("keyword").value);
			item.setCaseSensitivity(Boolean.parseBoolean(element.getElement("case_sen").value));
			item.setExactChecking(Boolean.parseBoolean(element.getElement("exact_check").value));
			system.BlackListed.add(item);

			CLogger.log(CLogger.LogType.SYSTEM,buff, "\t\tItem[{0}] = {1}/{2}/{3}!",
														Integer.toString(i++),
														element.getElement("keyword").value,
														element.getElement("case_sen").value,
														element.getElement("exact_check").value);
		}
		
		if(i == 0)
			CLogger.log(CLogger.LogType.SYSTEM,buff, "\tNo BlackList Item Loaded.!");
		
		CLogger.flushLog(buff);
	}
}
