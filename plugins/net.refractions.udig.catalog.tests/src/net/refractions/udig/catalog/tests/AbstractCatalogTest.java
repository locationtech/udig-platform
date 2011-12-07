/*
 * Created on 28-Mar-2005
 */
package net.refractions.udig.catalog.tests;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ICatalogInfo;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

import org.eclipse.core.runtime.IProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Sub-class me and fill in the appripriate protected methods ...
 * 
 * @author dzwiers
 */
public abstract class AbstractCatalogTest extends AbstractResolveTest {
    protected abstract ICatalog getResolve();

    protected abstract boolean mutable();

    public void testAddService() {
        if (mutable()) {
            long start = System.currentTimeMillis();
            IService service = new IService(){

                public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor )
                        throws IOException {
                    return null;
                }

                public List< ? extends IGeoResource> resources( IProgressMonitor monitor )
                        throws IOException {
                    return null;
                }

                public Map<String, Serializable> getConnectionParams() {
                    return null;
                }

                public <T> boolean canResolve( Class<T> adaptee ) {
                    return false;
                }

                public Status getStatus() {
                    return null;
                }

                public Throwable getMessage() {
                    return null;
                }

                protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
                    return null;
                }
                public URL getIdentifier() {
                    try {
                        return new URL("http://localhost/udig-tests"); //$NON-NLS-1$
                    } catch (MalformedURLException e) {
                        fail(e.toString());
                        return null;
                    }
                }

            };
            getResolve().add(service);
            List<IResolve> services = getResolve().find(service.getIdentifier(), null);
            assertNotNull("should return a value", services); //$NON-NLS-1$
            assertEquals("Should only return one value", 1, services.size()); //$NON-NLS-1$
            assertTrue("The Service returned is not my service :(", services.contains(service)); //$NON-NLS-1$
            assertTrue("Took too long ... blocking?", (start + BLOCK) >= System.currentTimeMillis()); //$NON-NLS-1$
        }
    }

    public void testRemoveService() {
        if (mutable()) {
            long start = System.currentTimeMillis();
            IService service = new IService(){

                public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor )
                        throws IOException {
                    return null;
                }

                public List< ? extends IGeoResource> resources( IProgressMonitor monitor )
                        throws IOException {
                    return null;
                }

                public Map<String, Serializable> getConnectionParams() {
                    return null;
                }

                public <T> boolean canResolve( Class<T> adaptee ) {
                    return false;
                }

                public Status getStatus() {
                    return null;
                }

                protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
                    return null;
                }
                public Throwable getMessage() {
                    return null;
                }

                public URL getIdentifier() {
                    try {
                        return new URL("http://localhost/udig-tests"); //$NON-NLS-1$
                    } catch (MalformedURLException e) {
                        fail(e.toString());
                        return null;
                    }
                }

            };
            List<IResolve> services = getResolve().find(service.getIdentifier(), null);
            if (!services.contains(service))
                getResolve().add(service);

            getResolve().remove(service);
            services = getResolve().find(service.getIdentifier(), null);
            assertFalse("Shouldn't contain the service", services.contains(service)); //$NON-NLS-1$
            assertTrue("Took too long ... blocking?", (start + BLOCK) >= System.currentTimeMillis()); //$NON-NLS-1$
        }
    }

    public void testReplaceService() {
        if (mutable()) {
            long start = System.currentTimeMillis();
            IService service1 = new IService(){

                public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor )
                        throws IOException {
                    return null;
                }

                public List< ? extends IGeoResource> resources( IProgressMonitor monitor )
                        throws IOException {
                    return null;
                }

                public Map<String, Serializable> getConnectionParams() {
                    return null;
                }

                public <T> boolean canResolve( Class<T> adaptee ) {
                    return false;
                }

                protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
                    return null;
                }
                public Status getStatus() {
                    return null;
                }

                public Throwable getMessage() {
                    return null;
                }

                public URL getIdentifier() {
                    try {
                        return new URL("http://localhost/udig-tests/1"); //$NON-NLS-1$
                    } catch (MalformedURLException e) {
                        fail(e.toString());
                        return null;
                    }
                }

            };
            IService service2 = new IService(){

                public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor )
                        throws IOException {
                    return null;
                }

                public List< ? extends IGeoResource> resources( IProgressMonitor monitor )
                        throws IOException {
                    return null;
                }

                public Map<String, Serializable> getConnectionParams() {
                    return null;
                }

                protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
                    return null;
                }
                public <T> boolean canResolve( Class<T> adaptee ) {
                    return false;
                }

                public Status getStatus() {
                    return null;
                }

                public Throwable getMessage() {
                    return null;
                }

                public URL getIdentifier() {
                    try {
                        return new URL("http://localhost/udig-tests/2"); //$NON-NLS-1$
                    } catch (MalformedURLException e) {
                        fail(e.toString());
                        return null;
                    }
                }

            };
            getResolve().add(service1);
            getResolve().replace(service1.getID(), service2);
            List<IResolve> services = getResolve().find(service2.getIdentifier(), null);
            assertFalse("Shouldn't contain the service", services.contains(service1)); //$NON-NLS-1$
            assertTrue("Should contain the service", services.contains(service2)); //$NON-NLS-1$
            assertTrue("Took too long ... blocking?", (start + BLOCK) >= System.currentTimeMillis()); //$NON-NLS-1$
        }
    }

    public void testInfo() throws IOException {
        ICatalogInfo info = getResolve().getInfo(null);
        assertNotNull("Info is required", info); //$NON-NLS-1$
    }

    public void testInfoMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        ICatalogInfo info = getResolve().getInfo(monitor);
        assertNotNull("Info is required", info); //$NON-NLS-1$
        assertEquals( "Monitor must be finished",  monitor.total, monitor.completed); //$NON-NLS-1$
        assertTrue("Monitor must be used.", monitor.total != 0); //$NON-NLS-1$
    }

    protected abstract Envelope getSearchBounds();
    protected abstract String getSearchString();

    public void xtestSearch() throws IOException {
        if (getSearchString() != null) {
            List<IResolve> results = getResolve().search(getSearchString(), null, null);
            assertNotNull("Must return a non-null list", results); //$NON-NLS-1$
            assertTrue("Must have found at least one item", results.size() > 0); //$NON-NLS-1$
        }
        if (getSearchBounds() != null && !getSearchBounds().isNull()) {
            List<IResolve> results = getResolve().search(null, getSearchBounds(), null);
            assertNotNull("Must return a non-null list", results); //$NON-NLS-1$
            assertTrue("Must have found at least one item", results.size() > 0); //$NON-NLS-1$
        }
        if (getSearchString() != null && getSearchBounds() != null) {
            List<IResolve> results = getResolve()
                    .search(getSearchString(), getSearchBounds(), null);
            assertNotNull("Must return a non-null list", results); //$NON-NLS-1$
            assertTrue("Must have found at least one item", results.size() > 0); //$NON-NLS-1$
        }
    }

    public void xtestSearchMonitor() throws IOException {
        if (getSearchString() != null) {
            FakeProgress monitor = new FakeProgress();
            List<IResolve> results = getResolve().search(getSearchString(), null, monitor);
            assertNotNull("Must return a non-null list", results); //$NON-NLS-1$
            assertTrue("Must have found at least one item", results.size() > 0); //$NON-NLS-1$
            assertEquals( "Monitor must be finished",  monitor.total, monitor.completed); //$NON-NLS-1$
            assertTrue("Monitor must be used.", monitor.total != 0); //$NON-NLS-1$
        }
        if (getSearchBounds() != null) {
            FakeProgress monitor = new FakeProgress();
            List<IResolve> results = getResolve().search(null, getSearchBounds(), monitor);
            assertNotNull("Must return a non-null list", results); //$NON-NLS-1$
            assertTrue("Must have found at least one item", results.size() > 0); //$NON-NLS-1$
            assertEquals( "Monitor must be finished",  monitor.total, monitor.completed); //$NON-NLS-1$
            assertTrue("Monitor must be used.", monitor.total != 0); //$NON-NLS-1$
        }
        if (getSearchString() != null && getSearchBounds() != null) {
            FakeProgress monitor = new FakeProgress();
            List<IResolve> results = getResolve()
                    .search(getSearchString(), getSearchBounds(), monitor);
            assertNotNull("Must return a non-null list", results); //$NON-NLS-1$
            assertTrue("Must have found at least one item", results.size() > 0); //$NON-NLS-1$
            assertEquals( "Monitor must be finished",  monitor.total, monitor.completed); //$NON-NLS-1$
            assertTrue("Monitor must be used.", monitor.total != 0); //$NON-NLS-1$
        }
    }

    public void testAddRemoveListener() {
        long start = System.currentTimeMillis();
        IResolveChangeListener listner = new IResolveChangeListener(){

            public void changed( IResolveChangeEvent event ) {
                // no op
            }

        };
        getResolve().addCatalogListener(listner);

        getResolve().removeCatalogListener(listner);
        assertTrue("Took too long ... blocking?", (start + BLOCK) >= System.currentTimeMillis()); //$NON-NLS-1$
    }

    public void testEventsSentToListner() {
        class BWrap {
            boolean value = false;
        }

        final BWrap action = new BWrap();
        long start = System.currentTimeMillis();

        IResolveChangeListener listner = new IResolveChangeListener(){

            public void changed( IResolveChangeEvent event ) {
                action.value = true;
            }

        };
        getResolve().addCatalogListener(listner);

        IService service = new IService(){

            public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
                return null;
            }

            public List< ? extends IGeoResource> resources( IProgressMonitor monitor )
                    throws IOException {
                return null;
            }

            public Map<String, Serializable> getConnectionParams() {
                return null;
            }

            public <T> boolean canResolve( Class<T> adaptee ) {
                return false;
            }

            public Status getStatus() {
                return null;
            }

            public Throwable getMessage() {
                return null;
            }
            protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
                return null;
            }
            public URL getIdentifier() {
                try {
                    return new URL("http://localhost/udig-tests/listen"); //$NON-NLS-1$
                } catch (MalformedURLException e) {
                    fail(e.toString());
                    return null;
                }
            }

        };

        getResolve().add(service);
        assertTrue("An event should have occurred", action.value); //$NON-NLS-1$
        getResolve().removeCatalogListener(listner);

        action.value = false; // reset
        getResolve().remove(service);
        assertFalse("An event should not have occurred", action.value); //$NON-NLS-1$
        assertTrue("Took too long ... blocking?", (start + BLOCK) >= System.currentTimeMillis()); //$NON-NLS-1$
    }

    public void testCanResolve() {
        long start = System.currentTimeMillis();
        assertTrue("Must resolve List.class", getResolve().canResolve(List.class)); //$NON-NLS-1$
        assertTrue("Must resolve ICatalogInfo.class", getResolve().canResolve(ICatalogInfo.class)); //$NON-NLS-1$
        assertTrue("Took too long ... blocking?", (start + BLOCK) >= System.currentTimeMillis()); //$NON-NLS-1$
    }

    public void testResolve() throws IOException {
        List children = getResolve().resolve(List.class, null);
        if (!isLeaf())
            assertTrue("May not have null children", children != null); //$NON-NLS-1$
        ICatalogInfo info = getResolve().resolve(ICatalogInfo.class, null);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
    }

    public void testResolveMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        List children = getResolve().resolve(List.class, monitor);
        if (!isLeaf())
            assertTrue("May not have null children", children != null); //$NON-NLS-1$
        // should still be taken care of
        assertTrue("Monitor must be used.", //$NON-NLS-1$
                ((monitor.total == monitor.completed) && (monitor.total != 0)));

        monitor = new FakeProgress();
        ICatalogInfo info = getResolve().resolve(ICatalogInfo.class, monitor);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
        // should still be taken care of
        assertTrue("Monitor must be used.", //$NON-NLS-1$
                ((monitor.total == monitor.completed) && (monitor.total != 0)));
    }
}
