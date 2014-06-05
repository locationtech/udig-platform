/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.style;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.net.URL;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.mapgraphic.MapGraphicPlugin;
import org.locationtech.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

/**
 * This content 
 * @author jesse
 * @since 1.1.0
 */
public class FontStyleContent extends StyleContent {
    /** extension id */
    public static final String ID = "org.locationtech.udig.mapgraphic.style.font"; //$NON-NLS-1$

    private static final String FONT_NAME = "FONT_NAME"; //$NON-NLS-1$
    private static final String STYLE = "STYLE"; //$NON-NLS-1$
    private static final String SIZE = "SIZE"; //$NON-NLS-1$
    private static final String COLOR = "COLOR"; //$NON-NLS-1$

    public FontStyleContent( ) {
        super(ID);
    }

    @Override
    public Object createDefaultStyle( IGeoResource resource, Color colour, IProgressMonitor monitor )
            throws IOException {
        return new FontStyle();
    }

    @Override
    public Class<?> getStyleClass() {
        return FontStyle.class;
    }

    @Override
    public Object load( IMemento memento ) {
        try {
            if (memento.getString(FONT_NAME) != null) {
                String name = memento.getString(FONT_NAME);
                Integer style = memento.getInteger(STYLE);
                Integer size = memento.getInteger(SIZE);
                Font font = new Font(name, style, size);
                Integer color = memento.getInteger(COLOR);
                
                if (color != null){
                	return new FontStyle(font, new Color(color));	
                }else{
                	return new FontStyle(font);
                }
            }
        } catch (Throwable e) {
            MapGraphicPlugin.log("Error decoding the stored font", e); //$NON-NLS-1$
        }
        return new FontStyle();
    }

    @Override
    public Object load( URL url, IProgressMonitor monitor ) throws IOException {
        return null;
    }

    @Override
    public void save( IMemento memento, Object value ) {
        FontStyle style = (FontStyle) value;
        if( style.getFont()!=null ){
            memento.putString(FONT_NAME, style.getFont().getFamily());
            memento.putInteger(STYLE, style.getFont().getStyle());
            memento.putInteger(SIZE, style.getFont().getSize());
            memento.putInteger(COLOR, style.getColor().getRGB());
        }
        
    }

}
