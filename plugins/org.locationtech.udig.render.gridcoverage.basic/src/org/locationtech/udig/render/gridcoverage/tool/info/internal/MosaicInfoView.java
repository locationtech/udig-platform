/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.render.gridcoverage.tool.info.internal;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.render.gridcoverage.basic.internal.Messages;
import org.locationtech.udig.render.internal.gridcoverage.basic.RendererPlugin;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.Intersects;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A view for displaying information about a specific image mosaic tile.
 *  
 * <p>
 *
 * </p>
 * @author Emily Gouge (Refractions Research, Inc.)
 * @since 1.1.0
 */
public class MosaicInfoView extends ViewPart {

    private static final String ICONS_DTOOL16_UNDO_EDIT = "icons/dtool16/undo_edit.gif"; //$NON-NLS-1$

    private static final String ICONS_ETOOL16_UNDO_EDIT = "icons/etool16/undo_edit.gif"; //$NON-NLS-1$

    /**
     * Identifier
     */
    public static final String VIEW_ID = "org.locationtech.udig.render.gridcoverage.view.infoView"; //$NON-NLS-1$

    private Text information;
    private MosaicInfoPanel infoPanel;
    private Composite ctr;
    private PageBook book;
    
    /**
     * Undo action to allow you to undo the changes
     */
    private Action undoAction;

    public MosaicInfoView() {
        super();
    }

    @Override
    public void createPartControl( Composite parent ) {
        book = new PageBook(parent, SWT.NONE);

        information = new Text(book, SWT.WRAP);
        information.setText(Messages.MosaicInfoView_DefaultViewText);
        book.showPage(information);

        infoPanel = new MosaicInfoPanel();
        //add a listeners to deal with undo button
        infoPanel.addFeatureSelectedListener(new MosaicInfoPanel.FeatureSelectedListener(){
            public void fireFeatureSelected(SimpleFeature feature){
                enableUndoAction();
            }
        });
        infoPanel.addFeatureUpdatedListener(new MosaicInfoPanel.FeatureUpdatedListener(){
            public void fireFeatureUpdated(SimpleFeature feature){
                enableUndoAction();
            }
        });
        
        ctr = infoPanel.createControl(book);
        
        createActions();
        createToolBar();
       
    }
    
    /**
     * Sets the enabled/disabled status of the
     * undo action based on the information in the infoPanel
     */
    private void enableUndoAction(){
        Display.getDefault().asyncExec(new Runnable(){
            public void run(){
                undoAction.setEnabled(infoPanel.canUndo());
            }
        });
        
    }

    @Override
    public void setFocus() {
        // button.setFocus();
    }

    /**
     * Updates the information using the first feature imagemosaic feature found
     * which matches the request.
     *
     * @param request
     */
    public void updateInfo( InfoRequest request ) {
        SimpleFeature selectedFeature = null;
        ILayer selectedLayer = null;

        for( int i = request.layers.size() - 1; i > -1; i-- ) {
            ILayer layer = request.layers.get(i); // navigate the list backwards

            if (!layer.isVisible())
                continue;

            // only working on image mosaics
            if (layer.hasResource(FeatureSource.class)
                    && layer.getGeoResource().canResolve(AbstractGridCoverage2DReader.class)) {
                try {
                    selectedFeature = findFirstFeature(layer, request.bbox);
                    selectedLayer = layer;
                    if (selectedFeature != null) {
                        break;
                    }
                } catch (Throwable t) {
                    RendererPlugin
                            .log("Information request " + layer.getName() + " failed " + t, t); //$NON-NLS-1$ //$NON-NLS-2$
                }
                continue;
            }

        }
        updateFeatureInfo(selectedFeature, selectedLayer);
    }

    /*
     * Updates the feature info displayed on the control and
     * ensures the correct page is displayed
     */
    private void updateFeatureInfo( SimpleFeature selectedFeature, ILayer selectedLayer ) {
        infoPanel.updateInfo(selectedFeature, selectedLayer);
        book.showPage(ctr);
    }

    /**
     * Finds the first feature in the given layer 
     * that interested the given bounding box.
     * 
     *
     * @param layer
     * @param bbox
     * @return first feature which intersects the bounding box; null if not feature found
     * @throws Exception
     */
    private SimpleFeature findFirstFeature( ILayer layer, ReferencedEnvelope bbox )
            throws Exception {

        FeatureSource<SimpleFeatureType, SimpleFeature> source = layer.getResource(FeatureSource.class, null);
        SimpleFeatureType type = source.getSchema();
        CoordinateReferenceSystem crs = layer.getCRS();

        if (!bbox.getCoordinateReferenceSystem().equals(crs)) {
            bbox = bbox.transform(crs, true);
        }
        FilterFactory2 factory = (FilterFactory2) CommonFactoryFinder.getFilterFactory(GeoTools
                .getDefaultHints());
        Geometry geom = new GeometryFactory().toGeometry(bbox);
        Intersects filter = factory.intersects(factory.property(type.getGeometryDescriptor()
                .getName()), factory.literal(geom));

        layer.getQuery(false);
        final FeatureCollection<SimpleFeatureType, SimpleFeature> results = source
                .getFeatures(filter);

        FeatureIterator<SimpleFeature> reader = results.features();
        try {
            while( reader.hasNext() ) {
                SimpleFeature f = reader.next();
                return f;
            }
        } finally {
            reader.close();
        }
        return null;
    }
    
    
    /**
     * Create the actions associated with the view.
     */
    private void createActions(){
        undoAction = new Action(Messages.MosaicInfoView_UndoAction){
            public void run(){
                if (infoPanel.canUndo()){
                    infoPanel.undo();
                }
            }
        };
        undoAction.setDisabledImageDescriptor(RendererPlugin.getImageDescriptor(ICONS_DTOOL16_UNDO_EDIT));
        undoAction.setImageDescriptor(RendererPlugin.getImageDescriptor(ICONS_ETOOL16_UNDO_EDIT));
        
        undoAction.setToolTipText(Messages.MosaicInfoView_UndoActionToolTip);
        undoAction.setEnabled(false);
    }
    
    /**
     * Create the view tool bar and add undo action.
     */
    private void createToolBar(){
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.add(undoAction);
    }

    /**
     * Class to track the information request
     * <p>
     * </p>
     * 
     * @author Emily Gouge (Refractions Research, Inc)
     * @since 1.1.0
     */
    public static class InfoRequest {
        public ReferencedEnvelope bbox;
        public List<ILayer> layers;
    };

}
