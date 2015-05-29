package com.github.diagnoseit.ppinject;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import kieker.monitoring.core.signaturePattern.InvalidPatternException;

import com.github.diagnoseit.ppinject.Container.BasePerformanceProblem;

/**
 * The performance problem injection service manages all performance problems
 * that are injected into the application. Note that the implementation should
 * and could easily be changed to retrieve the configuration through JMX or from
 * a file.
 * 
 * @author Philipp Keck (University of Stuttgart)
 */
public class InjectionService {

	/**
	 * Singleton pattern instance.
	 */
	private static final InjectionService INSTANCE = new InjectionService();

	/**
	 * Singleton pattern getter.
	 * 
	 * @return The one and only InjectionService instance.
	 */
	public static InjectionService getInstance() {
		return INSTANCE;
	}

	/**
	 * Stores the configurations of all performance problems.
	 */
	private final Map<Class<? extends Container.BasePerformanceProblem<?, ?>>, SignatureConfigurationStore<?>> configuration = new ConcurrentHashMap<Class<? extends Container.BasePerformanceProblem<?, ?>>, SignatureConfigurationStore<?>>();

	/**
	 * Used to store the execution states. Aspect classes may be instantiated
	 * multiple times, so that a static store needs to be used.
	 */
	private static final ConcurrentMap<Class<? extends Container.BasePerformanceProblem<?, ?>>, ExecutionStateStore<?>> STATE_STORE_BY_TYPE = new ConcurrentHashMap<Class<? extends Container.BasePerformanceProblem<?, ?>>, ExecutionStateStore<?>>();

	
	/**
	 * Accessor method for the state store cache.
	 * 
	 * @param problemType
	 *            The type of the performance problem (represented by the
	 *            {@link Class} instance).
	 * @return The {@link ExecutionStateStore} for the problem type, which in
	 *         turn contains the scoped state information.
	 */
	@SuppressWarnings("unchecked")
	public static <S extends ExecutionState> ExecutionStateStore<S> getExecutionStateStore(
			Class<? extends BasePerformanceProblem<?, S>> problemType) {
		ExecutionStateStore<S> store = (ExecutionStateStore<S>) STATE_STORE_BY_TYPE
				.get(problemType);
		if (store == null) {
			store = new ExecutionStateStore<S>();
			STATE_STORE_BY_TYPE.putIfAbsent(problemType, store);
		}
		return (ExecutionStateStore<S>) STATE_STORE_BY_TYPE.get(problemType);
	}

	/**
	 * Singleton pattern private constructor.
	 */
	private InjectionService() {
		System.out.println("### > INJECTION SERVICE instantiated. < ###");

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name;
		try {
			name = new ObjectName("com.github.diagnoseit.ppinject:type=Ppi");
			Ppi mbean = new Ppi();
			mbs.registerMBean(mbean, name);
		} catch (MalformedObjectNameException e) {
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		}

//		try {
//			addTestData();
//		} catch (InvalidPatternException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * Adds a new configuration entry. By keeping the reference to the
	 * <tt>config</tt>, the caller can later on (de)activate and re-configure
	 * the performance problem.
	 * 
	 * @param signaturePattern
	 *            A pattern matching the method/pointcut signatures, to which
	 *            the configuration shall be applied (see Kieker documentation
	 *            for details).
	 * @param problemType
	 *            The type of the performance problem to be injected.
	 * @param config
	 *            The configuration for the performance problem.
	 * @throws InvalidPatternException
	 *             If the pattern is syntactically wrong.
	 */
	public <C extends ProblemConfiguration, P extends Container.BasePerformanceProblem<C, ?>> void configure(
			String signaturePattern, Class<P> problemType, C config)
			throws InvalidPatternException {
		@SuppressWarnings("unchecked")
		SignatureConfigurationStore<C> configurationStore = (SignatureConfigurationStore<C>) configuration
				.get(problemType);
		if (configurationStore == null) {
			configurationStore = new SignatureConfigurationStore<C>();
			configuration.put(problemType, configurationStore);
		}
		configurationStore.addPattern(signaturePattern, config);
	}

	/**
	 * Creates sample data.
	 * 
	 * @throws InvalidPatternException
	 *             If a pattern is syntactically wrong.
	 */
//	private void addTestData() throws InvalidPatternException {
//		// TODO This would normally be read from a file or a JMX interface
//		// System.out.println("Initializing Test data");
//		// configure("*", TheRampAspect.class, new TheRampAspect.Config(
//		// Scope.Global, 1000, 0.25));
//		// configure("*", OneLaneBridgeAspect.class,
//		// new OneLaneBridgeAspect.Config(Scope.Global, 2));
//	}

	/**
	 * Retrieves the configuration store for a performance problem type.
	 * 
	 * @param problemType
	 *            The type of a performance problem.
	 * @return All the configuration for the problem type.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	<C extends ProblemConfiguration> SignatureConfigurationStore<C> getConfiguration(
			Class<? extends Container.BasePerformanceProblem<C, ?>> problemType) {

		// This is bad and should be refactored to some cleverer way of getting
		// the class for which we have a
		// configuration
		SignatureConfigurationStore<C> result = (SignatureConfigurationStore<C>) configuration
				.get(problemType);
		while (result == null) {
			problemType = (Class<? extends BasePerformanceProblem<C, ?>>) problemType
					.getSuperclass();
			// And this typecasting is for Java 6 only, which has some generics
			// bugs ...
			if ((Class) problemType == (Class) Container.BasePerformanceProblem.class) {
				return null;
			}

			result = (SignatureConfigurationStore<C>) configuration
					.get(problemType);
		}
		return result;
	}

	public <C extends ProblemConfiguration, P extends Container.BasePerformanceProblem<C, ?>> void deconfigure(
			String signaturePattern, Class<P> problemType)
			throws InvalidPatternException {
		@SuppressWarnings("unchecked")
		SignatureConfigurationStore<C> configurationStore = (SignatureConfigurationStore<C>) configuration
				.get(problemType);

		if (configurationStore == null) {
			configurationStore = new SignatureConfigurationStore<C>();
			configuration.put(problemType, configurationStore);
		}
		
		//TODO: For now we assume PerMethod scope only
		@SuppressWarnings("unchecked")
		C config = (C) new ProblemConfiguration(Scope.PerMethod);
		config.setActivationStatus(false);
		
		configurationStore.addPattern(signaturePattern, config);
	}

	public <C extends ProblemConfiguration, P extends Container.BasePerformanceProblem<C, ?>> void clearForProblemType(
			Class<P> problemType) {
		configuration.remove(problemType);
	}

	public void clearAll() {
		configuration.clear();
	}
}
