package header;


import java.util.ArrayList;
import java.util.List;

import util.CStringHelper;

public class CXMLElement {
	public ArrayList<CXMLElement> elements = new ArrayList<CXMLElement>();
	private int Hierarchy_level = 0;
	public String name = new String();
	public String value = null;
	
	
	private static int findPair(String element,String xmlContent,int StartingIndex)
	{
		return xmlContent.indexOf("</" + element + ">");
	}
	

	
	public static ArrayList<CXMLElement> parse(String xmlContent) 
	{
		ArrayList<CXMLElement> elements = new ArrayList<CXMLElement>();
		
		int indexOpen 	= xmlContent.indexOf("<");
		int indexClose  = xmlContent.indexOf(">");
		while(indexOpen > -1)
		{
			
			String 	contentName 	= 	xmlContent.substring(indexOpen+1, indexClose).trim();
			int 	contentEnd 		= 	findPair(contentName,xmlContent,indexClose);
			
			CXMLElement content = new CXMLElement(contentName);
			
			String  insideContent = xmlContent.substring(indexClose+1,contentEnd).trim();
			if(insideContent.length() > 0)
			{
				if( insideContent.indexOf("<") != -1 )
					content.addElement(  parse(insideContent) );
				else
					content.value = insideContent;
			}
			elements.add(content);
			if(contentEnd + contentName.length() + 3 <= xmlContent.length())
			{
				xmlContent = xmlContent.substring(contentEnd + contentName.length() + 3);
				indexOpen 	= xmlContent.indexOf("<");
				indexClose  = xmlContent.indexOf(">");
			}
		}
		
		
		return elements;
	}
	
	public CXMLElement(String name)
	{
		this.name = name;
	}
	
	public CXMLElement(String name,String value)
	{
		this.name =  name;
		this.value = value;
	}
	
	public CXMLElement getElement(String name)
	{
		
		for(CXMLElement element: elements)
		{
			if(element.name.equalsIgnoreCase(name))
				return element;
		}
		
		return null;
	}
	
	
	public void addElement(Object ...elements)
	{
		for(Object element : elements)
		{
			this.elements.add(((CXMLElement)element).incrementHlevel(this.Hierarchy_level));
			
		}
	}
	
	public CXMLElement incrementHlevel(int level)
	{
		this.Hierarchy_level = level + 1;
		for(CXMLElement element: elements)
			element.incrementHlevel(this.Hierarchy_level);
		
		return this;
	}
	
	public void addElement(List<CXMLElement> elements)
	{
		addElement( elements.toArray());
	}
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		String tab = CStringHelper.repeat("\t", this.Hierarchy_level);
		
		buffer.append(tab + "<"+ name +">\n");
		if(value != null) buffer.append(tab + "\t" + value + "\n");
		
		for(CXMLElement element : elements)
			buffer.append(element.toString() + "\n");

		
		buffer.append(tab + "</" + name +">");
		return buffer.toString();
	}
}
