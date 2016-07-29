package com.maldou.lightr.client;

import java.util.List;

import com.maldou.lightr.client.proxy.ProxyFactory;
import com.maldou.lightr.example.service.AmapfuseSevice;
import com.maldou.lightr.example.service.entity.Amapfuse;

public class ClientTest {

    public static void main(String[] args) throws Exception {
    	 for(long i = 100; i < 200000; i++) {
    		 System.out.println("start:" + i);
    		 String url = "bus://busservice1/AmapfuseServiceImpl";
             AmapfuseSevice serivce = (AmapfuseSevice)ProxyFactory.createProxy(url, AmapfuseSevice.class);
             String result = serivce.getNameById(i);
             System.out.println("result:" + result);
             List<Amapfuse> lists = serivce.allEntities();
             if(lists != null) {
            	 for(Amapfuse a : lists) {
//            		 System.out.println(a.getId() + "," + a.getName());
            	 }
             }
    	 }
    }
}
