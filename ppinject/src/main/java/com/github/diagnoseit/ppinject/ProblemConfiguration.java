package com.github.diagnoseit.ppinject;

/**
 * Holds the configuration of a specific performance problem instance. Usually, performance problem implementations will
 * extend this class to store more information, but it may also be used "as is" to merely indicate that the performance
 * problem is already active within the respective scope.
 * 
 * @author Philipp Keck (University of Stuttgart)
 */
public class ProblemConfiguration {

    private final Scope executionScope;
    private boolean activated = true;

    /**
     * Creates a new instance.
     * 
     * @param executionScope The scope of an instance of the problem. Use finer-grained scopes to create more
     *            simultaneous instances of the problem.
     */
    public ProblemConfiguration(Scope executionScope) {
        super();
        this.executionScope = executionScope;
    }

    /**
     * @return The execution scope.
     */
    public Scope getExecutionScope() {
        return executionScope;
    }

    /**
     * @return A boolean indicating if the problem is activated.
     */
    public boolean isActivated() {
        return activated;
    }
    
    /**
     * Sets the activated flag to the given parameter.
     * @param status new state of activated flag
     */
    public void setActivationStatus(boolean status) {
    	this.activated = status;
    }

}
