package manager;

import util.clogger.LogBuffer;


public class CUpdateManager implements Runnable,CIManager{

	public String getType() { return "Update"; }
	
	public boolean isRunnable() { return true; }
	@Override
	
	public boolean initialize(LogBuffer buf)
	{
		boolean success = false;
		
		return success;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
