/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.export;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.IllegalFilterException;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.geotools.referencing.wkt.UnformattableObjectException;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.internal.ui.CatalogView;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.ProgressManager;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;

public class CatalogExportWizard extends WorkflowWizard implements IExportWizard {

    private boolean select = true;

    public CatalogExportWizard( Workflow workflow,
            Map<Class< ? extends State>, WorkflowWizardPageProvider> map ) {
        super(workflow, map);
        setWindowTitle(Messages.CatalogExportWizard_WindowTitle);
    }

    /**
     * If true this will open the catalog view and select the exported items in the catalog view.
     * This is true by default.
     * 
     * @param select if true the exported items will be selected in the catalog view
     */
    public void setSelectExportedInCatalog( boolean select ) {
        this.select = select;
    }

    @Override
    public IDialogSettings getDialogSettings() {
        return CatalogUIPlugin.getDefault().getDialogSettings();
    }

    @Override
    protected boolean performFinish( IProgressMonitor monitor ) {
        ExportResourceSelectionState layerSelectState = getWorkflow().getState(
                ExportResourceSelectionState.class);

        if (layerSelectState == null)
            return false;

        List<Data> resources = layerSelectState.getExportData();
        boolean success = true;

        monitor.beginTask(Messages.CatalogExport_taskName, resources.size() + 1);
        monitor.worked(1);

        // for each layer, export
        for( Data data : resources ) {
            SubProgressMonitor currentMonitor = new SubProgressMonitor(monitor, 1);
            boolean isExported;

            isExported = exportResource(data, currentMonitor);
            if (!isExported) {
                success = false;
            }
        }
        if (success) {
            selectInCatalog();
        }
        monitor.done();
        if (success) {
            getDialogSettings().put(ExportResourceSelectionPage.DIRECTORY_KEY,
                    findState().getExportDir());
        }
        return success;

    }

    /**
     * Export data, currently focused on Features.
     * 
     * @param monitor
     * @param data
     * @return success
     */
    @SuppressWarnings("unchecked")
    private boolean exportResource( Data data, IProgressMonitor monitor ) {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        final WizardDialog wizardDialog = (WizardDialog) getContainer();
        IGeoResource resource = data.getResource();

        try {
            SimpleFeatureSource fs = resource.resolve(SimpleFeatureSource.class, null);
            SimpleFeatureCollection fc = fs.getFeatures(data.getQuery());

            // TODO: remove from catalog/close layers if open?
            SimpleFeatureType schema = fs.getSchema();
            if (data.getName() != null) {
                SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
                builder.init(schema);
                builder.setName(data.getName());
                schema = builder.buildFeatureType();
            }

            File file = determineDestinationFile(data);

            monitor.beginTask("", IProgressMonitor.UNKNOWN); //$NON-NLS-1$

            CoordinateReferenceSystem fromCRS = schema.getCoordinateReferenceSystem();
            CoordinateReferenceSystem crs = data.getCRS();
            MathTransform mt;

            if (fromCRS != null && crs != null) {
                mt = CRS.findMathTransform(fromCRS, crs, true);
            } else {
                if (crs != null)
                    mt = IdentityTransform.create(crs.getCoordinateSystem().getDimension());
                else if (fromCRS != null)
                    mt = IdentityTransform.create(fromCRS.getCoordinateSystem().getDimension());
                else
                    mt = IdentityTransform.create(2);

            }

            if (isAbstractGeometryType(schema)) {
                // possibly multiple geometry types
                String geomName = schema.getGeometryDescriptor().getName().getLocalPart();

                DefaultFeatureCollection pointCollection = new DefaultFeatureCollection();
                DefaultFeatureCollection lineCollection = new DefaultFeatureCollection();
                DefaultFeatureCollection polygonCollection = new DefaultFeatureCollection();

                FeatureIterator<SimpleFeature> featureIterator = fc.features();
                while( featureIterator.hasNext() ) {
                    SimpleFeature feature = featureIterator.next();
                    String geometryType = ((Geometry) feature.getDefaultGeometry())
                            .getGeometryType();

                    if (geometryType.endsWith("Point")) {
                        pointCollection.add(feature);
                    } else if (geometryType.endsWith("LineString")) {
                        lineCollection.add(feature);
                    } else if (geometryType.endsWith("Polygon")) {
                        polygonCollection.add(feature);
                    }
                }

                if (polygonCollection.size() > 0) {
                    exportPolygonFeatures(data, monitor, file, polygonCollection, schema, mt);
                }
                if (pointCollection.size() > 0) {
                    exportPointFeatures(data, monitor, file, pointCollection, schema, mt);
                }
                if (lineCollection.size() > 0) {
                    exportLineFeatures(data, monitor, file, lineCollection, schema, mt);
                }
            } else {
                // single geometry type
                SimpleFeatureType destinationFeatureType = createFeatureType(schema,
                        (Class< ? extends Geometry>) schema.getGeometryDescriptor().getType()
                                .getBinding(), crs);

                ShapefileDataStore shapefile = createShapefile(destinationFeatureType, file, data.getCharset());
                SimpleFeatureType targetFeatureType = shapefile.getSchema();

                ReprojectingFeatureCollection processed = new ReprojectingFeatureCollection(fc, monitor, targetFeatureType, mt);
                boolean success = writeToShapefile(shapefile, processed);

                if (success) {
                    addToCatalog(file, data);
                } else {
                    Display.getDefault().asyncExec(new Runnable(){
                        public void run() {
                            String msg = "No features were exported; did you select anything?"; //$NON-NLS-1$
                            CatalogUIPlugin.log(msg, null);
                            wizardDialog.setErrorMessage(msg);
                        }
                    });
                    return false;
                }
            }
        } catch (IOException e) {
            String msg = MessageFormat.format(Messages.CatalogExport_layerFail, resource
                    .getIdentifier());
            CatalogUIPlugin.log(msg, e);
            wizardDialog.setErrorMessage(msg);
            return false;
        } catch (IllegalFilterException e) {
            String msg = MessageFormat.format(Messages.CatalogExport_layerFail, resource
                    .getIdentifier());
            CatalogUIPlugin.log(msg, e);
            wizardDialog.setErrorMessage(msg);
            return false;
        } catch (SchemaException e) {
            String msg = MessageFormat.format(Messages.CatalogExport_layerFail, resource
                    .getIdentifier());
            CatalogExport.setError(wizardDialog, msg, e);
            return false;
        } catch (FactoryException e) {
            String msg = resource.getIdentifier() + Messages.CatalogExport_reprojectError;
            CatalogExport.setError(wizardDialog, msg, e);
            return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e.getCause() instanceof TransformException) {
                String msg = resource.getIdentifier() + Messages.CatalogExport_reprojectError;
                CatalogExport.setError(wizardDialog, msg, e);
            } else {
                String msg = MessageFormat.format(Messages.CatalogExport_layerFail, resource
                        .getIdentifier());
                CatalogExport.setError(wizardDialog, msg, e);
            }
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private File determineDestinationFile( Data data ) {
        ExportResourceSelectionState layerSelectState = findState();
        File exportDir = new File(layerSelectState.getExportDir());
        String typeName = data.getName();
        try {
            if (typeName == null) {
                FeatureSource<SimpleFeatureType, SimpleFeature> source;
                source = data.getResource().resolve(FeatureSource.class, new NullProgressMonitor());
                typeName = source.getSchema().getTypeName();
            }
            typeName = URLUtils.cleanFilename(typeName);

            final File[] destination = new File[]{addSuffix(new File(exportDir, typeName))};
            if (destination[0].exists()) {
                getContainer().getShell().getDisplay().syncExec(new Runnable(){
                    public void run() {
                        String pattern = Messages.CatalogExportWizard_OverwriteDialogQuery;

                        String message = MessageFormat.format(pattern, destination[0].getName());

                        boolean overwrite = !MessageDialog.openQuestion(getContainer().getShell(),
                                Messages.CatalogExportWizard_0, message);

                        if (!overwrite) {
                            if (!destination[0].delete()) {
                                destination[0] = selectFile(destination[0],
                                        Messages.CatalogExportWizard_UnableToDelete);
                            }

                        } else {
                            destination[0] = selectFile(destination[0],
                                    Messages.CatalogExportWizard_SelectFile);
                        }
                    }
                });

            }

            if (destination[0] == null) {
                return null;
            }

            return addSuffix(destination[0]);
        } catch (IOException e) {
            CatalogPlugin.log("Stupidly got a IOException", e); //$NON-NLS-1$
            throw new RuntimeException(e);
        }

    }

    private File addSuffix( File file ) {
        String path = stripEndSlash(file.getPath());

        File destination;
        String extension = "shp"; //$NON-NLS-1$
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

    private ExportResourceSelectionState findState() {
        State[] states = getWorkflow().getStates();
        for( State state : states ) {
            if (ExportResourceSelectionState.class.isAssignableFrom(state.getClass())) {
                return (ExportResourceSelectionState) state;
            }
        }
        return getWorkflow().getState(ExportResourceSelectionState.class);
    }

    private void selectInCatalog() {
        if (!select) {
            // return if the option to show the selection is false.
            return;
        }

        // ok we will show selection (if we can)

        ExportResourceSelectionState layerSelectState = getWorkflow().getState(
                ExportResourceSelectionState.class);
        List<Data> resources = layerSelectState.getExportData();

        final List<IGeoResource> exported = new ArrayList<IGeoResource>(resources.size());

        for( Data data : resources ) {
            exported.addAll(data.getExportedResources());
        }

        PlatformGIS.asyncInDisplayThread(new Runnable(){
            public void run() {
                CatalogView catalogView = getCatalogView();
                if (catalogView != null) {
                    catalogView.getSite().getPage().activate(catalogView);
                    catalogView.getTreeviewer().setSelection(toTreePathSelection(exported), true);
                }
            }

            private ISelection toTreePathSelection( List<IGeoResource> exported ) {

                List<TreePath> paths = new ArrayList<TreePath>(exported.size());

                for( IGeoResource resource : exported ) {
                    paths.add(new TreePath(toTreePath(resource).toArray()));
                }
                return new TreeSelection(paths.toArray(new TreePath[paths.size()]));
            }

            private List<IResolve> toTreePath( IGeoResource resource ) {
                IResolve parent;
                try {
                    parent = resource.parent(ProgressManager.instance().get());
                } catch (IOException e) {
                    CatalogUIPlugin.log("Error resolving the parent", e); //$NON-NLS-1$
                    parent = null;
                }
                List<IResolve> path = new ArrayList<IResolve>();

                if (parent instanceof IGeoResource) {
                    IGeoResource parentResource = (IGeoResource) parent;
                    path.addAll(toTreePath(parentResource));
                } else if (parent instanceof IService) {
                    IService service = (IService) parent;
                    path.add(service);
                }

                path.add(resource);
                return path;
            }
        }, true);

    }

    private CatalogView getCatalogView() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if (window == null) {
            return null;
        }

        IWorkbenchPage page = window.getActivePage();

        if (page == null) {
            return null;
        }

        try {
            IViewPart catalogView = page.showView(CatalogView.VIEW_ID);
            return (CatalogView) catalogView;
        } catch (PartInitException e) {
            CatalogUIPlugin.log("Uh oh problem opening the catalog view", e); //$NON-NLS-1$
            return null;
        }
    }

    private void exportLineFeatures( Data data, IProgressMonitor currentMonitor, File file,
            SimpleFeatureCollection lineCollection,
            SimpleFeatureType schema, MathTransform mt )
            throws IllegalFilterException, IOException, SchemaException, MalformedURLException {

        File lineFile = addFileNameSuffix(file, LINE_SUFFIX);
        ShapefileDataStore shapefile = createShapefile(createFeatureType(schema, MultiLineString.class, data.getCRS()), lineFile, data.getCharset());
        SimpleFeatureType targetFeatureType = shapefile.getSchema();

        SimpleFeatureCollection temp = new ToMultiLineFeatureCollection(
                                                lineCollection, targetFeatureType, 
                                                schema.getGeometryDescriptor(), 
                                                mt, currentMonitor);

        if (writeToShapefile(shapefile, temp))
            addToCatalog(lineFile, data);
    }

    private void exportPointFeatures( Data data, IProgressMonitor currentMonitor, File file,
            SimpleFeatureCollection pointCollection,
            SimpleFeatureType schema, MathTransform mt )
            throws IllegalFilterException, IOException, SchemaException, MalformedURLException {

        File pointFile = addFileNameSuffix(file, POINT_SUFFIX);
        ShapefileDataStore shapefile = createShapefile(createFeatureType(schema, MultiPoint.class, data.getCRS()), pointFile, data.getCharset());
        SimpleFeatureType targetFeatureType = shapefile.getSchema();

        SimpleFeatureCollection temp = new ToMultiPointFeatureCollection(
                                                pointCollection, targetFeatureType, 
                                                schema.getGeometryDescriptor(), 
                                                mt, currentMonitor);

        if (writeToShapefile(shapefile, temp))
            addToCatalog(pointFile, data);
    }

    private void exportPolygonFeatures( Data data, IProgressMonitor currentMonitor, File file,
            SimpleFeatureCollection polygonCollection,
            SimpleFeatureType schema, MathTransform mt )
            throws IllegalFilterException, IOException, SchemaException, MalformedURLException {

        File polyFile = addFileNameSuffix(file, POLY_SUFFIX);
        ShapefileDataStore shapefile = createShapefile(createFeatureType(schema, MultiPolygon.class, data.getCRS()), polyFile, data.getCharset());
        SimpleFeatureType targetFeatureType = shapefile.getSchema();

        SimpleFeatureCollection temp = new ToMultiPolygonFeatureCollection( 
                polygonCollection, targetFeatureType,
                schema.getGeometryDescriptor(), mt, currentMonitor);

        if (writeToShapefile(shapefile, temp))
            addToCatalog(polyFile, data);
    }

    private SimpleFeatureType createFeatureType( SimpleFeatureType schema,
            Class< ? extends Geometry> geomBinding, CoordinateReferenceSystem crs )
            throws SchemaException {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("type"); //$NON-NLS-1$

        try {
            crs.toWKT();
        } catch (UnformattableObjectException e) {
            // cannot create WKT for crs so that means that it is probably a hard coded CRS so
            // we will not use it.
            crs = null;
        }

        for( int i = 0; i < schema.getAttributeCount(); i++ ) {
            AttributeDescriptor attribute = schema.getDescriptor(i);
            if (!(attribute instanceof GeometryDescriptor)) {
                builder.add(attribute);
            } else {
                GeometryDescriptor geom = schema.getGeometryDescriptor();
                builder.crs(crs).defaultValue(null).restrictions(geom.getType().getRestrictions())
                        .nillable(geom.isNillable()).add(geom.getLocalName(), geomBinding);
            }
        }

        return builder.buildFeatureType();
    }

    /**
     * Connect to the file (using the existing catalog entry if avaiable) and
     * add 
     * 
     * @param file
     * @param data
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void addToCatalog( File file, Data data ) throws IOException {
        IProgressMonitor monitor = ProgressManager.instance().get();

        // add the service to the catalog
        IServiceFactory sFactory = CatalogPlugin.getDefault().getServiceFactory();
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        URL url = URLUtils.fileToURL(file);
        List<IService> services = sFactory.createService(url);
        monitor.beginTask("add to catalog", services.size() * 10);
        for( IService service : services ) {
            IService registeredService = catalog.add(service);
            SubProgressMonitor monitor2 = new SubProgressMonitor(monitor, 10);
            List<IGeoResource> contents = (List<IGeoResource>) registeredService.resources(monitor2);
            data.addNewResources( contents );
        }
    }

    @SuppressWarnings("unchecked")
    /**
     * Returns true if a generic Geometry class; so we cannot tell if it is point / line / polygon.
     */
    private boolean isAbstractGeometryType( SimpleFeatureType schema ) {
        Class geometryType = schema.getGeometryDescriptor().getType().getBinding();

        return geometryType.isAssignableFrom(Geometry.class);
        // return Geometry.class.isAssignableFrom(geometryType);
        // return Geometry.class==geometryType;
    }

    static String SHAPEFILE_EXT = ".shp"; //$NON-NLS-1$
    static String POLY_SUFFIX = "_poly"; //$NON-NLS-1$
    static String POINT_SUFFIX = "_point"; //$NON-NLS-1$
    static String LINE_SUFFIX = "_line"; //$NON-NLS-1$

    private File addFileNameSuffix( File file, String suffix ) {
        String path = file.getPath();
        path = path.replaceAll(POLY_SUFFIX + SHAPEFILE_EXT, SHAPEFILE_EXT);
        path = path.replaceAll(POINT_SUFFIX + SHAPEFILE_EXT, SHAPEFILE_EXT);
        path = path.replaceAll(LINE_SUFFIX + SHAPEFILE_EXT, SHAPEFILE_EXT);

        if (path.toLowerCase().endsWith(SHAPEFILE_EXT)) {
            String start = path.substring(0, path.length() - 4);
            String end = path.substring(path.length() - 4);
            return new File(start + suffix + end);
        }

        return null;
    }

    private org.opengis.filter.Filter createGeometryTypeFilter( String geomName, String type )
            throws IllegalFilterException {
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        Function function = ff.function("geometryType", ff.property(geomName));
        return ff.equal(function, ff.literal(type)); //$NON-NLS-1$
    }
    
    
    
    private ShapefileDataStore createShapefile( SimpleFeatureType targetType, File file, Charset charset) throws IOException {
        
        if (!canWrite(file)) {
            throw new IOException(MessageFormat.format(Messages.CatalogExport_cannotWrite, file
                    .getAbsolutePath()));
        }
        // creates the shapefile
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", URLUtils.fileToURL(file)); //$NON-NLS-1$
        params.put("create spatial index", Boolean.TRUE); //$NON-NLS-1$
        ShapefileDataStore ds = (ShapefileDataStore) dataStoreFactory.createDataStore(params);
        if (charset != null) {
            ds.setCharset(charset);
        }
        ds.createSchema(targetType);
        
        
        return ds;
        
    }

    private boolean writeToShapefile(ShapefileDataStore shapefile, SimpleFeatureCollection fc) throws IOException {
        SimpleFeatureStore featureSource = (SimpleFeatureStore) shapefile.getFeatureSource();
        List<FeatureId> ids = featureSource.addFeatures(fc);
        return ids.size() >= 0;
    }

    private boolean canWrite( File file ) throws IOException {
        if (file.exists()) {
            return file.canWrite();
        } else {
            return file.createNewFile();
        }
    }

    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        getWorkflow().getState(ExportResourceSelectionState.class).selection = selection;
    }
}
