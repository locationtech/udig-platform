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
package org.locationtech.udig.style.sld.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.IStyleConfigurator;

/**
 * Adapter used to place a StyleConfigurator into the StyleEditor.
 */
public class StyleEditorPageAdapter extends StyleEditorPage {
    IStyleConfigurator configurator;

    public static final String XPID = "org.locationtech.udig.style.styleConfigurator"; //$NON-NLS-1$
    
    public StyleEditorPageAdapter(IStyleConfigurator configurator) {
        this.configurator = configurator;
    }
    
    @Override
    public void createPageContent( Composite parent ) {
        Composite configHolder = new Composite(parent, SWT.NONE);
        RowLayout layout = new RowLayout(SWT.HORIZONTAL);
        layout.pack = false;
        layout.wrap = true;
        layout.type = SWT.HORIZONTAL;
        layout.fill = true;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.spacing = 0;
        configHolder.setLayout(layout);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 800;
        configHolder.setLayoutData(gd);

        configurator.createControl(configHolder);
        
        Layer selectedLayer = getContainer().getSelectedLayer();
        if (configurator.canStyle(selectedLayer)) {
            
            configurator.setAction(getContainer().getApplyAction());
            configurator.focus(selectedLayer);
        }
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public boolean performCancel() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    public void styleChanged(Object source ){
        //do nothing
    }
    
    public void gotFocus() {
        refresh();
    };

    public boolean okToLeave() {
        return true;
    }

    public boolean performOk() {
        return false;
    }

    public boolean performApply() {
        configurator.preApply();
        return true;
    }

    public void refresh() {
        configurator.setAction(getContainer().getApplyAction());
        configurator.focus(getSelectedLayer());
    }

}
