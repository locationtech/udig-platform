package net.refractions.udig.catalog.internal.shp;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.ServiceMover;
import net.refractions.udig.catalog.URLUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStoreFactory;

public class ShapeMover implements IResolveAdapterFactory, ServiceMover {

    private IResolve resolve;
    private String[] extentions = new String[]{".shp", ".prj", ".dbf", ".shx", ".fix", ".qix",      //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$
            ".xml", ".grx"};  //$NON-NLS-1$//$NON-NLS-2$

    public ShapeMover() {
    }

    public ShapeMover( IResolve resolve ) {
        this.resolve = resolve;
    }

    @SuppressWarnings("unchecked")
    public Object adapt( IResolve resolve, Class adapter, IProgressMonitor monitor )
            throws IOException {

        if (adapter.isAssignableFrom(ShapeMover.class)) {
            this.resolve = resolve;
            return new ShapeMover(resolve);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public boolean canAdapt( IResolve resolve, Class adapter ) {
        return adapter.isAssignableFrom(ShapeMover.class);
    }

    public String move( File destinationFolder ) {
        /*
         * shapefile are moved into a folder that has to exist
         */
        if (destinationFolder.isDirectory() && destinationFolder.exists()) {
            ShpServiceImpl shpServiceImpl = (ShpServiceImpl) resolve;

            Map<String, Serializable> parametersMap = shpServiceImpl.getConnectionParams();
            URL url = (URL) parametersMap.get(IndexedShapefileDataStoreFactory.URLP.key);

            File file = URLUtils.urlToFile(url);
            String completeShapeFilePath = file.getAbsolutePath();
            completeShapeFilePath.replaceAll("\\\\", "/");
            int dotPosition = completeShapeFilePath.lastIndexOf("."); //$NON-NLS-1$
            String completeShapefileBasePath = completeShapeFilePath.substring(0, dotPosition);

            // update parameter so service indicates correct file
            try {
				updateConnectionParameters(destinationFolder, parametersMap, completeShapeFilePath);
			} catch (MalformedURLException e) {
				return "Failed to update the service's connection Parameters";
			}

            for( String extention : extentions ) {

                File tmpFile = new File(completeShapefileBasePath + extention);
                if (tmpFile.exists()) {
                    // Move file to new directory
                    boolean success = tmpFile.renameTo(new File(destinationFolder, tmpFile
                            .getName()));
                    if (!success) {
                        return "Wasn't able to move file: " + tmpFile.getAbsolutePath();
                    }
                } else {// try uppercase
                    extention = extention.toUpperCase();
                    tmpFile = new File(completeShapefileBasePath + extention);
                    if (tmpFile.exists()) {
                        // Move file to new directory
                        boolean success = tmpFile.renameTo(new File(destinationFolder, tmpFile
                                .getName()));
                        if (!success) {
                            return "Wasn't able to move file: " + tmpFile.getAbsolutePath();
                        }
                    }
                }
            }
            URL id = resolve.getIdentifier();
			IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
			IService newService = serviceFactory.createService(parametersMap).iterator().next();
			ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
			localCatalog.replace(id, newService );
            return null;
        }

        return "Problems in preparing the consolidation environment.";
    }

	private void updateConnectionParameters(File destinationFolder,
			Map<String, Serializable> parametersMap, String completeShapeFilePath)
			throws MalformedURLException {
		int lastSlash = completeShapeFilePath.lastIndexOf("/");
		// keep the slash
		String shpName = completeShapeFilePath.substring(lastSlash);
		String destinationPath = destinationFolder.getAbsolutePath();
		String urlString = destinationPath+shpName;
		urlString = urlString.replaceAll("//", "/");
		URL url = new URL("file://"+urlString);
		parametersMap.put(IndexedShapefileDataStoreFactory.URLP.key,
				url);
	}

}
