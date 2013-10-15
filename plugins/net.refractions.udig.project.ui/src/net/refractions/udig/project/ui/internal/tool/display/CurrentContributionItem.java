/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.project.ui.internal.tool.display;

import org.eclipse.jface.action.ContributionItem;

/**
 * Contribution item used to work with modal tools (thus having the concept of a
 * "current" contribution item).
 * <p>
 * The real strength of this class is the ability to handle enablement via the setEnabled method;
 * this allows an external party such as ToolManager to control what is shown.
 * @since 1.1.0
 * @version 1.3.0
 */
public abstract class CurrentContributionItem extends ContributionItem {
	
	/**
	 * Enablement of the contribution item.
	 */
    protected boolean enabled = true;

    /**
     * @see org.eclipse.jface.action.ContributionItem#dispose()
     */
    public void dispose() {
        super.dispose();
    }

    /**
     * Tndicates whether the widget should appear pressed.
     * 
     * @param checked indicates whether the widget should appear pressed.
     */
    public abstract void setSelection( boolean checked, ModalItem proxy );
    
    /**
     * True of the item is selected.
     * 
     * @return true if the item is considered checked (ie current)
     */
    protected abstract boolean isChecked();

    /**
     * Indicates if the item is disposed.
     */
    public abstract boolean isDisposed();
    
    /**
     *  (non-Javadoc)
     * @see org.eclipse.jface.action.ContributionItem#isEnabled()
     */
    @Override
	public boolean isEnabled() {
    	return enabled;
	}

	/**
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
        this.enabled  = enabled;
    }
}