/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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
package net.refractions.udig.tools.options;

import net.refractions.udig.project.ui.tool.options.AbstractToolOptionsContributionItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of 
 * <p>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * @author leviputna
 * @since 1.2.0
 */
public class PanToolOptionsContribution extends AbstractToolOptionsContributionItem {

    @Override
    public void fill(Composite parent) {
        Button fixedScaleButton = new Button(parent, SWT.CHECK);
        fixedScaleButton.setText("Fixed Scale");
        
        Button scrollButton = new Button(parent, SWT.CHECK);
        scrollButton.setText("Scroll");

        fixedScaleButton.pack();
        scrollButton.pack();
    }
    
}
