package net.refractions.udig.catalog.internal.wfs;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import net.refractions.udig.catalog.wfs.internal.Messages;

import org.junit.Test;

public class WFSServiceExtensionTest {

	@Test
	public void testReasonForFailure() throws Exception {
		WFSServiceExtension ext=new WFSServiceExtension();
		URL url = new URL("http://something.ss?Service="); //$NON-NLS-1$
		String reason = ext.reasonForFailure(url);
		assertEquals(Messages.WFSServiceExtension_badService, reason);
	}
}
