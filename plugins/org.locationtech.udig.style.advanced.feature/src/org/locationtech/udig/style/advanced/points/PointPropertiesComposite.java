/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.points;

import static org.locationtech.udig.style.advanced.utils.Utilities.sb;
import static org.locationtech.udig.style.advanced.utils.Utilities.wkMarkNames;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.styling.Font;
import org.geotools.styling.TextSymbolizer;
import org.locationtech.udig.style.advanced.StylePlugin;
import org.locationtech.udig.style.advanced.common.FiltersComposite;
import org.locationtech.udig.style.advanced.common.IStyleChangesListener;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.PointSymbolizerWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.SymbolizerWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.TextSymbolizerWrapper;
import org.locationtech.udig.style.advanced.internal.Messages;
import org.locationtech.udig.style.advanced.points.widgets.PointBoderParametersComposite;
import org.locationtech.udig.style.advanced.points.widgets.PointCharacterChooserComposite;
import org.locationtech.udig.style.advanced.points.widgets.PointFillParametersComposite;
import org.locationtech.udig.style.advanced.points.widgets.PointGeneralParametersComposite;
import org.locationtech.udig.style.advanced.points.widgets.PointLabelsParametersComposite;
import org.locationtech.udig.style.advanced.utils.Utilities;
import org.locationtech.udig.style.sld.SLD;
import org.opengis.filter.Filter;

public class PointPropertiesComposite extends SelectionAdapter implements ModifyListener, IStyleChangesListener {

    private static final String[] POINT_STYLE_TYPES = {Messages.PointPropertiesComposite_0, Messages.PointPropertiesComposite_1, Messages.PointPropertiesComposite_2};
    private static final String[] WK_MARK_NAMES = wkMarkNames;
    private static final String TTF_PREFIX = "ttf://"; //$NON-NLS-1$
    
    private RuleWrapper ruleWrapper;

    private Composite simplePointComposite = null;
    private Composite graphicsPointComposite = null;
    private Composite fontPointComposite = null;
    private PointPropertiesEditor pointPropertiesEditor;
    private Composite mainComposite;
    private StackLayout mainStackLayout;

    private Combo wknMarksCombo;

    private String[] numericAttributesArrays;
    private String[] allAttributesArrays;
    private Text graphicsPathText;

    private Composite parentComposite;

    private Combo styleTypecombo;

    private final Composite parent;

    private PointGeneralParametersComposite generalParametersCompositeSIMPLE;

    private PointGeneralParametersComposite generalParametersCompositeGRAPHICS;
    
    private PointGeneralParametersComposite generalParametersCompositeFONT;
    
    private PointCharacterChooserComposite fontParametersComposite;
    
    private PointBoderParametersComposite borderParametersComposite;
    
    private PointBoderParametersComposite fontBorderParametersComposite;
    
    private PointFillParametersComposite fillParametersComposite;
    
    private PointFillParametersComposite fontFillParametersComposite;
    
    private PointLabelsParametersComposite labelsParametersComposite;
    
    private PointLabelsParametersComposite fontLabelsParametersComposite;
    
    private FiltersComposite filtersComposite;
    
    private FiltersComposite fontFiltersComposite;

    public PointPropertiesComposite( final PointPropertiesEditor pointPropertiesEditor, Composite parent ) {
        this.pointPropertiesEditor = pointPropertiesEditor;
        this.parent = parent;
    }

    public void setRule( RuleWrapper ruleWrapper ) {
        this.ruleWrapper = ruleWrapper;

        if (mainComposite == null) {
            init();
            if (simplePointComposite == null) {
                createSimpleComposite();
            }
            if (graphicsPointComposite == null) {
                createGraphicsComposite();
            }
            if (fontPointComposite == null) {
                createFontComposite();
            }
        } else {
            update();
        }
        setRightPanel();
    }

    private void update() {
        SymbolizerWrapper geometrySymbolizersWrapper = ruleWrapper.getGeometrySymbolizersWrapper();
        PointSymbolizerWrapper pointSymbolizerWrapper = geometrySymbolizersWrapper.adapt(PointSymbolizerWrapper.class);
        
        filtersComposite.update(ruleWrapper);
        
        String markName = pointSymbolizerWrapper.getMarkName();
        
        if (pointSymbolizerWrapper.hasExternalGraphic()) {
            generalParametersCompositeGRAPHICS.update(ruleWrapper);

            // external graphics path
            graphicsPathText.removeModifyListener(this);
            try {
                graphicsPathText.setText(pointSymbolizerWrapper.getExternalGraphicPath());
            } catch (MalformedURLException e) {
                graphicsPathText.setText(""); //$NON-NLS-1$
            }
            graphicsPathText.addModifyListener(this);
        } else if (markName != null && markName.toLowerCase().startsWith(TTF_PREFIX)) {
            generalParametersCompositeFONT.update(ruleWrapper);
            fontBorderParametersComposite.update(ruleWrapper);
            fontFillParametersComposite.update(ruleWrapper);
            fontLabelsParametersComposite.update(ruleWrapper);
        }
        else {
            generalParametersCompositeSIMPLE.update(ruleWrapper);
            borderParametersComposite.update(ruleWrapper);
            fillParametersComposite.update(ruleWrapper);
            labelsParametersComposite.update(ruleWrapper);

            // mark
            if (markName == null) {
                markName = WK_MARK_NAMES[0];
            }
            for( int i = 0; i < WK_MARK_NAMES.length; i++ ) {
                if (markName.equalsIgnoreCase(WK_MARK_NAMES[i])) {
                    wknMarksCombo.removeSelectionListener(this);
                    wknMarksCombo.select(i);
                    wknMarksCombo.addSelectionListener(this);
                    break;
                }
            }
        }

        pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
        pointPropertiesEditor.refreshPreviewCanvasOnStyle();
    }

    private void init() {
        List<String> numericAttributeNames = pointPropertiesEditor.getNumericAttributeNames();
        numericAttributesArrays = (String[]) numericAttributeNames.toArray(new String[numericAttributeNames.size()]);
        List<String> allAttributeNames = pointPropertiesEditor.getAllAttributeNames();

        // sort alphabetical
        Collections.sort(allAttributeNames);

        allAttributesArrays = (String[]) allAttributeNames.toArray(new String[allAttributeNames.size()]);
        // geometryPropertyName = pointPropertiesEditor.getGeometryPropertyName().getLocalPart();

        parentComposite = new Composite(parent, SWT.NONE);
        parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        parentComposite.setLayout(new GridLayout(1, false));

        styleTypecombo = new Combo(parentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        styleTypecombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
        styleTypecombo.setItems(POINT_STYLE_TYPES);
        styleTypecombo.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                        PointSymbolizerWrapper.class);
                int selectionIndex = styleTypecombo.getSelectionIndex();
                if (selectionIndex == 0) {
                    int index = wknMarksCombo.getSelectionIndex();
                    if (index != -1)
                    {
                        String markName = wknMarksCombo.getItem(index);
                        pointSymbolizerWrapper.setMarkName(markName);
                    }
                    mainStackLayout.topControl = simplePointComposite;

                    generalParametersCompositeSIMPLE.update(ruleWrapper);

                    pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
                    pointPropertiesEditor.refreshPreviewCanvasOnStyle();
                } else if (selectionIndex == 1) {
                    try {
                        URL iconUrl = Platform.getBundle(StylePlugin.PLUGIN_ID).getResource("icons/delete.png"); //$NON-NLS-1$
                        String iconPath = ""; //$NON-NLS-1$
                        try {
                            iconPath = FileLocator.toFileURL(iconUrl).getPath();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        pointSymbolizerWrapper.setExternalGraphicPath(iconPath);
                        graphicsPathText.setText(iconPath);
                    } catch (MalformedURLException e1) {
                        // can't happen
                    }
                    mainStackLayout.topControl = graphicsPointComposite;

                    generalParametersCompositeGRAPHICS.update(ruleWrapper);

                    pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
                    pointPropertiesEditor.refreshPreviewCanvasOnStyle();
                } else {
                    String markName = fontParametersComposite.getCharacterPath();
                    pointSymbolizerWrapper.setMarkName(markName);
                    mainStackLayout.topControl = fontPointComposite;
                    generalParametersCompositeFONT.update(ruleWrapper);
                    pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
                    pointPropertiesEditor.refreshPreviewCanvasOnStyle();
                }
                mainComposite.layout();
            }
        });

        mainComposite = new Composite(parentComposite, SWT.NONE);
        mainStackLayout = new StackLayout();
        mainComposite.setLayout(mainStackLayout);
        GridData mainCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        mainComposite.setLayoutData(mainCompositeGD);
        // mainStackLayout.topControl = l;
    }

    private void setRightPanel() {
        final PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);
        String markName = pointSymbolizerWrapper.getMarkName();
        boolean hasExt = pointSymbolizerWrapper.hasExternalGraphic();
        if (hasExt) {
            styleTypecombo.select(1);
            mainStackLayout.topControl = graphicsPointComposite;
        } else if (markName != null && markName.toLowerCase().startsWith(TTF_PREFIX)) {
            styleTypecombo.select(2);
            mainStackLayout.topControl = fontPointComposite;
        } else {
            styleTypecombo.select(0);
            mainStackLayout.topControl = simplePointComposite;
        }
        mainComposite.layout();
    }

    public Composite getComposite() {
        return parentComposite;
    }

    private void createSimpleComposite() {
        final PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        simplePointComposite = new Composite(mainComposite, SWT.None);
        simplePointComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        simplePointComposite.setLayout(new GridLayout(1, false));

        // rule name
        Composite nameComposite = new Composite(simplePointComposite, SWT.NONE);
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        nameComposite.setLayout(new GridLayout(2, true));

        // well known marks
        wknMarksCombo = new Combo(simplePointComposite, SWT.DROP_DOWN);
        GridData wknMarksComboGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        wknMarksCombo.setLayoutData(wknMarksComboGD);
        wknMarksCombo.setItems(WK_MARK_NAMES);
        String markName = pointSymbolizerWrapper.getMarkName();
        if (markName == null) {
            // use a default
            markName = WK_MARK_NAMES[0];
        }
        for( int i = 0; i < WK_MARK_NAMES.length; i++ ) {
            if (markName.equalsIgnoreCase(WK_MARK_NAMES[i])) {
                wknMarksCombo.select(i);
                break;
            }
        }
        wknMarksCombo.addSelectionListener(this);

        // use an expandbar for the properties
        Group propertiesGroup = new Group(simplePointComposite, SWT.SHADOW_ETCHED_IN);
        propertiesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        propertiesGroup.setLayout(new GridLayout(1, false));
        propertiesGroup.setText(Messages.PointPropertiesComposite_8);

        TabFolder tabFolder = new TabFolder(propertiesGroup, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        generalParametersCompositeSIMPLE = new PointGeneralParametersComposite(tabFolder, numericAttributesArrays);
        generalParametersCompositeSIMPLE.init(ruleWrapper);
        generalParametersCompositeSIMPLE.addListener(this);
        Composite generalParametersInternalComposite = generalParametersCompositeSIMPLE.getComposite();

        TabItem tabItem1 = new TabItem(tabFolder, SWT.NULL);
        tabItem1.setText(Messages.PointPropertiesComposite_9);
        tabItem1.setControl(generalParametersInternalComposite);

        // BORDER GROUP
        borderParametersComposite = new PointBoderParametersComposite(tabFolder, numericAttributesArrays);
        borderParametersComposite.init(ruleWrapper);
        borderParametersComposite.addListener(this);
        Composite borderParametersInternalComposite = borderParametersComposite.getComposite();

        TabItem tabItem2 = new TabItem(tabFolder, SWT.NULL);
        tabItem2.setText(Messages.PointPropertiesComposite_10);
        tabItem2.setControl(borderParametersInternalComposite);

        // Fill GROUP
        fillParametersComposite = new PointFillParametersComposite(tabFolder, numericAttributesArrays);
        fillParametersComposite.init(ruleWrapper);
        fillParametersComposite.addListener(this);
        Composite fillParametersInternalComposite = fillParametersComposite.getComposite();

        TabItem tabItem3 = new TabItem(tabFolder, SWT.NULL);
        tabItem3.setText(Messages.PointPropertiesComposite_11);
        tabItem3.setControl(fillParametersInternalComposite);

        // Label GROUP
        labelsParametersComposite = new PointLabelsParametersComposite(tabFolder, numericAttributesArrays, allAttributesArrays);
        labelsParametersComposite.init(ruleWrapper);
        labelsParametersComposite.addListener(this);
        Composite labelParametersInternalComposite = labelsParametersComposite.getComposite();

        TabItem tabItem4 = new TabItem(tabFolder, SWT.NULL);
        tabItem4.setText(Messages.PointPropertiesComposite_12);
        tabItem4.setControl(labelParametersInternalComposite);
        
        // Filter GROUP
        filtersComposite = new FiltersComposite(tabFolder);
        filtersComposite.init(ruleWrapper);
        filtersComposite.addListener(this);
        Composite filtersInternalComposite = filtersComposite.getComposite();

        TabItem tabItem5 = new TabItem(tabFolder, SWT.NULL);
        tabItem5.setText(Messages.PointPropertiesComposite_13);
        tabItem5.setControl(filtersInternalComposite);
    }

    private void createGraphicsComposite() {
        final PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        graphicsPointComposite = new Composite(mainComposite, SWT.None);
        graphicsPointComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        graphicsPointComposite.setLayout(new GridLayout(1, false));

        // rule name
        Composite nameComposite = new Composite(graphicsPointComposite, SWT.NONE);
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        nameComposite.setLayout(new GridLayout(2, true));

        // external graphics path
        Group pathGroup = new Group(graphicsPointComposite, SWT.NONE);
        pathGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        pathGroup.setLayout(new GridLayout(2, false));
        pathGroup.setText(Messages.PointPropertiesComposite_14);
        graphicsPathText = new Text(pathGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        graphicsPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        try {
            graphicsPathText.setText(pointSymbolizerWrapper.getExternalGraphicPath());
        } catch (MalformedURLException e1) {
            graphicsPathText.setText(""); //$NON-NLS-1$
        }
        graphicsPathText.addModifyListener(this);
        Button pathButton = new Button(pathGroup, SWT.PUSH);
        pathButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        pathButton.setText("..."); //$NON-NLS-1$
        pathButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog fileDialog = new FileDialog(graphicsPathText.getShell(), SWT.OPEN);
                String path = fileDialog.open();
                if (path == null || path.length() < 1) {
                    graphicsPathText.setText(""); //$NON-NLS-1$
                } else {
                    graphicsPathText.setText(path);
                }
            }
        });

        // rule name, size, rotation, offset, zoomlevels group
        Group genericsGroup = new Group(graphicsPointComposite, SWT.NONE);
        genericsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        genericsGroup.setLayout(new GridLayout(1, true));
        genericsGroup.setText(Messages.PointPropertiesComposite_18);

        generalParametersCompositeGRAPHICS = new PointGeneralParametersComposite(genericsGroup, numericAttributesArrays);
        generalParametersCompositeGRAPHICS.init(ruleWrapper);
        generalParametersCompositeGRAPHICS.addListener(this);
    }

    private void createFontComposite() {
        fontPointComposite = new Composite(mainComposite, SWT.None);
        fontPointComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        fontPointComposite.setLayout(new GridLayout(1, false));
        
        // rule name
        Composite nameComposite = new Composite(fontPointComposite, SWT.NONE);
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        nameComposite.setLayout(new GridLayout(2, true));

        // use an expandbar for the properties
        Group propertiesGroup = new Group(fontPointComposite, SWT.SHADOW_ETCHED_IN);
        propertiesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        propertiesGroup.setLayout(new GridLayout(1, false));
        propertiesGroup.setText(Messages.PointPropertiesComposite_19);

        TabFolder tabFolder = new TabFolder(propertiesGroup, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        //TODO PointCharacterChooserComposite is loading in the ui this is holding up the dialog desplay
        fontParametersComposite = new PointCharacterChooserComposite(tabFolder);
        fontParametersComposite.init(ruleWrapper);
        fontParametersComposite.addListener(this);
        Composite fontParametersInternalComposite = fontParametersComposite.getComposite();

        TabItem tabItem1 = new TabItem(tabFolder, SWT.NULL);
        tabItem1.setText(Messages.PointPropertiesComposite_20);
        tabItem1.setControl(fontParametersInternalComposite);
        
        generalParametersCompositeFONT = new PointGeneralParametersComposite(tabFolder, numericAttributesArrays);
        generalParametersCompositeFONT.init(ruleWrapper);
        generalParametersCompositeFONT.addListener(this);
        Composite generalParametersInternalComposite = generalParametersCompositeFONT.getComposite();

        TabItem tabItem2 = new TabItem(tabFolder, SWT.NULL);
        tabItem2.setText(Messages.PointPropertiesComposite_21);
        tabItem2.setControl(generalParametersInternalComposite);

        // BORDER GROUP
        fontBorderParametersComposite = new PointBoderParametersComposite(tabFolder, numericAttributesArrays);
        fontBorderParametersComposite.init(ruleWrapper);
        fontBorderParametersComposite.addListener(this);
        Composite borderParametersInternalComposite = fontBorderParametersComposite.getComposite();

        TabItem tabItem3 = new TabItem(tabFolder, SWT.NULL);
        tabItem3.setText(Messages.PointPropertiesComposite_22);
        tabItem3.setControl(borderParametersInternalComposite);

        // Fill GROUP
        fontFillParametersComposite = new PointFillParametersComposite(tabFolder, numericAttributesArrays);
        fontFillParametersComposite.init(ruleWrapper);
        fontFillParametersComposite.addListener(this);
        Composite fillParametersInternalComposite = fontFillParametersComposite.getComposite();

        TabItem tabItem4 = new TabItem(tabFolder, SWT.NULL);
        tabItem4.setText(Messages.PointPropertiesComposite_23);
        tabItem4.setControl(fillParametersInternalComposite);

        // Label GROUP
        fontLabelsParametersComposite = new PointLabelsParametersComposite(tabFolder, numericAttributesArrays, allAttributesArrays);
        fontLabelsParametersComposite.init(ruleWrapper);
        fontLabelsParametersComposite.addListener(this);
        Composite labelParametersInternalComposite = fontLabelsParametersComposite.getComposite();

        TabItem tabItem5 = new TabItem(tabFolder, SWT.NULL);
        tabItem5.setText(Messages.PointPropertiesComposite_24);
        tabItem5.setControl(labelParametersInternalComposite);
        
        // Filter GROUP
        fontFiltersComposite = new FiltersComposite(tabFolder);
        fontFiltersComposite.init(ruleWrapper);
        fontFiltersComposite.addListener(this);
        Composite filtersInternalComposite = fontFiltersComposite.getComposite();

        TabItem tabItem6 = new TabItem(tabFolder, SWT.NULL);
        tabItem6.setText(Messages.PointPropertiesComposite_25);
        tabItem6.setControl(filtersInternalComposite);
    }
    
    @Override
    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();

        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        if (source.equals(wknMarksCombo)) {
            int selectionIndex = wknMarksCombo.getSelectionIndex();
            
            if (selectionIndex != -1)
            {
                String item = wknMarksCombo.getItem(selectionIndex);
                pointSymbolizerWrapper.setMarkName(item);
                pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
                pointPropertiesEditor.refreshPreviewCanvasOnStyle();
            }
        }
        return;
    }

    public void modifyText( ModifyEvent e ) {
        Object source = e.getSource();
        if (source.equals(graphicsPathText)) {
            try {
                setNewGraphicPath();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                return;
            }
        }
        pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
        pointPropertiesEditor.refreshPreviewCanvasOnStyle();
    }

    private void setNewGraphicPath() throws MalformedURLException {
        String path = graphicsPathText.getText();
        if (! path.equals("")) //$NON-NLS-1$
        {
            PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                    PointSymbolizerWrapper.class);
            pointSymbolizerWrapper.setExternalGraphicPath(path);
        }
    }

    public void onStyleChanged( Object source, String[] values, boolean fromField, STYLEEVENTTYPE styleEventType ) {
        String value = values[0];

        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);
        TextSymbolizerWrapper textSymbolizerWrapper = ruleWrapper.getTextSymbolizersWrapper();

        switch( styleEventType ) {
        // GENERAL PARAMETERS
        case NAME:
            ruleWrapper.setName(value);
            break;
        case SIZE:
            pointSymbolizerWrapper.setSize(value, fromField);
            break;
        case ROTATION:
            pointSymbolizerWrapper.setRotation(value, fromField);
            break;
        case OFFSET:
            pointSymbolizerWrapper.setOffset(value);
            break;
        case MAXSCALE:
            ruleWrapper.setMaxScale(value);
            break;
        case MINSCALE:
            ruleWrapper.setMinScale(value);
            break;
        // BORDER PARAMETERS
        case BORDERENABLE: {
            boolean enabled = Boolean.parseBoolean(value);
            pointSymbolizerWrapper.setHasStroke(enabled);
            break;
        }
        case BORDERWIDTH: {
            pointSymbolizerWrapper.setStrokeWidth(value, fromField);
            break;
        }
        case BORDERCOLOR: {
            pointSymbolizerWrapper.setStrokeColor(value);
            break;
        }
        case BORDEROPACITY: {
            pointSymbolizerWrapper.setStrokeOpacity(value, fromField);
            break;
        }
            // FILL PARAMETERS
        case FILLENABLE: {
            boolean enabled = Boolean.parseBoolean(value);
            pointSymbolizerWrapper.setHasFill(enabled);
            break;
        }
        case FILLCOLOR: {
            pointSymbolizerWrapper.setFillColor(value);
            break;
        }
        case FILLOPACITY: {
            pointSymbolizerWrapper.setFillOpacity(value, fromField);
            break;
        }
            // LABEL PARAMETERS
        case LABELENABLE: {
            boolean doEnable = Boolean.parseBoolean(value);
            if (doEnable) {
                if (textSymbolizerWrapper == null) {
                    TextSymbolizer textSymbolizer = Utilities.createDefaultTextSymbolizer(SLD.POINT);
                    ruleWrapper.addSymbolizer(textSymbolizer, TextSymbolizerWrapper.class);
                    labelsParametersComposite.update(ruleWrapper);
                }
            } else {
                ruleWrapper.removeTextSymbolizersWrapper();
            }
            break;
        }
        case LABEL: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setLabelName(value, fromField);
            break;
        }
        case LABELFONT: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            String name = values[0];
            int style = Integer.parseInt(values[1]);
            int height = Integer.parseInt(values[2]);
            Font font = sb.createFont(name, style == SWT.ITALIC, style == SWT.BOLD, height);

            textSymbolizerWrapper.setFont(font);
            break;
        }
        case LABELCOLOR: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setColor(value);
            break;
        }
        case LABELHALOCOLOR: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setHaloColor(value);
            break;
        }
        case LABELHALORADIUS: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setHaloRadius(value);
            break;
        }
        case LABELANCHOR: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setAnchorX(values[0]);
            textSymbolizerWrapper.setAnchorY(values[1]);
            break;
        }
        case LABELDISPLACEMENT: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setDisplacement(value);
            break;
        }
        case LABELROTATION: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setRotation(value, fromField);
            break;
        }
        case LABELMAXDISPLACEMENT_VO: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setMaxDisplacementVO(value);
            break;
        }
        case LABELAUTOWRAP_VO: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setAutoWrapVO(value);
            break;
        }
        case LABELSPACEAROUND_VO: {
            if (textSymbolizerWrapper == null) {
                break;
            }
            textSymbolizerWrapper.setSpaceAroundVO(value);
            break;
        }
        case FILTER: {
            if (value.length() > 0) {
                try {
                    Filter filter = ECQL.toFilter(value);
                    ruleWrapper.getRule().setFilter(filter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        }
        case MARKNAME:
            pointSymbolizerWrapper.setMarkName(value);
            break;
        default:
            break;
        }

        pointPropertiesEditor.refreshTreeViewer(ruleWrapper);
        pointPropertiesEditor.refreshPreviewCanvasOnStyle();

    }

}
