/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.common.styleattributeclasses;

import static org.locationtech.udig.style.advanced.utils.Utilities.DEFAULT_COLOR;
import static org.locationtech.udig.style.advanced.utils.Utilities.DEFAULT_OFFSET;
import static org.locationtech.udig.style.advanced.utils.Utilities.DEFAULT_OPACITY;
import static org.locationtech.udig.style.advanced.utils.Utilities.DEFAULT_ROTATION;
import static org.locationtech.udig.style.advanced.utils.Utilities.DEFAULT_WIDTH;
import static org.locationtech.udig.style.advanced.utils.Utilities.externalGraphicsFromGraphic;
import static org.locationtech.udig.style.advanced.utils.Utilities.ff;
import static org.locationtech.udig.style.advanced.utils.Utilities.sf;

import java.awt.geom.Point2D;
import java.util.List;

import org.locationtech.udig.style.sld.SLD;
import org.locationtech.udig.ui.graphics.SLDs;

import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Stroke;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.expression.Expression;

import org.locationtech.udig.style.advanced.utils.Utilities;

/**
 * A wrapper for a {@link PointSymbolizer} to ease interaction with gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class DummySymbolizerWrapper extends SymbolizerWrapper {

    private String size;
    private String rotation;

    private String markName;
    private String fillColor;
    private String fillOpacity;
    private String strokeColor;
    private String strokeOpacity;
    private String strokeWidth;

    private boolean hasFill;
    private boolean hasStroke;
    private Mark mark;
    private Fill fill;
    private Stroke stroke;

    private Graphic graphic;

    public DummySymbolizerWrapper( Symbolizer symbolizer, RuleWrapper parent ) {
        super(symbolizer, parent);

        PointSymbolizer pointSymbolizer = (PointSymbolizer) symbolizer;
        graphic = pointSymbolizer.getGraphic();
        List<ExternalGraphic> externalGraphicsList = externalGraphicsFromGraphic(graphic);

        // size
        Expression sizeExpr = graphic.getSize();
        String tmp = expressionToString(sizeExpr);
        if (tmp != null) {
            size = tmp;
        } else {
            size = DEFAULT_WIDTH;
        }
        // rotation
        Expression rotationExpr = graphic.getRotation();
        tmp = expressionToString(rotationExpr);
        if (tmp != null) {
            rotation = tmp;
        } else {
            rotation = DEFAULT_ROTATION;
        }
        // offset
        Point2D offset = Utilities.getOffset(pointSymbolizer);
        if (offset != null) {
            xOffset = String.valueOf(offset.getX());
            yOffset = String.valueOf(offset.getY());
        } else {
            xOffset = DEFAULT_OFFSET;
            yOffset = DEFAULT_OFFSET;
        }

        if (externalGraphicsList.size() == 0) {
            mark = SLDs.mark(pointSymbolizer);
            if (mark == null) {
                return;
            }
            markName = mark.getWellKnownName().evaluate(null, String.class);
            if (markName == null || markName.equals("")) { //$NON-NLS-1$
                markName = "circle"; //$NON-NLS-1$
                mark.setWellKnownName(ff.literal(markName));
            }

            fill = mark.getFill();
            if (fill != null) {
                fillColor = fill.getColor().evaluate(null, String.class);
                Expression opacityExpr = fill.getOpacity();
                fillOpacity = expressionToString(opacityExpr);
                hasFill = true;
            } else {
                hasFill = false;
            }

            stroke = mark.getStroke();
            if (stroke != null) {
                Expression color = stroke.getColor();
                tmp = color.evaluate(null, String.class);
                if (tmp != null) {
                    strokeColor = tmp;
                } else {
                    strokeColor = DEFAULT_COLOR;
                }

                Expression opacity = stroke.getOpacity();
                tmp = expressionToString(opacity);
                if (tmp != null) {
                    strokeOpacity = tmp;
                } else {
                    strokeOpacity = DEFAULT_OPACITY;
                }

                Expression width = stroke.getWidth();
                tmp = expressionToString(width);
                if (tmp != null) {
                    strokeWidth = tmp;
                } else {
                    strokeWidth = DEFAULT_WIDTH;
                }
                hasStroke = true;
            } else {
                hasStroke = false;
            }
        } else {
            // graphics case
            externalGraphic = externalGraphicsList.get(0);
        }
    }

    public <T> T adapt( Class<T> adaptee ) {
        if (adaptee.isAssignableFrom(PointSymbolizerWrapper.class)) {
            PointSymbolizerWrapper geometrySymbolizersWrapper = new PointSymbolizerWrapper(
                    Utilities.createDefaultGeometrySymbolizer(SLD.POINT), null);
            return adaptee.cast(geometrySymbolizersWrapper);
        } else if (adaptee.isAssignableFrom(LineSymbolizerWrapper.class)) {
            LineSymbolizerWrapper geometrySymbolizersWrapper = new LineSymbolizerWrapper(
                    Utilities.createDefaultGeometrySymbolizer(SLD.LINE), null);
            return adaptee.cast(geometrySymbolizersWrapper);
        } else if (adaptee.isAssignableFrom(PolygonSymbolizerWrapper.class)) {
            PolygonSymbolizerWrapper geometrySymbolizersWrapper = new PolygonSymbolizerWrapper(
                    Utilities.createDefaultGeometrySymbolizer(SLD.POLYGON), null);
            return adaptee.cast(geometrySymbolizersWrapper);
        } else if (adaptee.isAssignableFrom(TextSymbolizerWrapper.class)) {
            return adaptee.cast(this);
        }
        return null;
    }

    public Graphic getGraphic() {
        return graphic;
    }

    // ///// GETTERS/SETTERS
    public void setSize( String size, boolean isProperty ) {
        this.size = size;
        if (isProperty) {
            graphic.setSize(ff.property(size));
        } else {
            graphic.setSize(ff.literal(size));
        }
    }

    public void setRotation( String rotation, boolean isProperty ) {
        this.rotation = rotation;
        if (isProperty) {
            graphic.setRotation(ff.property(rotation));
        } else {
            graphic.setRotation(ff.literal(rotation));
        }
    }

    public void setMarkName( String markName ) {
        if (markName == null || markName.equals("")) { //$NON-NLS-1$
            return;
        }
        this.markName = markName;
        if (mark == null) {
            mark = sf.createMark();
        }
        mark.setWellKnownName(ff.literal(markName));

        graphic.graphicalSymbols().clear();
        graphic.graphicalSymbols().add(mark);
    }

    public void setFillColor( String fillColor ) {
        if (fillColor == null) {
            hasFill = false;
        } else {
            hasFill = true;
        }
        this.fillColor = fillColor;
        checkFillExists();
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

    public void setHasStroke( boolean hasStroke ) {
        this.hasStroke = hasStroke;
        if (hasStroke) {
            checkStrokeExists();
        } else {
            stroke = null;
            mark.setStroke(null);
        }
    }

    public void setHasFill( boolean hasFill ) {
        this.hasFill = hasFill;
        if (hasFill) {
            checkFillExists();
        } else {
            fill = null;
            mark.setFill(null);
        }
    }

    private void checkStrokeExists() {
        if (stroke == null) {
            stroke = sf.createStroke(ff.literal(strokeColor), ff.literal(strokeWidth));
            if (mark != null) {
                mark.setStroke(stroke);
            }
        }
    }
    private void checkFillExists() {
        if (fill == null) {
            fill = sf.createFill(ff.literal(fillColor));
            if (mark != null) {
                mark.setFill(fill);
            }
        }
    }

    public void setStrokeWidth( String strokeWidth, boolean isProperty ) {
        this.strokeWidth = strokeWidth;
        checkStrokeExists();
        if (isProperty) {
            stroke.setWidth(ff.property(strokeWidth));
        } else {
            stroke.setWidth(ff.literal(strokeWidth));
        }
    }

    public void setStrokeColor( String strokeColor ) {
        this.strokeColor = strokeColor;
        checkStrokeExists();
        if (strokeColor == null) {
            hasStroke = false;
        } else {
            hasStroke = true;
        }
        stroke.setColor(ff.literal(strokeColor));
    }

    public void setStrokeOpacity( String strokeOpacity, boolean isProperty ) {
        this.strokeOpacity = strokeOpacity;
        checkStrokeExists();
        if (isProperty) {
            stroke.setOpacity(ff.property(strokeOpacity));
        } else {
            stroke.setOpacity(ff.literal(strokeOpacity));
        }
    }

    // getters
    public String getSize() {
        return size;
    }

    public String getRotation() {
        return rotation;
    }

    public String getMarkName() {
        return markName;
    }

    public String getFillColor() {
        return fillColor;
    }

    public String getFillOpacity() {
        return fillOpacity;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public String getStrokeOpacity() {
        return strokeOpacity;
    }

    public String getStrokeWidth() {
        return strokeWidth;
    }

    public boolean hasFill() {
        return hasFill;
    }

    public boolean hasStroke() {
        return hasStroke;
    }
}
