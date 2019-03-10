/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.feature.editor;


import org.locationtech.udig.project.ui.IUDIGDialogPage;
import org.locationtech.udig.project.ui.tool.IToolContext;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A basic, default example of a Dialog feature Editor.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public class DialogEditor implements IUDIGDialogPage {
	private IToolContext context;

	private PropertySheetPage featureDisplay;

	private PropertySheetEntry entry;

	public DialogEditor() {
		featureDisplay = new PropertySheetPage();
		entry = new PropertySheetEntry();
		featureDisplay.setRootEntry(entry);
	}
	/**
	 * @see org.locationtech.udig.project.ui.IUDIGView#setContext()
	 */
	public void setContext(IToolContext context) {
		this.context = context;
	}

	/**
	 * @see org.locationtech.udig.project.ui.IUDIGView#getContext()
	 */
	public IToolContext getContext() {
		return context;
	}

	public void createControl(Composite parent) {
		featureDisplay.createControl(parent);	
	}

	public Point getPreferredSize() {
		return new Point(400, 600);
	}

	public Control getControl() {
		return featureDisplay.getControl();
	}

	public void setFeature(SimpleFeature feature) {
		entry.setValues(new Object[]{feature});
	}

}
