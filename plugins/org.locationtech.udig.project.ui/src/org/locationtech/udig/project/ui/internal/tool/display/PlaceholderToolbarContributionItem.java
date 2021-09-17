/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.tool.display;

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

    public void dispose() {
        // Do nothing
    }

    public void fill(Composite parent) {
        throw new UnsupportedOperationException();
    }

    public void fill(CoolBar parent, int index) {
        throw new UnsupportedOperationException();
    }

    public void fill(Menu parent, int index) {
        throw new UnsupportedOperationException();
    }

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

    public boolean isDirty() {
        return false;
    }

    public boolean isDynamic() {
        return false;
    }

    public boolean isEnabled() {
        return false;
    }

    public boolean isGroupMarker() {
        return false;
    }

    public boolean isSeparator() {
        return false;
    }

    public boolean isVisible() {
        return false;
    }

    public void saveWidgetState() {
        // Do nothing.
    }

    public void setParent(IContributionManager parent) {
        // Do nothing
    }

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

    public void update() {
        update(null);
    }

    public void update(String identifier) {
        // Do nothing
    }
}
