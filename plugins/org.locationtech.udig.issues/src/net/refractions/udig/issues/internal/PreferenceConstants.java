/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.issues.internal;

import net.refractions.udig.issues.IIssuesContentProvider;
import net.refractions.udig.issues.IIssuesExpansionProvider;
import net.refractions.udig.issues.IIssuesLabelProvider;
import net.refractions.udig.issues.IIssuesManager;
import net.refractions.udig.issues.IIssuesViewSorter;
import net.refractions.udig.issues.IssueConfiguration;

/**
 * Lists the constants used for keys and values in the preferences.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface PreferenceConstants {

    // keys
    /**
     * Key for which issues list is used
     * @see IIssuesManager#setIssuesList(net.refractions.udig.issues.IIssuesList)
     * @see IIssuesManager#getIssuesList()
     */
    static final String KEY_ACTIVE_LIST = "enabledIssuesList"; //$NON-NLS-1$
    /**
     * Key for preference controlling the active issues view sorter
     * @see IIssuesViewSorter
     * @see IssueConfiguration#setIssuesViewSorter(IIssuesViewSorter)
     */
    static final String KEY_VIEW_SORTER = "KEY_VIEW_SORTER"; //$NON-NLS-1$
    /**
     * Key for preference controlling the active issues view content provider
     * @see IIssuesContentProvider
     * @see IssueConfiguration#setContentProvider(IIssuesContentProvider)
     */
    static final String KEY_VIEW_CONTENT_PROVIDER = "KEY_VIEW_CONTENT_PROVIDER"; //$NON-NLS-1$
    /**
     * Key for preference controlling the active issues view expansion provider
     * @see IIssuesExpansionProvider
     * @see IssueConfiguration#setExpansionProvider(IIssuesExpansionProvider)
     */
    static final String KEY_VIEW_EXPANSION_PROVIDER = "KEY_VIEW_EXPANSION_PROVIDER"; //$NON-NLS-1$
    /**
     * Key for preference controlling the active issues view label provider
     * @see IIssuesLabelProvider
     * @see IssueConfiguration#setLabelProvider(IIssuesLabelProvider)
     */
    static final String KEY_VIEW_LABEL_PROVIDER = "KEY_VIEW_LABEL_PROVIDER"; //$NON-NLS-1$
    
    //Standard values
    /**
     * default value for {@link #KEY_ACTIVE_LIST}
     */
    static final String VALUE_MEMORY_LIST = "net.refractions.udig.issues.memory"; //$NON-NLS-1$
}
