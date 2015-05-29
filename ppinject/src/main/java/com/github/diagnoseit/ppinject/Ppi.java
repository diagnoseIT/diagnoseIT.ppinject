package com.github.diagnoseit.ppinject;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import kieker.monitoring.core.signaturePattern.InvalidPatternException;

import com.github.diagnoseit.ppinject.problems.OneLaneBridgeAspect;
import com.github.diagnoseit.ppinject.problems.TheRampAspect;

public class Ppi implements PpiMBean {

	private InjectionService injectionService;

	@Override
	public String getBeanName() {
		return "Ppi";
	}
	
	private void initInjectionService() {
		if(this.injectionService == null) {
			this.injectionService = InjectionService.getInstance();
		}
	}

	@Override
	public long addTheRampAspect(String signaturePattern, String scope,
			long offsetMillis, double alpha) {
		
		long id = -1;
		
		initInjectionService();
		
		TheRampAspect.Config config = new TheRampAspect.Config(
				Scope.valueOf(scope), offsetMillis, alpha);

		try {
			this.injectionService.configure(signaturePattern,
					TheRampAspect.class, config);
			id = config.getId();
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}
		return id;
	}

	@Override
	public long addOneLaneBridgeAspect(String signaturePattern, String scope,
			int numLanes) {
		
		long id = -1;
		
		initInjectionService();
		
		OneLaneBridgeAspect.Config config = new OneLaneBridgeAspect.Config(
				Scope.valueOf(scope), numLanes);

		try {
			this.injectionService.configure(signaturePattern,
					OneLaneBridgeAspect.class, config);
			id = config.getId();
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}
		return id;
	}

	@Override
	public String getTheRampConfigForSignature(String signature) {
		initInjectionService();
		SignatureConfigurationStore<TheRampAspect.Config> config = this.injectionService.getConfiguration(TheRampAspect.class);
		if(null == config) {
			return "No configuration defined for TheRampAspect";
		}
		TheRampAspect.Config rampConfig = config.getConfiguration(signature);
		if(null == rampConfig) {
			return "No Configuration defined for \"" + signature + "\"";
		}
		return rampConfig.toString();
	}
		
	@Override
	public String getOneLaneBridgeConfigForSignature(String signature) {
		initInjectionService();
		SignatureConfigurationStore<OneLaneBridgeAspect.Config> config = this.injectionService.getConfiguration(OneLaneBridgeAspect.class);
		if(null == config) {
			return "No configuration defined for OneLaneBridgeAspect";
		}
		OneLaneBridgeAspect.Config bridgeConfig = config.getConfiguration(signature);
		if(null == bridgeConfig) {
			return "No Configuration defined for \"" + signature + "\"";
		}
		return bridgeConfig.toString();
	}

	@Override
	public void removeTheRampAspectForSignature(String signaturePattern) {
		initInjectionService();
		try {
			this.injectionService.deconfigure(signaturePattern, TheRampAspect.class);
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void removeOneLaneBridgeConfigForSignature(String signaturePattern) {
		initInjectionService();
		try {
			this.injectionService.deconfigure(signaturePattern, OneLaneBridgeAspect.class);
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeTheRampAspectAll() {
		initInjectionService();
		this.injectionService.clearForProblemType(TheRampAspect.class);
	}

	@Override
	public void removeOneLaneBridgeAll() {
		initInjectionService();
		this.injectionService.clearForProblemType(OneLaneBridgeAspect.class);
	}

	@Override
	public void clearAll() {
		initInjectionService();
		this.injectionService.clearAll();
	}

	@Override
	public String getConfigForId(long id) {
		return ConfStateManager.getConfigById(id).toString();
	}

	@Override
	public String getStateForId(long id) {
		return ConfStateManager.getStateById(id).toString();
	}

	@Override
	public String showAll() {
		StringBuilder sb = new StringBuilder();
		Map<Long, Long> csm = ConfStateManager.getConfigToStateMap();
		for(long confId : csm.keySet()) {
			ProblemConfiguration config = ConfStateManager.getConfigById(confId);
			ExecutionState state = ConfStateManager.getStateById(config.getId());
			sb.append("config: " + config.toString() + "; state: " + state.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
