/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2013, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.wizard.export.image;

import java.awt.Graphics2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.core.internal.Icons;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.ApplicationGIS.DrawMapParameter;
import org.locationtech.udig.project.ui.BoundsStrategy;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.geotools.data.DataUtilities;
import org.opengis.referencing.operation.TransformException;

/**
 * Wizard for exporting a collection of maps to a collection of images. One for each map.
 * 
 * @author Jesse
 */
public class ExportMapToImageWizard extends Wizard implements IExportWizard {

    public static final String DIRECTORY_KEY = "exportDirectoryKey"; //$NON-NLS-1$
    public static final String FORMAT_KEY = "exportFormatKey"; //$NON-NLS-1$
    public static final String WIDTH_KEY = "widthKey"; //$NON-NLS-1$
    public static final String HEIGHT_KEY = Messages.ExportMapToImageWizard_3;
    public static final String SELECTION = "SELECTION_HANDLING"; //$NON-NLS-1$

    private ImageExportPage imageSettingsPage = new ImageExportPage();
    private MapSelectorPageWithScaleColumn mapSelectorPage;

    public ExportMapToImageWizard() {
        setWindowTitle(Messages.ExportMapToImageWizard_windowtitle);
        setDialogSettings(ProjectUIPlugin.getDefault().getDialogSettings());

        String title = null; // will use default title
        ImageDescriptor banner = ProjectUIPlugin.getDefault().getImageDescriptor(Icons.WIZBAN + "exportimage_wiz.gif"); //$NON-NLS-1$
        setDefaultPageImageDescriptor(banner);
        mapSelectorPage = new MapSelectorPageWithScaleColumn("Select Map With Scale", title, banner); //$NON-NLS-1$
        addPage(mapSelectorPage);
        addPage(imageSettingsPage);
        setNeedsProgressMonitor(true);
    }

    @Override
    public void setContainer( IWizardContainer wizardContainer ) {
        super.setContainer(wizardContainer);
        addPageChangeListener();
    }

    /**
     * Updates the Map selector page's description so that it indicates the File format that the
     * export will go to.
     */
    private void addPageChangeListener() {
        if (getContainer() instanceof WizardDialog) {
            WizardDialog dialog = (WizardDialog) getContainer();
            dialog.addPageChangedListener(new IPageChangedListener(){

                public void pageChanged( PageChangedEvent event ) {
                    WizardPage currentPage = (WizardPage) event.getSelectedPage();
                    if (currentPage == mapSelectorPage) {
                        String currentFormat = imageSettingsPage.getFormat().getName();
                        String description = MessageFormat.format(
                                "Select map to export to {0} images", currentFormat);
                        currentPage.setDescription(description);
                    }
                }

            });
        }
    }
    @Override
    public boolean performFinish() {

	// reset error message if user hit finish again (hopefully with modified parameters)
        if (getContainer().getCurrentPage() instanceof WizardPage) {
            WizardPage wp = (WizardPage) getContainer().getCurrentPage();
            wp.setErrorMessage(null);
        };

        final Collection<String> errors = new ArrayList<String>();
        final Collection<IMap> renderedMaps = new ArrayList<IMap>();
        try {
            getContainer().run(false, true, new IRunnableWithProgress(){

                public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                        InterruptedException {

                    Collection<IMap> maps = mapSelectorPage.getMaps();

                    monitor.beginTask(Messages.ExportMapToImageWizard_exportMapsTaskName, maps
                            .size() * 3 + 1);
                    monitor.worked(1);
                    for( IMap map : maps ) {
                        try {
                            exportMap(map, new SubProgressMonitor(monitor, 3));
                        } catch (RenderException e) {

                            Object[] args = new Object[]{map.getName(), e.getLocalizedMessage()};
                            String pattern = Messages.ExportMapToImageWizard_renderingErrorMessage;
                            errors.add(MessageFormat.format(pattern, args));
                        } catch (IOException e) {
                            errors.add(Messages.ExportMapToImageWizard_ioexceptionErrorMessage
                                    + e.getLocalizedMessage());
                        } catch (TransformException e) {
                            errors
                                    .add("Failed to create world file.  This image can not be used as a Raster file in uDig");
                        } catch (NoninvertibleTransformException e) {
                            errors
                                    .add("Failed to create world file.  This image can not be used as a Raster file in uDig");
                        } catch (RuntimeException e) {
                            errors
                                    .add("An unexpected failure occurred: "
                                            + e.getLocalizedMessage());
                        }
                        renderedMaps.add(map);
                    }
                    mapSelectorPage.getMaps().remove(renderedMaps);
                    mapSelectorPage.updateMapList();
                }
            });
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        if (!errors.isEmpty()) {
            ((WizardPage) getContainer().getCurrentPage())
                    .setErrorMessage(Messages.ExportMapToImageWizard_ShowErrorMessage
                            + errors.iterator().next());
            return false;
        }

        getDialogSettings().put(DIRECTORY_KEY, mapSelectorPage.getExportDir().getAbsolutePath());
        getDialogSettings().put(FORMAT_KEY, imageSettingsPage.getFormat().getName());

        return true;
    }

    private void exportMap( IMap map, IProgressMonitor monitor ) throws RenderException,
            IOException, TransformException, NoninvertibleTransformException {

        monitor.beginTask(Messages.ExportMapToImageWizard_RenderingTaskName, 10);
        String pattern = Messages.ExportMapToImageWizard_preparingTaskName;
        Object[] args = new Object[]{map.getName()};
        monitor.setTaskName(MessageFormat.format(pattern, args));
        File destination = determineDestinationFile(map);
        if (destination == null) {
            return;
        }

        int width = imageSettingsPage.getWidth(map.getViewportModel().getWidth(), map
                .getViewportModel().getHeight());
        int height = imageSettingsPage.getHeight(map.getViewportModel().getWidth(), map
                .getViewportModel().getHeight());

        // gdavis - ARGB won't output proper background color for non-alpha supporting
        // image types like jpg. Since the resulting image contains no alpha, RGB works
        // fine for all formats.
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // .TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();

        IMap renderedMap;
        try {
            monitor.worked(2);
            pattern = Messages.ExportMapToImageWizard_renderingTaskname;
            args = new Object[]{map.getName()};
            monitor.setTaskName(MessageFormat.format(pattern, args));
            int scaleDenom = MapSelectorPageWithScaleColumn.getScaleDenom(map);
            BoundsStrategy boundsStrategy = new BoundsStrategy(scaleDenom);

            DrawMapParameter drawMapParameter = new DrawMapParameter(g, new java.awt.Dimension(
                    width, height), map, boundsStrategy, imageSettingsPage.getFormat().getDPI(),
                    imageSettingsPage.getSelectionHandling(), monitor);

            renderedMap = ApplicationGIS.drawMap(drawMapParameter);
        } finally {
            g.dispose();
        }
        monitor.worked(3);
        pattern = Messages.ExportMapToImageWizard_writingTaskname;
        args = new Object[]{map.getName()};
        monitor.setTaskName(MessageFormat.format(pattern, args));
        imageSettingsPage.getFormat().write(renderedMap, image, destination);
        
        addToCatalog(destination ); 
        
        monitor.done();
    }

    private void addToCatalog( final File file ) throws IOException {
        if( !file.exists() ){
            throw new FileNotFoundException("Expected "+file+" was not created");
        }

        // try if the file isn't a pdf
        if ( file.getAbsolutePath().toLowerCase().endsWith("pdf") ) { //$NON-NLS-1$
            return;
        }

        Job addToCatalog = new Job("Add "+file.getName() ){
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
                URL url = new ID( file, null ).toURL();
                IService service = null;
                try {
                    service = localCatalog.acquire( url, monitor );
                }
                catch( IOException unepected ){
                }
                return service != null ? Status.OK_STATUS : new Status( IStatus.ERROR, ProjectUIPlugin.ID, "Failed to add "+file );
            }
        };
        addToCatalog.schedule();
    }
    /**
     * This implementation is a bit more drastic then the usual catalog add method
     * as it will replace the existing contents if required.
     *
     * @param localCatalog
     * @param service
     */
    private void addToCatalog( ICatalog localCatalog, IService service ) {
        ID id = service.getID();
        IService found = localCatalog.getById(IService.class, id, new NullProgressMonitor());
        if (found != null) {
            // existing catalog entry to replace
            // (if this is only being done for a "refresh" we may be able to take less
            // drastic action)
            localCatalog.replace(service.getID(), service);
        } else {
            localCatalog.add(service);
        }
    }

    private File determineDestinationFile( IMap map ) {
        File exportDir = mapSelectorPage.getExportDir();
        String name = URLUtils.cleanFilename(map.getName());
        File destination = addSuffix(new File(exportDir, name));
        if (destination.exists()) {
            boolean overwrite = !MessageDialog
                    .openQuestion(getContainer().getShell(),
                            Messages.ExportMapToImageWizard_overwriteWarningTitle,
                            Messages.ExportMapToImageWizard_overwriteWarningMessage
                                    + destination.getName());

            if (!overwrite) {
                if (!destination.delete()) {
                    destination = selectFile(destination,
                            Messages.ExportMapToImageWizard_unableToDeleteMsg);
                }

            } else {
                destination = selectFile(destination,
                        Messages.ExportMapToImageWizard_selectFileTitle);
            }
        }

        if (destination == null) {
            return null;
        }

        return addSuffix(destination);
    }

    private File addSuffix( File file ) {
        String path = stripEndSlash(file.getPath());

        File destination;
        String extension = imageSettingsPage.getFormat().getExtension();
        if (!path.endsWith(extension)) {
            destination = new File(path + "." + extension); //$NON-NLS-1$
        } else {
            return file;
        }
        return destination;
    }

    private String stripEndSlash( String path ) {
        if (path.endsWith("/")) //$NON-NLS-1$
            return stripEndSlash(path.substring(0, path.length() - 1));
        return path;
    }

    private File selectFile( File destination, String string ) {
        FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
        dialog.setText(string);
        dialog.setFilterPath(destination.getParent());
        dialog.setFileName(destination.getName());
        String file = dialog.open();
        if (file == null) {
            destination = null;
        } else {
            destination = new File(file);
        }
        return destination;
    }

    @SuppressWarnings("unchecked")
    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        mapSelectorPage.setSelection(selection);
    }

}
