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
package org.locationtech.udig.project.ui;

import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;

public class EditReRenderTest extends AbstractProjectUITestCase {

	private Map map;
	private SimpleFeature[] features;

	@Before
	public void setUp() throws Exception {
		features = UDIGTestUtil.createDefaultTestFeatures("featuretype", 4); //$NON-NLS-1$
		map = MapTests.createNonDynamicMapAndRenderer(MapTests.createGeoResource(features, true),new Dimension(512,512));
		map.setRenderManagerInternal(null);
		
	}

	@SuppressWarnings("unchecked")
	@Ignore
	@Test
	public void testEditReRender() throws Exception {
        ApplicationGIS.openMap(map, true);
        
        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue() {
                return map.getRenderManagerInternal().getRenderExecutor().getState()==IRenderer.DONE;
            }
            
        }, true);
        
        final RenderListener listener=new RenderListener();

        map.getRenderManagerInternal().getRenderExecutor().eAdapters().add(listener);
        listener.rendered=false;
        FeatureStore<SimpleFeatureType, SimpleFeature> store=map.getLayersInternal().get(0).getResource(FeatureStore.class, new NullProgressMonitor());
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        store.removeFeatures(fac.id(FeatureUtils.stringToId(fac, features[0].getID())));

        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue() {
                return listener.rendered;
            }
            
        }, true);
        
        assertTrue(listener.rendered);
        
        listener.rendered=false;
        
        store.modifyFeatures(features[0].getFeatureType().getDescriptor("name"),"changed",  //$NON-NLS-1$ //$NON-NLS-2$
        		fac.id( FeatureUtils.stringToId(fac, features[1].getID() )));

        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue() {
                return listener.rendered;
            }
            
        }, true);

        assertTrue(listener.rendered);

        listener.rendered=false;
        
        map.getEditManagerInternal().rollbackTransaction();

        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue() {
                return listener.rendered;
            }
            
        }, true);

        assertTrue(listener.rendered);
        
        listener.rendered=false;
        
        store.modifyFeatures(features[0].getFeatureType().getDescriptor("name"),"changed",   //$NON-NLS-1$//$NON-NLS-2$
        		fac.id( FeatureUtils.stringToId(fac, features[1].getID() )));
        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue() {
                return listener.rendered;
            }
            
        }, true);
        
        assertTrue(listener.rendered);

        listener.rendered=false;
        
        map.getEditManagerInternal().rollbackTransaction();
        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue() {
                return listener.rendered;
            }
            
        }, true);
        assertTrue(listener.rendered);
	}
	
	class RenderListener extends AdapterImpl{
		boolean rendered;
		
		@Override
		public void notifyChanged(Notification msg) {
			if ( msg.getFeatureID(Renderer.class)==RenderPackage.RENDERER__STATE ){
				if ( msg.getNewIntValue()==IRenderer.DONE ){
					rendered=true;
				}
			}
		}
	}
}
