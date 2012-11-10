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
package net.refractions.udig.tutorials.genericprojectelement;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * This tells the framework how to display the object in JFace viewers
 * 
 * @author jesse
 * @since 1.1.0
 */
public class MyProjectElementLabelProvider extends LabelProvider implements IColorProvider {

    @Override
    public String getText( Object element ) {
        return ((MyProjectElement)element).getLabel();
    }

    public Color getBackground( Object element ) {
        if( ((MyProjectElement)element).getLabel()==null){
            return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        }
        return null;
    }

    public Color getForeground( Object element ) {
        return null;
    }

}
