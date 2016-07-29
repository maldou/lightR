package com.maldou.lightr.client.loadbalance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.maldou.lightr.client.socket.Sockets;

import org.apache.zookeeper.ZooKeeper;

public class ServerSelector {
	
	private ZooKeeper zk;	
	private String zkConnectStr = "192.168.1.100:2181";
	
	private String root = "/myservice";
	
	private Map<String, Boolean> serviceInitMap = new HashMap<String, Boolean>();
	private Map<String, Integer> currentServiceMap = new HashMap<String, Integer>();
	private Map<String, List<String>> serviceMap = new HashMap<String, List<String>>();
	
	private ServerSelector() {
		try {
			zk = new ZooKeeper(zkConnectStr, 2000, new Watcher() {
	            // 监控所有被触发的事件
	            public void process(WatchedEvent event) {
	                System.out.println("已经触发了" + event.getType() + "事件！");
	            }
	        });
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	private static ServerSelector instance = new ServerSelector();
	public static ServerSelector getInstance() {
		return instance;
	}
	
	public String getServer(final String serviceName) {
		try {
			List<String> serviceList = serviceMap.get(serviceName);
			if(serviceList == null) {
				Boolean isInit = serviceInitMap.get(serviceName);
				if(isInit != null && isInit) {
					return null;
				}
				final String path = root + "/" + serviceName;
				serviceList = zk.getChildren(path, new ServiceNodeWatcher(path, serviceName));
				serviceInitMap.put(serviceName, true);
				if(serviceList == null || serviceList.size() == 0) {
					return null;
				}
				serviceMap.put(serviceName, serviceList);
				for(String serverStr : serviceList) {
					String[] serviceConfig = serverStr.split(":");
					Sockets.getInstance().openConnection(serviceConfig[0], Integer.valueOf(serviceConfig[1]));
				}
			}
			if(serviceList == null || serviceList.size() == 0) {
				return null;
			}
			
			Integer currentServiceIndex = currentServiceMap.get(serviceName);
			if(currentServiceIndex == null || currentServiceIndex >= serviceList.size() - 1) {
				currentServiceIndex = 0;
			}
			else {
				currentServiceIndex += 1;
			}
			currentServiceMap.put(serviceName, currentServiceIndex);
			return serviceList.get(currentServiceIndex);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private class ServiceNodeWatcher implements Watcher {
		
		private String path;
		private String serviceName;
		
		public ServiceNodeWatcher(String path, String serviceName) {
			this.path = path;
			this.serviceName = serviceName;
		}
		
		@Override
		public void process(WatchedEvent event) {
            if(event.getType() == EventType.NodeChildrenChanged) {
            	System.out.println(event.getWrapper());
            	try {
   				 List<String> newServerList = zk.getChildren(path, this);
   				 List<String> serverList = serviceMap.get(serviceName);
   				 if(serverList == null) {
   					 serverList = new ArrayList<String>();
   				 }
   				 List<Integer> deleteIndex = new ArrayList<Integer>();
  				 for(int i = 0; i < serverList.size(); i++) {
  					 String serverStr = serverList.get(i);
  					 if(!newServerList.contains(serverStr)) {
  						 deleteIndex.add(i);
  					 }
  				 }
  				//删除server
  				 for(Integer index : deleteIndex) {
  					 String serverStr = serverList.remove(index.intValue());
  					 String[] serverConfig = serverStr.split(":");
  	            	 Sockets.getInstance().removeConnection(serverConfig[0], Integer.valueOf(serverConfig[1]));
  				 }
  				 
   				 for(String serverStr : newServerList) {
   					 if(!serverList.contains(serverStr)) {
   						 //新增server
   						 serverList.add(serverStr);
   						 String[] serviceConfig = serverStr.split(":");
      					 Sockets.getInstance().openConnection(serviceConfig[0], Integer.valueOf(serviceConfig[1]));
   					 }
   				 }
   				 serviceMap.put(serviceName, serverList);
   			} catch (Exception e) {
   				e.printStackTrace();
   			}
          }
		}
		
	}
	
	private class ServerNodeWatcher implements Watcher {

		private String serviceName;
		private String serverStr;
		
		public ServerNodeWatcher(String serviceName, String serverStr) {
			this.serviceName = serviceName;
			this.serverStr = serverStr;
		}
		
		@Override
		public void process(WatchedEvent event) {
            System.out.println("已经触发了" + event.getType() + "事件！");
            if(event.getType() == EventType.NodeDeleted) {
            	List<String> serverList = serviceMap.get(serviceName);
            	if(serverList == null || serverList.size() == 0) {
            		return ;
            	}
            	serverList.remove(serverStr);
            	serviceMap.put(serviceName, serverList);
            	
            	String[] serverConfig = serverStr.split(":");
            	Sockets.getInstance().removeConnection(serverConfig[0], Integer.valueOf(serverConfig[1]));
            }
		}
		
	}

}
