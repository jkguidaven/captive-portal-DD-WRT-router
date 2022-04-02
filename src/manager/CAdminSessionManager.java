package manager;



import header.CAdminSession;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import main.CSysConfig;

import util.clogger.LogBuffer;


public class CAdminSessionManager implements Runnable,CIManager{

	private static CopyOnWriteArrayList<CAdminSession> AdminSessionList;
	
	public String getType() { return "Admin Session"; }
	public boolean initialize(LogBuffer buf)
	{

		AdminSessionList = new CopyOnWriteArrayList<CAdminSession>();

		return true;
	}
	
	public boolean isRunnable() { return true; }
	
	

	@SuppressWarnings("deprecation")
	public static void invokeActiveSession(String ipaddress)
	{
		for(CAdminSession session: AdminSessionList)
		{
			session.LastActive.setDate((new Date()).getDate());
			break;
		}
	}
	
	public static boolean authenticate(String username,String password)
	{
		return username.equals(CSysConfig.sysUsername) && password.equals(CSysConfig.sysPassword);
	}
	
	public static void AddAdminSession(String ipaddress,boolean keepActive)
	{
		CAdminSession session = new CAdminSession(ipaddress, new Date() );
		session.KeepActive = keepActive;
		AdminSessionList.add(session);
	}
	
	public static void removeSession(String ipaddress)
	{			
		for(CAdminSession session: AdminSessionList)
		{
			if(session.IPAddress.equals(ipaddress))
			{
				AdminSessionList.remove(session);
				break;
			}
		}
	}
	
	public static boolean hasSession(String ipaddress)
	{
		boolean hasSession = false;
		
		for(CAdminSession session: AdminSessionList)
		{
			if(session.IPAddress.equals(ipaddress))
			{
				if(session.isExpired())
					AdminSessionList.remove(session);
				else
					hasSession = true;
				break;
			}
		}
		
		return hasSession;
	}
	
	public void run() {
		while(true)
		{
			for(CAdminSession session: AdminSessionList)
			{
				if(session.isExpired())
				{
					AdminSessionList.remove(session);
					break;
				}
			}
			
			try
			{
				Thread.sleep(200);
			}catch(Exception e) { }
			
		}
	}
}
