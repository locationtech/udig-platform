package net.refractions.udig.catalog.tests.ui;

import static org.junit.Assert.fail;

import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CatalogUIPluginTest {

    @Before
    public void setUp() throws Exception {
        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
        List< ? extends IResolve> members = localCatalog.members(new NullProgressMonitor());
        
        for( IResolve resolve : members ) {
            localCatalog.remove((IService) resolve);
        }
        
        
    }
    
    @Ignore
    @Test
    public void testHasCachedTitle() {
        fail("Not yet implemented"); // TODO
    }

    @Ignore
    @Test
    public void testStoreLabel() {
        fail("Not yet implemented"); // TODO
    }

}
