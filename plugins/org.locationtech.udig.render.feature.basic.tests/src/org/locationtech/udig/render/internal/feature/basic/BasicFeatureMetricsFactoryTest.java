package org.locationtech.udig.render.internal.feature.basic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FeatureSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.project.render.IRenderContext;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BasicFeatureMetricsFactoryTest {

    @Mock
    IRenderContext mockedRenderContext;

    @Mock
    IResolve mockedGeoResource;

    class TestGeoResource extends IGeoResource {

        Class resolveClass;

        TestGeoResource(Class resolveClass) {
            this.resolveClass = resolveClass;
        }

        @Override
        public Status getStatus() {
            return null;
        }

        @Override
        public Throwable getMessage() {
            return null;
        }

        @Override
        protected IGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException {
            return null;
        }

        @Override
        public URL getIdentifier() {
            return null;
        }

        @Override
        public <T> boolean canResolve(Class<T> adaptee) {
            if (resolveClass != null && adaptee != null
                    && adaptee.getName().equals(resolveClass.getName())) {
                return true;
            }
            return false;
        }
    }

    @Test
    public void giveCoveragePriorityOverShapefile() throws Exception {
        TestGeoResource testGeoResource = new TestGeoResource(AbstractGridCoverage2DReader.class);
        when(mockedRenderContext.getGeoResource()).thenReturn(testGeoResource);

        assertFalse(new BasicFeatureMetricsFactory().canRender(mockedRenderContext));

        verify(mockedRenderContext, atLeastOnce()).getGeoResource();
        verifyNoMoreInteractions(mockedGeoResource, mockedRenderContext);
    }

    @Test
    public void acceptOnFeatureSource() throws Exception {
        TestGeoResource testGeoResource = new TestGeoResource(FeatureSource.class);
        when(mockedRenderContext.getGeoResource()).thenReturn(testGeoResource);

        assertTrue(new BasicFeatureMetricsFactory().canRender(mockedRenderContext));

        verify(mockedRenderContext, atLeastOnce()).getGeoResource();
        verifyNoMoreInteractions(mockedGeoResource, mockedRenderContext);
    }
}
