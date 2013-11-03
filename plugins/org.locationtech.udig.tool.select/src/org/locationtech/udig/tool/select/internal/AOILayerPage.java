/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */

package org.locationtech.udig.tool.select.internal;

import java.util.List;

import org.locationtech.udig.aoi.AOIListener;
import org.locationtech.udig.aoi.AOIProxy;
import org.locationtech.udig.aoi.IAOIService;
import org.locationtech.udig.aoi.IAOIStrategy;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

/**
 * A page to add to the AOI View (Area of Interest) used for additional configuration of the AOI.
 * <p>
 * Has a combo to select a AOILayer from and displays the current AOI/s selected. 
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class AOILayerPage extends Page {

    private Composite page;
    private AOIProxy strategy;
    private ComboViewer comboViewer;
//    private ListViewer listViewer;
    
    private static String AOI_LAYER_ID = "org.locationtech.udig.tool.select.internal.aoiLayer";

    private ISelectionChangedListener comboListener = new ISelectionChangedListener(){
        @Override
        public void selectionChanged( SelectionChangedEvent event ) {
            IStructuredSelection selectedLayer = (IStructuredSelection) event.getSelection();
            ILayer selected = (ILayer) selectedLayer.getFirstElement();
            getAOILayerStrategy().setActiveLayer(selected);
        }
    };

    /*
     * Listens to the AOILayerStrategy and updates our view if anything changes!
     */
    private AOIListener layerWatcher = new AOIListener(){
        
        public void handleEvent( AOIListener.Event event ) {
            // must be run in the UI thread to be able to call setSelected
            PlatformGIS.asyncInDisplayThread(new Runnable(){
                
                @Override
                public void run() {
                    ILayer activeLayer = getAOILayerStrategy().getActiveLayer();
                    List<ILayer> layers = getAOILayerStrategy().getAOILayers();
                    if( comboViewer == null || comboViewer.getControl() == null || comboViewer.getControl().isDisposed()){
                        return; // we are shut down or hidden
                    }
                    comboViewer.setInput(layers);
                    // check if the current layer still exists
                    if (layers.contains(activeLayer)) {
                        setSelected(activeLayer);
                    }
                    else {
                        setSelected(null);
                    }
//                    listViewer.setInput(getAOILayerStrategy().getFeatures());
                }
            }, true);
        }
        
    };

    public AOILayerPage() {
        // careful don't do any work here
    }
    
    // We would overrride init if we needed to (remmeber to call super)
    @Override
    public void init(IPageSite pageSite){
        super.init(pageSite); // provides access to stuff
    }
    
    /*
     * returns a AOILayerStrategy object for quick access
     */
    private AOILayerStrategy getAOILayerStrategy() {
        IAOIService aOIService = PlatformGIS.getAOIService();
        IAOIStrategy aOIStrategy = aOIService.findProxy(AOI_LAYER_ID)
                .getStrategy();

        if (aOIStrategy instanceof AOILayerStrategy) {
            return (AOILayerStrategy) aOIStrategy;
        }
        return null;
    }
    

    @Override
    public void createControl( Composite parent ) {
        page = new Composite(parent, SWT.NONE);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        page.setLayout(layout);

        Label comboLabel = new Label(page, SWT.LEFT);
        comboLabel.setText("Layer:");
        comboLabel.pack();
        
        listenStrategy(true);
        
        comboViewer = new ComboViewer(page, SWT.READ_ONLY);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider(){
            @Override
            public String getText( Object element ) {
                if (element instanceof ILayer) {
                    ILayer layer = (ILayer) element;
                    return layer.getName();
                }
                return super.getText(element);
            }
        });
        List<ILayer> layers = getAOILayerStrategy().getAOILayers();
        comboViewer.setInput(layers);
        
        ILayer activeLayer = getAOILayerStrategy().getActiveLayer();
        setSelected(activeLayer);
        
        comboViewer.addSelectionChangedListener(comboListener);
        
//        // list of current AOI features
//        Label listLabel = new Label(page, SWT.LEFT);
//        listLabel.setText("Current Features:");
//        listLabel.pack();
//        
//        listViewer = new ListViewer(page, SWT.READ_ONLY);
//        listViewer.setContentProvider(new ArrayContentProvider());
//        listViewer.setLabelProvider(new LabelProvider(){
//            @Override
//            public String getText( Object element ) {
//                if (element instanceof SimpleFeature) {
//                    SimpleFeature feature = (SimpleFeature) element;
//                    Collection<Property> properties = feature.getProperties();
//                    String name = new String();
//                    for (Property property : properties) {
//                        String propertyName = property.getName().getLocalPart();
//                        if(propertyName.toLowerCase().contains("name")) {
//                            name = property.getValue().toString();
//                            System.out.println(name);
//                            continue;
//                        }
//                            
//                    }
//                    return name;
//                }
//                return super.getText(element);
//            }
//        });
//        List<SimpleFeature> features = getAOILayerStrategy().getFeatures(); 
//        listViewer.setInput(features);
    }
    
    /*
     * This will update the combo viewer (carefully unhooking events while the viewer is updated).
     * 
     * @param selected
     */
    private void setSelected( ILayer selected ) {
        
        boolean disposed = comboViewer.getControl().isDisposed();
        if (comboViewer == null || disposed) {
            listenStrategy(false);
            return; // the view has shutdown!
        }
        
        ILayer current = getSelected();
        // check combo
        if (current != selected) {
            try {
                listenCombo(false);
                comboViewer.setSelection(new StructuredSelection(selected), true);
            } finally {
                listenCombo(true);
            }
        }

    }
    
    /*
     * Get the AOI Layer selected by the user
     * 
     * @return ILayer selected by the user
     */
    private ILayer getSelected() {
        if (comboViewer.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
            return (ILayer) selection.getFirstElement();
        }
        return null;
    }

    protected void listenCombo( boolean listen ){
        if (listen) {
            comboViewer.addSelectionChangedListener(comboListener);
        } else {
            comboViewer.removeSelectionChangedListener(comboListener);
        }
    }

    protected void listenStrategy( boolean listen ){
        AOILayerStrategy aOILayerStrategy = getAOILayerStrategy();
        if (listen) {
            aOILayerStrategy.addListener(layerWatcher);
        } else {
            aOILayerStrategy.removeListener(layerWatcher);
        }
    }
    
    @Override
    public Composite getControl() {
        return page;
    }

    @Override
    public void setFocus() {
        if (page != null && !page.isDisposed()) {
            page.setFocus();
        }
    }
    
    @Override
    public void dispose() {
        if( page != null ){
            // remove any listeners            
        }
        if( strategy != null ){
            // remove any listeners
            strategy = null;
        }
        super.dispose();
    }

}
