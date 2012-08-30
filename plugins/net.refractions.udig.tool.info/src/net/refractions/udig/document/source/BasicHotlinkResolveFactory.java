package net.refractions.udig.document.source;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.document.IHotlinkSource;
import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.document.ui.DocumentPropertyPage;
import net.refractions.udig.tool.info.InfoPlugin;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Folds in BasicHotlink implementation for any IGeoResource that lists its hotlink attributes as
 * part of its {@link IGeoResource#getPersistentProperties()}.
 * <p>
 * Contains utility methods to help both {@link BasicHotlink} and {@link DocumentPropertyPage}
 * extract descriptors from a IGeoResource.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class BasicHotlinkResolveFactory implements IResolveAdapterFactory {

    /**
     * {@link IGeoResource#getPersistentProperties()} key used to record hotlink descriptor list.
     * <p>
     * The value is stored as a definition consisting of:
     * <code>attributeName:file,attributeName:link</code>
     */
    final static String HOTLINK = "hotlink"; //$NON-NLS-1$

    @Override
    public boolean canAdapt(IResolve resolve, Class<? extends Object> adapter) {
        IGeoResource resource = (IGeoResource) resolve; // safe cast due to extension point config
        if (resource.getPersistentProperties().containsKey(BasicHotlinkResolveFactory.HOTLINK)) {
            return true;
        }
        return false;
    }

    @Override
    public Object adapt(IResolve resolve, Class<? extends Object> adapter, IProgressMonitor monitor)
            throws IOException {
        IGeoResource resource = (IGeoResource) resolve; // safe cast due to extension point config
        if (resource.getPersistentProperties().containsKey(BasicHotlinkResolveFactory.HOTLINK)) {
            IHotlinkSource hotlink = new BasicHotlink((ShpGeoResourceImpl) resource);
            return hotlink;
        }
        return null; // not available
    }
    
    public static boolean hasHotlinkDescriptors(IGeoResource resource) {
        Map<String, Serializable> persistentProperties = resource.getPersistentProperties();
        return persistentProperties.containsKey(HOTLINK);
    }
    /**
     * Retrieve the hotlink descriptors for the provided resource.
     * 
     * @return
     */
    public static List<HotlinkDescriptor> getHotlinkDescriptors(IGeoResource resource) {
        Map<String, Serializable> persistentProperties = resource.getPersistentProperties();
        String definition = (String) persistentProperties.get(HOTLINK);
        List<HotlinkDescriptor> list = new ArrayList<IHotlinkSource.HotlinkDescriptor>();
        if (definition != null && !definition.isEmpty()) {
            String split[] = definition.split(",");
            for (String defn : split) {
                try {
                    HotlinkDescriptor descriptor = new HotlinkDescriptor(defn);
                    list.add(descriptor);
                } catch (Throwable t) {
                    InfoPlugin.log("Unable describe hotlink:" + defn, t);
                }
            }
        }
        return list;
    }
    /**
     * Retrieve the hotlink descriptors for the provided resource.
     * 
     * @return
     */
    public static void putHotlinkDescriptors(IGeoResource resource, List<HotlinkDescriptor> list) {
        Map<String, Serializable> persistentProperties = resource.getPersistentProperties();
        
        if( list == null ){
            persistentProperties.remove( HOTLINK);
        }
        else {
            StringBuilder build = new StringBuilder();
            for( Iterator<HotlinkDescriptor> i=list.iterator(); i.hasNext(); )  {
                HotlinkDescriptor descriptor = i.next();
                 build.append( descriptor );
                 if( i.hasNext() ){
                     build.append( "," ); //$NON-NLS-1$
                 }
            }
            String definition = build.toString();
            persistentProperties.put( HOTLINK, definition );
        }
    }
    public static void clearHotlinkDescriptors(IGeoResource resource) {
        Map<String, Serializable> persistentProperties = resource.getPersistentProperties();
        persistentProperties.remove(HOTLINK);
    }

}
