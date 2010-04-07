/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.ui.workflow;

import java.io.IOException;

import net.refractions.udig.core.Pair;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A state in a workflow.  Provides behaviour and logic for the state.
 * <p>
 * A WorkflowWizardPage delegates workflow decisions to a State; we can
 * actually go through the State's without user interaction during a
 * DnD operation; and only pop open the correct WizardPage when 
 * interaction is required.
 * 
 * @author jesse
 * @since 1.1.0
 */
public abstract class State {

    /** previous state * */
    State previous;

    /** the workflow * */
    Workflow workflow;

    /**
     * @param workflow The workflow containing the state.
     */
    public void setWorkflow( Workflow workflow ) {
        this.workflow = workflow;
    }

    public abstract String getName();

    /**
     * @return the worklow containing all the states.
     */
    public Workflow getWorkflow() {
        return workflow;
    }

    /**
     * Sets the previous state. The first method in the lifecycle of the state which is called
     * by the data workflow to track the states that have been completed. Should not be called
     * by client code.
     * 
     * @param previous The previous state.
     */
    public void setPrevious( State previous ) {
        this.previous = previous;
    }

    /**
     * Returns the previous state.
     * 
     * @return The state previous to this state, or null if no such state exisits.
     */
    public State getPreviousState() {
        return previous;
    }

    /**
     * Initialize the state. This is the second method in the lifecycle of the state. It is
     * called after #setPrevious(). If the state needs to "seed" itself with any context, that
     * should occur here.
     */
    public void init( IProgressMonitor monitor ) throws IOException {
        // do nothing
    }

    /**
     * Attempts to predict whether or not this page can be ran successfully.  This method must 
     * be very efficient and to this end it may return an incorrect result.    
     *
     * @return a pair where the boolean is true if it is likely that the state can be run correct and the State 
     * is the next state or null if the next state is not known of if this state cannot be ran completely.
     */
    public Pair<Boolean, State> dryRun(){
        return new Pair<Boolean, State>(true, null);
    }
    
    /**
     * Performs any "hard" work. This method is provided is provided for states which have to
     * block to get work done. For instance, making a connection to a remote service. This
     * method returns a boolean which signals wether the state was able to get the work done.
     * 
     * @param monitor A progress monitor.
     * @return True if the state was able to complete its job, otherwise false.
     * @throws IOException
     */
    public boolean run( IProgressMonitor monitor ) throws IOException {

        return true;
    }

    /**
     * Determines if the state can dynamically create a new state to be the next active state of
     * the workflow. Note, in most cases this is equivalent to <code>next() != null</code>.
     * However some implementations require that next() be called only once, as it is a life-cycle
     * event.
     * 
     * @return true if the state can create a new state, otherwise false.
     */
    public boolean hasNext() {
        return false;
    }

    /**
     * The final method in the lifecycle of the state. This method is used for states to
     * dynamically link to each other. This method returns null to indicate no state.
     * 
     * @return A new state which is to become the next active state, otherwise null.
     */
    public State next() {
        return null;
    }
    
    @Override
    public String toString() {
    	StringBuffer text = new StringBuffer();
    	text.append("State:");
    	text.append( getName() );
    	return text.toString();
    }
}