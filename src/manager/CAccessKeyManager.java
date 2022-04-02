package manager;





import header.CAccessKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import main.CSysConfig;

import util.clogger.CLogger;
import util.clogger.LogBuffer;


public class CAccessKeyManager implements Runnable,CIManager{
	private static CopyOnWriteArrayList<CAccessKey> keys;
	
	
	public String getType() { return "Access Key"; }
	public boolean initialize(LogBuffer buf)
	{
		boolean success = false;
		keys = new CopyOnWriteArrayList<CAccessKey>(); 
		if(!CSysConfig.AKMAutoSaveEnabled) 
		{
			CLogger.log(CLogger.LogType.SYSAKM,buf, "loading fresh Access Key list.");
			success = true;
		}
		else
		{
			ObjectInputStream ois = null;
			try
			{
				CLogger.log(CLogger.LogType.SYSAKM, "loading existing Access Key List[AccessKeyList.list].");
				ois = new ObjectInputStream(new FileInputStream(new File("AccessKeyList.list")));
				Object obj = ois.readObject();
				if(obj instanceof CopyOnWriteArrayList)
				{
					@SuppressWarnings("rawtypes")
					CopyOnWriteArrayList list = (CopyOnWriteArrayList)obj;
					for(Object obj2 : list)
					{
							if(obj2 instanceof CAccessKey)
								keys.add((CAccessKey)obj2); 
					}
					CLogger.log(CLogger.LogType.SYSAKM, "Successfully loaded access key files[AccessKeyList.list].");
				} else throw new Exception();
				}catch(Exception e)
				{
					CLogger.log(CLogger.LogType.AKMERROR, "No existing Access Key list.");
					CLogger.log(CLogger.LogType.AKMERROR, "loading fresh Access Key list.");
					
					System.err.println("");
					System.out.println("");
					try { invokeSaveListToFile(); }catch(Exception ex) {  }
					
				}
				finally{
					try
					{
						if(ois != null) ois.close();
					}catch(Exception ex) {  }
					success = true;
				}
		}
		
		

		
		
		return success;
	}
	
	private void invokeSaveListToFile() throws IOException
	{
		if(CSysConfig.AKMAutoSaveEnabled)
		{
			ObjectOutputStream oos = null;
			try
			{
				oos = new ObjectOutputStream(new FileOutputStream(new File("AccessKeyList.list")));
				oos.writeObject(keys); 
			}catch(Exception ex) 
			{
			}
			finally{
				if(oos != null)
				{
	       			oos.flush();
	       			oos.close();
				}
			}
		}
	}
	
	public CAccessKey generateKey(int hours,int minutes)
	{ 
		String generatedKeyCode = generateKeyCode();
		CAccessKey generatedKey = new CAccessKey(generatedKeyCode);
		generatedKey.setHour(hours);
		generatedKey.setMinute(minutes);
		if(CSysConfig.MaxAccessKeyPool > totalLiveKey())
		{
			keys.add(generatedKey); 
			try{invokeSaveListToFile();}catch(Exception e) { }
			
		}
		else
			CLogger.log(CLogger.LogType.AKMERROR, "Can No longer create Key, Max AccessKey Pool reach..");
		
		return generatedKey;
		
	}
	
	public void removeKey(CAccessKey key)
	{
		keys.remove(key); 
		try{invokeSaveListToFile();}catch(Exception e) { }
		
	}
	
	public void removeKey(String keycode)
	{
		for(CAccessKey key : keys)
		{
			if(key.getKeyCode().equalsIgnoreCase(keycode))
			{
				removeKey(key);
				break;
			}
		}
	}
	
	private int totalLiveKey()
	{
		int count=0;
		
			for(CAccessKey key: keys)
			{
				if(key.isAvailable() || !key.isExpired())
					count++;
			}
		
		return count;
	}
	
	private String generateKeyCode()
	{
		boolean isOk = false;
		 Random randomGenerator = new Random();
		 String GeneratedKeyCode = null;
		
		while(!isOk)
		{
			GeneratedKeyCode = "Key" + randomGenerator.nextInt(100);
			boolean notFound = true;
			
				for(CAccessKey key : keys)
				{
					if(key.getKeyCode().equals(GeneratedKeyCode))
					{
						notFound= false;
						break;
					}
				}
			
			if(notFound) isOk = true;
		}
		
		return GeneratedKeyCode;
	}
	
	
	public String toString()
	{
		return keys.toString();
	}
	
	public int isIPMapped(String ClientIP)
	{
		int isIPMapped = 0;
		
			for(CAccessKey key : keys)
			{
				if(key.getOwnerIP() == null) continue;
				
				if(key.isOwner(ClientIP))
				{
					if(!key.isExpired())
					{
						isIPMapped = 1;
						break;
					}
					else
					{
						isIPMapped = -1;
						
					}
				}
			}
		
		
		return isIPMapped;
	}
	
	public CAccessKey getKey(String KeyCode)
	{
		CAccessKey retkey = null;
		for(CAccessKey key : keys)
		{
			if(key.getKeyCode().equals(KeyCode) && key.isAvailable())
			{
				retkey = key;
				break;
			}
		}
		
		return retkey;
	}
	
	public CopyOnWriteArrayList<CAccessKey> getKeyList()
	{
		return keys;
	}
	
	public boolean isRunnable() { return true; }
	
	@SuppressWarnings("deprecation")
	public void run() {
		while(true)
		{
			try
			{
				
			int hasKeyRemove = 0;
			
				for(CAccessKey key : keys)
				{
					
						if( key.isExpired() ) 
						{
							Date expiredDate = (Date)key.getExpiration().clone();
							expiredDate.setMinutes(expiredDate.getMinutes() + 
												   CSysConfig.UnusedKeyDuration);
							
							if(expiredDate.before(new Date()))
							{
								removeKey(key);
								hasKeyRemove++;
							}
						}
				}


			if(hasKeyRemove >  0) System.out.println("Key Manager removed " + hasKeyRemove + " expired key/s..");
			
			
				Thread.sleep(200); 
			}catch(Exception e) { }
		}
	}

}
