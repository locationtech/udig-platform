package net.refractions.udig.catalog.tests.ui;

import java.util.List;

import junit.framework.TestCase;
import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.NullProgressMonitor;

public class CatalogUIPluginTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
        List< ? extends IResolve> members = localCatalog.members(new NullProgressMonitor());
        
        for( IResolve resolve : members ) {
            localCatalog.remove((IService) resolve);
        }
        
        
    }
//
//    public void testHasCachedTitle() {
//        fail("Not yet implemented"); // TODO
//    }
//
//    public void testStoreLabel() {
//        fail("Not yet implemented"); // TODO
//    }

}
