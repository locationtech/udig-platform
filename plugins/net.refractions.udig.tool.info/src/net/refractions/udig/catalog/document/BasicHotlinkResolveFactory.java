package net.refractions.udig.catalog.document;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IHotlink;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;

/**
 * Folds in BasicHotlink implementation for any IGeoResource that lists its hotlink
 * attributes as part of its {@link IGeoResource#getPersistentProperties()}.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class BasicHotlinkResolveFactory implements IResolveAdapterFactory {

    @Override
    public boolean canAdapt(IResolve resolve, Class<? extends Object> adapter) {
        IGeoResource resource = (IGeoResource) resolve; // safe cast due to extension point config
        if( resource.getPersistentProperties().containsKey("hotlink") ){
            return true;
        }
        return false;
    }

    @Override
    public Object adapt(IResolve resolve, Class<? extends Object> adapter, IProgressMonitor monitor)
            throws IOException {
        IGeoResource resource = (IGeoResource) resolve; // safe cast due to extension point config
        if( resource.getPersistentProperties().containsKey( BasicHotlink.HOTLINK) ){
            IHotlink hotlink = new BasicHotlink( resource );
            return hotlink;
        }
        return null; // not available
    }

}
