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
package net.refractions.udig.tutorials.featureeditor;

import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.opengis.feature.simple.SimpleFeature;

public class CountryView extends ViewPart implements IUDIGView {

    // CountryPanel panel = new CountryPanel();
    // CountryPanelForm panel = new CountryPanelForm();
    CountryPanelFormCellEditor panel = new CountryPanelFormCellEditor();
    FormToolkit toolkit;
    private IToolContext context;

    public void createPartControl( Composite parent ) {
        // panel.createControl(parent);
        this.toolkit = new FormToolkit(parent.getDisplay());
        panel.createControl(parent, toolkit);
    }

    @Override
    public void init( IViewSite site ) throws PartInitException {
        super.init(site);
    }

    public void setFocus() {
        panel.setFocus();
    }

    public void setContext( IToolContext newContext ) {
        context = newContext;
    }

    public IToolContext getContext() {
        return context;
    }

    public void editFeatureChanged( SimpleFeature feature ) {
        panel.setEditFeature(feature, context);
    }

}
