package net.refractions.udig.catalog.tests.ui.index;

import java.io.File;
import java.net.URL;

import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.shapefile.Lock;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileRendererUtil;
import org.geotools.data.shapefile.indexed.ShapeFileIndexer;

public class RTree implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        index(target, ShapeFileIndexer.RTREE);
    }

    /**
     * @param target
     * @param indexType
     */
    protected void index( Object target, String indexType ) {
        URL shpURL = ShapefileRendererUtil.getshpURL((ShapefileDataStore) target);
        deleteIndexFiles(shpURL);
        ShapeFileIndexer indexer = new ShapeFileIndexer();
        indexer.setIdxType(indexType);
        indexer.setShapeFileName(shpURL.getFile());
        try {
            indexer.index(true, new Lock());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void deleteIndexFiles( URL shpURL ) {
        String url = shpURL.getFile();
        url = url.substring(0, url.lastIndexOf('.'));
        File qix = new File(url + ".qix"); //$NON-NLS-1$
        if (qix.exists())
            qix.delete();
        File qrx = new File(url + ".qrx"); //$NON-NLS-1$
        if (qrx.exists())
            qrx.delete();
    }

}
