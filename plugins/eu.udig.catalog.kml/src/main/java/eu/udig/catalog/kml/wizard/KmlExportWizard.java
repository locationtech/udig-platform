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
package eu.udig.catalog.kml.wizard;

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
import org.geotools.data.simple.SimpleFeatureSource;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import eu.udig.catalog.kml.core.KmlToolPlugin;
import eu.udig.catalog.kml.core.KmlUtils;
import eu.udig.catalog.kml.internal.Messages;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 * @author Frank Gasdorf
 */
public class KmlExportWizard extends Wizard implements IExportWizard {

    public static boolean canFinish = false;
    private KmlExportWizardPage mainPage;
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmm"); //$NON-NLS-1$

    public KmlExportWizard() {
        super();
    }

    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        setWindowTitle(Messages.getString("KmlExportWizard.windowTitle")); //$NON-NLS-1$
        setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(KmlToolPlugin.PLUGIN_ID,
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
                pm.beginTask(Messages.getString("KmlExportWizard.taskExportingMap"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
                try {
                    if (geoResource.canResolve(SimpleFeatureSource.class)) {

                        SimpleFeatureSource featureStore = (SimpleFeatureSource) geoResource.resolve(SimpleFeatureSource.class,
                                pm);
                        KmlUtils.writeKml(new File(filePath), featureStore.getFeatures());
                    } else {
                        throw new IOException(Messages.getString("KmlExportWizard.error.ResourceIsNotAFeatureLayer") + geoResource.getTitle()); //$NON-NLS-1$
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    String message = Messages.getString("KmlExportWizard.error.ErrorOccuredWhileExproting") + geoResource.getTitle(); //$NON-NLS-1$
                    ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, KmlToolPlugin.PLUGIN_ID, e);
                }
                pm.done();

            }
        };

        PlatformGIS.runInProgressDialog(Messages.getString("KmlExportWizard.taskExportingToKML"), true, operation, true); //$NON-NLS-1$
        return true;
    }

    public boolean canFinish() {
        return super.canFinish() && canFinish;
    }

}
