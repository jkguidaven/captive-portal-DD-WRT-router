package client;

import header.CHTTPRequest;
import header.response.*;

import java.net.Socket;

import security.CSecMsg;
import security.CSecurityAgent;
import security.CSecurityAgent.MsgCode;
import util.clogger.CLogger;
import util.clogger.LogBuffer;

public class CRequestHandler implements Runnable {

	Socket client = null;
	LogBuffer log = null;
	String clientIP = null;
	public CRequestHandler(String clientIP,Socket client,LogBuffer log)
	{
		this.clientIP = clientIP;
		this.client = client;
		this.log = log;
		CLogger.log(CLogger.LogType.RQSTPRC ,log,"Processing Client[" + clientIP + "]'s request.");
		
	}
	
	public void run()  {
		CHTTPRequest request = new CHTTPRequest(log);
		request.clientIP = clientIP;
		try
		{
			if(request.parse(clientIP,client))
			{
				CLogger.log(CLogger.LogType.RQSTPRC ,log,"Request parsing successful.");

				
				CLogger.log(CLogger.LogType.RQSTCNT ,false,log,"=========HTTP=Request=Content=========");
				CLogger.log(CLogger.LogType.RQSTCNT ,false,log,"{0}",request.toString(false).trim());
				CLogger.log(CLogger.LogType.RQSTCNT ,false,log,"======================================");
				
				CLogger.log(CLogger.LogType.RQSTPRC ,log,"Security Agent Analyzing request.");
				CSecMsg message = CSecurityAgent.getMsgCommand(request);
				CLogger.log(CLogger.LogType.RQSTCNT ,log,
						"Security Agent returned \"{0}[{1}]\" message.",
						message.type.toString(),
						(message.code == null ? "" :message.code.toString()));
				
				
			
				switch(message.type)
				{
						case SA_PROXRES: // Proxy Server Response
							
							// send "No Key found for user" message && send "Key already Expired" message
							if(message.code.equals(MsgCode.C201) || message.code.equals(MsgCode.C202))
							{
								CReqResponse.sendToClient(log,new CProxReqResponse(client,log).processData(message));
							}
							else // send "forward request" message
							{
								
							}
							
						break;
						case SA_ADMINRES:  // Administrative Request Response
							if(message.code.equals(MsgCode.C301) || message.code.equals(MsgCode.C302) || message.code.equals(MsgCode.C303))
							{
								CReqResponse.sendToClient(log,new CAdminReqResponse(client,log).processData(message));
							}
							else
							{
								CReqResponse.sendToClient(log,new CErrReqResponse(client,log).processData(message));
							}
						break;
						case SA_RSRCRES: // Resource Request Response
							CReqResponse.sendToClient(log,new CRsrcReqResponse(client,log).processData(request));
						break;
						case SA_ERRRES:  // source unknown
							CReqResponse.sendToClient(log,new CErrReqResponse(client,log).processData(message));
						break;
						case SA_ADSPAGE: // Ads Page Response
						break;
						case SA_UPGRADE: // Upgrade Handler for Request
						break;
				}
				
			}
			else CLogger.log(CLogger.LogType.RQSTERROR ,log,"Request process canceled!");


		}catch(Exception e) { e.printStackTrace(); }
		finally
		{
			CLogger.flushLog(log);
			
		}
		
	}

}
