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
package eu.udig.style.advanced.common.styleattributeclasses;

import static eu.udig.style.advanced.utils.Utilities.DEFAULT_COLOR;
import static eu.udig.style.advanced.utils.Utilities.DEFAULT_OFFSET;
import static eu.udig.style.advanced.utils.Utilities.DEFAULT_OPACITY;
import static eu.udig.style.advanced.utils.Utilities.DEFAULT_WIDTH;
import static eu.udig.style.advanced.utils.Utilities.ff;
import static eu.udig.style.advanced.utils.Utilities.getOffset;
import static eu.udig.style.advanced.utils.Utilities.sf;

import java.awt.geom.Point2D;
import java.util.List;

import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Stroke;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.expression.Expression;
import org.opengis.style.GraphicalSymbol;

import eu.udig.style.advanced.utils.Utilities;

/**
 * A wrapper for a {@link PolygonSymbolizer} to ease interaction with gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class PolygonSymbolizerWrapper extends LineSymbolizerWrapper {

    private Fill fill;
    private String fillColor;
    private String fillOpacity;
    private Graphic fillGraphicFill;
    private String wkMarkNameFill;
    private String wkMarkColorFill;
    private String wkMarkWidthFill;
    private String wkMarkSizeFill;
    private boolean hasFill;
    private Mark mark;

    public PolygonSymbolizerWrapper( Symbolizer tmpSymbolizer, RuleWrapper parent ) {
        super((PolygonSymbolizer) tmpSymbolizer, parent);

        PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) tmpSymbolizer;

        // offset
        Point2D offset = getOffset(polygonSymbolizer);
        if (offset != null) {
            xOffset = String.valueOf(offset.getX());
            yOffset = String.valueOf(offset.getY());
        } else {
            xOffset = DEFAULT_OFFSET;
            yOffset = DEFAULT_OFFSET;
        }

        stroke = polygonSymbolizer.getStroke();
        if (stroke != null) {
            strokeColor = stroke.getColor().evaluate(null, String.class);
            Expression width = stroke.getWidth();
            strokeWidth = width.evaluate(null, String.class);
            Expression opacity = stroke.getOpacity();
            strokeOpacity = opacity.evaluate(null, String.class);

            if (strokeColor == null) {
                strokeColor = DEFAULT_COLOR;
            }
            if (strokeOpacity == null) {
                strokeOpacity = DEFAULT_OPACITY;
            }
            if (strokeWidth == null) {
                strokeWidth = DEFAULT_WIDTH;
            }

            strokeGraphicStroke = stroke.getGraphicStroke();
            if (strokeGraphicStroke != null) {
                List<GraphicalSymbol> graphicalSymbolsList = strokeGraphicStroke.graphicalSymbols();
                if (graphicalSymbolsList.size() > 0) {
                    GraphicalSymbol graphicalSymbol = graphicalSymbolsList.get(0);
                    if (graphicalSymbol instanceof ExternalGraphic) {
                        strokeExternalGraphicStroke = (ExternalGraphic) graphicalSymbol;
                    }
                }
            }

            // dash
            float[] dashArray = stroke.getDashArray();
            if (dashArray != null) {
                dash = Utilities.getDashString(dashArray);
            } else {
                dash = "";
            }
            // dashoffset
            dashOffset = stroke.getDashOffset().evaluate(null, String.class);
            // line cap
            lineCap = stroke.getLineCap().evaluate(null, String.class);
            // line join
            lineJoin = stroke.getLineJoin().evaluate(null, String.class);

            hasStroke = true;
        } else {
            hasStroke = false;
        }

        fill = polygonSymbolizer.getFill();
        if (fill != null) {
            Expression color = fill.getColor();
            if (color != null)
                fillColor = color.evaluate(null, String.class);
            Expression opacity = fill.getOpacity();
            fillOpacity = opacity.evaluate(null, String.class);

            fillGraphicFill = fill.getGraphicFill();
            if (fillGraphicFill != null) {
                List<GraphicalSymbol> graphicalSymbolsList = fillGraphicFill.graphicalSymbols();

                if (graphicalSymbolsList.size() > 0) {
                    GraphicalSymbol graphicalSymbol = graphicalSymbolsList.get(0);
                    if (graphicalSymbol instanceof ExternalGraphic) {
                        fillExternalGraphicFill = (ExternalGraphic) graphicalSymbol;
                    } else if (graphicalSymbol instanceof Mark) {
                        mark = (Mark) graphicalSymbol;
                        wkMarkNameFill = mark.getWellKnownName().evaluate(null, String.class);
                        Stroke stroke = mark.getStroke();
                        wkMarkColorFill = stroke.getColor().evaluate(null, String.class);
                        wkMarkWidthFill = stroke.getWidth().evaluate(null, String.class);
                        wkMarkSizeFill = fillGraphicFill.getSize().evaluate(null, String.class);
                    }
                }
            }

            hasFill = true;
        } else {
            hasFill = false;
        }

    }

    private void checkFillExists() {
        if (fill == null) {
            fill = sf.createFill(ff.literal(strokeColor));
            PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) getSymbolizer();
            polygonSymbolizer.setFill(fill);
            fillGraphicFill = fill.getGraphicFill();
        }
    }

    private void checkMarkExists() {
        if (mark == null) {
            mark = sf.createMark();
            checkFillExists();
            if (fillGraphicFill == null) {
                fillGraphicFill = sf.createDefaultGraphic();
                fill.setGraphicFill(fillGraphicFill);
            }
            fillGraphicFill.graphicalSymbols().clear();
            fillGraphicFill.graphicalSymbols().add(mark);
        }
    }

    public Graphic getFillGraphicFill() {
        return fillGraphicFill;
    }

    public void setFillGraphicFill( Graphic fillGraphicFill ) {
        this.fillGraphicFill = fillGraphicFill;
        checkFillExists();

        fill.setGraphicFill(fillGraphicFill);
    }

    // ///// GETTERS/SETTERS
    public void setHasFill( boolean hasFill ) {
        this.hasFill = hasFill;
        if (hasFill) {
            checkFillExists();
        } else {
            fill = null;
            PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) getSymbolizer();
            polygonSymbolizer.setFill(null);
        }
    }

    public void setHasStroke( boolean hasStroke ) {
        this.hasStroke = hasStroke;
        if (hasStroke) {
            checkStrokeExists();
        }else{
            stroke = null;
            PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) getSymbolizer();
            polygonSymbolizer.setStroke(null);
        }
    }

    protected void checkStrokeExists() {
        if (stroke == null) {
            stroke = sf.createStroke(ff.literal(strokeColor), ff.literal(strokeWidth));
            PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) getSymbolizer();
            polygonSymbolizer.setStroke(stroke);
            strokeGraphicStroke = stroke.getGraphicStroke();
        }
    }
    
    public void setFillColor( String fillColor ) {
        this.fillColor = fillColor;
        checkFillExists();
        if (fillColor == null) {
            hasFill = false;
        } else {
            hasFill = true;
        }
        fill.setColor(ff.literal(fillColor));
    }

    public void setFillOpacity( String fillOpacity, boolean isProperty ) {
        this.fillOpacity = fillOpacity;
        checkFillExists();
        if (isProperty) {
            fill.setOpacity(ff.property(fillOpacity));
        } else {
            fill.setOpacity(ff.literal(fillOpacity));
        }
    }

    public void setWkMarkNameFill( String wkMarkNameFill ) {
        this.wkMarkNameFill = wkMarkNameFill;
        checkMarkExists();
        if (wkMarkNameFill != null) {
            mark.setWellKnownName(ff.literal(wkMarkNameFill));
        }
    }

    public void setWkMarkColorFill( String wkMarkColorFill ) {
        this.wkMarkColorFill = wkMarkColorFill;
        checkMarkExists();
        if (wkMarkColorFill != null) {
            Stroke markStroke = mark.getStroke();
            markStroke.setColor(ff.literal(wkMarkColorFill));
        }
    }

    public void setWkMarkWidthFill( String wkMarkWidthFill ) {
        this.wkMarkWidthFill = wkMarkWidthFill;
        checkMarkExists();
        if (wkMarkWidthFill != null) {
            Stroke markStroke = mark.getStroke();
            markStroke.setWidth(ff.literal(wkMarkWidthFill));
        }
    }

    public void setWkMarkSizeFill( String wkMarkSizeFill ) {
        this.wkMarkSizeFill = wkMarkSizeFill;
        checkFillExists();
        fillGraphicFill.setSize(ff.literal(wkMarkSizeFill));
    }

    // getters
    public boolean hasFill() {
        return hasFill;
    }

    public String getFillColor() {
        return fillColor;
    }

    public String getFillOpacity() {
        return fillOpacity;
    }

    public String getWkMarkNameFill() {
        return wkMarkNameFill;
    }

    public String getWkMarkColorFill() {
        return wkMarkColorFill;
    }

    public String getWkMarkWidthFill() {
        return wkMarkWidthFill;
    }

    public String getWkMarkSizeFill() {
        return wkMarkSizeFill;
    }

}