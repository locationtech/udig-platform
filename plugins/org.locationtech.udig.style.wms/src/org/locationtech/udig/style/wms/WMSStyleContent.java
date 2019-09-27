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
package org.locationtech.udig.style.wms;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.geotools.ows.wms.Layer;
import org.geotools.ows.wms.StyleImpl;

public class WMSStyleContent extends StyleContent {

    /**
     * Key used to store a wms style on the style blackboard of a layer.
     */
    public static final String WMSSTYLE = "org.locationtech.udig.render.wmsStyle"; //$NON-NLS-1$

	public WMSStyleContent() {
		super(WMSSTYLE);
	}

	@Override
	public Class getStyleClass() {
		return StyleImpl.class;
	}

	@Override
	public void save(IMemento memento, Object value) {
        StyleImpl style = (StyleImpl)value;
		memento.putString("value", style.getName()); //$NON-NLS-1$
	}

	@Override
	public Object load(IMemento memento) {
		return new StyleImpl(memento.getString("value")); //$NON-NLS-1$
	}

	@Override
	public Object load(URL url, IProgressMonitor monitor) throws IOException {
		return null;
	}

	@Override
	public Object createDefaultStyle(IGeoResource resource, Color colour, IProgressMonitor monitor) throws IOException {
        if( !resource.canResolve(Layer.class) ){
            return null;
        }
        List<StyleImpl> styles = WMSStyleConfigurator.getStyles(resource);
        if( styles.isEmpty() )
            return null;
        else
            return styles.get(0);
	}

}
