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
package org.locationtech.udig.catalog.tests.ui.dnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.ui.CatalogView;
import org.locationtech.udig.catalog.tests.ui.workflow.DummyMonitor;
import org.locationtech.udig.catalog.util.CatalogTestUtils;
import org.locationtech.udig.internal.ui.StaticDestinationProvider;
import org.locationtech.udig.internal.ui.UDIGDropHandler;
import org.locationtech.udig.internal.ui.UDIGViewerDropAdapter;
import org.locationtech.udig.ui.IDropAction;
import org.locationtech.udig.ui.IDropHandlerListener;
import org.locationtech.udig.ui.ViewerDropLocation;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

public abstract class CatalogViewDNDTest {

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
        URL data = getData();
        CatalogTestUtils.assumeNoConnectionException(Collections.singletonList(data), 1000);

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
        URL[] data = getDataMulti();
        CatalogTestUtils.assumeNoConnectionException(Arrays.asList(getDataMulti()), 1000);
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

    abstract URL getData() throws Exception;
    abstract URL[] getDataMulti() throws Exception;

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
