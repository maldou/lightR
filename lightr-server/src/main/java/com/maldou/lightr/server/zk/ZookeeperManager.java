package com.maldou.lightr.server.zk;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.maldou.lightr.server.util.Constants;
import com.maldou.lightr.server.util.GlobalParameter;

public class ZookeeperManager {
	
	private static Logger logger = Logger.getLogger(ZookeeperManager.class);
	
	private ZooKeeper zk;
	
	private String zkConnectStr = Constants.zkConStr;	
	private String root = Constants.zkRoot;
	private int sessionTimeOut = Constants.zkSessionTimeOut;
	
	private String serverName = GlobalParameter.getInstance().getServiceName();
	private String host = Constants.serviceHost;
	private int port = Constants.servicePort;
	
	private String zkPath;
	
	private ZookeeperManager() {
		try {
			zk = new ZooKeeper(zkConnectStr, sessionTimeOut, new Watcher() {
	            // 监控所有被触发的事件
	            public void process(WatchedEvent event) {
	                logger.info("已经触发了" + event.getState() + " && " + event.getWrapper() + " && " + event.getPath() + " && " + event.getType() + "事件！");
	            } 
	        });
			Stat stat = zk.exists(root , true);
			if(stat == null) {
				zk.create(root, "0".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			stat = zk.exists(root + "/" + serverName, false);
			if(stat == null) {
				zk.create(root + "/" + serverName, "0".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			zkPath = root + "/" + serverName + "/" + host + ":" + port;
		} catch(Exception e) {
			logger.error("", e);
		}
	}
	private static ZookeeperManager instance = new ZookeeperManager();
	public static ZookeeperManager getInstance() {
		return instance;
	}
	
	public void registServer() {
		try {
			Stat stat = zk.exists(zkPath , true);
			if(stat == null) {
				ByteBuffer b = ByteBuffer.allocate(4); 		       
		        b.putInt(11); 
		        byte[] value = b.array();
				zk.create(zkPath, value, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void removeServer() {
		
	}
	
	public void oper() {
		try {
			System.out.println(zk.getChildren("/myservice/myserver1", true));
			List<String> list = zk.getChildren("/myservice/myserver1", true);
			for(String znode : list) {
				byte[] date = zk.getData("/myservice/myserver1/" + znode, true, null);
				ByteBuffer buffer = ByteBuffer.wrap(date);
				int result = buffer.getInt();
				System.out.println(result);
				
				Stat stat = zk.exists("/myservice/myserver1/" + znode, false);
				buffer = ByteBuffer.allocate(4);
				buffer.putInt(22);
				zk.setData("/myservice/myserver1/" + znode, buffer.array(), 0);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ZookeeperManager zkM = ZookeeperManager.getInstance();
		
		zkM.registServer();
		zkM.oper();
	}

}
