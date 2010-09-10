package net.refractions.udig.catalog.internal.wmt.ui.properties;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;

import org.eclipse.core.expressions.PropertyTester;

/**
 * This class ensures that the property page is only
 * shown for layers that have a WMTGeoResource.
 * 
 * Take a look at the extension points (plugin.xml) to see,
 * how it is called.
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WMTShowPropertiesTester extends PropertyTester {

   public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
      if (receiver == null || !(receiver instanceof ILayer))
         return false;
      
      ILayer layer = (Layer) receiver;      
      IGeoResource resource = layer.findGeoResource(WMTSource.class); 
      
      return (resource != null);
   }
}
