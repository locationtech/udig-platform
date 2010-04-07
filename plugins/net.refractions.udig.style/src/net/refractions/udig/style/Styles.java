package net.refractions.udig.style;

import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.project.internal.Layer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

public class Styles {
    /**
     * 
     * Returns the set of style id's which support a particular layer.
     * 
     * @return A set of id's.
     */
    public static Set<String> getStyleIDs(final Layer layer) {
        final Set<String> ids = new HashSet<String>();
        ExtensionPointProcessor p = new ExtensionPointProcessor() {
            public void process( IExtension extension, IConfigurationElement element ) throws Exception {
                IStyleConfigurator sce = (IStyleConfigurator)element.createExecutableExtension("class"); //$NON-NLS-1$
                if (sce.canStyle(layer)) {
                    ids.add(sce.getStyleId());
                }
            }            
        };        
        ExtensionPointUtil.process( StylePlugin.getDefault(), IStyleConfigurator.XPID, p);
        return ids;
    }
}