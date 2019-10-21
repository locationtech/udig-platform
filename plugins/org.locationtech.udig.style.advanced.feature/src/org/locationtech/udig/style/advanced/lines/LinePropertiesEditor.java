/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.lines;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Style;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.style.advanced.common.GroupRulesTreeContentProvider;
import org.locationtech.udig.style.advanced.common.GroupRulesTreeLabelProvider;
import org.locationtech.udig.style.advanced.common.PropertiesEditor;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.FeatureTypeStyleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.LineSymbolizerWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.StyleWrapper;
import org.locationtech.udig.style.advanced.internal.Messages;
import org.locationtech.udig.style.advanced.internal.WrapperUtilities;
import org.locationtech.udig.style.advanced.utils.ImageCache;
import org.locationtech.udig.style.advanced.utils.Utilities;
import org.locationtech.udig.style.internal.StyleLayer;
import org.locationtech.udig.style.sld.SLD;

/**
 * An editor for line styles.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class LinePropertiesEditor extends PropertiesEditor {

    private Composite propertiesComposite;
    private StackLayout propertiesStackLayout;

    private LineStyleManager lineStyleManager;

    public LinePropertiesEditor( StyleLayer layer ) {
        super(layer);
    }

    protected void createGui( Composite parent ) {
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(2, true));

        /*
         * ****************
         * Left side, preview window and rules list
         * ****************
         */

        Composite leftComposite = new Composite(mainComposite, SWT.NONE);
        leftComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        leftComposite.setLayout(new GridLayout(1, false));

        /*
         * rules and preview group
         */
        Group rulesAndPreviewGroup = new Group(leftComposite, SWT.SHADOW_ETCHED_OUT);
        rulesAndPreviewGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout rulesAndPreviewGroupLayout = new GridLayout(2, false);
        rulesAndPreviewGroupLayout.marginTop = 0;
        rulesAndPreviewGroup.setLayout(rulesAndPreviewGroupLayout);
        rulesAndPreviewGroup.setText(Messages.LinePropertiesEditor_0);

        Composite canvasComposite = new Composite(rulesAndPreviewGroup, SWT.NONE);
        canvasComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        canvasComposite.setLayout(new GridLayout(1, false));
        previewCanvas = new Canvas(canvasComposite, SWT.BORDER);
        GridData previewCanvasGD = new GridData(SWT.BEGINNING, SWT.TOP, true, true);
        previewCanvasGD.minimumHeight = PREVIEWHEIGHT;
        previewCanvasGD.minimumWidth = PREVIEWWIDTH;
        previewCanvas.setSize(PREVIEWWIDTH, PREVIEWHEIGHT);
        previewCanvas.setBackground(white);
        previewCanvas.setLayoutData(previewCanvasGD);
        previewCanvas.addPaintListener(new PaintListener(){
            public void paintControl( PaintEvent e ) {
                if (previewImage == null) {
                    Display display = Display.getDefault();
                    previewImage = new Image(display, PREVIEWWIDTH, PREVIEWHEIGHT);
                }
                // Rectangle clientArea = previewCanvas.getClientArea();
                // e.gc.setAntialias(SWT.ON);
                e.gc.drawImage(previewImage, 0, 0);
                e.gc.setLineWidth(1);
                e.gc.setLineStyle(SWT.LINE_DOT);
                e.gc.setForeground(gray);
                // e.gc.setAlpha(128);
                e.gc.drawLine(PREVIEWWIDTH / 2, 0, PREVIEWWIDTH / 2, PREVIEWHEIGHT);
                e.gc.drawLine(0, PREVIEWHEIGHT / 2, PREVIEWWIDTH, PREVIEWHEIGHT / 2);
            }
        });

        Composite rulesComposite = new Composite(rulesAndPreviewGroup, SWT.NONE);
        GridData rulesCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        rulesComposite.setLayoutData(rulesCompositeGD);
        GridLayout rulesCompositeLayout = new GridLayout(6, true);
        rulesComposite.setLayout(rulesCompositeLayout);
        groupRulesTreeViewer = createGroupRulesTableViewer(rulesComposite);
        createRulesButtons(rulesComposite);
        groupRulesTreeViewer.setInput(styleWrapper.getFeatureTypeStylesWrapperList());

        // style list viewer
        Group styleViewerGroup = new Group(leftComposite, SWT.NONE);
        GridData styleViewerGroupGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        styleViewerGroup.setLayoutData(styleViewerGroupGD);
        styleViewerGroup.setLayout(new GridLayout(10, false));
        styleViewerGroup.setText(Messages.LinePropertiesEditor_1);

        Composite styleManagerComposite = new Composite(styleViewerGroup, SWT.NONE);
        GridData styleManagerCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        styleManagerCompositeGD.horizontalSpan = 10;
        styleManagerComposite.setLayoutData(styleManagerCompositeGD);
        styleManagerComposite.setLayout(new GridLayout(1, false));
        lineStyleManager = new LineStyleManager();
        lineStyleManager.init(styleManagerComposite);

        createStyleButtons(styleViewerGroup);

        /*
         * properties area
         */
        Composite rightComposite = new Composite(mainComposite, SWT.NONE);
        rightComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        rightComposite.setLayout(new GridLayout(1, false));

        propertiesComposite = new Composite(rightComposite, SWT.NONE);
        propertiesStackLayout = new StackLayout();
        propertiesComposite.setLayout(propertiesStackLayout);
        GridData propertiesCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        // propertiesCompositeGD.horizontalSpan = 2;
        propertiesComposite.setLayoutData(propertiesCompositeGD);

        RuleWrapper ruleWrapper = styleWrapper.getFirstRule();
        if (ruleWrapper != null) {
            setRuleToSelected(ruleWrapper);
        } else {
            // if it came here, nothing to select
            Label l = new Label(propertiesComposite, SWT.SHADOW_ETCHED_IN);
            l.setText(Messages.LinePropertiesEditor_2);
            propertiesStackLayout.topControl = l;
        }
    }

    private TreeViewer createGroupRulesTableViewer( Composite rulesGroup ) {
        final TreeViewer rulesViewer = new TreeViewer(rulesGroup, SWT.SINGLE | SWT.BORDER);
        Control treeControl = rulesViewer.getControl();
        // table.setSize(PREVIEWWIDTH, -1);
        GridData treeGD = new GridData(SWT.FILL, SWT.TOP, true, false);
        treeGD.horizontalSpan = 6;
        treeGD.heightHint = 100;
        // tableGD.minimumWidth = PREVIEWWIDTH;
        treeControl.setLayoutData(treeGD);
        rulesViewer.setContentProvider(new GroupRulesTreeContentProvider());

        rulesViewer.setLabelProvider(new GroupRulesTreeLabelProvider(SLD.LINE));

        rulesViewer.addSelectionChangedListener(new ISelectionChangedListener(){

            private LinePropertiesComposite linePropertieComposite;

            public void selectionChanged( SelectionChangedEvent event ) {
                ISelection selection = event.getSelection();
                if (!(selection instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection sel = (IStructuredSelection) selection;

                if (sel.isEmpty()) {
                    showEmptyLabel();
                    return;
                }

                Object selectedItem = sel.getFirstElement();
                if (selectedItem == null) {
                    // unselected, show empty panel
                    return;
                }

                if (selectedItem instanceof FeatureTypeStyleWrapper) {
                    showEmptyLabel();
                }

                if (selectedItem instanceof RuleWrapper) {
                    RuleWrapper currentSelectedRule = (RuleWrapper) selectedItem;
                    if (propertiesComposite != null) {
                        if (linePropertieComposite == null) {
                            linePropertieComposite = new LinePropertiesComposite(LinePropertiesEditor.this, propertiesComposite);
                        }
                        linePropertieComposite.setRule(currentSelectedRule);
                        propertiesStackLayout.topControl = linePropertieComposite.getComposite();
                        propertiesComposite.layout();
                    }

                }
            }

            private void showEmptyLabel() {
                Label emptyLabel = new Label(propertiesComposite, SWT.NONE);
                emptyLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
                emptyLabel.setText(Messages.LinePropertiesEditor_3);
                propertiesStackLayout.topControl = emptyLabel;
                propertiesComposite.layout();
            }

        });
        return rulesViewer;
    }

    private void createRulesButtons( Composite rulesGroup ) {
        Image addGroupImg = ImageCache.getInstance().getImage(ImageCache.ADDGROUP);
        Image addImg = ImageCache.getInstance().getImage(ImageCache.ADD);
        Image delImg = ImageCache.getInstance().getImage(ImageCache.DELETE);
        Image delAllImg = ImageCache.getInstance().getImage(ImageCache.DELETEALL);
        Image upImg = ImageCache.getInstance().getImage(ImageCache.UP);
        Image downImg = ImageCache.getInstance().getImage(ImageCache.DOWN);

        final Button addGroupButton = new Button(rulesGroup, SWT.PUSH);
        addGroupButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        addGroupButton.setImage(addGroupImg);
        addGroupButton.setToolTipText(Messages.LinePropertiesEditor_4);
        addGroupButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                FeatureTypeStyle featureTypeStyle = Utilities.sf.createFeatureTypeStyle();
                FeatureTypeStyleWrapper addedFeatureTypeStyle = styleWrapper.addFeatureTypeStyle(featureTypeStyle);

                String tmpName = Messages.LinePropertiesEditor_5;
                tmpName = WrapperUtilities.checkSameNameFeatureTypeStyle(styleWrapper.getFeatureTypeStylesWrapperList(), tmpName);
                addedFeatureTypeStyle.setName(tmpName);

                reloadGroupsAndRules();
                refreshPreviewCanvasOnStyle();
            }
        });

        final Button addButton = new Button(rulesGroup, SWT.PUSH);
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        addButton.setImage(addImg);
        addButton.setToolTipText(Messages.LinePropertiesEditor_6);
        addButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
            	Object[] elements = groupRulesTreeViewer.getExpandedElements();
                FeatureTypeStyleWrapper selectedFtsw = getSelectedFtsw();
                if (selectedFtsw == null) {
                    RuleWrapper selectedRule = getSelectedRule();
                    if (selectedRule != null) {
                        selectedFtsw = selectedRule.getParent();
                    }
                }
                if (selectedFtsw == null) {
                    MessageDialog.openWarning(addButton.getShell(), Messages.LinePropertiesEditor_7,
                            Messages.LinePropertiesEditor_8);
                    return;
                }
                RuleWrapper addedRuleWrapper = selectedFtsw.addRule(null, LineSymbolizerWrapper.class);
                String tmpName = Messages.LinePropertiesEditor_9;
                tmpName = WrapperUtilities.checkSameNameRule(addedRuleWrapper.getParent().getRulesWrapperList(), tmpName);
                addedRuleWrapper.setName(tmpName);

                reloadGroupsAndRules();
                refreshPreviewCanvasOnStyle();
                groupRulesTreeViewer.setExpandedElements(elements);
                setRuleToSelected(addedRuleWrapper);
            }
        });

        Button deleteButton = new Button(rulesGroup, SWT.PUSH);
        deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        deleteButton.setImage(delImg);
        deleteButton.setToolTipText(Messages.LinePropertiesEditor_10);
        deleteButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
            	Object[] elements = groupRulesTreeViewer.getExpandedElements();
                FeatureTypeStyleWrapper selectedFtsw = getSelectedFtsw();
                RuleWrapper selectedRule = getSelectedRule();
                if (selectedFtsw != null) {
                    styleWrapper.removeFeatureTypeStyle(selectedFtsw);
                } else if (selectedRule != null) {
                    selectedRule.getParent().removeRule(selectedRule);
                } else {
                    MessageDialog.openWarning(addButton.getShell(), Messages.LinePropertiesEditor_11, Messages.LinePropertiesEditor_12);
                    return;
                }

                reloadGroupsAndRules();
                refreshPreviewCanvasOnStyle();
                
                groupRulesTreeViewer.setExpandedElements(elements);
            }
        });

        Button deleteAllButton = new Button(rulesGroup, SWT.PUSH);
        deleteAllButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        deleteAllButton.setImage(delAllImg);
        deleteAllButton.setToolTipText(Messages.LinePropertiesEditor_13);
        deleteAllButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                styleWrapper.clear();
                reloadGroupsAndRules();
                refreshPreviewCanvasOnStyle();
            }
        });

        Button upButton = new Button(rulesGroup, SWT.PUSH);
        upButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        upButton.setImage(upImg);
        upButton.setToolTipText(Messages.LinePropertiesEditor_14);
        upButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                swap(true);
            }
        });

        Button downButton = new Button(rulesGroup, SWT.PUSH);
        downButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        downButton.setImage(downImg);
        downButton.setToolTipText(Messages.LinePropertiesEditor_15);
        downButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                swap(false);
            }
        });
    }

    private void createStyleButtons( Composite rulesGroup ) {
        Image saveImg = ImageCache.getInstance().getImage(ImageCache.SAVE);
        Image saveAllImg = ImageCache.getInstance().getImage(ImageCache.SAVEALL);
        Image delImg = ImageCache.getInstance().getImage(ImageCache.DELETE);
        Image loadImg = ImageCache.getInstance().getImage(ImageCache.APPLY);
        // Image importImg = ImageCache.getInstance().getImage(ImageCache.IMPORT);
        Image exportImg = ImageCache.getInstance().getImage(ImageCache.ONECLICKEXPORT);
        Image openImg = ImageCache.getInstance().getImage(ImageCache.OPEN);

        final Button saveButton = new Button(rulesGroup, SWT.PUSH);
        saveButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        saveButton.setImage(saveImg);
        saveButton.setToolTipText(Messages.LinePropertiesEditor_16);
        saveButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                RuleWrapper selectedRule = getSelectedRule();

                FeatureTypeStyle featureTypeStyle = Utilities.sf.createFeatureTypeStyle();
                featureTypeStyle.rules().add(selectedRule.getRule());
                Style namedStyle = Utilities.sf.createStyle();
                namedStyle.featureTypeStyles().add(featureTypeStyle);
                namedStyle.setName(selectedRule.getName());

                try {
                    lineStyleManager.addStyle(new StyleWrapper(namedStyle));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        final Button saveAllButton = new Button(rulesGroup, SWT.PUSH);
        saveAllButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        saveAllButton.setImage(saveAllImg);
        saveAllButton.setToolTipText(Messages.LinePropertiesEditor_17);
        saveAllButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                String newStyleName = Messages.LinePropertiesEditor_18;
                InputDialog iDialog = new InputDialog(saveAllButton.getShell(), Messages.LinePropertiesEditor_19,
                        Messages.LinePropertiesEditor_20, newStyleName, null);
                iDialog.setBlockOnOpen(true);
                int open = iDialog.open();
                if (open == SWT.CANCEL) {
                    return;
                }
                String name = iDialog.getValue();
                if (name == null || name.length() == 0) {
                    name = newStyleName;
                }
                styleWrapper.setName(name);
                try {
                    lineStyleManager.addStyle(styleWrapper);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        });

        Button deleteButton = new Button(rulesGroup, SWT.PUSH);
        deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        deleteButton.setImage(delImg);
        deleteButton.setToolTipText(Messages.LinePropertiesEditor_21);
        deleteButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                StyleWrapper styleWrapper = lineStyleManager.getCurrentSelectedStyle();
                if (styleWrapper == null) {
                    return;
                }
                try {
                    lineStyleManager.removeStyle(styleWrapper);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        Button loadButton = new Button(rulesGroup, SWT.PUSH);
        loadButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        loadButton.setImage(loadImg);
        loadButton.setToolTipText(Messages.LinePropertiesEditor_22);
        loadButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                StyleWrapper styleWrapperToLoad = lineStyleManager.getCurrentSelectedStyle();
                if (styleWrapperToLoad == null) {
                    return;
                }

                List<FeatureTypeStyleWrapper> featureTypeStylesWrapperList = styleWrapperToLoad.getFeatureTypeStylesWrapperList();
                for( FeatureTypeStyleWrapper featureTypeStyleWrapper : featureTypeStylesWrapperList ) {
                    styleWrapper.addFeatureTypeStyle(featureTypeStyleWrapper.getFeatureTypeStyle());
                }

                reloadGroupsAndRules();
                refreshPreviewCanvasOnStyle();
            }
        });

        final Button exportButton = new Button(rulesGroup, SWT.PUSH);
        exportButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        exportButton.setImage(exportImg);
        exportButton.setToolTipText(Messages.LinePropertiesEditor_23);
        exportButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                IGeoResource geoResource = layer.getGeoResource();
                ID id = geoResource.getID();
                if (id.isFile()) {
                    try {
                        File file = id.toFile();
                        lineStyleManager.exportStyle(styleWrapper, file);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    MessageDialog.openWarning(exportButton.getShell(), Messages.LinePropertiesEditor_24,
                            Messages.LinePropertiesEditor_25);
                }
            }
        });

        final Button openFolderButton = new Button(rulesGroup, SWT.PUSH);
        openFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        openFolderButton.setImage(openImg);
        openFolderButton.setToolTipText(Messages.LinePropertiesEditor_26);
        openFolderButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                File styleFolderFile = lineStyleManager.getStyleFolderFile();
                Program.launch(styleFolderFile.getAbsolutePath());
            }
        });
    }
}
