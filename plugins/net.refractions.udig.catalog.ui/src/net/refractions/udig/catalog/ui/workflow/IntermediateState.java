/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.ui.workflow;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A state that tracks intermediate pages.
 * <p>
 * They have no purpose beyond matching the state of a wizard page.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class IntermediateState extends State {

    private int numberOfPages;
    private int currentPage;
    private State endState;

    /**
     * New instance
     * 
     * @param numberOfPages the number of pages that this state must track.  The last page will be the endState 
     * 
     * @param endState the state that must be satisfied for all the pages to be satisfied.
     */
    public IntermediateState( int index, int numberOfPages, State endState ) {
        this.numberOfPages = numberOfPages;
        this.currentPage = index;
        this.endState = endState;
    }

    @Override
    public boolean hasNext() {
        return true;
    }
    
    @Override
    public State next() {
        if( currentPage<numberOfPages-2 ){
            return new IntermediateState(currentPage+1, numberOfPages, endState);
        }
        return endState;
    }
    
    @Override
    public boolean run( IProgressMonitor monitor ) throws IOException {
        return true;
    }

    @Override
    public String getName() {
        return "Intermediate Page";
    }

    public State getEndState() {
        return endState;
    }

    public int getIndex() {
        return currentPage;
    }
    
}
