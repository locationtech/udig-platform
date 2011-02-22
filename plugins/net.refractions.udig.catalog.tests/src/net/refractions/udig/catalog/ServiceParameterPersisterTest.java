package net.refractions.udig.catalog;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ServiceParameterPersisterTest extends TestCase {

	private static final String NODE_ID = "ServiceParameterPersisterTest"; //$NON-NLS-1$
	private TestPersister storer;
	private Preferences preferences;
	private NullProgressMonitor monitor;
	private TestPersister restorer;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		storer=new TestPersister();
		restorer=new TestPersister();
		preferences=Platform.getPreferencesService().getRootNode().node(NODE_ID);
		monitor=new NullProgressMonitor();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		preferences.clear();
		preferences.removeNode();
	}

	public void testPersistString() throws Exception {
		URL url=new URL("http://someurl.org"); //$NON-NLS-1$
		String id=new String("SomeString"); //$NON-NLS-1$
		TestService service=new TestService(url, id);

		assertPersistance(service);
	}

	private void assertPersistance(TestService service) throws BackingStoreException, IOException, MalformedURLException {
		storer.store(monitor, preferences, Collections.singleton(service));

		restorer.restore(preferences);

		assertEquals( service.params.toString() , restorer.map.toString() );
		assertTrue( URLUtils.urlEquals(service.url , restorer.url, false ) );
	}

	public void testPersistURL() throws Exception {
		URL url=new URL("http://someurl.org"); //$NON-NLS-1$
		URL id=new URL("http://anotherURL.org"); //$NON-NLS-1$
		TestService service=new TestService(url, id);

		assertPersistance(service);
	}

	public void testPersistInteger() throws Exception {
		URL url=new URL("http://someurl.org"); //$NON-NLS-1$
		Integer id=Integer.valueOf(66);
		TestService service=new TestService(url, id);

		assertPersistance(service);

	}

	public void testPersistFileURL() throws Exception {
		URL url=new URL("file:/c:\\someurl.org"); //$NON-NLS-1$
		URL id=new URL("file:/c:\\anotherURL.org"); //$NON-NLS-1$
		TestService service=new TestService(url, id);

		assertPersistance(service);
	}

	class TestPersister extends ServiceParameterPersister{
		URL url;
		Map<String, Serializable> map;

		public TestPersister() {
			super(CatalogPlugin.getDefault().getLocalCatalog(), CatalogPlugin.getDefault().getServiceFactory());
		}
		@Override
		protected void locateService(URL url, Map<String, Serializable> map) {
			this.url = url;
			this.map=map;
		}
	}
	class TestService extends IService{

		static final String PARAM_VALUE = "paramValue"; //$NON-NLS-1$
		final Map<String, Serializable> params;
		final URL url;

		public TestService(URL id, Serializable paramValue) {
			params=new HashMap<String, Serializable>();
			params.put(PARAM_VALUE, paramValue);
			this.url=id;
		}

		@Override
		public Map<String, Serializable> getConnectionParams() {
			return params;
		}

		@Override
		public IServiceInfo getInfo(IProgressMonitor monitor) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<? extends IGeoResource> resources(IProgressMonitor monitor) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		public URL getIdentifier() {
			return url;
		}

		public Throwable getMessage() {
			// TODO Auto-generated method stub
			return null;
		}

		public Status getStatus() {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
