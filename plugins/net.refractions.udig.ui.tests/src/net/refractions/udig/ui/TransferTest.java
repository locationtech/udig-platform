package net.refractions.udig.ui;

import junit.framework.TestCase;
import net.refractions.udig.internal.ui.UDigByteAndLocalTransfer;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class TransferTest extends TestCase {
    public void testFeatureTransfering() throws Exception {
        Display display=Display.getCurrent();
        Clipboard cp=new Clipboard(display);
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("test", 1); //$NON-NLS-1$
        cp.setContents(features,new Transfer[]{UDigByteAndLocalTransfer.getInstance()});
        assertSame(features[0], cp.getContents(UDigByteAndLocalTransfer.getInstance()));

//        cp.setContents(features,new Transfer[]{FeatureTextTransfer.getInstance()});
//        SimpleFeature contents = (SimpleFeature) cp.getContents(FeatureTextTransfer.getInstance());
//        Geometry defaultGeometry = features[0].getDefaultGeometry();
//        assertTrue(defaultGeometry.equalsExact(contents.getDefaultGeometry()));
    }
    public void testGeometryTransfering() throws Exception {
        Display display=Display.getCurrent();
        Clipboard cp=new Clipboard(display);
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("test", 1); //$NON-NLS-1$
        cp.setContents(new Object[]{features[0].getDefaultGeometry()},new Transfer[]{UDigByteAndLocalTransfer.getInstance()});
        assertSame(features[0].getDefaultGeometry(), cp.getContents(UDigByteAndLocalTransfer.getInstance()));

//        cp.setContents(new Object[]{features[0].getDefaultGeometry()},new Transfer[]{GeometryTextTransfer.getInstance()});
//        Geometry contents = (Geometry) cp.getContents(GeometryTextTransfer.getInstance());
//        Geometry defaultGeometry = features[0].getDefaultGeometry();
//        assertTrue(defaultGeometry.equalsExact(contents));
    }

    public void testSelectionTransfering() throws Exception{
        Display display=Display.getCurrent();
        Clipboard cp=new Clipboard(display);
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("test", 1); //$NON-NLS-1$
        FilterFactory factory= CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        SimpleFeatureType featureType = features[0].getFeatureType();
        BoundingBox bounds = features[0].getBounds();
        CoordinateReferenceSystem crs= featureType.getCoordinateReferenceSystem();
		BBOX filter = factory.bbox(featureType.getGeometryDescriptor().getName().getLocalPart(), bounds.getMinX(),
        		bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), CRS.lookupIdentifier(crs, false));
        cp.setContents(new Object[]{filter},new Transfer[]{UDigByteAndLocalTransfer.getInstance()});
        assertSame(filter, cp.getContents(UDigByteAndLocalTransfer.getInstance()));

//        cp.setContents(new Object[]{filter},new Transfer[]{FilterTextTransfer.getInstance()});
//        Filter contents = (Filter) cp.getContents(FilterTextTransfer.getInstance());
//        // There is some sort of bug in the filter parser or the Filter.equals that make the two not be equal
//        // so this is a work around because my code is correct.
//        assertTrue(filter.contains(features[0]));
//        assertTrue(contents.contains(features[0]));
    }
}
