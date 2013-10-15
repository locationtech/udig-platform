/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
