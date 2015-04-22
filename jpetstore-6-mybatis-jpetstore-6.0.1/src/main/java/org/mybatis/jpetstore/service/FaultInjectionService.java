package org.mybatis.jpetstore.service;

import java.util.concurrent.ConcurrentHashMap;

public class FaultInjectionService {
	private static final FaultInjectionService INSTANCE = new FaultInjectionService();
	
	private ConcurrentHashMap<String, Long> complexity = new ConcurrentHashMap<String, Long>();
	private ConcurrentHashMap<String, Long> memoryLeak = new ConcurrentHashMap<String, Long>();
	
	private FaultInjectionService() {
		
	}
	
	public FaultInjectionService getInstance() {
		return FaultInjectionService.INSTANCE;
	}
}
