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
package org.locationtech.udig.project.ui.commands;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * A command that draws the indicated shape onto the viewport in the correct "selection" style.  The default Shape is 
 * a rectangle.
 * 
 * @author Jesse
 */
public class SelectionBoxCommand extends AbstractDrawCommand implements
		IDrawCommand {

	private Shape shape;
	
	
	/* (non-Javadoc)
	 * @see org.locationtech.udig.project.ui.commands.IDrawCommand#getValidArea()
	 */
	public Rectangle getValidArea() {
		return shape.getBounds();
	}

	/* (non-Javadoc)
	 * @see org.locationtech.udig.project.command.Command#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws Exception {
		if( shape!=null ){
			graphics.setColor(getSelectionColor(75));
			graphics.fill(shape);
			graphics.setColor(getSelectionColor2(75));
			graphics.setStroke(ViewportGraphics.LINE_SOLID, 3);
			graphics.draw(shape);
			graphics.setStroke(ViewportGraphics.LINE_SOLID, 1);
			graphics.setColor(getSelectionColor(255));
			graphics.draw(shape);
		}
	}

	private Color getSelectionColor(int alpha) {
		IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
		String name = PreferenceConstants.P_SELECTION_COLOR;
		RGB rgb = PreferenceConverter.getColor(store, name );
		return new Color( rgb.red, rgb.green, rgb.blue, alpha);
	}

	private Color getSelectionColor2(int alpha) {
		IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
		String name = PreferenceConstants.P_SELECTION2_COLOR;
		RGB rgb = PreferenceConverter.getColor(store, name );
		return new Color( rgb.red, rgb.green, rgb.blue, alpha);
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Shape getShape() {
		return shape;
	}

}
