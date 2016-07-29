package com.maldou.lightr.example.service.impl.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.maldou.lightr.example.service.AmapfuseSevice;
import com.maldou.lightr.example.service.entity.Amapfuse;
import com.maldou.lightr.server.Annocations.BusSerivce;

@BusSerivce
public class AmapfuseServiceImpl implements AmapfuseSevice {
	
	private Logger logger = Logger.getLogger(AmapfuseServiceImpl.class);

	@Override
	public String getNameById(long id) {
		logger.info("getNameByid:" + id);
		return "" + id;
	}

	@Override
	public List<Amapfuse> allEntities() {
		List<Amapfuse> list = new ArrayList<Amapfuse>();
		Amapfuse af = new Amapfuse();
		af.setDescription("no.1");
		af.setId(10000000L);
		af.setName("amapfuse1");
		list.add(af);
		
		Amapfuse af2 = new Amapfuse();
		af2.setDescription("no.2");
		af2.setId(20000000L);
		af2.setName("amapfuse2");
		list.add(af2);
		
		return list;
	}

}
