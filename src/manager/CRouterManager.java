package manager;

import main.CSysConfig;
import net.CTelnetCom;
import util.clogger.CLogger;
import util.clogger.LogBuffer;

public class CRouterManager implements CIManager{

	CTelnetCom telnet = null;
	
	
	public boolean isRunnable() {
		return false;
	}

	public boolean initialize(LogBuffer buf) {

		try
		{
			CLogger.log(CLogger.LogType.SYSRTR ,buf, "Establishing Telnet Connection with Router[{0}]...",CSysConfig.RtrTelnetIP);
			telnet = new CTelnetCom(CSysConfig.RtrTelnetIP,
									CSysConfig.RtrTelnetUsername,
									CSysConfig.RtrTelnetPassword,
									buf);
			
		}catch(Exception e) { 
			CLogger.log(CLogger.LogType.RTRERR ,buf, "Failed to established connection with Router[{0}]...",CSysConfig.RtrTelnetIP);
			
			return false; 
		}
		
		CLogger.log(CLogger.LogType.SYSRTR ,buf, "Sucessfully established Telnet Connection with Router[{0}]...",CSysConfig.RtrTelnetIP);
		
		
		if(login())
		{
			reset();
			return true;
		}
		return false;
	}

	@Override
	public String getType() {
		return "Router Manager";
	}
	
	
	private static boolean login()
	{
		return false;
	}
	public static void accept(String access)
	{
		
	}
	
	public static void deny(String access)
	{
		
	}
	
	public static void reset()
	{
		
	}

}
