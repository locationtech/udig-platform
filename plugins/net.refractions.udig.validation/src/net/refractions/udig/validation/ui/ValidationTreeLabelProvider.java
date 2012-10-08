/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.validation.ui;

import net.refractions.udig.validation.DTOUtils;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.geotools.validation.dto.PlugInDTO;
import org.geotools.validation.dto.TestDTO;

public class ValidationTreeLabelProvider extends LabelProvider implements IColorProvider {

    public ValidationTreeLabelProvider() {
    }

    public Image getImage( Object element ) {
        return null;
    }

    public String getText( Object element ) {
    	if (element instanceof PlugInDTO) {
        	PlugInDTO plugin = (PlugInDTO) element;
        	return plugin.getName();
        } else if (element instanceof TestDTO) {
        	TestDTO test = (TestDTO) element;
        	return test.getName();
        }
        return super.getText(element); //unknown type
    }

    public void addListener( ILabelProviderListener listener ) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty( Object element, String property ) {
        return false;
    }

    public void removeListener( ILabelProviderListener listener ) {
    }

	public Color getForeground(Object element) {
		//check element to determine if it has bad args
		if (element instanceof TestDTO) {
			Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			if (!(DTOUtils.noNullArguments((TestDTO) element))) {
				return red;
			}
		}
		return null;
	}

	public Color getBackground(Object element) {
		return null;
	}
}
