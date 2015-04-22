package de.uni_stuttgart.iste.ppi;

import de.uni_stuttgart.iste.ppi.Container.BasePerformanceProblem;

/**
 * Execution scopes for performance problems.
 * 
 * @author Philipp Keck (University of Stuttgart)
 */
public enum Scope {

    /**
     * The problem is local to a single method of an object.
     */
    Local,

    /**
     * The problem affects all instrumented methods of an object instance.
     */
    PerInstance,

    /**
     * The problem affects a single method of all instances of its class.
     */
    PerMethod,

    /**
     * The problem affects all instances of a class.
     */
    PerClass,

    /**
     * The problem affects all instrumented methods in all classes, but only when executed within a single thread.
     */
    PerThread,

    /**
     * The problem affects all instrumented methods in all classes.
     */
    Global,

    /**
     * The problem affects all instrumented methods in all classes, but there is no connection between subsequent
     * executions of the problem. Performance problems that operate statelessly should use <tt>null</tt> objects for
     * their state and thus return <tt>null</tt> from
     * {@link BasePerformanceProblem#produceInitialState(ProblemConfiguration)}.
     */
    Stateless,

    /**
     * The problem affects all instrumented method, which are activated by the given {@link ProblemConfiguration}
     * instance.
     */
    PerConfiguration

}
