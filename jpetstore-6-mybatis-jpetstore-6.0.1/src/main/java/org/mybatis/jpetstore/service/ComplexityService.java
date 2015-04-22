package org.mybatis.jpetstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Andre van Hoorn
 * 
 */
public class ComplexityService {
	private static final ComplexityService INSTANCE = new ComplexityService();

	private final Random rnd = new Random();

	private final ConcurrentHashMap<String, Long> injectionComplexities = new ConcurrentHashMap<String, Long>();
	private final ConcurrentHashMap<String, Long> injectionComplexities2 = new ConcurrentHashMap<String, Long>();
	private final ConcurrentHashMap<String, Long> delay = new ConcurrentHashMap<String, Long>();
	private final ConcurrentHashMap<String, Long> memoryLeakIntensities = new ConcurrentHashMap<String, Long>();

	private final List<List<Integer>> memoryLeak = new ArrayList<List<Integer>>();

	/**
	 * 
	 */
	private ComplexityService() {

	}

	public final static ComplexityService getInstance() {
		return ComplexityService.INSTANCE;
	}

	/**
	 * @return the complexity
	 */
	public final long getComplexity(final String methodName) {
		if (this.injectionComplexities.containsKey(methodName)) {
			return this.injectionComplexities.get(methodName);
		}
		return 0;
	}

	/**
	 * @param complexity
	 *            the complexity to set
	 */
	public final void setComplexity(final String methodName,
			final long complexity) {
		this.injectionComplexities.put(methodName, complexity);
	}

	public final void setComplexity2(final String methodName,
			final long complexity) {
		this.injectionComplexities2.put(methodName, complexity);
	}

	public final void setMemoryLeakIntensity(final String methodName,
			final long leakIntensity) {
		this.memoryLeakIntensities.put(methodName, leakIntensity);
	}
	
	public final void setDelayTime(final String methodName,
			final long delayTime) {
		this.delay.put(methodName, delayTime);
	}
	
	public final long getDelayTime(final String methodName) {
		if (this.delay.containsKey(methodName)) {
			return this.delay.get(methodName);
		}
		return 0;
	}

	public final int compute(final String methodName) {
		int retVal = 0;

		if (!this.injectionComplexities.containsKey(methodName)) {
			return retVal;
		}

		final long complexity = this.injectionComplexities.get(methodName);
		if (complexity <= 0) {
			return 0;
		} else {
			for (int i = 1; i < complexity; i++) { // i must not be 0!
				retVal *= Math.tan(Math.atan(Math.tan(Math.atan(Math.tan(Math
						.atan(this.rnd.nextInt(i)))))));
			}
		}
		return retVal;
	}

	public final int compute2(final String methodName) {
		int retVal = 0;

		if (!this.injectionComplexities2.containsKey(methodName)) {
			return retVal;
		}

		long complexity = this.injectionComplexities2.get(methodName);
		if (complexity <= 0) {
			complexity = 1;
		} else if (complexity > 1000000) {
			complexity = 1000000;
		} else {
			complexity += 1000;
		}

		this.injectionComplexities2.put(methodName, complexity);
		for (int i = 1; i < complexity; i++) { // i must not be 0!
			retVal *= Math.tan(Math.atan(Math.tan(Math.atan(Math.tan(Math
					.atan(this.rnd.nextInt(i)))))));
		}
		return retVal;
	}

	public final long leak(final String componentName) {
		if (!this.memoryLeakIntensities.contains(componentName)) {
			return 0;
		}

		final long leakIntensity = this.memoryLeakIntensities
				.get(componentName);
		if (leakIntensity <= 0) {
			return 0;
		} else {
			List<Integer> newList = new ArrayList<Integer>();
			this.memoryLeak.add(newList);
			for (long i = 0; i < leakIntensity; i++) {
				// this.memoryLeak.add(rnd.nextInt());
				newList.add(rnd.nextInt());
			}
		}
		return leakIntensity;
	}

	/** To test */
	public static void main(String[] args) {
		final ComplexityService complexityService = new ComplexityService();
		for (int i = 0; i < 50; i++) {
			final long complexity = i * 1000;
			complexityService.setComplexity("TestMethod", complexity);
			long before = System.currentTimeMillis();
			complexityService.compute("TestMethod");
			long after = System.currentTimeMillis();
			System.out.println(complexity + "=" + (after - before));
		}
	}

	public void delay(final String signatureToLongString) {
		try {
			if (this.delay.containsKey(signatureToLongString)) {
				long delayTime = delay.get(signatureToLongString);
				Thread.sleep(delayTime);
			} else {
			    System.out.println("Ignoring " + signatureToLongString);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
