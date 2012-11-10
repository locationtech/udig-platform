/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package net.refractions.udig.catalog.imageio.mosaicwizard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.imageio.Activator;
import net.refractions.udig.ui.ExceptionDetailsDialog;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.geotools.gce.imagemosaic.ImageMosaicFormat;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class MosaicImportWizard extends Wizard implements INewWizard {

    private static final String WIZBAN_GIF = "icons/wizban/worldimage_wiz.gif";

    private MosaicImportWizardPage mainPage;

    public static boolean canFinish = false;

    public MosaicImportWizard() {
        super();
    }

    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        setWindowTitle("Imagery to Mosaic import");
        ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
        ImageDescriptor banner = imageRegistry.getDescriptor(WIZBAN_GIF);
        if( banner == null ){
            URL bannerURL = Activator.getDefault().getBundle().getEntry( WIZBAN_GIF );        
            banner = ImageDescriptor.createFromURL( bannerURL );
            imageRegistry.put( WIZBAN_GIF, banner );
        }
        Image image = banner.createImage();
        
        setDefaultPageImageDescriptor( banner );
        setNeedsProgressMonitor(true);
        mainPage = new MosaicImportWizardPage("Imagery to Mosaic import"); //$NON-NLS-1$
    }

    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

    public boolean canFinish() {
        return super.canFinish() && canFinish;
    }

    public boolean performFinish() {

        /*
         * run with backgroundable progress monitoring
         */
        IRunnableWithProgress operation = new IRunnableWithProgress(){

            public void run( IProgressMonitor pm ) throws InvocationTargetException,
                    InterruptedException {
                try {
                    ImageMosaicFormat imageMosaicFormat = new ImageMosaicFormat();
                    File imageryFolder = mainPage.getImageryFolder();
                    imageMosaicFormat.getReader(imageryFolder);

                    String folderName = imageryFolder.getName();
                    File shapeFile = new File(imageryFolder, folderName + ".shp");

                    IServiceFactory sFactory = CatalogPlugin.getDefault().getServiceFactory();
                    ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
                    URL url = shapeFile.toURI().toURL();
                    IService registered = catalog.acquire( url, null );                    
//                    List<IService> services = sFactory.createService(shapeFile.toURI().toURL());
//                    for( IService service : services ) {
//                        catalog.add(service);
//                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    String message = "An error occurred while importing the imagery to mosaic.";
                    ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, Activator.ID, e);
                } catch (IOException e) {
                    String message = "An error occurred while importing the imagery to mosaic.";
                    ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, Activator.ID, e);
                }

            }
        };
        PlatformGIS.runInProgressDialog("Importing imagery to mosaic", true, operation, true);
        return true;
    }
}
