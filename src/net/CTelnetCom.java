package net;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import util.clogger.CLogger;
import util.clogger.LogBuffer;

public class CTelnetCom {

	private InputStream  in  		= null;
	private OutputStream out 		= null;
	private Socket       socket  	= null;
	
	public CTelnetCom(String router,String username,String password,LogBuffer log) throws UnknownHostException, IOException
	{
		socket = new Socket(router,23);
		socket.setKeepAlive(true);
		in = socket.getInputStream();
		out = socket.getOutputStream();
		
		
		CLogger.log(CLogger.LogType.SYSRTR ,false,log,"======Router=Telnet=Communication=====");
		CLogger.log(CLogger.LogType.SYSRTR ,false,log,"{0}",readResponse("Login: ","Username: "));
		//CLogger.log(CLogger.LogType.RQSTCNT ,false,log,"{0}",sendCommand(username, "Password: "));
		//CLogger.log(CLogger.LogType.RQSTCNT ,false,log,"{0}",sendCommand(password));
		CLogger.log(CLogger.LogType.SYSRTR ,false,log,"======================================");
		
	}
	
	private String readResponse(String...stopUntilString) throws IOException
	{
		String default_stopper = "# ";
		String responses = "";
		boolean useDefaultStopper = true;
		for(String keyword : stopUntilString)
		{
			if(keyword.length() > 0)
			{
				useDefaultStopper = false;
				break;
			}
		}
		
		int read = -1;
		boolean hasFinishedRead;
		do
		{
			hasFinishedRead = false;
			read = in.read();
			
			responses += (char)read;
			
			if(useDefaultStopper)
			{
				if(responses.toLowerCase().contains(default_stopper))
					break;
			}
			else
			{
				for(String keyword : stopUntilString)
				{
					if(responses.toLowerCase().contains(keyword))
					{
						hasFinishedRead = true;
						break;
					}
				}
			}
			
		} while(read!=-1 && !hasFinishedRead);
		
		
		return responses;
	}
	
	public String sendCommand(String commandStr,String...stopUntilString) throws IOException
	{
		out.write( (commandStr + "\r\n").getBytes());
		out.flush();
		return readResponse(stopUntilString);
	}
	
}
