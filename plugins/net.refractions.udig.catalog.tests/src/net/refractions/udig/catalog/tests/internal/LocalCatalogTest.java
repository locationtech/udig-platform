package net.refractions.udig.catalog.tests.internal;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.tests.AbstractCatalogTest;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author dzwiers
 */
public class LocalCatalogTest extends AbstractCatalogTest {

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.catalog.tests.AbstractCatalogTest#getResolve()
     */
    protected ICatalog getResolve() {
        return instance;
    }
    private ICatalog instance = null;

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        instance = new CatalogImpl();
        instance.add(new IService(){

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

            public URL getIdentifier() {
                try {
                    return new URL("http://localhost:1234/testing/1"); //$NON-NLS-1$
                } catch (MalformedURLException e) {
                    return null;
                }
            }
            public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
                return new IServiceInfo("Testing 1", "", "", getIdentifier(), (URL) null, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        (URI) null, (new String[]{"Test"}), (ImageDescriptor) null); //$NON-NLS-1$
            }
        });
        instance.add(new IService(){

            public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
                return null;
            }

            public List< ? extends IGeoResource> resources( IProgressMonitor monitor )
                    throws IOException {
            	ArrayList<IGeoResource> list = new ArrayList<IGeoResource>();
            	list.add( new IGeoResource(){

					@Override
					public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
						return super.resolve(adaptee, monitor);
					}
                    public IGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
                        return new IGeoResourceInfo("Test Title", "Test Name", "description", null,  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                new Envelope(20,30,0,40), DefaultGeographicCRS.WGS84, new String[0],
                                null);
                    }
                    public IService service( IProgressMonitor monitor ) throws IOException {
                        return null;
                    }
					public <T> boolean canResolve(Class<T> adaptee) {
						return super.canResolve(adaptee);
					}

					public Status getStatus() {
						return null;
					}

					public Throwable getMessage() {
						return null;
					}

					public URL getIdentifier() {
						return null;
					}

            	});
                return list;
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

            public URL getIdentifier() {
                try {
                    return new URL("http://localhost:1234/testing/2"); //$NON-NLS-1$
                } catch (MalformedURLException e) {
                    return null;
                }
            }
            public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
                return new IServiceInfo("Testing 2", "", "", getIdentifier(), (URL) null, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        (URI) null, (new String[]{"Test"}), (ImageDescriptor) null); //$NON-NLS-1$
            }
        });
    }
    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.catalog.tests.AbstractCatalogTest#mutable()
     */
    protected boolean mutable() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.catalog.tests.AbstractCatalogTest#getSearchBounds()
     */
    protected Envelope getSearchBounds() {
        return new Envelope(-180,180,-90,90);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.catalog.tests.AbstractCatalogTest#getSearchString()
     */
    protected String getSearchString() {
        return "Test"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.catalog.tests.AbstractResolveTest#hasParent()
     */
    protected boolean hasParent() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.catalog.tests.AbstractResolveTest#isLeaf()
     */
    protected boolean isLeaf() {
        return false;
    }

}
