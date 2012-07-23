package net.refractions.udig.catalog.document;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IHotlink;
import net.refractions.udig.catalog.IHotlink.HotlinkDescriptor;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.tool.info.InfoPlugin;

/**
 * Folds in BasicHotlink implementation for any IGeoResource that lists its hotlink attributes as
 * part of its {@link IGeoResource#getPersistentProperties()}.
 * <p>
 * Contains utility methods to help both {@link BasicHotlink} and {@link HotlinkPropertyPage}
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
    final static String HOTLINK = "hotlink";

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
            IHotlink hotlink = new BasicHotlink(resource);
            return hotlink;
        }
        return null; // not available
    }

    /**
     * Retrieve the hotlink descriptors for the provided resource.
     * 
     * @return
     */
    public static List<HotlinkDescriptor> hotlinkDescriptors(IGeoResource resource) {
        Map<String, Serializable> persistentProperties = resource.getPersistentProperties();
        String definition = (String) persistentProperties.get(BasicHotlinkResolveFactory.HOTLINK);
        List<HotlinkDescriptor> list = new ArrayList<IHotlink.HotlinkDescriptor>();
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

}
