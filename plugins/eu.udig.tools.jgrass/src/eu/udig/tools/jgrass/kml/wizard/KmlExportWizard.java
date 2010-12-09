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
package eu.udig.tools.jgrass.kml.wizard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.ui.ExceptionDetailsDialog;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.coverage.processing.Operations;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.gce.arcgrid.ArcGridFormat;
import org.geotools.gce.arcgrid.ArcGridWriteParams;
import org.geotools.gce.arcgrid.ArcGridWriter;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.referencing.CRS;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import eu.udig.tools.jgrass.JGrassToolsPlugin;
import eu.udig.tools.jgrass.kml.core.Kmlutils;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class KmlExportWizard extends Wizard implements IExportWizard {

    public static boolean canFinish = false;
    private KmlExportWizardPage mainPage;
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmm"); //$NON-NLS-1$

    public KmlExportWizard() {
        super();
    }

    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        setWindowTitle("Kml export");
        setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/export_wiz.png")); //$NON-NLS-1$
        setNeedsProgressMonitor(true);

        mainPage = new KmlExportWizardPage();
    }

    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

    public boolean performFinish() {
        /*
         * run with backgroundable progress monitoring
         */
        IRunnableWithProgress operation = new IRunnableWithProgress(){

            public void run( IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {

                IGeoResource geoResource = mainPage.getGeoResource();
                String filePath = mainPage.getFilePath();

                /*
                 * finally do some processing
                 */
                pm.beginTask("Exporting map...", IProgressMonitor.UNKNOWN);
                try {
                    if (geoResource.canResolve(SimpleFeatureSource.class)) {
                        SimpleFeatureSource featureStore = (SimpleFeatureSource) geoResource.resolve(SimpleFeatureSource.class,
                                pm);
                        Kmlutils.writeKml(new File(filePath), featureStore.getFeatures());
                    } else {
                        throw new IOException("The selected resource is not a feature layer: " + geoResource.getTitle());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    String message = "An error occurred while exporting the resource: " + geoResource.getTitle();
                    ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassToolsPlugin.PLUGIN_ID, e);
                }
                pm.done();

            }
        };

        PlatformGIS.runInProgressDialog("Exporting to kml...", true, operation, true);
        return true;
    }

    public boolean canFinish() {
        return super.canFinish() && canFinish;
    }

}
