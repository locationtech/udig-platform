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
package org.locationtech.udig.style.sld.editor;

import java.awt.Color;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.ColorBrewer;
import org.geotools.brewer.color.PaletteSuitability;
import org.geotools.brewer.color.PaletteType;
import org.geotools.brewer.color.SampleScheme;
import org.geotools.brewer.color.StyleGenerator;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.feature.visitor.UniqueVisitor;
import org.geotools.filter.IllegalFilterException;
import org.geotools.filter.function.ClassificationFunction;
import org.geotools.filter.function.Classifier;
import org.geotools.filter.function.EqualIntervalFunction;
import org.geotools.filter.function.QuantileFunction;
import org.geotools.filter.function.StandardDeviationFunction;
import org.geotools.filter.function.UniqueIntervalFunction;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.Symbolizer;
import org.geotools.util.NullProgressListener;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.style.internal.StyleLayer;
import org.locationtech.udig.style.sld.ImageConstants;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.style.sld.SLDPlugin;
import org.locationtech.udig.style.sld.editor.BorderColorComboListener.Outline;
import org.locationtech.udig.style.sld.editor.CustomDynamicPalette.TABLE;
import org.locationtech.udig.style.sld.editor.internal.StyleTreeContentProvider;
import org.locationtech.udig.style.sld.editor.internal.StyleTreeLabelProvider;
import org.locationtech.udig.style.sld.editor.internal.StyleTreeSorter;
import org.locationtech.udig.style.sld.internal.Messages;
import org.locationtech.udig.ui.DecoratorOverlayIcon;
import org.locationtech.udig.ui.ErrorManager;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.graphics.SLDs;
import org.locationtech.udig.ui.graphics.TableSettings;
import org.locationtech.udig.ui.graphics.TableUtils;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.util.ProgressListener;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;

/**
 * Page for editing a style Theme.
 */
public class StyleThemePage extends StyleEditorPage {

    private static final int COMBO_ATTRIBUTES = 1;
    static final int COMBO_CLASSES = 2;
    static final int COMBO_BREAKTYPE = 3;
    private static final int COMBO_NORMALIZE = 4;
    private static final int COMBO_ELSE = 5;
    static final int COMBO_PALETTES = 6;
    private static final int COMBO_OPACITY = 7;
    private static final int COMBO_BORDER_COLOR = 8;

    private static final int LABEL_SEPARATOR_BOTTOM = 12;
    private static final int LABEL_STATUSBAR = 13;
    
    private static final int LABEL_ICON_COLORBLIND = 14;
    private static final int LABEL_ICON_PHOTOCOPY = 15;
    private static final int LABEL_ICON_PROJECTOR = 16;
    private static final int LABEL_ICON_LCD = 17;
    private static final int LABEL_ICON_CRT = 18;
    private static final int LABEL_ICON_PRINT = 19;
    
    static final int BUTTON_COLORBLIND = 20;
    static final int BUTTON_PHOTOCOPY = 21;
    static final int BUTTON_PROJECTOR = 22;
    static final int BUTTON_LCD = 23;
    static final int BUTTON_CRT = 24;
    static final int BUTTON_PRINT = 25;
    private static final int BUTTON_REMOVE = 26;
    
    private static final int COMPOSITE_PARENT = 30;
    private static final int COMPOSITE_TOP = 31;
    private static final int COMPOSITE_MIDDLE = 32;
    private static final int COMPOSITE_BOTTOM = 33;
    private static final int COMPOSITE_BOTTOM_LEFT = 34;
    
    // Key for saving the opacity in the layer's blackboard.  String formatted 
    // red-green-blue
    private static final String BORDER_COLOR_KEY = "theme_page_border_color"; //$NON-NLS-1$
    // Key for saving the opacity in the layer's blackboard.  Integer
    private static final String OPACITY_KEY = "theme_page_opacity";
    // Key for saving the AttributeDescriptor in the layer's blackboard.  String
    private static final String ATTRIBUTE_KEY = "theme_page_attribute";
    // Key for saving the Number of classes in the layer's blackboard.  Integer
    private static final String CLASSES_KEY = "theme_page_classes";
    // Key for saving the BreakType in the layer's blackboard.  Integer
    private static final String BREAK_KEY = "theme_page_break";
    // Key for saving the normalize attribute in the layer's blackboard.  String
    private static final String NORMALIZE_KEY = "theme_page_normalize";
    // Key for saving the else in the layer's blackboard.  Boolean
    private static final String ELSE_KEY = "theme_page_else";
    // Key for saving the Palette category in the layer's blackboard.  String
    private static final String PALETTE_CATEGORY_KEY = "theme_page_palette_category";
    // Key for saving the Colorblind in the layer's blackboard.  Boolean
    private static final String COLOR_BLIND_KEY = "theme_page_color_blind";
    // Key for saving the CRT in the layer's blackboard.  Boolean
    private static final String CRT_KEY = "theme_page_crt";
    // Key for saving the Projector in the layer's blackboard.  Boolean
    private static final String PROJECTOR_KEY = "theme_page_projector";
    // Key for saving the LCD in the layer's blackboard.  Boolean
    private static final String LCD_KEY = "theme_page_lcd";
    // Key for saving the Printing in the layer's blackboard.  Boolean
    private static final String PRINT_KEY = "theme_page_print";
    // Key for saving the photocopy in the layer's blackboard.  Boolean
    private static final String PHOTO_COPY_KEY = "theme_page_copy";
    // Key for saving the Palette in the layer's blackboard.  String (palette name)
    private static final String PALETTE_KEY = "theme_page_palette";
    // Key for memento which contains the custom palette's colors
    private static final String CUSTOM_PALETTE = "custom_palette_key";


    private HashMap<Integer,Control> pageControls = new HashMap<Integer,Control>();
    private List<String> numericAttr = new ArrayList<String>();
    private double[] opacity;    

    private HashMap<Integer, String> controlNames = new HashMap<Integer, String>();
    private HashMap<Integer, Integer> viewerQuality = new HashMap<Integer, Integer>();
    private HashMap<String, Integer> uniqueCounts = new HashMap<String, Integer>();
    
    private ColorBrewer brewer;
    private boolean reverseColours = false;
    private StyleGenerator sg;
    private AttributeDescriptor selectedAttributeType;
    private AttributeDescriptor normalize;
    private ClassificationFunction function;
    private Classifier classifier;
    private TableSettings tableSettings;
    private boolean elseSelection = true;
    
    private FilterFactory2 ff;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> collection;
    private  FeatureSource<SimpleFeatureType, SimpleFeature> source;
    private StyleBuilder sb;

    TableViewer paletteTable;
    TreeViewer treeViewer;
    BrewerPalette customPalette = null;
    
    /**
     * Classifier is produced by running a classification function over a FeatureCollection.
     * <p>
     * The Classifer contains "Summary" or "Histogram" information about the FeatureCollection and
     * is used as a basis for style generation.
     */
    //ExplicitClassifier customBreak = null;
    Classifier customBreak = null;
    
    public StyleThemePage() {
        //create factories
        ff = (FilterFactory2) CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        sb = new StyleBuilder(ff);
        
        //suitability icons
        controlNames.put(LABEL_ICON_COLORBLIND, Messages.StyleEditor_theme_suitability_colour); 
        controlNames.put(LABEL_ICON_CRT, Messages.StyleEditor_theme_suitability_crt); 
        controlNames.put(LABEL_ICON_LCD, Messages.StyleEditor_theme_suitability_lcd); 
        controlNames.put(LABEL_ICON_PHOTOCOPY, Messages.StyleEditor_theme_suitability_pcopy); 
        controlNames.put(LABEL_ICON_PRINT, Messages.StyleEditor_theme_suitability_print); 
        controlNames.put(LABEL_ICON_PROJECTOR, Messages.StyleEditor_theme_suitability_proj); 

        //suitability toggle buttons
        controlNames.put(BUTTON_COLORBLIND, Messages.StyleEditor_theme_suitability_colour); 
        controlNames.put(BUTTON_CRT, Messages.StyleEditor_theme_suitability_crt); 
        controlNames.put(BUTTON_LCD, Messages.StyleEditor_theme_suitability_lcd); 
        controlNames.put(BUTTON_PHOTOCOPY, Messages.StyleEditor_theme_suitability_pcopy); 
        controlNames.put(BUTTON_PRINT, Messages.StyleEditor_theme_suitability_print); 
        controlNames.put(BUTTON_PROJECTOR, Messages.StyleEditor_theme_suitability_proj); 

        //suitability icon - palette suitability lookup
        viewerQuality.put(PaletteSuitability.VIEWER_COLORBLIND, LABEL_ICON_COLORBLIND);
        viewerQuality.put(PaletteSuitability.VIEWER_CRT, LABEL_ICON_CRT);
        viewerQuality.put(PaletteSuitability.VIEWER_LCD, LABEL_ICON_LCD);
        viewerQuality.put(PaletteSuitability.VIEWER_PHOTOCOPY, LABEL_ICON_PHOTOCOPY);
        viewerQuality.put(PaletteSuitability.VIEWER_PRINT, LABEL_ICON_PRINT);
        viewerQuality.put(PaletteSuitability.VIEWER_PROJECTOR, LABEL_ICON_PROJECTOR);
    }
    
    public Button getButton(int widgetID) {
        return (Button) getControl(widgetID);
    }
    
    public Combo getCombo(int widgetID) {
        return (Combo) getControl(widgetID);
    }

    public Composite getComposite(int widgetID) {
        return (Composite) getControl(widgetID);
    }
    
    public Label getLabel(int widgetID) {
        return (Label) getControl(widgetID);
    }

    public GridData getLayoutData(int widgetID) {
        Object widget = getControl(widgetID);
        Object layout = null;
        if (widget instanceof Composite) {
            layout = ((Composite) widget).getLayoutData();
        }
        if (layout instanceof GridData) return (GridData) layout;
        else return new GridData(); //return empty, or throw exception (null)?
    }
    
    public Object getControl(int widgetID) {
        return pageControls.get(widgetID);
    }
    
    private boolean isNumber(String attributeType) {
        return numericAttr.contains(attributeType);
    }
    
    private boolean isNumber(AttributeDescriptor attributeType) {
        if (Number.class.isAssignableFrom(attributeType.getType().getBinding())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public boolean okToLeave() {
        return true;
    }

    @Override
    public boolean performCancel() {
        return true;
    }

    public boolean performOk() {
        return true;
    }

    @Override
    public void dispose() {
        super.dispose();
        //collection.purge();
        collection = null;
        source = null;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    public ColorBrewer getBrewer() {
        if (brewer == null) {
            createBrewer();
            
            // add custom palettes
            List<BrewerPalette> palettesList = CustomPalettesLoader.PALETTESLIST;
            for( BrewerPalette brewerPalette : palettesList ) {
                brewer.registerPalette(brewerPalette);
            }
            // add a dynamic one that support everythings
            TABLE[] dynamicPalettes = CustomDynamicPalette.TABLE.values();
            for( TABLE colorTable : dynamicPalettes ) {
                CustomDynamicPalette customDynamicPalette = new CustomDynamicPalette(colorTable);
                brewer.registerPalette(customDynamicPalette);
            }
        }
        return brewer;
    }
    
    private void createToggleButton(Composite parent, int buttonId, Image image) {
        Button toggleButton = new Button(parent, SWT.TOGGLE);
        GridData gridData = new GridData(SWT.NONE, SWT.NONE, false, false);
        toggleButton.setLayoutData(gridData);
        toggleButton.setImage(image);
        toggleButton.setSelection(false);
        toggleButton.addSelectionListener(new SuitabilityToggleListener(buttonId));
        pageControls.put(buttonId, toggleButton);
        updateToggleTooltip(buttonId);
    }
    
    private void updateToggleTooltip(int buttonId) {
        Button toggle = getButton(buttonId);
        String tooltip;
        if (toggle.getSelection()) {
            tooltip = Messages.StyleEditor_theme_suitability_show; 
        } else {
            tooltip = Messages.StyleEditor_theme_suitability_hide; 
        }
        toggle.setToolTipText(tooltip+" "+controlNames.get(buttonId)); //$NON-NLS-1$
    }
    
    private void createSuitabilityIcon(Composite parent, int id, String tooltip, Image image) {
        Label suitabilityIcon = new Label(parent, SWT.NONE);
        GridData gridData = new GridData(SWT.NONE, SWT.NONE, false, false);
        suitabilityIcon.setLayoutData(gridData);
        suitabilityIcon.setImage(image);
        suitabilityIcon.setToolTipText(tooltip);
        pageControls.put(id, suitabilityIcon);
    }
    
    @Override
    public void createPageContent( Composite parent2 ) {
        Composite parent = new Composite(parent2, SWT.NONE);
        parent.setLayout(new GridLayout());
        GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData2.heightHint = 500;
        parent.setLayoutData(gridData2);
        //create the featureCollection
        IProgressMonitor monitor = new NullProgressMonitor();
        try {
            source = getSelectedLayer().getResource(FeatureSource.class, monitor); //monitor
            if (source == null) return;
            collection = source.getFeatures();
        } catch (IOException e) {
            SLDPlugin.log("StyleThemePage.createPageContent() failed to create FeatureCollection", e); //$NON-NLS-1$
            return;
        }

        //create the content
        pageControls.put(COMPOSITE_PARENT, parent);
        Font font = getShell().getFont();
        
		createTopComponent(parent, font);
        
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
        separator.setLayoutData(gridData);
        separator.setFont(font);
        
        createPaletteChooserComposite(parent, font);

        separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
        separator.setLayoutData(gridData);
        separator.setVisible(false);
        separator.setFont(font);
        pageControls.put(LABEL_SEPARATOR_BOTTOM, separator);

        createPaletteEditorComposite(parent, font);

        //create status bar
        Label status = new Label(parent, SWT.NONE);
        gridData = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        status.setLayoutData(gridData);
        status.setVisible(true);
        status.setFont(font);
        status.setText(""); //$NON-NLS-1$
        pageControls.put(LABEL_STATUSBAR, status);
    }

	/**
	 * Creates the composite for modifying the palette
	 */
	private Composite createPaletteEditorComposite(Composite parent, Font font) {
		GridLayout layout;
		GridData gridData;
		Composite compBottom = new Composite(parent, SWT.NONE);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.heightHint = 0;
        compBottom.setLayoutData(gridData);
        layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        compBottom.setLayout(layout);
        compBottom.setVisible(false);
        compBottom.setFont(font);
        pageControls.put(COMPOSITE_BOTTOM, compBottom);
        
        Composite compBottomLeft = new Composite(compBottom, SWT.NONE);
        compBottomLeft.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        compBottomLeft.setLayout(layout);
        pageControls.put(COMPOSITE_BOTTOM_LEFT, compBottomLeft);
        
        Composite compBottomLeftButtons = new Composite(compBottomLeft, SWT.NONE);
        compBottomLeftButtons.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
        layout = new GridLayout(4, true);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        compBottomLeftButtons.setLayout(layout);
        
        createPaletteEditorControls(font, compBottomLeftButtons);
        
        createPaletteEditorTreeViewer(compBottomLeft, font); 

        CellEditor[] editors = createCellEditors();
        
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged( SelectionChangedEvent event ) {
                
                if (((StructuredSelection) event.getSelection()).isEmpty()) {
                    getButton(BUTTON_REMOVE).setEnabled(false);
                } else {
                    getButton(BUTTON_REMOVE).setEnabled(true);
                }
                
            }
            
        });
        
        treeViewer.setCellModifier(new IPaletteCellEditor(this));
        treeViewer.setColumnProperties(new String[] {"colour", "title", "styleExpr"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        treeViewer.setCellEditors(editors);
        //populate the tree
        
        Composite compBottomRight = new Composite(compBottom, SWT.NONE);
        compBottomRight.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
        layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        compBottomRight.setLayout(layout);
        
        createSuitabilityDisplay(compBottomRight); 
        
        
		return compBottom;
	}

	/**
	 * Creates the widgets that display the what situations the current palette is suitable for.
	 */
	private void createSuitabilityDisplay(Composite compBottomRight) {
		GridLayout layout;
		Label labelSuitability = new Label(compBottomRight, SWT.NONE);
        labelSuitability.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
        labelSuitability.setText(Messages.StyleEditor_theme_suitability); 
        
        Composite icons = new Composite(compBottomRight, SWT.RIGHT);
        icons.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
        layout = new GridLayout(3, true);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        icons.setLayout(layout);

        createSuitabilityIcon(icons, LABEL_ICON_COLORBLIND, Messages.StyleEditor_theme_suitability_colour, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.COLORBLIND_ICON).createImage()); 
        createSuitabilityIcon(icons, LABEL_ICON_CRT, Messages.StyleEditor_theme_suitability_crt, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.CRT_ICON).createImage()); 
        createSuitabilityIcon(icons, LABEL_ICON_PROJECTOR, Messages.StyleEditor_theme_suitability_proj, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.PROJECTOR_ICON).createImage()); 
        createSuitabilityIcon(icons, LABEL_ICON_LCD, Messages.StyleEditor_theme_suitability_lcd, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.LAPTOP_ICON).createImage()); 
        createSuitabilityIcon(icons, LABEL_ICON_PRINT, Messages.StyleEditor_theme_suitability_print, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.PRINTER_ICON).createImage()); 
        createSuitabilityIcon(icons, LABEL_ICON_PHOTOCOPY, Messages.StyleEditor_theme_suitability_pcopy, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.PHOTOCOPY_ICON).createImage());
	}

	private CellEditor[] createCellEditors() {
		CellEditor[] editors = new CellEditor[3];

        //TODO: create a nicer color chooser
        CellEditor celledit0 = new ColorCellEditor(treeViewer.getTree());
        TextCellEditor celledit1 = new TextCellEditor(treeViewer.getTree()); 
        TextCellEditor celledit2 = new TextCellEditor(treeViewer.getTree()); 
        editors[0] = celledit0;
        editors[1] = celledit1;
        editors[2] = celledit2;
		return editors;
	}

	/**
	 * @param compBottomLeft
	 * @param font 
	 * @return
	 */
	private Tree createPaletteEditorTreeViewer(Composite compBottomLeft, Font font) {
		GridData gridData;
		treeViewer = new TreeViewer(compBottomLeft, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        Tree treeTable = treeViewer.getTree();
        treeTable.setHeaderVisible(true);
        treeTable.setLinesVisible(true);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.widthHint = compBottomLeft.getBounds().x;
        gridData.heightHint = compBottomLeft.getBounds().y;
        treeTable.setLayoutData(gridData);
        
        TreeColumn colImage = new TreeColumn(treeTable, SWT.LEFT);
        colImage.setText(Messages.StyleEditor_theme_column_colour); 

        TreeColumn colTitle = new TreeColumn(treeTable, SWT.LEFT);
        colTitle.setText(Messages.StyleEditor_theme_column_label); 
        
        TreeColumn colExpr = new TreeColumn(treeTable, SWT.LEFT);
        colExpr.setText(Messages.StyleEditor_theme_column_expression);

        treeTable.layout();
        
        tableSettings = new TableSettings(treeTable);
        tableSettings.setColumnMin(0, 40);
        tableSettings.setColumnMin(1, 100);
        tableSettings.setColumnMin(0, 50);
        
        treeViewer.setLabelProvider(new StyleTreeLabelProvider());
        treeViewer.setSorter(new StyleTreeSorter());
        treeViewer.setContentProvider(new StyleTreeContentProvider());
        
        treeViewer.setInput(getFTS());
        treeViewer.expandAll();
        Control treeControl = treeViewer.getControl();
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        treeControl.setLayoutData(gd);
        treeControl.setFont(font);
        treeControl.setVisible(false);
		return treeTable;
	}

	/**
	 * Creates the Opacity Reverse and Remove controls
	 */
	private void createPaletteEditorControls(Font font,
			Composite compBottomLeftButtons) {
        Composite combos = new Composite(compBottomLeftButtons, SWT.NONE);
        combos.setLayout(new GridLayout(2,false));
        createOpacityControls(font, combos);
        createBorderControls(font, combos);
		createReverseButton(compBottomLeftButtons);
		createRemoveButton(compBottomLeftButtons);
	}

	/**
	 * @param compBottomLeftButtons
	 */
	private void createRemoveButton(Composite compBottomLeftButtons) {
		Button removeButton = new Button(compBottomLeftButtons, SWT.RIGHT);
        removeButton.setText(Messages.StyleEditor_theme_remove); 
        removeButton.setEnabled(false);
        removeButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected( SelectionEvent e ) {
                StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
                if (selection.isEmpty()) return;
                Rule rule = (Rule) selection.getFirstElement();
                if (removeRule(rule)) {
                    treeViewer.refresh();
                } else {
                    //freak out
                }
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }
        
        });
        pageControls.put(BUTTON_REMOVE, removeButton);
	}

	/**
	 * @param compBottomLeftButtons
	 */
	private void createReverseButton(Composite compBottomLeftButtons) {
		Button reverseButton = new Button(compBottomLeftButtons, SWT.RIGHT);
        reverseButton.setText(Messages.StyleEditor_theme_reverse); 
        reverseButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected( SelectionEvent e ) {
                reverseColours = !reverseColours; //used by generateTheme to get things half right
                FeatureTypeStyle fts = getFTS();
                List<Rule> ruleList = fts.rules();
                for (int i = 0; i < (ruleList.size() / 2); i++) {
                    swapColours(ruleList.get(i), ruleList.get(ruleList.size()-i-1));
                }
                treeViewer.refresh();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }
        
        });
	}

    private void createBorderControls( Font font, Composite compBottomLeftButtons ) {
        GridData gridData;
        Label opacityLabel = new Label(compBottomLeftButtons, SWT.NONE);
        opacityLabel.setFont(font);
        opacityLabel.setText("Outline"); 
        gridData = new GridData(SWT.LEFT, SWT.DEFAULT, false, false);
        opacityLabel.setLayoutData(gridData);

        Combo colorCombo = new Combo(compBottomLeftButtons, SWT.BORDER | SWT.READ_ONLY);
        pageControls.put(COMBO_BORDER_COLOR, colorCombo);
        String[] items = BorderColorComboListener.Outline.labels();
        colorCombo.setItems(items);
        colorCombo.select(0);
        boolean polygonCompatible = isPolygonCompatible();
        colorCombo.setEnabled(polygonCompatible);
        if( polygonCompatible ){
            colorCombo.addSelectionListener(new BorderColorComboListener(this));
        }
    }
    
    private boolean isPolygonCompatible() {
        Class<?> type = getSelectedLayer().getSchema().getGeometryDescriptor().getType().getBinding();
        if( LineString.class.isAssignableFrom(type) || MultiLineString.class.isAssignableFrom(type)
                || LinearRing.class.isAssignableFrom(type)){
            return false;
        }
        return true;
    }
	/**
	 * @param font
	 * @param compBottomLeftButtons
	 */
	private void createOpacityControls(Font font,
			Composite compBottomLeftButtons) {
		GridData gridData;
		Label opacityLabel = new Label(compBottomLeftButtons, SWT.NONE);
        opacityLabel.setFont(font);
        opacityLabel.setText(Messages.StyleEditor_theme_opacity); 
        gridData = new GridData(SWT.LEFT, SWT.DEFAULT, false, false);
        opacityLabel.setLayoutData(gridData);

        Combo opacityCombo = new Combo(compBottomLeftButtons, SWT.BORDER | SWT.READ_ONLY);
        pageControls.put(COMBO_OPACITY, opacityCombo);
        opacity = new double[11];
        for (int i = 0; i < 11; i++) {
            opacityCombo.add(Integer.toString(i*10)+"%"); //$NON-NLS-1$
            opacity[i] = i / 10.0;
        }
        opacityCombo.select(5); //default 50%
        opacityCombo.setVisibleItemCount(8);
        opacityCombo.addSelectionListener(new SimpleComboListener());
	}

    /**
     * Creates the composite that allows the user to choose the palette
     */
	private Composite createPaletteChooserComposite(Composite parent, Font font) {
		GridLayout layout;
		
		Composite compMiddle = new Composite(parent, SWT.NONE);
        compMiddle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        compMiddle.setLayout(layout);
        compMiddle.setFont(font);
        pageControls.put(COMPOSITE_MIDDLE, compMiddle);

        GridData gridData;
		Composite compMiddle2 = createPaletteLabel(font, compMiddle);
        
        Composite compMiddle3 = new Composite(compMiddle2, SWT.NONE);
        compMiddle3.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, true, false));
        layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        compMiddle3.setLayout(layout);

        createPaletteFilterComposite(font, compMiddle3);
        
        //list of matching palettes
        paletteTable = new TableViewer(new Table(compMiddle, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION));
        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1, 20, false));
        TableColumn firstColumn = new TableColumn(paletteTable.getTable(), SWT.LEFT);
        firstColumn.setAlignment(SWT.LEFT);
        paletteTable.getTable().setLayout(tableLayout);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 2;
        gridData.heightHint = 150;
        gridData.widthHint = 175;
        paletteTable.getControl().setLayoutData(gridData);

        paletteTable.setLabelProvider(new BrewerPaletteLabelProvider());
        paletteTable.setContentProvider(new BrewerPaletteContentProvider(this));

        paletteTable.addFilter(new BrewerPaletteViewerFilter(this));
        paletteTable.setSorter(new BrewerPaletteViewerSorter());
        
        paletteTable.setInput(getBrewer());
        paletteTable.addSelectionChangedListener(new PalettesListener());
        
		return compMiddle;
	}

	/**
	 * Creates the palette type chooser and the buttons to filter for parameters such as color blindness.
	 */
	private void createPaletteFilterComposite(Font font, Composite compMiddle3) {
		GridLayout layout;
		GridData gridData;
		Label paletteFilterLabel = new Label(compMiddle3, SWT.LEFT);
        paletteFilterLabel.setFont(font);
        paletteFilterLabel.setText(Messages.StyleEditor_theme_show); 
        gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
        gridData.verticalSpan = 2;
        paletteFilterLabel.setLayoutData(gridData);
        
        Combo paletteFilter = new Combo(compMiddle3, SWT.BORDER | SWT.READ_ONLY);
        pageControls.put(COMBO_PALETTES, paletteFilter);
        paletteFilter.add(Messages.StyleEditor_theme_palette_all, 0); 
        paletteFilter.add(Messages.StyleEditor_theme_palette_num, 1); 
        paletteFilter.add(Messages.StyleEditor_theme_palette_seq, 2); 
        paletteFilter.add(Messages.StyleEditor_theme_palette_div, 3); 
        paletteFilter.add(Messages.StyleEditor_theme_palette_cat, 4); 
        paletteFilter.select(0);
        paletteFilter.setToolTipText(Messages.StyleEditor_theme_palette_tip); 
        paletteFilter.addSelectionListener(new SelectionListener() {

            public void widgetSelected( SelectionEvent e ) {
                paletteTable.refresh();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }
            
        });

        Composite compMiddle4 = new Composite(compMiddle3, SWT.NONE);
        compMiddle4.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, true, false));
        layout = new GridLayout(6, true);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 0;
        compMiddle4.setLayout(layout);
        
        //toggle buttons
        createToggleButton(compMiddle4, BUTTON_COLORBLIND, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.COLORBLIND_ICON).createImage());
        createToggleButton(compMiddle4, BUTTON_CRT, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.CRT_ICON).createImage());
        createToggleButton(compMiddle4, BUTTON_PROJECTOR, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.PROJECTOR_ICON).createImage());
        createToggleButton(compMiddle4, BUTTON_LCD, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.LAPTOP_ICON).createImage());
        createToggleButton(compMiddle4, BUTTON_PRINT, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.PRINTER_ICON).createImage());
        createToggleButton(compMiddle4, BUTTON_PHOTOCOPY, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.PHOTOCOPY_ICON).createImage());
	}

	private Composite createPaletteLabel(Font font, Composite compMiddle) {
		GridLayout layout;
		Composite compMiddle2 = new Composite(compMiddle, SWT.NONE);
        compMiddle2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        compMiddle2.setLayout(layout);

        Label palettesLabel = new Label(compMiddle2, SWT.LEFT);
        palettesLabel.setFont(font);
        palettesLabel.setText(Messages.StyleEditor_theme_palette); 
        GridData gridData = new GridData(SWT.LEFT, SWT.BOTTOM, true, false);
        palettesLabel.setLayoutData(gridData);
		return compMiddle2;
	}

	/**
	 * Creates the composite that contains the information about what attributes to theme by
	 * and the type of theme.
	 */
	private Composite createTopComponent(Composite parent, Font font) {
		Composite compTop = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        compTop.setLayoutData(gridData);
        GridLayout layout = new GridLayout(5, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        compTop.setLayout(layout);
        compTop.setFont(font);
        pageControls.put(COMPOSITE_TOP, compTop);
        createAttributeLabels(font, compTop);
        
        createAttributeCombos(compTop);
        
        
		return compTop;
	}

	/**
	 * Creates all the labels for the Attribute composite 
	 */
	private void createAttributeLabels(Font font, Composite compTop) {
		Label attributesLabel = new Label(compTop, SWT.NONE);
        attributesLabel.setFont(font);
        attributesLabel.setText(Messages.StyleEditor_theme_attribute); 
        attributesLabel.setLayoutData(createDefaultGridData());

        Label classesLabel = new Label(compTop, SWT.NONE);
        classesLabel.setFont(font);
        classesLabel.setText(Messages.StyleEditor_theme_classes); 
        classesLabel.setLayoutData(createDefaultGridData());

        Label breaksLabel = new Label(compTop, SWT.NONE);
        breaksLabel.setFont(font);
        breaksLabel.setText(Messages.StyleEditor_theme_break); 
        breaksLabel.setLayoutData(createDefaultGridData());

        Label normalizeLabel = new Label(compTop, SWT.LEFT);
        normalizeLabel.setFont(font);
        normalizeLabel.setText(Messages.StyleEditor_theme_normalize); 
        normalizeLabel.setLayoutData(createDefaultGridData());

        Label elseLabel = new Label(compTop, SWT.LEFT);
        elseLabel.setFont(font);
        elseLabel.setText(Messages.StyleEditor_theme_else); 
        elseLabel.setLayoutData(createDefaultGridData());
	}

	/**
	 * @return
	 */
	private GridData createDefaultGridData() {
		return new GridData(SWT.LEFT, SWT.DEFAULT, true, false);
	}

	/**
	 * Create the Combos for selecting and configuring how the themeing will work
	 */
	private void createAttributeCombos(Composite compTop) {
		GridData gridData;
		Combo attributeCombo = new Combo(compTop, SWT.BORDER | SWT.READ_ONLY);
        pageControls.put(COMBO_ATTRIBUTES, attributeCombo);
        attributeCombo.setLayout(new GridLayout());
        gridData = createDefaultGridData();
        attributeCombo.setLayoutData(gridData);
        //populate the comboBox
        StyleLayer selectedLayer = getSelectedLayer();
        if (selectedLayer != null) {
            loadWithAttributeTypes(attributeCombo, selectedLayer);
        }
        attributeCombo.setVisibleItemCount(16);
        attributeCombo.addListener(SWT.Modify, new AttributeComboListener());
        
        Combo classesCombo = new Combo(compTop, SWT.BORDER);
        pageControls.put(COMBO_CLASSES, classesCombo);
        classesCombo.setLayout(new GridLayout(1, false));
        gridData = createDefaultGridData();
        classesCombo.setLayoutData(gridData);
        
        for (int i = 2; i < 13; i++) {
            Integer j = (Integer) i;
            classesCombo.add(j.toString());
        }
        
        classesCombo.select(3); //default is 5 classes
        classesCombo.setVisibleItemCount(16);
        classesCombo.addListener(SWT.Modify, new ClassesComboListener());

        Combo breaksCombo = new Combo(compTop, SWT.BORDER | SWT.READ_ONLY);
        pageControls.put(COMBO_BREAKTYPE, breaksCombo);
        breaksCombo.setLayout(new GridLayout(1, false));
        gridData = createDefaultGridData();
        breaksCombo.setLayoutData(gridData);
        updateBreaks();
        breaksCombo.addSelectionListener(new SimpleComboListener());

        Combo normalizeCombo = new Combo(compTop, SWT.BORDER | SWT.READ_ONLY);
        pageControls.put(COMBO_NORMALIZE, normalizeCombo);
        gridData = createDefaultGridData();
        normalizeCombo.setLayoutData(gridData);
        updateNormalize();
        normalizeCombo.setVisibleItemCount(10);
        normalizeCombo.addSelectionListener(new SimpleComboListener());

        Combo elseCombo = new Combo(compTop, SWT.BORDER | SWT.READ_ONLY);
        pageControls.put(COMBO_ELSE, elseCombo);
        elseCombo.setLayout(new GridLayout(1, false));
        gridData = createDefaultGridData();
        elseCombo.setLayoutData(gridData);
        elseCombo.add(Messages.StyleEditor_theme_else_hide); 
        elseCombo.add(Messages.StyleEditor_theme_else_min); 
        elseCombo.add(Messages.StyleEditor_theme_else_max); 
        elseCombo.select(0);
        elseCombo.setToolTipText(Messages.StyleEditor_theme_else_tip); 
        elseCombo.addSelectionListener(new ElseComboListener());
	}

	private void loadWithAttributeTypes(Combo attributeCombo,
			StyleLayer selectedLayer) {
		SimpleFeatureType featureType = selectedLayer.getSchema();

		if (featureType != null) {
                    List<String> attributesList = new ArrayList<String>();
                    for (int i = 0; i < featureType.getAttributeCount(); i++) {
                        AttributeDescriptor attributeType = featureType.getDescriptor(i);
                        if (!(attributeType instanceof GeometryDescriptor)) { // don't include the geometry
                            attributesList.add(attributeType.getName().getLocalPart());
                            if (isNumber(attributeType)) {
                                numericAttr.add(attributeType.getName().getLocalPart());
                            }
                        }
                    }

                    // sort alphabetical
                    Collections.sort(attributesList);
                    attributeCombo.removeAll();
                    attributeCombo.setItems(attributesList.toArray(new String[] {}));

		    //select the first numeric attribute that isn't ID
		    int index = -1;
		    if (numericAttr.size() > 1) {
		        if (numericAttr.get(0).equalsIgnoreCase("id")) { //$NON-NLS-1$
		            index = attributeCombo.indexOf(numericAttr.get(1));
		        } else {
		            index = attributeCombo.indexOf(numericAttr.get(0));
		        }
		    } else if (numericAttr.size() == 1) {
		        index = attributeCombo.indexOf(numericAttr.get(0));
		    }
		    if (index > -1) attributeCombo.select(index);
		    if (attributeCombo.getSelectionIndex() == -1) {
		        //we couldn't find a desirable attribute, just grab the first one 
		        if (featureType.getAttributeCount() > 0) {
		            attributeCombo.select(0);
		        }
		    }
		}
	}
    private boolean removeRule(Rule rule) {
        Style style = getStyle();
        FeatureTypeStyle[] featureTypeStyles = style.getFeatureTypeStyles();
        if( featureTypeStyles == null ){
            return false;
        }
        for( int i=0; i<featureTypeStyles.length; i++ ){
            FeatureTypeStyle featureTypeStyle = featureTypeStyles[i];
            if( featureTypeStyle == null ) continue;
            boolean removed = featureTypeStyle.rules().remove( rule );
            if( removed ){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void styleChanged( Object source ) {
        // ignore
    }
    private void swapColours(Rule rule1, Rule rule2) {
        Symbolizer[] symb1 = rule1.getSymbolizers();
        Symbolizer[] symb2 = rule2.getSymbolizers();
        if (symb1.length != symb2.length) {
            SLDPlugin.log("StyleThemePage.swapColours(): Number of symbolizers each rule and not equal - aborting colour swap", null); //$NON-NLS-1$
            return;
        }
        Expression tempColour; 
        for (int i = 0; i < symb1.length; i++) {
            if (symb1[i] instanceof PolygonSymbolizer) {
                tempColour = ((PolygonSymbolizer) symb1[i]).getFill().getColor();
                ((PolygonSymbolizer) symb1[i]).getFill().setColor(((PolygonSymbolizer) symb2[i]).getFill().getColor());
                ((PolygonSymbolizer) symb2[i]).getFill().setColor(tempColour);
            } else if (symb1[i] instanceof PointSymbolizer) {
                tempColour = ((PointSymbolizer) symb1[i]).getGraphic().getMarks()[0].getFill().getColor();
                ((PointSymbolizer) symb1[i]).getGraphic().getMarks()[0].getFill().setColor(((PointSymbolizer) symb2[i]).getGraphic().getMarks()[0].getFill().getColor());
                ((PointSymbolizer) symb2[i]).getGraphic().getMarks()[0].getFill().setColor(tempColour);
            } else if (symb1[i] instanceof LineSymbolizer) {
                tempColour = ((LineSymbolizer) symb1[i]).getStroke().getColor();
                ((LineSymbolizer) symb1[i]).getStroke().setColor(((LineSymbolizer) symb2[i]).getStroke().getColor());
                ((LineSymbolizer) symb2[i]).getStroke().setColor(tempColour);
            }
        }
    }
    
    public void setStatusText(String text) {
        getLabel(LABEL_STATUSBAR).setText(text);
    }
    
    @Override
    public String getLabel() {
        return null;
    }
    
    private class AttributeComboListener implements Listener {
        public void handleEvent(Event e) {
            //record the attribute
            selectedAttributeType = getAttributeType(getCombo(COMBO_ATTRIBUTES).getText());
            //update the normalization combo
            updateNormalize();
            //update the classification type (breaks) combo
            updateBreaks();
            if (isNumber(selectedAttributeType)) {
                //clear the status label
                setStatusText(""); //$NON-NLS-1$
            } else {
                //TODO: don't use strings to match break types
                if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_uniques)) { 
                    //determine if we've already calculated the number of unique values
                    String attribute = selectedAttributeType.getName().getLocalPart();
                    if (uniqueCounts.containsKey(attribute)) {
                        updateUnique(attribute, uniqueCounts.get(attribute));
                    }
                    //count the number of unique attributes
                    int uniqueCount = -1;
                    try {
                        Expression attr = ff.property(attribute);
                        UniqueVisitor uniques = new UniqueVisitor(attr);
                        ProgressListener progress = new NullProgressListener();
                        collection.accepts(uniques, progress);
                        uniqueCount = uniques.getUnique().size();
                    } catch (IOException e1) {
                        SLDPlugin.log("unique values calculation failed", e1); //$NON-NLS-1$
                    }
                    if (uniqueCount > -1) {
                        updateUnique(attribute, uniqueCount);                    
                    }
                } else {
                    //clear the status label
                    setStatusText(""); //$NON-NLS-1$
                }
            }

            //calculate the color scheme, if we have enough information
            if (inputsValid()) generateTheme();
            return;
        }


        private void updateUnique(String attr, int uniqueCount) {
            setStatusText(MessageFormat.format(Messages.StyleEditor_theme_unique_values, uniqueCount, attr));
            if (uniqueCount < 12) {
                if (getCombo(COMBO_ELSE).getSelectionIndex() > 0) {
                    getCombo(COMBO_CLASSES).select(uniqueCount - 1);
                } else {
                    getCombo(COMBO_CLASSES).select(uniqueCount - 2);
                }
            } else if (uniqueCount == 12) {
                getCombo(COMBO_CLASSES).select(10);
            }
        }
    }

    private class PalettesListener implements ISelectionChangedListener {

        public void selectionChanged(SelectionChangedEvent event) {
            if (inputsValid()) generateTheme();
        }
        
    }

    private class SuitabilityToggleListener implements SelectionListener {
        private int id;
        
        public SuitabilityToggleListener(int id) {
            super();
            this.id = id;
        }

        public void widgetSelected( SelectionEvent e ) {
            updateToggleTooltip(id);
            updatePalettes();
        }

        public void widgetDefaultSelected( SelectionEvent e ) {
            widgetSelected(e);
        }
        
    }
    
    private class ClassesComboListener implements Listener {
        public void handleEvent( Event event ) {
            updatePalettes();
            if (inputsValid()) generateTheme();
            return;
        }

    }
    
    private class ElseComboListener implements SelectionListener {
        public void widgetSelected(SelectionEvent e) {
            int index = getCombo(COMBO_ELSE).getSelectionIndex();
            int classesIndex = getCombo(COMBO_CLASSES).getSelectionIndex();
            if (elseSelection && index == 0) { //else rule was turned off
                if (classesIndex > 0) {
                    getCombo(COMBO_CLASSES).select(classesIndex - 1);
                }
            } else if (!elseSelection && index != 0) { //else rule was turned on
                if (classesIndex < (getCombo(COMBO_CLASSES).getItemCount() - 1)) {
                    getCombo(COMBO_CLASSES).select(classesIndex + 1);
                }
            } 
            elseSelection = (index > 0);
            if (inputsValid()) generateTheme();
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    }
    
    private class SimpleComboListener implements SelectionListener {
        public void widgetSelected(SelectionEvent e) {
            if (inputsValid()) generateTheme();
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    }

    private void updatePalettes() {
        paletteTable.setInput(getBrewer());
    }

    private void updateBreaks() {
        Combo breaksCombo = getCombo(COMBO_BREAKTYPE);
        String currentAttr = getCombo(COMBO_ATTRIBUTES).getText();
        String value = breaksCombo.getText();
        breaksCombo.removeAll();
        if (isNumber(currentAttr)) { //show numeric break types
            breaksCombo.add(Messages.StyleEditor_theme_equalInterval); 
            breaksCombo.add(Messages.StyleEditor_theme_quantile); 
            breaksCombo.add(Messages.StyleEditor_theme_uniques); 
            //breaksCombo.add(Messages.StyleEditor_theme_standardDeviation); 
            int index = breaksCombo.indexOf(value);
            if (index > -1) breaksCombo.select(index);
            else breaksCombo.select(1);
        } else { //show categorical break types
            breaksCombo.add(Messages.StyleEditor_theme_uniques); 
            int index = breaksCombo.indexOf(value);
            if (index > -1) breaksCombo.select(index);
            else breaksCombo.select(0);
        }
    }
    
    private void updateNormalize() {
        Combo normalizeCombo = getCombo(COMBO_NORMALIZE);
        String value = normalizeCombo.getText();
        String currentAttr = getCombo(COMBO_ATTRIBUTES).getText();
        normalizeCombo.removeAll();
        normalizeCombo.add(Messages.StyleEditor_theme_none); 
        if (isNumber(currentAttr)) {
            normalizeCombo.setEnabled(true);
            for (int i = 0; i < numericAttr.size(); i++) {
                String attr = numericAttr.get(i);
                if (!currentAttr.equals(attr))
                    normalizeCombo.add(attr);
            }
            int index = normalizeCombo.indexOf(value);
            if (index > 1) normalizeCombo.select(index);
            else normalizeCombo.select(0); //default is "< NONE >"
        } else {
            normalizeCombo.select(0);
            normalizeCombo.setEnabled(false);
        }
    }
    
    boolean inputsValid() {
        if (getCombo(COMBO_ATTRIBUTES).getText().length() < 1) return false;
        else if (paletteTable.getSelection().isEmpty()) return false;
        else if (getCombo(COMBO_CLASSES).getText().length() < 1) return false;
        else return true;
    }

    void generateTheme() {
        customPalette=null;
        if (selectedAttributeType == null) {
            selectedAttributeType = getAttributeType(getCombo(COMBO_ATTRIBUTES).getText());
        }
        if (getCombo(COMBO_NORMALIZE).getSelectionIndex() == 0) {
            normalize = null;
        } else {
            normalize = getAttributeType(getCombo(COMBO_NORMALIZE).getText());
        }
                
        //generate the defaults
        Runnable genDefault = new Runnable(){
            public void run() {
                //generate the expression
                Expression expr = null;
                Expression attr = ff.property(selectedAttributeType.getName());
                
                if (normalize != null) {
                    Divide divide = ff.divide(attr, ff.property(normalize.getName()));
                    expr = divide;
                } else {
                    expr = attr;
                }
                
                StructuredSelection structuredSelection = (StructuredSelection) paletteTable.getSelection();
                BrewerPalette pal = (BrewerPalette) structuredSelection.getFirstElement();
                if (pal == null) {
                    // get it from the last memento used
                    StyleBlackboard bb = getSelectedLayer().getStyleBlackboard();
                    IMemento memento = (IMemento) bb.get(DialogSettingsStyleContent.EXTENSION_ID);
                    String paletteName = memento.getString(PALETTE_KEY);
                    if (paletteName == null) return;
                    pal = getBrewer().getPalette(paletteName);
                }
                String paletteName = pal.getName();
                Combo combo = getCombo(COMBO_CLASSES);
                int numClasses = new Integer(combo.getText()).intValue();
                int[] suitability =null;
                try{
                    suitability = pal.getPaletteSuitability().getSuitability(numClasses);
                }catch (Exception e) {
                    suitability = CustomUnknownPaletteSuitability.getInstance().getSuitability(numClasses);
                }
                
                //check for custom classifier
                if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_custom)) { 
                    classifier = customBreak;
                }
                
                boolean classifierModified = false;
                if (classifier == null) {
                    classifierModified = true;
                } else {
                    //determine if the classifier needs recalculation
                    if (function.getClasses() != new Integer(getCombo(COMBO_CLASSES).getText()).intValue()) {
                        if (getCombo(COMBO_ELSE).getSelectionIndex() == 0) classifierModified = true;
                        if (function.getClasses() != new Integer(getCombo(COMBO_CLASSES).getText()).intValue()-1)
                            classifierModified = true;
                    }
                    if (getCombo(COMBO_ELSE).getSelectionIndex() > 0) 
                        classifierModified = true;
                    else if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_custom))  
                        classifierModified = true;
                    else if ((getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_equalInterval)) && !(function instanceof EqualIntervalFunction)) 
                        classifierModified = true;
                    else if ((getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_quantile)) && !(function instanceof QuantileFunction)) 
                        classifierModified = true;
                    else if ((getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_standardDeviation)) && !(function instanceof StandardDeviationFunction)) 
                        classifierModified = true;
                    else if ((getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_uniques)) && !(function instanceof UniqueIntervalFunction)) 
                        classifierModified = true;
                    else if (!function.equals(expr))
                        classifierModified = true;
                }
                
                String semanticTypeIdentifier;
                //break type:palette
                if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_uniques)) 
                    semanticTypeIdentifier = "unique:"; //$NON-NLS-1$
                else if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_equalInterval)) 
                    semanticTypeIdentifier = "equalinterval:"; //$NON-NLS-1$
                else if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_quantile)) 
                    semanticTypeIdentifier = "quantile:"; //$NON-NLS-1$
                else if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase("standard deviation")) //$NON-NLS-1$
                    semanticTypeIdentifier = "standarddeviation:"; //$NON-NLS-1$
                else if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_custom)) 
                    semanticTypeIdentifier = "custom:"; //$NON-NLS-1$
                else {
                    semanticTypeIdentifier = "default:"; //$NON-NLS-1$
                }
                semanticTypeIdentifier = semanticTypeIdentifier.concat(paletteName.toLowerCase());
                
                //create the classification function, if necessary
                if (classifierModified) {
                    //TODO: add other classifiers
                	FunctionName fn = null;
                    boolean createClassifier = true;
                    if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_uniques))
                    	fn = UniqueIntervalFunction.NAME;
                    else if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_equalInterval))
                    	fn = EqualIntervalFunction.NAME;
                    else if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_quantile))
                    	fn = QuantileFunction.NAME;
                    else if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_standardDeviation))
                    	fn = StandardDeviationFunction.NAME;
                    else if (getCombo(COMBO_BREAKTYPE).getText().equalsIgnoreCase(Messages.StyleEditor_theme_custom)){ 
                        classifier = customBreak;
                        createClassifier = false;
                    }else{ 
                        return;
                    }
                    
					if (createClassifier) {
						function = (ClassificationFunction) ff.function(fn.getFunctionName(), new Expression[fn.getArgumentCount()]);
						
						ProgressListener cancelProgress = ((StyleEditorDialog) getContainer()).getProgressListener();
						function.setProgressListener((org.geotools.util.ProgressListener) cancelProgress);
						numClasses = new Integer(getCombo(COMBO_CLASSES).getText()).intValue();

						if (getCombo(COMBO_ELSE).getSelectionIndex() == 0) {
							// function.setNumberOfClasses(numClasses);
							function.setClasses(numClasses);
						} else {
							// function.setNumberOfClasses(numClasses-1);
							function.setClasses(numClasses - 1);
						}
						// function.setCollection(collection);
						function.getParameters().set(0, expr); // set the expression last, since it causes the calculation
						// function.setExpression(expr);
						classifier = (Classifier) function.evaluate(collection,Classifier.class);
					}
                }

                //generate the style
                BrewerPalette palette = getBrewer().getPalette(paletteName);
                
                Color[] colors = null;
                try{
                    colors = palette.getColors(numClasses);
                }catch (Exception e) {
                    colors = palette.getColors();
                    palette = new CustomDynamicPalette(palette.getName(), palette.getDescription(), colors);
                    colors = palette.getColors(numClasses);
                }
                if (reverseColours) {
                    for (int i = 0; i < colors.length / 2; i++) {
                        Color tempColor = colors[i];
                        int j = colors.length-i-1;
                        colors[i] = colors[j];
                        colors[j] = tempColor;
                    }
                }
                
                int elsemode = -1;
                if (getCombo(COMBO_ELSE).getSelectionIndex() == 0) {
                    //sg.setElseMode(StyleGenerator.ELSEMODE_IGNORE);
                    elsemode = StyleGenerator.ELSEMODE_IGNORE;
                } else if (getCombo(COMBO_ELSE).getSelectionIndex() == 1) {
                    //sg.setElseMode(StyleGenerator.ELSEMODE_INCLUDEASMIN);
                    elsemode = StyleGenerator.ELSEMODE_INCLUDEASMIN;
                } else if (getCombo(COMBO_ELSE).getSelectionIndex() == 2) {
                    //sg.setElseMode(StyleGenerator.ELSEMODE_INCLUDEASMAX);
                    elsemode = StyleGenerator.ELSEMODE_INCLUDEASMAX;
                }
                int opacIndex = getCombo(COMBO_OPACITY).getSelectionIndex();
                double opac;
                if (opacIndex > -1) {
                    opac = opacity[opacIndex];
                } else {
                    opac = 1;
                }
                Color borderColor = BorderColorComboListener.getBorder(getCombo(COMBO_BORDER_COLOR));
                
                FeatureTypeStyle newFTS = null;
                try {
                    newFTS = StyleGenerator.createFeatureTypeStyle(classifier, (org.opengis.filter.expression.Expression) expr, colors, semanticTypeIdentifier, getSelectedLayer().getSchema().getGeometryDescriptor(), elsemode, opac, null);
                    applyExistingRulesProperties(newFTS, opac, borderColor);
                } catch (IllegalFilterException e) {
                    newFTS = null;
                    SLDPlugin.log("sg.createFeatureTypeStyle() failed", e); //$NON-NLS-1$
                } catch (NullPointerException e) {
                    newFTS = null;
                    SLDPlugin.log("sg.createFeatureTypeStyle() failed", e); //$NON-NLS-1$
                }
                if (newFTS == null) {
                    ErrorManager.get().displayError(Messages.StyleEditor_error, Messages.StyleEditor_theme_failure);
                    return;
                } else {
                    //set the FeatureTypeName to the current layer name
                    newFTS.featureTypeNames().clear();
                    newFTS.featureTypeNames().add( new NameImpl( SLDs.GENERIC_FEATURE_TYPENAME ));
                    //get the style
                    Style style = getStyle();
                    //ensure the style has an SLD
                    if (style == null) throw new RuntimeException("Style is null"); //$NON-NLS-1$
                    
                    StyledLayerDescriptor sld = null; //SLDs.styledLayerDescriptor(style);
                    if (sld == null) {
                        SLDContent.createDefaultStyledLayerDescriptor(style);
                    }
                    //insert/replace the FTS
                    try {
                        addThemedFTStoStyle(style, newFTS);
                    } catch (Exception e) {
                        String msg = "addThemedFTStoStyle() failed"; //$NON-NLS-1$
                        SLDPlugin.log(msg, e);
                        ErrorManager.get().displayException(e, msg, SLDPlugin.ID);
                        return;
                    }
                    
                    //update the suitability icons
                    if (suitability != null) {
                        updateSuitabilities(suitability);
                    }
                    
                    treeViewer.setInput(newFTS);
                }
            }

        };
        try {
            BusyIndicator.showWhile(Display.getCurrent(), genDefault);
        } catch (Exception e) {
            String msg = "Theme Generation Failed"; //$NON-NLS-1$
            SLDPlugin.log(msg, e);
            ErrorManager.get().displayException(e, msg, SLDPlugin.ID);
        }

        //resize the palette selection table
        displayBottomComposite();
        TableUtils.resizeColumns(treeViewer.getTree(), tableSettings, TableUtils.MODE_JUMP);
        //TODO: only the first time
        
        treeViewer.getControl().setVisible(true);
    }

    /**
     * This takes as much as possible from the old rules and applies them 
     * to the new ones. In that way mark type, size and borders are properly kept.
     * 
     * @param newFTS the new style to tweak.
     * @param opac an opacity value to apply to the fill.
     * @param borderColor 
     */
    private void applyExistingRulesProperties( FeatureTypeStyle newFTS, double opac, Color borderColor ) {
        Style style = getStyle();
        Symbolizer[] symbolizers = SLDs.symbolizers(style);
        if (symbolizers.length > 0) {
            Symbolizer symbolizer = symbolizers[0];
            if (symbolizer instanceof PointSymbolizer) {
                PointSymbolizer previousSymbolizer = (PointSymbolizer) symbolizer;
                Graphic oldGraphic = SLDs.graphic(previousSymbolizer);
                Mark oldMark = SLDs.mark(previousSymbolizer);
                if (oldMark != null) {
                    // we apply the properties to all the new rules
                    List<Rule> rules = newFTS.rules();
                    for( Rule rule : rules ) {
                        String[] colors = SLDs.colors(rule);
                        Color fill = SLDs.toColor(colors[0]);

                        List<Symbolizer> newSymbolizers = rule.symbolizers();
                        for( Symbolizer newSymbolizer : newSymbolizers ) {
                            if (newSymbolizer instanceof PointSymbolizer) {
                                PointSymbolizer newPointSymbolizer = (PointSymbolizer) newSymbolizer;

                                Mark mark = sb.createMark(oldMark.getWellKnownName().evaluate(null, String.class));
                                Fill newFill = sb.createFill(fill);
                                newFill.setOpacity(ff.literal(opac));
                                mark.setFill(newFill);

                                Stroke newStroke = oldMark.getStroke();
                                if (newStroke != null) {
                                    if (borderColor!=null) {
                                        newStroke.setColor(ff.literal(borderColor));
                                        mark.setStroke(newStroke);
                                    }else{
                                        mark.setStroke(null);
                                    }
                                }

                                Graphic newGraphic = SLDs.graphic(newPointSymbolizer);
                                newGraphic.setSize(oldGraphic.getSize());
                                newGraphic.setRotation( oldGraphic.getRotation());
                                newGraphic.graphicalSymbols().clear();
                                newGraphic.graphicalSymbols().add(mark);
                                break;
                            }
                        }
                    }
                }
            } else if (symbolizer instanceof PolygonSymbolizer) {
                List<Rule> rules = newFTS.rules();
                for( Rule rule : rules ) {
                    List<Symbolizer> newSymbolizers = rule.symbolizers();
                    for( Symbolizer newSymbolizer : newSymbolizers ) {
                        if (newSymbolizer instanceof PolygonSymbolizer) {
                            PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer ) newSymbolizer;
                            
                            Fill previousFill = SLDs.fill(polygonSymbolizer);
                            previousFill.setOpacity(ff.literal(opac));
                            
                            Stroke stroke = SLDs.stroke(polygonSymbolizer);
                            if (stroke != null) {
                                if(borderColor!=null){
                                    stroke.setColor(ff.literal(borderColor));
                                }else{
                                    polygonSymbolizer.setStroke(null);
                                }
                            }
                            
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Iterates through the style blackboard style and returns the ColorBrewer FeatureTypeStyle.
     *
     * @return the ColorBrewer {@link FeatureTypeStyle} or null if none found.
     */
    FeatureTypeStyle getFTS() {
        Style style = getStyle();
        List<FeatureTypeStyle> featureTypeStyles = style.featureTypeStyles();
        for( FeatureTypeStyle featureTypeStyle : featureTypeStyles ) {
            if (SLDs.isSemanticTypeMatch(featureTypeStyle, "colorbrewer:.*")) { //$NON-NLS-1$
                return featureTypeStyle;
            }
        }
        return null;
    }
    
    /**
     * Given a Style object and a FeatureTypeStyle, this method scans the SemanticTypeIdentifiers of
     * each FTS inside the style. If a ColorBrewer themed identifier is found, the new FTS will
     * replace it.
     * 
     * @param style future parent of the FTS
     * @param fts FTS to insert/replace with
     */
    private void addThemedFTStoStyle(Style style, FeatureTypeStyle fts) {
        //determine what symbolizers exist in the fts
        Set<Object> symbs = new HashSet<Object>();
        Rule[] newRules = fts.getRules();
        for (int i = 0; i < newRules.length; i++) {
            Symbolizer[] newSymbs = newRules[i].getSymbolizers();
            for (int j = 0; j < newSymbs.length; j++) {
                symbs.add(newSymbs[j].getClass());
            }
        }
        
        FeatureTypeStyle[] FTSs = style.getFeatureTypeStyles();
        //search for a match, and replace if found
        boolean found = false;
        for (int i = 0; i < FTSs.length; i++) {
            if (SLDs.isSemanticTypeMatch(FTSs[i], "colorbrewer:.*")) { //$NON-NLS-1$
                FTSs[i] = fts;
                found = true;
            } else {
                //purge any conflicting Symbolizers (same class)
                //TODO: add conditions under which other Symbolizers might live
                Rule[] rule = FTSs[i].getRules();
                for (int j = 0; j < rule.length; j++) {
                    Symbolizer[] symb = rule[j].getSymbolizers();
                    Symbolizer[] newSymb = (Symbolizer[]) symb.clone();
                    int deletedElements = 0;
                    for (int k = 0; k < symb.length; k++) {
                        if (symbs.contains(symb[k].getClass())) { //the Symbolizer class is a match
                            Object[] temp = removeElement(newSymb, k-deletedElements);
                            if (temp.length > 0) { 
                                newSymb = new Symbolizer[temp.length];
                                for (int l = 0; l < temp.length; l++) {
                                    newSymb[l] = (Symbolizer) temp[l];
                                }
                            } else {
                                newSymb = new Symbolizer[0];
                            }
                            deletedElements++;
                        }
                    }
                    if (deletedElements > 0) {
                        rule[j].setSymbolizers(newSymb);
                    }
                }
            }
        }
        //delete any rules with zero symbolizers
        for (int i = 0; i < FTSs.length; i++) {
            boolean changed = false;
            Rule[] rule = FTSs[i].getRules();
            for (int j = 0; j < rule.length; j++) {
                if (rule[j].getSymbolizers().length == 0) {
                    if (rule.length > 1) {
                        Object[] temp = removeElement(rule, j);
                        rule = new Rule[temp.length];
                        for (int k = 0; k < temp.length; k++) {
                            rule[k] = (Rule) temp[k];
                        }
                    } else {
                        rule = new Rule[0];
                    }
                    changed = true;
                    j--;
                }
            }
            if (changed) {
                FTSs[i].setRules(rule);
            }
        }
        //delete any FTSs with zero rules
        for (int i = 0; i < FTSs.length; i++) {
            if (FTSs[i].getRules().length == 0) {
                Object[] temp = removeElement(FTSs, i);
                FTSs = new FeatureTypeStyle[temp.length];
                for (int j = 0; j < temp.length; j++) {
                    FTSs[j] = (FeatureTypeStyle) temp[j];
                }
            }
        }
        //create a FTS if needed
        if (!found) {
            //match was not found, so add the FTS
            FeatureTypeStyle[] newFTSList = new FeatureTypeStyle[FTSs.length+1];
            newFTSList[0] = fts; //add the new fts to the START of the array
            System.arraycopy(FTSs, 0, newFTSList, 1, FTSs.length);
            style.setFeatureTypeStyles(newFTSList);
        } else {
            style.setFeatureTypeStyles(FTSs);
        }
    }
    
    private Object[] removeElement(Object[] array, int indexToRemove) {
        if (array.length == 1) return new Object[0];
        Object[] newArray = new Object[array.length-1];
        if (indexToRemove > 0) {
            System.arraycopy(array, 0, newArray, 0, indexToRemove);
        }
        if (indexToRemove < array.length-1) {
            System.arraycopy(array, indexToRemove+1, newArray, indexToRemove, array.length-indexToRemove-1);
        }
        return newArray;
    }
        
    private AttributeDescriptor getAttributeType(String attributeTypeName) {
        SimpleFeatureType featureType = getSelectedLayer().getSchema();
        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            AttributeDescriptor attributeType = featureType.getDescriptor(i);
            //if (attributeType.getName().equals(attributeTypeName)) return attributeType;
            if (attributeType.getName().getLocalPart().equals(attributeTypeName)) return attributeType;
        }
        return null;
    }

    private void createBrewer() {
        brewer = PlatformGIS.getColorBrewer();
    }
    
    private void displayBottomComposite() {
        Object layout = paletteTable.getControl().getLayoutData();
        if (layout instanceof GridData) {
            GridData gd = (GridData) layout;
            gd.grabExcessVerticalSpace = false;
            gd.heightHint = 70; //50 ~= 4 lines, 70 ~= 5 lines
        }
        getLabel(LABEL_SEPARATOR_BOTTOM).setVisible(true);
        getLayoutData(COMPOSITE_MIDDLE).grabExcessVerticalSpace = false;
        getComposite(COMPOSITE_BOTTOM).setVisible(true);
        getLayoutData(COMPOSITE_BOTTOM).grabExcessVerticalSpace = true;
        getLayoutData(COMPOSITE_BOTTOM_LEFT).grabExcessVerticalSpace = true;
        
        getComposite(COMPOSITE_MIDDLE).layout();
        getComposite(COMPOSITE_BOTTOM).layout();
        getComposite(COMPOSITE_PARENT).layout();
    }
    
    private void updateSuitabilities(int[] suitability) {
        for (Iterator<Integer> i = viewerQuality.keySet().iterator(); i.hasNext();) {
            Integer key = i.next();
            int icon = (viewerQuality.get(key)).intValue();
            int quality = suitability[key];
            String viewer = Messages.StyleEditor_theme_suitability_visiblefor + " " //$NON-NLS-1$
                    + controlNames.get(icon) + " ";     //$NON-NLS-1$
            Label label = getLabel(icon);
            if (quality == PaletteSuitability.QUALITY_GOOD) {
                //getLabel(icon).setImage(getImageWithOverlay(icon, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.GOOD_OVERLAY)));
                label.setImage(getImageWithOverlay(icon, null));
                label.setToolTipText(viewer+Messages.StyleEditor_theme_suitability_good); 
            } else if (quality == PaletteSuitability.QUALITY_DOUBTFUL) {
                getLabel(icon).setImage(getImageWithOverlay(icon, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.DOUBTFUL_OVERLAY)));
                label.setToolTipText(viewer+Messages.StyleEditor_theme_suitability_doubtful); 
            } else if (quality == PaletteSuitability.QUALITY_BAD) {
                getLabel(icon).setImage(getImageWithOverlay(icon, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.BAD_OVERLAY)));
                label.setToolTipText(viewer+Messages.StyleEditor_theme_suitability_bad); 
            } else { //UNKNOWN
                getLabel(icon).setImage(getImageWithOverlay(icon, SLDPlugin.getDefault().getImageDescriptor(ImageConstants.UNKNOWN_OVERLAY)));
                label.setToolTipText(viewer+Messages.StyleEditor_theme_suitability_unknown); 
            }
        }
    }
    
    private Image getImageWithOverlay(int iconLabelID, ImageDescriptor overlay) {
        //get the base image
        ImageDescriptor descriptor = null;
    	Image base = null;
        if (iconLabelID == LABEL_ICON_COLORBLIND) descriptor = SLDPlugin.getDefault().getImageDescriptor(ImageConstants.COLORBLIND_ICON); 
        else if (iconLabelID == LABEL_ICON_CRT) descriptor = SLDPlugin.getDefault().getImageDescriptor(ImageConstants.CRT_ICON); 
        else if (iconLabelID == LABEL_ICON_LCD) descriptor = SLDPlugin.getDefault().getImageDescriptor(ImageConstants.LAPTOP_ICON); 
        else if (iconLabelID == LABEL_ICON_PHOTOCOPY) descriptor = SLDPlugin.getDefault().getImageDescriptor(ImageConstants.PHOTOCOPY_ICON); 
        else if (iconLabelID == LABEL_ICON_PRINT) descriptor = SLDPlugin.getDefault().getImageDescriptor(ImageConstants.PRINTER_ICON); 
        else if (iconLabelID == LABEL_ICON_PROJECTOR) descriptor = SLDPlugin.getDefault().getImageDescriptor(ImageConstants.PROJECTOR_ICON); 
        if (descriptor != null) base = descriptor.createImage();
        if (overlay == null) return base;
        //apply the overlay
        DecoratorOverlayIcon ovrIcon = new DecoratorOverlayIcon(base, new ImageDescriptor[] {null,null,overlay,null,null});
        return ovrIcon.createImage();
    }

    public void gotFocus() {
        StyleBlackboard bb = getSelectedLayer().getStyleBlackboard();
        IMemento memento = (IMemento) bb
                .get(DialogSettingsStyleContent.EXTENSION_ID);
        if (memento != null) {
            setComboSelectionInt(memento, COMBO_OPACITY, OPACITY_KEY);
            setComboSelectionString(memento, COMBO_ATTRIBUTES, ATTRIBUTE_KEY);
            setComboSelectionInt(memento, COMBO_CLASSES, CLASSES_KEY);
            setComboSelectionString(memento, COMBO_BREAKTYPE, BREAK_KEY);
            setComboSelectionString(memento, COMBO_NORMALIZE, NORMALIZE_KEY);
            setComboSelectionString(memento, COMBO_ELSE, ELSE_KEY);
            setComboSelectionString(memento, COMBO_PALETTES,
                    PALETTE_CATEGORY_KEY);

            setButtonSelection(memento, BUTTON_COLORBLIND, COLOR_BLIND_KEY);
            setButtonSelection(memento, BUTTON_CRT, CRT_KEY);
            setButtonSelection(memento, BUTTON_LCD, LCD_KEY);
            setButtonSelection(memento, BUTTON_PHOTOCOPY, PHOTO_COPY_KEY);
            setButtonSelection(memento, BUTTON_PRINT, PRINT_KEY);
            setButtonSelection(memento, BUTTON_PROJECTOR, PROJECTOR_KEY);
            setOutine(memento);
            
            setPaletteSelection(memento);
            readCustomPalette(memento);
            readCustomBreak(memento);
            // make sure theme is up to date
            generateTheme();
        }
    }

    private void setOutine( IMemento memento ) {
        String string = memento.getString(BORDER_COLOR_KEY);
        if (string == null) {
            // nothing set
            return;
        }
        String[] parts = string.split("-"); //$NON-NLS-1$
        if (parts.length != 3) {
            // only support red green and blue if not that number of parts then
            // somehow got bad so go to default;
            return;
        }

        int red = Integer.parseInt(parts[0]);
        int green = Integer.parseInt(parts[1]);
        int blue = Integer.parseInt(parts[2]);

        Combo combo = getCombo(COMBO_BORDER_COLOR);
        if (red == 0 && blue == 0 && green == 0) {
            // its black so set combo to black
            combo.select(Outline.BLACK.ordinal());
        } else if (red == 255 && blue == 255 && green == 255) {
            // its white so set combo to white
            combo.select(Outline.WHITE.ordinal());
        } else {
            combo.select(Outline.CUSTOM.ordinal());
            combo.setData(new RGB(red,green,blue));
        }
    }

    private void readCustomBreak(IMemento memento) {
        // TODO Auto-generated method stub
        
    }

    private void readCustomPalette(IMemento memento) {
        List<Color> colors = new ArrayList<Color>();
        int index = 1;
        while(memento.getInteger(CUSTOM_PALETTE+index)!=null){ 
            int rgb = memento.getInteger(CUSTOM_PALETTE+index); 
            colors.add(new Color(rgb));
            index++;
        }
        if(colors.isEmpty()){
            return;
        }
        if( getBrewer().hasPalette(Messages.StyleEditor_theme_custom)){
            customPalette = getBrewer().getPalette(Messages.StyleEditor_theme_custom);
            customPalette.setColors(colors.toArray(new Color[0]));
        }else{
            customPalette = new BrewerPalette();
            PaletteSuitability suitability = new PaletteSuitability();
    
            SampleScheme newScheme = new SampleScheme();
            String unknown = "?"; //$NON-NLS-1$
            for (int i = 0; i < colors.size(); i++) {
                if (i > 0) { 
                    //create a simple scheme
                    int[] scheme = new int[i+1];
                    for (int j = 0; j < i+1; j++) {
                        scheme[j] = j;
                    }
                    try {
                        newScheme.setSampleScheme(i+1, scheme);
                        //set the suitability to unknown
                        suitability.setSuitability(i+1, new String[] {unknown, unknown, unknown, unknown, unknown, unknown});
                    } catch (Exception e) {
                        // SLDPlugin.log("setSuitability() failed", e); //$NON-NLS-1$
                        // don't block here, give an unknown status
                        suitability = CustomUnknownPaletteSuitability.getInstance();
                        newScheme = new CustomSampleScheme(colors.size());
                    }
                }
            }
            customPalette.setPaletteSuitability(suitability);
            customPalette.setColors(colors.toArray(new Color[0]));
            customPalette.setColorScheme(newScheme);
            customPalette.setName(Messages.StyleEditor_theme_custom); 
            customPalette.setDescription(Messages.StyleEditor_theme_custom_desc); 
            customPalette.setType(new PaletteType());
            if (!getBrewer().hasPalette(Messages.StyleEditor_theme_custom)) {
                getBrewer().registerPalette(customPalette);
            }
        }
        paletteTable.setInput(getBrewer());
        String paletteName = memento.getString(PALETTE_KEY);
        if(paletteName.equals(customPalette.getName()))
            paletteTable.setSelection(new StructuredSelection(customPalette));
    }

    private void setPaletteSelection(IMemento memento) {
        String paletteName = memento.getString(PALETTE_KEY);
        if( paletteName!=null ){
            BrewerPalette[] palettes = brewer.getPalettes();
            for (BrewerPalette brewerPalette : palettes) {
                String currentName = brewerPalette.getName();
                if( currentName.equals(paletteName) ){
                    ISelection selection = new StructuredSelection(brewerPalette);
                    paletteTable.setSelection(selection, true);
                    return;
                }
            }
        }
    }

    private void setButtonSelection(IMemento memento, int widgetID,
			String mementoKey) {
		if( memento.getInteger(mementoKey)!=null ){
			Button button = getButton(widgetID);
			boolean selected = memento.getInteger(mementoKey)>0;
			button.setSelection(selected);
		}
	}

	private void setComboSelectionString(IMemento memento, int widgetId, String key) {
		Combo control = getCombo(widgetId);
		if( memento.getString(key)!=null ){
    		String string = memento.getString(key);
    		String[] items = control.getItems();
    		for (int i = 0; i < items.length; i++) {
    			if( items[i].equals(string)){
	    			control.select(i);
	    			break;
    			}
			}
    	}
	};
    

	private void setComboSelectionInt(IMemento memento, int widgetId, String key) {
		Combo control = getCombo(widgetId);
		if( memento.getInteger(key)!=null ){
    		Integer integer = memento.getInteger(key);
			control.select(integer);
    	}
	};
    
    public boolean performApply() {
    	StyleBlackboard bb = getSelectedLayer().getStyleBlackboard();
    	IMemento memento = (IMemento) bb.get(DialogSettingsStyleContent.EXTENSION_ID);
    	if(memento == null ){
    		memento = XMLMemento.createWriteRoot("ThemeingData");
    	}

    	putIntFromCombo(memento, OPACITY_KEY, COMBO_OPACITY);
    	putStringFromCombo(memento, ATTRIBUTE_KEY, COMBO_ATTRIBUTES);
    	putIntFromCombo(memento, CLASSES_KEY, COMBO_CLASSES);
    	putStringFromCombo(memento, BREAK_KEY, COMBO_BREAKTYPE);
    	putStringFromCombo(memento, NORMALIZE_KEY, COMBO_NORMALIZE);
    	memento.putInteger(ELSE_KEY, getCombo(COMBO_ELSE).getSelectionIndex());
        putStringFromCombo(memento, PALETTE_CATEGORY_KEY, COMBO_PALETTES);

        putButton(memento, BUTTON_COLORBLIND, COLOR_BLIND_KEY);
        putButton(memento, BUTTON_CRT, CRT_KEY);
        putButton(memento, BUTTON_PROJECTOR, PROJECTOR_KEY);
        putButton(memento, BUTTON_LCD, LCD_KEY);
        putButton(memento, BUTTON_PRINT, PRINT_KEY);
        putButton(memento, BUTTON_PHOTOCOPY, PHOTO_COPY_KEY);

        storeOutline(memento);
        storePalette(memento);
        storeCustomPalette(memento);
        storeCustomBreak(memento);
        bb.put(DialogSettingsStyleContent.EXTENSION_ID, memento);
        return true;
    }

    private void storeOutline( IMemento memento ) {
        Combo combo = getCombo(COMBO_BORDER_COLOR);

        Outline outline = Outline.values()[combo.getSelectionIndex()];
        RGB rgb = null;
        switch( outline ) {
        case BLACK:
            rgb = new RGB(0,0,0);
            break;
        case WHITE:
            rgb = new RGB(255,255,255);
            break;
        case CUSTOM:
            rgb = (RGB) combo.getData();
            break;

        default:
            memento.putString(BORDER_COLOR_KEY, ""); //$NON-NLS-1$
            return;
        }
        
        String sep = "-"; //$NON-NLS-1$
        String stringVal = rgb.red+sep+rgb.green+sep+rgb.blue;
        memento.putString(BORDER_COLOR_KEY, stringVal);
    }

    private void storeCustomBreak(IMemento memento) {
        // TODO Auto-generated method stub
        if( customBreak!=null ){
            
        }
    }

    private void storeCustomPalette(IMemento memento) {
        if( customPalette!=null ){
            Color[] colors = customPalette.getColors();
            int index = 1;
            for (Color color : colors) {
                int rgb = color.getRGB();
                memento.putInteger(CUSTOM_PALETTE+index, rgb);
                index++;
            }
        }
    }


    private void storePalette(IMemento memento) {
        ISelection selection = paletteTable.getSelection();
        if (!selection.isEmpty()) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            BrewerPalette palette = (BrewerPalette) (structuredSelection).getFirstElement();
            memento.putString(PALETTE_KEY, palette.getName());
        }
    }

	private void putButton(IMemento memento, int buttonId, String key) {
		boolean selection;
		selection = getButton(buttonId).getSelection();
		memento.putInteger(key, selection?1:0);
	}

    private void putStringFromCombo(IMemento memento, String key,
			int comboID) {
    	Combo combo = getCombo(comboID);
		int index = combo.getSelectionIndex();
		memento.putString(key, combo.getItem(index));
	}

	private void putIntFromCombo(IMemento memento, String key,
			int comboID) {
		memento.putInteger(key, getCombo(comboID).getSelectionIndex());
	}

	public void refresh() {
        //TODO: add refresh method
    }
}
