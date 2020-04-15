/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.geotools.data.FeatureSource;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LabelPlacement;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.style.sld.internal.Messages;
import org.locationtech.udig.style.sld.simple.FillViewer;
import org.locationtech.udig.style.sld.simple.GraphicViewer;
import org.locationtech.udig.style.sld.simple.LabelViewer;
import org.locationtech.udig.style.sld.simple.Mode;
import org.locationtech.udig.style.sld.simple.ScaleViewer;
import org.locationtech.udig.style.sld.simple.StrokeViewer;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.style.SemanticType;

/**
 * Defines a "simple" StyleConfigurator for working with SLD documents.
 * <p>
 * This style configurator is defined as follows:
 * 
 * <pre>
 * &lt;code&gt;
 *         Mode: (*) Point ( ) Line ( ) Polygon
 *               +-+ +-------+ +------+ +------+
 *         Line: |x| | color | |size\/| |100%\/|
 *           	 +-+ +-------+ +------+ +------+
 *           	 +-+ +-------+ +------+             
 *         Fill: |x| | color | | 90%\/| 
 *           	 +-+ +-------+ +------+
 *           	 +-+ +----------------+ +------+
 *        Label: |x| |         title\/| | Font |
 *           	 +-+ +----------------+ +------+
 *           	 +-+ +-------+ +------+
 *        Point: |x| | star\/| |size\/|
 *           	 +-+ +-------+ +------+
 *               +-+ +-------------+
 * Min scale d.: |x| |      scale\/|
 *               +-+ +-------------+
 *               +-+ +-------------+
 * Max scale d.: |x| |      scale\/|
 *               +-+ +-------------+
 * &lt;/code&gt;
 * </pre>
 * 
 * </p>
 * Where:
 * <ul>
 * <li>Mode is used to switch between Point / Line / Polygon
 * <li>Line is used for: <br>
 * LineString: line color, width, opacity <br>
 * Polygon: border color, width, opacity <br>
 * Point: border color, width, opacity
 * <li>Fill is used for Polygon or Point fill color, opacity
 * <li>Label is used to choose attribute and set font (the only dialog)
 * <li>Point is used to set the marker type and size
 * <li>Min/max scale denominator define at which scale the layer is visible
 * </ul>
 * </p>
 * <p>
 * Notes:
 * <ul>
 * <li>RasterSymbolizer is handled by its own thing, as is WMS etc...
 * <li>Layout as per the SLDEditorPart examples - so we can take advantage of more or less room.
 * <li>Presets is a good idea, just not here
 * <li>Apply/Revert buttons to be green/red
 * <li>Advanced (edit the SLD) can be in the view menu
 * <li>If possible replace color button with a drop down list (may not be possible)
 * </ul>
 * </P>
 * <p>
 * We will do our best to make this thing reusable on an Array of Symbolizers.
 * </p>
 * 
 * @author Jody Garnett
 * @since 1.0.0
 */
public class SimpleStyleConfigurator extends AbstractSimpleConfigurator {
    private static final String DEFAULT_GEOMETRY = "(default)"; //$NON-NLS-1$

    /**
     * Viewer capturing the geometry name; may be "default" or an explicit geometryName provided by
     * the user
     */
    ComboViewer geometryName;

    /** Radio button used to indicate point geometry type */
    Button pointMode;

    /** Radio button used to indicate polygon geometry type */
    Button polyMode;

    /** Radio button used to indicate linestring geometry type */
    Button lineMode;

    /** Viewer used to allow interaction with Stroke definition */
    StrokeViewer line = new StrokeViewer();

    /** Viewer used to allow interaction with Fill definition */
    FillViewer fill = new FillViewer();

    /** Viewer used to allow interaction with Graphic definition */
    GraphicViewer point = new GraphicViewer();

    /** Viewer used to allow interaction with TextSymbolizer definition */
    LabelViewer label = new LabelViewer();

    /** Viewer used to allow interaction with minScale definition */
    ScaleViewer minScale = new ScaleViewer(ScaleViewer.MIN);

    /** Viewer used to allow interaction with maxScale definition */
    ScaleViewer maxScale = new ScaleViewer(ScaleViewer.MAX);

    /** The current mode we are working with */
    Mode mode;

    /**
     * Used to respond to any widget selection event; will call synchronize() method to extract any
     * changes of state from the user interface
     */
    SelectionListener synchronize = new SelectionListener(){
        public void widgetSelected( SelectionEvent e ) {
            synchronize();
        }
        public void widgetDefaultSelected( SelectionEvent e ) {
            synchronize();
        }
    };

    private Button replace;
    /**
     * Construct <code>SimpleStyleConfigurator</code>.
     */
    public SimpleStyleConfigurator() {
        super();
        this.line.addListener(this.synchronize);
        this.fill.addListener(this.synchronize);
        this.label.addListener(this.synchronize);
        this.point.addListener(this.synchronize);
        this.minScale.addListener(this.synchronize);
        this.maxScale.addListener(this.synchronize);
    }

    @Override
    public boolean canStyle( Layer aLayer ) {
        if (aLayer.hasResource(FeatureSource.class))
            return true;
        return false;
    }

    public Mode determineMode( SimpleFeatureType schema, boolean askUser ) {
        if (schema == null) {
            return Mode.NONE;
        } else if (SLD.isLine(schema)) {
            return Mode.LINE;
        } else if (SLD.isPolygon(schema)) {
            return Mode.POLYGON;
        } else if (SLD.isPoint(schema)) {
            return Mode.POINT;
        } else {
            // we must be Geometry?
            if (askUser) {
                // could not figure it out from the schema
                // try trusting the user?
                if (polyMode.getSelection()) {
                    return Mode.POLYGON;
                } else if (lineMode.getSelection()) {
                    return Mode.LINE;
                } else if (pointMode.getSelection()) {
                    return Mode.POINT;
                }
            }
            return Mode.ALL; // we are a generic geometry
        }
    }
    @Override
    protected void refresh() {
        Style style = getStyle(); // grab an SLD style or bust

        List<FeatureTypeStyle> ftsList = style.featureTypeStyles();
        FeatureTypeStyle fts = null;
        if (ftsList.size()>0) {
            fts  = ftsList.get(0);
        }

        SimpleFeatureType schema = getLayer().getSchema();
        geometryName.setInput(schema);
        String name = DEFAULT_GEOMETRY;

        Stroke stroke = null;
        Fill fill = null;
        Graphic graphic = null;
        TextSymbolizer text = null;
        LabelPlacement placement = null;

        List<Rule> rules = fts.rules();
        if (rules.size() > 1) {
            // simple mode trimms away all but the first rule
            Rule keepRule = rules.get(0);
            rules.clear();
            rules.add(keepRule);
        }
        this.mode = determineMode(schema, true);

        if (mode == Mode.NONE) {
            pointMode.setSelection(false);
            polyMode.setSelection(false);
            lineMode.setSelection(false);
        } else if (mode == Mode.LINE) {
            lineMode.setSelection(true);
            LineSymbolizer sym = SLDs.lineSymbolizer(fts);
            stroke = SLDs.stroke(sym);
            placement = SLDs.getPlacement(SLDs.ALIGN_LEFT, SLDs.ALIGN_MIDDLE, 0);

            name = sym == null ? null : sym.getGeometryPropertyName();
        } else if (mode == Mode.POLYGON) {
            polyMode.setSelection(true);
            PolygonSymbolizer sym = SLDs.polySymbolizer(fts);
            stroke = SLDs.stroke(sym);
            fill = SLDs.fill(sym);
            placement = SLDs.getPlacement(SLDs.ALIGN_CENTER, SLDs.ALIGN_MIDDLE, 0);

            name = sym == null ? null : sym.getGeometryPropertyName();
        } else if (mode == Mode.POINT || mode == Mode.ALL) { // default to handling as Point
            pointMode.setSelection(true);

            PointSymbolizer sym = SLDs.pointSymbolizer(fts);
            stroke = SLDs.stroke(sym);
            fill = SLDs.fill(sym);
            graphic = SLDs.graphic(sym);
            placement = SLDs.getPlacement(SLDs.ALIGN_LEFT, SLDs.ALIGN_MIDDLE, 0);

            name = sym == null ? null : sym.getGeometryPropertyName();
        }
        
        text = SLDs.textSymbolizer(fts);
        if (text != null && placement != null) {
            text.setLabelPlacement(placement);
        }
        
        if (name == null) {
            name = DEFAULT_GEOMETRY;
            geometryName.getCombo().setText(name);
        }
        else {
            geometryName.getCombo().setText(name);    
        }
        Mode raw = determineMode(schema, false);
        pointMode.setEnabled(raw == Mode.ALL);
        polyMode.setEnabled(raw == Mode.ALL);
        lineMode.setEnabled(raw == Mode.ALL);

        double minScaleDen = SLDs.minScale(fts);
        double maxScaleDen = SLDs.maxScale(fts);
        Color defaultColor = getLayer().getDefaultColor();

        this.line.setStroke(stroke, this.mode, defaultColor);

        this.fill.setFill(fill, this.mode, defaultColor);
        this.point.setGraphic(graphic, this.mode, defaultColor);
        
        this.label.set(schema, text, this.mode);
        this.minScale.setScale(minScaleDen, Math.round(getLayer().getMap().getViewportModel()
                .getScaleDenominator()));
        this.maxScale.setScale(maxScaleDen, Math.round(getLayer().getMap().getViewportModel()
                .getScaleDenominator()));
    }

    /** Synchronize the SLD with the array of symbolizers */
    @Override
    public void synchronize() {
        List<Symbolizer> acquire = new ArrayList<Symbolizer>();
        TextSymbolizer textSym = this.label.get(this.build);

        SimpleFeatureType schema = getLayer().getSchema();
        this.mode = determineMode(schema, true);

        String geometryPropertyName = null;
        if (geometryName.getCombo().getSelectionIndex() != 0) {
            geometryPropertyName = geometryName.getCombo().getText();
        }

        switch( this.mode ) {
        case LINE: {
            LineSymbolizer lineSymbolizer = this.build.createLineSymbolizer(this.line
                    .getStroke(this.build));
            acquire.add(lineSymbolizer);
            lineSymbolizer.setGeometryPropertyName(geometryPropertyName);
            if (textSym != null) {
                acquire.add(textSym);
            }
        }
            break;

        case POLYGON: {
            PolygonSymbolizer polygonSymbolizer = this.build.createPolygonSymbolizer(this.line
                    .getStroke(this.build), this.fill.getFill(this.build));
            polygonSymbolizer.setGeometryPropertyName(geometryPropertyName);
            acquire.add(polygonSymbolizer);
            if (textSym != null) {
                acquire.add(textSym);
            }
        }
            break;

        case POINT: {
            PointSymbolizer pointSymbolizer = this.build.createPointSymbolizer(this.point
                    .getGraphic(this.fill.getFill(this.build), this.line.getStroke(this.build),
                            this.build));
            pointSymbolizer.setGeometryPropertyName(geometryPropertyName);
            acquire.add(pointSymbolizer);
            if (textSym != null) {
                acquire.add(textSym);
            }
        }
            break;
        case ALL: {
            LineSymbolizer lineSymbolizer = this.build.createLineSymbolizer(this.line
                    .getStroke(this.build));
            acquire.add(lineSymbolizer);
            acquire.add(lineSymbolizer);
            PolygonSymbolizer polygonSymbolizer = this.build.createPolygonSymbolizer(this.line
                    .getStroke(this.build), this.fill.getFill(this.build));
            polygonSymbolizer.setGeometryPropertyName(geometryPropertyName);
            acquire.add(polygonSymbolizer);
            PointSymbolizer pointSymbolizer = this.build.createPointSymbolizer(this.point
                    .getGraphic(this.fill.getFill(this.build), this.line.getStroke(this.build),
                            this.build));
            pointSymbolizer.setGeometryPropertyName(geometryPropertyName);
            acquire.add(pointSymbolizer);
            if (textSym != null) {
                acquire.add(textSym);
            }
        }
            break;
        case NONE:
        }
        double minScaleDen = minScale.getScale();
        double maxScaleDen = maxScale.getScale();

        Symbolizer[] array = acquire.toArray(new Symbolizer[acquire.size()]);
        Rule rule = this.build.createRule(array);
        if (minScale.isEnabled())
            rule.setMinScaleDenominator(minScaleDen);
        if (maxScale.isEnabled())
            rule.setMaxScaleDenominator(maxScaleDen);
        FeatureTypeStyle featureTypeStyle = this.build.createFeatureTypeStyle(
                SLDs.GENERIC_FEATURE_TYPENAME, rule);
        featureTypeStyle.setName("simple"); //$NON-NLS-1$
        featureTypeStyle.semanticTypeIdentifiers().add(new SemanticType("generic:geometry")); //$NON-NLS-1$
        featureTypeStyle.semanticTypeIdentifiers().add(new SemanticType("simple")); //$NON-NLS-1$

        Style style = (Style) getStyleBlackboard().get(SLDContent.ID);
        style.setDefault(true);
        if (replace.getSelection()) {
            // if repalce was hit we are going to completly redfine the style
            // based on what the user has here
            //
            style.featureTypeStyles().clear();
            style.featureTypeStyles().add(featureTypeStyle);
        } else {
            // if we are just responding to what is going on we will try and update the existing
            // style in place (leaving any other content alone)
            //
            List<FeatureTypeStyle> fts = style.featureTypeStyles();
            boolean match = false;
            for( int i = fts.size() - 1; i > -1; i-- ) {
                if (SLDs.isSemanticTypeMatch(fts.get(i), "simple")) { //$NON-NLS-1$
                    fts.set(i, featureTypeStyle);
                    match = true;
                    break;
                }
            }
            if (match) {
                style.featureTypeStyles().clear();
                style.featureTypeStyles().addAll(fts);
            } else {
                // add the new entry to the array
                List<FeatureTypeStyle> fts2 = new ArrayList<FeatureTypeStyle>(fts);
                Collections.copy(fts2, fts);
                fts2.add(featureTypeStyle);
                style.featureTypeStyles().clear();
                style.featureTypeStyles().addAll(fts2);
            }
        }
        // put style on blackboard
        getStyleBlackboard().put(SLDContent.ID, style);
        ((StyleBlackboard) getStyleBlackboard()).setSelected(new String[]{SLDContent.ID});
    }

    @Override
    public void createControl( Composite parent ) {
        setLayout(parent);
        //ensure vertical layout
        ((RowLayout)parent.getLayout()).type = SWT.VERTICAL;
        ((RowLayout)parent.getLayout()).spacing = 3;
        
        
        KeyAdapter adp = new KeyAdapter(){
            @Override
            public void keyReleased( KeyEvent e ) {
                /*
                 * I don't like having different ways of checking for keypad enter and the normal
                 * one. Using the keyCode would be better, but I couldn't readily find the value for
                 * CR.
                 */
                if (e.keyCode == SWT.KEYPAD_CR || e.character == SWT.CR) {
                    makeActionDoStuff();
                }
            }
        };
        Composite part = AbstractSimpleConfigurator.subpart(parent, Messages.SimpleStyleConfigurator_GeometryLabel);
        geometryName = new ComboViewer(part);
        geometryName.setContentProvider(new IStructuredContentProvider(){
            FeatureType schema;
            public Object[] getElements( Object inputElement ) {
                // note use of descriptors; so we can make use of associations if available
                ArrayList<String> names = new ArrayList<String>();
                names.add(DEFAULT_GEOMETRY);
                if (schema != null) {
                    for( PropertyDescriptor descriptor : schema.getDescriptors() ) {
                        if (descriptor instanceof GeometryDescriptor) {
                            names.add(((GeometryDescriptor) descriptor).getLocalName());
                        }
                    }
                }
                return names.toArray();
            }
            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
                // we don't really care since we are not listening to the change in schema
                schema = (FeatureType) newInput;
            }
            public void dispose() {
            }
        });
        geometryName.getCombo().setText(DEFAULT_GEOMETRY);
        geometryName.getCombo().addSelectionListener( synchronize );

        
        part = AbstractSimpleConfigurator.subpart(parent, Messages.SimpleStyleConfigurator_ModeLabel);
        this.pointMode = new Button(part, SWT.RADIO);
        pointMode.setText(Messages.SimpleStyleConfigurator_PointMode);
        this.lineMode = new Button(part, SWT.RADIO);
        lineMode.setText(Messages.SimpleStyleConfigurator_LineMode);
        this.polyMode = new Button(part, SWT.RADIO);
        polyMode.setText(Messages.SimpleStyleConfigurator_PolygonMode);

        this.line.createControl(parent, adp);
        this.fill.createControl(parent, adp);
        this.point.createControl(parent, adp, this.build);
        this.label.createControl(parent, adp);
        this.minScale.createControl(parent, adp);
        this.maxScale.createControl(parent, adp);

        Composite replaceComp = AbstractSimpleConfigurator.subpart(parent,
                Messages.SimpleStyleConfigurator_replaceButton);
        this.replace = new Button(replaceComp, SWT.CHECK);
        replace.addSelectionListener(synchronize);
        replace.setSelection(true);
    }

}
