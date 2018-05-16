package org.locationtech.udig.catalog.tests.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.catalog.tests.ui.workflow.Assertion;
import org.locationtech.udig.catalog.tests.ui.workflow.DialogDriver;
import org.locationtech.udig.catalog.tests.ui.workflow.DummyMonitor;
import org.locationtech.udig.catalog.ui.ConnectionErrorPage;
import org.locationtech.udig.catalog.ui.wizard.CatalogImport;

public class ConnectionErrorTest {

    CatalogImport catalogImport;

    @Before
    public void setUp() throws Exception {
        catalogImport = new CatalogImport();
    }

    @Test
    public void testConnectionError() throws MalformedURLException {
        // create a bad context object, lets say a wfs that doesn't exist
        URL context = new URL("http://foo.blah.hehehe/geoserver/wfs"); //$NON-NLS-1$

        Assertion a1 = new Assertion(){
            @Override
            public void run() {
                fail = !(catalogImport.getDialog().getCurrentPage() instanceof ConnectionErrorPage);
            }
        };
        // sleep for 10 seconds, if dialog still active by then kill it
        DialogDriver driver = new DialogDriver(catalogImport.getDialog(), new Object[]{a1,
                IDialogConstants.CANCEL_ID});

        driver.schedule();
        catalogImport.getDialog().getWorkflowWizard().getWorkflow().setContext(context);
        catalogImport.run(new DummyMonitor(), context);
        driver.cancel();
    }
}
