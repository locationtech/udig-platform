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
package org.locationtech.udig.tools.edit.support;

import java.awt.Dimension;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.gml.GMLFilterDocument;
import org.geotools.gml.GMLFilterFeature;
import org.geotools.gml.GMLFilterGeometry;
import org.geotools.gml.GMLReceiver;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.udig.project.internal.render.impl.ScaleUtils;
import org.locationtech.udig.tool.edit.tests.TestsPlugin;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TimingTests {
    
    private static final MathTransform layerToWorld;
    static {
        try {
            layerToWorld=CRS.findMathTransform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);
        } catch (FactoryException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }    
    }

    @Test(timeout = 5000)
    public void testPointCreation(){
        Random r=new Random(298719283471298L);

      for( int j=0; j<1000; j++){
          r=new Random(298719283471298L);
          for( int i=0; i<100000;i++)
              Point.valueOf(r.nextInt(1000),r.nextInt(1000));
      }
    }
    
    @Test
    public void testPrimitiveShapeIterator() throws Exception {
        URL url = TestsPlugin.getDefault().getBundle().getResource("data/lake.gml"); //$NON-NLS-1$
        InputStream in = url.openConnection().getInputStream();
        
        InputStreamReader filereader=new InputStreamReader(in);
        
        InputSource input = new InputSource(filereader);
        DefaultFeatureCollection collection = new DefaultFeatureCollection();
        GMLReceiver receiver=new GMLReceiver(collection);
        GMLFilterFeature filterFeature = new GMLFilterFeature(receiver);
        GMLFilterGeometry filterGeometry = new GMLFilterGeometry(filterFeature);
        GMLFilterDocument filterDocument = new GMLFilterDocument(filterGeometry);
        try {
            // parse xml
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(filterDocument);
            reader.parse(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SimpleFeature feature=collection.features().next();
        ReferencedEnvelope bounds = new ReferencedEnvelope( feature.getBounds() );
        bounds=new ReferencedEnvelope( bounds.getMinX()-(bounds.getWidth()/8),
                bounds.getMaxX()+(bounds.getWidth()/8),
                bounds.getMinY()-(bounds.getHeight()/4),
                bounds.getMaxY()+(bounds.getHeight()/4), bounds.getCoordinateReferenceSystem() );
        EditBlackboard map=new EditBlackboard(500, 500, ScaleUtils.worldToScreenTransform(bounds, new Dimension(1000,1000)), layerToWorld);
        
       PrimitiveShape shell = map.setGeometries((Geometry)feature.getDefaultGeometry(), null).values().iterator().next().getShell();
       System.out.println( shell.getNumPoints());
       System.out.println( shell.getNumCoords());

//        long start=System.currentTimeMillis();
//        
//        for( int i=0; i<1000; i++){
//            for( Point point : shell ){
//                for( int j=0; j<1000; j++ ){
//                    point.getX();  point.getY();
//                }
//            }
//        }
//        
//        long end=System.currentTimeMillis();
//        
//        System.out.println("Time to iterate through 10000 points 1000 times is: " + (end-start) +" msec"); //$NON-NLS-1$ //$NON-NLS-2$
//        
    }
}
