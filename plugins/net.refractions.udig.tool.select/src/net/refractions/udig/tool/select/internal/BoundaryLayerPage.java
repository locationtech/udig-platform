/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package net.refractions.udig.tool.select.internal;

import java.util.List;

import net.refractions.udig.boundary.BoundaryListener;
import net.refractions.udig.boundary.BoundaryProxy;
import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.PlatformGIS;

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
 * A page to add to the Boundary View used for additional configuration of the boundary.
 * <p>
 * Has a combo to select a BoundaryLayer from and displays the current boundary/s selected. 
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class BoundaryLayerPage extends Page {

    private Composite page;
    private BoundaryProxy strategy;
    private ComboViewer comboViewer;
//    private ListViewer listViewer;
    
    private static String BOUNDARY_LAYER_ID = "net.refractions.udig.tool.default.BoundaryLayerService";

    private ISelectionChangedListener comboListener = new ISelectionChangedListener(){
        @Override
        public void selectionChanged( SelectionChangedEvent event ) {
            IStructuredSelection selectedLayer = (IStructuredSelection) event.getSelection();
            ILayer selected = (ILayer) selectedLayer.getFirstElement();
            getBoundaryLayerStrategy().setActiveLayer(selected);
        }
    };

    /*
     * Listens to the boundaryLayerStrategy and updates our view if anything changes!
     */
    private BoundaryListener layerWatcher = new BoundaryListener(){
        
        public void handleEvent( BoundaryListener.Event event ) {
            // must be run in the UI thread to be able to call setSelected
            PlatformGIS.asyncInDisplayThread(new Runnable(){
                
                @Override
                public void run() {
                    ILayer activeLayer = getBoundaryLayerStrategy().getActiveLayer();
                    List<ILayer> layers = getBoundaryLayerStrategy().getBoundaryLayers();
                    comboViewer.setInput(layers);
                    // check if the current layer still exists
                    if (layers.contains(activeLayer)) {
                        setSelected(activeLayer);
                    }
                    else {
                        setSelected(null);
                    }
//                    listViewer.setInput(getBoundaryLayerStrategy().getFeatures());
                }
            }, true);
        }
        
    };

    public BoundaryLayerPage() {
        // careful don't do any work here
    }
    
    // We would overrride init if we needed to (remmeber to call super)
    @Override
    public void init(IPageSite pageSite){
        super.init(pageSite); // provides access to stuff
    }
    
    /*
     * returns a BoundaryLayerStrategy object for quick access
     */
    private BoundaryLayerStrategy getBoundaryLayerStrategy() {
        IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
        IBoundaryStrategy boundaryStrategy = boundaryService.findProxy(BOUNDARY_LAYER_ID)
                .getStrategy();

        if (boundaryStrategy instanceof BoundaryLayerStrategy) {
            return (BoundaryLayerStrategy) boundaryStrategy;
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
        comboLabel.setText("Select From:");
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
        List<ILayer> layers = getBoundaryLayerStrategy().getBoundaryLayers();
        comboViewer.setInput(layers);
        
        ILayer activeLayer = getBoundaryLayerStrategy().getActiveLayer();
        setSelected(activeLayer);
        
        comboViewer.addSelectionChangedListener(comboListener);
        
//        // list of current Boundary features
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
//        List<SimpleFeature> features = getBoundaryLayerStrategy().getFeatures(); 
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
     * Get the Boundary Layer selected by the user
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
        BoundaryLayerStrategy boundaryLayerStrategy = getBoundaryLayerStrategy();
        if (listen) {
            boundaryLayerStrategy.addListener(layerWatcher);
        } else {
            boundaryLayerStrategy.removeListener(layerWatcher);
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
