/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.style.jgrass.legend;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

@SuppressWarnings("nls")
public class RasterLegendStyleContent extends StyleContent {

    public static final String ID = "eu.hydrologis.jgrass.rasterlegend.rasterlegendStyle"; //$NON-NLS-1$

    public static final String MAPPATH = "MAPPATH";
    public static final String LEGENDWIDTH = "LEGENDWIDTH";
    public static final String LEGENDHEIGHT = "LEGENDHEIGHT";
    public static final String BOXWIDTH = "BOXWIDTH";
    public static final String XPOS = "XPOS";
    public static final String YPOS = "YPOS";
    public static final String ISROUNDEDRECTANGLE = "ISROUNDEDRECTANGLE";
    public static final String BALPHA = "BALPHA";
    public static final String FALPHA = "FALPHA";
    public static final String TITLESTRING = "TITLESTRING";

    public RasterLegendStyleContent() {
        super(ID);
    }

    @Override
    public Class getStyleClass() {
        return RasterLegendStyle.class;
    }

    @Override
    public void save( IMemento memento, Object value ) {
        RasterLegendStyle style = (RasterLegendStyle) value;

        memento.putString(MAPPATH, style.mapPath);
        memento.putInteger(LEGENDWIDTH, style.legendWidth);
        memento.putInteger(LEGENDHEIGHT, style.legendHeight);
        memento.putInteger(BOXWIDTH, style.boxWidth);
        memento.putInteger(XPOS, style.xPos);
        memento.putInteger(YPOS, style.yPos);
        memento.putBoolean(ISROUNDEDRECTANGLE, style.isRoundedRectangle);
        memento.putInteger(BALPHA, style.bAlpha);
        memento.putInteger(FALPHA, style.fAlpha);
        memento.putString(TITLESTRING, style.titleString);
        
        // TODO save colors
    }

    @Override
    public Object load( IMemento memento ) {
        RasterLegendStyle style = createDefault();

        style.mapPath = memento.getString(MAPPATH);
        style.legendWidth = memento.getInteger(LEGENDWIDTH);
        style.legendHeight = memento.getInteger(LEGENDHEIGHT);
        style.boxWidth = memento.getInteger(BOXWIDTH);
        style.xPos = memento.getInteger(XPOS);
        style.yPos = memento.getInteger(YPOS);
        style.isRoundedRectangle = memento.getBoolean(ISROUNDEDRECTANGLE);
        style.bAlpha = memento.getInteger(BALPHA);
        style.fAlpha = memento.getInteger(FALPHA);
        style.titleString = memento.getString(TITLESTRING);

        return style;
    }

    @Override
    public Object load( URL url, IProgressMonitor monitor ) throws IOException {
        return null;
    }

    @Override
    public Object createDefaultStyle( IGeoResource resource, Color colour, IProgressMonitor monitor )
            throws IOException {
        if (!resource.canResolve(RasterLegendGraphic.class))
            return null;
        return createDefault();
    }

    public static RasterLegendStyle createDefault() {
        RasterLegendStyle style = new RasterLegendStyle();

        style.mapPath = null;
        style.legendWidth = 150;
        style.legendHeight = 200;
        style.boxWidth = 15;
        style.xPos = 15;
        style.yPos = 15;
        style.isRoundedRectangle = false;
        style.bAlpha = 230;
        style.fAlpha = 255;
        style.titleString = ""; //$NON-NLS-1$

        style.fontColor = new Color(0, 0, 0);
        style.foregroundColor = new Color(255, 255, 255, style.fAlpha);
        style.backgroundColor = new Color(255, 255, 255, style.bAlpha);

        return style;
    }
}
