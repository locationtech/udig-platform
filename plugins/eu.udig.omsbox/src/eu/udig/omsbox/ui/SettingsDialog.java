/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.omsbox.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import eu.udig.omsbox.OmsBoxPlugin;

/**
 * Jar chooser dialog.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class SettingsDialog {

    private Dialog dialog;

    private List<String> resourcesList;
    private String currentSelectedJarPath;

    private TableViewer tableViewer;

    private boolean cancelPressed = false;

    public void open( Shell parentShell, final int selectionType ) {

        resourcesList = new ArrayList<String>();

        String[] retrieveSavedJars = OmsBoxPlugin.getDefault().retrieveSavedJars();
        for( String jarPath : retrieveSavedJars ) {
            resourcesList.add(jarPath);
        }

        dialog = new Dialog(parentShell){

            @Override
            protected void configureShell( Shell shell ) {
                super.configureShell(shell);
                shell.setText(""); //$NON-NLS-1$
            }

            @Override
            protected Point getInitialSize() {
                return new Point(640, 450);
            }

            @Override
            protected Control createDialogArea( Composite parent ) {
                Composite parentPanel = (Composite) super.createDialogArea(parent);

                final TabFolder tabFolder = new TabFolder(parentPanel, SWT.NONE);
                GridData tabFolderGD = new GridData(SWT.FILL, SWT.FILL, true, true);
                tabFolder.setLayoutData(tabFolderGD);

                TabItem librariesTabItem = new TabItem(tabFolder, SWT.NULL);
                librariesTabItem.setText("General Settings");
                Composite librariesPanel = new Composite(tabFolder, SWT.NONE);
                librariesPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                librariesPanel.setLayout(new GridLayout(2, true));

                createWorkingFolderText(librariesPanel);

                final Group librariesGroup = new Group(librariesPanel, SWT.None);
                GridData librariesGroupGD = new GridData(SWT.FILL, SWT.FILL, true, true);
                librariesGroupGD.horizontalSpan = 2;
                librariesGroup.setLayoutData(librariesGroupGD);
                librariesGroup.setLayout(new GridLayout(2, false));
                librariesGroup.setText("Modules Libraries");
                createTableViewer(librariesGroup);
                createAddRemoveButtons(librariesGroup);
                librariesTabItem.setControl(librariesPanel);

                TabItem grassTabItem = new TabItem(tabFolder, SWT.NULL);
                grassTabItem.setText("Grass settings");
                Composite grassPanel = new Composite(tabFolder, SWT.NONE);
                grassPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                grassPanel.setLayout(new GridLayout(1, false));
                grassTabItem.setControl(grassPanel);

                createGrassPanel(grassPanel);

                return parentPanel;
            }

            @Override
            protected void buttonPressed( int buttonId ) {
                if (buttonId == OK) {
                    OmsBoxPlugin.getDefault().saveJars(resourcesList);
                    cancelPressed = false;
                } else {
                    cancelPressed = true;
                }
                super.buttonPressed(buttonId);
            }
        };
        dialog.setBlockOnOpen(true);
        dialog.open();
    }

    public boolean isDisposed() {
        return dialog.getShell().isDisposed();
    }

    public void widgetSelected( SelectionEvent e ) {

    }

    public List<String> getSelectedResources() {
        return resourcesList;
    }

    public String getNameOfResourceAtIndex( int index ) {
        return resourcesList.get(index);
    }

    public boolean isCancelPressed() {
        return cancelPressed;
    }

    private void createWorkingFolderText( Composite librariesPanel ) {
        final Group workingFolderGroup = new Group(librariesPanel, SWT.None);
        GridData workingFolderGroupGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        workingFolderGroupGD.horizontalSpan = 2;
        workingFolderGroup.setLayoutData(workingFolderGroupGD);
        workingFolderGroup.setLayout(new GridLayout(2, false));
        workingFolderGroup.setText("Optional Spatial Toolbox Working Folder");

        final Text pathText = new Text(workingFolderGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        pathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        String workingFolder = OmsBoxPlugin.getDefault().getWorkingFolder();
        if (workingFolder == null) {
            workingFolder = "";
        }
        pathText.setText(workingFolder);
        pathText.setEditable(false);

        Button browseButton = new Button(workingFolderGroup, SWT.PUSH);
        browseButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        browseButton.setText("...");
        browseButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                DirectoryDialog folderDialog = new DirectoryDialog(workingFolderGroup.getShell(), SWT.OPEN);
                String path = folderDialog.open();
                if (path != null && path.length() >= 1) {
                    OmsBoxPlugin.getDefault().setWorkingFolder(path);
                    pathText.setText(path);
                }
            }
        });
    }

    private void createGrassPanel( Composite grassPanel ) {

        final Group gisbaseGroup = new Group(grassPanel, SWT.NONE);
        gisbaseGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        gisbaseGroup.setLayout(new GridLayout(2, false));
        gisbaseGroup.setText("Grass Gisbase Path");

        final Text pathText = new Text(gisbaseGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        pathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        String gisbasePreference = OmsBoxPlugin.getDefault().getGisbasePreference();
        if (gisbasePreference == null) {
            gisbasePreference = "";
        }
        pathText.setText(gisbasePreference);
        pathText.setEditable(false);

        Button browseButton = new Button(gisbaseGroup, SWT.PUSH);
        browseButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        browseButton.setText("...");
        browseButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                DirectoryDialog folderDialog = new DirectoryDialog(gisbaseGroup.getShell(), SWT.OPEN);
                String path = folderDialog.open();
                if (path != null && path.length() >= 1) {
                    OmsBoxPlugin.getDefault().setGisbasePreference(path);
                    pathText.setText(path);
                }
            }
        });

        final Group shellGroup = new Group(grassPanel, SWT.NONE);
        shellGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        shellGroup.setLayout(new GridLayout(2, false));
        shellGroup.setText("Grass Shell");

        final Text shellText = new Text(shellGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        shellText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        String grassShell = OmsBoxPlugin.getDefault().getShellPreference();
        if (grassShell == null) {
            grassShell = "";
        }
        shellText.setText(grassShell);
        shellText.setEditable(false);

        Button shellButton = new Button(shellGroup, SWT.PUSH);
        shellButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        shellButton.setText("...");
        shellButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog fileDialog = new FileDialog(shellGroup.getShell(), SWT.OPEN);
                String path = fileDialog.open();
                if (path != null && path.length() >= 1) {
                    OmsBoxPlugin.getDefault().setShellPreference(path);
                    shellText.setText(path);
                }
            }
        });

    }

    private void createTableViewer( Composite parentPanel ) {
        tableViewer = new TableViewer(parentPanel);

        Control control = tableViewer.getControl();
        GridData controlGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        controlGD.horizontalSpan = 2;
        control.setLayoutData(controlGD);

        tableViewer.setContentProvider(new IStructuredContentProvider(){

            public Object[] getElements( Object inputElement ) {
                if (inputElement instanceof List) {
                    List< ? > paths = (List< ? >) inputElement;
                    Object[] array = (Object[]) paths.toArray(new Object[paths.size()]);
                    return array;
                }
                return new String[0];
            }
            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
            }
            public void dispose() {
            }

        });

        tableViewer.setLabelProvider(new LabelProvider(){
            public Image getImage( Object element ) {
                return null;
            }

            public String getText( Object element ) {
                if (element instanceof String) {
                    String jarPath = (String) element;
                    return jarPath;
                }
                return ""; //$NON-NLS-1$
            }
        });

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged( SelectionChangedEvent event ) {
                if (!(event.getSelection() instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection sel = (IStructuredSelection) event.getSelection();

                Object selectedItem = sel.getFirstElement();
                if (selectedItem == null) {
                    return;
                }

                if (selectedItem instanceof String) {
                    currentSelectedJarPath = (String) selectedItem;
                }
            }
        });

        if (resourcesList.size() > 0) {
            tableViewer.setInput(resourcesList);
        } else {
            tableViewer.setInput(""); //$NON-NLS-1$
        }
    }
    private void createAddRemoveButtons( Composite parentPanel ) {

        final Button addButton = new Button(parentPanel, SWT.PUSH);
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        addButton.setText("+"); //$NON-NLS-1$
        addButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog fileDialog = new FileDialog(addButton.getShell(), SWT.OPEN | SWT.MULTI);
                fileDialog.setFilterExtensions(new String[]{"*.jar"}); //$NON-NLS-1$
                fileDialog.setFilterPath(OmsBoxPlugin.getDefault().getLastFolderChosen());
                String path = fileDialog.open();
                if (path == null || path.length() < 1) {
                    return;
                }

                String filterPath = fileDialog.getFilterPath();
                OmsBoxPlugin.getDefault().setLastFolderChosen(filterPath);
                String[] fileNames = fileDialog.getFileNames();
                for( String fileName : fileNames ) {
                    String jarPath = filterPath + File.separator + fileName;
                    if (!resourcesList.contains(jarPath)) {
                        resourcesList.add(jarPath);
                    }
                }
                tableViewer.setInput(resourcesList);
            }
        });

        final Button removeButton = new Button(parentPanel, SWT.PUSH);
        removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        removeButton.setText("-"); //$NON-NLS-1$
        removeButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                if (currentSelectedJarPath != null) {
                    resourcesList.remove(currentSelectedJarPath);
                    tableViewer.setInput(resourcesList);
                }
            }
        });

    }
}
