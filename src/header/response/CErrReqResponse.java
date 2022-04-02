package header.response;

import java.net.Socket;

import main.CSysConfig;

import security.CSecMsg;
import security.CSecurityAgent.MsgCode;
import util.clogger.LogBuffer;

public class CErrReqResponse extends CReqResponse{

	
	
	public CErrReqResponse(Socket socket,LogBuffer log) {
		super(socket,log);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CErrReqResponse generatingResponseContent(Object artifact) {
		
		if(artifact instanceof CSecMsg)
		{

			String body="",header="";
			switch((MsgCode)((CSecMsg)artifact).code)
			{
				case C304:
					body 	= generateBody("admin_login_page.html",
											   replace( (((CSecMsg)artifact).content == null ? 
													   	"" : ((CSecMsg)artifact).content),"@ERR_MESSAGE"),
											   replace( CSysConfig.localHostIP, "@HOST_ADDRESS"));
					header 	= generateHeader("200","Administrative Login!",body.length());
				break;
				case C402: case C404:
					body 	= generateBody("err_page.html",
											replace(((CSecMsg)artifact).code.toString()	,"@ERR_CODE","@ERR_TITLE"),
										 	replace(((CSecMsg)artifact).content			,"@ERR_MESSAGE"));
					header 	= generateHeader(((CSecMsg)artifact).code.toString().substring(1),((CSecMsg)artifact).content,body.length());
				
				break;
			}
			
			this.content = (header + body).getBytes();
		}
		
		return this;
	}
	

	


}
