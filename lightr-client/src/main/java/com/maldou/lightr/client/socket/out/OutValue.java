package com.maldou.lightr.client.socket.out;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.maldou.lightr.transport.TransportObject;

public class OutValue {	
	private ListeningExecutorService service1 = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
	
	private OutValue() {
		
	}
	private static OutValue instance = new OutValue();
	public static OutValue getInstance() {
		return instance;
	}
	
	private Map<Long, TransportObject> transObjectMap = new ConcurrentHashMap<Long, TransportObject>();
	
	public TransportObject getValueById(long id) {
		return getValueById(id, 5 * 1000);
	}
	
	public TransportObject getValueById(final long id, final long waitTime) {
		
		ListenableFuture<TransportObject> future = service1.submit(new Callable<TransportObject>() {
			@Override
			public TransportObject call() throws Exception {
				TransportObject result = transObjectMap.remove(id);
				long time = System.currentTimeMillis();
				while(result == null) {
					result = transObjectMap.remove(id);
					long nowTime = System.currentTimeMillis();
					if(nowTime - time > waitTime) {
						break;
					}
					LockSupport.parkNanos(1);
				}
				return result;
			}
		
		});
		
//		Futures.addCallback(future, new FutureCallback<TransportObject>() {
//			@Override
//			public void onSuccess(TransportObject result) {
//				System.out.println("onsuccess:" + result.getId());
//			}
//
//			@Override
//			public void onFailure(Throwable t) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
		
		try {
			return future.get(waitTime, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			//e.printStackTrace();
		} catch (ExecutionException e) {
			//e.printStackTrace();
		} catch (TimeoutException e) {
			//e.printStackTrace();
		}
		return null;
	}
	
	public void addValue(TransportObject transportObject) {
		if(transportObject == null) {
			return ;
		}
		long id = transportObject.getId();
		transObjectMap.put(id, transportObject);
	}

}
