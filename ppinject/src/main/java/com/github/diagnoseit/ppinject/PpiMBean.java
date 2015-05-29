package com.github.diagnoseit.ppinject;



public interface PpiMBean {
	public String getBeanName();
	public long addTheRampAspect(String signaturePattern, String scope, long offsetMillis, double alpha);
	public long addOneLaneBridgeAspect(String signaturePattern, String scope, int numLanes);
	public String getTheRampConfigForSignature(String signature);
	public String getOneLaneBridgeConfigForSignature(String signature);
	public void removeTheRampAspectForSignature(String signaturePattern);
	public void removeOneLaneBridgeConfigForSignature(String signaturePattern);
	public void removeTheRampAspectAll();
	public void removeOneLaneBridgeAll();
	public void clearAll();
	public String getConfigForId(long id);
	public String getStateForId(long id);
	public String showAll();
}
