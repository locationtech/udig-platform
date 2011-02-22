package net.refractions.udig.ui;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.geotools.data.DataUtilities;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.filter.FidFilter;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import junit.framework.TestCase;

public class SelectionComparatorTest extends TestCase {

    private FeatureType type;
    private ArrayList<Feature> features;
    private Feature feature1;
    private Feature feature2;
    private Feature feature3;
    private Feature feature4;
    private FilterFactory ff;

    protected void setUp() throws Exception {
        super.setUp();
        GeometryFactory fac = new GeometryFactory();
        type = DataUtilities.createType("type", "geom:Point,name:String,id:int"); //$NON-NLS-1$ //$NON-NLS-2$

        features = new ArrayList<Feature>(2);

        feature1 = type.create(new Object[]{fac.createPoint(new Coordinate(10, 10)), "name1", //$NON-NLS-1$
                1}, "ID1"); //$NON-NLS-1$
        feature2 = type.create(new Object[]{fac.createPoint(new Coordinate(10, 10)), "name2", //$NON-NLS-1$
                2}, "ID2"); //$NON-NLS-1$
        feature3 = type.create(new Object[]{fac.createPoint(new Coordinate(10, 10)), "name3", //$NON-NLS-1$
                3}, "ID3"); //$NON-NLS-1$
        feature4 = type.create(new Object[]{fac.createPoint(new Coordinate(10, 10)), "name4", //$NON-NLS-1$
                4}, "ID4"); //$NON-NLS-1$

        features.add(feature1);
        features.add(feature2);
        features.add(feature3);
        features.add(feature4);

        ff = FilterFactoryFinder.createFilterFactory();
    }

    public void testCompare() throws Exception{


        Filter fidFilter=ff.createFidFilter("ID3"); //$NON-NLS-1$
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

    public void testCompareWithSubComparator() throws Exception{

        FidFilter fidFilter=ff.createFidFilter("ID3"); //$NON-NLS-1$
        Collections.sort(features, new SelectionComparator(fidFilter, SWT.UP, new FIDComparator(SWT.UP)));

        assertEquals( feature3, features.get(0));
        assertEquals( feature4, features.get(1));
        assertEquals( feature2, features.get(2));
        assertEquals( feature1, features.get(3));

        fidFilter.addFid("ID1"); //$NON-NLS-1$
        Collections.sort(features, new SelectionComparator(fidFilter, SWT.UP, new FIDComparator(SWT.UP)));

        assertEquals( feature3, features.get(0));
        assertEquals( feature1, features.get(1));
        assertEquals( feature4, features.get(2));
        assertEquals( feature2, features.get(3));
    }

}
