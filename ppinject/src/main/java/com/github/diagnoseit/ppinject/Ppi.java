package com.github.diagnoseit.ppinject;

import kieker.monitoring.core.signaturePattern.InvalidPatternException;

import com.github.diagnoseit.ppinject.problems.OneLaneBridgeAspect;
import com.github.diagnoseit.ppinject.problems.TheRampAspect;


public class Ppi implements PpiMBean {

	private InjectionService injectionService = InjectionService.getInstance();
	
	@Override
	public String getBeanName() {
		return "Ppi";
	}
	
	@Override
	public void addTheRampAspect(String signaturePattern, String scope, long offsetMillis, double alpha) {
		TheRampAspect.Config config = new TheRampAspect.Config(Scope.valueOf(scope), offsetMillis, alpha);
		try {
			this.injectionService.configure(signaturePattern, TheRampAspect.class, config);
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void addOneLaneBridgeAspect(String signaturePattern, String scope, int numLanes) {
		OneLaneBridgeAspect.Config config = new OneLaneBridgeAspect.Config(Scope.valueOf(scope), numLanes);
		try {
			this.injectionService.configure(signaturePattern, OneLaneBridgeAspect.class, config);
		} catch (InvalidPatternException e) {
			e.printStackTrace();
		}
	}
}

