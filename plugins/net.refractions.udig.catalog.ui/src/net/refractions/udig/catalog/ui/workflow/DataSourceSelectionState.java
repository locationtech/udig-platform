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
package net.refractions.udig.catalog.ui.workflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ui.ConnectionFactoryManager;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;
import net.refractions.udig.catalog.ui.UDIGConnectionFactoryDescriptor;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.core.Pair;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * First state in the data import worklow.
 * <p>
 * This state chooses a data source based on the context of
 * the workflow.
 * 
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 */
public class DataSourceSelectionState extends State {

    /**
     * This chosen import page - there should only be one; or we will need
     * to ask the user to choose.
     */
    UDIGConnectionFactoryDescriptor descriptor;
    
    /**
     * Flag used to check that the service connects; by listing members
     */
    private boolean validateService;

    private ArrayList<UDIGConnectionFactoryDescriptor> shortlist;

    /**
     * Create new Instance
     * @param validateServices indicates whether the service should be probed for its members and metadata.
     */
    public DataSourceSelectionState(boolean validateServices ){
        this.validateService=validateServices;
    }
    
    @Override
    public void init( IProgressMonitor monitor ) throws IOException {
        super.init(monitor);

        descriptor=null;
        // based on context, try to choose a single data source
        Object context = getWorkflow().getContext();
        if (context == null){
            return; // we got a single page
        }
        Collection<UDIGConnectionFactoryDescriptor> descriptors = ConnectionFactoryManager.instance().getConnectionFactoryDescriptors();
        
        // determine if any connection factory can process the context object
        descriptor = null;
        shortlist = new ArrayList<UDIGConnectionFactoryDescriptor>();
        for( UDIGConnectionFactoryDescriptor d : descriptors ) {
            UDIGConnectionFactory factory = d.getConnectionFactory();
            try {
                if (factory.canProcess(context)) {
                    shortlist.add( d );
                }
            } catch (Throwable t) {
                // log and keep going
                CatalogPlugin.trace("Factory "+d.getId()+" unable to handle "+context, t);
                // CatalogPlugin.log(t.getLocalizedMessage(), t);
            }
        }
        // if we already have a descriptor, we have a conflict
        if( shortlist.isEmpty() ){
            descriptor = null;            
        }
        else if( shortlist.size() == 1 ){
            // record the fact we can connect to this page
            descriptor = shortlist.get(0);
        }        
        else {
            // ignore generic datastore for now
            if( true ){
                for( Iterator<UDIGConnectionFactoryDescriptor> i= shortlist.iterator(); i.hasNext();){
                    UDIGConnectionFactoryDescriptor d = i.next();
                    String id = d.getId();
                    if( "net.refractions.udig.catalog.geotools.connection.dataStore".equals(id)){
                        i.remove();
                    }
                }
                if( shortlist.size() == 1 ){
                    // record the fact we can connect to this page
                    descriptor = shortlist.get(0);
                    return;
                }
            }
            descriptor = null;
            return; // we need to ask the user (prompt with shortlist)
        }
 
        if( descriptor != null ){
            String value = context.toString();
            String type = context.getClass().getName();
            
            CatalogPlugin.trace("Drag and Drop selected factory "+descriptor.getId() + " to handle "+type+":"+value, null );
        }
    }

    @Override
    public Pair<Boolean, State> dryRun() {
        return new Pair<Boolean, State>(descriptor!=null, null);
    }
    
    @Override
    public boolean run( IProgressMonitor monitor ) throws IOException {
    	monitor.beginTask(getName(),1);
    	monitor.done();
        return descriptor != null;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public State next() {
        // move to connection state
        int numberOfPages = descriptor.getWizardPageCount();
        if( numberOfPages==1 ){
            return new EndConnectionState(descriptor, validateService);
        } else {
            return new IntermediateState(0, numberOfPages, new EndConnectionState(descriptor, validateService));
        }
    }

    public UDIGConnectionFactoryDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor( UDIGConnectionFactoryDescriptor descriptor ) {
        this.descriptor = descriptor;
    }

    @Override
    public String getName() {
        return Messages.DataSourceSelectionState_name;
    }

    public List<UDIGConnectionFactoryDescriptor> getShortlist() {
        return shortlist;
    }
}
