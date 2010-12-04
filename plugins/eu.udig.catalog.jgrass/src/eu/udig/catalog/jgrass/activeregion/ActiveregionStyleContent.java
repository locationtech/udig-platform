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
package eu.udig.catalog.jgrass.activeregion;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

@SuppressWarnings("nls")
public class ActiveregionStyleContent extends StyleContent {

    public static final String ID = "eu.udig.catalog.jgrass.activeregion.activeregionStyle"; //$NON-NLS-1$

    public static final String WINDPATH = "WINDPATH";
    public static final String N = "N";
    public static final String S = "S";
    public static final String E = "E";
    public static final String W = "W";
    public static final String ROWS = "ROWS";
    public static final String COLS = "COLS";
    public static final String CRS = "CRS";
    public static final String BALPHA = "BALPHA";
    public static final String FALPHA = "FALPHA";
    public static final String DOGRID = "DOGRID";

    public ActiveregionStyleContent() {
        super(ID);
    }

    @Override
    public Class getStyleClass() {
        return ActiveRegionStyle.class;
    }

    @Override
    public void save( IMemento memento, Object value ) {
        ActiveRegionStyle style = (ActiveRegionStyle) value;

        memento.putString(WINDPATH, style.windPath);
        memento.putFloat(N, style.north);
        memento.putFloat(S, style.south);
        memento.putFloat(E, style.east);
        memento.putFloat(W, style.west);
        memento.putInteger(ROWS, style.rows);
        memento.putInteger(COLS, style.cols);
        memento.putString(CRS, style.crsString);
        memento.putBoolean(DOGRID, style.doGrid);
        memento.putFloat(BALPHA, style.bAlpha);
        memento.putFloat(FALPHA, style.fAlpha);

        // TODO save colors
    }

    @Override
    public Object load( IMemento memento ) {
        ActiveRegionStyle style = createDefault();

        style.windPath = memento.getString(WINDPATH);
        style.north = memento.getFloat(N);
        style.south = memento.getFloat(S);
        style.east = memento.getFloat(E);
        style.west = memento.getFloat(W);
        style.rows = memento.getInteger(ROWS);
        style.cols = memento.getInteger(COLS);
        style.crsString = memento.getString(CRS);
        style.doGrid = memento.getBoolean(DOGRID);
        style.bAlpha = memento.getFloat(BALPHA);
        style.fAlpha = memento.getFloat(FALPHA);

        return style;
    }

    @Override
    public Object load( URL url, IProgressMonitor monitor ) throws IOException {
        return null;
    }

    @Override
    public Object createDefaultStyle( IGeoResource resource, Color colour, IProgressMonitor monitor ) throws IOException {
        if (!resource.canResolve(ActiveRegionMapGraphic.class))
            return null;
        return createDefault();
    }

    public static ActiveRegionStyle createDefault() {
        ActiveRegionStyle style = new ActiveRegionStyle();

        style.windPath = null;
        style.north = Float.NaN;
        style.south = Float.NaN;
        style.east = Float.NaN;
        style.west = Float.NaN;
        style.rows = -1;
        style.cols = -1;
        style.bAlpha = 0.3f;
        style.backgroundColor = new Color(0f, 0.56f, 0f, style.bAlpha);
        style.fAlpha = 0.5f;
        style.foregroundColor = new Color(0f, 0.56f, 0f, style.fAlpha);
        style.doGrid = false;

        return style;
    }
}
