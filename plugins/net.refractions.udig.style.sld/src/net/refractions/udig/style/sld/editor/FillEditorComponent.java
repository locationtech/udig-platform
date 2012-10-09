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
package net.refractions.udig.style.sld.editor;

import net.refractions.udig.filter.ComboExpressionViewer;
import net.refractions.udig.filter.ExpressionViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class FillEditorComponent {
    private ComboExpressionViewer fillTypeViewer;
    private ComboExpressionViewer colourViewer;
    private ComboExpressionViewer opacityViewer;
    
    public void createControl(Composite parent) {
        parent.setLayout(new GridLayout(2, false));
        
        Label label = new Label(parent, SWT.NONE);
        label.setText("Fill Type");
        label.setToolTipText("Select the type of fill");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
        fillTypeViewer = new ComboExpressionViewer(parent, SWT.SINGLE);
        fillTypeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        fillTypeViewer.getControl().setToolTipText("Select the type of fill");
        
        label = new Label(parent, SWT.NONE);
        label.setText("Colour");
        label.setToolTipText("Select fill colour");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
        colourViewer = new ComboExpressionViewer(parent, SWT.SINGLE);
        colourViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        label = new Label(parent, SWT.NONE);
        label.setText("Opacity");
        label.setToolTipText("Select the opacity");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
        opacityViewer = new ComboExpressionViewer(parent, SWT.SINGLE);
        opacityViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            
        
        
    }

}
