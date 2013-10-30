/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.issues;

import java.io.IOException;

import net.refractions.udig.issues.internal.IssuesManager;
import net.refractions.udig.issues.listeners.IIssuesListListener;
import net.refractions.udig.issues.listeners.IIssuesManagerListener;

import org.eclipse.core.runtime.IProgressMonitor;


/**
 * Provides access to the issues list and allows listeners to be added to it.
 * 
 * @author jones
 * @since 1.0.0
 */
public interface IIssuesManager {
    /**
     * The default issues manager.
     */
    IIssuesManager defaultInstance=new IssuesManager();
    /**
     * Adds a listener to the manager.
     *
     * @param listener listener to add.
     */
    void addListener( IIssuesManagerListener listener);
    /**
     * Remove a listener from the manager.
     *
     * @param listener listener to remove.
     */
    void removeListener( IIssuesManagerListener listener);
    /**
     * Adds a listener to the issues list.  Unlike {@link IIssuesList#addListener(IIssuesListListener)} this method will ensure
     * that the listener is transfered to new lists when {@link #setIssuesList(IIssuesList)} is called.
     * @param listener the listener to add
     */
    public void addIssuesListListener( IIssuesListListener listener );
    /**
     * Removes a listener to the issues list.  
     * <p>
     * <em>WARNING:</em> if {@link #addIssuesListListener(IIssuesListListener)} is used to add a listener
     * then {@link #removeIssuesListListener(IIssuesListListener)} should be used to remove the listener because otherwise
     * the listener will be added to a new list if {@link #setIssuesList(IIssuesList)} is called.
     * 
     * @param listener the listener to remove
     */
    public void removeIssuesListListener( IIssuesListListener listener );
    /**
     * Sets the currently used {@link IssuesList}.  
     *
     * @param newList the new list that will be used. 
     */
    public void setIssuesList( IIssuesList newList );
	/**
	 * Gets the global list of issues.
	 * @return the global list of issues.
	 */
    public IIssuesList getIssuesList();
    /**
     * Saves the issues list if necessary.
     * @param monitor Monitor that reports the save progress.
     * @return returns true if a issue has been changed since last save and needs saving.
     */
    public boolean save(IProgressMonitor monitor) throws IOException;
    /**
     * Returns true if there is one or more dirty issues in the issues list.  If the issues list is not a 
     * {@link IRemoteIssuesList} then this always returns false (since only {@link IRemoteIssuesList} need to
     * be saved).
     *
     * @return true if there is one or more dirty issues in the issues list.
     */
    public boolean isDirty();
}
