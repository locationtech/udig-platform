/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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
package net.refractions.udig.project.ui.tool.options;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

/**
 * This object is used when creating tool options, it provide access to the 
 * preference store so that tool options can update preferences 
 * when changes.
 * 
 * TODO Provide a method to load / and update preferences. 
 * @author leviputna
 * @since 1.2.0
 */
public abstract class AbstractToolOptionsContributionItem extends ContributionItem{
    
    private IPreferenceStore store;
    
    /**
     * The default implementation of this <code>IContributionItem</code>
     * Subclasses must override to provide config to tool options UI.
     */
    abstract public void fill(Composite parent);


}
