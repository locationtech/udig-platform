/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.google;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalogInfo;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.catalog.IResolveChangeListener;
import org.locationtech.udig.catalog.ISearch;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.google.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import org.locationtech.jts.geom.Envelope;

public class GoogleCatalog extends ISearch {

    private Throwable msg;
    URL url = null;
    
    /**
     * Construct <code>GoogleCatalog</code>.
     *
     */
    public GoogleCatalog() {
        catalogListeners = new ListenerList(org.eclipse.core.runtime.ListenerList.IDENTITY);
        try {
            url = new URL("http://udig.refractions.net/search/google-xml.php?"); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            msg = e;
            GooglePlugin.log(null,e);
        }
    }
    private ListenerList catalogListeners;
    
    /*
     * @see org.locationtech.udig.catalog.ICatalog#add(org.locationtech.udig.catalog.IService)
     */
    public void add( IService service ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * @see org.locationtech.udig.catalog.ICatalog#remove(org.locationtech.udig.catalog.IService)
     */
    public void remove( IService service ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * @see org.locationtech.udig.catalog.ICatalog#replace(java.net.URL, org.locationtech.udig.catalog.IService)
     */
    public void replace( URL id, IService service ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * Required adaptions:
     * <ul>
     * <li>ICatalogInfo.class
     * <li>List.class <IService>
     * </ul>
     * @see net.reurl.fractions.udig.catalog.IResolve#resolve(java.lang.Class, org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException{
        if(adaptee == null)
            return null;
        if(adaptee.isAssignableFrom(ICatalogInfo.class)){
            return adaptee.cast(getInfo(monitor));
        }
        if(adaptee.isAssignableFrom(List.class)){
            return adaptee.cast(members(monitor));
        }
        return null;
    }

    private ICatalogInfo info = null;
    /*
     * @see org.locationtech.udig.catalog.ICatalog#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    public ICatalogInfo getInfo(IProgressMonitor monitor){
        if(info != null){
            info = new GoogleICatalogInfo();
        }
        return info;
    }

    private class GoogleICatalogInfo extends ICatalogInfo{
        GoogleICatalogInfo(){
            this.title = Messages.GoogleCatalog_title; 
            this.description = Messages.GoogleCatalog_description; 
            this.source = url;
            this.keywords = new String[]{"Catalog","Google","Refractions Research","Search"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
    }
    
    /*
     * @see org.locationtech.udig.catalog.ICatalog#find(java.net.URL)
     */
    public List<IResolve> find( ID id, IProgressMonitor monitor ) {
        return new LinkedList<IResolve>();
    }

    /*
     * hits the server using soap ...
     * 
     * @see org.locationtech.udig.catalog.ICatalog#search(java.lang.String, org.locationtech.jts.geom.Envelope, org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<IResolve> search( String pattern, Envelope bbox, IProgressMonitor monitor ) throws IOException{
        
        monitor.beginTask(Messages.GoogleCatalog_searchMessage+pattern, IProgressMonitor.UNKNOWN); 
        
        List<IResolve> results = new ArrayList<IResolve>();
        
        if (bbox == null || bbox.isNull()) {
            bbox = new Envelope(-180, 180, -90, 90);
        }

        double xmin = bbox.getMinX();
        double xmax = bbox.getMaxX();
        
        double ymin = bbox.getMinY();
        double ymax = bbox.getMaxY();
        
        //keywords=bird&xmin=-180&ymin=-90&xmax=180&ymax=90
        String urlString = url.toExternalForm();
        
        urlString = urlString.concat("keywords="+URLEncoder.encode( pattern, "UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$
        urlString = urlString.concat("&xmin="+xmin+"&ymin="+ymin+"&xmax="+xmax+"&ymax="+ymax); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        
        if (monitor.isCanceled()) {
            return results;
        }
        
        URL finalURL = new URL(urlString);
        
        URLConnection connection = finalURL.openConnection();
        
        connection.addRequestProperty("Accept-Encoding", "gzip"); //$NON-NLS-1$ //$NON-NLS-2$

        InputStream inputStream = connection.getInputStream();
        
        if (connection.getContentEncoding() != null && connection.getContentEncoding().indexOf("gzip") != -1) { //$NON-NLS-1$
            inputStream = new GZIPInputStream(inputStream);
        }
                
        SAXBuilder builder = new SAXBuilder(false);
        Document document = null;
        
        if (monitor.isCanceled()) {
            return results;
        }
        
        try {
            document = builder.build(inputStream);
        } catch (JDOMException e) {
            throw (IOException) new IOException(Messages.GoogleCatalog_parseError).initCause(e); 
        }
        
        if (monitor.isCanceled()) {
            return results;
        }
        
        for (Object object : document.getRootElement().getChildren("r")) { //$NON-NLS-1$
            
            if (monitor.isCanceled()) {
                return results;
            }
            
            Element rElement = (Element) object;
            
            String name = rElement.getChildText("name"); //$NON-NLS-1$
            String title = rElement.getChildText("title"); //$NON-NLS-1$
            String description = rElement.getChildText("description"); //$NON-NLS-1$
            URL onlineResource = new URL(rElement.getChildText("onlineresource")); //$NON-NLS-1$
            String serverType = rElement.getChildText("servertype"); //$NON-NLS-1$
            String serverVersion = rElement.getChildText("serverversion"); //$NON-NLS-1$
            URL id = new URL(rElement.getChildText("id")); //$NON-NLS-1$
            
            OGCLayer layer = new OGCLayer(name, title, description, onlineResource, serverType, serverVersion, id);
            
            results.add(GoogleResource.getResource(layer));
        }
       
        return results;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee!=null && (adaptee.isAssignableFrom(ICatalogInfo.class) || adaptee.isAssignableFrom(List.class));
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<IResolve> members( IProgressMonitor monitor ) throws IOException{
        return search("",new Envelope(),monitor); //$NON-NLS-1$
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        return msg == null?Status.CONNECTED:Status.BROKEN;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return msg;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return url;
    }
    public ID getID() {
    	return new ID( getIdentifier() );		
    }
    
    void fire( IResolveChangeEvent event ) {
        Object[] listeners = catalogListeners.getListeners();        
        if( listeners.length == 0 ) return;
        
        for (int i = 0; i < listeners.length; ++i) {
            try {
                ((IResolveChangeListener) listeners[i]).changed( event );
            }
            catch( Throwable die) {
                CatalogPlugin.log( null, new Exception(die));
            }
        }
    }
    
    /**
     * 
     * @see org.locationtech.udig.catalog.ICatalog#addCatalogListener(org.locationtech.udig.catalog.ICatalog.ICatalogListener)
     * @param listener
     */
    public void addCatalogListener( IResolveChangeListener listener ) {
        catalogListeners.add(listener);
    }

    /**
     * 
     * @see org.locationtech.udig.catalog.ICatalog#removeCatalogListener(org.locationtech.udig.catalog.ICatalog.ICatalogListener)
     * @param listener
     */
    public void removeCatalogListener( IResolveChangeListener listener ) {
        catalogListeners.remove(listener);
    }
	@Override
	public <T extends IResolve> T getById(Class<T> type, ID id, IProgressMonitor monitor) {
		return null;
	}
        
    @Override
    public void dispose( IProgressMonitor monitor ) {
        // do nothing
        catalogListeners.clear();
    }

	public String getTitle() {
		return info != null ? info.getTitle() : null;
	}
}
