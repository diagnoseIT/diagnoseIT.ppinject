package de.uni_stuttgart.iste.ppi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kieker.monitoring.core.signaturePattern.InvalidPatternException;
import de.uni_stuttgart.iste.ppi.Container.BasePerformanceProblem;
import de.uni_stuttgart.iste.ppi.problems.OneLaneBridgeAspect;
import de.uni_stuttgart.iste.ppi.problems.TheRampAspect;

/**
 * The performance problem injection service manages all performance problems that are injected into the application.
 * Note that the implementation should and could easily be changed to retrieve the configuration through JMX or from a
 * file.
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
     * Singleton pattern private constructor.
     */
    private InjectionService() {
        try {
            addTestData();
        } catch (InvalidPatternException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new configuration entry. By keeping the reference to the <tt>config</tt>, the caller can later on
     * (de)activate and re-configure the performance problem.
     * 
     * @param signaturePattern A pattern matching the method/pointcut signatures, to which the configuration shall be
     *            applied (see Kieker documentation for details).
     * @param problemType The type of the performance problem to be injected.
     * @param config The configuration for the performance problem.
     * @throws InvalidPatternException If the pattern is syntactically wrong.
     */
    public <C extends ProblemConfiguration, P extends Container.BasePerformanceProblem<C, ?>> void configure(String signaturePattern, Class<P> problemType, C config) throws InvalidPatternException {
        @SuppressWarnings("unchecked")
        SignatureConfigurationStore<C> configurationStore = (SignatureConfigurationStore<C>) configuration.get(problemType);
        if (configurationStore == null) {
            configurationStore = new SignatureConfigurationStore<C>();
            configuration.put(problemType, configurationStore);
        }
        configurationStore.addPattern(signaturePattern, config);
    }

    /**
     * Creates sample data.
     * 
     * @throws InvalidPatternException If a pattern is syntactically wrong.
     */
    private void addTestData() throws InvalidPatternException {
        // TODO This would normally be read from a file or a JMX interface
        System.out.println("Initializing Test data");
        configure("*", TheRampAspect.class, new TheRampAspect.Config(Scope.Global, 1000, 0.01));
        configure("*", OneLaneBridgeAspect.class, new OneLaneBridgeAspect.Config(Scope.Global, 2));
    }

    /**
     * Retrieves the configuration store for a performance problem type.
     * 
     * @param problemType The type of a performance problem.
     * @return All the configuration for the problem type.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    <C extends ProblemConfiguration> SignatureConfigurationStore<C> getConfiguration(Class<? extends Container.BasePerformanceProblem<C, ?>> problemType) {

        // This is bad and should be refactored to some cleverer way of getting the class for which we have a
        // configuration
        SignatureConfigurationStore<C> result = (SignatureConfigurationStore<C>) configuration.get(problemType);
        while (result == null) {
            problemType = (Class<? extends BasePerformanceProblem<C, ?>>) problemType.getSuperclass();
            // And this typecasting is for Java 6 only, which has some generics bugs ...
            if ((Class) problemType == (Class) Container.BasePerformanceProblem.class) {
                return null;
            }
            result = (SignatureConfigurationStore<C>) configuration.get(problemType);
        }
        return result;
    }

}
