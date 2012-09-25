/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2005, Refractions Research Inc.
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
package net.refractions.udig.legend.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.styling.Rule;

/**
 * Legend row entry.
 * 
 * @author Emily Gouge
 * 
 */
public class LegendEntry {

	private String text;
	private ImageDescriptor icon;
	private Rule rule;

	/**
	 * Creates a new legend entry based on a rule.
	 * 
	 * @param rule
	 */
	public LegendEntry(Rule rule) {
		this.rule = rule;
		this.text = null;
		this.icon = null;
	}

	/**
	 * Creates a new legend entry with the given text
	 * 
	 * @param text
	 */
	public LegendEntry(String text) {
		this.text = text;
		this.icon = null;
		this.rule = null;
	}

	/**
	 * Creates a new legend entry with the given text and icon
	 * 
	 * @param text
	 * @param icon
	 */
	public LegendEntry(String text, ImageDescriptor icon) {
		this.text = text;
		this.icon = icon;
		this.rule = null;
	}

	/**
	 * Gets the legend entry text.
	 * <p>
	 * If the text has not been set then it looks for the text associated with
	 * the rule. Otherwise it returns an empty string.
	 * </p>
	 * 
	 * @return
	 */
	public String getText() {
		if (this.text != null) {
			return this.text;
		} else if (rule != null) {
			return getText(rule);
		}
		return ""; //$NON-NLS-1$ 
	}
	
	/**
	 * Sets the rule associated with this legend entry
	 * @param r
	 */
	public void setRule(Rule rule){
		this.rule = rule;
	}
	/**
	 * @return the rule associated with the legend entry
	 */
	public Rule getRule(){
		return this.rule;
	}
	
	/**
	 * The icon explicity set by the caller.  Will not
	 * generate an icon from the rule.
	 * @return
	 */
	public ImageDescriptor getIcon(){
		return this.icon;
	}

	/**
	 * Finds the text with the associated rule
	 * @param rule
	 * @return
	 */
	private String getText(Rule rule) {
		String text = ""; //$NON-NLS-1$
		String title = null;
		if (rule.getDescription() != null) {
			if (rule.getDescription().getTitle() != null) {
				title = rule.getDescription().getTitle().toString();
			}
		}
		if (title != null && !"".equals(title)) { //$NON-NLS-1$
			text = title;
		} else if (rule.getName() != null && !"".equals(rule.getName())) { //$NON-NLS-1$
			text = rule.getName();
		} else if (rule.getFilter() != null) {
			text = rule.getFilter().toString();
		}

		if (text.length() > 19) {
			return text.substring(0, 18) + "..."; //$NON-NLS-1$
		} else {
			return text;
		}
	}
}
