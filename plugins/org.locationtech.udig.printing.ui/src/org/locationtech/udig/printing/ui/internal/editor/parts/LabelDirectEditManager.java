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

import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.impl.LabelBoxPrinter;
import org.locationtech.udig.printing.ui.internal.editor.figures.BoxFigure;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
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
 * NodeDirectEditManager x = new NodeDirectEditManager( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class LabelDirectEditManager extends DirectEditManager {


    Font scaledFont;
    protected VerifyListener verifyListener;
    protected BoxFigure nodeFigure;
    
    public LabelDirectEditManager( GraphicalEditPart source, Class editorType, CellEditorLocator locator, BoxFigure nodeFigure) {
        super(source, editorType, locator);
        this.nodeFigure = nodeFigure;
    }

    protected void bringDown() {
        Font disposeFont = this.scaledFont;
        this.scaledFont = null;
        super.bringDown();
        if (disposeFont != null) {
            disposeFont.dispose();
        }
    }
    
    protected void unhookListeners() {
        super.unhookListeners();
        Text text = (Text) getCellEditor().getControl();
        text.removeVerifyListener(verifyListener);
        verifyListener = null;
    }
    
    protected void initCellEditor() {
        verifyListener = new VerifyListener(){
            public void verifyText( VerifyEvent event ) {
                
                Text text = (Text) getCellEditor().getControl();
                String oldText = text.getText();
                String leftText = oldText.substring(0, event.start);
                String rightText = oldText.substring(event.end, oldText.length());
                
                GC gc = new GC(text);
                Point size = gc.textExtent(leftText + event.text + rightText);
                gc.dispose();
                
                if (size.x != 0) {
                    size = text.computeSize(size.x, SWT.DEFAULT);
                }else{
                    size.x = 1;
                }
                
                Control control = getCellEditor().getControl();
                control.setSize(size.x, size.y);
            }
        };
        
        Text text = (Text) getCellEditor().getControl();
        text.addVerifyListener(verifyListener);
        text.setText(((LabelBoxPrinter) ((Box)getEditPart().getModel()).getBoxPrinter()).getText());
        getCellEditor().setValue(this.nodeFigure.getText());
        IFigure figure = ((GraphicalEditPart) getEditPart()).getFigure();
        scaledFont = figure.getFont();
        FontData data = scaledFont.getFontData()[0];
        Dimension fontSize = new Dimension(0, data.getHeight());
        data.setHeight(fontSize.height);
        scaledFont = new Font(null, data);
        
        text.setFont(scaledFont);
        text.selectAll();
    }

}
