package net.refractions.udig.project.internal.commands.edit;

import java.awt.Dimension;
import java.util.Iterator;

import junit.framework.TestCase;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;

public class AddFeatureCommandTest extends TestCase {

    /*
     * Test method for 'net.refractions.udig.project.internal.commands.edit.AddFeatureCommand.run(IProgressMonitor)'
     */
    public void testRun() throws Exception {
        Map map = MapTests.createDefaultMap("test", 2, true, new Dimension(10,10)); //$NON-NLS-1$
        Layer layer = map.getLayersInternal().get(0);
        Feature feature = layer.getSchema().create(new Object[]{null,null});

        AddFeatureCommand command=new AddFeatureCommand(feature, layer);

        command.setMap(map);
        command.run(new NullProgressMonitor());
        FeatureSource source = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        assertEquals(3, source.getCount(Query.ALL));

        command.rollback(new NullProgressMonitor());
        FeatureCollection collection = source.getFeatures();
        int i=0;
        for( Iterator iter = collection.iterator(); iter.hasNext(); ) {
            iter.next();
            i++;
        }

        assertEquals(2, i);

    }

}
