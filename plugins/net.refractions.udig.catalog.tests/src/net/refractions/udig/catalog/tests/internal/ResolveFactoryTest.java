package net.refractions.udig.catalog.tests.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.refractions.udig.catalog.tests.DummyService;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

public class ResolveFactoryTest {

    @Test
    public void testResolve() throws Exception {
        DummyService service=new DummyService();
        assertTrue(service.canResolve(ResolvedTo.class));
        assertNotNull(service.resolve(ResolvedTo.class, new NullProgressMonitor()));
    }
}
