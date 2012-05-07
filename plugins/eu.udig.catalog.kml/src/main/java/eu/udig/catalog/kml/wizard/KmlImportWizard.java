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
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.ExceptionDetailsDialog;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import eu.udig.catalog.kml.core.KmlToolPlugin;
import eu.udig.catalog.kml.core.KmlUtils;
import eu.udig.catalog.kml.internal.Messages;
/**
 * @author Andrea Antonello - www.hydrologis.com
 * @author Frank Gasdorf
 */
public class KmlImportWizard extends Wizard implements INewWizard {

    private KmlImportWizardPage mainPage;

    public static boolean canFinish = false;

    private final Map<String, String> params = new HashMap<String, String>();

    public KmlImportWizard() {
        super();
    }

    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        setWindowTitle(Messages.getString("KmlImportWizard.windowTitle")); //$NON-NLS-1$
        setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(KmlToolPlugin.PLUGIN_ID,
                "icons/icon_kml48.png")); //$NON-NLS-1$
        setNeedsProgressMonitor(true);
        mainPage = new KmlImportWizardPage(Messages.getString("KmlImportWizard.KmlFilImportPageName"), params); //$NON-NLS-1$
    }

    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

    public boolean canFinish() {
        return super.canFinish() && canFinish;
    }

    public boolean performFinish() {

        final File kmlFile = mainPage.getKmlFile();

        /*
         * run with backgroundable progress monitoring
         */
        IRunnableWithProgress operation = new IRunnableWithProgress(){

            public void run( IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {
                try {
                    SimpleFeatureCollection collection = KmlUtils.kmlFile2FeatureCollection(kmlFile);

                    IGeoResource resource = CatalogPlugin.getDefault().getLocalCatalog()
                            .createTemporaryResource(collection.getSchema());

                    @SuppressWarnings("unchecked")
                    FeatureStore<SimpleFeatureType, SimpleFeature> store = resource.resolve(FeatureStore.class, pm);
                    store.addFeatures(collection);

                    ApplicationGIS.addLayersToMap(ApplicationGIS.getActiveMap(), Collections.singletonList(resource), -1);
                } catch (Exception e) {
                    e.printStackTrace();
                    String message = Messages.getString("KmlImportWizard.error.ExceptionDuringKMLToFeatureLayer"); //$NON-NLS-1$
                    ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, KmlToolPlugin.PLUGIN_ID, e);
                }
            }
        };

        PlatformGIS.runInProgressDialog(Messages.getString("KmlImportWizard.taskImportKmlDataMessage"), true, operation, true); //$NON-NLS-1$

        return true;
    }
}
