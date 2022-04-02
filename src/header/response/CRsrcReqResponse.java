package header.response;

import header.CHTTPRequest;

import java.net.Socket;

import util.clogger.LogBuffer;

import manager.CHTTPResourceManager;

public class CRsrcReqResponse extends CReqResponse{

	public CRsrcReqResponse(Socket socket,LogBuffer log) {
		super(socket,log);
	}
	
	protected CRsrcReqResponse generatingResponseContent(Object artifact)
	{
		if(artifact instanceof CHTTPRequest)
		{
			String filename = ((CHTTPRequest)artifact).url;
			
			this.content = CHTTPResourceManager.getContent(filename);
		}
		return this;
	}

}
