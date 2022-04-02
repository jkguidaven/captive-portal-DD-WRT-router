package header;

public class CBlackListItem {
	private String keyword;
	private boolean exact_check_enabled;
	private boolean case_sensitive_enabled;
	
	public CBlackListItem(String keyword)
	{
		this.keyword = keyword;
		exact_check_enabled = false;
		case_sensitive_enabled = false;
	}
	
	public boolean isCaseSensitive()
	{
		return case_sensitive_enabled;
	}
	
	public boolean isExactCheck()
	{
		return exact_check_enabled;
	}
	
	public String getKeyword()
	{
		return keyword;
	}
	
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}
	
	public void setCaseSensitivity(boolean enabled)
	{
		this.case_sensitive_enabled = enabled;
	}
	
	public void setExactChecking(boolean enabled)
	{
		this.exact_check_enabled = enabled;
	}
}
