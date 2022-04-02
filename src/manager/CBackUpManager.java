package manager;

import util.clogger.LogBuffer;


public class CBackUpManager implements Runnable,CIManager{

	public String getType() { return "Back-up"; }
	public boolean initialize(LogBuffer buf)
	{
		boolean success = false;
		
		return success;
	}
	
	public boolean isRunnable() { return false; }
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
