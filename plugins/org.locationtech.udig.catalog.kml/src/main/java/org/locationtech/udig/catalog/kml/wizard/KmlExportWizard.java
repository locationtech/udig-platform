/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.kml.wizard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.ui.ExceptionDetailsDialog;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.geotools.data.simple.SimpleFeatureSource;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.locationtech.udig.catalog.kml.core.KmlToolPlugin;
import org.locationtech.udig.catalog.kml.core.KmlUtils;
import org.locationtech.udig.catalog.kml.internal.Messages;
import org.locationtech.udig.catalog.kml.internal.ui.ImageConstants;

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
        setDefaultPageImageDescriptor(KmlToolPlugin.getDefault().getImageDescriptor(ImageConstants.EXPORTKML_WIZ));
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
