package net.refractions.udig.catalog.tests;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;

/**
 * @author dzwiers
 */
public abstract class AbstractGeoResourceTest extends AbstractResolveTest {
    protected abstract IGeoResource getResolve();

    protected boolean isLeaf() {
        return true;
    }

    public void testInfo() throws IOException {
        IGeoResourceInfo info = getResolve().getInfo(null);
        assertNotNull("Info is required", info); //$NON-NLS-1$
    }

    public void testInfoMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        IGeoResourceInfo info = getResolve().getInfo(monitor);
        assertNotNull("Info is required", info); //$NON-NLS-1$
    }

    public void testCanResolve() {
        long start = System.currentTimeMillis();
        assertTrue("Must resolve IService.class", getResolve().canResolve(IService.class)); //$NON-NLS-1$
        assertTrue("Must resolve IGeoResourceInfo.class", getResolve().canResolve( //$NON-NLS-1$
                IGeoResourceInfo.class));
        assertTrue("Took too long ... blocking?", (start + BLOCK) >= System.currentTimeMillis()); //$NON-NLS-1$
    }

    public void testResolve() throws IOException {
        IService children = getResolve().resolve(IService.class, null);
        assertTrue("May not have null parent", children != null); //$NON-NLS-1$
        IGeoResourceInfo info = getResolve().resolve(IGeoResourceInfo.class, null);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
    }

    public void testResolveMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        IService children = getResolve().resolve(IService.class, monitor);
        assertTrue("May not have null parent", children != null); //$NON-NLS-1$

        monitor = new FakeProgress();
        IGeoResourceInfo info = getResolve().resolve(IGeoResourceInfo.class, monitor);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
    }

    public void testGetId(){
        long start = System.currentTimeMillis();
    	assertNotNull( getResolve().getIdentifier() );
        assertTrue("Took too long ... blocking?", (start + BLOCK) >= System.currentTimeMillis()); //$NON-NLS-1$
        assertNotNull(getResolve().getIdentifier().getRef());
    }

}
