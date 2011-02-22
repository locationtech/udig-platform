package net.refractions.udig.catalog.tests;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;

public abstract class AbstractServiceExtensionTest extends TestCase {
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
	public void testCreateParams() throws Exception {
		long start=System.currentTimeMillis();
		Map<String, Serializable> params = getServiceExtension().createParams(getTestURL());
		assertTrue("Took too long, shouldn't be blocking", start+BLOCK>=System.currentTimeMillis()); //$NON-NLS-1$
		assertEquals(getExpectedParams(), params);
	}

	public void testCreateParamsNullURL() throws Exception {
		long start=System.currentTimeMillis();
		Map<String, Serializable> params = getServiceExtension().createParams(null);
		assertTrue("Took too long, shouldn't be blocking", start+BLOCK>=System.currentTimeMillis()); //$NON-NLS-1$
		assertNull( params );
	}

	public void testCreateParamsCrazyURL() throws Exception {
		URL url=new URL("http://erk.bom"); //$NON-NLS-1$
		long start=System.currentTimeMillis();
		Map<String, Serializable> params = getServiceExtension().createParams(url);
		assertTrue("Took too long, shouldn't be blocking", start+BLOCK>=System.currentTimeMillis()); //$NON-NLS-1$
		assertNull( params );
	}

	public void testCreateService() throws Exception {
		Map<String, Serializable> params = getServiceExtension().createParams(getTestURL());
		long start=System.currentTimeMillis();
		IService service = getServiceExtension().createService(getTestURL(), params);
		assertTrue("Took too long, shouldn't be blocking", start+BLOCK>=System.currentTimeMillis()); //$NON-NLS-1$
		assertNotNull(service);
	}
	public void testCreateServiceNullId() throws Exception {
		Map<String, Serializable> params = getServiceExtension().createParams(getTestURL());
		long start=System.currentTimeMillis();
		IService service = getServiceExtension().createService(null, params);
		assertTrue("Took too long, shouldn't be blocking", start+BLOCK>=System.currentTimeMillis()); //$NON-NLS-1$
		assertNotNull(service);
	}
	public void testCreateServiceNullParams() throws Exception {
		long start=System.currentTimeMillis();
		IService service = getServiceExtension().createService(getTestURL(), null);
		assertTrue("Took too long, shouldn't be blocking", start+BLOCK>=System.currentTimeMillis()); //$NON-NLS-1$
		assertNotNull(service);
	}
}
