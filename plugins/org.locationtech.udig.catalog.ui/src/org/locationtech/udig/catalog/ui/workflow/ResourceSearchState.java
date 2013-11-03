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
package org.locationtech.udig.catalog.ui.workflow;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.core.Pair;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * State searching for IGeoResources using a text string and extend.
 * <p>
 * The selected resources are those in the {@link #getResources()} map.  The keys are the selected
 * resources and the values are the parents.  State normally uses a ConnectionState but it
 * isn't required is {@link #setServices(List)} is used to set the services.
 * 
 * @author Jody Garnett
 * @since 1.3.3
 */
public class ResourceSearchState extends State {
    
    public static final IResolve IMPORT_PLACEHOLDER = new ImportPlaceholder();
    
    private String search;
    private ReferencedEnvelope extent;
    private List<IResolve> results;
    private Set<IResolve> selected = Collections.emptySet();
    
    /**
     * Initial results based on workflow context.
     */
    @Override
    public void init( IProgressMonitor monitor ) throws IOException {
        // try to generate some default resources based on context
        // LinkedHashMap to keep the order
        results = new ArrayList<IResolve>();
        selected = new HashSet<IResolve>();
        
        Object context = getWorkflow().getContext();
        
        // use the context object to try and match a resource
        if (context != null) {
            if( context instanceof String ){
                search = (String) context;
            }
            if( context instanceof IResolve ){
                results.add( (IResolve) context );
            }
            if( context instanceof Iterable ){
                Iterable<?> iterable = (Iterable<?>) context;
                for( Object object : iterable ) {
                    if( object instanceof IResolve ){
                        results.add( (IResolve) object );
                    }
                }
            }
        }
        if( results != null && results.size() == 1 ){
            // only one thing to select!
            selected.add( results.get(0) ); 
        }
    }
    public String getSearch() {
        return search;
    }
    public void setSearch(String search) {
        this.search = search;
    }
    public ReferencedEnvelope getExtent() {
        return extent;
    }
    public void setExtent(ReferencedEnvelope extent) {
        this.extent = extent;
    }
    public List<IResolve> getResults() {
        return results;
    }
    public Set<IResolve> getSelected() {
        return selected;
    }
    public void setSelected(Set<IResolve> selected) {
        this.selected = selected;
    }
    
    @Override
    public Pair<Boolean, State> dryRun() {
        boolean singeSelection = selected != null && selected.size() == 1;
        return new Pair<Boolean, State>( singeSelection, null );
    }
    /**
     * Complete if any resources have been selected.
     * <p>
     * Note a placeholder resource is used to mark the user requesting a
     * a data import.
     */
    @Override
    public boolean run( IProgressMonitor monitor ) throws IOException {
        // complete if any the resources have been "selected"

        if (selected == null || selected.isEmpty()){
            return false;
        }
        return true;
    }

    public boolean hasNext() {
        if (selected == null || selected.isEmpty()){
            return false;
        }
        if( selected.contains( IMPORT_PLACEHOLDER )){
            return true; // will queue a data import next
        }
        return false;
    }
    
    @Override
    public State next() {
        if( selected.isEmpty() ){
            return null;
        }
        if( selected.contains( IMPORT_PLACEHOLDER )){
            DataSourceSelectionState dsState = new DataSourceSelectionState(true);
            return dsState;
            //return getWorkflow().getState( DataSourceSelectionState.class );
        }
        else {
            //ResourceSelectionState rsState = new ResourceSelectionState();
            //return rsState;
            return null;
        }
    }
    
    @Override
    public String getName() {
        return Messages.ResourceSelectionPage_searching ; 
    }
}
class ImportPlaceholder implements IResolve {
    private ID identifier = new ID("internal:///localhost/import","import");
    
    @Override
    public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
        return null;
    }
    @Override
    public <T> boolean canResolve(Class<T> adaptee) {
        return false;
    }
    @Override
    public IResolve parent(IProgressMonitor monitor) throws IOException {
        return null;
    }
    @Override
    public List<IResolve> members(IProgressMonitor monitor) throws IOException {
        return Collections.emptyList();
    }

    @Override
    public Status getStatus() {
        return Status.CONNECTED;
    }

    @Override
    public Throwable getMessage() {
        return null;
    }

    @Override
    public URL getIdentifier() {
        return identifier.toURL();
    }

    @Override
    public ID getID() {
        return identifier;
    }

    @Override
    public String getTitle() {
        return Messages.WorkflowWizardDialog_importTask;
    }

    @Override
    public void dispose(IProgressMonitor monitor) {        
    }
    
}
