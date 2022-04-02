package header;



import java.util.Date;

import main.CSysConfig;

public class CAdminSession {
	public String IPAddress;
	public boolean KeepActive = false;
	public Date	LastActive = new Date();
	
	@SuppressWarnings("deprecation")
	
	public CAdminSession(String IPAddress,Date date)
	{
		
		this.IPAddress = IPAddress;
		this.LastActive.setDate(date.getDate());
	}
	
	@SuppressWarnings("deprecation")
	public boolean isExpired()
	{
		if(!KeepActive)
		{
			Date EstimatedExpire = new Date();
			EstimatedExpire.setDate(LastActive.getDate());
			EstimatedExpire.setMinutes(LastActive.getMinutes() + CSysConfig.AdminIdleDuration);
			Date currentDate = new Date();
			return (EstimatedExpire.before(currentDate));
		}
		else 
			return KeepActive;
	}
}
