package net.refractions.udig.catalog.rasterings;

import java.io.File;
import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.gce.imagemosaic.ImageMosaicFormat;
import org.geotools.gce.imagemosaic.ImageMosaicReader;

/**
 * This class teaches ShpGeoResource a new trick - how to be a GridCoverage.
 * <p>
 * Specifically this class will use ImageMoasicFormat and check if the provided
 * resource can be understood as an ImageMoasic.
 * <pre><code>
 * if( handle.canAdapate( AbstractGridCoverage2DReader.class ){
 *     AbstractGridCoverage2DReader reader = handle.adapate( AbstractGridCoverage2DReader.class, monitor );
 *     ....
 * }
 * </code></pre>
 * 
 * @author Jody Garnett
 */
public class ShpImageMoasicAdaptorFactory implements IResolveAdapterFactory {
	/**
	 * This is the format we are supporting, used as a factory object
	 * to create 
	 */
	static ImageMosaicFormat format = new ImageMosaicFormat();
	
	public Object adapt(IResolve resolve, Class<? extends Object> adapter,
			IProgressMonitor monitor) throws IOException {
		
		if( adapter.isAssignableFrom(ImageMosaicReader.class)){
			return toGridCoverage2DReader( resolve, monitor);
		}		
		return null;
	}

	/**
	 * Check to see if the provided handle can adapte to an AbstractGridCoverage2DReader .
	 * <p>
	 * We chose AbstractGridCoverage2DReader as our target as that is what
	 * BasicGridCoverageRenderer asks for.
	 */
	public boolean canAdapt(IResolve resolve, Class<? extends Object> adapter) {
		if (adapter.isAssignableFrom(ImageMosaicReader.class)) {
			File file = toShpFile(resolve);
            return file!=null  && format.accepts(file);
        }
        return false;
	}
	private File toShpFile( IResolve resolve ) {
	    IGeoResource resource = (IGeoResource) resolve;
        IService service;
        try {
            service = resource.service(new NullProgressMonitor());
        if (service.canResolve(ShapefileDataStore.class)) {
            return service.resolve(File.class, new NullProgressMonitor());
        }
        } catch (IOException e) {
            RasteringsPlugin.log("Error obtaining shapefile file", e); //$NON-NLS-1$
            return null;
        }
        return null;
    }

    /**
	 * ImageMosaicReader for the provided shapefile or null.
	 * 
	 * @param shapefile
	 * @param monitor
	 * @return ImageMosaicReader for the provided shapefile, or null 
	 */
	AbstractGridCoverage2DReader toGridCoverage2DReader( IResolve resolve, IProgressMonitor monitor ) throws IOException {
	    File file = toShpFile(resolve);
		if( format.accepts( file )){
			return new ImageMosaicReader( file, null );		
		}
		return null; // did canAdapt get called?
	}
}
