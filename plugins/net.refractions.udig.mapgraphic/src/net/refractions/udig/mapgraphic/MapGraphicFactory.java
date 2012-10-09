/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.mapgraphic;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;

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

