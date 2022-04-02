package manager;

import util.clogger.LogBuffer;


public class CSSLManager implements CIManager{


	public String getType() { return "SSL"; }
	public boolean initialize(LogBuffer buf)
	{
		boolean success = false;
		
		return success;
	}
	
	public boolean isRunnable() { return false; }

}
