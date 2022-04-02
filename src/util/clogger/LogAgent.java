package util.clogger;

import java.util.Date;

import util.clogger.CLogger.LogType;

public class LogAgent implements Runnable
{
	private LogType type = null;
	private String ptr_str = null;
	private String[] params = null;
	private LogBuffer buf = null;
	private boolean Err = false;
	private boolean showDetail = true;
	

	public LogAgent(LogType type,boolean showDetail,boolean Err,LogBuffer buf,String ptr_str, String ...params)
	{
		this.type = type;
		this.ptr_str = ptr_str;
		this.params = params;
		this.Err = Err;
		this.buf = buf;
		this.showDetail = showDetail;
	}
	
	@SuppressWarnings("deprecation")
	public void run() {
		
		ptr_str = cleanStr(ptr_str,params);
		
		String detail = (showDetail ? 
						type + (!Err ? "\t\t" : "\t") + " - " + 
						(new Date()).toLocaleString() + " - " : "");
		saveToFile( type, ptr_str);
		
		if(buf==null)
		{
			if(!Err)
				System.out.println(detail + ptr_str );
			else
				System.err.println(detail + ptr_str );
		}
		else
		{
				buf.stack(detail + ptr_str );
		}
	}
	
	private synchronized static void saveToFile(LogType type,String string)
	{
		
	}
	
	private static String cleanStr(String ptr_str,String ...params)
	{
		int index=0;
		for(String param : params)
		{
			ptr_str = ptr_str.replace("{" + index + "}", param);
			index++;
		}
		
		return ptr_str;
	}
}