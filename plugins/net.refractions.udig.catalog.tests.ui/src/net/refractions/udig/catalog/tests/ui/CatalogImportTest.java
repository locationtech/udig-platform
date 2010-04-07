package net.refractions.udig.catalog.tests.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.ui.CatalogImport;
import net.refractions.udig.catalog.internal.wms.WMSServiceImpl;
import net.refractions.udig.catalog.tests.ui.workflow.Assertion;
import net.refractions.udig.catalog.tests.ui.workflow.DialogDriver;
import net.refractions.udig.catalog.tests.ui.workflow.DummyMonitor;
import net.refractions.udig.catalog.ui.ConnectionErrorPage;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.jface.dialogs.IDialogConstants;

public class CatalogImportTest extends TestCase {
	
	CatalogImport catalogImport;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		catalogImport = new CatalogImport();
	}
	
	public void testNormal() throws Exception{
			Object context = getContext();
			
			final ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
			
			List members = catalog.members(new DummyMonitor());
			if (!members.isEmpty()) {
				//clear the catalog
				for (Iterator itr = members.iterator(); itr.hasNext();) {
					IService service = (IService)itr.next();
					catalog.remove(service);
				}
			}
			members = catalog.members(new DummyMonitor());
			assertTrue(members.isEmpty());
			
			catalogImport.getDialog().getWorkflowWizard().getWorkflow()
				.setContext(context);
			catalogImport.run(new DummyMonitor(),context);
			
            //sleep for 10 seconds, if dialog still active by then kill it
            UDIGTestUtil.inDisplayThreadWait(2000000, new WaitCondition(){

                public boolean isTrue() {
                    try {
                        return !catalog.members(new DummyMonitor()).isEmpty();
                    } catch (IOException e) {
                        return false;
                    }
                }
                
            }, true);
            
			members = catalog.members(new DummyMonitor());
			assertTrue(!members.isEmpty());
			for (Iterator itr = members.iterator(); itr.hasNext();) {
				assertServiceType((IService)itr.next());
			}
	}

	public void testConnectionError() throws MalformedURLException {
			//create a bad context object, lets say a wfs that doesn't exist
			URL context = new URL("http://foo.blah.hehehe/geoserver/wfs"); //$NON-NLS-1$
			
			
			Assertion a1 = new Assertion() {
				@Override
				public void run() {
					fail = !(catalogImport.getDialog().getCurrentPage() instanceof ConnectionErrorPage);
				}
			};
			//sleep for 10 seconds, if dialog still active by then kill it
			DialogDriver driver = new DialogDriver(
				catalogImport.getDialog(), 
				new Object[]{
					a1, IDialogConstants.CANCEL_ID
				}
			);
			
			driver.schedule();
			catalogImport.getDialog().getWorkflowWizard().getWorkflow()
				.setContext(context);
			catalogImport.run(new DummyMonitor(),context);
			driver.cancel();
	}
	
	Object getContext() throws Exception {
		return new URL("http://www.refractions.net:8080/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities"); //$NON-NLS-1$
	}
	
	void assertServiceType(IService service) {
		assertTrue(service instanceof WMSServiceImpl);
	}
}