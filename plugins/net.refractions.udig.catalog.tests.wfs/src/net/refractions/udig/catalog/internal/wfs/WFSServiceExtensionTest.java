package net.refractions.udig.catalog.internal.wfs;

import java.net.URL;

import net.refractions.udig.catalog.wfs.internal.Messages;

import junit.framework.TestCase;

public class WFSServiceExtensionTest extends TestCase {


	public void testReasonForFailure() throws Exception {
		WFSServiceExtension ext=new WFSServiceExtension();
		URL url = new URL("http://something.ss?Service="); //$NON-NLS-1$
		String reason = ext.reasonForFailure(url);
		assertEquals(Messages.WFSServiceExtension_badService, reason);
	}
}
