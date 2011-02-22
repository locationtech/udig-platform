/*
 * LGPL
 */
package net.refractions.udig.project.ui.wizard.export.image;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.core.internal.Icons;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.render.RenderException;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.BoundsStrategy;
import net.refractions.udig.project.ui.ApplicationGIS.DrawMapParameter;
import net.refractions.udig.project.ui.internal.Images;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
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
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Wizard for exporting a collection of maps to a collection of images.  One for each map.
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
		ImageDescriptor banner = Images.getDescriptor( Icons.WIZBAN +"exportimage_wiz.gif" ); //$NON-NLS-1$
		setDefaultPageImageDescriptor( banner );
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
    if( getContainer() instanceof WizardDialog ){
        WizardDialog dialog = (WizardDialog) getContainer();
        dialog.addPageChangedListener(new IPageChangedListener(){

            public void pageChanged( PageChangedEvent event ) {
                WizardPage currentPage = (WizardPage) event.getSelectedPage();
                if( currentPage== mapSelectorPage ){
                    String currentFormat = imageSettingsPage.getFormatName();
                    String description = MessageFormat.format(Messages.ExportMapToImageWizard_mapSelectorPageDescription, currentFormat);
                    currentPage.setDescription(description);
                }
            }

        });
    }
}
    @Override
	public boolean performFinish() {

		final Collection<String> errors = new ArrayList<String>();
		final Collection<IMap> renderedMaps = new ArrayList<IMap>();
		try {
			getContainer().run(false, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {

					Collection<IMap> maps = mapSelectorPage.getMaps();

					monitor.beginTask(Messages.ExportMapToImageWizard_exportMapsTaskName, maps.size() * 3 + 1);
					monitor.worked(1);
					for (IMap map : maps) {
						try {
							exportMap(map, new SubProgressMonitor(monitor, 3));
						} catch (RenderException e) {

							Object[] args = new Object[]{map.getName(), e.getLocalizedMessage()};
							String pattern = Messages.ExportMapToImageWizard_renderingErrorMessage;
							errors.add(MessageFormat.format(pattern, args));
						} catch (IOException e) {
							errors
									.add(Messages.ExportMapToImageWizard_ioexceptionErrorMessage
											+ e.getLocalizedMessage());
						} catch (TransformException e) {
							errors.add(Messages.ExportMapToImageWizard_failureMessage);
						} catch (NoninvertibleTransformException e) {
							errors.add(Messages.ExportMapToImageWizard_failureMessage);
						} catch (RuntimeException e) {
                            errors.add("An unexpected failure occurred: "+e.getLocalizedMessage());
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

		if( !errors.isEmpty() ){
			((WizardPage)getContainer().getCurrentPage()).setErrorMessage(Messages.ExportMapToImageWizard_ShowErrorMessage+errors.iterator().next());
			return false;
		}

		getDialogSettings().put(DIRECTORY_KEY,
				mapSelectorPage.getExportDir().getAbsolutePath());
		getDialogSettings().put(FORMAT_KEY, imageSettingsPage.getFormatName());

		return true;
	}

	private void exportMap(IMap map, IProgressMonitor monitor)
			throws RenderException, IOException, TransformException, NoninvertibleTransformException {

		monitor.beginTask(Messages.ExportMapToImageWizard_RenderingTaskName, 3);
		String pattern = Messages.ExportMapToImageWizard_preparingTaskName;
		Object[] args = new Object[]{map.getName()};
		monitor.setTaskName(MessageFormat.format(pattern, args));
		File destination = determineDestinationFile(map);
		if (destination == null) {
			return;
		}

		int width = imageSettingsPage.getWidth();
		int height = imageSettingsPage.getHeight(map.getViewportModel().getWidth(), map.getViewportModel().getHeight());

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = image.createGraphics();

		IMap renderedMap;
		try {
			monitor.worked(1);
			pattern = Messages.ExportMapToImageWizard_renderingTaskname;
			args = new Object[]{map.getName()};
			monitor.setTaskName(MessageFormat.format(pattern, args));
			int scaleDenom = MapSelectorPageWithScaleColumn.getScaleDenom(map);
			BoundsStrategy boundsStrategy = new BoundsStrategy(scaleDenom);
			DrawMapParameter drawMapParameter = new DrawMapParameter(g, new java.awt.Dimension(width, height), map, boundsStrategy , 72,
			        imageSettingsPage.getSelectionHandling(), monitor);
			renderedMap = ApplicationGIS.drawMap(drawMapParameter);
		} finally {
			g.dispose();
		}
		monitor.worked(1);
		pattern = Messages.ExportMapToImageWizard_writingTaskname;
		args = new Object[]{map.getName()};
		monitor.setTaskName(MessageFormat.format(pattern, args));
		imageSettingsPage.write(image, destination);
		String baseFile = destination.getPath().substring(0, destination.getPath().lastIndexOf(".")); //$NON-NLS-1$
		createWorldFile(renderedMap.getViewportModel().worldToScreenTransform().createInverse(), baseFile);
		createProjectionFile(baseFile, renderedMap.getViewportModel().getCRS());

		addToCatalog(destination);

		monitor.done();
	}

	private void addToCatalog(File destination) throws MalformedURLException {
		List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(destination.toURL());
		ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
		for (IService service : services) {
			addToCatalog(localCatalog, service);
		}
	}

	/**
	 * This method is responsible for creating a projection file using the WKT
	 * representation of this coverage's coordinate reference system. We can
	 * reuse this file in order to rebuild later the crs.
	 *
	 *
	 * @param baseFile
	 * @param coordinateReferenceSystem
	 * @throws IOException
	 */
	private void createProjectionFile(final String baseFile,
			final CoordinateReferenceSystem coordinateReferenceSystem)
			throws IOException {
	    try {
    		final File prjFile = new File(new StringBuffer(baseFile).append(".prj") //$NON-NLS-1$
    				.toString());
    		BufferedWriter out = new BufferedWriter(new FileWriter(prjFile));
    		out.write(coordinateReferenceSystem.toWKT());
    		out.close();
	    }
	    catch( Throwable ignore ){
	        // projection cannot be represented in WKT
	    }
	}

	/**
	 * This method is responsible fro creating a world file to georeference an
	 * image given the image bounding box and the image geometry. The name of
	 * the file is composed by the name of the image file with a proper
	 * extension, depending on the format (see WorldImageFormat). The projection
	 * is in the world file.
	 *
	 * @param gridToWorld
	 *            the transformation from the image to the world coordinates.
	 * @param baseFile
	 *            Basename and path for this image.
	 * @throws IOException
	 *             In case we cannot create the world file.
	 * @throws TransformException
	 * @throws TransformException
	 */
	private void createWorldFile(final AffineTransform gridToWorld,
			final String baseFile) throws IOException, TransformException {
		// /////////////////////////////////////////////////////////////////////
		//
		// CRS information
		//
		// ////////////////////////////////////////////////////////////////////
		final boolean lonFirst = (XAffineTransform.getSwapXY(gridToWorld) != -1);

		// /////////////////////////////////////////////////////////////////////
		//
		// World File values
		// It is worthwhile to note that we have to keep into account the fact
		// that the axis could be swapped (LAT,lon) therefore when getting
		// xPixSize and yPixSize we need to look for it a the right place
		// inside the grid to world transform.
		//
		// ////////////////////////////////////////////////////////////////////
		final double xPixelSize = (lonFirst) ? gridToWorld.getScaleX()
				: gridToWorld.getShearY();
		final double rotation1 = (lonFirst) ? gridToWorld.getShearX()
				: gridToWorld.getScaleX();
		final double rotation2 = (lonFirst) ? gridToWorld.getShearY()
				: gridToWorld.getScaleY();
		final double yPixelSize = (lonFirst) ? gridToWorld.getScaleY()
				: gridToWorld.getShearX();
		final double xLoc = gridToWorld.getTranslateX();
		final double yLoc = gridToWorld.getTranslateY();

		// /////////////////////////////////////////////////////////////////////
		//
		// writing world file
		//
		// ////////////////////////////////////////////////////////////////////
		final StringBuffer buff = new StringBuffer(baseFile);
		buff.append(".wld"); //$NON-NLS-1$
		final File worldFile = new File(buff.toString());
		final PrintWriter out = new PrintWriter(new FileOutputStream(worldFile));
		out.println(xPixelSize);
		out.println(rotation1);
		out.println(rotation2);
		out.println(yPixelSize);
		out.println(xLoc);
		out.println(yLoc);
		out.flush();
		out.close();

	}
    private void addToCatalog( ICatalog localCatalog, IService service ) {
        if( localCatalog.getById(IService.class, service.getIdentifier(), new NullProgressMonitor())!=null ){
            localCatalog.replace(service.getIdentifier(), service);
        }else{
            localCatalog.add(service);
        }
    }

	private File determineDestinationFile(IMap map) {
		File exportDir = mapSelectorPage.getExportDir();
		String name = URLUtils.cleanFilename(map.getName());
        File destination = addSuffix(new File(exportDir, name));
		if (destination.exists()) {
			boolean overwrite = !MessageDialog.openQuestion(getContainer()
					.getShell(), Messages.ExportMapToImageWizard_overwriteWarningTitle, Messages.ExportMapToImageWizard_overwriteWarningMessage
					+ destination.getName());

			if (!overwrite) {
				if (!destination.delete()) {
					destination = selectFile(destination,
							Messages.ExportMapToImageWizard_unableToDeleteMsg);
				}

			} else {
				destination = selectFile(destination, Messages.ExportMapToImageWizard_selectFileTitle);
			}
		}

		if (destination == null) {
			return null;
		}

		return addSuffix(destination);
	}

	private File addSuffix(File file) {
		String path = stripEndSlash(file.getPath());

		File destination;
		String extension = imageSettingsPage.getFormatExtension();
		if (!path.endsWith(extension)) {
			destination = new File(path + "." + extension); //$NON-NLS-1$
		} else {
			return file;
		}
		return destination;
	}

	private String stripEndSlash(String path) {
		if (path.endsWith("/")) //$NON-NLS-1$
			return stripEndSlash(path.substring(0, path.length() - 1));
		return path;
	}

	private File selectFile(File destination, String string) {
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

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		mapSelectorPage.setSelection(selection);
	}

}
