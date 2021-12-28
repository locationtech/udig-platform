/**
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.jgrass.categories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.geotools.gce.grassraster.JGrassConstants;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.jgrass.activeregion.dialogs.JGRasterChooserDialog;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import org.locationtech.udig.catalog.jgrass.utils.JGrassCatalogUtilities;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.jgrass.JGrassrasterStyleActivator;

/**
 * The composite holding the JGrass Raster map editing logic
 *
 * @author Andrea Antonello - www.hydrologis.com
 */
public class CategoryEditor extends Composite implements SelectionListener {

    private ArrayList<CategoryRule> listOfRules = null;

    private Button addRuleButton = null;

    private Button removeRuleButton = null;

    private Button moveRuleUpButton = null;

    private Button moveRuleDownButton = null;

    private Composite rulesComposite = null;

    private ScrolledComposite scrolledRulesComposite = null;

    private Layer layer;

    private String[] mapsetPathAndMapName;

    private File catsFile;

    private Button loadFromFileButton = null;

    private Button loadFromMapButton = null;

    public CategoryEditor(Composite parent, int style) {
        super(parent, style);
        listOfRules = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        GridData gridData21 = new GridData();
        gridData21.horizontalSpan = 2;
        gridData21.verticalAlignment = GridData.CENTER;
        gridData21.horizontalAlignment = GridData.FILL;
        GridData gridData11 = new GridData();
        gridData11.horizontalAlignment = GridData.FILL;
        gridData11.horizontalSpan = 2;
        gridData11.verticalAlignment = GridData.CENTER;
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
        loadFromFileButton = new Button(this, SWT.NONE);
        loadFromFileButton.setText("load from file"); //$NON-NLS-1$
        loadFromFileButton.setLayoutData(gridData11);
        loadFromFileButton.addSelectionListener(this);
        loadFromMapButton = new Button(this, SWT.NONE);
        loadFromMapButton.setText("load from map"); //$NON-NLS-1$
        loadFromMapButton.setLayoutData(gridData21);
        loadFromMapButton.addSelectionListener(this);
        createRulesComposite();
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
        scrolledRulesComposite.setAlwaysShowScrollBars(true);
        scrolledRulesComposite.setExpandHorizontal(true);
        scrolledRulesComposite.setExpandVertical(true);
        scrolledRulesComposite.setMinSize(300, 500);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        Button selectedButton = (Button) e.getSource();
        if (selectedButton.equals(addRuleButton)) {
            // add an empty rule to the composite
            CategoryRule r = new CategoryRule();
            listOfRules.add(r);
            redoLayout();
        } else if (selectedButton.equals(removeRuleButton)) {
            for (int i = 0; i < listOfRules.size(); i++) {
                CategoryRule r = listOfRules.get(i);
                if (r.isActive()) {
                    listOfRules.remove(r);
                }
            }
            redoLayout();
        } else if (selectedButton.equals(moveRuleUpButton)) {
            for (int i = 0; i < listOfRules.size(); i++) {
                CategoryRule r = listOfRules.get(i);
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
                CategoryRule r = listOfRules.get(i);
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

            makeSomeCategories(path);

        } else if (selectedButton.equals(loadFromMapButton)) {

            JGRasterChooserDialog tree = new JGRasterChooserDialog(null);
            tree.open(this.getShell(), SWT.SINGLE);

            update(tree.getSelectedResources());
        }
    }

    public void makeSomeCategories(String catspath) {

        File catsFile = new File(catspath);
        BufferedReader inputReader = null;
        try {
            inputReader = new BufferedReader(new FileReader(catsFile));

            String line = null;
            LinkedHashMap<String, String> cats = new LinkedHashMap<>();
            if (catsFile.exists()) {
                // jump over the first 4 lines
                line = inputReader.readLine();
                line = inputReader.readLine();
                line = inputReader.readLine();
                line = inputReader.readLine();

                while ((line = inputReader.readLine()) != null) {
                    if (line == null || line.equals("")) { //$NON-NLS-1$
                        return;
                    }

                    StringTokenizer valtok = new StringTokenizer(line, ":"); //$NON-NLS-1$
                    if (valtok.countTokens() > 1) {
                        cats.put(valtok.nextToken(), valtok.nextToken());
                    } else {
                        return;
                    }
                }

                listOfRules.clear();
                Set<Entry<String, String>> entrySet = cats.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    listOfRules.add(new CategoryRule(key, value, true));
                }
            }
        } catch (IOException e1) {
            JGrassrasterStyleActivator.log("JGrassrasterStyleActivator problem", e1); //$NON-NLS-1$
            e1.printStackTrace();
            return;
        } finally {
            try {
                inputReader.close();
            } catch (IOException e) {
                JGrassrasterStyleActivator.log("JGrassrasterStyleActivator problem", e); //$NON-NLS-1$
                e.printStackTrace();
            }
        }
        this.setLayer(layer);

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
        catsFile = new File(mapsetPathAndMapName[0] + File.separator + JGrassConstants.CATS
                + File.separator + mapsetPathAndMapName[1]);
    }

    public void setRulesList(ArrayList<CategoryRule> listOfRules) {
        this.listOfRules = listOfRules;
        redoLayout();
    }

    protected void redoLayout() {
        // remove the rules from the composite
        Control[] rulesControls = rulesComposite.getChildren();
        for (int i = 0; i < rulesControls.length; i++) {
            rulesControls[i].dispose();
        }

        // recreate the rules composites from the list
        for (CategoryRule rule : listOfRules) {
            new CategoryRuleComposite(rulesComposite, SWT.BORDER, rule);
        }

        rulesComposite.layout();
    }

    /**
     * write the rules to file
     */
    public void makePersistent() {
        // write to disk
        BufferedWriter bw = null;
        if (catsFile != null) {
            try {
                bw = new BufferedWriter(new FileWriter(catsFile));

                if (listOfRules.isEmpty()) {
                    return;
                }

                StringBuffer header = new StringBuffer();
                header.append("# " + listOfRules.size() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
                header.append(mapsetPathAndMapName[1] + "\n"); //$NON-NLS-1$
                header.append("\n"); //$NON-NLS-1$
                header.append("0.00 0.00 0.00 0.00\n"); //$NON-NLS-1$
                bw.write(header.toString());

                for (CategoryRule r : listOfRules) {
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
                            String catsPath = mapsetPath + File.separator + JGrassConstants.CATS
                                    + File.separator + mapName;
                            makeSomeCategories(catsPath);
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
