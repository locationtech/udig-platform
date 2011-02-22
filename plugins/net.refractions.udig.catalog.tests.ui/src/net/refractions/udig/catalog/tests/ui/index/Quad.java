package net.refractions.udig.catalog.tests.ui.index;

import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.shapefile.indexed.ShapeFileIndexer;

public class Quad extends RTree implements IOp {

    @Override
    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        index(target, ShapeFileIndexer.QUADTREE);
    }

}
