package com.example.service;

import java.util.List;
import java.util.Map;

public interface SpringDemoMapper {
	List<Map<String, Object>> findAll();
	
	void addEntity(Map<String, Object> entity);
}
