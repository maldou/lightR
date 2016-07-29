package com.maldou.lightr.client.socket;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

import com.maldou.lightr.client.loadbalance.ServerSelector;
import com.maldou.lightr.client.socket.out.OutValue;
import com.maldou.lightr.transport.TransportObject;

public class Sockets {
	
	private Sockets() {
		init();
	}
	
	private static Sockets instance = new Sockets();
	public static Sockets getInstance() {
		return instance;
	}
	
	private Map<String, ASocket> socketMap = new HashMap<String, ASocket>();
	
	private void init() {
		
	}
	
	public void openConnection(String remoteHost, int remotePort) {
		removeConnection(remoteHost, remotePort);
		
		String key = remoteHost + ":" + remotePort;
		if(socketMap.get(key) == null) { 
			synchronized(socketMap) {
				if(socketMap.get(remoteHost + ":" + remotePort) == null) { 
					ASocket aSocket = new ASocket(remoteHost, remotePort);
					try {
						aSocket.start();
						socketMap.put(key, aSocket);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void removeConnection(String remoteHost, int remotePort) {
		ASocket aSocket = socketMap.remove(remoteHost + ":" + remotePort);
		if(aSocket != null) {
			aSocket.stop();
		}
	}
	
	public void write(String service, Object msg) {
		String server = ServerSelector.getInstance().getServer(service);
		ASocket selectSocket = socketMap.get(server);
		if(selectSocket != null) {
			selectSocket.writeAndFlush(msg);
		}
	}
	
	public void asyncRequest(String service, TransportObject request) {
		write(service, request);
	}
	
	public Object syncRequest(String service, TransportObject request) {
		return syncRequest(service, request, 1000);
	}
	
	public Object syncRequest(String service, TransportObject request, long waitTime) {
		write(service, request);
		long tpObjectId = request.getId();
		TransportObject result = OutValue.getInstance().getValueById(tpObjectId, waitTime);
		if(result != null && result.getTransType() == 2) {
			return result.getOutObject();
		}
		return null;
	}

}
