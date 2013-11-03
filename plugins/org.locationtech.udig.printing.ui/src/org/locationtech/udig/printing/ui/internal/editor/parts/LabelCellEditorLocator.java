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
package org.locationtech.udig.printing.ui.internal.editor.parts;

import org.locationtech.udig.printing.ui.internal.editor.figures.BoxFigure;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p><p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p><p>
 * Example Use:<pre><code>
 * NodeCellEditorLocator x = new NodeCellEditorLocator( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class LabelCellEditorLocator implements CellEditorLocator {

    private BoxFigure nodeFigure;
    
    public LabelCellEditorLocator(BoxFigure nodeFigure) {
        this.nodeFigure = nodeFigure;
    }
    
    public void relocate(CellEditor celleditor) {
        Text text = (Text) celleditor.getControl();
        Point pref = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        Rectangle rect = this.nodeFigure.getTextBounds();
        text.setBounds(rect.x -1, rect.y -1, pref.x +1, pref.y+1);
    }
}
