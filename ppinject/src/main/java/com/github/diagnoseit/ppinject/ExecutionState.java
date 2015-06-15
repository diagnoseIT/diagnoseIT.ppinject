package com.github.diagnoseit.ppinject;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Holds the execution state of a specific performance problem within a particular scope instance. Usually, performance
 * problem implementations will extend this class to store more information, but it may also be used "as is" to merely
 * indicate that the performance problem is already active within the respective scope.
 * 
 * @author Philipp Keck (University of Stuttgart)
 */
public class ExecutionState {
	private static AtomicLong idCounter = new AtomicLong(0);
    private final long id = idCounter.getAndIncrement();
    private boolean active = true;

    public long getId() {
    	return this.id;
    }
    
    @Override
    public String toString() {
    	return this.id + ", " + this.active;
    }
    
    public boolean isActive() {
    	return this.active;
    }
    
    public void deactivate() {
    	this.active = false;
    }
}
