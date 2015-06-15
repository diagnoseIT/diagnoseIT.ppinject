package com.github.diagnoseit.ppinject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConfStateManager {
	
	private static ConcurrentMap<Long, Long> configToStateMap = new ConcurrentHashMap<Long,Long>();
	private static ConcurrentMap<Long, ProblemConfiguration> configMap = new ConcurrentHashMap<Long, ProblemConfiguration>();
	private static ConcurrentMap<Long, ExecutionState> stateMap = new ConcurrentHashMap<Long, ExecutionState>();
	
    public static void addMapping(ProblemConfiguration config, final ExecutionState state) {
    	addConfigToStateMapping(config.getId(), state.getId());
    	addConfigMapping(config);
    	addStateMapping(state);
    }
    
    public static void deleteConfig(final long id) {
    	configToStateMap.remove(id);
    	configMap.remove(id);
    }
    
    public static void deleteState(final long id) {
    	configToStateMap.keySet().remove(id);
    	stateMap.remove(id);
    }
    
    public static ProblemConfiguration getConfigById(final long id) {
    	return configMap.get(id);
    }
    
    public static ExecutionState getStateById(final long id) {
    	return stateMap.get(id);
    }
    
    private static void addConfigMapping(final ProblemConfiguration config) {
    	configMap.put(config.getId(), config);
    }
    
    private static void addStateMapping(final ExecutionState state) {
    	stateMap.put(state.getId(), state);
    }
    
    public static Long getStateIdByConfigId(final long configId) {
    	return configToStateMap.get(configId);
    }
    
    private static void addConfigToStateMapping(final long configId, final long stateId) {
        
//        ArrayList<Long> stateIdList = configToStateMap.get(configId);
//        
//        if(null != stateIdList) {
//        	// list already exists
//        	if(!stateIdList.contains(stateId)) {
//        		
//        		// does not contain state yet
//        		stateIdList.add(stateId);
//        	}
//        } else {
//        	// list does not exist yet, so create a new state list and add state to it
//        	stateIdList = new ArrayList<Long>();
//        	stateIdList.add(stateId);
//        }
//        configToStateMap.put(configId, stateIdList);
        
        configToStateMap.put(configId, stateId);
 }
    
    public static Map<Long,Long> getConfigToStateMap() {
    	return configToStateMap;
    }
    
	public static List<String> getConfMap() {
		List<String> confList = new ArrayList<String>();
    	for(long i : configMap.keySet()) {
    		confList.add(configMap.get(i) + "");
    	}
		return confList;
	}

	public static List<String> getStateMap() {
		List<String> stateList = new ArrayList<String>();
    	for(long j : stateMap.keySet()) {
    		stateList.add(stateMap.get(j) + "");
    	}
		return stateList;
	}

	public static List<String> getConfStateMap() {
		List<String> confStateList = new ArrayList<String>();
    	for(long k: configToStateMap.keySet()) {
    		confStateList.add(k + " -> " + configToStateMap.get(k));
    	}
    	return confStateList;
	}

	public static void deleteMapping(long id, long stateId) {
		if(configToStateMap.get(id) == stateId) {
			configToStateMap.remove(id);
		}
	}
}
