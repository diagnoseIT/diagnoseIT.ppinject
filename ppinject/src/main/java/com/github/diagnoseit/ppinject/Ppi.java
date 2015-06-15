package com.github.diagnoseit.ppinject;

import java.util.List;

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
		if (this.injectionService == null) {
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
		SignatureConfigurationStore<TheRampAspect.Config> config = this.injectionService
				.getConfiguration(TheRampAspect.class);
		if (null == config) {
			return "No configuration defined for TheRampAspect";
		}
		TheRampAspect.Config rampConfig = config.getConfiguration(signature);
		if (null == rampConfig) {
			return "No Configuration defined for \"" + signature + "\"";
		}
		return rampConfig.toString();
	}

	@Override
	public String getOneLaneBridgeConfigForSignature(String signature) {
		initInjectionService();
		SignatureConfigurationStore<OneLaneBridgeAspect.Config> config = this.injectionService
				.getConfiguration(OneLaneBridgeAspect.class);
		if (null == config) {
			return "No configuration defined for OneLaneBridgeAspect";
		}
		OneLaneBridgeAspect.Config bridgeConfig = config
				.getConfiguration(signature);
		if (null == bridgeConfig) {
			return "No Configuration defined for \"" + signature + "\"";
		}
		return bridgeConfig.toString();
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
	public void removeTheRampAspectById(long id) {
		initInjectionService();

		ProblemConfiguration pc = ConfStateManager.getConfigById(id);
		ExecutionStateStore<? extends ExecutionState> ess = InjectionService
				.getExecutionStateStore(TheRampAspect.class);
		SignatureConfigurationStore<? extends ProblemConfiguration> scs = injectionService
				.getConfiguration(TheRampAspect.class);
		String signature = scs
				.getSignatureForConfiguration((TheRampAspect.Config) pc);

		try {
			this.injectionService.deconfigure(signature, TheRampAspect.class);
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}
		long stateId = ConfStateManager.getStateIdByConfigId(id);
		ess.deleteState(pc.getExecutionScope(), stateId);

		deleteMappings(id, stateId);
	}

	@Override
	public void removeOneLaneBridgeById(long id) {
		initInjectionService();

		ProblemConfiguration pc = ConfStateManager.getConfigById(id);
		ExecutionStateStore<? extends ExecutionState> ess = InjectionService
				.getExecutionStateStore(OneLaneBridgeAspect.class);
		SignatureConfigurationStore<? extends ProblemConfiguration> scs = injectionService
				.getConfiguration(OneLaneBridgeAspect.class);
		String signature = scs.getSignatureForConfiguration(pc);

		try {
			this.injectionService.deconfigure(signature,
					OneLaneBridgeAspect.class);
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}
		long stateId = ConfStateManager.getStateIdByConfigId(id);
		ess.deleteState(pc.getExecutionScope(), stateId);

		deleteMappings(id, stateId);
	}

	@Override
	public void removeTheRampAspectForSignature(String signaturePattern) {
		initInjectionService();

		ExecutionStateStore<? extends ExecutionState> ess = InjectionService
				.getExecutionStateStore(TheRampAspect.class);
		SignatureConfigurationStore<? extends ProblemConfiguration> scs = injectionService
				.getConfiguration(TheRampAspect.class);
		long configId = scs.getConfiguration(signaturePattern).getId();
		ProblemConfiguration pc = ConfStateManager.getConfigById(configId);

		try {
			this.injectionService.deconfigure(signaturePattern,
					TheRampAspect.class);
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}

		long stateId = ConfStateManager.getStateIdByConfigId(configId);
		ess.deleteState(pc.getExecutionScope(), stateId);
		deleteMappings(configId, stateId);
	}

	@Override
	public void removeOneLaneBridgeConfigForSignature(String signaturePattern) {
		initInjectionService();

		ExecutionStateStore<? extends ExecutionState> ess = InjectionService
				.getExecutionStateStore(OneLaneBridgeAspect.class);
		SignatureConfigurationStore<? extends ProblemConfiguration> scs = injectionService
				.getConfiguration(OneLaneBridgeAspect.class);
		long configId = scs.getConfiguration(signaturePattern).getId();
		ProblemConfiguration pc = ConfStateManager.getConfigById(configId);

		try {
			this.injectionService.deconfigure(signaturePattern,
					OneLaneBridgeAspect.class);
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}
		long stateId = ConfStateManager.getStateIdByConfigId(configId);
		ess.deleteState(pc.getExecutionScope(), stateId);
		deleteMappings(configId, stateId);
	}

	@Override
	public List<String> getConfMap() {
		return ConfStateManager.getConfMap();
	}

	@Override
	public List<String> getStateMap() {
		return ConfStateManager.getStateMap();
	}

	@Override
	public List<String> getConfStateMap() {
		return ConfStateManager.getConfStateMap();
	}

	private void deleteMappings(final long configId, final long stateId) {
		ConfStateManager.deleteMapping(configId, stateId);
		ConfStateManager.deleteConfig(configId);
		ConfStateManager.deleteState(stateId);
	}
}
