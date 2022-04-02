package manager;



import header.CRequestDetail;

import java.util.concurrent.CopyOnWriteArrayList;


import util.clogger.LogBuffer;


public class CRequestManager implements Runnable,CIManager {

	private static CopyOnWriteArrayList<CRequestDetail> requestList = null;
	
	public String getType() { return "Request"; }
	public boolean initialize(LogBuffer buf)
	{
		boolean success = false;
		requestList = new CopyOnWriteArrayList<CRequestDetail>();
		success = true;
		return success;
	}
	
	
	public void AddToTrackList(CRequestDetail request)
	{
		requestList.add(request);
	}
	
	public boolean isRunnable() { return true; }
	
	public void run() {
		
		while(true)
		{
			for(CRequestDetail request : requestList)
			{
				if(request.socket.isClosed())
					requestList.remove(request);
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {}
		}
	}

}
