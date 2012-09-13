package net.refractions.udig.catalog.ui.workflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.core.Pair;

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

    private String search;
    private ReferencedEnvelope extent;
    private List<IResolve> results;
    private Set<IResolve> selected;
    
    /**
     * Initial results based on workflow context.
     */
    @SuppressWarnings("unchecked")
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

    @Override
    public String getName() {
        return Messages.ResourceSelectionPage_searching ; 
    }
}