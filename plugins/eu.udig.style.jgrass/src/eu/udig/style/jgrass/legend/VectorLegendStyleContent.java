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

public class VectorLegendStyleContent extends StyleContent {

    public static final String ID = "eu.hydrologis.jgrass.rasterlegend.vectorlegendStyle"; //$NON-NLS-1$
    private Object stylevalue;

    public VectorLegendStyleContent() {
        super(ID);
    }

    @Override
    public Class getStyleClass() {
        return VectorLegendStyle.class;
    }

    @Override
    public void save( IMemento momento, Object value ) {
        this.stylevalue = value;
    }

    @Override
    public Object load( IMemento momento ) {
        return stylevalue;
    }

    @Override
    public Object load( URL url, IProgressMonitor monitor ) throws IOException {
        return null;
    }

    @Override
    public Object createDefaultStyle( IGeoResource resource, Color colour, IProgressMonitor monitor )
            throws IOException {
        if (!resource.canResolve(VectorLegendGraphic.class))
            return null;
        return createDefault();
    }

    public static VectorLegendStyle createDefault() {
        VectorLegendStyle style = new VectorLegendStyle();

        style.legendWidth = 80;
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
