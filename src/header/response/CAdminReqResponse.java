package header.response;


import java.net.Socket;

import main.CSysConfig;
import main.CSysDaemon;
import manager.CHTTPResourceManager;

import security.CSecMsg;
import security.CSecurityAgent.MsgCode;
import util.CStringHelper;
import util.clogger.LogBuffer;

public class CAdminReqResponse extends CReqResponse{

	public CAdminReqResponse(Socket socket, LogBuffer log) {
		super(socket, log);
	}

	private String generateHeader(int length)
	{
		return generateHeader( "200","Ok",length);
	}
	
	private String getIndex(MsgCode code)
	{
		int index=1;
		switch(code)
		{
			case C302:
				index=1;
			break;
 			case C303:
				index=3;
			break;
				
		}
		return Integer.toString(index);
	}
	
	protected CReqResponse generatingResponseContent(Object artifact) {
		


		
		if(artifact instanceof CSecMsg)
		{
			String body="",header="";
			
			switch((MsgCode)((CSecMsg)artifact).code)
			{
				case C302:case C303:
					header = generateHeaderRedirect("http://" + CSysConfig.localHostIP + "/admin?Tab=" + getIndex( ((CSecMsg)artifact).code ));
				break;
				default:
					body 	= generateBody("admin_config_page.html",
							   			replace( ( ((CSecMsg)artifact).content != null ? ((CSecMsg)artifact).content : "1" )	,"@INDEX_VAR"),
							   			replace( CSysConfig.HTTPResourceManger.LAST_TIME_GENERATED_KEY		, "@KEYGEN-GENERATED-TIME"),
							   			replace( CSysConfig.HTTPResourceManger.LAST_DURATION_GENERATED_KEY	, "@KEYGEN-GENERATED-DURA"),
							   			replace( CSysConfig.HTTPResourceManger.LAST_GENERATED_KEY			, "@KEYGEN"),
							   			MultipleReplace(CStringHelper.extractStrFromRange(new String(CHTTPResourceManager.getContent("admin_config_page.html",true)), 
							   												  			  "</@REPEAT-KEYGEN-LIST>" , 
							   												  			  "</@REPEAT-KEYGEN-LIST /CLOSE>")
							   							,CSysDaemon.AccessKeyManager.getKeyList().toArray() ),
							   							MultipleReplace(CStringHelper.extractStrFromRange(new String(CHTTPResourceManager.getContent("admin_config_page.html",true)), 
	   												  			  "</@REPEAT-BLOCKSITE-LIST>" , 
	   												  			  "</@REPEAT-BLOCKSITE-LIST /CLOSE>")
	   												  			  ,CSysDaemon.BlackListed.toArray() )
	   			
							   			);
			
					header = generateHeader(body.length());
				break;
			}
			

			this.content = (header + body).getBytes();
		}
		return this;
	}
	

}
