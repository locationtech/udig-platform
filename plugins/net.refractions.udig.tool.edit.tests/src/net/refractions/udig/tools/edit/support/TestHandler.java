/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.tools.edit.support;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import net.refractions.udig.TestViewportPane;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventType;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
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
        MathTransform transform = CRS.transform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);
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
    public Feature getFeature( final int i ) throws IOException {
        FeatureSource source=map.getLayersInternal().get(0).getResource(FeatureSource.class, new NullProgressMonitor());
        FeatureCollection features=source.getFeatures();
        Feature feature=null;
        FeatureIterator iter=features.features();
        for( int j=0; j<i+1; j++){
            feature=iter.next();
        }
        return feature;
    }

}
