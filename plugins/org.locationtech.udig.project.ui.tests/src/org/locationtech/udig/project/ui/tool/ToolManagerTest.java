/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.tool;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.io.IOException;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.core.filter.AdaptingFilter;
import org.locationtech.udig.core.filter.AdaptingFilterFactory;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class ToolManagerTest extends AbstractProjectUITestCase {

    private Map map;
    private Layer firstLayer;

    @Before
    public void setUp() throws Exception {
        map = MapTests.createDefaultMap("test", 10, true, new Dimension(500, 500)); //$NON-NLS-1$
        firstLayer=map.getLayersInternal().get(0);
    }

    @Ignore
    @Test
    public void testCUTPASTEFeatures() throws Exception {
        ApplicationGIS.openMap(map);
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return ApplicationGIS.getActiveMap() != null && ApplicationGIS.getActiveMap()==map;
            }

        }, true);

        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("new", 1); //$NON-NLS-1$
        IGeoResource resource = MapTests.createGeoResource(features, true);
        Layer layer = map.getLayerFactory().createLayer(resource);
        map.getLayersInternal().add(layer);
        
        
        IAction copyAction = ApplicationGIS.getToolManager().getCOPYAction(ApplicationGISInternal.getActiveEditor());
        IAction pasteAction = ApplicationGIS.getToolManager().getPASTEAction(ApplicationGISInternal.getActiveEditor());
        
        map.getEditManagerInternal().setSelectedLayer(firstLayer);

        firstLayer.setFilter( null ); // Filter.INCLUDE 
        
        AdaptingFilter filter = AdaptingFilterFactory.createAdaptingFilter(firstLayer.getFilter(), firstLayer );
        StructuredSelection structuredSelection = new StructuredSelection(filter);
        
        ApplicationGISInternal.getActiveEditor().getEditorSite().getSelectionProvider().setSelection(structuredSelection) ;
        Event event = new Event();
        event.display=Display.getCurrent();
        copyAction.runWithEvent(event);

        ApplicationGISInternal.getActiveEditor().getEditorSite().getSelectionProvider().setSelection(new StructuredSelection(layer));
        
        pasteAction.runWithEvent(event);
        
        final FeatureSource<SimpleFeatureType, SimpleFeature> fs = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        
        UDIGTestUtil.inDisplayThreadWait( 4000, new WaitCondition(){

            public boolean isTrue() {
                try {
                    return fs.getCount(Query.ALL)==11;
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
            }
            
        }, true);
        assertEquals(11, fs.getCount(Query.ALL));
        
    }
    
    @Test
    public void testDeleteAction() throws Exception {
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("new", 15); //$NON-NLS-1$
        IGeoResource resource = MapTests.createGeoResource(features, true);
        Layer layer = map.getLayerFactory().createLayer(resource);
        map.getLayersInternal().add(layer);
        
        
    }
}
