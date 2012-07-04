/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
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
