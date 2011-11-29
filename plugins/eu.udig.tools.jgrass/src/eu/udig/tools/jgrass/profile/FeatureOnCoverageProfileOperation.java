/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.tools.jgrass.profile;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.style.sld.SLD;
import net.refractions.udig.ui.ExceptionDetailsDialog;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.GeometryDescriptor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import eu.udig.tools.jgrass.JGrassToolsPlugin;
import eu.udig.tools.jgrass.profile.borrowedfromjgrasstools.CoverageUtilities;
import eu.udig.tools.jgrass.profile.borrowedfromjgrasstools.ProfilePoint;

/**
 * Operation to create a profile of a line feature over a coverage.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class FeatureOnCoverageProfileOperation implements IOp {
    private ProfileView chartView;
    private GridCoverage2D coverage = null;
    private SimpleFeature lineFeature = null;

    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ILayer[] layers = (ILayer[]) target;

        for( ILayer iLayer : layers ) {
            final IGeoResource geoResource = iLayer.getGeoResource();
            if (geoResource.canResolve(FeatureSource.class)) {
                SimpleFeatureSource lineSource = (SimpleFeatureSource) geoResource.resolve(FeatureSource.class, monitor);
                SimpleFeatureCollection featureCollection = lineSource.getFeatures(iLayer.getFilter());
                GeometryDescriptor geometryDescriptor = featureCollection.getSchema().getGeometryDescriptor();
                if (!SLD.isLine(geometryDescriptor)) {
                    break;
                }
                SimpleFeatureIterator featureIterator = featureCollection.features();
                if (featureIterator.hasNext()) {
                    lineFeature = featureIterator.next();
                }

            }
            if (geoResource.canResolve(GridCoverage.class)) {
                coverage = (GridCoverage2D) geoResource.resolve(GridCoverage.class, monitor);
            }
        }

        if (lineFeature == null || coverage == null) {
            display.asyncExec(new Runnable(){
                public void run() {
                    MessageDialog.openWarning(display.getActiveShell(), "Wrong layers error",
                            "This operation works only if a line layer and a coverage layer are selected in the layers view.");
                }
            });
            return;
        }
        display.syncExec(new Runnable(){
            public void run() {
                try {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ProfileView.ID);
                    chartView = ((ProfileView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .findView(ProfileView.ID));
                    chartView.clearSeries();
                    chartView.clearMarkers();

                    Geometry geometry = (Geometry) lineFeature.getDefaultGeometry();
                    Coordinate[] coordinates = geometry.getCoordinates();

                    List<ProfilePoint> profile = CoverageUtilities.doProfile(coverage, coordinates);

                    for( ProfilePoint profilePoint : profile ) {
                        double elevation = profilePoint.getElevation();
                        if (!Double.isNaN(elevation)) {
                            chartView.addToSeries(profilePoint.getProgressive(), elevation);
                        } else {
                            chartView.addToSeries(profilePoint.getProgressive(), 0.0);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    String message = "An error occurred while extracting the profile data";
                    ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassToolsPlugin.PLUGIN_ID, e);
                }
            }
        });

    }

}
