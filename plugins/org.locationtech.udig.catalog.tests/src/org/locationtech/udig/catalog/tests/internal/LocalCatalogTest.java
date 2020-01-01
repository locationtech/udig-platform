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
package org.locationtech.udig.catalog.tests.internal;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.internal.CatalogImpl;
import org.locationtech.udig.catalog.tests.AbstractCatalogTest;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;

import org.locationtech.jts.geom.Envelope;

/**
 * @author dzwiers
 */
public class LocalCatalogTest extends AbstractCatalogTest {

    private ICatalog instance = null;
    
    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.catalog.tests.AbstractCatalogTest#getResolve()
     */
    protected ICatalog getResolve() {
        return instance;
    }

    @Before
    public void setUp() throws Exception {
        instance = new CatalogImpl();
        instance.add(new IService(){

            public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
                return null;
            }

            public List< ? extends IGeoResource> resources( IProgressMonitor monitor )
                    throws IOException {
                return null;
            }

            public Map<String, Serializable> getConnectionParams() {
                return null;
            }

            public <T> boolean canResolve( Class<T> adaptee ) {
                return false;
            }

            public Status getStatus() {
                return null;
            }

            public Throwable getMessage() {
                return null;
            }

            public URL getIdentifier() {
                try {
                    return new URL("http://localhost:1234/testing/1"); //$NON-NLS-1$
                } catch (MalformedURLException e) {
                    return null;
                }
            }
            protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
                try {
                    return new IServiceInfo("Testing 1", "", "", getIdentifier().toURI(), (URI) null, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            (URI) null, (new String[]{"Test"}), (ImageDescriptor) null); //$NON-NLS-1$
                } catch (URISyntaxException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }                 
            }
        });
        instance.add(new IService(){

            public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
                return null;
            }

            public List< ? extends IGeoResource> resources( IProgressMonitor monitor )
                    throws IOException {
            	ArrayList<IGeoResource> list = new ArrayList<IGeoResource>();
            	list.add( new IGeoResource(){

					@Override
					public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
						return super.resolve(adaptee, monitor);
					}
                    protected IGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
                        return new IGeoResourceInfo("Test Title", "Test Name", "description", null,  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                new Envelope(20,30,0,40), DefaultGeographicCRS.WGS84, new String[0],
                                null);
                    }
                    public <T> boolean canResolve(Class<T> adaptee) {
						return super.canResolve(adaptee);
					}

					public Status getStatus() {
						return null;
					}

					public Throwable getMessage() {
						return null;
					}

					public URL getIdentifier() {
						return null;
					}
            		
            	});
                return list;
            }

            public Map<String, Serializable> getConnectionParams() {
                return null;
            }

            public <T> boolean canResolve( Class<T> adaptee ) {
                return false;
            }

            public Status getStatus() {
                return null;
            }

            public Throwable getMessage() {
                return null;
            }

            public URL getIdentifier() {
                try {
                    return new URL("http://localhost:1234/testing/2"); //$NON-NLS-1$
                } catch (MalformedURLException e) {
                    return null;
                }
            }
            protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
                try {
                    return new IServiceInfo("Testing 2", "", "", getIdentifier().toURI(), (URI) null, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            (URI) null, (new String[]{"Test"}), (ImageDescriptor) null); //$NON-NLS-1$
                } catch (URISyntaxException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                } 
            }
        });
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.catalog.tests.AbstractCatalogTest#mutable()
     */
    protected boolean mutable() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.catalog.tests.AbstractCatalogTest#getSearchBounds()
     */
    protected Envelope getSearchBounds() {
        return new Envelope(-180,180,-90,90);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.catalog.tests.AbstractCatalogTest#getSearchString()
     */
    protected String getSearchString() {
        return "Test"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.catalog.tests.AbstractResolveTest#hasParent()
     */
    protected boolean hasParent() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.catalog.tests.AbstractResolveTest#isLeaf()
     */
    protected boolean isLeaf() {
        return false;
    }

}
