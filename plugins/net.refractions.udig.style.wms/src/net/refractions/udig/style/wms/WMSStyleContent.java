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
package net.refractions.udig.style.wms;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.StyleImpl;

public class WMSStyleContent extends StyleContent {

    /**
     * Key used to store a wms style on the style blackboard of a layer.
     */
    public static final String WMSSTYLE = "net.refractions.udig.render.wmsStyle"; //$NON-NLS-1$

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
