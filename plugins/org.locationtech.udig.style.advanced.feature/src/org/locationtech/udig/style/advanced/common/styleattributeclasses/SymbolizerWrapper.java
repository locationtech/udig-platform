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

import static org.locationtech.udig.style.advanced.utils.Utilities.getFormat;
import static org.locationtech.udig.style.advanced.utils.Utilities.sb;
import static org.locationtech.udig.style.advanced.utils.Utilities.sf;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.LiteralExpressionImpl;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.Graphic;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.geotools.util.factory.GeoTools;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.style.advanced.utils.Utilities;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;

/**
 * An abstract wrapper for a {@link Symbolizer} to ease interaction with gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public abstract class SymbolizerWrapper {

    private Symbolizer symbolizer;

    /**
     * The {@link ExternalGraphic} of the {@link Symbolizer}.
     * 
     * <p>Currently just one is supported.
     */
    protected ExternalGraphic externalGraphic;

    /**
     * The stroke's {@link ExternalGraphic} stroke of the {@link Symbolizer}.
     */
    protected ExternalGraphic strokeExternalGraphicStroke;

    /**
     * The stroke's {@link ExternalGraphic} fill of the {@link Symbolizer}.
     * 
     * <p><b>This is not used at the moment.</b></p>
     */
    protected ExternalGraphic strokeExternalGraphicFill;

    /**
     * The fill's {@link ExternalGraphic} stroke of the {@link Symbolizer}.
     * 
     * <p><b>This is not used at the moment.</b></p>
     */
    protected ExternalGraphic fillExternalGraphicStroke;

    /**
     * The fill's {@link ExternalGraphic} fill of the {@link Symbolizer}.
     * 
     * <p>Currently just one is supported.
     */
    protected ExternalGraphic fillExternalGraphicFill;

    protected String xOffset;

    protected String yOffset;

    private final RuleWrapper parent;

    protected SymbolizerWrapper( Symbolizer symbolizer, RuleWrapper parent ) {
        this.symbolizer = symbolizer;
        this.parent = parent;
    }

    public RuleWrapper getParent() {
        return parent;
    }

    public Symbolizer getSymbolizer() {
        return symbolizer;
    }

    public boolean isTextSymbolizer() {
        return symbolizer instanceof TextSymbolizer;
    }

    public <T> T adapt( Class<T> adaptee ) {
        if (adaptee.isAssignableFrom(PointSymbolizerWrapper.class) && this instanceof PointSymbolizerWrapper) {
            return adaptee.cast(this);
        } else if (adaptee.isAssignableFrom(LineSymbolizerWrapper.class) && this instanceof LineSymbolizerWrapper) {
            return adaptee.cast(this);
        } else if (adaptee.isAssignableFrom(PolygonSymbolizerWrapper.class) && this instanceof PolygonSymbolizerWrapper) {
            return adaptee.cast(this);
        } else if (adaptee.isAssignableFrom(TextSymbolizerWrapper.class) && this instanceof TextSymbolizerWrapper) {
            return adaptee.cast(this);
        }
        return null;
    }

    /**
     * Tests if the {@link SymbolizerWrapper} bases on an {@link ExternalGraphic}.
     * 
     * <p>This is used for point styles.
     * 
     * @return true if the {@link ExternalGraphic} is != null.
     */
    public boolean hasExternalGraphic() {
        return externalGraphic != null;
    }

    /**
     * Tests if the stroke's {@link SymbolizerWrapper} bases on an {@link ExternalGraphic}.
     * 
     * <p>This is used for lines.
     * 
     * @return true if the {@link ExternalGraphic} is != null.
     */
    public boolean hasStrokeExternalGraphicStroke() {
        return strokeExternalGraphicStroke != null;
    }

    /**
     * Tests if the fill's {@link SymbolizerWrapper} bases on an {@link ExternalGraphic}.
     * 
     * <p>This is used for polygon fills.
     * 
     * @return true if the {@link ExternalGraphic} is != null.
     */
    public boolean hasFillExternalGraphicFill() {
        return fillExternalGraphicFill != null;
    }

    /**
     * Get the {@link ExternalGraphic}'s path.
     * 
     * <p>Currently one {@link ExternalGraphic} per {@link Symbolizer} is supported.
     * 
     * <p>This is used for point styles.
     * 
     * @return the graphic's path.
     * @throws MalformedURLException
     */
    public String getExternalGraphicPath() throws MalformedURLException {
        return getExternalGraphicPath(externalGraphic);
    }

    /**
     * Get the stroke's {@link ExternalGraphic} stroke path.
     * 
     * <p>Currently one stroke {@link ExternalGraphic} per {@link Symbolizer} is supported.
     * 
     * <p>This is used for lines.
     * 
     * @return the graphic's path.
     * @throws MalformedURLException
     */
    public String getStrokeExternalGraphicStrokePath() throws MalformedURLException {
        return getExternalGraphicPath(strokeExternalGraphicStroke);
    }

    /**
     * Get the fill's {@link ExternalGraphic} fill path.
     * 
     * Currently one fill {@link ExternalGraphic} per {@link Symbolizer} is supported.
     * 
     * <p>This is used for polygon fills.
     * 
     * @return the graphic's path.
     * @throws MalformedURLException
     */
    public String getFillExternalGraphicFillPath() throws MalformedURLException {
        return getExternalGraphicPath(fillExternalGraphicFill);
    }

    private String getExternalGraphicPath( ExternalGraphic extGraphic ) throws MalformedURLException {
        if (extGraphic == null) {
            return ""; //$NON-NLS-1$
        }
        URL location = extGraphic.getLocation();
        String urlString = location.toExternalForm();
        if (urlString.startsWith("http://")) { //$NON-NLS-1$
            return urlString;
        } else {
            File urlToFile = URLUtils.urlToFile(location);
            if (urlString.equals("file:")) { //$NON-NLS-1$
                return ""; //$NON-NLS-1$
            } else {
                return urlToFile.getAbsolutePath();
            }
        }
    }

    /**
     * Set the {@link ExternalGraphic}'s path.
     * 
     * <p>Currently one {@link ExternalGraphic} per {@link Symbolizer} is supported.
     * 
     * <p>This is used for point styles.
     * 
     * @param externalGraphicPath the path to set.
     * @throws MalformedURLException
     */
    public void setExternalGraphicPath( String externalGraphicPath ) throws MalformedURLException {
        if (externalGraphic == null) {
            PointSymbolizerWrapper pointSymbolizerWrapper = adapt(PointSymbolizerWrapper.class);
            if (pointSymbolizerWrapper != null) {
                Graphic graphic = pointSymbolizerWrapper.getGraphic();
                graphic.graphicalSymbols().clear();
                String urlStr = externalGraphicPath;
                if (!externalGraphicPath.startsWith("http:") && !externalGraphicPath.startsWith("file:")) { //$NON-NLS-1$ //$NON-NLS-2$
                    urlStr = "file:" + externalGraphicPath; //$NON-NLS-1$
                }
                externalGraphic = sb.createExternalGraphic(new URL(urlStr), getFormat(externalGraphicPath));
                graphic.graphicalSymbols().add(externalGraphic);
            }
        }
        setExternalGraphicPath(externalGraphicPath, externalGraphic);
    }

    /**
     * Set the stroke's {@link ExternalGraphic} path.
     * 
     * <p>Currently one {@link ExternalGraphic} per {@link Symbolizer} is supported.
     * 
     * <p>This is used for lines.
     * 
     * @param externalGraphicPath the path to set.
     * @throws MalformedURLException
     */
    public void setStrokeExternalGraphicStrokePath( String externalGraphicPath ) throws MalformedURLException {
        if (strokeExternalGraphicStroke == null) {
            Graphic graphic = null;
            LineSymbolizerWrapper lineSymbolizerWrapper = adapt(LineSymbolizerWrapper.class);
            PolygonSymbolizerWrapper polygonSymbolizerWrapper = adapt(PolygonSymbolizerWrapper.class);
            if (lineSymbolizerWrapper != null) {
                graphic = lineSymbolizerWrapper.getStrokeGraphicStroke();
                if (graphic == null) {
                    graphic = sb.createGraphic();
                    lineSymbolizerWrapper.setStrokeGraphicStroke(graphic);
                }
            } else if (polygonSymbolizerWrapper != null) {
                graphic = polygonSymbolizerWrapper.getStrokeGraphicStroke();
                if (graphic == null) {
                    graphic = sb.createGraphic();
                    polygonSymbolizerWrapper.setStrokeGraphicStroke(graphic);
                }
            } else {
                return;
            }

            graphic.graphicalSymbols().clear();
            String urlStr = externalGraphicPath;
            if (!externalGraphicPath.startsWith("http:") && !externalGraphicPath.startsWith("file:")) { //$NON-NLS-1$ //$NON-NLS-2$
                urlStr = "file:" + externalGraphicPath; //$NON-NLS-1$
            }
            strokeExternalGraphicStroke = sb.createExternalGraphic(new URL(urlStr), getFormat(externalGraphicPath));
            graphic.graphicalSymbols().add(strokeExternalGraphicStroke);
        } else {
            setExternalGraphicPath(externalGraphicPath, strokeExternalGraphicStroke);
        }
    }

    /**
     * Set the fill's {@link ExternalGraphic} path.
     * 
     * <p>Currently one {@link ExternalGraphic} per {@link Symbolizer} is supported.
     * 
     * <p>This is used for polygons.
     * 
     * @param externalGraphicPath the path to set.
     * @throws MalformedURLException
     */
    public void setFillExternalGraphicFillPath( String externalGraphicPath, double size ) throws MalformedURLException {
        Graphic graphic = null;
        PolygonSymbolizerWrapper polygonSymbolizerWrapper = adapt(PolygonSymbolizerWrapper.class);
        if (polygonSymbolizerWrapper != null) {
            graphic = polygonSymbolizerWrapper.getFillGraphicFill();
            if (graphic == null) {
                graphic = sf.createDefaultGraphic();
            }
            polygonSymbolizerWrapper.setFillGraphicFill(graphic);
        } else {
            return;
        }
        graphic.graphicalSymbols().clear();
        String urlStr = externalGraphicPath;
        if (!externalGraphicPath.startsWith("http:") && !externalGraphicPath.startsWith("file:")) { //$NON-NLS-1$ //$NON-NLS-2$
            urlStr = "file:" + externalGraphicPath; //$NON-NLS-1$
        }
        if (fillExternalGraphicFill == null) {
            fillExternalGraphicFill = sb.createExternalGraphic(new URL(urlStr), getFormat(externalGraphicPath));
        } else {
            setExternalGraphicPath(externalGraphicPath, fillExternalGraphicFill);
        }
        graphic.graphicalSymbols().add(fillExternalGraphicFill);
        
        FilterFactory ff = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        graphic.setSize(ff.literal(size));
    }

    private void setExternalGraphicPath( String externalGraphicPath, ExternalGraphic extGraphic ) throws MalformedURLException {
        URL url = null;
        File f = new File(externalGraphicPath);
        String format = Utilities.getFormat(externalGraphicPath);
        if (!f.exists()) {
            if (externalGraphicPath.startsWith("http://") || externalGraphicPath.startsWith("file:")) { //$NON-NLS-1$ //$NON-NLS-2$
                url = new URL(externalGraphicPath);
            }
        }
        if (url == null) {
            url = f.toURI().toURL();
            if (externalGraphicPath.equals("")) { //$NON-NLS-1$
                url = new URL("file:"); //$NON-NLS-1$
            }
        }
        extGraphic.setLocation(url);
        extGraphic.setFormat(format);
    }

    protected String expressionToString( Expression expression ) {
    	if (expression == null){
    		return null;
    	}
        if (expression instanceof PropertyName) {
            PropertyName pName = (PropertyName) expression;
            return pName.getPropertyName();
        }

        if (expression instanceof LiteralExpressionImpl) {
            LiteralExpressionImpl pName = (LiteralExpressionImpl) expression;
            return pName.getValue().toString();
        }

        Integer evaluateInt = expression.evaluate(null, Integer.class);
        if (evaluateInt != null) {
            return evaluateInt.toString();
        }

        Double evaluateDouble = expression.evaluate(null, Double.class);
        if (evaluateDouble != null) {
            return evaluateDouble.toString();
        }

        String evaluateString = expression.evaluate(null, String.class);
        if (evaluateString != null) {
            return evaluateString;
        }
        return null;
    }

    public void setOffset( String xOffset, String yOffset ) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        Utilities.setOffset(symbolizer, xOffset + "," + yOffset); //$NON-NLS-1$
    }

    public void setOffset( String offset ) {
        if (offset.indexOf(',') == -1) {
            return;
        }
        String[] split = offset.split(","); //$NON-NLS-1$
        if (split.length != 2) {
            return;
        }
        this.xOffset = split[0];
        this.yOffset = split[1];
        setOffset(xOffset, yOffset);
    }

    public String getxOffset() {
        return xOffset;
    }

    public String getyOffset() {
        return yOffset;
    }

}
