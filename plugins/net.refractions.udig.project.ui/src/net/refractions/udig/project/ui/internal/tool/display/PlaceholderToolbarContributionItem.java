/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal.tool.display;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;

public class PlaceholderToolbarContributionItem implements IContributionItem {

    /**
     * The identifier for the replaced contribution item.
     */
    private final String id;

    /**
     * The height of the SWT widget corresponding to the replaced contribution
     * item.
     */
    private final int storedHeight;

    /**
     * The minimum number of items to display on the replaced contribution
     * item.
     */
    private final int storedMinimumItems;

    /**
     * Whether the replaced contribution item would display chevrons.
     */
    private final boolean storedUseChevron;

    /**
     * The width of the SWT widget corresponding to the replaced contribution
     * item.
     */
    private final int storedWidth;

    /**
     * Constructs a new instance of <code>PlaceholderContributionItem</code>
     * from the item it is intended to replace.
     * 
     * @param item
     *            The item to be replaced; must not be <code>null</code>.
     */
    public PlaceholderToolbarContributionItem(String id) {
        this.id = id;
        storedHeight = -1;
        storedWidth = -1;
        storedMinimumItems = 0;
        storedUseChevron = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#dispose()
     */
    public void dispose() {
        // Do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.Composite)
     */
    public void fill(Composite parent) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.CoolBar,
     *      int)
     */
    public void fill(CoolBar parent, int index) {
        throw new UnsupportedOperationException();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.Menu,
     *      int)
     */
    public void fill(Menu parent, int index) {
        throw new UnsupportedOperationException();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.ToolBar,
     *      int)
     */
    public void fill(ToolBar parent, int index) {
        throw new UnsupportedOperationException();

    }

    /**
     * The height of the replaced contribution item.
     * 
     * @return The height.
     */
    int getHeight() {
        return storedHeight;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#getId()
     */
    public String getId() {
        return id;
    }

    /**
     * The width of the replaced contribution item.
     * 
     * @return The width.
     */
    int getWidth() {
        return storedWidth;
    }
    
    /**
     * Returns the minimum number of tool items to show in the cool item.
     * 
     * @return the minimum number of tool items to show, or <code>SHOW_ALL_ITEMS</code>
     *         if a value was not set
     * @see #setMinimumItemsToShow(int)
     * @since 3.2
     */
    int getMinimumItemsToShow() {
        return storedMinimumItems;
    }
    
    /**
     * Returns whether chevron support is enabled.
     * 
     * @return <code>true</code> if chevron support is enabled, <code>false</code>
     *         otherwise
     * @since 3.2
     */
    boolean getUseChevron() {
        return storedUseChevron;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#isDirty()
     */
    public boolean isDirty() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#isDynamic()
     */
    public boolean isDynamic() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#isEnabled()
     */
    public boolean isEnabled() {
        // XXX Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#isGroupMarker()
     */
    public boolean isGroupMarker() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#isSeparator()
     */
    public boolean isSeparator() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#isVisible()
     */
    public boolean isVisible() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#saveWidgetState()
     */
    public void saveWidgetState() {
        // Do nothing.

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#setParent(org.eclipse.jface.action.IContributionManager)
     */
    public void setParent(IContributionManager parent) {
        // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        // Do nothing.
    }

    /**
     * Displays a string representation of this contribution item, which is
     * really just a function of its identifier.
     */
    public String toString() {
        return "PlaceholderContributionItem(" + id + ")"; //$NON-NLS-1$//$NON-NLS-2$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#update()
     */
    public void update() {
        update(null);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IContributionItem#update(java.lang.String)
     */
    public void update(String identifier) {
        // Do nothing
    }
}
