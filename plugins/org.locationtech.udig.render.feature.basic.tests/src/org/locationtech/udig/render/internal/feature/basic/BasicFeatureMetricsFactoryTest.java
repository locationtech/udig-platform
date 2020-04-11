package org.locationtech.udig.render.internal.feature.basic;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FeatureSource;
import org.junit.Test;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.project.render.IRenderContext;

public class BasicFeatureMetricsFactoryTest {

    class TestGeoResource extends IGeoResource {

        Class resolveClass;
        
        TestGeoResource(Class resolveClass) {
            this.resolveClass = resolveClass;
        }
        
        @Override
        public Status getStatus() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Throwable getMessage() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected IGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public URL getIdentifier() {
            // TODO Auto-generated method stub
            return null;
        }
        
        @Override
        public <T> boolean canResolve(Class<T> adaptee) {
            if (resolveClass!=null && adaptee != null && adaptee.getName().equals(resolveClass.getName())) {
                return true;
            }
            return false;
        }
    }

    @Test
    public void giveImageMoasicPriorityOverShapefile() throws Exception {
        IRenderContext mockedRenderContect = EasyMock.createNiceMock(IRenderContext.class);
        IResolve mockedGeoResource = EasyMock.createNiceMock(IResolve.class);
        
        TestGeoResource testGeoResource = new TestGeoResource(AbstractGridCoverage2DReader.class);
        EasyMock.expect(mockedRenderContect.getGeoResource()).andReturn(testGeoResource).anyTimes();
        EasyMock.replay(mockedGeoResource, mockedRenderContect);
        
        assertFalse(new BasicFeatureMetricsFactory().canRender(mockedRenderContect));
        
        EasyMock.verify(mockedGeoResource, mockedRenderContect);
        
    }

    @Test
    public void acceptOnFeatureSource() throws Exception {
        IRenderContext mockedRenderContect = EasyMock.createNiceMock(IRenderContext.class);
        IResolve mockedGeoResource = EasyMock.createNiceMock(IResolve.class);
        
        TestGeoResource testGeoResource = new TestGeoResource(FeatureSource.class);
        EasyMock.expect(mockedRenderContect.getGeoResource()).andReturn(testGeoResource).anyTimes();
        EasyMock.replay(mockedGeoResource, mockedRenderContect);
        
        assertTrue(new BasicFeatureMetricsFactory().canRender(mockedRenderContect));
        
        EasyMock.verify(mockedGeoResource, mockedRenderContect);
        
    }
}
