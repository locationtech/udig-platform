/**
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.jgrass.colors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.geotools.gce.grassraster.JGrassConstants;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.JGrassUtilities;
import org.geotools.gce.grassraster.core.color.ColorRule;
import org.geotools.gce.grassraster.core.color.JGrassColorTable;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.jgrass.activeregion.dialogs.JGRasterChooserDialog;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import org.locationtech.udig.catalog.jgrass.utils.JGrassCatalogUtilities;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.jgrass.JGrassrasterStyleActivator;
import org.locationtech.udig.style.jgrass.core.GrassColorTable;
import org.locationtech.udig.style.jgrass.core.PredefinedColorRules;
import org.locationtech.udig.ui.ExceptionDetailsDialog;
import org.locationtech.udig.ui.PlatformGIS;

/**
 * The composite holding the JGrass Raster map editing logic
 *
 * @author Andrea Antonello - www.hydrologis.com
 */
public class ColorEditor extends Composite implements SelectionListener {

    private ArrayList<Rule> listOfRules = null;

    private Button addRuleButton = null;

    private Button removeRuleButton = null;

    private Button moveRuleUpButton = null;

    private Button moveRuleDownButton = null;

    private Composite rulesComposite = null;

    private Group alphaGroup = null;

    private Scale alphaScale = null;

    private ScrolledComposite scrolledRulesComposite = null;

    private Layer layer;

    private String[] mapsetPathAndMapName;

    private File colrFile;

    private Label alphaLabel = null;

    private Button loadFromFileButton = null;

    private Button loadFromMapButton = null;

    private Button exportToFileButton = null;

    private Combo predefinedRulesCombo;

    private HashMap<String, String[][]> colorRulesMap;

    private Button resetColormapButton;

    public ColorEditor(Composite parent, int style) {
        super(parent, style);
        listOfRules = new ArrayList<>();
        initialize();
    }

    private void initialize() {

        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = GridData.FILL;
        gridData3.grabExcessHorizontalSpace = true;
        gridData3.verticalAlignment = GridData.CENTER;
        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.verticalAlignment = GridData.CENTER;
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.verticalAlignment = GridData.CENTER;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.CENTER;
        addRuleButton = new Button(this, SWT.NONE);
        addRuleButton.setText("+"); //$NON-NLS-1$
        addRuleButton.setLayoutData(gridData);
        addRuleButton.addSelectionListener(this);
        removeRuleButton = new Button(this, SWT.NONE);
        removeRuleButton.setText("-"); //$NON-NLS-1$
        removeRuleButton.setLayoutData(gridData1);
        removeRuleButton.addSelectionListener(this);
        moveRuleUpButton = new Button(this, SWT.UP | SWT.ARROW);
        moveRuleUpButton.setLayoutData(gridData2);
        moveRuleUpButton.addSelectionListener(this);
        moveRuleDownButton = new Button(this, SWT.DOWN | SWT.ARROW);
        moveRuleDownButton.setLayoutData(gridData3);
        moveRuleDownButton.addSelectionListener(this);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        gridLayout.makeColumnsEqualWidth = true;
        this.setLayout(gridLayout);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        this.setLayoutData(gd);
        createScrolledRulesComposite();

        GridData gridDataBC = new GridData();
        gridDataBC.horizontalAlignment = GridData.FILL;
        gridDataBC.horizontalSpan = 4;
        gridDataBC.verticalAlignment = GridData.CENTER;
        Composite buttonComposite = new Composite(this, SWT.NONE);
        buttonComposite.setLayoutData(gridDataBC);
        buttonComposite.setLayout(new GridLayout(8, true));

        GridData loadGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        loadGD.horizontalSpan = 2;
        GridData loadFMapGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        loadFMapGD.horizontalSpan = 2;
        GridData exportGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        exportGD.horizontalSpan = 2;
        GridData resetGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        resetGD.horizontalSpan = 2;
        loadFromMapButton = new Button(buttonComposite, SWT.NONE);
        loadFromMapButton.setText("load from map"); //$NON-NLS-1$
        loadFromMapButton.setLayoutData(loadFMapGD);
        loadFromMapButton.addSelectionListener(this);
        loadFromFileButton = new Button(buttonComposite, SWT.NONE);
        loadFromFileButton.setText("import colormap"); //$NON-NLS-1$
        loadFromFileButton.setLayoutData(loadGD);
        loadFromFileButton.addSelectionListener(this);
        exportToFileButton = new Button(buttonComposite, SWT.NONE);
        exportToFileButton.setText("export colormap"); //$NON-NLS-1$
        exportToFileButton.setLayoutData(resetGD);
        exportToFileButton.addSelectionListener(this);
        resetColormapButton = new Button(buttonComposite, SWT.NONE);
        resetColormapButton.setText("reset colormap"); //$NON-NLS-1$
        resetColormapButton.setLayoutData(resetGD);
        resetColormapButton.addSelectionListener(this);

        // predefined rules combo
        Label predefinedRulesLabel = new Label(buttonComposite, SWT.NONE);
        GridData rulesLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        rulesLabelGD.horizontalSpan = 3;
        predefinedRulesLabel.setLayoutData(rulesLabelGD);
        predefinedRulesLabel.setText("Set from predefined table"); //$NON-NLS-1$

        predefinedRulesCombo = new Combo(buttonComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData comboGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboGD.horizontalSpan = 5;
        predefinedRulesCombo.setLayoutData(comboGD);
        colorRulesMap = PredefinedColorRules.getColorsFolder(true);
        Set<String> keySet = colorRulesMap.keySet();
        String[] rulesNames = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(rulesNames);
        predefinedRulesCombo.setItems(rulesNames);
        predefinedRulesCombo.addSelectionListener(this);

        createRulesComposite();
        createAlphaGroup();
    }

    /**
     * This method initializes rulesComposite
     */
    private void createRulesComposite() {
        GridData gridData4 = new GridData();
        gridData4.horizontalSpan = 4;
        gridData4.verticalAlignment = GridData.FILL;
        gridData4.grabExcessHorizontalSpace = true;
        gridData4.grabExcessVerticalSpace = true;
        gridData4.horizontalAlignment = GridData.FILL;
        rulesComposite = new Composite(scrolledRulesComposite, SWT.NONE);
        rulesComposite.setLayout(new GridLayout());
        rulesComposite.setLayoutData(gridData4);
        scrolledRulesComposite.setContent(rulesComposite);
    }

    /**
     * This method initializes scrolledRulesComposite
     */
    private void createScrolledRulesComposite() {
        GridData gridData7 = new GridData();
        gridData7.horizontalSpan = 4;
        gridData7.verticalAlignment = GridData.FILL;
        gridData7.grabExcessVerticalSpace = true;
        gridData7.grabExcessHorizontalSpace = true;
        gridData7.horizontalAlignment = GridData.FILL;

        scrolledRulesComposite = new ScrolledComposite(this, SWT.V_SCROLL | SWT.BORDER);
        scrolledRulesComposite.setLayoutData(gridData7);
        scrolledRulesComposite.setExpandHorizontal(true);
        scrolledRulesComposite.setExpandVertical(true);
        scrolledRulesComposite.setMinHeight(2000);
    }

    /**
     * This method initializes alphaGroup
     */
    private void createAlphaGroup() {
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 4;
        GridData gridData6 = new GridData();
        gridData6.horizontalAlignment = GridData.FILL;
        gridData6.grabExcessHorizontalSpace = true;
        gridData6.horizontalSpan = 3;
        gridData6.verticalAlignment = GridData.CENTER;
        GridData gridData5 = new GridData();
        gridData5.horizontalSpan = 4;
        gridData5.verticalAlignment = GridData.CENTER;
        gridData5.grabExcessHorizontalSpace = true;
        gridData5.horizontalAlignment = GridData.FILL;
        alphaGroup = new Group(this, SWT.NONE);
        alphaGroup.setLayoutData(gridData5);
        alphaGroup.setLayout(gridLayout1);
        alphaGroup.setText("alpha"); //$NON-NLS-1$
        alphaScale = new Scale(alphaGroup, SWT.NONE);
        alphaScale.setLayoutData(gridData6);
        alphaScale.setMinimum(0);
        alphaScale.setMaximum(255);
        alphaScale.setPageIncrement(5);
        alphaScale.setSelection(255);
        alphaLabel = new Label(alphaGroup, SWT.NONE);
        alphaLabel.setText(alphaScale.getSelection() + ""); //$NON-NLS-1$
        alphaScale.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                int perspectiveValue = alphaScale.getSelection();
                alphaLabel.setText(perspectiveValue + ""); //$NON-NLS-1$
            }
        });
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        Object source = e.getSource();
        if (source instanceof Button) {
            Button selectedButton = (Button) source;

            if (selectedButton.equals(addRuleButton)) {
                // add an empty rule to the composite
                Rule r = new Rule();
                listOfRules.add(r);
                redoLayout();
            } else if (selectedButton.equals(removeRuleButton)) {
                for (int i = 0; i < listOfRules.size(); i++) {
                    Rule r = listOfRules.get(i);
                    if (r.isActive()) {
                        listOfRules.remove(r);
                    }
                }
                redoLayout();
            } else if (selectedButton.equals(moveRuleUpButton)) {
                for (int i = 0; i < listOfRules.size(); i++) {
                    Rule r = listOfRules.get(i);
                    if (r.isActive()) {
                        if (i > 0) {
                            listOfRules.remove(r);
                            listOfRules.add(i - 1, r);
                        }
                    }
                }
                redoLayout();
            } else if (selectedButton.equals(moveRuleDownButton)) {
                for (int i = 0; i < listOfRules.size(); i++) {
                    Rule r = listOfRules.get(i);
                    if (r.isActive()) {
                        if (i < listOfRules.size() - 1) {
                            listOfRules.remove(r);
                            listOfRules.add(i + 1, r);
                            i++;
                        }
                    }
                }
                redoLayout();
            } else if (selectedButton.equals(loadFromFileButton)) {

                FileDialog fileDialog = new FileDialog(this.getShell(), SWT.OPEN);
                String path = fileDialog.open();

                if (path == null) {
                    return;
                }

                makeSomeColor(path);

            } else if (selectedButton.equals(loadFromMapButton)) {

                JGRasterChooserDialog tree = new JGRasterChooserDialog(null);
                tree.open(this.getShell(), SWT.SINGLE);
                update(tree.getSelectedResources());

            } else if (selectedButton.equals(exportToFileButton)) {

                FileDialog fileDialog = new FileDialog(this.getShell(), SWT.SAVE);
                fileDialog.setText("Choose file"); //$NON-NLS-1$
                String path = fileDialog.open();

                try {
                    FileUtils.copyFile(colrFile, new File(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else if (selectedButton.equals(resetColormapButton)) {

                /**
                 * run with backgroundable progress monitoring
                 */
                IRunnableWithProgress operation = new IRunnableWithProgress() {

                    @Override
                    public void run(IProgressMonitor monitor)
                            throws InvocationTargetException, InterruptedException {

                        try {
                            File cellFile = new File(
                                    mapsetPathAndMapName[0] + File.separator + JGrassConstants.CELL
                                            + File.separator + mapsetPathAndMapName[1]);
                            JGrassMapEnvironment mE = new JGrassMapEnvironment(cellFile);
                            double[] dataRange = mE.getRangeFromMapScan();

                            List<String> defColorTable = JGrassColorTable
                                    .createDefaultColorTable(dataRange, 255);
                            File colrFile = mE.getCOLR();
                            JGrassUtilities.makeColorRulesPersistent(colrFile, defColorTable,
                                    dataRange, 255);
                            makeSomeColor(colrFile.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                            String message = "An error occurred while persisting the colortable to disk."; //$NON-NLS-1$
                            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR,
                                    JGrassrasterStyleActivator.PLUGIN_ID, e);
                            return;
                        }

                    }
                };
                PlatformGIS.runInProgressDialog("Resetting colormap", true, operation, true); //$NON-NLS-1$
            }
        }
        if (source instanceof Combo) {
            Combo combo = (Combo) source;
            if (combo.equals(predefinedRulesCombo)) {
                int selectionIndex = predefinedRulesCombo.getSelectionIndex();
                String item = predefinedRulesCombo.getItem(selectionIndex);
                try {
                    String[][] colorRules = colorRulesMap.get(item);
                    GrassColorTable.setColorTableFromRules(colrFile, null, colorRules);
                    makeSomeColor(colrFile.getAbsolutePath());
                } catch (IOException e1) {
                    MessageDialog.openError(this.getShell(), "ERROR", //$NON-NLS-1$
                            "An error occurred while setting the colortable: " //$NON-NLS-1$
                                    + colrFile.getAbsolutePath());
                    e1.printStackTrace();
                }

            }
        }
    }

    private void makeSomeColor(String colrpath) {
        GrassColorTable ctable = null;
        Enumeration<ColorRule> rules = null;

        try {
            while (rules == null || !rules.hasMoreElements()) {

                try {
                    ctable = new GrassColorTable(colrpath, null);
                } catch (IOException e1) {
                    JGrassrasterStyleActivator.log("JGrassrasterStyleActivator problem", e1); //$NON-NLS-1$
                    e1.printStackTrace();
                }
                rules = ctable.getColorRules();

                // create a default color file
                if (rules == null || !rules.hasMoreElements()) {
                    ctable.createDefaultColorRulesString(null, true);
                }

            }
        } catch (Exception e) {
            JGrassrasterStyleActivator.log(
                    "JGrassrasterStyleActivator problem: eu.hydrologis.jgrass.style.jgrassraster.colors#ColorEditor#makeSomeColor", //$NON-NLS-1$
                    e);
            e.printStackTrace();
        }

        ArrayList<Rule> listOfRules = new ArrayList<>();

        while (rules.hasMoreElements()) {
            ColorRule element = rules.nextElement();

            float lowvalue = element.getLowCategoryValue();
            float highvalue = element.getLowCategoryValue() + element.getCategoryRange();
            byte[] lowcatcol = element.getColor(lowvalue);
            byte[] highcatcol = element.getColor(highvalue);

            float[] lowHigh = new float[] { lowvalue, highvalue };
            Color lowColor = new Color(Display.getDefault(), (lowcatcol[0] & 0xff),
                    (lowcatcol[1] & 0xff), (lowcatcol[2] & 0xff));
            Color highColor = new Color(Display.getDefault(), (highcatcol[0] & 0xff),
                    (highcatcol[1] & 0xff), (highcatcol[2] & 0xff));

            listOfRules.add(new Rule(lowHigh, lowColor, highColor, true));
        }

        this.setLayer(layer);

        this.setAlphaValue(ctable.getAlpha());

        this.setRulesList(listOfRules);

    }

    /**
     * Set the layer that called this style editor. Needed for putting the alpha value into the
     * blackboard whenever it something changes.
     *
     * @param layer
     */
    public void setLayer(Layer layer) {
        this.layer = layer;
        IGeoResource resource = layer.getGeoResource();
        mapsetPathAndMapName = JGrassCatalogUtilities
                .getMapsetpathAndMapnameFromJGrassMapGeoResource(resource);
        colrFile = new File(mapsetPathAndMapName[0] + File.separator + JGrassConstants.COLR
                + File.separator + mapsetPathAndMapName[1]);
    }

    public void setAlphaValue(final int value) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                alphaScale.setSelection(value);
                alphaLabel.setText(String.valueOf(value));
            }
        });

    }

    public void setRulesList(ArrayList<Rule> listOfRules) {
        this.listOfRules = listOfRules;
        redoLayout();
    }

    protected void redoLayout() {

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                // remove the rules from the composite
                Control[] rulesControls = rulesComposite.getChildren();
                for (int i = 0; i < rulesControls.length; i++) {
                    rulesControls[i].dispose();
                }

                // recreate the rules composites from the list
                for (Rule rule : listOfRules) {
                    new RuleComposite(ColorEditor.this, rulesComposite, SWT.BORDER, rule);
                }

                rulesComposite.layout();
            }
        });
    }

    /**
     * write the rules to file
     */
    public synchronized void makePersistent() {
        // write to disk
        if (colrFile != null) {
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(colrFile));

                if (listOfRules.isEmpty()) {
                    return;
                }
                float[] dataRange = new float[] { listOfRules.get(0).getFromToValues()[0],
                        listOfRules.get(listOfRules.size() - 1).getFromToValues()[1] };

                String header = "% " + dataRange[0] + "   " + dataRange[1] + "   " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + alphaLabel.getText();
                bw.write(header + "\n"); //$NON-NLS-1$

                for (Rule r : listOfRules) {
                    if (r.isActive())
                        bw.write(r.ruleToString() + "\n"); //$NON-NLS-1$
                }

            } catch (IOException e1) {
                JGrassrasterStyleActivator.log("JGrassrasterStyleActivator problem", e1); //$NON-NLS-1$
                e1.printStackTrace();
            } finally {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    JGrassrasterStyleActivator.log("JGrassrasterStyleActivator problem", e); //$NON-NLS-1$
                }
            }
        }

    }

    @SuppressWarnings("rawtypes")
    public void update(Object updatedObject) {
        if (updatedObject instanceof List) {
            String mapName = null;
            String mapsetPath = null;
            List layers = (List) updatedObject;
            for (Object layer : layers) {
                if (layer instanceof JGrassMapGeoResource) {
                    JGrassMapGeoResource rasterMapResource = (JGrassMapGeoResource) layer;
                    try {
                        mapName = rasterMapResource.getInfo(null).getTitle();
                        mapsetPath = ((JGrassMapsetGeoResource) rasterMapResource.parent(null))
                                .getFile().getAbsolutePath();
                        if (mapName != null && mapsetPath != null) {
                            String colrPath = mapsetPath + File.separator + JGrassConstants.COLR
                                    + File.separator + mapName;
                            makeSomeColor(colrPath);
                        }
                    } catch (IOException e) {
                        JGrassrasterStyleActivator.log("JGrassrasterStyleActivator problem", e); //$NON-NLS-1$
                        e.printStackTrace();
                    }
                }
            }
        }
    }
} // @jve:decl-index=0:visual-constraint="10,10"
