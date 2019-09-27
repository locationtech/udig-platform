/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.view.widgets;

import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.internal.ui.UDigByteAndLocalTransfer;
import org.locationtech.udig.project.BlackboardEvent;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IBlackboardListener;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.project.internal.impl.LayerImpl;
import org.locationtech.udig.project.render.IRenderManager;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.ui.CRSChooser;
import org.locationtech.udig.ui.Controller;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;

import org.locationtech.udig.omsbox.OmsBoxPlugin;
import org.locationtech.udig.omsbox.core.FieldData;
import org.locationtech.udig.omsbox.processingregion.ProcessingRegion;
import org.locationtech.udig.omsbox.processingregion.ProcessingRegionStyle;
import org.locationtech.udig.omsbox.processingregion.ProcessingRegionStyleContent;
import org.locationtech.udig.omsbox.utils.MapcalculatorUtils;
import org.locationtech.udig.omsbox.utils.OmsBoxConstants;
import org.locationtech.udig.omsbox.utils.OmsBoxUtils;

/**
 * Class representing an swt textfield gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GuiTextField extends ModuleGuiElement implements ModifyListener, FocusListener {

    private StyledText text;
    private String constraints;
    private final FieldData data;
    private boolean isInFile;
    private boolean isInFolder;
    private boolean isOutFile;
    private boolean isOutFolder;
    private boolean isCrs;
    private boolean isEastingNorthing;
    private boolean isNorthing;
    private boolean isEasting;
    private boolean isMultiline;
    private boolean isProcessingNorth;
    private boolean isProcessingSouth;
    private boolean isProcessingWest;
    private boolean isProcessingEast;
    private boolean isProcessingCols;
    private boolean isProcessingRows;
    private boolean isProcessingXres;
    private boolean isProcessingYres;
    private boolean isMapcalc;
    private boolean isGrassfile;

    private MapMouseListener currentMapMouseListener;
    private IBlackboardListener currentBlackboardListener;

    private int rows;

    public GuiTextField( FieldData data, String constraints ) {
        this.data = data;
        this.constraints = constraints;

        if (data.guiHints != null) {
            if (data.guiHints.contains(OmsBoxConstants.FILEIN_UI_HINT)) {
                isInFile = true;
            }
            if (data.guiHints.contains(OmsBoxConstants.FOLDERIN_UI_HINT)) {
                isInFolder = true;
            }
            if (data.guiHints.contains(OmsBoxConstants.FILEOUT_UI_HINT)) {
                isOutFile = true;
            }
            if (data.guiHints.contains(OmsBoxConstants.FOLDEROUT_UI_HINT)) {
                isOutFolder = true;
            }
            if (data.guiHints.contains(OmsBoxConstants.CRS_UI_HINT)) {
                isCrs = true;
            }
            if (data.guiHints.contains(OmsBoxConstants.EASTINGNORTHING_UI_HINT)) {
                isEastingNorthing = true;
            } else if (data.guiHints.contains(OmsBoxConstants.NORTHING_UI_HINT)) {
                isNorthing = true;
            } else if (data.guiHints.contains(OmsBoxConstants.EASTING_UI_HINT)) {
                isEasting = true;
            }
            if (data.guiHints.contains(OmsBoxConstants.MULTILINE_UI_HINT)) {
                isMultiline = true;

                String[] split = data.guiHints.split(","); //$NON-NLS-1$
                for( String string : split ) {
                    String hint = string.trim();
                    if (hint.startsWith(OmsBoxConstants.MULTILINE_UI_HINT)) {
                        hint = hint.replaceFirst(OmsBoxConstants.MULTILINE_UI_HINT, ""); //$NON-NLS-1$
                        rows = 1;
                        try {
                            rows = Integer.parseInt(hint);
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
            }
            if (data.guiHints.contains(OmsBoxConstants.MAPCALC_UI_HINT)) {
                isMapcalc = true;
            }
            if (data.guiHints.contains(OmsBoxConstants.GRASSFILE_UI_HINT)) {
                isGrassfile = true;
            }
            if (data.guiHints.contains(OmsBoxConstants.PROCESS_NORTH_UI_HINT)) {
                isProcessingNorth = true;
            } else if (data.guiHints.contains(OmsBoxConstants.PROCESS_SOUTH_UI_HINT)) {
                isProcessingSouth = true;
            } else if (data.guiHints.contains(OmsBoxConstants.PROCESS_WEST_UI_HINT)) {
                isProcessingWest = true;
            } else if (data.guiHints.contains(OmsBoxConstants.PROCESS_EAST_UI_HINT)) {
                isProcessingEast = true;
            } else if (data.guiHints.contains(OmsBoxConstants.PROCESS_COLS_UI_HINT)) {
                isProcessingCols = true;
            } else if (data.guiHints.contains(OmsBoxConstants.PROCESS_ROWS_UI_HINT)) {
                isProcessingRows = true;
            } else if (data.guiHints.contains(OmsBoxConstants.PROCESS_XRES_UI_HINT)) {
                isProcessingXres = true;
            } else if (data.guiHints.contains(OmsBoxConstants.PROCESS_YRES_UI_HINT)) {
                isProcessingYres = true;
            }
        }
    }

    @Override
    public Control makeGui( Composite parent ) {
        int cols = 1;
        if (isInFile || isInFolder || isOutFile || isOutFolder || isCrs || isMultiline) {
            cols = 2;
        }

        final boolean isFile = isInFile || isOutFile;
        final boolean isFolder = isInFolder || isOutFolder;

        parent = new Composite(parent, SWT.NONE);
        parent.setLayoutData(constraints);
        GridLayout layout = new GridLayout(cols, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        parent.setLayout(layout);

        if (!isMultiline) {
            text = new StyledText(parent,SWT.RIGHT | SWT.BORDER);
            GridData textGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
            textGD.widthHint = 100;
            text.setLayoutData(textGD);
            // text.setLineAlignment(0, 1, SWT.RIGHT);
        } else if (isMapcalc) {
            text = MapcalculatorUtils.createMapcalcPanel(parent, rows);
        } else {
            text = new StyledText(parent, SWT.MULTI | SWT.WRAP | SWT.LEAD | SWT.BORDER | SWT.V_SCROLL);
            GridData textGD = new GridData(SWT.FILL, SWT.CENTER, true, true);
            textGD.verticalSpan = rows;
            textGD.widthHint = 100;
            text.setLayoutData(textGD);
        }
        text.addModifyListener(this);
        text.addFocusListener(this);
        if (data.fieldValue != null) {
            String tmp = data.fieldValue;

            if (tmp.contains(OmsBoxConstants.WORKINGFOLDER)) {
                // check if there is a working folder defined
                String workingFolder = OmsBoxPlugin.getDefault().getWorkingFolder();
                workingFolder = checkBackSlash(workingFolder, true);
                if (workingFolder != null) {
                    tmp = tmp.replaceFirst(OmsBoxConstants.WORKINGFOLDER, workingFolder);
                    data.fieldValue = tmp;
                } else {
                    data.fieldValue = "";
                }
            }
            data.fieldValue = checkBackSlash(data.fieldValue, isFile);
            text.setText(data.fieldValue);
            // text.setSelection(text.getCharCount());
        }

        if (isMultiline) {
            for( int i = 0; i < rows; i++ ) {
                Label dummyLabel = new Label(parent, SWT.NONE);
                dummyLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
                // dummyLabel.setText("");

            }
        }

        if (isFile) {
            final Button browseButton = new Button(parent, SWT.PUSH);
            browseButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
            browseButton.setText("...");
            browseButton.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected( SelectionEvent e ) {
                    FileDialog fileDialog = new FileDialog(text.getShell(), isInFile ? SWT.OPEN : SWT.SAVE);
                    String lastFolderChosen = OmsBoxPlugin.getDefault().getLastFolderChosen();
                    fileDialog.setFilterPath(lastFolderChosen);
                    String path = fileDialog.open();

                    if (path == null || path.length() < 1) {
                        text.setText("");
                    } else {
                        path = checkBackSlash(path, isFile);
                        text.setText(path);
                        text.setSelection(text.getCharCount());
                        setDataValue();
                    }
                    OmsBoxPlugin.getDefault().setLastFolderChosen(fileDialog.getFilterPath());
                }
            });
        }

        if (isFolder) {
            final Button browseButton = new Button(parent, SWT.PUSH);
            browseButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
            browseButton.setText("...");
            browseButton.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected( SelectionEvent e ) {
                    DirectoryDialog directoryDialog = new DirectoryDialog(text.getShell(), isInFolder ? SWT.OPEN : SWT.SAVE);
                    String lastFolderChosen = OmsBoxPlugin.getDefault().getLastFolderChosen();
                    directoryDialog.setFilterPath(lastFolderChosen);
                    String path = directoryDialog.open();

                    if (path == null || path.length() < 1) {
                        text.setText("");
                    } else {
                        path = checkBackSlash(path, isFile);
                        text.setText(path);
                        // text.setSelection(text.getCharCount());
                        setDataValue();
                    }
                    OmsBoxPlugin.getDefault().setLastFolderChosen(directoryDialog.getFilterPath());
                }
            });
        }
        if (isCrs) {
            // the crs choice group
            final Button crsButton = new Button(parent, SWT.BORDER);
            crsButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
            crsButton.setText("..."); //$NON-NLS-1$
            crsButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){

                public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                    Shell shell = new Shell(text.getShell(), SWT.SHELL_TRIM);
                    Dialog dialog = new Dialog(shell){

                        private CRSChooser chooser;
                        private CoordinateReferenceSystem crs;

                        @Override
                        protected void configureShell( Shell shell ) {
                            super.configureShell(shell);
                            shell.setText("Choose CRS");
                        }

                        @Override
                        protected Control createDialogArea( Composite parent ) {
                            Composite comp = (Composite) super.createDialogArea(parent);
                            GridLayout gLayout = (GridLayout) comp.getLayout();

                            gLayout.numColumns = 1;

                            chooser = new CRSChooser(new Controller(){

                                public void handleClose() {
                                    buttonPressed(OK);
                                }

                                public void handleOk() {
                                    buttonPressed(OK);
                                }

                            });

                            return chooser.createControl(parent);
                        }

                        @Override
                        protected void buttonPressed( int buttonId ) {
                            if (buttonId == OK) {
                                crs = chooser.getCRS();

                                try {
                                    String codeFromCrs = OmsBoxUtils.getCodeFromCrs(crs);
                                    text.setText(codeFromCrs);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            close();
                        }

                    };

                    dialog.setBlockOnOpen(true);
                    dialog.open();
                }
            });

            // initially set to map's crs
            IMap activeMap = ApplicationGIS.getActiveMap();
            if (activeMap != null) {
                try {
                    CoordinateReferenceSystem crs = activeMap.getViewportModel().getCRS();
                    String codeFromCrs = OmsBoxUtils.getCodeFromCrs(crs);
                    text.setText(codeFromCrs);
                    setDataValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (isNorthing || isEasting || isEastingNorthing) {
            addMapMouseListener();
        }

        if (isProcessing()) {
            addRegionListener();
            ILayer processingRegionLayer = OmsBoxPlugin.getDefault().getProcessingRegionMapGraphic();
            IStyleBlackboard blackboard = processingRegionLayer.getStyleBlackboard();
            Object object = blackboard.get(ProcessingRegionStyleContent.ID);
            if (object instanceof ProcessingRegionStyle) {
                ProcessingRegionStyle processingStyle = (ProcessingRegionStyle) object;
                setRegion(processingStyle);
            }
        }

        text.addDisposeListener(new DisposeListener(){
            public void widgetDisposed( DisposeEvent e ) {
                if (isNorthing || isEasting || isEastingNorthing) {
                    removeMapMouseListener();
                }
                if (isProcessing()) {
                    removeRegionListener();
                }
            }
        });

        addDrop();

        return text;
    }

    public FieldData getFieldData() {
        if (isMapcalc) {
            MapcalculatorUtils.saveMapcalcHistory(text.getText());
        }
        return data;
    }

    private void setDataValue() {
        String textStr = text.getText();
        boolean isFile = isInFile || isOutFile;
        textStr = checkBackSlash(textStr, isFile);

        data.fieldValue = textStr;
    }

    public boolean hasData() {
        return true;
    }

    public void modifyText( ModifyEvent e ) {
        setDataValue();
        // text.setSelection(text.getCharCount());
    }

    public void focusGained( FocusEvent e ) {
        // text.setSelection(text.getCharCount());
    }

    @Override
    public void focusLost( FocusEvent e ) {
        setDataValue();
    }

    private void removeMapMouseListener() {
        if (currentMapMouseListener != null) {
            final IMap activeMap = ApplicationGIS.getActiveMap();
            final IRenderManager renderManager = activeMap.getRenderManager();
            final ViewportPane viewportPane = (ViewportPane) renderManager.getMapDisplay();
            viewportPane.removeMouseListener(currentMapMouseListener);
        }
    }

    private void addMapMouseListener() {
        final IMap activeMap = ApplicationGIS.getActiveMap();
        if (activeMap == null) {
            return;
        }
        final IRenderManager renderManager = activeMap.getRenderManager();
        if (renderManager == null) {
            return;
        }
        final ViewportPane viewportPane = (ViewportPane) renderManager.getMapDisplay();
        if (viewportPane == null) {
            return;
        }

        currentMapMouseListener = new MapMouseListener(){
            public void mouseReleased( MapMouseEvent event ) {
                Point point = event.getPoint();
                Coordinate worldClick = activeMap.getViewportModel().pixelToWorld(point.x, point.y);
                if (isEastingNorthing) {
                    text.setText(String.valueOf(worldClick.x) + "," + String.valueOf(worldClick.y));
                }
                if (isNorthing) {
                    text.setText(String.valueOf(worldClick.y));
                }
                if (isEasting) {
                    text.setText(String.valueOf(worldClick.x));
                }
            }
            public void mousePressed( MapMouseEvent event ) {
            }
            public void mouseExited( MapMouseEvent event ) {
            }
            public void mouseEntered( MapMouseEvent event ) {
            }
            public void mouseDoubleClicked( MapMouseEvent event ) {
            }
        };
        viewportPane.addMouseListener(currentMapMouseListener);
    }

    private void removeRegionListener() {
        if (currentBlackboardListener != null) {
            ILayer processingRegionLayer = OmsBoxPlugin.getDefault().getProcessingRegionMapGraphic();
            IStyleBlackboard blackboard = processingRegionLayer.getStyleBlackboard();
            blackboard.removeListener(currentBlackboardListener);
        }
    }

    private void addRegionListener() {
        ILayer processingRegionLayer = OmsBoxPlugin.getDefault().getProcessingRegionMapGraphic();
        IStyleBlackboard blackboard = processingRegionLayer.getStyleBlackboard();
        currentBlackboardListener = new IBlackboardListener(){
            public void blackBoardCleared( IBlackboard source ) {
            }
            public void blackBoardChanged( BlackboardEvent event ) {
                Object key = event.getKey();
                if (key.equals(ProcessingRegionStyleContent.ID)) {
                    Object newValue = event.getNewValue();
                    if (newValue instanceof ProcessingRegionStyle) {
                        ProcessingRegionStyle processingStyle = (ProcessingRegionStyle) newValue;
                        setRegion(processingStyle);
                    }
                }
            }
        };
        blackboard.addListener(currentBlackboardListener);
    }

    private void setRegion( ProcessingRegionStyle processingStyle ) {
        ProcessingRegion region = new ProcessingRegion(processingStyle.west, processingStyle.east, processingStyle.south,
                processingStyle.north, processingStyle.rows, processingStyle.cols);
        if (isProcessingNorth) {
            text.setText(String.valueOf(processingStyle.north));
        } else if (isProcessingSouth) {
            text.setText(String.valueOf(processingStyle.south));
        } else if (isProcessingWest) {
            text.setText(String.valueOf(processingStyle.west));
        } else if (isProcessingEast) {
            text.setText(String.valueOf(processingStyle.east));
        } else if (isProcessingCols) {
            text.setText(String.valueOf(processingStyle.cols));
        } else if (isProcessingRows) {
            text.setText(String.valueOf(processingStyle.rows));
        } else if (isProcessingXres) {
            text.setText(String.valueOf(region.getWEResolution()));
        } else if (isProcessingYres) {
            text.setText(String.valueOf(region.getNSResolution()));
        }
    }

    private boolean isProcessing() {
        return isProcessingNorth || isProcessingSouth || isProcessingEast || isProcessingWest || isProcessingCols
                || isProcessingRows || isProcessingXres || isProcessingYres;
    }

    private void addDrop() {
        int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        DropTarget target = new DropTarget(text, operations);

        final TextTransfer textTransfer = TextTransfer.getInstance();
        final FileTransfer fileTransfer = FileTransfer.getInstance();
        final URLTransfer urlTransfer = URLTransfer.getInstance();
        final UDigByteAndLocalTransfer omsboxTransfer = UDigByteAndLocalTransfer.getInstance();
        Transfer[] types = new Transfer[]{fileTransfer, textTransfer, urlTransfer, omsboxTransfer};
        target.setTransfer(types);
        target.addDropListener(new DropTargetListener(){
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
                            setTextContent(file);
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
                                            setTextContent(file);
                                            OmsBoxPlugin.getDefault().setLastFolderChosen(file.getParentFile().getAbsolutePath());
                                        }
                                    } else if (id.toString().contains("#") && id.toString().startsWith("file")) {
                                        // try to get the file
                                        String string = id.toString().replaceAll("#", "");
                                        URL url = new URL(string);
                                        File file = new File(url.toURI());
                                        if (file.exists()) {
                                            setTextContent(file);
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

    private void setTextContent( File file ) {
        if (isMapcalc) {
            String map = file.getName();
            insertTextAtCaretPosition(text, map);
        } else {
            text.setText(file.getAbsolutePath());
            text.setSelection(text.getCharCount());
        }
    }

    private static void insertTextAtCaretPosition( StyledText text, String string ) {
        int caretPosition = text.getCaretOffset();

        String textStr = text.getText();
        String sub1 = textStr.substring(0, caretPosition);
        String sub2 = textStr.substring(caretPosition);

        StringBuilder sb = new StringBuilder();
        sb.append(sub1);
        sb.append(string);
        sb.append(sub2);

        text.setText(sb.toString());
    }

    @Override
    public String validateContent() {
        StringBuilder sb = new StringBuilder();
        String textStr = text.getText();
        int length = textStr.length();
        if (isInFile || isInFolder) {
            if (length != 0) {
                File file = new File(textStr);
                if (!file.exists()) {
                    sb.append(MessageFormat.format("File {0} dosen''t exist.\n", textStr));
                }
            }
        }
        if (isMapcalc) {
            if (length == 0) {
                sb.append("The function is mandatory for the mapcalc module.\n");
            }
        }
        if (isGrassfile) {
            if (length != 0 && !OmsBoxUtils.isGrass(textStr)) {
                File tmp = new File(textStr);
                sb.append("Grass modules currently work only with data contained in a GRASS mapset (which doesn't seem to be the case for: "
                        + tmp.getName() + ").\n");
            }
        }

        if (sb.length() > 0) {
            return sb.toString();
        } else {
            return null;
        }
    }

}
