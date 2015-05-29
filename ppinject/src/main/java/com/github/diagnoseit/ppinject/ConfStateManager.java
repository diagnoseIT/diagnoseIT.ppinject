package com.github.diagnoseit.ppinject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConfStateManager {
	
	private static ConcurrentMap<Long,Long> configToStateMap = new ConcurrentHashMap<Long,Long>();
	private static ConcurrentMap<Long, ProblemConfiguration> configMap = new ConcurrentHashMap<Long, ProblemConfiguration>();
	private static ConcurrentMap<Long, ExecutionState> stateMap = new ConcurrentHashMap<Long, ExecutionState>();
	
    public static <C extends ProblemConfiguration, S extends ExecutionState> void addMapping(C config, final S state) {
    	addConfigToStateMapping(config.getId(), state.getId());
    	addConfigMapping(config);
    	addStateMapping(state);
    }
    
    public static ProblemConfiguration getConfigById(final long id) {
    	return configMap.get(id);
    }
    
    public static ExecutionState getStateById(final long id) {
    	return stateMap.get(id);
    }
    
    private static <C extends ProblemConfiguration> void addConfigMapping(final C config) {
    	configMap.put(config.getId(), config);
    }
    
    private static <S extends ExecutionState> void addStateMapping(final S state) {
    	stateMap.put(state.getId(), state);
    }
    
    public static Long getStateByConfigId(final long configId) {
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
}
