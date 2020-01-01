/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.location;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.XmlRpcException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

/**
 *
 * @author James
 */
public class AddressSeeker {
    
    // protected String host = "http://geocoder.us/service/xmlrpc";
    protected String username = ""; //$NON-NLS-1$
    protected String password = ""; //$NON-NLS-1$
    
    /** Creates a new instance of AddressSeeker */
    public AddressSeeker() {
        
    }
    
    public void setPassword(String pwd){
        password = pwd;
    }
    
    static private SimpleFeatureType createAddressType( List<String> keys ) {
    	SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    	builder.setName("Address");
        for( String key : keys ){
            if( "long".equals(key) || "lat".equals(key)) continue;             //$NON-NLS-1$ //$NON-NLS-2$
            builder.add(key, String.class);
        }
        String geometryAtt = "location";
		builder.add(geometryAtt, Point.class, DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        builder.setDefaultGeometry(geometryAtt);
        
        try {
            return builder.buildFeatureType(); //$NON-NLS-1$
        } catch (Throwable e) {
            return null;
        }
    }
    
    public void setUsername(String uname){
        username = uname;
    }
    
    private List<String> keys( Vector<Hashtable<String,Object>> vector ){
        List<String> keys = new ArrayList<String>();
        for( Hashtable<String,Object> record : vector )
            for( String key : record.keySet() )
                if( !keys.contains( key ) ) keys.add( key );
        return keys;
    }
    
    private XmlRpcClient getGeoCoderClient(String username, String password) throws MalformedURLException {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        XmlRpcClient geocoder = new XmlRpcClient();
        
        if(username != null && password != null){
            config.setServerURL(new URL("http://"+username+":"+password+"@geocoder.us/service/xmlrpc")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }else{
            config.setServerURL(new URL("http://geocoder.us/service/xmlrpc")); //$NON-NLS-1$
        }
        geocoder.setConfig(config);        
        
        return geocoder;
    }
    
    /** Returns a List<SimpleFeature> of ADDRESS */
    public Point where(String address) throws IOException,XmlRpcException {
        GeometryFactory fac = new GeometryFactory();        
        XmlRpcClient geocoder = getGeoCoderClient(username, password);
        
        Vector params = new Vector();
        params.addElement(address);
        // this method returns a string
        Vector vec = (Vector)geocoder.execute("geocode", params); //$NON-NLS-1$
        System.out.println("vec"+vec); //$NON-NLS-1$
        
        Hashtable table = (Hashtable)vec.get(0);
        double lat = ((Number)table.get("lat")).doubleValue(); //$NON-NLS-1$
        double lon = ((Number)table.get("long")).doubleValue(); //$NON-NLS-1$
        Coordinate c = new Coordinate(lon, lat);
        Point p = fac.createPoint(c);
        return p;
    }
    
    public List<SimpleFeature> geocode(String address) throws IOException,XmlRpcException {
        GeometryFactory fac = new GeometryFactory();      
        XmlRpcClient geocoder = getGeoCoderClient(username, password);
        
        Vector params = new Vector();
        params.addElement(address);
        
        // this method returns a string
        Vector<Hashtable<String,Object>> vec = (Vector<Hashtable<String,Object>>)geocoder.execute("geocode", params); //$NON-NLS-1$
        System.out.println("vec"+vec); //$NON-NLS-1$

        List<String> keys = keys( vec );
        SimpleFeatureType ADDRESS = createAddressType( keys );
        
        List<SimpleFeature> places = new ArrayList<SimpleFeature>( vec.size() );

        int count=1;
        for( Hashtable table : vec ){
            double lat = Double.NaN, lon = Double.NaN;
            Object values[] = new Object[keys.size()-1];
            int index = 0;
            for( String key : keys ){
                if( !table.containsKey( key )){
                    System.out.println("missed key - "+key ); //$NON-NLS-1$
                    continue;
                }
                else {
                        if( "lat".equals( key ) ){ //$NON-NLS-1$
                            lat = ((Number)table.get("lat")).doubleValue(); //$NON-NLS-1$
                            continue;
                        }
                        else if( "long".equals( key ) ){ //$NON-NLS-1$
                            lon = ((Number)table.get("long")).doubleValue(); //$NON-NLS-1$
                            continue;
                        }                
                        values[index] = table.get( key );
                }
                index++;
            }
            if( Double.isNaN( lat ) || Double.isNaN( lon )){
                System.out.println("missed location - " );                 //$NON-NLS-1$
            }
            else {
                values[index] = fac.createPoint( new Coordinate(lon, lat) );
            }
            try{
                SimpleFeature f = SimpleFeatureBuilder.build(ADDRESS, values, fid( count++, table ) );
                places.add( f );
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return places;
    }
    private void append( StringBuffer fid, Hashtable<String,Object> table, String key ){
        if( table.containsKey( key ) && table.get(key ) != null ){
            fid.append( " " );                     //$NON-NLS-1$
            fid.append( table.get( key ) );                    
        }
    }
    String fid( int count, Hashtable<String,Object> table ){
        StringBuffer fid = new StringBuffer();
        fid.append( count );                        
        fid.append( "." );                 //$NON-NLS-1$
        
        append( fid, table, "number"); //$NON-NLS-1$
        append( fid, table, "street"); //$NON-NLS-1$
        append( fid, table, "type");             //$NON-NLS-1$
        if( table.containsKey("city")){ //$NON-NLS-1$
            fid.append( "," );                     //$NON-NLS-1$
            fid.append( table.get("city") ); //$NON-NLS-1$
        }
        append( fid, table, "state" ); //$NON-NLS-1$
        return fid.toString();
    }
}
