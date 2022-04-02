package main;

import header.CBlackListItem;
import header.CXMLElement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.concurrent.CopyOnWriteArrayList;


import security.util.CCrypto;
import util.clogger.CLogger;
import util.clogger.LogBuffer;
import manager.CAccessKeyManager;
import manager.CAdminSessionManager;
import manager.CBackUpManager;
import manager.CBlackListManager;
import manager.CHTTPResourceManager;
import manager.CIManager;
import manager.CRequestManager;
import manager.CRouterManager;
//import manager.CSSLManager;
import manager.CUpdateManager;


public abstract class CSysConfig {
	
	private final static String ResourceConfig = "config.xml";
	
	//public static CSSLManager 			SSLManager 			= null;
	public static CAccessKeyManager 	AccessKeyManager 	= null;
	public static CAdminSessionManager 	AdminSessionManager = null;
	public static CBackUpManager 		BackUpManager 		= null;
	public static CUpdateManager		UpdateManager		= null;
	public static CRequestManager		RequestManager		= null;
	public static CBlackListManager		BlackListManager	= null;
	public static CHTTPResourceManager  HTTPResourceManger 	= null;
	public static CRouterManager	    RouterManager 		= null;
	
	public static boolean 	AKMAutoSaveEnabled 	= false;
	public static boolean   RemoteAccessEnabled = true;
	public static boolean   ProxyEnabled 	= false;
	
	public  static String 	ServerName 			= "Internet Access Manager";
	public  static String 	localHostIP 		= null;
	public  static String 	localHostName 		= null;
	public  static String 	sysUsername  		= "admin";
	public  static String 	sysPassword 		= "pass";
	

	public  static String   HTTPProtocol		= "HTTP/1.1";
	
	public 	static String	RtrTelnetIP			= "192.168.1.1";
	public 	static String	RtrTelnetUsername	= "root";
	public 	static String	RtrTelnetPassword	= "pass";
	
	public  static String 	ResourceDirectory 	= "Resource";
	
	public static int 		MaxAccessKeyPool 	= 25; 
	public static int 		UnusedKeyDuration 	= 5; // in minutes
	public static int 		AdminIdleDuration 	= 5; // in minutes
	public static int 		AdsPopUpChance		= 5; // in percentage
	public static int 		ProxyPort		 	= 3128;
	public static int 		WebPort				= 80;
	

	public static CopyOnWriteArrayList<CBlackListItem> BlackListed = null;
	
	
	public CSysConfig(String HostIP,String HostName) throws Exception
	{
		//SSLManager 			= new CSSLManager();
		AccessKeyManager 	= new CAccessKeyManager();
		AdminSessionManager = new CAdminSessionManager();
		RequestManager		= new CRequestManager();
		BackUpManager 		= new CBackUpManager();
		UpdateManager		= new CUpdateManager();
		BlackListManager    = new CBlackListManager();
		HTTPResourceManger  = new CHTTPResourceManager();
		RouterManager 		= new CRouterManager();
		
		localHostIP 		= HostIP;
		localHostName 		= HostName;
		
		BlackListed = new CopyOnWriteArrayList<CBlackListItem>();
		
		loadLogger();
		
		CSysConfigReader reader = null;

		try
		{
			CLogger.log(CLogger.LogType.SYSTEM , "Loading {0} configuration file.", ResourceConfig);
			reader = new CSysConfigReader(ResourceConfig);

			CLogger.log(CLogger.LogType.SYSTEM , "Applying {0} configuration file to System.", ResourceConfig);
			reader.extractConfiguration(this);
			
			
		}catch(FileNotFoundException e) {
			CLogger.log(CLogger.LogType.SYSERROR , "No {0} found.",ResourceConfig);
			CLogger.log(CLogger.LogType.SYSERROR , "Creating {0} file.",ResourceConfig);
			

			SaveConfigDetailsToFile();
			
			// test newly created ConfigDetail file by loading
			CLogger.log(CLogger.LogType.SYSTEM , "Reloading {0} configuration file.", ResourceConfig);
			reader = new CSysConfigReader(ResourceConfig);

			CLogger.log(CLogger.LogType.SYSTEM , "Applying {0} configuration file to System.", ResourceConfig);
			reader.extractConfiguration(this);
			
			
		}catch(Exception e)
		{
			CLogger.log(CLogger.LogType.SYSERROR , "Enable to parse {0}",ResourceConfig);
			throw e;
		}
		

		loadManagers();
		
		
	}
	
	public static void SaveConfigDetailsToFile() throws Exception
	{
		File xmlConfig = new File(ResourceConfig);
		
		CXMLElement XMLSysElement			 	= new CXMLElement("SYSTEM");
		CXMLElement XMLAKMAutoSaveEnabled 		= new CXMLElement("AKMAutoSaveEnabled",Boolean.toString(AKMAutoSaveEnabled));
		CXMLElement XMLRemoteAccessEnabled 		= new CXMLElement("RemoteAccessEnabled",Boolean.toString(RemoteAccessEnabled));
		CXMLElement XMLProxyEnabled		 		= new CXMLElement("ProxyEnabled",Boolean.toString(ProxyEnabled));
		CXMLElement XMLServerName 				= new CXMLElement("ServerName",ServerName);
		CXMLElement XMLHTTPProtocol 			= new CXMLElement("HTTPProtocol",HTTPProtocol);
		CXMLElement XMLsysPassword 				= new CXMLElement("sysPassword",CCrypto.encrypt(sysPassword));
		CXMLElement XMLsysUsername 				= new CXMLElement("sysUsername",CCrypto.encrypt(sysUsername));
		CXMLElement XMLRtrTelnetIP		 		= new CXMLElement("RtrTelnetIP",RtrTelnetIP);
		CXMLElement XMLRtrTelnetUsername 		= new CXMLElement("RtrTelnetUsername",CCrypto.encrypt(RtrTelnetUsername));
		CXMLElement XMLRtrTelnetPassword 		= new CXMLElement("RtrTelnetPassword",CCrypto.encrypt(RtrTelnetPassword));
		CXMLElement XMLwebDirectory 			= new CXMLElement("ResourceDirectory",ResourceDirectory);
		CXMLElement XMLMaxAccessKeyPool 		= new CXMLElement("MaxAccessKeyPool",Integer.toString(MaxAccessKeyPool));
		CXMLElement XMLUnusedKeyDuration 		= new CXMLElement("UnusedKeyDuration",Integer.toString(UnusedKeyDuration));
		CXMLElement XMLAdminIdleDuration 		= new CXMLElement("AdminIdleDuration",Integer.toString(AdminIdleDuration));
		CXMLElement XMLAdsPopUpChance			= new CXMLElement("AdsPopUpChance",Integer.toString(AdsPopUpChance));
		CXMLElement XMLProxyPort		 		= new CXMLElement("ProxyPort",Integer.toString(ProxyPort));
		CXMLElement XMLWebPort					= new CXMLElement("WebPort",Integer.toString(WebPort));
		CXMLElement XMLBlackListed				= new CXMLElement("BlackListed");
		
		
		
		XMLSysElement.addElement(XMLAKMAutoSaveEnabled);
		XMLSysElement.addElement(XMLRemoteAccessEnabled);
		XMLSysElement.addElement(XMLProxyEnabled);
		XMLSysElement.addElement(XMLServerName);
		XMLSysElement.addElement(XMLHTTPProtocol);
		XMLSysElement.addElement(XMLsysPassword);
		XMLSysElement.addElement(XMLsysUsername);
		XMLSysElement.addElement(XMLRtrTelnetIP);
		XMLSysElement.addElement(XMLRtrTelnetUsername);
		XMLSysElement.addElement(XMLRtrTelnetPassword);
		XMLSysElement.addElement(XMLwebDirectory);
		XMLSysElement.addElement(XMLMaxAccessKeyPool);
		XMLSysElement.addElement(XMLUnusedKeyDuration);
		XMLSysElement.addElement(XMLAdminIdleDuration);
		XMLSysElement.addElement(XMLAdsPopUpChance);
		XMLSysElement.addElement(XMLProxyPort);
		XMLSysElement.addElement(XMLWebPort);
		
		for(CBlackListItem item : BlackListed)
		{
			CXMLElement XMLCBlackListIemElement = new CXMLElement("CBlackListItem");

			XMLCBlackListIemElement.addElement(new CXMLElement("keyword",item.getKeyword()));
			XMLCBlackListIemElement.addElement(new CXMLElement("exact_check",Boolean.toString(item.isExactCheck())));
			XMLCBlackListIemElement.addElement(new CXMLElement("case_sen",Boolean.toString(item.isCaseSensitive())));
			XMLBlackListed.addElement(XMLCBlackListIemElement);
		}

		XMLSysElement.addElement(XMLBlackListed);
		
		FileWriter fstream = new FileWriter(xmlConfig);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(XMLSysElement.toString());
		out.flush();
		out.close();
		
		CLogger.log(CLogger.LogType.SYSTEM , "Successfully save {0} configuration  to Config.xml!", ResourceConfig);
		
	}
	
	private boolean loadManager(CIManager manager,LogBuffer buf)
	{
		boolean success = false;
		CLogger.log(CLogger.LogType.SYSTEM ,buf, "Loading {0} Manager.", manager.getType());

		if(manager.initialize(buf))
		{
			CLogger.log(CLogger.LogType.SYSTEM ,buf,  "Successfully loaded {0} Manager.", manager.getType());

			if(manager.isRunnable())
			{
				Thread thread = new Thread((Runnable) manager, manager.getType() + " Manager");
				thread.start();
				CLogger.log(CLogger.LogType.SYSTEM ,buf, "{0} Manager is now running.", manager.getType());
			}
			success = true;
		} else CLogger.log(CLogger.LogType.SYSERROR ,buf, "Error on loading {0} Manager!!", manager.getType());
		
		CLogger.flushLog(buf);
		return success;
	}
	
	protected void loadLogger() { 

		CLogger.load(); 
		CLogger.log(CLogger.LogType.SYSTEM , "Loading Logger.");
	}
	
	protected void loadManagers()
	{
		LogBuffer buf = new LogBuffer();
		//loadManager(SSLManager,buf);
		loadManager(AccessKeyManager,buf);
		loadManager(AdminSessionManager,buf);
		loadManager(RequestManager,buf);
		loadManager(BackUpManager,buf);
		loadManager(UpdateManager,buf);
		loadManager(BlackListManager,buf);
		loadManager(HTTPResourceManger,buf);
		//loadManager(RouterManager,buf);
	}
}
