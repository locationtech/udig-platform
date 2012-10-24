/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.tests.ui.dnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

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
import org.junit.Before;
import org.junit.Test;

public class CatalogViewDNDTest {

	private ICatalog catalog;

	private CatalogView view;

	private TreeViewer viewer;

	private UDIGDropHandler handler;

	@Before
	public void setUp() throws Exception {
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
	
    @Test
	public void testSingle() throws Throwable {
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

        UDIGTestUtil.inDisplayThreadWait(5000, condition, true);

        if( failure[0]!=null )
            throw failure[0];
		makeAssertion(getSingleDataAssertionDescription(), catalog);
	}

    @Test
	public void testMulti() throws Throwable {
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

		UDIGTestUtil.inDisplayThreadWait(20000, condition, false);
        
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
				new File("Does Not Exist").toURI().toURL() //$NON-NLS-1$
		};
	}

	void makeAssertion(String assertionDescription, ICatalog catalog) throws Exception {
		assertTrue(assertionDescription, !catalog.members(null).isEmpty());
	}

	void makeAssertionMulti(String assertionDescription, ICatalog catalog) {
		try {
			assertEquals(assertionDescription, 2, catalog.members(null).size());
		} catch (IOException e) {
			fail();
		}
	}

}