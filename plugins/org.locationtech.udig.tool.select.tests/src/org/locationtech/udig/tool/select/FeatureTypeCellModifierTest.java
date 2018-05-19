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
package org.locationtech.udig.tool.select;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.tests.CatalogTests;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class FeatureTypeCellModifierTest extends AbstractProjectUITestCase {

    private static final String NAME1 = "name1"; //$NON-NLS-1$
    private static final String DISTANCE = "distance"; //$NON-NLS-1$
    private static final String NAME = "name"; //$NON-NLS-1$
    private static final String ID = "id"; //$NON-NLS-1$
    private static final String GEOM = "geom";
    private SimpleFeatureType featureType;
    private Map map;
    private IGeoResource resource;
    private FeatureTypeCellModifier modifier;
    private List<SimpleFeature> features;
    
    @Before
    public void setUp() throws Exception {
        featureType=DataUtilities.createType("testType", "*"+GEOM+":Polygon,"+NAME+":String,"+ID+":Integer,"+DISTANCE+":Double");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        features=new ArrayList<SimpleFeature>(2);
        
        SimpleFeature emptyFeature = SimpleFeatureBuilder.build( featureType, new Object[]{null, null, null, null}, null );
        SimpleFeature feature = SimpleFeatureBuilder.build( featureType,new Object[]{null, NAME1, 5, 5.5},null);
        
        features.add( emptyFeature );
        features.add( feature );
        resource = CatalogTests.createGeoResource(features.toArray(new SimpleFeature[0]), true);
        map = MapTests.createNonDynamicMapAndRenderer(resource, null);
        
        modifier=new FeatureTypeCellModifier(map.getMapLayers().get(0));
    }

    @Test
    public void testCanModify() {
        assertTrue( modifier.canModify(features.get(0), GEOM));
        assertTrue( modifier.canModify(features.get(0), NAME));
        assertTrue( modifier.canModify(features.get(0), ID));
        assertTrue( modifier.canModify(features.get(0), DISTANCE));
        
        assertFalse( modifier.canModify(features.get(0), "SOMETHING_ELSE")); //$NON-NLS-1$
        
    }

    @Test
    public void testGetValue() {
        assertNull( modifier.getValue(features.get(0), NAME));
        assertNull( modifier.getValue(features.get(0), ID));
        assertNull( modifier.getValue(features.get(0), DISTANCE));
        
        assertEquals( NAME1, modifier.getValue(features.get(1), NAME));
        assertEquals( 5, modifier.getValue(features.get(1), ID));
        assertEquals( 5.5, modifier.getValue(features.get(1), DISTANCE));
    }

    @Test
    public void testModify() throws Exception {
        Shell shell=new Shell(Display.getCurrent());
        Tree tree=new Tree(shell, SWT.DEFAULT);
        TreeItem treeItem=new TreeItem(tree, SWT.DEFAULT);
        
        try{
            treeItem.setData(features.get(0));
            runModifyAttribute(treeItem, "newName", NAME); //$NON-NLS-1$
            runModifyAttribute(treeItem, 22, ID);
            runModifyAttribute(treeItem, 22.2, DISTANCE);
        }finally{
            treeItem.dispose();
            tree.dispose();
            shell.dispose();
        }
    }

    private void runModifyAttribute( TreeItem treeItem, final Object newValue, final String attributeToTest ) throws Exception {
        modifier.modify(treeItem, attributeToTest, newValue );
        UDIGTestUtil.inDisplayThreadWait(1000, new WaitCondition(){

            public boolean isTrue() {
                return newValue.equals( features.get(0).getAttribute(attributeToTest) );
            }
            
        }, false);
        assertEquals( newValue, features.get(0).getAttribute(attributeToTest));
    }

}
