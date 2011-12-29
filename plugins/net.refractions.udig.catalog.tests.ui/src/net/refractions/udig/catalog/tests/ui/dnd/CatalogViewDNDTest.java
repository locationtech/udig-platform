package net.refractions.udig.catalog.tests.ui.dnd;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import junit.framework.TestCase;
import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.ui.CatalogView;
import net.refractions.udig.catalog.tests.DummyService;
import net.refractions.udig.catalog.tests.ui.workflow.DummyMonitor;
import net.refractions.udig.internal.ui.StaticDestinationProvider;
import net.refractions.udig.internal.ui.UDIGDropHandler;
import net.refractions.udig.internal.ui.UDIGViewerDropAdapter;
import net.refractions.udig.ui.IDropAction;
import net.refractions.udig.ui.IDropHandlerListener;
import net.refractions.udig.ui.ViewerDropLocation;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;

public class CatalogViewDNDTest extends TestCase {

	private ICatalog catalog;

	private CatalogView view;

	private TreeViewer viewer;

	private UDIGDropHandler handler;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
		    catalog = CatalogPlugin.getDefault().getLocalCatalog();
		}
		catch( NullPointerException npe ){
		    fail("Please run as a Plug-in Test");
		}
		for (Iterator itr = catalog.members(new DummyMonitor()).iterator(); itr
				.hasNext();) {
			catalog.remove((IService) itr.next());
		}

		view = (CatalogView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().showView(
						CatalogView.VIEW_ID);
		viewer = view.getTreeviewer();
		UDIGViewerDropAdapter adapter = new UDIGViewerDropAdapter(viewer,
				new StaticDestinationProvider(view));
		handler = adapter.getDropHandler();
        handler.setTarget(view);
		handler.setViewerLocation(ViewerDropLocation.NONE);
	}

	public void testStub() throws Throwable {
	    assertTrue(true);
	}
	
	public void xtestSingle() throws Throwable {
		Object data = getData();

        final Throwable[] failure=new Throwable[1];
        handler.addListener(new IDropHandlerListener(){

            public void done( IDropAction action, Throwable error ) {
                if( error!=null ){
                    failure[0]=error;
                }
            }

            public void noAction( Object data ) {
            }

            public void starting( IDropAction action ) {
            }
            
        });
		handler.performDrop(data, null);

		// this method does work in another thread so we have to wait
		WaitCondition condition = new WaitCondition() {
			public boolean isTrue()  {
				try {
                    return !catalog.members(null).isEmpty();
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
			};
		};

        UDIGTestUtil.inDisplayThreadWait(4000, condition, true);
//        UDIGTestUtil.inDisplayThreadWait(400000, condition);

        if( failure[0]!=null )
            throw failure[0];
		makeAssertion(getSingleDataAssertionDescription(), catalog);
	}

	public void xtestMulti() throws Throwable {
		Object data = getDataMulti();

        final Throwable[] failure=new Throwable[1];
        handler.addListener(new IDropHandlerListener(){

            public void done( IDropAction action, Throwable error ) {
                if( error!=null ){
                    failure[0]=error;
                }
            }

            public void noAction( Object data ) {
            }

            public void starting( IDropAction action ) {
            }
            
        });


		handler.performDrop(data, null);

		// this method does work in another thread so we have to wait
		WaitCondition condition = new WaitCondition() {
			public boolean isTrue()  {
				try {
                    return catalog.members(null).size()>1;
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
			};
		};

		UDIGTestUtil.inDisplayThreadWait(4000, condition, false);
//		UDIGTestUtil.inDisplayThreadWait(400000, condition);
        
        if( failure[0]!=null )
            throw failure[0];

		makeAssertionMulti(getMultiAssertionDescription(), catalog);

	}

    protected String getMultiAssertionDescription() {
        return "Multiple dummy resource objects should be in catalog"; //$NON-NLS-1$
    }
    protected String getSingleDataAssertionDescription() {
        return "At least 1 dummy resource objects should be in catalog"; //$NON-NLS-1$
    }

    protected Object getData() throws Exception {
		return DummyService.url;

	}

	Object getDataMulti() throws Exception {
		return new URL[] {
				new URL(DummyService.url.toExternalForm() + "/dummy1"), //$NON-NLS-1$
				new URL(DummyService.url.toExternalForm() + "/dummy2"), //$NON-NLS-1$
				new File("Does Not Exist").toURL() //$NON-NLS-1$
		};
	}

	void makeAssertion(String assertionDescription, ICatalog catalog) throws Exception {
		assertTrue(assertionDescription, !catalog.members(null).isEmpty());
	}

	void makeAssertionMulti(String assertionDescription, ICatalog catalog) {
		try {
			assertEquals(assertionDescription, catalog.members(null).size(), 2);
		} catch (IOException e) {
			fail();
		}
	}

}