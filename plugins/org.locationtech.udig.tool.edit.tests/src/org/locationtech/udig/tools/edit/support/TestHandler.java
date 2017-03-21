/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.udig.TestViewportPane;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.testsupport.MapTests;
import org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventType;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;

/**
 * Makes all protected methods public and allows state to be set.
 * 
 * @author jones
 * @since 1.1.0
 */
public class TestHandler extends EditToolHandler {
    
    private Map map;

    public TestHandler( ) throws Exception {
        this(1);
    }

    public TestHandler( int numFeatures) throws Exception {
        this(numFeatures,"TestFeatureType"); //$NON-NLS-1$
        
    }
    
    public TestHandler( int numFeatures, String featureTypeName) throws Exception {
        super(null, null);
        ToolContextImpl context = new ToolContextImpl();
        map = MapTests.createDefaultMap(featureTypeName, numFeatures, true, new Dimension(10,10)); 
        context.setMapInternal(map);
        context.setRenderManagerInternal(map.getRenderManagerInternal());
        ((RenderManager)context.getRenderManager()).setMapDisplay(new TestViewportPane(new Dimension(500,500)));
        setContext(context);
        testing=true;
        bb=new TestEditBlackboard(10,10, context.worldToScreenTransform(), map.getLayersInternal().get(0).layerToMapTransform());

        setCurrentState(EditState.NONE);
        setCurrentShape(null);
    }
    
    @Override
    public void handleEvent( MapMouseEvent e, EventType eventType ) {
        if( e==null )
            e=new MapMouseEvent(null, 0,0,0,0,0);
        if( eventType==null)
            eventType=EventType.MOVED;
        super.handleEvent(e, eventType);
    }
    
    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
    }
    
    EditBlackboard bb;
    
    public void resetEditBlackboard() throws FactoryException{
        MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);
        bb=new TestEditBlackboard(500,500, new AffineTransform(), transform);
    }
    
    @Override
    public EditBlackboard getEditBlackboard(ILayer layer) {
        return bb;
    }
    
    public EditBlackboard getEditBlackboard() {
        return bb;
    }

    public TestEditBlackboard getTestEditBlackboard() {
        return (TestEditBlackboard) bb;
    }

    
    TestMouseTracker tracker=new TestMouseTracker(this);
    
    @Override
    public TestMouseTracker getMouseTracker() {
        return tracker;
    }
    
    @Override
    public void setContext( IToolContext context2 ) {
        super.setContext(context2);
    }

    /**
     * @param bb The blackboard to set.
     */
    public void setEditBlackboard( EditBlackboard bb ) {
        this.bb = bb;
    }

    /**
     *
     * @param b
     */
    public void setTesting( boolean b ) {
        testing=b;
    }

    /**
     *
     * @param i
     * @return
     * @throws IOException 
     */
    public SimpleFeature getFeature( final int i ) throws IOException {
        FeatureSource<SimpleFeatureType, SimpleFeature> source =map.getLayersInternal().get(0).getResource(FeatureSource.class, new NullProgressMonitor());
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features=source.getFeatures();
        SimpleFeature feature=null;
        FeatureIterator<SimpleFeature> iter=features.features();
        for( int j=0; j<i+1; j++){
            feature=iter.next();
        }
        return feature;
    }

}
