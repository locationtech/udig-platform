/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.style.advanced.lines;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.style.internal.StyleLayer;
import net.refractions.udig.style.sld.SLD;

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

import eu.udig.style.advanced.common.GroupRulesTreeContentProvider;
import eu.udig.style.advanced.common.GroupRulesTreeLabelProvider;
import eu.udig.style.advanced.common.PropertiesEditor;
import eu.udig.style.advanced.common.styleattributeclasses.FeatureTypeStyleWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.LineSymbolizerWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.StyleWrapper;
import eu.udig.style.advanced.utils.ImageCache;
import eu.udig.style.advanced.utils.Utilities;

/**
 * An editor for line styles.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
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
        rulesAndPreviewGroup.setText("Preview, Groups and Rules");

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
        styleViewerGroup.setText("Style list");

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
            l.setText("No rule selected");
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
                emptyLabel.setText("No rule selected");
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
        addGroupButton.setToolTipText("Add a new group");
        addGroupButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                FeatureTypeStyle featureTypeStyle = Utilities.sf.createFeatureTypeStyle();
                FeatureTypeStyleWrapper addedFeatureTypeStyle = styleWrapper.addFeatureTypeStyle(featureTypeStyle);

                String tmpName = "New Group";
                tmpName = Utilities.checkSameNameFeatureTypeStyle(styleWrapper.getFeatureTypeStylesWrapperList(), tmpName);
                addedFeatureTypeStyle.setName(tmpName);

                reloadGroupsAndRules();
                refreshPreviewCanvasOnStyle();
            }
        });

        final Button addButton = new Button(rulesGroup, SWT.PUSH);
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        addButton.setImage(addImg);
        addButton.setToolTipText("Add a new rule");
        addButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                FeatureTypeStyleWrapper selectedFtsw = getSelectedFtsw();
                if (selectedFtsw == null) {
                    RuleWrapper selectedRule = getSelectedRule();
                    if (selectedRule != null) {
                        selectedFtsw = selectedRule.getParent();
                    }
                }
                if (selectedFtsw == null) {
                    MessageDialog.openWarning(addButton.getShell(), "Warning",
                            "Please select a group to which to add a new rule.");
                    return;
                }
                RuleWrapper addedRuleWrapper = selectedFtsw.addRule(null, LineSymbolizerWrapper.class);
                String tmpName = "New Rule";
                tmpName = Utilities.checkSameNameRule(addedRuleWrapper.getParent().getRulesWrapperList(), tmpName);
                addedRuleWrapper.setName(tmpName);

                reloadGroupsAndRules();
                refreshPreviewCanvasOnStyle();
                setRuleToSelected(addedRuleWrapper);
            }
        });

        Button deleteButton = new Button(rulesGroup, SWT.PUSH);
        deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        deleteButton.setImage(delImg);
        deleteButton.setToolTipText("Delete the selected rule");
        deleteButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                FeatureTypeStyleWrapper selectedFtsw = getSelectedFtsw();
                RuleWrapper selectedRule = getSelectedRule();
                if (selectedFtsw != null) {
                    styleWrapper.removeFeatureTypeStyle(selectedFtsw);
                } else if (selectedRule != null) {
                    selectedRule.getParent().removeRule(selectedRule);
                } else {
                    MessageDialog.openWarning(addButton.getShell(), "Warning", "No rule or group selected.");
                    return;
                }

                reloadGroupsAndRules();
                refreshPreviewCanvasOnStyle();
            }
        });

        Button deleteAllButton = new Button(rulesGroup, SWT.PUSH);
        deleteAllButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        deleteAllButton.setImage(delAllImg);
        deleteAllButton.setToolTipText("Delete all rules");
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
        upButton.setToolTipText("Move rule up");
        upButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                swap(true);
            }
        });

        Button downButton = new Button(rulesGroup, SWT.PUSH);
        downButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        downButton.setImage(downImg);
        downButton.setToolTipText("Move rule down");
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
        saveButton.setToolTipText("Save selected rule as style");
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
        saveAllButton.setToolTipText("Save all rules as style");
        saveAllButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                String newStyleName = "New Style";
                InputDialog iDialog = new InputDialog(saveAllButton.getShell(), "Style name",
                        "Please enter a name for the new style. Styles with same name are substituted.", newStyleName, null);
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
        deleteButton.setToolTipText("Remove selected style");
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
        loadButton.setToolTipText("Load selected style into the rules list");
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
        exportButton.setToolTipText("One click style export for file based layers");
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
                    MessageDialog.openWarning(exportButton.getShell(), "Warning",
                            "The selected layer doesn't seem to be file based.");
                }
            }
        });

        final Button openFolderButton = new Button(rulesGroup, SWT.PUSH);
        openFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        openFolderButton.setImage(openImg);
        openFolderButton.setToolTipText("Open the style library folder");
        openFolderButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                File styleFolderFile = lineStyleManager.getStyleFolderFile();
                Program.launch(styleFolderFile.getAbsolutePath());
            }
        });
    }
}
