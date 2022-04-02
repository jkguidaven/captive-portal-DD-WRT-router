package header.response;



import java.net.Socket;

import main.CSysConfig;

import security.CSecMsg;
import security.CSecurityAgent.MsgCode;
import util.clogger.LogBuffer;

public class CProxReqResponse extends CReqResponse{

	public CProxReqResponse(Socket socket,LogBuffer log) {
		super(socket,log);
	}
	
	
	
	private String generateHeader(int length)
	{
		return generateHeader( "200","Ok",length);
	}
	

	@Override
	protected CProxReqResponse generatingResponseContent(Object artifact)
	{
		if(artifact instanceof CSecMsg)
		{
			String body="",header="";
			switch((MsgCode)((CSecMsg)artifact).code)
			{
			case C201: case C202:
				body 	= generateBody("ask_key_page.html",
						   replace( (((CSecMsg)artifact).content == null ? 
								   	"" : ((CSecMsg)artifact).content),"@ERR_MESSAGE"),
						   replace( CSysConfig.localHostIP, "@HOST_ADDRESS"));
				header 	= generateHeader(body.length());
				break;
			}
			

			this.content = (header + body).getBytes();
		}
		return this;
	}

}
