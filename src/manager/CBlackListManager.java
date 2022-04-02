package manager;


import main.CSysConfig;
import header.CBlackListItem;
import util.clogger.LogBuffer;

public class CBlackListManager implements CIManager{

	public boolean isRunnable() {
		return false;
	}

	public boolean initialize(LogBuffer log) {
		return true;
	}
	
	public  void BlackList(String keyword,boolean exact_check_enabled, boolean case_sensitive_enabled)
	{
		CBlackListItem blacklistitem = new CBlackListItem(keyword);
		blacklistitem.setCaseSensitivity(case_sensitive_enabled);
		blacklistitem.setExactChecking(exact_check_enabled);
		CSysConfig.BlackListed.add(blacklistitem);
		

		try {
			CSysConfig.SaveConfigDetailsToFile();
		} catch (Exception e) { } 
	}
	
	public  void removeToBlackList(String item)
	{
		for(CBlackListItem blacklistitem : CSysConfig.BlackListed)
		{
			if(blacklistitem.getKeyword().equalsIgnoreCase(item))
			{
				CSysConfig.BlackListed.remove(blacklistitem);
				break;
			}
		}
		
		try { CSysConfig.SaveConfigDetailsToFile(); } catch(Exception e) {}
	}
	
	public  boolean isBlackListed(String url)
	{
		boolean isBlackList = false;
		
		for(CBlackListItem blacklistitem : CSysConfig.BlackListed)
		{
			String keyword = (blacklistitem.isCaseSensitive() ?  
							  		blacklistitem.getKeyword() :
							  		blacklistitem.getKeyword().toLowerCase());
			
			url = (blacklistitem.isCaseSensitive() ? url : url.toLowerCase());
			isBlackList = (blacklistitem.isExactCheck() ? 
									url.equals(keyword) : 
									url.contains(keyword));
		}
		
		return isBlackList;
	}

	public String getType() {
		return "Black List";
	}

	

}
