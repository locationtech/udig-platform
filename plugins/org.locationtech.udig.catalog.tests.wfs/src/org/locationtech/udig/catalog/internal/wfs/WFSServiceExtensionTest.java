/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wfs;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.locationtech.udig.catalog.wfs.internal.Messages;

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
