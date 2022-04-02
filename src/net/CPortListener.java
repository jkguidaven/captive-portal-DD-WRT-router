package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import main.CSysDaemon;

import client.CRequestHandler;

import util.clogger.CLogger;
import util.clogger.LogBuffer;

public class CPortListener implements Runnable {

	private int port = 0;
	private String name = new String();
	private boolean listening;
	
	public CPortListener(String name,int port)
	{
		this.name = name;
		this.port = port;
		this.listening = false;

		CLogger.log(CLogger.LogType.SYSTEM , 
					"Initializing {0}[{1}] Port Listener...", 
					name,
					Integer.toString(port));
	}
	
	public boolean isListening()
	{
		return listening;
	}
	
	public void run() {
			listening = true;

			ServerSocket listener = null;
			try {
				listener = new ServerSocket(port);
				CLogger.log(CLogger.LogType.SYSTEM , 
							"{0}[{1}] Port Listener is now listening..", 
							name,
							Integer.toString(port));
			} catch (IOException e) {
				CLogger.log(CLogger.LogType.SYSERROR , 
							"Could not listen to port {0}.{1} Port listener aborted!", 
							Integer.toString(port),
							name );
				return;
			}
			
			while(true)
			{
				try {
					Socket client = listener.accept();
					String clientIP = client.getInetAddress().toString().replace("/", "");
					if(clientIP.equals("127.0.0.1")) clientIP = CSysDaemon.localHostIP;
					
					CLogger.log(CLogger.LogType.SYSTEM , 
							"{0}[{1}] Port Listener received Incoming request from client[{2}]. ", 
							name,
							Integer.toString(port),
							clientIP);
					
					CRequestHandler handler = new CRequestHandler(clientIP,client, new LogBuffer());
					Thread thread = new Thread(handler,"Request Handler[" + clientIP + "]");
					thread.start();
					
				} catch (IOException e) {}
			}
	}

}
