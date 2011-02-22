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
package net.refractions.udig.project.tests.ui;

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
    public final static String ID="net.refractions.udig.project.tests.ui.view1"; //$NON-NLS-1$
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
