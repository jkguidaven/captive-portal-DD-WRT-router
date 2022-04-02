package manager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import main.CSysConfig;
import util.CStringHelper;
import util.clogger.CLogger;
import util.clogger.LogBuffer;

public class CHTTPResourceManager implements CIManager{

	public  String LAST_GENERATED_KEY				= "";
	public  String LAST_TIME_GENERATED_KEY		= "";
	public  String LAST_DURATION_GENERATED_KEY	= "";
	
	private static List<CResource> ResourceList;
	private static String[] resourceFormat = 
							{  "js"  , "css" , 
							   "jpg" , "gif"  , "png" , "pdf"    };
	
	private class CResource
	{
		public String resource;
		public String extension;
		public byte[] content;
		
		public CResource(String resource,String extension,long size)
		{
			
			this.content = new byte[(int)size];
			this.extension =extension;
			this.resource = resource.replace(CSysConfig.ResourceDirectory, "");

		}
		
		public String toString(boolean withExtension)
		{
			// if resource is not html or htm, force with extension for security reason
			if(!(extension.equals("html") || extension.equals("htm")) )
			withExtension = true;
			
			return resource + (withExtension ? "." + extension : "");
		}
		
		public String toString()
		{
			return toString(true);
		}
		
	}
	
	
	public static byte[] getContent(String name){
		return getContent( name, CStringHelper.separateLeftMost(name, ".") != null  , false);
	}
	
	public static byte[] getContent(String name,boolean ignoreCache)
	{
		return getContent( name, CStringHelper.separateLeftMost(name, ".") != null  , true);
	}
	
	private static byte[] getContent(String name,boolean withExtension,boolean ignoreCache){
		byte[] data = new byte[0];
		
		name = name.replace("http://" + CSysConfig.localHostIP, "");
		name = name.replace("/admin/", "");
		
		if(!ignoreCache)
		{
			// load from cache resources
			for(CResource resource : ResourceList)
			{
				if(resource.toString(withExtension).equalsIgnoreCase(name) ||
				   resource.toString(withExtension).equalsIgnoreCase("/" + name))
				{
					data = resource.content;
					break;
				}
			}
		}
		else
		{
			// load directly from resource directory regardless of file cachable
			File file = new File(CSysConfig.ResourceDirectory,name);
			
			if(file.exists())
			{
				try
				{
					BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
					data = new byte[(int)file.length()];
					
					int totalBytesRead = 0;
					while(totalBytesRead < file.length())
					{
						int bytesRemaining = data.length - totalBytesRead;
						int bytesRead = input.read(data,totalBytesRead,bytesRemaining);
						totalBytesRead = totalBytesRead + bytesRead;
					}
				}catch(Exception e) { }
			}
		}
		
		return data;
	}
	
	
	public boolean isRunnable() {
		return false;
	}
	
	private boolean validResource(String extension)
	{
		boolean valid = false;
		for(int i=0;i<resourceFormat.length;i++)
		{
			if(resourceFormat[i].equalsIgnoreCase(extension))
			{
				valid = true;
				break;
			}
		}
		return valid;
	}
	
	private void cacheFiles(String Curr_Dir,File dir,LogBuffer buf)
	{

		boolean hadNotCacheToThisDir = true; //+ Log Purpose only -//
		String[] files = dir.list();
		
		for(String filename: files)
		{
			try
			{
				String extension = CStringHelper.separateLeftMost(filename, ".");
				if(extension != null)
				{
					
					//+ Log Purpose only +//
					
					if(hadNotCacheToThisDir)
					{
						/*-
					 	Print only if there is file being process in the current dir
						*/
						CLogger.log(CLogger.LogType.SYSHRM ,buf, "Caching Files[{0}/]:",Curr_Dir );
						hadNotCacheToThisDir=false;
					}
					//- Log Purpose only -//
					
					if( validResource(extension) )
					{	
						File file = new File(Curr_Dir,filename);
						CResource resource = new CResource( 
								CStringHelper.removeToString(Curr_Dir + "/" + filename,"." + extension),
								extension,
								file.length());
						
						BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
						
						int totalBytesRead = 0;
						while(totalBytesRead < file.length())
						{
							int bytesRemaining = resource.content.length - totalBytesRead;
							int bytesRead = input.read(resource.content,totalBytesRead,bytesRemaining);
							totalBytesRead = totalBytesRead + bytesRead;
						}

						CLogger.log(CLogger.LogType.SYSHRM ,buf, "\t\tcaching {0}. successful!",filename );
						ResourceList.add(resource);
					}
					else
					{	
						CLogger.log(CLogger.LogType.SYSHRM ,buf, "\t\tcaching {0}. skipped!",filename );
					}
				}
				else 
					cacheFiles(Curr_Dir + "/" + filename,new File(Curr_Dir,filename),buf);

			}catch(Exception e) {
				CLogger.log(CLogger.LogType.HRMERROR ,buf, "Unexpected Error occured in File[{0}].", filename );
			}
		}
	}
	

	
	public boolean initialize(LogBuffer buf) {
		ResourceList = new ArrayList<CResource>();
		
		CLogger.log(CLogger.LogType.SYSHRM ,buf, "Loading resources in {0} Directory",CSysConfig.ResourceDirectory);
		File dir = new File(CSysConfig.ResourceDirectory);
		if(dir.exists())
		{
			cacheFiles(CSysConfig.ResourceDirectory,dir,buf);
		} 
		else 
		{
			dir.mkdir();
		}
		
		
		return true;
	}

	@Override
	public String getType() {
		return "HTTP Resource Page";
	}

}
