package util.clogger;

import java.util.ArrayList;
import java.util.List;


public abstract class CLogger {
	

	
	public enum LogType {   SYSERROR   ,    SYSTEM    , AKMERROR , 
						    SYSAKM     ,    RQSTPRC   , RQSTCNT  ,
						   RQSTERROR   ,    SYSHRM    ,HRMERROR  ,
						   SYSRTR	   ,    RTRERR  };
	private static List<LogType> errListType = null;
	
	public static void load() { 
		errListType = new ArrayList<LogType>();
		errListType.add(LogType.SYSERROR);
		errListType.add(LogType.AKMERROR);
		errListType.add(LogType.RQSTERROR);
		errListType.add(LogType.HRMERROR);
	} 
	
	
	public static void log(LogType type,String ptr_str, String ...params)
	{
		log(type,null,ptr_str,params);
	}
	
	public static void log(LogType type,LogBuffer buf,String ptr_str,String ...params)
	{

		log(type,true,buf,ptr_str,params);
	}
	
	public static void log(LogType type,boolean showDate,LogBuffer buf,String ptr_str,String ...params)
	{
		LogAgent agent = new LogAgent(type,
				  showDate,
				  errListType.contains(type),
				  buf,
				  ptr_str,
				  params);
		Thread thread = new Thread(agent);
		thread.run();
	}
	
	public static void flushLog(LogBuffer buf)
	{
		System.out.println(buf);
		buf.clear();
	}
	
	
	

}
