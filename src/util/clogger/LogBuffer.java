package util.clogger;

public class LogBuffer
{
	private StringBuffer content = new StringBuffer();

	
	public void stack(String str) { content.append(str + "\n"); }
	public String toString() { return content.toString(); }
	public void clear() { 
		content = new StringBuffer();
	} 
}