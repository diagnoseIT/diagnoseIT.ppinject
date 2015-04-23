package de.uni_stuttgart.iste.ppi.problems;

import java.util.concurrent.atomic.AtomicInteger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;

import de.uni_stuttgart.iste.ppi.Container;
import de.uni_stuttgart.iste.ppi.ExecutionState;
import de.uni_stuttgart.iste.ppi.ProblemConfiguration;
import de.uni_stuttgart.iste.ppi.Scope;

/**
 * This aspect implements the antipattern called The Ramp, introduced by Smith and Williams [2002], by replicating its
 * typical response times. The implementation uses a constant offset {@link Config#offsetMillis} and a factor
 * {@link Config#alpha} to determine the slope of the ramp.
 * To produce more realistic response times, the actual runtime of the target method is measured upon each execution and
 * the measured result is immediately used to influence the added ''Ramp'' time.
 * 
 * @author Philipp Keck (University of Stuttgart)
 */
@Aspect
@DeclarePrecedence("kieker..*,TheRampAspect")
public abstract class TheRampAspect extends Container.BasePerformanceProblem<TheRampAspect.Config, TheRampAspect.State> {

    /**
     * Increases the runtime of the target method by i*alpha*runtime, where i is the successive execution number, alpha
     * is from {@link Config#alpha} and the runtime is the measured runtime of the instrumented target method.
     */
    @Override
    protected Object execute(ProceedingJoinPoint joinPoint, Config configuration, State state) throws Throwable {

        final int executionNumber = state.executionNumber.getAndIncrement();

        // execution of the called method
        Object retval = null;
        Throwable exception = null;
        long startTime = System.nanoTime();
        try {
            retval = joinPoint.proceed();
        } catch (Throwable t) {
            exception = t;
        }
        long endTime = System.nanoTime();

        // Measure duration
        long duration = endTime - startTime;
        if (duration <= 0) {
            duration = 1;
        }

        // Sleep for the Ramp
        long sleepTimeNanos = configuration.offsetMillis*1000000 + (long) (duration * executionNumber * configuration.alpha);
        
        System.out.println("Sleeping " + sleepTimeNanos + " nanos (= "+ sleepTimeNanos/1000000 +" millis)" );
        try {
            Thread.sleep(sleepTimeNanos / 1000000, (int) (sleepTimeNanos % 1000000));
        } catch (Throwable t) {
            // Ignore
        }

        // Replicate original result
        if (exception == null) {
            return retval;
        } else {
            throw exception;
        }

    }

    @Override
    protected State produceInitialState(Config configuration) {
        return new State();
    }

    /**
     * Problem configuration for The Ramp.
     */
    public static class Config extends ProblemConfiguration {
    	private final long offsetMillis;
        private final double alpha;

        /**
         * @param executionScope The scope of the injected performance problem.
         * @param offsetMillis the basic offset in milliseconds
         * @param alpha The slope of the ramp.
         */
        public Config(Scope executionScope, long offsetMillis, double alpha) {
            super(executionScope);
            this.offsetMillis = offsetMillis;
            this.alpha = alpha;
        }
    }

    /**
     * Execution state for The Ramp.
     */
    public static class State extends ExecutionState {
        private final AtomicInteger executionNumber = new AtomicInteger(0);
    }

}
