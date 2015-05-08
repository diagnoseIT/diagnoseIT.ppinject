package com.github.diagnoseit.ppinject;


public interface PpiMBean {
	public String getBeanName();
	public void addTheRampAspect(String signaturePattern, String scope, long offsetMillis, double alpha);
	public void addOneLaneBridgeAspect(String signaturePattern, String scope, int numLanes);
}
