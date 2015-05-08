package com.github.diagnoseit.ppinject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.aspectj.lang.ProceedingJoinPoint;

import com.github.diagnoseit.ppinject.Container.BasePerformanceProblem;

/**
 * Instances of this class store the execution state of a performance problem per scope. NOTE: This implementation is
 * not yet thread safe!
 * 
 * @author Philipp Keck (University of Stuttgart)
 *
 * @param <S> The type of execution state.
 */
public final class ExecutionStateStore<S extends ExecutionState> {

    private volatile S globalState;
    private ConcurrentMap<Class<?>, S> perClassState = new ConcurrentHashMap<Class<?>, S>();
    private ConcurrentMap<Object, S> perInstanceState = new ConcurrentHashMap<Object, S>();
    private ConcurrentMap<String, S> perMethodState = new ConcurrentHashMap<String, S>();
    private ConcurrentMap<Object, ConcurrentMap<String, S>> localState = new ConcurrentHashMap<Object, ConcurrentMap<String, S>>();
    private ThreadLocal<S> threadState = new ThreadLocal<S>();
    private ConcurrentMap<ProblemConfiguration, S> perConfigurationState = new ConcurrentHashMap<ProblemConfiguration, S>();

    /**
     * Retrieves the state for a specific scope. The scope is specified in <tt>configuration.</tt>
     * {@link C#getExecutionScope()}.
     * 
     * @param joinPoint The AspectJ join point, identifying the target class, method and instance (a Java object).
     * @param configuration The configuration of the performance problem, for which the state is required.
     * @param problem The instance of the performance problem itself (used to created initial states through
     *            {@link BasePerformanceProblem#produceInitialState(ProblemConfiguration)}, in case a new scope is
     *            opened.
     * @return The state for the specified scope.
     */
    public <C extends ProblemConfiguration> S getState(ProceedingJoinPoint joinPoint, C configuration, Container.BasePerformanceProblem<C, S> problem) {
        switch (configuration.getExecutionScope()) {
        case Stateless:
            return null; // No state saving

        case Global:
            if (globalState == null) {
                globalState = problem.produceInitialState(configuration);
            }
            return globalState;

        case PerClass:
            if (joinPoint.getTarget() == null) {
                throw new RuntimeException("PerClass scope is not allowed on static methods!");
            }
            Class<?> class1 = joinPoint.getTarget().getClass();

            S state1 = perClassState.get(class1);
            if (state1 == null) {
                state1 = problem.produceInitialState(configuration);
                perClassState.putIfAbsent(class1, state1);
            }
            return perClassState.get(class1);

        case PerInstance:
            if (joinPoint.getTarget() == null) {
                throw new RuntimeException("PerInstance scope is not allowed on static methods!");
            }
            Object instance2 = joinPoint.getTarget();

            S state2 = perInstanceState.get(instance2);
            if (state2 == null) {
                state2 = problem.produceInitialState(configuration);
                perInstanceState.putIfAbsent(instance2, state2);
            }
            return perInstanceState.get(instance2);

        case PerMethod:
            String sig3 = joinPoint.getSignature().toLongString();
            S state3 = perMethodState.get(sig3);
            if (state3 == null) {
                state3 = problem.produceInitialState(configuration);
                perMethodState.putIfAbsent(sig3, state3);
            }
            return perMethodState.get(sig3);

        case Local:
            if (joinPoint.getTarget() == null) {
                throw new RuntimeException("PerInstance scope is not allowed on static methods!");
            }
            Object instance4 = joinPoint.getTarget();

            ConcurrentMap<String, S> instanceMethods4 = localState.get(instance4);
            if (instanceMethods4 == null) {
                localState.putIfAbsent(instance4, new ConcurrentHashMap<String, S>());
                instanceMethods4 = localState.get(instance4);
            }

            String sig4 = joinPoint.getSignature().toLongString();
            S state4 = instanceMethods4.get(sig4);
            if (state4 == null) {
                state4 = problem.produceInitialState(configuration);
                instanceMethods4.putIfAbsent(sig4, state4);
            }
            return instanceMethods4.get(sig4);

        case PerThread:
            synchronized (threadState) {
                S state5 = threadState.get();
                if (state5 == null) {
                    state5 = problem.produceInitialState(configuration);
                    threadState.set(state5);
                }
                return state5;
            }

        case PerConfiguration:
            S state5 = perConfigurationState.get(configuration);
            if (state5 == null) {
                state5 = problem.produceInitialState(configuration);
                perConfigurationState.putIfAbsent(configuration, state5);
            }
            return perConfigurationState.get(configuration);

        default:
            throw new RuntimeException("Unsupported scope " + configuration.getExecutionScope());
        }
    }

}
