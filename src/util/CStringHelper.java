package util;

public abstract class CStringHelper {
	

    public static final String CR="\r\n";
    
	public static String repeat(String str,int repeat)
	{
		while(repeat>1)
		{
			str += str;
			repeat--;
		}
		
		return str;
	}
	
	
	public static String removeToString(String str,String remove)
	{
		return str.substring(0, str.indexOf(remove));
	}
	
	public static String separateLeftMost(String str,String separator)
	{
		String leftmost = null;
		
		int indexSep = str.indexOf(separator);
		
		if(indexSep != -1)
		{
			String next_leftmost = separateLeftMost(str.substring(indexSep+1),
													separator);
			if(next_leftmost != null)
				leftmost = next_leftmost;
			else
				leftmost = str.substring(indexSep+1);
			
		}
		return leftmost;
	}
	
	public static String extractStrFromRange(String Str,String beginStr, String endStr)
	{
		int beginIndex = Str.indexOf(beginStr) + beginStr.length();
		int endIndex = Str.indexOf(endStr);
		
		if(beginIndex > -1 && endIndex >-1 && beginIndex < endIndex)
			return Str.substring(beginIndex,endIndex);
		else
			return "";
	}
	
    public static String parsePOSTtoString(String POST,String Value)
    {
    	String parsedValue = null; 
    	try
    	{
    		int indexOfValue = POST.indexOf(Value+"=");
    		int indexAmper = -1;
    		for(int i=indexOfValue + Value.length();i<POST.length();i++)
    		{
    			if(POST.charAt(i) == '&') 
    			{
    				indexAmper = i;
    				break;
    			}
    		}
    		
    		if(indexAmper == - 1) indexAmper = POST.length();
    		
    		parsedValue = POST.substring(indexOfValue + Value.length()+1, indexAmper );
    		
    		
    	}catch(Exception e) { parsedValue = null; } 
    	
    	return parsedValue;	
    }
    
    public static String convertToNonWebencoding(String encodedtext)
    {
		String[] WebEncoded = {"%3B","%3F","%2F","%3A","%23","%26","%3D","%24","%2C",
				   "%20",  "+","%2B","%3C","%3E","%7E","%25" };
		String[] charRep = {";" , "?" , "/" , ":" , "#" , "&" , "=" , "$" , 
								 "," , " " , " " , "+" , "<" , ">" , "~" , "%"};
		for(int i=0;i<WebEncoded.length;i++)
			encodedtext = encodedtext.replace(WebEncoded[i], charRep[i]);
		
		return encodedtext;
    }
}
