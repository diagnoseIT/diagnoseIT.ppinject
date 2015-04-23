package de.uni_stuttgart.iste.ppi.problems;

import java.util.concurrent.Semaphore;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;

import de.uni_stuttgart.iste.ppi.Container;
import de.uni_stuttgart.iste.ppi.ExecutionState;
import de.uni_stuttgart.iste.ppi.ProblemConfiguration;
import de.uni_stuttgart.iste.ppi.Scope;

/**
 * This aspect implements the antipattern One Lane Bridge, introduced by Smith and Williams [2000], by limiting the
 * number of threads that can concurrently execute the target method to a constant number.
 * 
 * @author Philipp Keck (University of Stuttgart)
 */
@Aspect
@DeclarePrecedence("kieker..*,OneLaneBridgeAspect")
public abstract class OneLaneBridgeAspect extends Container.BasePerformanceProblem<OneLaneBridgeAspect.Config, OneLaneBridgeAspect.State> {

    /**
     * Lets only {@link Config#numberOfLanes} threads pass this point.
     */
    @Override
    protected Object execute(ProceedingJoinPoint joinPoint, Config configuration, State state) throws Throwable {
    	System.out.println("One lane bridge, available permits: " + state.semaphore.availablePermits());
    	System.out.println("One lane bridge, queue length (estimate): " + state.semaphore.getQueueLength());
        state.semaphore.acquire();
        try {
            return joinPoint.proceed();
        } finally {
            state.semaphore.release();
        }
    }

    @Override
    protected State produceInitialState(Config configuration) {
        return new State(configuration.numberOfLanes);
    }

    /**
     * The configuration for the One Lane Bridge.
     */
    public static class Config extends ProblemConfiguration {
        private final int numberOfLanes;

        /**
         * @param executionScope The scope of the injected performance problem.
         * @param numberOfLanes The number of threads, which can execute simultaneously.
         */
        public Config(Scope executionScope, int numberOfLanes) {
            super(executionScope);
            this.numberOfLanes = numberOfLanes;
        }
    }

    /**
     * The execution state of the One Lane Bridge.
     */
    public static class State extends ExecutionState {
        private final Semaphore semaphore;

        /**
         * @param numberOfLanes The number of threads, which can execute simultaneously.
         */
        public State(int numberOfLanes) {
            semaphore = new Semaphore(numberOfLanes, true);
        }
    }

}
