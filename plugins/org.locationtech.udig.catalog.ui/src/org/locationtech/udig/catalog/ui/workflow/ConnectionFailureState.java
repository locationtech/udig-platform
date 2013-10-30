/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.workflow;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.ServiceExtension;
import org.locationtech.udig.catalog.ServiceExtension2;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.core.Pair;
import org.locationtech.udig.core.internal.ExtensionPointList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A State that will occur if no services were able to be created from a param map or a URL.  This state is designed
 * to explain "why" the failure occurred.
 *  
 * @author Jesse
 * @since 1.1.0
 */
public class ConnectionFailureState extends State {

    private Map<String, Serializable> params;
    private List<URL> urls;
    private Map<String, List<Data>> reports=new HashMap<String,List<Data>>();

    public ConnectionFailureState( List<URL> urls, Map<String, Serializable> params ) {
        if( params == null ){
            this.urls=urls;
            if( urls!=null && urls.isEmpty() )
                this.urls=null;
        } else
            this.params=params;
    }

    @Override
    public String getName() {
        return Messages.ConnectionFailureState_name;
    }

    public Map<String, Serializable> getParams() {
        return params;
    }

    public List<URL> getUrls() {
        return urls;
    }

    public Map<String, List<Data>> getReports() {
        return reports;
    }

    @Override
    public void init( IProgressMonitor monitor ) throws IOException {
        
        if( params==null && urls==null ){
            reports.put( "msg", Collections.singletonList(new Data("msg", "No Connection Information", "For some reason the previous wizard page did not"))); //$NON-NLS-1$ //$NON-NLS-2$
            return ;
        }
        
        List<IConfigurationElement> list = ExtensionPointList.getExtensionPointList(ServiceExtension.EXTENSION_ID);
        for( IConfigurationElement element : list ) {
            List<Data> data=new ArrayList<Data>();
            
            String id = element.getNamespaceIdentifier().toString()+"."+element.getAttribute("id"); //$NON-NLS-1$ //$NON-NLS-2$
            String name = element.getAttribute("name"); //$NON-NLS-1$
            if( name==null ){
                name=element.getAttribute("id"); //$NON-NLS-1$
            }

            
            try{
                ServiceExtension extension = (ServiceExtension) element.createExecutableExtension("class"); //$NON-NLS-1$
                if( extension instanceof ServiceExtension2 ){
                    ServiceExtension2 e2=(ServiceExtension2) extension;
                    if( params!=null ){
                        data.add(new Data( id, name, e2.reasonForFailure(params)) );
                    }else{
                        for( URL url : urls ) {
                            data.add(new Data( id, name, e2.reasonForFailure(url), url) );
                        }
                    }
                }
                if( data.isEmpty())
                    data.add(new Data(id, name, "Implementation does not provide any debug information"));
                
            }catch(Throwable e){
                data.add(new Data(id, name, e.getLocalizedMessage()));
            }
            reports.put(id, data);
        }
    }
    @Override
    public Pair<Boolean, State> dryRun() {
        return new Pair<Boolean, State>(false, null);
    }

    @Override
    public boolean run( IProgressMonitor monitor ) throws IOException {
        return false;
    }

    class Data{

        String message;
        String name;
        URL url;
        String id;

        public Data( String id, String name, String message ) {
            if( id==null )
                throw new NullPointerException();
            if( name==null )
                throw new NullPointerException();
            this.name=name;
            this.message=message;
            this.id=id;
        }

        public Data( String id, String name2, String string, URL url ) {
            this( id, name2, string );
            if( url==null )
                throw new NullPointerException();
            this.url=url;
        }
        
    }
}
