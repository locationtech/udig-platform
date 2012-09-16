package net.refractions.udig.ui;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.core.internal.FeatureUtils;

import org.eclipse.swt.SWT;
import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("nls")
public class SelectionComparatorTest {

    private SimpleFeatureType type;
    private ArrayList<SimpleFeature> features;
    private SimpleFeature feature1;
    private SimpleFeature feature2;
    private SimpleFeature feature3;
    private SimpleFeature feature4;
    private FilterFactory ff;

    @Before
    public void setUp() throws Exception {
        GeometryFactory fac = new GeometryFactory();
        type = DataUtilities.createType("type", "geom:Point,name:String,id:int");

        features = new ArrayList<SimpleFeature>(2);

        feature1 = SimpleFeatureBuilder.build(type, new Object[]{fac.createPoint(new Coordinate(10, 10)), "name1", 1}, "ID1");
        feature2 = SimpleFeatureBuilder.build(type, new Object[]{fac.createPoint(new Coordinate(10, 10)), "name2", 2}, "ID2");
        feature3 = SimpleFeatureBuilder.build(type, new Object[]{fac.createPoint(new Coordinate(10, 10)), "name3", 3}, "ID3");
        feature4 = SimpleFeatureBuilder.build(type, new Object[]{fac.createPoint(new Coordinate(10, 10)), "name4", 4}, "ID4");

        features.add(feature1);
        features.add(feature2);
        features.add(feature3);
        features.add(feature4);
        
        ff = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
    }

    @Test
    public void testCompare() throws Exception{
        
        Filter fidFilter=ff.id(FeatureUtils.stringToId(ff, "ID3")); //$NON-NLS-1$
        Collections.sort(features, new SelectionComparator(fidFilter, SWT.UP));
        
        assertEquals( feature3, features.get(0));
        assertEquals( feature1, features.get(1));
        assertEquals( feature2, features.get(2));
        assertEquals( feature4, features.get(3));

        Collections.sort(features, new SelectionComparator(fidFilter, SWT.DOWN));
        
        assertEquals( feature1, features.get(0));
        assertEquals( feature2, features.get(1));
        assertEquals( feature4, features.get(2));
        assertEquals( feature3, features.get(3));
    }
    
    @Test
    public void testCompareWithSubComparator() throws Exception{
        Set<Identifier> fids = new HashSet<Identifier>();
        fids.add(ff.featureId("ID3"));
        Id fidFilter=ff.id(fids);
        Collections.sort(features, new SelectionComparator(fidFilter, SWT.UP, new FIDComparator(SWT.UP)));
        
        assertEquals( feature3, features.get(0));
        assertEquals( feature4, features.get(1));
        assertEquals( feature2, features.get(2));
        assertEquals( feature1, features.get(3));

        fids.add(ff.featureId("ID1")); //$NON-NLS-1$
        
        fidFilter = ff.id(fids);
        Collections.sort(features, new SelectionComparator(fidFilter, SWT.UP, new FIDComparator(SWT.UP)));
        
        assertEquals( feature3, features.get(0));
        assertEquals( feature1, features.get(1));
        assertEquals( feature4, features.get(2));
        assertEquals( feature2, features.get(3));
    }

}
