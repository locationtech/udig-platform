package net.refractions.udig.catalog.tests;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

public abstract class AbstractServiceTest extends AbstractResolveTest {
    protected abstract IService getResolve();

    public void xtestInfo() throws IOException {
        IServiceInfo info = getResolve().getInfo(null);
        assertNotNull("Info is required", info); //$NON-NLS-1$
    }

    public void xtestInfoMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        IServiceInfo info = getResolve().getInfo(monitor);
        assertNotNull("Info is required", info); //$NON-NLS-1$
    }

    public void testGetParams() {
        long start = System.currentTimeMillis();
        Map<String, Serializable> params = getResolve().getConnectionParams();
        assertNotNull("The params cannot be null", params); //$NON-NLS-1$
        assertTrue("Took too long ... blocking?", (start + BLOCK) >= System.currentTimeMillis()); //$NON-NLS-1$
    }

    public void testCanResolve() {
        long start = System.currentTimeMillis();
        assertTrue("Must resolve List.class", getResolve().canResolve(List.class)); //$NON-NLS-1$
        assertTrue("Must resolve IServiceInfo.class", getResolve().canResolve(IServiceInfo.class)); //$NON-NLS-1$
        assertTrue("Took too long ... blocking?", (start + BLOCK) >= System.currentTimeMillis()); //$NON-NLS-1$
    }

    public void xtestResolve() throws IOException {
        IServiceInfo info = getResolve().resolve(IServiceInfo.class, null);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
    }

    public void xtestResolveMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();

        monitor = new FakeProgress();
        IServiceInfo info = getResolve().resolve(IServiceInfo.class, monitor);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
    }
    
}
