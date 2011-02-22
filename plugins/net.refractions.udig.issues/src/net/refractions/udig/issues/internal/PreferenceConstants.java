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
