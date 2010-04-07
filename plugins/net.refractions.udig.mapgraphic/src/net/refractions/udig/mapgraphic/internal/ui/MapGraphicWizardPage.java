/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.mapgraphic.internal.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.ui.workflow.Listener;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.mapgraphic.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class MapGraphicWizardPage extends AbstractUDIGImportPage implements UDIGConnectionPage, Listener {

    public static final String ID = "net.refractions.udig.mapgraphic.wizardPageId"; //$NON-NLS-1$

    public MapGraphicWizardPage() {
        super(Messages.MapGraphic_title); 
    }

    public List<IService> getResources( IProgressMonitor monitor ) throws Exception {
        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
        List<IService> services = factory.createService(MapGraphicService.SERVICE_URL);

        return services;
    }

    public String getId() {
        return ID;
    }

    @Override
    public Collection<IService> getServices() {
        IService service = CatalogPlugin.getDefault().getLocalCatalog().getById(IService.class, MapGraphicService.SERVICE_ID, new NullProgressMonitor());
        return Collections.singleton(service);
    }

    public void createControl( Composite parent ) {
        setControl(new Composite(parent, SWT.NONE));
        IRunnableWithProgress runnable = new IRunnableWithProgress(){

            public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                    InterruptedException {
                getWizard().getWorkflow().next();
            }
            
        };
        try {
            getContainer().run(true, false, runnable);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        } catch (InterruptedException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }
    
    @Override
    public void setState( State state ) {
        super.setState(state);
        state.getWorkflow().addListener(this);
    }
    
    void close() {
        if( getContainer()!=null && getContainer().getShell()!=null && !getContainer().getShell().isDisposed() ){
            getContainer().getShell().close();
        }
    }

    public void backward( State current, State next ) {
        if( current == getState() ){
            current.getWorkflow().previous();
            current.getWorkflow().removeListener(this);
        }
    }

    public void finished( State last ) {
    }

    public void forward( State current, State prev ) {
        if( current == getState() ){
            current.getWorkflow().next();
        }
    }

    public void started( State first ) {
    }

    public void stateFailed( State state ) {
    }

    public void statePassed( State state ) {
    }
}