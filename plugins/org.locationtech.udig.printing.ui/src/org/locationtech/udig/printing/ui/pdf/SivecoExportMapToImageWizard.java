/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2013, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.pdf;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.XMLMemento;
import org.geotools.data.DataUtilities;
import org.geotools.data.Query;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.util.URLs;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.core.internal.CorePlugin;
import org.locationtech.udig.mapgraphic.style.LocationStyleContent;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerFactory;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.ApplicationGIS.DrawMapParameter;
import org.locationtech.udig.project.ui.BoundsStrategy;
import org.locationtech.udig.project.ui.SelectionStyle;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.wizard.export.image.Image2Pdf;
import org.locationtech.udig.project.ui.wizard.export.image.ImageExportPage;
import org.locationtech.udig.project.ui.wizard.export.image.MapSelectorPageWithScaleColumn;
import org.locationtech.udig.project.ui.wizard.export.image.PDFImageExportFormat;
import org.locationtech.udig.project.ui.wizard.export.image.Paper;
import org.locationtech.udig.project.ui.wizard.export.image.XAffineTransform;
import org.locationtech.udig.style.sld.editor.DialogSettingsStyleContent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Wizard for exporting a collection of maps to a collection of images.  One for each map.
 * 
 * @author Not Jesse
 */
public class SivecoExportMapToImageWizard extends Wizard implements IExportWizard {

	public static final String DIRECTORY_KEY = "exportDirectoryKey"; //$NON-NLS-1$
	public static final String FORMAT_KEY = "exportFormatKey"; //$NON-NLS-1$
	public static final String WIDTH_KEY = "widthKey"; //$NON-NLS-1$
	public static final String HEIGHT_KEY = Messages.ExportMapToImageWizard_3;
    public static final String SELECTION = "SELECTION_HANDLING"; //$NON-NLS-1$

    private static final String FEATURE_ID_KEY = "FEATURE_ID";
    
	private ImageExportPage imageSettingsPage = new ImageExportPage();
	private MapSelectorPageWithScaleColumn mapSelectorPage = new MapSelectorPageWithScaleColumn("Map Select", null, null);

	public SivecoExportMapToImageWizard() {
		setWindowTitle(Messages.ExportMapToImageWizard_windowtitle);
		setDialogSettings(ProjectUIPlugin.getDefault().getDialogSettings());
		addPage(mapSelectorPage);
		addPage(imageSettingsPage);
		setNeedsProgressMonitor(true);
	}

	/**
	 * Utility method to export a given map to a PDF according to the
	 * passed parameters.
	 */
	public static void exportSivecoMap(IMap map, int dpi, Paper paper, File dest, SelectionStyle selectionStyle, IProgressMonitor monitor) 
	    throws RenderException, IOException, TransformException, NoninvertibleTransformException {
	    
	    final boolean isLandscape = true;
	    final int pageMarginPixels = 10; 
	    
	    //check preconditions
        if (map == null) {
            throw new IllegalArgumentException("map must not be null"); //$NON-NLS-1$
        }
	    if (dest == null) {
            throw new IllegalArgumentException("dest must not be null"); //$NON-NLS-1$
        }
       if (dpi <= 0) {
            throw new IllegalArgumentException("dpi must be > 0"); //$NON-NLS-1$
        }
        if (paper == null) {
            throw new IllegalArgumentException("paper must not be null"); //$NON-NLS-1$
        }	    
        
        monitor.beginTask(Messages.ExportMapToImageWizard_RenderingTaskName, 3);
        String pattern = Messages.ExportMapToImageWizard_preparingTaskName;
        Object[] args = new Object[]{map.getName()};
        monitor.setTaskName(MessageFormat.format(pattern, args));
        
        Dimension contentDim = 
            new Dimension(
                paper.getPixelWidth(isLandscape, dpi) - (2 * pageMarginPixels),
                paper.getPixelHeight(isLandscape, dpi) - (2 * pageMarginPixels)
                );
        
        BufferedImage image = new BufferedImage(contentDim.width, 
                                                contentDim.height,
                                                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();
        
        try {
            monitor.worked(1);
            pattern = Messages.ExportMapToImageWizard_renderingTaskname;
            args = new Object[]{map.getName()};
            monitor.setTaskName(MessageFormat.format(pattern, args));
            
            int scaleDenom = MapSelectorPageWithScaleColumn.getScaleDenom(map);
            BoundsStrategy boundsStrategy = new BoundsStrategy(scaleDenom);
            
            //final Map mapCopy = (Map) EcoreUtil.copy((EObject) map);
            final Map mapCopy = (Map) ApplicationGIS.copyMap(map);
            List<Layer> layersInternal = mapCopy.getLayersInternal();            
            List<Layer> mapGraphics = prepareMapGraphics(mapCopy, contentDim, monitor);
            layersInternal.addAll(mapGraphics);
            
            //block out the labels over my whitebox
            //context.getLabelS...()
            
            //add decorator with some basic information about the selected feature
            /*
            Rectangle detailsBox = new Rectangle(whiteboxLoc.x, whiteboxLoc.y, whiteboxLoc.width, (int)(whiteboxLoc.height * 0.25));
            addSelectedFeatureDetails(mapCopy, detailsBox);
            */
            
            DrawMapParameter drawMapParameter = 
                new DrawMapParameter(g, 
                                     new java.awt.Dimension(contentDim.width, contentDim.height), 
                                     mapCopy, 
                                     boundsStrategy, 
                                     dpi, 
                                     selectionStyle, 
                                     monitor);
            
            ApplicationGIS.drawMap(drawMapParameter);
        } 
        finally {
            g.dispose();
        }
        
        monitor.worked(1);
        pattern = Messages.ExportMapToImageWizard_writingTaskname;
        args = new Object[]{map.getName()};
        monitor.setTaskName(MessageFormat.format(pattern, args));
        
        Image2Pdf.write(image, 
                        dest.getAbsolutePath(), 
                        paper,
                        /* top, left, bottom and right margin*/
                        new Insets(10, 10, 10, 10),
                        isLandscape);
	    
        monitor.done();
        
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
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
							errors.add("Failed to create world file.  This image can not be used as a Raster file in uDig");
						} catch (NoninvertibleTransformException e) {
							errors.add("Failed to create world file.  This image can not be used as a Raster file in uDig");
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
		getDialogSettings().put(FORMAT_KEY, imageSettingsPage.getFormat().getName());

		return true;
	}

	private void exportMap(IMap map, IProgressMonitor monitor)
			throws RenderException, IOException, TransformException, NoninvertibleTransformException {

	    if (imageSettingsPage.isPDF()) {
	        exportSivecoMap(map, 
	                        imageSettingsPage.getFormat().getDPI(),
	                        ((PDFImageExportFormat) imageSettingsPage.getFormat()).paper(),
	                        determineDestinationFile(map),
	                        imageSettingsPage.getSelectionHandling(),
	                        monitor);
	        return;
	    }
	    
		monitor.beginTask(Messages.ExportMapToImageWizard_RenderingTaskName, 3);
		String pattern = Messages.ExportMapToImageWizard_preparingTaskName;
		Object[] args = new Object[]{map.getName()};
		monitor.setTaskName(MessageFormat.format(pattern, args));
		File destination = determineDestinationFile(map);
		if (destination == null) {
			return;
		}
		
		double mapheight = map.getViewportModel().getHeight();
        double mapwidth = map.getViewportModel().getWidth();
        int width = imageSettingsPage.getWidth(mapwidth, mapheight);
		int height = imageSettingsPage.getHeight(mapwidth, mapheight);

		
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
			
			//final Map mapCopy = (Map) EcoreUtil.copy((EObject) map);
			final Map mapCopy = (Map) ApplicationGIS.copyMap(map);
				
			DrawMapParameter drawMapParameter = 
			    new DrawMapParameter(g, 
			                         new java.awt.Dimension(width, height), 
			                         mapCopy, 
			                         boundsStrategy, 
			                         imageSettingsPage.getFormat().getDPI(), 
			                         imageSettingsPage.getSelectionHandling(), 
			                         monitor);
			
			renderedMap = ApplicationGIS.drawMap(drawMapParameter);
		} 
		finally {
			g.dispose();
		}
		monitor.worked(1);
		pattern = Messages.ExportMapToImageWizard_writingTaskname;
		args = new Object[]{map.getName()};
		monitor.setTaskName(MessageFormat.format(pattern, args));
		imageSettingsPage.getFormat().write(renderedMap, image, destination);
		String baseFile = destination.getPath().substring(0, destination.getPath().lastIndexOf("."));
		createWorldFile(renderedMap.getViewportModel().worldToScreenTransform().createInverse(), baseFile);
		createProjectionFile(baseFile, renderedMap.getViewportModel().getCRS());

		addToCatalog(destination);
		
		monitor.done();
	}

	private static List<Layer> prepareMapGraphics(Map map, Dimension contentDim, IProgressMonitor monitor) {
	    
	    List<Layer> mapGraphics = new ArrayList<Layer>();
	    
	    //whitebox
	    Rectangle whiteboxLoc = new Rectangle((int)(contentDim.width-(contentDim.width*0.25)),0,contentDim.width,contentDim.height);
	    Layer whiteboxLayer = createWhitebox(map, whiteboxLoc);
	    mapGraphics.add(whiteboxLayer);
	    
	    //title area
	    Rectangle titleAreaBox = new Rectangle(whiteboxLoc.x, whiteboxLoc.y, whiteboxLoc.width, (int)(whiteboxLoc.height * 0.25));
	    Layer titleAreaLayer = createTitleArea(map, titleAreaBox, monitor);
	    mapGraphics.add(titleAreaLayer);
	    
	    return mapGraphics;
	}
	
	/**
	 * Adds a whitebox mapgraphic as the top-most layer on the map.
	 * On failure nothing is added to the map.
	 * @param map
     * @param location represents the location the mapgraphic will sit on the map	 * 
	 */
	private static Layer createWhitebox(Map map, Rectangle location) {
	    try {

    	    ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();

            LayerFactory layerFactory = map.getLayerFactory();
            URL resourceID = new URL(null, "mapgraphic://localhost/mapgraphic#org.locationtech.udig.tutorial.mapgraphic.Whitebox", CorePlugin.RELAXED_HANDLER); //$NON-NLS-1$
            ID id = new ID(resourceID);
            IGeoResource resource = localCatalog.getById(IGeoResource.class, id, new NullProgressMonitor());
            Layer whiteboxLayer = layerFactory.createLayer(resource);
            
            whiteboxLayer.getStyleBlackboard().put(LocationStyleContent.ID, location);
            return whiteboxLayer;
	    }
	    catch (Exception e) {
	        throw new IllegalStateException("failed to create whitebox", e); //$NON-NLS-1$
	    }
   
	}
	
	/**
     * Adds a mapgraphic as the top-most layer on the map 
     * with details about the selected feature.
     * On failure nothing is added to the map.
     * @param map the map to add a new layer to
     * @param location represents the location the mapgraphic will sit on the map
     */
    protected static Layer createTitleArea(Map map, Rectangle location, IProgressMonitor monitor) {
        try {

            ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
            List<Layer> layersInternal = map.getLayersInternal();
            LayerFactory layerFactory = map.getLayerFactory();
            
            Iterator layerIterator = layersInternal.iterator();
            Layer farmParcelLayer = null; 
            while (layerIterator.hasNext()) {
                Layer layer = (Layer)layerIterator.next();
                if (layer.getName().equals("SDE.DATE_PRIMARE")) {
                    farmParcelLayer = layer;
                }
            }
            
            if (farmParcelLayer == null) {
                throw new IllegalStateException("cannot find farm layer");
            }
            

            //get title from selected feature of farm layer            
            Filter farmParcelFilter = farmParcelLayer.getFilter();
            Query query = new Query(farmParcelLayer.getSchema().getTypeName(), 
                                           farmParcelFilter, 
                                           new String[0]  ); //{"nume_com", "fbid"}
            
             FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = 
                farmParcelLayer.getResource(FeatureSource.class, 
                                            new SubProgressMonitor(monitor, 1));
            FeatureCollection<SimpleFeatureType, SimpleFeature>  features = featureSource.getFeatures(query);
            FeatureIterator<SimpleFeature> featureIterator = features.features();
            if (!featureIterator.hasNext()) {
                throw new IllegalStateException("At least one feature must be selected in the farm layer. "+features.size()+ " found.");
            }
            SimpleFeature selectedFarmFeature = featureIterator.next();
            String prov = (String)selectedFarmFeature.getAttribute(0);
            String id = (String)selectedFarmFeature.getAttribute(1);
            
            //put layer info on the mapgraphic's style blackboard
            URL detailsResId = new URL(null, "mapgraphic://localhost/mapgraphic#org.locationtech.udig.tutorial.mapgraphic.TitleArea", CorePlugin.RELAXED_HANDLER);
            IGeoResource detailsRes = localCatalog.getById(IGeoResource.class, new ID(detailsResId), new NullProgressMonitor());
             
            Layer titleAreaLayer = layerFactory.createLayer(detailsRes);
            titleAreaLayer.getStyleBlackboard().put(LocationStyleContent.ID, location);
            XMLMemento selectedFeatureMemento = XMLMemento.createWriteRoot("style");
            selectedFeatureMemento.putString(FEATURE_ID_KEY, id);
            titleAreaLayer.getStyleBlackboard().put(DialogSettingsStyleContent.EXTENSION_ID, selectedFeatureMemento);
            return titleAreaLayer;
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to create title area", e);
        }
        
        
    }
	
    /**
     * Adds the destination file to the catalog.
     *
     * @param destination
     * @throws MalformedURLException
     */
	private void addToCatalog(File destination) throws MalformedURLException {
		IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
		URL url = URLs.fileToUrl( destination );
        List<IService> services = serviceFactory.createService(url);
		ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();;
		for (IService service : services) {
			localCatalog.add(service);
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
		final File prjFile = new File(new StringBuffer(baseFile).append(".prj")
				.toString());
		BufferedWriter out = new BufferedWriter(new FileWriter(prjFile));
		out.write(coordinateReferenceSystem.toWKT());
		out.close();

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
		buff.append(".wld");
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
	private File determineDestinationFile(IMap map) {
		File exportDir = mapSelectorPage.getExportDir();
		File destination = addSuffix(new File(exportDir, map.getName()));
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
		String extension = imageSettingsPage.getFormat().getExtension();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@SuppressWarnings("unchecked")
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		mapSelectorPage.setSelection(selection);
	}

}
