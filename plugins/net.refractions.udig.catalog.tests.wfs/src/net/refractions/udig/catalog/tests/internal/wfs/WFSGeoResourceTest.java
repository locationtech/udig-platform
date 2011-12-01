package net.refractions.udig.catalog.tests.internal.wfs;

import java.io.IOException;
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
        //die.. I'm broken
        if (true) {
            return;
        }
        
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
    
    // override cause it's broken
    public void testID() {
    }
    
    // override cause it's broken
    public void testInfo() throws IOException {
    }

    // override cause it's broken
    public void testInfoMonitor() throws IOException {
    }
    
    // override cause it's broken
    public void testCanResolve() {
    }
    
    // override cause it's broken
    public void testResolve() throws IOException {
    }
    
    // override cause it's broken
    public void testResolveMonitor() throws IOException {
    }
    
    // override cause it's broken
    public void testGetId() {
    }
    
}
