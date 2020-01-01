/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2013, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.wizard.export;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.ui.AdapterFactoryLabelProviderDecorator;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectExplorer;
import org.locationtech.udig.project.ui.wizard.export.image.ExportMapToImageWizard;

/**
 * Wizard Page for selecting the maps to export.
 * 
 * @author Jesse
 */
public class MapSelectorPage extends WizardPage {

    private Collection<IMap> maps = new java.util.HashSet<IMap>();
    protected StructuredViewer viewer;
    private Text destText;

    /**
     * Create a new instance
     * @param banner 
     * @param title 
     */
    public MapSelectorPage(String page, String title, ImageDescriptor banner) {
        super( page,
               title != null ? title : Messages.MapSelectorPage_pageName,
               banner );
        
        setPageComplete(!getMaps().isEmpty());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        comp.setLayout(layout);
        createExportLabel(comp);

        String tooltip = Messages.ImageExportPage_directoryToolTip;

        createExportText(comp, tooltip);

        createExportBrowseButton(comp, tooltip);

        createMapSelector(comp);

        setControl(comp);
    }
    

    private void createExportBrowseButton(Composite comp, String tooltip) {
        Button browse = new Button(comp, SWT.PUSH);
        browse.setText(Messages.ImageExportPage_browse);
        browse.setToolTipText(tooltip);
        browse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        browse.addSelectionListener(new SelectButtonListener());
    }

    private void createExportText(Composite comp, String tooltip) {
        destText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        destText.setToolTipText(tooltip);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        destText.setLayoutData(gridData);
        String previousLocation = getWizard().getDialogSettings().get(
                ExportMapToImageWizard.DIRECTORY_KEY);
        if (previousLocation != null) {
            destText.setText(previousLocation);
        } else {
            destText.setText(Platform.getLocation().toOSString());
        }
    }

    private void createExportLabel(Composite comp) {
        Label scaleLabel = new Label(comp, SWT.NONE);
        scaleLabel.setText(Messages.ImageExportPage_exportLabelText);
        scaleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
                false));
    }

    public File getExportDir() {
        return new File(this.destText.getText());
    }


    private void createMapSelector( Composite listComp ) {
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        listComp.setLayoutData(layoutData);
        listComp.setLayout(new GridLayout(3, false));

        Label label = new Label(listComp, SWT.NONE);
        label.setText(Messages.MapSelectorPage_selectMapsLabel);
        layoutData = new GridData(SWT.FILL, SWT.NONE, true, false);
        layoutData.horizontalSpan = 3;
        label.setLayoutData(layoutData);

        initializeViewer(listComp);

        Button add = new Button(listComp, SWT.PUSH);
        add.setText(Messages.MapSelectorPage_Add);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        add.setLayoutData(gridData);
        add.addSelectionListener(new AddMapsSelectionListener(this));

        Button remove = new Button(listComp, SWT.PUSH);
        remove.setText(Messages.MapSelectorPage_Remove);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        remove.setLayoutData(gridData);        
        remove.addListener(SWT.Selection, new Listener(){

            @SuppressWarnings("unchecked")
            public void handleEvent( Event event ) {
                IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                Iterator iter = selection.iterator();

                while( iter.hasNext() ) {
                    IMap map = (IMap) iter.next();
                    maps.remove(map);
                }

                viewer.refresh(false);
            }

        });

    }

    /**
     * By default creates a table viewer. Calls add columns in order to create the required columns
     * for the table.
     * 
     * @param listComp the parent Composite.
     */
    protected final void initializeViewer( Composite listComp ) {
        GridData layoutData;
        TableViewer tableViewer = new TableViewer(listComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        this.viewer = tableViewer;
        Table table = tableViewer.getTable();
        TableLayout tableLayout = new TableLayout();
        table.setLayout(tableLayout);
        tableLayout.addColumnData(new ColumnWeightData(1, true));
        createColumns(table, tableLayout);
        viewer.getControl().setToolTipText(Messages.MapSelectorPage_selectmapsExport);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.horizontalSpan = 2;
        layoutData.verticalSpan = 3;
        viewer.setContentProvider(new ContentProvider());
        viewer.getControl().setLayoutData(layoutData);
        viewer.setLabelProvider(createLabelProvider(viewer));
        viewer.setInput(maps);
        
        configureEditors(tableViewer);
    }

    /**
     * Implementation does nothing
     * 
     * @param viewer2 the viewer created by
     */
    protected void configureEditors( TableViewer viewer2 ) {
        
    }

    /**
     * Creates the label provider to use in the table viewer.
     * 
     * This version creates an AdapterFactoryLabelProviderDecorator.
     * @param viewer2 
     * 
     * @return
     */
    protected IBaseLabelProvider createLabelProvider(StructuredViewer viewer2) {
        return new AdapterFactoryLabelProviderDecorator(ProjectExplorer.getProjectExplorer()
                .getAdapterFactory(), viewer2);
    }

    /**
     * Creates the columns that are shown in the viewer. The default is a single "Map" column. The
     * table that is passed in has one column and uses TableLayout with ColumnWeightData. The
     * headers are not visible. 
     * <p>
     * This method simply renames the first (only) column's name to "Name"
     * </p>
     * 
     * @param table table to add columns to
     * @param tableLayout
     */
    protected void createColumns( Table table, TableLayout tableLayout ) {
        TableColumn column = new TableColumn(table, SWT.DEFAULT);
        column.setText(Messages.MapSelectorPage_mapNameColumnText);
        column.setResizable(true);
        column.setAlignment(SWT.LEFT);
    }

    /**
     * Updates the list of selected maps
     * 
     * @param mapList
     */
    public void updateMapList() {
        if (viewer != null) {
            viewer.refresh(false);
        }
    }

    public Collection<IMap> getMaps() {
        return maps;
    }

    /**
     * Sets the set of selected maps
     * 
     * @param selection
     */
    public void setSelection( IStructuredSelection selection ) {
        maps = new HashSet<IMap>();
        addToSelection(selection);

    }

    /**
     * Adds the selection to the set of selected maps
     * 
     * @param selection selections to inspect
     */
    @SuppressWarnings("unchecked")
    public void addToSelection( IStructuredSelection selection ) {
        Iterator iter = selection.iterator();
        while( iter.hasNext() ) {
            Object next = iter.next();
            IMap map = cast(maps, next, IMap.class);
            if (map != null) {
                maps.add(map);
            } else {
                IProject project = cast(maps, next, IProject.class);
                if (project != null) {
                    maps.addAll(project.getElements(IMap.class));
                }
            }
        }

        if (maps.isEmpty()) {
            IMap activeMap = ApplicationGIS.getActiveMap();
            if (activeMap != ApplicationGIS.NO_MAP) {
                maps.add(activeMap);
            }
        }
        updateMapList();
        setPageComplete(!maps.isEmpty());
    }

    private <T> T cast( Collection<IMap> maps, Object next, Class<T> mapClass ) {
        if (mapClass.isAssignableFrom(next.getClass())) {
            return mapClass.cast(next);
        } else if (next instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) next;
            if (adaptable.getAdapter(mapClass) != null) {
                return mapClass.cast(adaptable.getAdapter(mapClass));
            }
        }

        return null;
    }
    public class SelectButtonListener implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }

        public void widgetSelected(SelectionEvent e) {
            DirectoryDialog d = new DirectoryDialog(e.widget.getDisplay()
                    .getActiveShell());
            String selection = d.open();
            if (selection != null) {
                destText.setText(selection);
            }
        }

    }
}
