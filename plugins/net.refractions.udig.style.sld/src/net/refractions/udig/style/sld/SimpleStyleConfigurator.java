package net.refractions.udig.style.sld;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.style.sld.internal.Messages;
import net.refractions.udig.style.sld.simple.FillViewer;
import net.refractions.udig.style.sld.simple.GraphicViewer;
import net.refractions.udig.style.sld.simple.LabelViewer;
import net.refractions.udig.style.sld.simple.Mode;
import net.refractions.udig.style.sld.simple.ScaleViewer;
import net.refractions.udig.style.sld.simple.StrokeViewer;
import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureType;
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

/**
 * Defines a "simple" StyleConfigurator for working with SLD documents.
 * <p>
 * This style configurator is defined as follows:
 *
 * <pre><code>
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
 * </code></pre>
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
    Button pointMode;
    Button polyMode;
    Button lineMode;
    StrokeViewer line = new StrokeViewer();
    FillViewer fill = new FillViewer();
    GraphicViewer point = new GraphicViewer();
    LabelViewer label = new LabelViewer();
    ScaleViewer minScale = new ScaleViewer(ScaleViewer.MIN);
    ScaleViewer maxScale = new ScaleViewer(ScaleViewer.MAX);

    Mode mode;

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

    public Mode determineMode(FeatureType schema, boolean askUser){
        if (schema == null) {
            return Mode.NONE;
        }
        else if (SLD.isLine(schema)) {
            return Mode.LINE;
        }
        else if (SLD.isPolygon(schema) ) {
            return Mode.POLYGON;
        }
        else if (SLD.isPoint(schema)) {
            return Mode.POINT;
        }
        else {
            // we must be Geometry?
            if( askUser ){
                // could not figure it out from the schema
                // try trusting the user?
                if( polyMode.getSelection() ){
                    return Mode.POLYGON;
                }
                else if( lineMode.getSelection() ){
                    return Mode.LINE;
                }
                else if( pointMode.getSelection() ){
                    return Mode.POINT;
                }
            }
            return Mode.ALL; // we are a generic geometry
        }
    }
    @Override
    protected void refresh() {
        Style style = getStyle(); // grab an SLD style or bust

        // obtain the fts (if an FTS tagged with the SemanticTypeIdentifier "simple" doesn't exist,
        // null is fine)
        FeatureTypeStyle[] ftsList = style.getFeatureTypeStyles();
        FeatureTypeStyle fts = null;
        for( int i = 0; i < ftsList.length; i++ ) {
            if (SLDs.isSemanticTypeMatch(ftsList[i], "simple")) { //$NON-NLS-1$
                fts = ftsList[i];
            }
            // note: the last matching fts is returned (the one drawn last) -- although there should
            // only be one tagged "simple"
        }

        FeatureType schema = getLayer().getSchema();
        Stroke stroke = null;
        Fill fill = null;
        Graphic graphic = null;
        TextSymbolizer text = null;
        LabelPlacement placement = null;

        this.mode = determineMode( schema, true );

        text = SLDs.textSymbolizer(fts);
        if (text != null && placement != null) {
            text.setPlacement(placement);
        }
        if (mode == Mode.NONE) {
            pointMode.setSelection(false);
            polyMode.setSelection(false);
            lineMode.setSelection(false);
        } else if (mode == Mode.LINE) {
            lineMode.setSelection(true);
            LineSymbolizer sym = SLDs.lineSymbolizer(fts);
            stroke = SLDs.stroke(sym);
            placement = SLDs.getPlacement(SLDs.ALIGN_LEFT, SLDs.ALIGN_MIDDLE, 0);
        } else if (mode == Mode.POLYGON ){
            polyMode.setSelection(true);
            PolygonSymbolizer sym = SLDs.polySymbolizer(fts);
            stroke = SLDs.stroke(sym);
            fill = SLDs.fill(sym);
            placement = SLDs.getPlacement(SLDs.ALIGN_CENTER, SLDs.ALIGN_MIDDLE, 0);
        } else if (mode == Mode.POINT || mode == Mode.ALL) { // default to handling as Point
            pointMode.setSelection(true);
            PointSymbolizer sym = SLDs.pointSymbolizer(fts);
            stroke = SLDs.stroke(sym);
            fill = SLDs.fill(sym);
            graphic = SLDs.graphic(sym);
            placement = SLDs.getPlacement(SLDs.ALIGN_LEFT, SLDs.ALIGN_MIDDLE, 0);
        }
        Mode raw = determineMode( schema, false );
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
        this.minScale.setScale(minScaleDen, Math.round(getLayer().getMap().getViewportModel().getScaleDenominator()));
        this.maxScale.setScale(maxScaleDen, Math.round(getLayer().getMap().getViewportModel().getScaleDenominator()));
    }

    /** Synchronize the SLD with the array of symbolizers */
    @Override
    public void synchronize() {
        List<Symbolizer> acquire = new ArrayList<Symbolizer>();
        TextSymbolizer textSym = this.label.get(this.build);

        FeatureType schema = getLayer().getSchema();
        this.mode = determineMode(schema, true);

        switch( this.mode ) {
        case LINE:
            acquire.add(this.build.createLineSymbolizer(this.line.getStroke(this.build)));
            if (textSym != null) {
                acquire.add(textSym);
            }
            break;
        case POLYGON:
            acquire.add(this.build.createPolygonSymbolizer(this.line.getStroke(this.build),
                    this.fill.getFill(this.build)));
            if (textSym != null) {
                acquire.add(textSym);
            }
            break;
        case POINT:
            acquire.add(this.build.createPointSymbolizer(this.point.getGraphic(this.fill
                    .getFill(this.build), this.line.getStroke(this.build), this.build)));
            if (textSym != null) {
                acquire.add(textSym);
            }
            break;
        case ALL:
            acquire.add(this.build.createLineSymbolizer(this.line.getStroke(this.build)));
            acquire.add(this.build.createPolygonSymbolizer(this.line.getStroke(this.build),
                    this.fill.getFill(this.build)));
            acquire.add(this.build.createPointSymbolizer(this.point.getGraphic(this.fill
                    .getFill(this.build), this.line.getStroke(this.build), this.build)));
            if (textSym != null) {
                acquire.add(textSym);
            }
            break;
        case NONE:
        }
        double minScaleDen = minScale.getScale();
        double maxScaleDen = maxScale.getScale();

        Symbolizer[] array = acquire.toArray(new Symbolizer[acquire.size()]);
        Rule rule = this.build.createRule(array);
        if( minScale.isEnabled() )
            rule.setMinScaleDenominator(minScaleDen);
        if( maxScale.isEnabled() )
            rule.setMaxScaleDenominator(maxScaleDen);
        FeatureTypeStyle featureTypeStyle = this.build.createFeatureTypeStyle(SLDs.GENERIC_FEATURE_TYPENAME, rule);
        featureTypeStyle.setName("simple"); //$NON-NLS-1$
        featureTypeStyle.setSemanticTypeIdentifiers(new String[]{"generic:geometry", "simple"}); //$NON-NLS-1$ //$NON-NLS-2$

        Style style = (Style) getStyleBlackboard().get(SLDContent.ID);
        style.setDefault(true);
        if(replace.getSelection()){
            style.setFeatureTypeStyles(new FeatureTypeStyle[]{featureTypeStyle});
        }else{
            FeatureTypeStyle[] fts = style.getFeatureTypeStyles();
            boolean match = false;
            for( int i = fts.length - 1; i > -1; i-- ) {
                if (SLDs.isSemanticTypeMatch(fts[i], "simple")) { //$NON-NLS-1$
                    fts[i] = featureTypeStyle;
                    match = true;
                    break;
                }
            }
            if (match) {
                style.setFeatureTypeStyles(fts);
            } else {
                // add the new entry to the array
                FeatureTypeStyle[] fts2 = new FeatureTypeStyle[fts.length + 1];
                System.arraycopy(fts, 0, fts2, 0, fts.length);
                fts2[fts.length] = featureTypeStyle;
                style.setFeatureTypeStyles(fts2);
            }
        }
        // put style on blackboard
        getStyleBlackboard().put(SLDContent.ID, style);
        ((StyleBlackboard) getStyleBlackboard()).setSelected(new String[]{SLDContent.ID});
    }

    @Override
    public void createControl( Composite parent ) {
        setLayout(parent);
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
        Composite part = AbstractSimpleConfigurator.subpart(parent, Messages.SimpleStyleConfigurator_mode );
        this.pointMode = new Button(part, SWT.RADIO);
        pointMode.setText(Messages.SimpleStyleConfigurator_point);
        pointMode.addSelectionListener(synchronize);

        this.lineMode = new Button(part, SWT.RADIO);
        lineMode.setText(Messages.SimpleStyleConfigurator_line);
        lineMode.addSelectionListener(synchronize);

        this.polyMode = new Button(part, SWT.RADIO);
        polyMode.setText(Messages.SimpleStyleConfigurator_polygon);
        polyMode.addSelectionListener(synchronize);

        this.line.createControl(parent, adp);
        this.fill.createControl(parent, adp);
        this.point.createControl(parent, adp, this.build);
        this.label.createControl(parent, adp);
        this.minScale.createControl(parent, adp);
        this.maxScale.createControl(parent, adp);

        Composite replaceComp = AbstractSimpleConfigurator.subpart(parent, Messages.SimpleStyleConfigurator_replaceButton );
        this.replace = new Button(replaceComp, SWT.CHECK);
        replace.addSelectionListener(synchronize);
        replace.setSelection(true);
    }

}
