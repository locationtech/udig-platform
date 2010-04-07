package net.refractions.udig.tool.select;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.tests.CatalogTests;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
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
    
    protected void setUp() throws Exception {
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

    public void testCanModify() {
        assertTrue( modifier.canModify(features.get(0), GEOM));
        assertTrue( modifier.canModify(features.get(0), NAME));
        assertTrue( modifier.canModify(features.get(0), ID));
        assertTrue( modifier.canModify(features.get(0), DISTANCE));
        
        assertFalse( modifier.canModify(features.get(0), "SOMETHING_ELSE")); //$NON-NLS-1$
        
    }

    public void testGetValue() {
        assertNull( modifier.getValue(features.get(0), NAME));
        assertNull( modifier.getValue(features.get(0), ID));
        assertNull( modifier.getValue(features.get(0), DISTANCE));
        
        assertEquals( NAME1, modifier.getValue(features.get(1), NAME));
        assertEquals( 5, modifier.getValue(features.get(1), ID));
        assertEquals( 5.5, modifier.getValue(features.get(1), DISTANCE));
    }

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
