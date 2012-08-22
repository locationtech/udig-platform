package net.refractions.udig.catalog.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

import org.junit.Ignore;
import org.junit.Test;

public abstract class AbstractServiceTest extends AbstractResolveTest {
    
    protected abstract IService getResolve();

    @Ignore
    @Test
    public void testInfo() throws IOException {
        IServiceInfo info = getResolve().getInfo(null);
        assertNotNull("Info is required", info); //$NON-NLS-1$
    }

    @Ignore
    @Test
    public void testInfoMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        IServiceInfo info = getResolve().getInfo(monitor);
        assertNotNull("Info is required", info); //$NON-NLS-1$
    }

    @Test(timeout = BLOCK)
    public void testGetParams() {
        Map<String, Serializable> params = getResolve().getConnectionParams();
        assertNotNull("The params cannot be null", params); //$NON-NLS-1$
    }

    @Test(timeout = BLOCK)
    public void testCanResolve() {
        assertTrue("Must resolve List.class", getResolve().canResolve(List.class)); //$NON-NLS-1$
        assertTrue("Must resolve IServiceInfo.class", getResolve().canResolve(IServiceInfo.class)); //$NON-NLS-1$
    }

    @Ignore
    @Test
    public void testResolve() throws IOException {
        IServiceInfo info = getResolve().resolve(IServiceInfo.class, null);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
    }

    @Ignore
    @Test
    public void testResolveMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        IServiceInfo info = getResolve().resolve(IServiceInfo.class, monitor);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
    }
    
}
