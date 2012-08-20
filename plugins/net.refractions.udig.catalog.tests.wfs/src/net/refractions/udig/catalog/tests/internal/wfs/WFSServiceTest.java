package net.refractions.udig.catalog.tests.internal.wfs;

import java.net.URL;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wfs.WFSServiceExtension;
import net.refractions.udig.catalog.tests.AbstractServiceTest;

import org.junit.Before;
import org.junit.Ignore;

/**
 * @author dzwiers
 */
@Ignore
public class WFSServiceTest extends AbstractServiceTest {

    private IService service = null;
    
    @Before
    public void setUp() throws Exception {
        WFSServiceExtension fac = new WFSServiceExtension();
        URL url = new URL("http://www.refractions.net:8080/geoserver/wfs?"); //$NON-NLS-1$
        service = fac.createService(url, fac.createParams(url));
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.catalog.tests.AbstractServiceTest#getResolve()
     */
    protected IService getResolve() {
        return service;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.catalog.tests.AbstractResolveTest#hasParent()
     */
    protected boolean hasParent() {
        return true;
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
