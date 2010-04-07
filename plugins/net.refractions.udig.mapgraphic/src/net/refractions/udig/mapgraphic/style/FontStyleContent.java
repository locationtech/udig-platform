/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.mapgraphic.style;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.mapgraphic.MapGraphicPlugin;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

/**
 * This content 
 * @author jesse
 * @since 1.1.0
 */
public class FontStyleContent extends StyleContent {
    /** extension id */
    public static final String ID = "net.refractions.udig.mapgraphic.style.font"; //$NON-NLS-1$
    private static final String FONT_NAME = "FONT_NAME"; //$NON-NLS-1$
    private static final String STYLE = "STYLE"; //$NON-NLS-1$
    private static final String SIZE = "SIZE"; //$NON-NLS-1$

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
                return new FontStyle(font);
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
        }
    }

}
