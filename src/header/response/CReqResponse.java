package header.response;





import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Date;

import main.CSysConfig;
import manager.CHTTPResourceManager;

import util.CStringHelper;
import util.clogger.CLogger;
import util.clogger.LogBuffer;


public abstract class CReqResponse {
	private 	Socket socket  = null;
	protected 	byte[] content = null;
	protected 	LogBuffer log  = null;
	
	public class ReplaceCommand
	{
		String[] oldStr = null;
		String newStr = "";
		
		public ReplaceCommand(String newStr,String ...oldStr)
		{
			this.oldStr = oldStr;
			this.newStr = newStr;
		}
		
	
	}
	
	
	
	public CReqResponse(Socket socket,LogBuffer log)
	{
		this.socket = socket;
		this.log 	= log;
	}
	
	public Socket getSocket() { return socket; }
	public byte[] getContent() { 
		CLogger.log(CLogger.LogType.RQSTCNT ,false,log,"=========HTTP=Response=Content=========");
		CLogger.log(CLogger.LogType.RQSTCNT ,false,log,"{0}",new String(content));
		CLogger.log(CLogger.LogType.RQSTCNT ,false,log,"======================================");
		return content; 
	}
	
	
	public CReqResponse processData(Object artifacts)
	{
		CLogger.log(CLogger.LogType.RQSTPRC ,log,
					"Generating Response base to supplied artifact[{0}]...",
					artifacts.getClass().getName());
		return generatingResponseContent(artifacts);
	}
	
	protected abstract CReqResponse generatingResponseContent(Object artifact);
	
	public static void sendToClient(LogBuffer log,CReqResponse response) throws Exception
	{
		CLogger.log(CLogger.LogType.RQSTPRC ,log,"Sending Response to Client...");
		response.getSocket().getOutputStream().write(response.getContent());
		response.getSocket().getOutputStream().flush();
		response.getSocket().close();
		CLogger.log(CLogger.LogType.RQSTPRC ,log,"Successful!");
	}
	
	
	protected String generateHeader(String StatusCode,String StatusMsg,int length)
	{
		String content =new String();
	       
		content += CSysConfig.HTTPProtocol + " " + StatusCode + " " + StatusMsg + CStringHelper.CR;
		content += "Server: " + CSysConfig.ServerName   						+ CStringHelper.CR;
		content += "MIME-version: 1.0"       									+ CStringHelper.CR;
	    content += "Content-Type: text/html;" 									+ CStringHelper.CR;
	    content += "Content-Length: " + length 									+ CStringHelper.CR;

	    content += CStringHelper.CR;
		return content;
	}
	
	protected String generateHeaderRedirect(String redirect)
	{
		String content = new String();
		
		content += "HTTP/1.1 301 Moved Permanently" + CStringHelper.CR;
		content += "Location: " + redirect 			+ CStringHelper.CR;
		content += "+ CStringHelper.CR";
		return content;
	}
	protected String generateBody(String load_file,ReplaceCommand ... repcommand)
	{
		String content  = new String(CHTTPResourceManager.getContent(load_file,true));
		
		for(ReplaceCommand command: repcommand)
		{
			for(String oldStr : command.oldStr)
				content = content.replace(oldStr, command.newStr);
		}
		
		return content;
	}
	
	public ReplaceCommand replace(String newStr,String ...oldStr)
	{
		return new ReplaceCommand(newStr,oldStr);
	}
	


	@SuppressWarnings("deprecation")
	protected ReplaceCommand MultipleReplace(String str,Object ... artifacts)
	{
		StringBuffer sb = new StringBuffer();
		for(Object artifact : artifacts)
		{
			String replace = str;
			for(Method field : artifact.getClass().getMethods())
			{
				try
				{
					String data 	= "@" + field.getName().toUpperCase();
					Object obj = field.invoke(artifact);
					String value = "";
					if(obj != null)
					{
						if(obj instanceof Date)
						{
							value = ((Date)obj).toLocaleString();
						}
						else
						{
							value = obj.toString();
						}
					}
					
					replace = replace.replace(data,value );
				}catch(Exception e) {  }
				
			}
			sb.insert(0, replace);
		}
		
		return new ReplaceCommand(sb.toString(),str);
	}
}
