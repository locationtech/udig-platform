/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.shp;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.ServiceMover;
import org.locationtech.udig.catalog.URLUtils;

/**
 * This class represents a Shapefile that is known to be on disk.
 * <p>
 * This class implements the ServiceMover - indicating that the file(s) can
 * be moved to another location. (specifically client code can adapt to
 * the ServiceMover interface and move the files on disk
 * and update the connection parameters in one go.
 * <p>
 * If this class was public we could provide additional shapefile on
 * disk specific methods (such as create index).
 */
public class ShapeMover implements ServiceMover {
	
	/** Our handle to the shapefile on disk */
    private ShpServiceImpl shapefile;
    
    /**
     * The following extentions are considered part of the shapefile.
     */
    private String[] extentions = new String[]{".shp", ".prj", ".dbf", ".shx", ".fix", ".qix",      //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$
            ".xml", ".grx"};  //$NON-NLS-1$//$NON-NLS-2$

    public ShapeMover( IResolve resolve ) {
        this.shapefile = (ShpServiceImpl) resolve;
    }

    /**
     * The file as indicated in the connection parameters.
     * @return file as indicated in the connection parameters (may be relative)
     */
    public File toFile(){
    	Map<String, Serializable> parametersMap = shapefile.getConnectionParams();
        URL url = (URL) parametersMap.get(ShapefileDataStoreFactory.URLP.key);
        return URLUtils.urlToFile(url);        
    }
        
    /**
     * Move this shapefile to the indicated destinationFolder.
     */
    public String move( File destinationFolder ) {
    	if (!destinationFolder.exists() || !destinationFolder.isDirectory() ){
    		/*
             * shapefile are moved into a folder that has to exist
             */
            return "Indicated directory does not exist:"+destinationFolder; 
    	}
        Map<String, Serializable> parametersMap = shapefile.getConnectionParams();
        
        URL url = (URL) parametersMap.get(ShapefileDataStoreFactory.URLP.key);        
        File file = URLUtils.urlToFile(url);
        
        String completeShapeFilePath = file.getAbsolutePath();
        completeShapeFilePath = completeShapeFilePath.replaceAll("\\\\", "/");
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
        ID id = shapefile.getID();
		IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
		IService newService = serviceFactory.createService(parametersMap).iterator().next();
		ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
		localCatalog.replace(id, newService );
        return null;
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
		parametersMap.put(ShapefileDataStoreFactory.URLP.key, 
				url);
	}

}
