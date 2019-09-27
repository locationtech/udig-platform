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
package org.locationtech.udig.project.ui.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.ColorBrewer;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.graphics.Glyph;

/**
 * Used to allow the MapPreferencePage to select a palette to draw on when assigning colours to
 * new layers.
 */
public class PaletteSelectionFieldEditor extends FieldEditor {

    TableViewer palettes;
    
    public PaletteSelectionFieldEditor(String name, String string, Composite parent) {
        super(name, string, parent);
    }

    @Override
    protected void adjustForNumColumns( int numColumns ) {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) palettes.getControl().getLayoutData()).horizontalSpan = numColumns;
    }

    @Override
    protected void doFillIntoGrid( Composite parent, int numColumns ) {
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        palettes = getTableControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.heightHint = 95;
        palettes.getControl().setLayoutData(gd);

//        buttonBox = getButtonBoxControl(parent);
//        gd = new GridData();
//        gd.verticalAlignment = GridData.BEGINNING;
//        buttonBox.setLayoutData(gd);
    }

    private TableViewer getTableControl(Composite parent) {
        TableViewer paletteTable = new TableViewer(new Table(parent, SWT.BORDER | SWT.V_SCROLL
                | SWT.H_SCROLL | SWT.FULL_SELECTION));
        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1, 20, false));
        TableColumn firstColumn = new TableColumn(paletteTable.getTable(), SWT.LEFT);
        firstColumn.setAlignment(SWT.LEFT);
        paletteTable.getTable().setLayout(tableLayout);
//        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
//        gridData.horizontalSpan = 2;
//        gridData.heightHint = 150;
//        gridData.widthHint = 175;

        paletteTable.setLabelProvider(new LabelProvider() {
            public Image getImage(Object element) {
                if (element instanceof BrewerPalette) {
                    BrewerPalette palette = (BrewerPalette) element;
                    return Glyph.palette(palette.getColors(palette.getMaxColors())).createImage();
                }
                return null;
            }

            public String getText(Object element) {
                if (element instanceof BrewerPalette) {
                    BrewerPalette palette = (BrewerPalette) element;
                    String text = null;
                    text = palette.getName() + ": " + palette.getDescription(); //$NON-NLS-1$
                    if (text == null) text = palette.getName();
                    return text; 
                }
                return null;
            }
        });

        paletteTable.setContentProvider(new IStructuredContentProvider() {
            
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof ColorBrewer) {
                    ColorBrewer brewer = (ColorBrewer) inputElement;
                    return brewer.getPalettes();
                } else {
                    return new Object[0];
                }
            } 

            public void dispose() {
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        });

        paletteTable.setSorter(new ViewerSorter() {

            @Override
            public int compare( Viewer viewer, Object e1, Object e2 ) {
                if (e1 instanceof BrewerPalette && e2 instanceof BrewerPalette) {
                    BrewerPalette p1 = (BrewerPalette) e1;
                    BrewerPalette p2 = (BrewerPalette) e2;
                    //alphabetical by name
                    return p1.getName().compareTo(p2.getName());
                } else return super.compare(viewer, e1, e2);
            }
            
        });
        
        paletteTable.setInput(PlatformGIS.getColorBrewer());
        return paletteTable;
    }
    
    @Override
    protected void doLoad() {
        if (palettes != null) {
            select(getPreferenceStore().getString(getPreferenceName()));
        }
    }

    @Override
    protected void doLoadDefault() {
        if (palettes != null) {
            select(getPreferenceStore().getDefaultString(getPreferenceName()));
        }
    }

    private void select(String paletteName) {
        ColorBrewer brewer = PlatformGIS.getColorBrewer();
        if (paletteName == null || !brewer.hasPalette(paletteName))
            return;
        BrewerPalette palette = brewer.getPalette(paletteName);
        palettes.setSelection(new StructuredSelection(palette));
    }
    
    @Override
    protected void doStore() {
        ISelection select = palettes.getSelection();
        if (select == null) {
            getPreferenceStore().setToDefault(getPreferenceName());
            return;
        }
        BrewerPalette palette = (BrewerPalette) ((StructuredSelection) select).getFirstElement();
        getPreferenceStore().setValue(getPreferenceName(), palette.getName());
    }

    @Override
    public int getNumberOfControls() {
        return 2;
    }

}
