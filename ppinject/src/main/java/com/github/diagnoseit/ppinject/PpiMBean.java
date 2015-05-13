package com.github.diagnoseit.ppinject;



public interface PpiMBean {
	public String getBeanName();
	public void addTheRampAspect(String signaturePattern, String scope, long offsetMillis, double alpha);
	public void addOneLaneBridgeAspect(String signaturePattern, String scope, int numLanes);
	public String getTheRampConfigForSignature(String signature);
	public String getOneLaneBridgeConfigForSignature(String signature);
	public void removeTheRampAspectForSignature(String signaturePattern);
	public void removeOneLaneBridgeConfigForSignature(String signaturePattern);
	public void removeTheRampAspectAll();
	public void removeOneLaneBridgeAll();
	public void clearAll();
}
