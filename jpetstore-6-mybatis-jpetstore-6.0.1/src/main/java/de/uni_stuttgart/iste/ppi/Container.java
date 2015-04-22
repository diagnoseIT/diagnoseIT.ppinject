package de.uni_stuttgart.iste.ppi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * This looks like a terrible workaround - because it is. The container class is needed because AspectJ will fail on
 * aspects, which are defined by annotations, abstract and genericly typed - unless they are an inner class...
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=298675
 * @author Philipp Keck (University of Stuttgart)
 */
public class Container {

    /**
     * This is the base class for all performance problem implementations. If a performance problem needs additional
     * configuration data, it should inherit {@link ProblemConfiguration} and specify it as the <tt>C</tt> parameter.
     * Similarly, subclasses of {@link ExecutionState} can be specified as <tt>S</tt> to store execution data.<br>
     * The implementation of {@link BasePerformanceProblem} handles all configuration and scope issues, along with
     * {@link InjectionService}. Subclasses will usually be abstract themselves, since they provide a generic
     * implementation of a specific performance problem type. Concrete instances of these aspects are usually generated
     * by the AspectJ (XML) configuration, since their injection depends on the method signatures in the target
     * application.
     * 
     * @author Philipp Keck (University of Stuttgart)
     *
     * @param <C> The configuration type.
     * @param <S> The execution state type.
     */
    @Aspect
    public static abstract class BasePerformanceProblem<C extends ProblemConfiguration, S extends ExecutionState> {

        /**
         * Used to store the execution states. Aspect classes may be instantiated multiple times, so that a static store
         * needs to be used.
         */
        private static final Map<Class<? extends BasePerformanceProblem<?, ?>>, ExecutionStateStore<?>> STATE_STORE_BY_TYPE = new ConcurrentHashMap<Class<? extends BasePerformanceProblem<?, ?>>, ExecutionStateStore<?>>();

        /**
         * Accessor method for the state store cache.
         * 
         * @param problemType The type of the performance problem (represented by the {@link Class} instance).
         * @return The {@link ExecutionStateStore} for the problem type, which in turn contains the scoped state
         *         information.
         */
        @SuppressWarnings("unchecked")
        private static <S extends ExecutionState> ExecutionStateStore<S> getExecutionStateStore(Class<? extends BasePerformanceProblem<?, S>> problemType) {
            ExecutionStateStore<S> store = (ExecutionStateStore<S>) STATE_STORE_BY_TYPE.get(problemType);
            if (store == null) {
                store = new ExecutionStateStore<S>();
                STATE_STORE_BY_TYPE.putIfAbsent(problemType, store);
            }
            return (ExecutionStateStore<S>) STATE_STORE_BY_TYPE.get(problemType);
        }

        /**
         * The configuration store for this problem type. Note that Aspect classes are instantiated many times, which is
         * why the configuration store is shared between all instances by retrieving it from the
         * {@link InjectionService}.
         */
        @SuppressWarnings("unchecked")
        private final SignatureConfigurationStore<C> configurationStore = InjectionService.getInstance().getConfiguration((Class<? extends BasePerformanceProblem<C, S>>) getClass());

        /**
         * Direct reference to the respective execution state store from {@link #STATE_STORE_BY_TYPE} (for quicker
         * access).
         */
        @SuppressWarnings("unchecked")
        private final ExecutionStateStore<S> stateStore = getExecutionStateStore((Class<? extends BasePerformanceProblem<C, S>>) getClass());

        /**
         * This is the target operation. Note that this is a {@link Pointcut}! For your performance problem
         * implementation to be reusable, you probably do not want to implement this method, because it is specified for
         * the concrete aspect in the AspectJ (XML) configuration.
         */
        @Pointcut
        public abstract void targetOperation();

        /**
         * Handles the actual injection of the problem. To facilitate the implementation of performance problems, which
         * is done in {@link #execute(ProceedingJoinPoint, ProblemConfiguration, ExecutionState)}, this method retrieves
         * the appropriate configuration and execution state objects.
         * 
         * @param joinPoint The AspectJ join point.
         * @return The result of the operation.
         * @throws Throwable In case the operation failed.
         */
        @Around("targetOperation()")
        public Object operation(final ProceedingJoinPoint joinPoint) throws Throwable { // NOCS (Throwable)

            C joinPointConfig = configurationStore.getConfiguration(joinPoint.getSignature());
            if (joinPointConfig == null || !joinPointConfig.isActivated()) {
                // Deactivated
                System.out.println("Deactivated for " + joinPoint.getSignature());
                return joinPoint.proceed();
            }

            S state = stateStore.getState(joinPoint, joinPointConfig, this);
            return execute(joinPoint, joinPointConfig, state);
        }

        /**
         * This is where subclasses should implement the actual performance problem. To do so, you will need to call
         * {@link ProceedingJoinPoint#proceed()} (in most cases) and eventually return its result.
         * 
         * @param joinPoint The AspectJ join point.
         * @param configuration The configuration for the performance problem.
         * @param state The state of the performance problem, which depends on the current execution scope.
         * @return The result of the operation.
         * @throws Throwable In case the operation failed.
         */
        protected abstract Object execute(ProceedingJoinPoint joinPoint, C configuration, S state) throws Throwable;

        /**
         * This method is called whenever the performance problem is to be injected in a scope (like a thread) for the
         * first time. A new instance of the execution state should be returned.
         * 
         * @param configuration The configuration, for which the execution state is created.
         * @return A newly created execution state object.
         */
        protected abstract S produceInitialState(C configuration);

    }
}
