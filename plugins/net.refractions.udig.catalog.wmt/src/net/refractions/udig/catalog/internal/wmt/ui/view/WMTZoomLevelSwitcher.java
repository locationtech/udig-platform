package net.refractions.udig.catalog.internal.wmt.ui.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.internal.wmt.WMTRenderJob;
import net.refractions.udig.catalog.internal.wmt.WMTScaleZoomLevelMatcher;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.catalog.wmt.internal.Messages;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IMapCompositionListener;
import net.refractions.udig.project.MapCompositionEvent;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.commands.SetScaleCommand;
import net.refractions.udig.project.render.IViewportModelListener;
import net.refractions.udig.project.render.ViewportModelEvent;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.geotools.geometry.jts.ReferencedEnvelope;

public class WMTZoomLevelSwitcher extends ViewPart {
    
    // Listeners
    private ISelectionChangedListener listenerZoomLevel;
    private IMapCompositionListener listenerMap;
    private IViewportModelListener listenerViewport;
    private IPartListener listenerMapEditor;
    
    // GUI elements
    private Composite parentControl;
    
    private Button btnZoomOut;
    private Button btnZoomIn;

    private ComboViewer cvLayers;
    private ComboViewer cvZoomLevels;
    
    // Map/Layers/Zoom-Levels
    private IMap currentMap = ApplicationGIS.NO_MAP;
    private List<ILayer> layerList;
    
    private Integer[] zoomLevels;
    
    // Icons
    private ImageRegistry imageCache;
    private static final String ICONS_PATH = "/icons/etool16/"; //$NON-NLS-1$
    private static final String ICON_ZOOM_IN = "ZOOM_IN"; //$NON-NLS-1$
    private static final String ICON_ZOOM_IN_PATH = ICONS_PATH + "zoom_in_co.gif"; //$NON-NLS-1$
    private static final String ICON_ZOOM_OUT = "ZOOM_OUT"; //$NON-NLS-1$
    private static final String ICON_ZOOM_OUT_PATH = ICONS_PATH + "zoom_out_co.gif"; //$NON-NLS-1$
    
    public WMTZoomLevelSwitcher() {
        super();
        
        initListeners();
    }

    //region Init Listeners
    private void initListeners() {
        initMapListener();
        
        initViewportListener();
        
        initMapEditorListener();
    }

    private void initMapListener() {
        listenerMap = new IMapCompositionListener(){
            public void changed(MapCompositionEvent event) {

                if (parentControl == null || parentControl.isDisposed()) return;
                
                parentControl.getDisplay().asyncExec(new Runnable(){
                    public void run() {
                        updateGUI(ApplicationGIS.getActiveMap());
                    }
                });
            }
        };
    }

    private void initViewportListener() {
        listenerViewport = new IViewportModelListener() {
            public void changed(ViewportModelEvent event) {
                
                if (parentControl == null || parentControl.isDisposed()) return;
                                
                // when the scale changes, update the zoom-level ComboBox  
                parentControl.getDisplay().asyncExec(new Runnable() {
                    public void run(){
                        updateGUIFromScale();
                    }
                });
            }
        };
    }

    private void initMapEditorListener() {
        listenerMapEditor = new  IPartListener() {
            private IWorkbenchPart currentPart;

            public void partActivated(IWorkbenchPart part) {
                if (part == currentPart)
                    return;
                
                final IMap map = getMapFromPart(part);
                
                if (map != null) {
                    currentPart = part;
                    
                    if (parentControl == null || parentControl.isDisposed()) return;
                    
                    parentControl.getDisplay().asyncExec(new Runnable() {
                        public void run(){
                            setUpMapListeners(map);
                        }
                    });
                }
                
            }

            public void partBroughtToTop(IWorkbenchPart part) {
                partActivated(part);
            }

            public void partClosed(IWorkbenchPart part) {                
                if (part == WMTZoomLevelSwitcher.this) {
                    if (parentControl == null || parentControl.isDisposed()) return;
                    
                    // if the tool itself is closed
                    parentControl.getDisplay().asyncExec(new Runnable() {
                        public void run(){
                            removeAllListeners();
                            
                            currentPart = null;
                        }
                    });
                    
                    return;
                }
                
                if (part != currentPart)
                    return;

                final IMap map = getMapFromPart(part);
                
                if(map != null) {
                    if (parentControl == null || parentControl.isDisposed()) return;
                    
                    // remove 
                    parentControl.getDisplay().asyncExec(new Runnable() {
                        public void run(){
                            removeMapListeners(map, true);
                        }
                    });
                }
                
                currentPart = null;
            }

            private IMap getMapFromPart(IWorkbenchPart part) {
                if (part != null) {
                    IAdaptable adaptable = (IAdaptable) part;
                    Object obj = adaptable.getAdapter(Map.class);

                    if (obj instanceof Map) {
                        
                        return (Map) obj;
                    }
                }
                
                return null;
            }

            public void partDeactivated(IWorkbenchPart part) {}
            public void partOpened(IWorkbenchPart part) {}
            
        };
    }
    //endregion
    
    //region Add/remove listeners
    public void setUpMapListeners(IMap map) {
        // remove listeners from old map
        removeMapListeners(currentMap, false);
        
        // assure that the listeners are only once in the list of the new map
        removeMapListeners(map, false);
        
        map.addMapCompositionListener(listenerMap);       
        map.getViewportModel().addViewportModelListener(listenerViewport);
        
        updateGUI(map);        
    }
    
    public void removeMapListeners(IMap map, boolean updateGUI) {
        if (map == null || map == ApplicationGIS.NO_MAP) return;
        
        map.removeMapCompositionListener(listenerMap);  
        map.getViewportModel().removeViewportModelListener(listenerViewport);   
        
        if (updateGUI && parentControl != null && !parentControl.isDisposed()) {            
            currentMap = ApplicationGIS.NO_MAP;
            layerList.clear();
            
            enableComponents(false);
        }
    }  
    
    public void removeAllListeners() {
        removeMapListeners(currentMap, false);

        getSite().getWorkbenchWindow().getPartService().removePartListener(listenerMapEditor);        
    }
    //endregion
    
    //region Create Control
    @Override
    public void createPartControl(final Composite parent) {
        parentControl = parent;
        
        Composite composite = new Composite(parent, SWT.NONE);   
        composite.setLayout(new RowLayout(SWT.HORIZONTAL));
                 
        //region Label "Layer"
        Label lblLayer = new Label (composite, SWT.HORIZONTAL);
        lblLayer.setText(Messages.ZoomLevelSwitcher_Layer);
        //endregion
        
        //region Layer ComboBox
        cvLayers = new ComboViewer(composite, SWT.READ_ONLY);
               
        cvLayers.setContentProvider(new ArrayContentProvider());
        cvLayers.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof ILayer) {
                    return ((ILayer) element).getName();
                } else {
                    return super.getText(element);
                }
            }            
        });
        
        cvLayers.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged( SelectionChangedEvent event ) {
                updateZoomLevels();
                updateGUIFromScale();                    
            }
            
        });
        //endregion
              
        //region Label "Zoom-Level"
        Label lblZoomLevel = new Label (composite, SWT.HORIZONTAL);
        lblZoomLevel.setText(Messages.ZoomLevelSwitcher_ZoomLevel);
        //endregion
        
        //region Zoom-Level ComboBox
        cvZoomLevels = new ComboViewer(composite, SWT.READ_ONLY);
               
        cvZoomLevels.setContentProvider(new ArrayContentProvider());
        cvZoomLevels.setLabelProvider(new LabelProvider());
        //endregion
        
        //region Zoom-In/Zoom-Out Buttons 
        // load icons
        setUpImageCache(parent);
        
        btnZoomOut = new Button(composite, SWT.PUSH);
        btnZoomOut.setImage(imageCache.get(ICON_ZOOM_OUT));
        btnZoomOut.setToolTipText(Messages.ZoomLevelSwitcher_ZoomOut);
        
        btnZoomOut.addSelectionListener(new SelectionListener() {

            public void widgetSelected( SelectionEvent e ) {
                zoomOut();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {}
        });
        
        btnZoomIn = new Button(composite, SWT.PUSH);
        btnZoomIn.setImage(imageCache.get(ICON_ZOOM_IN));
        btnZoomIn.setToolTipText(Messages.ZoomLevelSwitcher_ZoomIn);
        
        btnZoomIn.addSelectionListener(new SelectionListener() {

            public void widgetSelected( SelectionEvent e ) {
                zoomIn();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {}            
        });
        //endregion
        
        //region Setup listeners
        listenerZoomLevel = new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                zoomToZoomLevel(getSelectedZoomLevel());
            }
            
        };
        
        cvZoomLevels.addSelectionChangedListener(listenerZoomLevel);
        
        setUpMapListeners(ApplicationGIS.getActiveMap());
        

        getSite().getWorkbenchWindow().getPartService().addPartListener(listenerMapEditor);
        //endregion
    }

    /**
     * Creates the ImageRegistry and adds the two icons.
     */
    private void setUpImageCache(final Composite parent) {
        imageCache = new ImageRegistry(parent.getDisplay());
        
        ImageDescriptor descZoomOut = ImageDescriptor.createFromFile(getClass(), 
                ICON_ZOOM_OUT_PATH);
        imageCache.put(ICON_ZOOM_OUT, descZoomOut);
        
        ImageDescriptor descZoomIn = ImageDescriptor.createFromFile(getClass(), 
                ICON_ZOOM_IN_PATH);
        imageCache.put(ICON_ZOOM_IN, descZoomIn);        
    }
    //endregion
    
    //region GUI Helper Methods
    //region Updates to the GUI when the map changed
    private void updateGUI(IMap map) {
        if (parentControl != null && !parentControl.isDisposed()) {
            updateLayerList(map);
            updateZoomLevels();
            updateGUIFromScale();

            parentControl.pack();
        }
    }
    
    private void updateLayerList(IMap map) {
        if (layerList == null) {
            layerList = new ArrayList<ILayer>();
        } 
        
        // remember the layer which is selected at the moment
        ILayer selectedLayer = getSelectedLayer();
        
        layerList.clear();
        
        if (map == null || map == ApplicationGIS.NO_MAP) {
            map = ApplicationGIS.getActiveMap();            
        }
        
        if (map != ApplicationGIS.NO_MAP) {
            List<ILayer> mapLayers = map.getMapLayers();

            // look for layers which have WMTSource as georesource
            for( ILayer layer : mapLayers ) {
                if ((layer != null) && (layer.findGeoResource(WMTSource.class) != null)) {
                    // valid layer
                    layerList.add(layer);
                }
            }
        }
        
        cvLayers.setInput(layerList);
        setSelectedLayer(selectedLayer);
        
        enableComponents(!layerList.isEmpty());
        
        // remember to which map these layers belong to
        this.currentMap = map;
    }
    //endregion
    
    //region Updates to the GUI when another layer was selected
    private void updateZoomLevels() {
        WMTSource wmtSource = getWMTSourceOfSelectedLayer();
        
        if (wmtSource == null) {
            cvZoomLevels.setInput(null);
        } else {
            int minZoomLevel = wmtSource.getMinZoomLevel();
            int maxZoomLevel = wmtSource.getMaxZoomLevel();
            
            generateZoomLevels(minZoomLevel, maxZoomLevel);
            
            cvZoomLevels.setInput(zoomLevels);
        }
    }
    //endregion
    
    //region Updates to the GUI regarding the scale
    private void updateGUIFromScale() {
        try {
            WMTSource wmtSource = getWMTSourceOfSelectedLayer();

            if (wmtSource != null) {
                // get the zoom-level for this scale
                WMTScaleZoomLevelMatcher zoomLevelMatcher = getZoomLevelMatcher(wmtSource);

                int zoomLevel = wmtSource.getZoomLevelFromMapScale(zoomLevelMatcher,
                        WMTRenderJob.getScaleFactor());

                setSelectedZoomLevel(zoomLevel);
                updateZoomButtons(zoomLevel);
            } else {
                throw new Exception("wmtSource is null"); //$NON-NLS-1$
            }
        } catch (Exception e) {
            WMTPlugin.log("[WMTZoomLevelSwitcher.updateGUIFromScale] Failed ", e); //$NON-NLS-1$
        }
    }
    //endregion
    
    //region Methods for the zoom-level combo-box
    private void setSelectedZoomLevel(int zoomLevel) {
        
        List<Integer> selectedZoomLevels = new ArrayList<Integer>(1);
        selectedZoomLevels.add(zoomLevel);      
        ISelection selection = new StructuredSelection(selectedZoomLevels);

        cvZoomLevels.removeSelectionChangedListener(listenerZoomLevel);
        cvZoomLevels.setSelection(selection);  
        cvZoomLevels.addSelectionChangedListener(listenerZoomLevel);
    }
    
    private int getSelectedZoomLevel() {
        StructuredSelection selection = (StructuredSelection) cvZoomLevels.getSelection();
        
        if (selection.isEmpty()) {
            return zoomLevels[0];
        } else {
            return (Integer) selection.getFirstElement();
        }
    }
    
    private void generateZoomLevels(int minZoomLevel, int maxZoomLevel) {
        int length = maxZoomLevel - minZoomLevel + 1;
        zoomLevels = new Integer[length];
        
        int zoomLevel = minZoomLevel;
        for (int i = 0; i < length; i++) {
            zoomLevels[i] = zoomLevel++;
        }
    }
    //endregion
   
    //region Methods for the layer combo-box
    private WMTSource getWMTSourceOfSelectedLayer() {
        ILayer layer = getSelectedLayer();
        
        if (layer == null) return null;
        
        IGeoResource resource = layer.findGeoResource(WMTSource.class); 
        if (resource == null) return null;
        
        try {
            WMTSource wmtSource = resource.resolve(WMTSource.class, null);
            
            return wmtSource;
        } catch (IOException e) {
            return null;
        }
    }
    
    private ILayer getSelectedLayer() {
        StructuredSelection selection = (StructuredSelection) cvLayers.getSelection();
        
        if (selection.isEmpty()) {
            return null;
        } else {
            return (ILayer) selection.getFirstElement();
        }
    }
    
    private void setSelectedLayer(ILayer layer) {
        if (layer == null || !layerList.contains(layer)) {
            // try to get the first layer
            if (layerList.isEmpty()) {
                // no layer there to select
                return;
            } else {
                layer = layerList.get(0);
            }
        } 
        
        // set the layer selected
        List<ILayer> selectedLayers = new ArrayList<ILayer>(1);
        selectedLayers.add(layer);      
        ISelection selection = new StructuredSelection(selectedLayers);
      
        cvLayers.setSelection(selection);
    }
    //endregion
    
    //region Methods to disable/enable GUI elements
    private void enableComponents(boolean enabled) {
        cvLayers.getCombo().setEnabled(enabled);
        cvZoomLevels.getCombo().setEnabled(enabled);
        btnZoomIn.setEnabled(enabled);
        btnZoomOut.setEnabled(enabled);        
    }
    
    private void updateZoomButtons(int zoomLevel) {
        boolean zoomInEnabled = true;
        boolean zoomOutEnabled = true;
        
        if (zoomLevel <= zoomLevels[0]) {
            zoomOutEnabled = false;
        } else if (zoomLevel >= zoomLevels[zoomLevels.length-1]) {
            zoomInEnabled = false;
        }
        
        btnZoomIn.setEnabled(zoomInEnabled);
        btnZoomOut.setEnabled(zoomOutEnabled);
    }
    //endregion
    
    //region Methods to zoom in/out
    private synchronized WMTScaleZoomLevelMatcher getZoomLevelMatcher(WMTSource wmtSource) throws Exception {
        double mapScale = currentMap.getViewportModel().getScaleDenominator();
        ReferencedEnvelope mapExtentMapCrs = currentMap.getViewportModel().getBounds();
        
        return WMTScaleZoomLevelMatcher.createMatcher(
                mapExtentMapCrs, mapScale, wmtSource);
    }
    
    private void zoomIn() {
        int zoomLevel = getSelectedZoomLevel();
        
        if (zoomLevel < zoomLevels[zoomLevels.length-1]){
            zoomToZoomLevel(zoomLevel+1);
        }
    }
    
    private void zoomOut() {
        int zoomLevel = getSelectedZoomLevel();
        
        if (zoomLevel > zoomLevels[0]){
            zoomToZoomLevel(zoomLevel-1);
        }
    }
    
    private void zoomToZoomLevel(int zoomLevel) {
        try {
            WMTSource wmtSource = getWMTSourceOfSelectedLayer();
            
            if (wmtSource != null) {                
                WMTScaleZoomLevelMatcher zoomLevelMatcher = getZoomLevelMatcher(wmtSource);
                double scale = zoomLevelMatcher.getOptimumScaleFromZoomLevel(zoomLevel, 
                        wmtSource);
                    
                zoomToScale(scale);
            } else {
                throw new Exception("wmtSource is null"); //$NON-NLS-1$
            }
        } catch (Exception e) {
            WMTPlugin.log("[WMTZoomLevelSwitcher.zoomToZoomLevel] Zooming failed: " + zoomLevel, e); //$NON-NLS-1$
        }
    }
    
    private void zoomToScale(double scale) {
        ApplicationGIS.getActiveMap().sendCommandASync(new SetScaleCommand(scale));
    }
    //endregion
    //endregion
    
    @Override
    public void setFocus() {}


}
