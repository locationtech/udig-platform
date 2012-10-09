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
package net.refractions.udig.issues.test;

import static org.junit.Assert.assertEquals;
import net.refractions.udig.issues.internal.datastore.FeatureTypeAttributeMapper;

import org.geotools.data.DataUtilities;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

public class FeatureTypeAttributeMapperTest {

    @Test
    public void testStraightMappingWithExtraAtts() throws Exception {
        SimpleFeatureType featureType = DataUtilities.createType("IssuesFeatureType", //$NON-NLS-1$
                "bounds:MultiPolygon," + //$NON-NLS-1$ 
                        "extension:int," + //$NON-NLS-1$
                        "ext:String," + //$NON-NLS-1$
                        "id:Integer," + //$NON-NLS-1$
                        "id2:String," + //$NON-NLS-1$
                        "group:String," + //$NON-NLS-1$
                        "resolution:String," + //$NON-NLS-1$
                        "priority:String," + //$NON-NLS-1$
                        "desc:String," + //$NON-NLS-1$
                        "mem:String," + //$NON-NLS-1$
                        "view:String," + //$NON-NLS-1$
                        "extra1:String"); //$NON-NLS-1$
        FeatureTypeAttributeMapper mapper=new FeatureTypeAttributeMapper(featureType);
        assertEquals("bounds", mapper.getBounds()); //$NON-NLS-1$
        assertEquals("id2", mapper.getId()); //$NON-NLS-1$
        assertEquals("ext", mapper.getExtensionId()); //$NON-NLS-1$
        assertEquals("group", mapper.getGroupId()); //$NON-NLS-1$
        assertEquals("resolution", mapper.getResolution()); //$NON-NLS-1$
        assertEquals("priority", mapper.getPriority()); //$NON-NLS-1$
        assertEquals("desc", mapper.getDescription()); //$NON-NLS-1$
        assertEquals("mem", mapper.getMemento()); //$NON-NLS-1$
        assertEquals("view", mapper.getViewMemento()); //$NON-NLS-1$
        
    }

    @Test
    public void testDifficultMapping() throws Exception {
        SimpleFeatureType featureType = DataUtilities.createType("IssuesFeatureType", //$NON-NLS-1$
                "b:MultiPolygon," + //$NON-NLS-1$ 
                        "extension:int," + //$NON-NLS-1$
                        "ee:String," + //$NON-NLS-1$
                        "id:Integer," + //$NON-NLS-1$
                        "i:String," + //$NON-NLS-1$
                        "g:String," + //$NON-NLS-1$
                        "r:String," + //$NON-NLS-1$
                        "p:String," + //$NON-NLS-1$
                        "dc:String," + //$NON-NLS-1$
                        "m:String," + //$NON-NLS-1$
                        "v:String," + //$NON-NLS-1$
                        "e2:String"); //$NON-NLS-1$
        FeatureTypeAttributeMapper mapper=new FeatureTypeAttributeMapper(featureType);
        assertEquals("b", mapper.getBounds()); //$NON-NLS-1$
        assertEquals("ee", mapper.getExtensionId()); //$NON-NLS-1$
        assertEquals("i", mapper.getId()); //$NON-NLS-1$
        assertEquals("g", mapper.getGroupId()); //$NON-NLS-1$
        assertEquals("r", mapper.getResolution()); //$NON-NLS-1$
        assertEquals("p", mapper.getPriority()); //$NON-NLS-1$
        assertEquals("dc", mapper.getDescription()); //$NON-NLS-1$
        assertEquals("v", mapper.getMemento()); //$NON-NLS-1$
        assertEquals("m", mapper.getViewMemento()); //$NON-NLS-1$
    }

}
