package net.refractions.udig.catalog.tests.internal.wfs;

import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wfs.WFSServiceExtension;
import net.refractions.udig.catalog.tests.AbstractGeoResourceTest;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author dzwiers
 */
public class WFSGeoResourceTest extends AbstractGeoResourceTest {

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        WFSServiceExtension fac = new WFSServiceExtension();
        URL url = new URL("http://www.refractions.net:8080/geoserver/wfs?"); //$NON-NLS-1$
        service = fac.createService(url, fac.createParams(url));
        resource = service.resources((IProgressMonitor) null).get(0);
    }
    private IService service = null;
    private IGeoResource resource = null;
    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.catalog.tests.AbstractGeoResourceTest#getResolve()
     */
    protected IGeoResource getResolve() {
        return resource;
    }
    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.catalog.tests.AbstractResolveTest#hasParent()
     */
    protected boolean hasParent() {
        return true;
    }
}
