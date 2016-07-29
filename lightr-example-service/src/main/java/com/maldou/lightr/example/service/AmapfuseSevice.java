package com.maldou.lightr.example.service;

import java.util.List;

import com.maldou.lightr.example.service.entity.Amapfuse;

public interface AmapfuseSevice {
	public String getNameById(long id);
	public List<Amapfuse> allEntities();
}
