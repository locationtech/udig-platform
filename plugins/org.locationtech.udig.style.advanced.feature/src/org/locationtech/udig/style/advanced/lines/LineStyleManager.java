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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.geotools.styling.Style;
import org.locationtech.udig.style.advanced.StylePlugin;
import org.locationtech.udig.style.advanced.common.StyleFilter;
import org.locationtech.udig.style.advanced.common.StyleManager;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.FeatureTypeStyleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.StyleWrapper;
import org.locationtech.udig.style.advanced.internal.WrapperUtilities;
import org.locationtech.udig.style.advanced.utils.Utilities;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;

/**
 * A style viewer that manages {@link Style}s.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class LineStyleManager extends StyleManager{

    /**
     * Creates a new style manager pointing to the default preferences folder for style.
     */
    public LineStyleManager() {
        IPath stateLocation = StylePlugin.getDefault().getStateLocation();
        File stateLocationFile = stateLocation.toFile();

        styleFolderFile = new File(stateLocationFile, LINESTYLEFOLDER);
        if (!styleFolderFile.exists()) {
            styleFolderFile.mkdirs();
        }

    }

    /**
     * Creates the style viewer panel.
     * 
     * @param parent the parent {@link Composite}.
     */
    public void init( Composite parent ) {
        createStylesTableViewer(parent);

        reloadStyleFolder();
    }

    private TableViewer createStylesTableViewer( Composite parent ) {
        final StyleFilter filter = new StyleFilter();
        final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
        searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        searchText.addKeyListener(new KeyAdapter(){
            public void keyReleased( KeyEvent ke ) {
                filter.setSearchText(searchText.getText());
                stylesViewer.refresh();
            }

        });

        stylesViewer = new TableViewer(parent, SWT.SINGLE | SWT.BORDER);
        Table table = stylesViewer.getTable();
        GridData tableGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableGD.heightHint = 200;
        table.setLayoutData(tableGD);

        stylesViewer.addFilter(filter);
        stylesViewer.setContentProvider(new IStructuredContentProvider(){
            public Object[] getElements( Object inputElement ) {
                if (inputElement instanceof List< ? >) {
                    List< ? > styles = (List< ? >) inputElement;
                    StyleWrapper[] array = (StyleWrapper[]) styles.toArray(new StyleWrapper[styles.size()]);
                    return array;
                }
                return null;
            }
            public void dispose() {
            }
            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
            }
        });

        stylesViewer.setLabelProvider(new LabelProvider(){
            public Image getImage( Object element ) {
                if (element instanceof StyleWrapper) {
                    StyleWrapper styleWrapper = (StyleWrapper) element;
                    List<FeatureTypeStyleWrapper> featureTypeStylesWrapperList = styleWrapper.getFeatureTypeStylesWrapperList();
                    int iconSize = 48;
                    BufferedImage image = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = image.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    for( FeatureTypeStyleWrapper featureTypeStyle : featureTypeStylesWrapperList ) {
                        List<RuleWrapper> rulesWrapperList = featureTypeStyle.getRulesWrapperList();
                        BufferedImage tmpImage = WrapperUtilities.lineRulesWrapperToImage(rulesWrapperList, iconSize, iconSize);
                        g2d.drawImage(tmpImage, 0, 0, null);
                    }
                    g2d.dispose();
                    Image convertToSWTImage = AWTSWTImageUtils.convertToSWTImage(image);
                    return convertToSWTImage;
                }
                return null;
            }

            public String getText( Object element ) {
                if (element instanceof StyleWrapper) {
                    StyleWrapper styleWrapper = (StyleWrapper) element;
                    String styleName = styleWrapper.getName();
                    if (styleName == null || styleName.length() == 0) {
                        styleName = Utilities.DEFAULT_STYLENAME;
                        styleName = WrapperUtilities.checkSameNameStyle(getStyles(), styleName);
                        styleWrapper.setName(styleName);
                    }
                    return styleName;
                }
                return ""; //$NON-NLS-1$
            }
        });

        stylesViewer.addSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged( SelectionChangedEvent event ) {
                ISelection selection = event.getSelection();
                if (!(selection instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection sel = (IStructuredSelection) selection;
                if (sel.isEmpty()) {
                    return;
                }

                Object selectedItem = sel.getFirstElement();
                if (selectedItem == null) {
                    // unselected, show empty panel
                    return;
                }

                if (selectedItem instanceof StyleWrapper) {
                    currentSelectedStyleWrapper = (StyleWrapper) selectedItem;
                }
            }

        });
        return stylesViewer;
    }

//    /**
//     * Add a style to the {@link TableViewer viewer} from a file.
//     * 
//     * @param files the array of files to import.
//     * @throws Exception
//     */
//    public void importToStyle( File... files ) throws Exception {
//        List<Style> styles = getStyles();
//
//        for( File file : files ) {
//            String name = file.getName();
//            File newFile = new File(styleFolderFile, name);
//            FileUtils.copyFile(file, newFile);
//            Style style = Utilities.createStyleFromGraphic(newFile);
//            styles.add(style);
//            styleToDisk(style);
//        }
//        stylesViewer.refresh(false, true);
//    }


}
