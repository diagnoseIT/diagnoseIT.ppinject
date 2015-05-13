package com.github.diagnoseit.ppinject;

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
	public void addTheRampAspect(String signaturePattern, String scope,
			long offsetMillis, double alpha) {
		initInjectionService();
		
		TheRampAspect.Config config = new TheRampAspect.Config(
				Scope.valueOf(scope), offsetMillis, alpha);

		try {
			this.injectionService.configure(signaturePattern,
					TheRampAspect.class, config);
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addOneLaneBridgeAspect(String signaturePattern, String scope,
			int numLanes) {
		initInjectionService();
		
		OneLaneBridgeAspect.Config config = new OneLaneBridgeAspect.Config(
				Scope.valueOf(scope), numLanes);

		try {
			this.injectionService.configure(signaturePattern,
					OneLaneBridgeAspect.class, config);
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getTheRampConfigForSignature(String signature) {
		initInjectionService();
		SignatureConfigurationStore<TheRampAspect.Config> config = this.injectionService.getConfiguration(TheRampAspect.class);
		TheRampAspect.Config rampConfig = config.getConfiguration(signature);
		return signature + "; " + rampConfig.getExecutionScope().name() + "; " 
				+ rampConfig.getOffsetMillis() + "; " + rampConfig.getAlpha() + "; " + rampConfig.isActivated();
	}
	@Override
	public String getOneLaneBridgeConfigForSignature(String signature) {
		initInjectionService();
		SignatureConfigurationStore<OneLaneBridgeAspect.Config> config = this.injectionService.getConfiguration(OneLaneBridgeAspect.class);
		OneLaneBridgeAspect.Config bridgeConfig = config.getConfiguration(signature);
		return signature + "; " + bridgeConfig.getExecutionScope().name() + "; "
				+ bridgeConfig.getNumberOfLanes() + "; " + bridgeConfig.isActivated();
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
}
