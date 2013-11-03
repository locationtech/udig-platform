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
package org.locationtech.udig.mapgraphic;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.core.internal.ExtensionPointUtil;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

public class MapGraphicFactory {
    
	private static final MapGraphicFactory instance = new MapGraphicFactory();
	
	public static MapGraphicFactory getInstance() {
		return instance;
	}
	
	private MapGraphicFactory() {
		//private constructor
	}
	
    public MapGraphic createMapGraphic(String id) {
        Processor p = new Processor(id);
        ExtensionPointUtil
        	.process(MapGraphicPlugin.getDefault(),MapGraphic.XPID,p);
        
        if (p.mg.isEmpty())
        	return null;
        
        try {
        	return (MapGraphic) p.mg.get(0).createExecutableExtension("class"); //$NON-NLS-1$
        }
        catch(Throwable t) {
        	CatalogPlugin.log(t.getLocalizedMessage(),t);
        }
        
        return null;
    }
    
    public List<IConfigurationElement> getMapGraphics() {
    	Processor p = new Processor();
    	ExtensionPointUtil.process(MapGraphicPlugin.getDefault(),MapGraphic.XPID,p);
    	
    	return p.mg;
    }
}


class Processor implements ExtensionPointProcessor {

    String id;
    List<IConfigurationElement> mg = new ArrayList<IConfigurationElement>();
    
    Processor() {
    	this(null);
    }
    
    Processor(String id) {
        this.id = id;
    }
    
    public void process( IExtension extension, IConfigurationElement element ) 
    	throws Exception {
    	try {
    		String theId = element.getAttribute("id"); //$NON-NLS-1$
    		if (id == null || id.equals(theId)) {
    			mg.add(element);
    		}
    	}
    	catch(Throwable t) {
    		CatalogPlugin.log(t.getLocalizedMessage(),t);
    	}
        
    }
    
}

