package main;

import java.net.InetAddress;

import net.CPortListener;

public class CSysDaemon extends CSysConfig 
						implements Runnable{

	
	public CSysDaemon(String ip,String host) throws Exception {
		super(ip,host);
	}

	public void run() {
		
		CPortListener WebListener 		= new CPortListener("Web",WebPort);
		new Thread(WebListener).start();
		CPortListener ProxyListener	 	= new CPortListener("Proxy",ProxyPort);
		new Thread(ProxyListener).start();
		
		
		while(true){ 
			try {
			Thread.sleep(1000);
			} catch (InterruptedException e) {} 
		}
	}

	public static void main(String args[]) 
	{
		try
		{
			String host = InetAddress.getLocalHost().getHostName();
			String ip = InetAddress.getLocalHost().getHostAddress();
			
			CSysDaemon daemon = new CSysDaemon(ip,host);
			Thread thread = new Thread(daemon);
			thread.start();
			
		}catch(Exception e) { e.printStackTrace(); } 
	}
}
