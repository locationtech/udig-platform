/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.udig.omsbox.view.widgets;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.internal.ui.UDigByteAndLocalTransfer;
import net.refractions.udig.project.internal.impl.LayerImpl;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.core.FieldData;
import eu.udig.omsbox.utils.OmsBoxConstants;

/**
 * Class representing an swt files input list.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GuiFilespathListInputField extends ModuleGuiElement implements ModifyListener, FocusListener {

    private String constraints;
    private final FieldData data;
    private TableViewer tableViewer;

    public GuiFilespathListInputField( FieldData data, String constraints ) {
        this.data = data;
        this.constraints = constraints;

    }

    @Override
    public Control makeGui( Composite parent ) {

        parent = new Composite(parent, SWT.NONE);
        parent.setLayoutData(constraints);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        parent.setLayout(layout);

        tableViewer = new TableViewer(parent);

        Table table = tableViewer.getTable();
        GridData controlGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        controlGD.verticalSpan = OmsBoxConstants.LISTHEIGHT;
        table.setLayoutData(controlGD);

        tableViewer.setContentProvider(new ArrayContentProvider());
        createColumns(parent, tableViewer);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        tableViewer.setInput(Arrays.asList("")); //$NON-NLS-1$

        for( int i = 0; i < OmsBoxConstants.LISTHEIGHT; i++ ) {
            Label dummyLabel = new Label(parent, SWT.NONE);
            dummyLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, true));
        }

        addDrop();

        return tableViewer.getControl();
    }

    private void createColumns( final Composite parent, final TableViewer viewer ) {

        int[] bounds = {200, 100};
        String[] titles = {"name", "path"};

        TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
        col.setLabelProvider(new ColumnLabelProvider(){
            public Image getImage( Object element ) {
                return null;
            }

            public String getText( Object element ) {
                if (element instanceof String) {
                    String path = (String) element;
                    File file = new File(path);
                    return file.getName();
                }
                return null;
            }
        });

        col = createTableViewerColumn(titles[1], bounds[1], 1);
        col.setLabelProvider(new ColumnLabelProvider(){
            public Image getImage( Object element ) {
                return null;
            }
            public String getText( Object element ) {
                if (element instanceof String) {
                    String path = (String) element;
                    return path;
                }
                return null;
            }
        });

    }

    private TableViewerColumn createTableViewerColumn( String title, int bound, final int colNumber ) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;
    }

    public FieldData getFieldData() {
        return data;
    }

    public boolean hasData() {
        return true;
    }

    public void modifyText( ModifyEvent e ) {
    }

    public void focusGained( FocusEvent e ) {

    }

    @Override
    public void focusLost( FocusEvent e ) {
    }

    private void addDrop() {
        int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        Control control = tableViewer.getControl();
        DropTarget target = new DropTarget(control, operations);

        final TextTransfer textTransfer = TextTransfer.getInstance();
        final FileTransfer fileTransfer = FileTransfer.getInstance();
        final URLTransfer urlTransfer = URLTransfer.getInstance();
        final UDigByteAndLocalTransfer omsboxTransfer = UDigByteAndLocalTransfer.getInstance();
        Transfer[] types = new Transfer[]{fileTransfer, textTransfer, urlTransfer, omsboxTransfer};
        target.setTransfer(types);
        target.addDropListener(new DropTargetListener(){
            @SuppressWarnings("nls")
            public void drop( DropTargetEvent event ) {
                if (textTransfer.isSupportedType(event.currentDataType)) {
                    String text = (String) event.data;
                    System.out.println(text);
                }
                if (fileTransfer.isSupportedType(event.currentDataType)) {
                    String[] files = (String[]) event.data;
                    if (files.length > 0) {
                        File file = new File(files[0]);
                        if (file.exists()) {
                            addFileToViewer(file);
                            OmsBoxPlugin.getDefault().setLastFolderChosen(file.getParentFile().getAbsolutePath());
                        }
                    }
                }
                if (urlTransfer.isSupportedType(event.currentDataType)) {
                    Object data2 = event.data;
                    System.out.println(data2);
                }
                if (omsboxTransfer.isSupportedType(event.currentDataType)) {
                    try {
                        Object data = event.data;
                        if (data instanceof TreeSelection) {
                            TreeSelection selection = (TreeSelection) data;
                            Object firstElement = selection.getFirstElement();

                            IGeoResource geoResource = null;
                            if (firstElement instanceof LayerImpl) {
                                LayerImpl layer = (LayerImpl) firstElement;
                                geoResource = layer.getGeoResource();

                            }
                            if (firstElement instanceof IService) {
                                IService service = (IService) firstElement;
                                List< ? extends IGeoResource> resources = service.resources(new NullProgressMonitor());
                                if (resources.size() > 0) {
                                    geoResource = resources.get(0);
                                }
                            }
                            if (geoResource != null) {
                                ID id = geoResource.getID();
                                if (id != null)
                                    if (id.isFile()) {
                                        File file = id.toFile();
                                        if (file.exists()) {
                                            addFileToViewer(file);
                                            OmsBoxPlugin.getDefault().setLastFolderChosen(file.getParentFile().getAbsolutePath());
                                        }
                                    } else if (id.toString().contains("#") && id.toString().startsWith("file")) {
                                        // try to get the file
                                        String string = id.toString().replaceAll("#", "");
                                        URL url = new URL(string);
                                        File file = new File(url.toURI());
                                        if (file.exists()) {
                                            addFileToViewer(file);
                                            OmsBoxPlugin.getDefault().setLastFolderChosen(file.getParentFile().getAbsolutePath());
                                        }
                                    } else {
                                        System.out.println("Not a file: " + id.toString());
                                    }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                modifyText(null);
            }

            public void dragEnter( DropTargetEvent event ) {
            }
            public void dragLeave( DropTargetEvent event ) {
            }
            public void dragOperationChanged( DropTargetEvent event ) {
            }
            public void dragOver( DropTargetEvent event ) {
            }
            public void dropAccept( DropTargetEvent event ) {
            }
        });

    }

    @SuppressWarnings({"unchecked", "nls"})
    private void addFileToViewer( File file ) {
        List<String> fileList = (List<String>) tableViewer.getInput();
        List<String> newFileList = new ArrayList<String>();
        for( String path : fileList ) {
            if (path != null && path.length() > 0)
                newFileList.add(path);
        }

        String absolutePath = file.getAbsolutePath();
        if (!newFileList.contains(absolutePath)) {
            newFileList.add(absolutePath);
            tableViewer.setInput(newFileList);

            StringBuilder sb = new StringBuilder();
            sb.append("Arrays.asList(");
            for( String path : newFileList ) {
                path = checkBackSlash(path, true);
                sb.append("\"");
                sb.append(path);
                sb.append("\",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
            data.fieldValue = sb.toString();
        }
    }

    @Override
    public String validateContent() {
        return null;
    }
}
