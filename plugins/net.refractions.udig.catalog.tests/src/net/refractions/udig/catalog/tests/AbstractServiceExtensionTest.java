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
package net.refractions.udig.catalog.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;

import org.junit.Test;

public abstract class AbstractServiceExtensionTest {
	private static final long BLOCK = 150;

	/**
	 * Must return the Service extension to be tested
	 * @return the service extension being tested
	 */
	public abstract ServiceExtension getServiceExtension();
	/**
	 * Returns a url that should be valid for the Service Extension.
	 * @return a url that should be valid for the Service Extension
	 */
	public abstract URL getTestURL();
	/**
	 * Returns the parameters that should be obtained by using the ServiceExtension
	 * to create the parameters using the url from getTestURL().
	 * @return
	 */
	public abstract Map<String, Serializable> getExpectedParams();
	
	@Test(timeout = BLOCK)
	public void testCreateParams() throws Exception {
		Map<String, Serializable> params = getServiceExtension().createParams(getTestURL());
		assertEquals(getExpectedParams(), params);
	}
	
	@Test(timeout = BLOCK)
	public void testCreateParamsNullURL() throws Exception {
		Map<String, Serializable> params = getServiceExtension().createParams(null);
		assertNull( params );
	}
	
	@Test(timeout = BLOCK)
	public void testCreateParamsCrazyURL() throws Exception {
		URL url=new URL("http://erk.bom"); //$NON-NLS-1$
		Map<String, Serializable> params = getServiceExtension().createParams(url);
		assertNull( params );
	}
	
	@Test(timeout = BLOCK)
	public void testCreateService() throws Exception {
		Map<String, Serializable> params = getServiceExtension().createParams(getTestURL());
		IService service = getServiceExtension().createService(getTestURL(), params);
		assertNotNull(service);
	}
	
	@Test(timeout = BLOCK)
	public void testCreateServiceNullId() throws Exception {
		Map<String, Serializable> params = getServiceExtension().createParams(getTestURL());
		IService service = getServiceExtension().createService(null, params);
		assertNotNull(service);
	}
	
	@Test(timeout = BLOCK)
	public void testCreateServiceNullParams() throws Exception {
		IService service = getServiceExtension().createService(getTestURL(), null);
		assertNotNull(service);
	}
}
