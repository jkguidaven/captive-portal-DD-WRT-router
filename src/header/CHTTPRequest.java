package header;



import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import util.CStringHelper;
import util.clogger.CLogger;
import util.clogger.LogBuffer;


public class CHTTPRequest {
	
	public String method 				= new String();
	public String url			 		= new String();
	public String Version 				= new String();
	public String UserAgent 			= new String();
	public String Host 					= new String();
	public String Referer 				= new String();
	public String IfModifiedSince 		= new String();
	public String mimeTypeAcceptable 	= new String();
	public String Authorization 		= new String();
	public String ContentType 			= new String();
	public int	  ContentSize 			= -1;
	public int    oldContentSize 		= -1;
	public String unrecognized 			= new String();
	public boolean pragmaNoCache 		= false;
	public boolean NonPersistent 		= true;
	public Socket  socket				= null;
	
	public String clientIP = null;
	private LogBuffer log = null;
	

	private String POSTContent 			= null;
	private String GETContent 			= null;
	
	public CHTTPRequest(LogBuffer log)
	{
		this.log = log;
	}
	
	public int getSize()
	{
		return ContentSize;
	}
	private String  getToken(StringTokenizer tk)
	{
	       String str ="";
	       if  (tk.hasMoreTokens())
	           str =tk.nextToken();
	       return str; 
	}  
	
	String  getRemainder(StringTokenizer tk)
	{
	       String str ="";
	       if  (tk.hasMoreTokens())
	           str =tk.nextToken();
	       while (tk.hasMoreTokens()){
	           str +=" " + tk.nextToken();
	       }
	       return str;
	}
	   
	@SuppressWarnings("deprecation")
	public boolean parse(String clientIP,Socket socket) 
	{
			this.socket = socket;
			this.clientIP = clientIP;
			
			
			DataInputStream lineIn = null;
			StringTokenizer tokenizer = null;
			
			try
			{

				InputStream in = socket.getInputStream();
				lineIn = new DataInputStream(in);
				int ElapseTime = 0;
				while(lineIn.available() <= 0)
				{
					if(ElapseTime>30000)
					{
						CLogger.log(CLogger.LogType.RQSTERROR ,log,"parsing HTTP Request.. Timeout!");
						return false;
					}
					Thread.sleep(20);
					ElapseTime+=20;
				}
				tokenizer = new StringTokenizer(lineIn.readLine());
				
			}catch(Exception e) { 
				CLogger.log(CLogger.LogType.RQSTERROR ,log,"Failed To Parse HTTP Request. Parsing Aborted!");
				return false; 
				
			}
			
			method = getToken(tokenizer).toUpperCase();
			url = getToken(tokenizer);
			
			int indexGETData = url.indexOf("?");
			if(indexGETData > -1)
			{
				GETContent = url.substring(indexGETData);
				url = url.substring(0,indexGETData);
			}
			
			Version = getToken(tokenizer);
			

		       while (true) 
			   {
		           try 
				   {
		        	   tokenizer = new StringTokenizer(lineIn.readLine());
		           } 
				   catch (Exception e) 
				   {
		               return false;
		           }
		           String Token = getToken(tokenizer); 
		           
		           if (0 == Token.length())
		               break;
		           
		           if (Token.equalsIgnoreCase("USER-AGENT:")) {
		               UserAgent = getRemainder(tokenizer);           
		           } else if (Token.equalsIgnoreCase("ACCEPT:")) {
		        	   mimeTypeAcceptable += " " + getRemainder(tokenizer);

		           } else if (Token.equalsIgnoreCase("REFERER:")) {
		               Referer = getRemainder(tokenizer);
		               
		           } else if (Token.equalsIgnoreCase("Host:")) {
		               Host = getRemainder(tokenizer);

		           } else if (Token.equalsIgnoreCase("PRAGMA:")) { 
		               Token = getToken(tokenizer);

		               if (Token.equalsIgnoreCase("NO-CACHE"))
		                   pragmaNoCache = true;
		               else
		                   unrecognized += "Pragma:" + Token + " "
		                       +getRemainder(tokenizer) +"\n";            
		           } else if (Token.equalsIgnoreCase("AUTHORIZATION:")) { 
		               Authorization=  getRemainder(tokenizer);
		               	
		           } else if (Token.equalsIgnoreCase("Proxy-Connection:"))
		           {     
		        	   String isAlive = getRemainder(tokenizer);
		        	   NonPersistent = !isAlive.equals("keep-alive");
		        	   unrecognized += Token + " " + isAlive + CStringHelper.CR;
		        	   
		           }else if (Token.equalsIgnoreCase("IF-MODIFIED-SINCE:")) {
		               // line =<If-Modified-Since: <http date>
		               // *** Conditional GET replaces HEAD method ***
		               String str = getRemainder(tokenizer);
		              int index = str.indexOf(";");
		              if (index == -1) {
		                   IfModifiedSince  =str;
		               } else {
		            	   IfModifiedSince  =str.substring(0,index);
		                  
		                  index = str.indexOf("=");
		                  if (index != -1) {
		                      str = str.substring(index+1);
		                      oldContentSize =Integer.parseInt(str);
		                  }
		              }
		           } else if (Token.equalsIgnoreCase("CONTENT-LENGTH:")) {
		               Token = getToken(tokenizer);
		               ContentSize =Integer.parseInt(Token);
		           } else if (Token.equalsIgnoreCase("CONTENT-TYPE:")) {
		               ContentType = getRemainder(tokenizer);
		           } else {  
		               unrecognized += Token + " " + getRemainder(tokenizer) + CStringHelper.CR;
		           }
		       }
		       
				if(url.contains("127.0.0.1") || Host.contains("127.0.0.1"))
				{
					url 	= url.replace("127.0.0.1", clientIP);
					Host 	= Host.replace("127.0.0.1", clientIP);
				}
		       
			return true;
	}
	
	
	public String toString(boolean sendUnkown)
	{
		   String strRequest="";
		   
	       if (0 == method.length())
	            method = "GET";
	       
	       strRequest = method +" "+ url + " HTTP/1.0" + CStringHelper.CR;

	       if (0 < UserAgent.length())
	    	   strRequest +="User-Agent:" + UserAgent + CStringHelper.CR;

	       if (0 < Referer.length())
	    	   strRequest+= "Referer:"+ Referer  + CStringHelper.CR;
	       
	       if (0 < Host.length())
	    	   strRequest+= "Host:"+ Host  + CStringHelper.CR;
	       
	       if (pragmaNoCache)
	    	   strRequest+= "Pragma: no-cache" + CStringHelper.CR;

	       if (0 < IfModifiedSince.length())
	    	   strRequest+= "If-Modified-Since: " + IfModifiedSince + CStringHelper.CR;
	           
	       // ACCEPT TYPES //
	       if (0 < mimeTypeAcceptable.length())
	    	   strRequest += "Accept: " + mimeTypeAcceptable + CStringHelper.CR;
	       else 
	    	   strRequest += "Accept: */"+"* \r\n";
	    
	       if (0 < ContentType.length())
	    	   strRequest += "Content-Type: " + ContentType   + CStringHelper.CR;

	       if (0 < ContentSize)
	    	   strRequest += "Content-Length: " + ContentSize + CStringHelper.CR;
	                           
	       
	       if (0 != Authorization.length())
	    	   strRequest += "Authorization: " + Authorization + CStringHelper.CR;

	       if (sendUnkown) {
	           if (0 != unrecognized.length())
	        	   strRequest += unrecognized;
	       }   

	       strRequest += CStringHelper.CR;
	       
		   return strRequest;
	}
	
	public InputStream getInputStream() throws IOException
	{
		return socket.getInputStream();
	}
	
	public String getPOST()
	{

		String content = "";
		try
		{
		if(getInputStream().available() > 0)
		{
			
				  for(int i=0;i < getSize();i++)
					  content += (char)getInputStream().read();
				  	  POSTContent = content;
			
		}
		else 
			return POSTContent;
		} catch(Exception e) { }
		
		return content;
	}
	
	public String getGET()
	{
		return GETContent;
	}
}
