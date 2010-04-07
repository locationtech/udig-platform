package net.refractions.udig.catalog.tests.internal;

import junit.framework.TestCase;
import net.refractions.udig.catalog.tests.DummyService;

import org.eclipse.core.runtime.NullProgressMonitor;

public class ResolveFactoryTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testResolve() throws Exception {
        DummyService service=new DummyService();
        assertTrue(service.canResolve(ResolvedTo.class));
        assertNotNull(service.resolve(ResolvedTo.class, new NullProgressMonitor()));
    }
}
