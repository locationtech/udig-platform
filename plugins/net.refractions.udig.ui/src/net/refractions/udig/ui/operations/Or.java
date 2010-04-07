package net.refractions.udig.ui.operations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Or element in the operation extension point.
 * @author jones
 * @since 1.1.0
 */
public class Or implements OpFilter{

    private List<OpFilter> filters=new ArrayList<OpFilter>();
    
    /**
     * @return Returns the filters.
     */
    public List<OpFilter> getFilters() {
        return filters;
    }

    public boolean accept( Object object ) {
        for( OpFilter filter : filters ) {
            if( filter.accept(object) )
                return true;
        }
        return false;
    }

    public void addListener( IOpFilterListener listener ) {
        for( OpFilter filter : filters ) {
            filter.addListener(listener);
        }
    }

    public boolean canCacheResult() {
        for( OpFilter filter : filters ) {
            if( !filter.canCacheResult() )
                return false;
        }

        return true;
    }

    public boolean isBlocking() {
        for( OpFilter filter : filters ) {
            if( filter.isBlocking() )
                return true;
        }

        return false;
    }

    public void removeListener( IOpFilterListener listener ) {
        for( OpFilter filter : filters ) {
            filter.removeListener(listener);
        }
    }

}
