package header;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class CAccessKey implements Serializable {
	public String KeyCode;
	private String DesignatedOwnerIP;
	private String DesignatedOwnerName;
	public int HourLength;
	private int MinuteLength;
	private Date AssignedTime;
	private Date TimeCreated;
	private Date ExpirationTime;
	private boolean isLocked;
	
	@SuppressWarnings("deprecation")
	public CAccessKey(String keyCode)
	{
		this.KeyCode  = keyCode;
		DesignatedOwnerIP = null;
		DesignatedOwnerName = null;
		HourLength = 0;
		MinuteLength = 5;
		AssignedTime = new Date();
		TimeCreated = new Date();
		ExpirationTime = new Date();
		ExpirationTime.setDate(new Date().getDate());
		TimeCreated.setDate(new Date().getDate());
		isLocked = false;
		
	
	}
	
	@SuppressWarnings("deprecation")
	public void CAssignKey(String DesignatedOwnerIP)
	{
		if(!isLocked)
		{
			Date ExpirationTime = new Date();
			AssignedTime.setDate(ExpirationTime.getDate());
			ExpirationTime.setHours(ExpirationTime.getHours() + HourLength);
			ExpirationTime.setMinutes(ExpirationTime.getMinutes() + MinuteLength);
			this.ExpirationTime = ExpirationTime;
			this.DesignatedOwnerIP = DesignatedOwnerIP;
			isLocked = true;
		}
	}
	
	public void setHour(int HourLength)
	{
		if(!isLocked)
			this.HourLength = HourLength;
		else
			System.out.println("Key[" + KeyCode + "] is already locked.");
	}
	
	public void setMinute(int MinuteLength)
	{
		if(!isLocked)
			this.MinuteLength = MinuteLength;
		else
			System.out.println("Key[" + KeyCode + "] is already locked.");
	}
	
	public int getHourLength()
	{
		return HourLength;
	}
	
	public int getMinuteLength()
	{
		return MinuteLength;
	}
	
	public Date getAssignedTime()
	{
		 if(isLocked)
			 return AssignedTime;
		 else
			 return null;
	}
	
	public boolean isExpired()
	{
		return (ExpirationTime.before(new Date()) );
	}
	
	public boolean isAvailable()
	{
		return (DesignatedOwnerIP == null);
	}
	
	public boolean isOwner(String DesignatedOwnerIP)
	{
		return (this.DesignatedOwnerIP.equals(DesignatedOwnerIP));
	}
	
	public String getOwnerIP()
	{
		return DesignatedOwnerIP;
	}
	
	public String getOwnerName()
	{
		return DesignatedOwnerName;
	}
	
	public String getKeyCode()
	{
		return KeyCode;
	}
	
	public Date getExpiration()
	{
		if(isLocked)
			return ExpirationTime;
		else
			return null;
	}
	
	public Date getTimeCreated()
	{
			return TimeCreated;
	}
	public String toString()
	{
		return KeyCode;
	}
	
	@SuppressWarnings("deprecation")
	public String getDuration()
	{
		int hour = 0;
		int min  = 0;
		String hourstr = "";
		String minstr = "";
		
		if(!isLocked)
		{
			hour = HourLength;
			min  = MinuteLength;
		}
		else
		{
			Date current = new Date();
			current.setHours(ExpirationTime.getHours() - current.getHours());
			current.setMinutes(ExpirationTime.getMinutes() - current.getMinutes());
			hour =  current.getHours();
			min  =  current.getMinutes();
		}
		
		
		if(hour > 0)
			hourstr = hour + " hr(s) ";
		if(min > 0)
			minstr = min + " min(s) ";
		
	
		return hourstr + (hour > 0 && min > 0 ? " & " : "") + minstr;
	}

}
