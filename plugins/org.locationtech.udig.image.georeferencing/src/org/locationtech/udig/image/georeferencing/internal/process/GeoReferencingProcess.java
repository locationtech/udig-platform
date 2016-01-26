/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.image.georeferencing.internal.process;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultDerivedCRS;
import org.geotools.referencing.cs.DefaultCartesianCS;
import org.geotools.referencing.operation.transform.WarpTransform2D;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.opengis.coverage.Coverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Class responsible of the image georeferencing process.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public class GeoReferencingProcess {

	private CoordinateReferenceSystem	crsTarget	= null;
	private Point2D[]					dstCoords	= null;
	private Point2D[]					srcCoords	= null;
	private BufferedImage				image		= null;
	private IMap						map			= null;
	private String						outputFileName;

	/**
	 * Constructor.
	 * 
	 * @param crsTarget
	 *            Target CRS.
	 * @param srcCoords
	 *            Source coordinates, these are map coordinates.
	 * @param dstCoords
	 *            Destiny coordinates, there are image coordinates.
	 * @param imgPath
	 *            Path of the image that'll suffer the georeferencing process.
	 * @param outputFileName
	 *            Resultant file path.
	 * @param map
	 *            IMap used by uDig.
	 */
	public GeoReferencingProcess(	final CoordinateReferenceSystem crsTarget,
									final Point2D[] srcCoords,
									final Point2D[] dstCoords,
									final String imgPath,
									final String outputFileName,
									final IMap map) {

		assert crsTarget != null;
		assert srcCoords != null;
		assert dstCoords != null;
		assert imgPath != null && !imgPath.equals(""); //$NON-NLS-1$
		assert outputFileName != null && !outputFileName.equals(""); //$NON-NLS-1$
		assert map != null;

		this.crsTarget = crsTarget;
		this.srcCoords = srcCoords;
		this.dstCoords = dstCoords;
		this.map = map;

		this.image = createImage(imgPath);
		this.outputFileName = outputFileName;
	}

	/**
	 * Creates the buffered image that will be used among the process.
	 * 
	 * @param imgPath
	 * @return The bufferedImage.
	 */
	private BufferedImage createImage(String imgPath) {

		BufferedImage image = null;
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(new File(imgPath)));
			image = ImageIO.read(bis);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * <p>
	 * Run the image georeferencing process.
	 * 
	 * <pre>
	 * -Transforms the coordinates into 2D. 
	 * -Creates a 2D CRS using the transformed coordinates. 
	 * -Using the CRS creates a referenced envelope
	 * which will be used with the image to create a {@link GridCoverage2D}.
	 * -Resample the coverage.
	 * -Save the resultant coverage as a Tif file.
	 * </pre>
	 * 
	 * </p>
	 * @throws IOException 
	 */
	public void run() throws IOException {

		WarpTransform2D warpTransform = new WarpTransform2D(srcCoords, dstCoords, 1);

		// create a DefaultDerivedCRS for the CRS of the image
		DefaultDerivedCRS derivedCRS = new DefaultDerivedCRS("imageCRS", this.crsTarget, warpTransform, //$NON-NLS-1$
					DefaultCartesianCS.GENERIC_2D);
		// now create a gridcoverage for the new warped image
		GridCoverageFactory factory = new GridCoverageFactory();
		ReferencedEnvelope ref = new ReferencedEnvelope(0, image.getWidth(null), 0, image.getHeight(null), derivedCRS);

		GridCoverage2D coverage = (GridCoverage2D) factory.create("GridCoverage", image, ref); //$NON-NLS-1$

		// resample the new image with the world CRS
		GridCoverage2D warpedCoverage = null;
		try {
			Operations ops = new Operations(null);
			Coverage resample = ops.resample(coverage, this.crsTarget);
			warpedCoverage = (GridCoverage2D) resample;
			saveImage(warpedCoverage);

		} catch (Exception e) {
			
			e.printStackTrace();
			throw new IOException("Failed generating the file " + outputFileName);
		}

	}

	/**
	 * Creates a tif file using the give coverage.
	 * 
	 * @param warpedCoverage
	 * @throws IOException 
	 */
	private void saveImage(GridCoverage2D warpedCoverage) throws IOException {

		if (warpedCoverage != null) {

			writeTif(warpedCoverage, outputFileName);
		}
	}

	/**
	 * Writes a .tif file.
	 * 
	 * @param warpedCoverage
	 *            The coverage.
	 * @param filename
	 *            The output file name.
	 * @throws IOException 
	 */
	private void writeTif(GridCoverage2D warpedCoverage, String filename) throws IOException {

		GeoTiffWriter writer;
		if (!filename.contains(".tif")) { //$NON-NLS-1$
			filename += ".tif"; //$NON-NLS-1$
		}
		writer = new GeoTiffWriter(new File(filename));
		writer.write(warpedCoverage, null);

		// add the result as a layer to the map by
		// loading that file as a layer
		File tempfile = new File(filename);
		IGeoResource tiffResource = null;
		try {
			writer = new GeoTiffWriter(tempfile);
			writer.write(warpedCoverage, null);
			// tiffResource = getTiffResource(tempfile.toURL());
			tiffResource = getTiffResource(tempfile.toURI().toURL());
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

		if (tiffResource != null) {
			ApplicationGIS.addLayersToMap(map, Collections.singletonList(tiffResource), -1);
		}
	}

	private IGeoResource getTiffResource(URL url) {
		IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
		List<IService> services = factory.createService(url);

		if (services.isEmpty()) {
			return null;
		}
		for (IService service : services) {
			IResolve resolve;
			IGeoResource geoR;
			try {
				resolve = service.members(null).get(0);
				geoR = resolve.resolve(IGeoResource.class, new NullProgressMonitor());
				return geoR;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
