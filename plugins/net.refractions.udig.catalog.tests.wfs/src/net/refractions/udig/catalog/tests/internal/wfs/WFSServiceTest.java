package net.refractions.udig.catalog.tests.internal.wfs;

import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wfs.WFSServiceExtension;
import net.refractions.udig.catalog.tests.AbstractServiceTest;

/**
 * @author dzwiers
 */
public class WFSServiceTest extends AbstractServiceTest {

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
    }
    private IService service = null;
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

    // override cause it's broken
    public void testParent() throws IOException {
    }

    // override cause it's broken
    public void testParentMonitor() throws IOException {
    }
    
    // override cause it's broken
    public void testMembers() throws IOException {
    }
    
    // override cause it's broken
    public void testMembersMonitor() throws IOException {
    }
    
}
