package security;



import java.net.MalformedURLException;

import java.net.URL;
import java.util.Random;

import util.CStringHelper;

import main.CSysConfig;
import main.CSysDaemon;
import manager.CAdminSessionManager;
import manager.CHTTPResourceManager;


import header.CAccessKey;
import header.CHTTPRequest;

public class CSecurityAgent {
	
		public static enum MsgType { 
									 SA_ERRRES   , 		// Web Page Error
									 SA_PROXRES  , 		// Proxy Server Response
									 SA_RSRCRES  , 		// Resource Request Response
									 SA_ADMINRES , 		// Administrative Request Response
									 SA_UPGRADE  , 		// Upgrade Handler for Request
									 SA_ADSPAGE   		// Ads Insertion Request Response
								   };  	

		public static enum MsgCode {  
			
									 /*- Proxy Response Code -*/
									 C100  , 	// forward request
									 C102  , 	// authenticate user
									 
									 /*- Proxy Error Response Code -*/
									 C201 , 	// no key
									 C202 , 	// expired key
									 C203 , 	// invalid key
									 
									 /*- Administrative Mode Code -*/
									 C301 , 	// Administrative Mode
									 C302 , 	// key generate
									 C303 , 	// blacklisting
									 C304 , 	// Administrative Login
									 C305 ,	 	// Remote Administrative Access disabled
									 
									 /*- Web Error Response Code -*/
									 C400 , 	// bad request
									 C401 , 	// Unauthorized
									 C402 , 	// prohibited
									 C403 , 	// Payment Required
									 C404 , 	// Not Found
									 C405 ,		// Unauthorized
									 C406 ,		// OK
								 }; 	

									 
		private static URL getURL(CHTTPRequest request) throws MalformedURLException  
		{
			String protocol = (request.method.equals("CONNECT") ? 
													"https://" :  "http://");

			URL url = null;
			
			int indexOfcolon = request.Host.indexOf(":");
			
			String request_url = "";
			if(indexOfcolon > -1)
			{
				 request_url = request.url.substring(0, indexOfcolon);
			}
			else
			{
				request_url = request.url;
			}
			
			try
			{
					url = new URL(request_url);
			}catch(Exception e){
					url = new URL(protocol + request.Host + request_url);
			}
			
			
			return url;
		}
		
		private static boolean InsertableAds(String method,String referer)
		{
			if(!method.equals("GET") || referer.length() > 0) return false;
			
			Random randomGenerator = new Random();
			int randomInt = randomGenerator.nextInt(100);
			return randomInt <= CSysDaemon.AdsPopUpChance;
		}
		
		@SuppressWarnings("deprecation")
		public static CSecMsg getMsgCommand(CHTTPRequest request) throws MalformedURLException  
		{
			
			CSecMsg SAMsg = new CSecMsg();
			URL url = getURL(request);
			
			// determine if request intended to Proxy Server or a HTTP Server
			if(url.getHost().equals(CSysDaemon.localHostIP))
			{
				String filename = url.getFile();
				if(filename.equalsIgnoreCase("/admin") || filename.equalsIgnoreCase("/admin/"))
				{
					SAMsg.type = MsgType.SA_ADMINRES;
					if(CSysConfig.RemoteAccessEnabled || CSysConfig.localHostIP.equals(request.clientIP))
					{
						if(request.method.equalsIgnoreCase("POST"))
						{
	
							String POSTcontent = request.getPOST();
							  
							String login 		= CStringHelper.parsePOSTtoString(POSTcontent, "Login");
							String password 	= CStringHelper.parsePOSTtoString(POSTcontent, "Pass");
							String KeepActive 	= CStringHelper.parsePOSTtoString(POSTcontent, "KeepActive").trim();
							
							if(!CAdminSessionManager.authenticate(login, password))
							{
								SAMsg.content = "Wrong Username/password!! try again!";
							}
							else
							{
								CAdminSessionManager.AddAdminSession(request.clientIP, KeepActive.equalsIgnoreCase("true") );
								
							}
						}
					
						SAMsg.code = (CAdminSessionManager.hasSession(request.clientIP) ? MsgCode.C301 : MsgCode.C304);
						if(SAMsg.code  == MsgCode.C301)
						{
							if(request.getGET() != null)
								SAMsg.content = CStringHelper.parsePOSTtoString(request.getGET(), "Tab");
						}
					}
					else 
					{
						SAMsg.code = MsgCode.C305;
						SAMsg.content = "Remote Administrative Access is Disabled!";
					}
				}
				else if(filename.equalsIgnoreCase("/admin/process_key.action") && request.method.equalsIgnoreCase("POST"))
				{
					SAMsg.type = MsgType.SA_ADMINRES;
					if(CSysConfig.RemoteAccessEnabled || CSysConfig.localHostIP.equals(request.clientIP))
					{
						SAMsg.code = (CAdminSessionManager.hasSession(request.clientIP) ? MsgCode.C302 : MsgCode.C304);
						
						if(SAMsg.code == MsgCode.C302)
						{
							String POSTcontent = request.getPOST();
							String action = CStringHelper.parsePOSTtoString(POSTcontent, "action");
							if(action.equalsIgnoreCase("generate_key"))
							{
							int hours 	= Integer.parseInt(CStringHelper.parsePOSTtoString(POSTcontent, "hours")); 
							int minutes 	= Integer.parseInt(CStringHelper.parsePOSTtoString(POSTcontent, "minutes"));
							
							CAccessKey key  = CSysDaemon.AccessKeyManager.generateKey(hours, minutes);
							CSysDaemon.HTTPResourceManger.LAST_GENERATED_KEY = key.getKeyCode();
							CSysDaemon.HTTPResourceManger.LAST_TIME_GENERATED_KEY = key.getTimeCreated().toLocaleString();
							CSysDaemon.HTTPResourceManger.LAST_DURATION_GENERATED_KEY = key.getDuration();
							
							}
							else
							{
								String keycode = CStringHelper.parsePOSTtoString(POSTcontent, "key");
								CSysDaemon.AccessKeyManager.removeKey(keycode);
							}
						}
					}
					else 
					{
						SAMsg.code = MsgCode.C305;
						SAMsg.content = "Remote Administrative Access is Disabled!";
					}

				}
				else if(filename.equalsIgnoreCase("/admin/block_site.action") && request.method.equalsIgnoreCase("POST"))
				{

					SAMsg.type = MsgType.SA_ADMINRES;
					if(CSysConfig.RemoteAccessEnabled || CSysConfig.localHostIP.equals(request.clientIP))
					{
						SAMsg.code = (CAdminSessionManager.hasSession(request.clientIP) ? MsgCode.C303 : MsgCode.C304);
						if(SAMsg.code == MsgCode.C303)
						{
							String action = CStringHelper.parsePOSTtoString(request.getPOST(), "action");
							if(action.equals("add_keyword"))
							{
								String keyword				 	= CStringHelper.parsePOSTtoString(request.getPOST(), "txt_keyword_site");
								boolean case_sensitive_enabled 	= CStringHelper.parsePOSTtoString(request.getPOST(), "case_sen_enabled").equalsIgnoreCase("on");
								boolean exact_check_enabled	  	= CStringHelper.parsePOSTtoString(request.getPOST(), "exact_enabled").equalsIgnoreCase("on");
								
								CSysDaemon.BlackListManager.BlackList(keyword, exact_check_enabled, case_sensitive_enabled);
							}
							else if(action.equals("edit_keyword"))
							{
								
							}
							else
							{
								String keyword				 	= CStringHelper.parsePOSTtoString(request.getPOST(), "keyword");
								CSysDaemon.BlackListManager.removeToBlackList(keyword);
							}
						}
					}
					else 
					{
						SAMsg.code = MsgCode.C305;
						SAMsg.content = "Remote Administrative Access is Disabled!";
					}
					
				}
				else if(filename.equalsIgnoreCase("/authenticate") && request.method.equalsIgnoreCase("POST"))
				{
					String POSTcontent = request.getPOST();
					
					String KeyCode = CStringHelper.parsePOSTtoString(POSTcontent, "key");
					String redirectPage = CStringHelper.parsePOSTtoString(POSTcontent, "redirectpage");
					CAccessKey AccessKey = CSysDaemon.AccessKeyManager.getKey(KeyCode);
					if(AccessKey != null)
					{
						AccessKey.CAssignKey(request.clientIP);
						SAMsg.type 		= MsgType.SA_ADSPAGE;
						SAMsg.content 	= CStringHelper.convertToNonWebencoding(redirectPage);
					}
					else
					{
						// send "No Key found for user" message
						SAMsg.type = MsgType.SA_PROXRES;
						SAMsg.code = MsgCode.C201;
						SAMsg.content	= "Invalid Access Key!";
					}
				}
				else
				{
					boolean exist = CHTTPResourceManager.getContent(filename).length > 0;
					
					if(exist)
					{
						SAMsg.type = MsgType.SA_RSRCRES;
						SAMsg.code = MsgCode.C406;
					}
					else
					{
						SAMsg.type = MsgType.SA_ERRRES;
						SAMsg.code = MsgCode.C404;
						SAMsg.content	= "File Not Found!";
					}
				}
				
				
				if(SAMsg.type.equals(MsgType.SA_ADMINRES) 
				    && (!request.clientIP.equals(CSysDaemon.localHostIP) &&
				        !CSysDaemon.RemoteAccessEnabled) )
				{
					SAMsg.type = MsgType.SA_ERRRES;
					SAMsg.code = MsgCode.C405;
				}
			}
			else if(request.method.equals("CONNECT"))
			{
				// send "Upgrade handler" message
				SAMsg.type = MsgType.SA_UPGRADE;
			}
			else
			{
				// authenticate user [ returns 1 = authenticated , -1 expired key, 0 not]
				switch(CSysDaemon.AccessKeyManager.isIPMapped(request.clientIP))
				{
					case 1:
						
						// determine if ads is insertable
						if(InsertableAds(request.method,request.Referer))
						{
							SAMsg.type 		= MsgType.SA_ADSPAGE;
							SAMsg.content 	= request.url;
						}
						else
						{
							// determine if site being access is not prohibited or ban
							if(CSysDaemon.BlackListManager.isBlackListed(request.url) ||
							   CSysDaemon.BlackListManager.isBlackListed(request.Host)	)
							{
								// send "error 402 prohibited" message
								SAMsg.type = MsgType.SA_ERRRES;
								SAMsg.code = MsgCode.C402;
							}
							else
							{
								// send "forward request" message
								SAMsg.type = MsgType.SA_PROXRES;
								SAMsg.code = MsgCode.C100;
							}
						}
					break;
					case -1:
						// send "Key already Expired" message
						SAMsg.type 		= MsgType.SA_PROXRES;
						SAMsg.code 		= MsgCode.C202;
						SAMsg.content	= "Access Key Already Expired!";
					break;
					default:
						// send "No Key found for user" message
						SAMsg.type = MsgType.SA_PROXRES;
						SAMsg.code = MsgCode.C201;
					break;
				}
				
			}
			
			return SAMsg;
		}
}
