/**
 *
 */
package net.refractions.udig.catalog.internal.gml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Map;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.ServiceMover;
import net.refractions.udig.catalog.URLUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStoreFactory;

/**
 * Mover to move the GML files and copy the xsd.
 *
 * @author Jesse
 */
public class GMLServiceMover implements IResolveAdapterFactory, ServiceMover {

    private GMLServiceImpl resolve;

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.catalog.IResolveAdapterFactory#adapt(net.refractions.udig.catalog.IResolve,
     *      java.lang.Class, org.eclipse.core.runtime.IProgressMonitor)
     */
    public Object adapt( IResolve resolve, Class< ? extends Object> adapter,
            IProgressMonitor monitor ) throws IOException {
        if (adapter.isAssignableFrom(getClass())) {
            GMLServiceMover serviceMover = new GMLServiceMover();
            serviceMover.resolve = (GMLServiceImpl) resolve;
            return serviceMover;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.catalog.IResolveAdapterFactory#canAdapt(net.refractions.udig.catalog.IResolve,
     *      java.lang.Class)
     */
    public boolean canAdapt( IResolve resolve, Class< ? extends Object> adapter ) {
        if (adapter.isAssignableFrom(getClass())) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.catalog.ServiceMover#move(java.io.File)
     */
    public String move( File destinationFolder ) {
        /*
         * gml are moved into a folder that has to exist
         */
        if (destinationFolder.isDirectory() && destinationFolder.exists()) {
            Map<String, Serializable> parametersMap = resolve.getConnectionParams();
            URL url = (URL) parametersMap.get(IndexedShapefileDataStoreFactory.URLP.key);

            File file = URLUtils.urlToFile(url);

            if( !moveFile(destinationFolder, file) ){
                return "The file can not be moved.  Check if the file is locked";
            }

            copySchemas(file, destinationFolder);

            return null;
        }

        return "The destination is not a valid directory";
    }

    private void copySchemas( File file, File destinationFolder ) {
        String completeGMLFilePath = file.getAbsolutePath();
        int dotPosition = completeGMLFilePath.lastIndexOf("."); //$NON-NLS-1$
        String completeGMLFileBasePath = completeGMLFilePath.substring(0, dotPosition);

        File gmlFileDirectory = new File(completeGMLFileBasePath);
        File[] xsdFiles = gmlFileDirectory.listFiles(new FilenameFilter(){

            public boolean accept( File dir, String name ) {
                return name.endsWith(".xsd"); //$NON-NLS-1$
            }

        });

        for( File file2 : xsdFiles ) {
            try {
                copyFile(file2, new File(destinationFolder, file2.getName()));
            } catch (Exception e) {

            }
        }
    }
    public void copyFile(File in, File out) throws Exception {
        FileChannel sourceChannel = new
             FileInputStream(in).getChannel();
        FileChannel destinationChannel = new
             FileOutputStream(out).getChannel();
        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        sourceChannel.close();
        destinationChannel.close();
    }

    /**
     * @param destinationFolder
     * @param file
     */
    private boolean moveFile( File destinationFolder, File file ) {
        if (file.exists()) {
            // Move file to new directory
            boolean success = file.renameTo(new File(destinationFolder, file.getName()));
            if (!success) {
                return false;
            }
            return true;
        }
        return false;
    }

}
