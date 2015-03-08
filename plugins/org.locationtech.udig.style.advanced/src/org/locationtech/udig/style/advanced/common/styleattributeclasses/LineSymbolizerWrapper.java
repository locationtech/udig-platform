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
import static org.locationtech.udig.style.advanced.utils.Utilities.DEFAULT_WIDTH;
import static org.locationtech.udig.style.advanced.utils.Utilities.ff;
import static org.locationtech.udig.style.advanced.utils.Utilities.getDashString;
import static org.locationtech.udig.style.advanced.utils.Utilities.getOffset;
import static org.locationtech.udig.style.advanced.utils.Utilities.sf;

import java.awt.geom.Point2D;
import java.util.List;

import org.geotools.filter.function.FilterFunction_endPoint;
import org.geotools.filter.function.FilterFunction_startPoint;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Stroke;
import org.geotools.styling.Symbolizer;
import org.locationtech.udig.style.advanced.utils.Utilities;
import org.opengis.filter.expression.Expression;
import org.opengis.style.GraphicalSymbol;

/**
 * A wrapper for a {@link LineSymbolizer} to ease interaction with gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class LineSymbolizerWrapper extends SymbolizerWrapper {
    protected String strokeColor;
    protected String strokeOpacity;
    protected String strokeWidth;

    protected boolean hasStroke;
    protected Stroke stroke;
    protected Graphic strokeGraphicStroke;
    protected String dash;
    protected String dashOffset;
    protected String lineCap;
    protected String lineJoin;

    
    protected PointSymbolizerWrapper endPointStyle;
    protected PointSymbolizerWrapper startPointStyle;
    
    public LineSymbolizerWrapper( PolygonSymbolizer polygonSymbolizer, RuleWrapper parent ) {
        super(polygonSymbolizer, parent);
        initEndPointSymbolizers();
    }

    public LineSymbolizerWrapper( Symbolizer symbolizer, RuleWrapper parent ) {
        super(symbolizer, parent);

        initEndPointSymbolizers();
        
        LineSymbolizer lineSymbolizer = (LineSymbolizer) symbolizer;

        // offset
        Point2D offset = getOffset(lineSymbolizer);
        if (offset != null) {
            xOffset = String.valueOf(offset.getX());
            yOffset = String.valueOf(offset.getY());
        } else {
            xOffset = DEFAULT_OFFSET;
            yOffset = DEFAULT_OFFSET;
        }

        stroke = lineSymbolizer.getStroke();
        if (stroke != null) {
            Expression color = stroke.getColor();
            strokeColor = expressionToString(color);
            Expression width = stroke.getWidth();
            strokeWidth = expressionToString(width);
            Expression opacity = stroke.getOpacity();
            strokeOpacity = expressionToString(opacity);

            if (strokeColor == null) {
                strokeColor = DEFAULT_COLOR;
                stroke.setColor(ff.literal(DEFAULT_COLOR));
            }
            if (strokeOpacity == null) {
                strokeOpacity = DEFAULT_OPACITY;
                stroke.setOpacity(ff.literal(DEFAULT_OPACITY));
            }
            if (strokeWidth == null) {
                strokeWidth = DEFAULT_WIDTH;
                stroke.setWidth(ff.literal(DEFAULT_WIDTH));
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
                dash = getDashString(dashArray);
            } else {
                dash = ""; //$NON-NLS-1$
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

    }
    
    private void initEndPointSymbolizers(){
    	for (Symbolizer x : super.getParent().getRule().getSymbolizers()){
        	if (x instanceof PointSymbolizer){
        		PointSymbolizer pnt = (PointSymbolizer) x;
        		Expression ex = pnt.getGeometry();
        		boolean endpnt = ex instanceof FilterFunction_endPoint;
        		boolean startpnt = ex instanceof FilterFunction_startPoint;
        		if (endpnt || startpnt){
        			GraphicalSymbol gs = pnt.getGraphic().graphicalSymbols().get(0);
        			if (gs instanceof Mark){
        				String name = ((Mark) gs).getWellKnownName().evaluate(null, String.class);
        				if (Utilities.lineEndStyles.values().contains(name)){
        					if (endpnt){
        						endPointStyle = new PointSymbolizerWrapper(pnt, super.getParent());
        					}else if (startpnt){
        						startPointStyle = new PointSymbolizerWrapper(pnt, super.getParent());
        					}
        				}
        			}
        		}
        	}
        }
    }
    
    public PointSymbolizerWrapper getEndPointStyle(){
    	return endPointStyle;
    }
    
    public PointSymbolizerWrapper getStartPointStyle(){
    	return startPointStyle;
    }
    
    public void setEndPointStyle(String geomName, String wkgName, String size, String color){
    	endPointStyle = updateEndpointStyle(geomName, endPointStyle, wkgName, size, color, false);
    }
    
    public void setStartPointStyle(String geomName, String wkgName, String size, String color){
    	startPointStyle = updateEndpointStyle(geomName, startPointStyle, wkgName, size, color, true);
    }
    
    private PointSymbolizerWrapper updateEndpointStyle(String geomName, PointSymbolizerWrapper wrapper, String wkgName, String size, String color, boolean isStart){
    	if (wkgName == null || wkgName.length() == 0){
    		if (wrapper != null){
    			getParent().getRule().symbolizers().remove(wrapper.getSymbolizer());
    			return null;
    		}
    	}
    	if (wrapper == null){
    		PointSymbolizer p = sf.createPointSymbolizer();
    		if (isStart){
    			p.setGeometry(ff.function("startPoint", ff.property(geomName))); //$NON-NLS-1$
    			p.getGraphic().setRotation(ff.add(ff.function("startAngle", ff.property(geomName)), ff.literal(-180)));	//rotate start 180 degrees //$NON-NLS-1$
    		}else{
    			p.setGeometry(ff.function("endPoint", ff.property(geomName))); //$NON-NLS-1$
    			p.getGraphic().setRotation(ff.function("endAngle", ff.property(geomName)));	//$NON-NLS-1$
    		}
    		wrapper = new PointSymbolizerWrapper(p, getParent());
    		
    		getParent().getRule().symbolizers().add(wrapper.getSymbolizer());
    	}
    	wrapper.setMarkName(wkgName);
    	wrapper.setStrokeColor(color);
    	wrapper.setFillColor(color);
    	wrapper.setSize(size, false);
    	return wrapper;
    }
    
    

    public void clearGraphicStroke(){
    	if (stroke == null) return;
   		stroke.setGraphicStroke(null);
   		strokeGraphicStroke = null;
    }
    
    public Graphic getStrokeGraphicStroke() {
        return strokeGraphicStroke;
    }

    public void setStrokeGraphicStroke( Graphic strokeGraphicStroke ) {
        this.strokeGraphicStroke = strokeGraphicStroke;
        if (hasStroke){
        	checkStrokeExists();

        	stroke.setGraphicStroke(strokeGraphicStroke);
        }
    }

    // ///// GETTERS/SETTERS
    public void setHasStroke( boolean hasStroke ) {
        this.hasStroke = hasStroke;
        if (hasStroke) {
            checkStrokeExists();
        } else {
            stroke = null;
            LineSymbolizer lineSymbolizer = (LineSymbolizer) getSymbolizer();
            lineSymbolizer.setStroke(null);
        }
    }

    protected void checkStrokeExists() {
        if (stroke == null) {
            if (strokeColor == null) {
                strokeColor = DEFAULT_COLOR;
            }
            if (strokeWidth == null) {
                strokeWidth = DEFAULT_WIDTH;
            }
            stroke = sf.createStroke(ff.literal(strokeColor), ff.literal(strokeWidth));
            LineSymbolizer lineSymbolizer = (LineSymbolizer) getSymbolizer();
            lineSymbolizer.setStroke(stroke);
            strokeGraphicStroke = stroke.getGraphicStroke();
        }
    }

    public void setStrokeWidth( String strokeWidth, boolean isProperty ) {
        this.strokeWidth = strokeWidth;
        if (hasStroke){
        	checkStrokeExists();
        	if (isProperty) {
        		stroke.setWidth(ff.property(strokeWidth));
        	} else {
        		stroke.setWidth(ff.literal(strokeWidth));
        	}
        }
    }

    public void setStrokeColor( String strokeColor, boolean isProperty ) {
        this.strokeColor = strokeColor;
        if (hasStroke){
        	checkStrokeExists();
        	if (isProperty) {
        		stroke.setColor(ff.property(strokeColor));
        	} else {
        		if (strokeColor != null) {
        			stroke.setColor(ff.literal(strokeColor));
        		}
            }
        }
    }

    public void setStrokeOpacity( String strokeOpacity, boolean isProperty ) {
        this.strokeOpacity = strokeOpacity;
        if (hasStroke){
        	checkStrokeExists();
        	if (isProperty) {
        		stroke.setOpacity(ff.property(strokeOpacity));
        	} else {
        		stroke.setOpacity(ff.literal(strokeOpacity));
        	}
        
        	//update end point styles if applicable
        	if (endPointStyle != null){
        		endPointStyle.setStrokeOpacity(strokeOpacity, isProperty);
        		endPointStyle.setFillOpacity(strokeOpacity, isProperty);
        	}
        	if (startPointStyle != null){
        		startPointStyle.setStrokeOpacity(strokeOpacity, isProperty);
        		startPointStyle.setFillOpacity(strokeOpacity, isProperty);
        	}
        }
    }

    public void setDash( String dash ) {
        this.dash = dash;
        if (hasStroke){
        	checkStrokeExists();
        	float[] dashArray = Utilities.getDash(dash);
        	stroke.setDashArray(dashArray);
        }
    }

    public void setDashOffset( String dashOffset ) {
        this.dashOffset = dashOffset;
        if (hasStroke){
        	checkStrokeExists();
        	if (dashOffset != null && dashOffset.length() > 0) {
        		stroke.setDashOffset(ff.literal(dashOffset));
        	}
        }
    }

    public void setLineCap( String lineCap ) {
        this.lineCap = lineCap;
        if (hasStroke){
        	checkStrokeExists();
        	stroke.setLineCap(ff.literal(lineCap));
        }
    }

    public void setLineJoin( String lineJoin ) {
        this.lineJoin = lineJoin;
        if (hasStroke){
        	checkStrokeExists();
        	stroke.setLineJoin(ff.literal(lineJoin));
        }
    }

    // getters
    public String getStrokeColor() {
        return strokeColor;
    }

    public String getStrokeOpacity() {
        return strokeOpacity;
    }

    public String getStrokeWidth() {
        return strokeWidth;
    }

    public boolean hasStroke() {
        return hasStroke;
    }

    public String getDash() {
        return dash;
    }

    public String getDashOffset() {
        return dashOffset;
    }

    public String getLineCap() {
        return lineCap;
    }

    public String getLineJoin() {
        return lineJoin;
    }

}
