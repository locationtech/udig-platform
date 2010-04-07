package net.refractions.udig.project.ui;

import java.awt.Dimension;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;

public class EditReRenderTest extends AbstractProjectUITestCase {

	private Map map;
	private SimpleFeature[] features;

	protected void setUp() throws Exception {
        super.setUp();
		features = UDIGTestUtil.createDefaultTestFeatures("featuretype", 4); //$NON-NLS-1$
		map = MapTests.createNonDynamicMapAndRenderer(MapTests.createGeoResource(features, true),new Dimension(512,512));
		map.setRenderManagerInternal(null);
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SuppressWarnings("unchecked") 
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
