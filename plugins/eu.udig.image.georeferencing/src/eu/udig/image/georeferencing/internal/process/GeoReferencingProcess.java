/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios) 
 * Axios agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.image.georeferencing.internal.process;

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

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.ViewType;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultDerivedCRS;
import org.geotools.referencing.cs.DefaultCartesianCS;
import org.geotools.referencing.operation.transform.WarpTransform2D;
import org.opengis.coverage.Coverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Class responsible of the image georeferencing process.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.0.0
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
	 */
	public void run() {

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
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		saveImage(warpedCoverage);
	}

	/**
	 * Creates a tif file using the give coverage.
	 * 
	 * @param warpedCoverage
	 */
	private void saveImage(GridCoverage2D warpedCoverage) {

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
	 */
	private void writeTif(GridCoverage2D warpedCoverage, String filename) {

		GeoTiffWriter writer;
		if (!filename.contains(".tif")) { //$NON-NLS-1$
			filename += ".tif"; //$NON-NLS-1$
		}
		try {
			writer = new GeoTiffWriter(new File(filename));
			writer.write(warpedCoverage.view(ViewType.GEOPHYSICS), null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// add the result as a layer to the map by
		// loading that file as a layer
		File tempfile = new File(filename);
		IGeoResource tiffResource = null;
		try {
			writer = new GeoTiffWriter(tempfile);
			writer.write(warpedCoverage.view(ViewType.GEOPHYSICS), null);
			// tiffResource = getTiffResource(tempfile.toURL());
			tiffResource = getTiffResource(tempfile.toURI().toURL());
		} catch (IOException e) {
			e.printStackTrace();
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
