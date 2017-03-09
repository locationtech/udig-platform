/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.scale;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.mapgraphic.MapGraphicPlugin;
import org.locationtech.udig.project.StyleContent;

/**
 * The content for the map scale mapgraphic background style
 * 
 * @author Emily
 * @since 1.1.0
 */
public class ScaleDenomStyleContent extends StyleContent {
	/** extension id */
	public static final String ID = "org.locationtech.udig.mapgraphic.scaledenom.style"; //$NON-NLS-1$

	private static final String COLOR = "COLOR"; //$NON-NLS-1$
	private static final String LABEL_PREFIX = "LABEL_PREFIX"; //$NON-NLS-1$
	
	public ScaleDenomStyleContent() {
		super(ID);
	}

	@Override
	public Object createDefaultStyle(IGeoResource resource, Color colour,
			IProgressMonitor monitor) throws IOException {
		return new ScaleDenomStyle(Color.WHITE);
	}

	@Override
	public Class<?> getStyleClass() {
		return ScaleDenomStyle.class;
	}

	@Override
	public Object load(IMemento memento) {
		try {
		        ScaleDenomStyle style = null;
			Integer color = memento.getInteger(COLOR);
			String label = memento.getString(LABEL_PREFIX);
			style = color != null ? new ScaleDenomStyle(new Color(color)) : new ScaleDenomStyle();
			if (label != null) {
			    style.setLabel(label);
			}
			return style;

		} catch (Throwable e) {
			MapGraphicPlugin.log("Error decoding the stored font", e); //$NON-NLS-1$
		}
		return new ScaleDenomStyle();
	}

	@Override
	public Object load(URL url, IProgressMonitor monitor) throws IOException {
		return null;
	}

	@Override
	public void save(IMemento memento, Object value) {
		ScaleDenomStyle style = (ScaleDenomStyle) value;
		if (style.getColor() != null) {
			memento.putInteger(COLOR, style.getColor().getRGB());
		}
		if (style.getLabel() != null) {
		    memento.putString(LABEL_PREFIX, style.getLabel());
		}

	}

}
