package header.response;



import java.net.Socket;

import util.clogger.LogBuffer;

public class CAdsReqResponse extends CReqResponse{

	public CAdsReqResponse(Socket socket,LogBuffer log) {
		super(socket,log);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CAdsReqResponse generatingResponseContent(Object request) {
		// TODO Auto-generated method stub
		return null;
	}

}
