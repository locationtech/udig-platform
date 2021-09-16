/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2007 IBM Corporation and others
 * ------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 * --------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.locationtech.udig.feature.panel;

import java.text.MessageFormat;

import org.locationtech.udig.project.ui.feature.FeaturePanelEntry;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.tabbed.ITabItem;

/**
 * Represents a feature panel as a tab descriptor.
 */
public class FeaturePanelTabDescriptor implements ITabItem {
    /**
     * If afterTab is not specified in the descriptor, we default to be the top
     * tab.
     */
    public static final String TOP = "top";

	private static final String TAB_ERROR = "Tab in {0} must provide an id, label and category.";

	private String id;

	private String label;

	private Image image;

	private boolean selected;

	private boolean indented;

	private String category;

	private String afterTab;

	FeaturePanelEntry entry;

	/**
	 * Constructor for TabDescriptor.
	 *
	 * @param configurationElement
	 *            the configuration element for the tab descriptor.
	 */
	public FeaturePanelTabDescriptor(FeaturePanelEntry entry) {
		if (entry == null) {
		    throw new NullPointerException("Feature Panel Entry required");
		}
		id = entry.getId();
		label = entry.getName();
		image = entry.getImage();
		indented = entry.isIndented();
		category = entry.getCategory();
		afterTab = entry.getAfterPanel();
		if (id == null || label == null || category == null) {
			// the tab id, label and category are mandatory - log error
		    String message = MessageFormat.format(TAB_ERROR,
	                new Object[] { entry.getId() });
		    entry.log( message, null );
		}
		this.entry = entry;
		selected = false;
	}

	public FeaturePanelEntry getEntry() {
        return entry;
    }
	/**
	 * Get the unique identifier for the tab.
	 *
	 * @return the unique identifier for the tab.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the text label for the tab.
	 *
	 * @return the text label for the tab.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Get the identifier of the tab after which this tab should be displayed.
	 * When two or more tabs belong to the same category, they are sorted by the
	 * after tab values.
	 *
	 * @return the identifier of the tab.
	 */
	public String getAfterTab() {
		if (afterTab == null) {
			return TOP;
		}
		return afterTab;
	}

	/**
	 * Get the category this tab belongs to.
	 *
	 * @return Get the category this tab belongs to.
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getId();
	}

	/**
	 * Set the image for the tab.
	 *
	 * @param image
	 *            the image for the tab.
	 */
	protected void setImage(Image image) {
		this.image = image;
	}

	/**
	 * Set the indicator to determine if the tab should be displayed as
	 * indented.
	 *
	 * @param indented
	 *            <code>true</code> if the tab should be displayed as
	 *            indented.
	 */
	protected void setIndented(boolean indented) {
		this.indented = indented;
	}

	/**
	 * Set the indicator to determine if the tab should be the selected tab in
	 * the list.
	 *
	 * @param selected
	 *            <code>true</code> if the tab should be the selected tab in
	 *            the list.
	 */
	protected void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Set the text label for the tab.
	 *
	 * @param label
	 *            the text label for the tab.
	 */
	protected void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Get the image for the tab.
	 *
	 * @return the image for the tab.
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Determine if the tab is selected.
	 *
	 * @return <code>true</code> if the tab is selected.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Determine if the tab should be displayed as indented.
	 *
	 * @return <code>true</code> if the tab should be displayed as indented.
	 */
	public boolean isIndented() {
		return indented;
	}

	/**
	 * Get the text label for the tab.
	 *
	 * @return the text label for the tab.
	 */
	public String getText() {
		return label;
	}
}
