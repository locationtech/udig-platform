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
package org.locationtech.udig.tutorials.featureeditor;

import org.locationtech.udig.project.ui.IUDIGView;
import org.locationtech.udig.project.ui.tool.IToolContext;

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
