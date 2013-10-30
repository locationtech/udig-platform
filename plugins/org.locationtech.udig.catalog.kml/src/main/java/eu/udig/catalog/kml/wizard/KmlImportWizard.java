/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
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
import org.geotools.data.FeatureStore;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import eu.udig.catalog.kml.core.KmlToolPlugin;
import eu.udig.catalog.kml.core.KmlUtils;
import eu.udig.catalog.kml.internal.Messages;
import eu.udig.catalog.kml.internal.ui.ImageConstants;
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
        setDefaultPageImageDescriptor(KmlToolPlugin.getDefault().getImageDescriptor(ImageConstants.IMPORTKML_WIZ));
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
                    FeatureCollection<SimpleFeatureType, SimpleFeature> collection = KmlUtils.kmlFile2FeatureCollection(kmlFile);

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
