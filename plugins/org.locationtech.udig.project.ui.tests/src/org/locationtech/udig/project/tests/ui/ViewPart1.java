/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * A test view.  Momento makes a difference.
 * 
 * @author jones
 * @since 1.0.0
 */
public class ViewPart1 extends ViewPart {
    public final static String ID="org.locationtech.udig.project.tests.ui.view1"; //$NON-NLS-1$
    public IMemento memento;
    /**
     * 
     */
    public ViewPart1() {
        super();
    }
    
    @Override
    public void init( IViewSite site, IMemento memento ) throws PartInitException {
        super.init(site, memento);
        this.memento=memento;
    }

    @Override
    public void createPartControl( Composite parent ) {
    }

    @Override
    public void setFocus() {
    }

}
