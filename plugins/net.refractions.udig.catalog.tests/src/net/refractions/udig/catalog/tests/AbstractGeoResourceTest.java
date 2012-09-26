package net.refractions.udig.catalog.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.Callable;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

/**
 * @author dzwiers
 */
public abstract class AbstractGeoResourceTest extends AbstractResolveTest {
    protected abstract IGeoResource getResolve();

    protected boolean isLeaf() {
        return true;
    }

    @Test
    public void testInfo() throws IOException {
        IGeoResourceInfo info = getInfo(getResolve(), null);
        assertNotNull("Info is required", info); //$NON-NLS-1$
    }

    @Test
    public void testInfoMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        IGeoResourceInfo info = getInfo(getResolve(), monitor);
        assertNotNull("Info is required", info); //$NON-NLS-1$
    }

    @Test(timeout = BLOCK)
    public void testCanResolve() {
        assertTrue("Must resolve IService.class", getResolve().canResolve(IService.class)); //$NON-NLS-1$
        assertTrue("Must resolve IGeoResourceInfo.class", getResolve().canResolve( //$NON-NLS-1$
                IGeoResourceInfo.class));
    }

    @Test
    public void testResolve() throws IOException {
        IService children = getResolve().resolve(IService.class, null);
        assertTrue("May not have null parent", children != null); //$NON-NLS-1$
        IGeoResourceInfo info = getResolve().resolve(IGeoResourceInfo.class, null);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
    }

    @Test
    public void testResolveMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        IService children = getResolve().resolve(IService.class, monitor);
        assertTrue("May not have null parent", children != null); //$NON-NLS-1$

        monitor = new FakeProgress();
        IGeoResourceInfo info = getResolve().resolve(IGeoResourceInfo.class, monitor);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
    }
    
    @Test(timeout = BLOCK)
    public void testGetId() {
    	assertNotNull( getResolve().getIdentifier() );
        assertNotNull(getResolve().getIdentifier().getRef());
    }
 
    protected IGeoResourceInfo getInfo(final IGeoResource geoResource, final IProgressMonitor monitor)
            throws IOException {
        final Callable<IGeoResourceInfo> job = new Callable<IGeoResourceInfo>() {
            
            @Override
            public IGeoResourceInfo call() throws Exception {
                return geoResource.getInfo(monitor);
            }
            
        };
        
        return retrieveInNewThread(job);
    }

}
