/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.location.test;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import net.refractions.udig.location.AddressSeeker;
import net.refractions.udig.location.USGLocation;

import org.apache.xmlrpc.XmlRpcException;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Jody Garnett
 * @since 1.0.0
 */
public class USGCatalogTest extends TestCase {
    USGLocation usg;
    AddressSeeker seeker;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        usg = new USGLocation();        
        seeker = new AddressSeeker();
    }
    public void testWhere() {
        Point location = null;
        try {
            location = seeker.where("1500 Poydras St, New Orleans, LA");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        } catch (XmlRpcException e) {
            e.printStackTrace();
            fail("XmlRpcException");
        }
        assertNotNull( location );
        assertEquals( -90.078377, location.getX() );
        assertEquals( 29.951663, location.getY() );
    }
    public void testGeocode() {
        List<SimpleFeature> features = null;
        try {
            features = seeker.geocode("1500 Poydras St, New Orleans, LA");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        } catch (XmlRpcException e) {
            e.printStackTrace();
            fail("XmlRpcException");
        }
        assertNotNull( features );
        assertEquals( 1, features.size() );
        SimpleFeature feature = features.get(0);
        Point location = ((Geometry)feature.getDefaultGeometry()).getCentroid();
        assertEquals( -90.078377, location.getX() );
        assertEquals( 29.951663, location.getY() );
    }
    
    private void printFeats(List<SimpleFeature> list) {
        System.out.println(list);
        for(SimpleFeature feat : list) {
            System.out.println(feat);
        }
    }
}
