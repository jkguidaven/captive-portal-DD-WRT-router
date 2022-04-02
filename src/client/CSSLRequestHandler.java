package client;

import java.net.Socket;

import security.CSSLHandler;
import util.clogger.LogBuffer;

public class CSSLRequestHandler extends CRequestHandler implements CSSLHandler {

	public CSSLRequestHandler(String clientIP,Socket client, LogBuffer log) {
		super(clientIP, client, log);
	}

}
