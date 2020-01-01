/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.ExceptionDetailsDialog;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gce.grassraster.JGrassConstants;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import org.locationtech.udig.catalog.jgrass.utils.JGrassCatalogUtilities;
import org.locationtech.udig.tools.jgrass.JGrassToolsPlugin;
import org.locationtech.udig.tools.jgrass.i18n.Messages;

/**
 * Csv to feature layer import wizard.
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class CsvImportWizard extends Wizard implements INewWizard {

    private CsvImportWizardPage mainPage;

    private final Map<String, String> params = new HashMap<String, String>();
    public static boolean canFinish = true;

    public CsvImportWizard() {
        super();
    }

    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        setWindowTitle(Messages.getString("CsvImportWizard.fileimport")); //$NON-NLS-1$
        setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/workset_wiz.png")); //$NON-NLS-1$
        setNeedsProgressMonitor(true);
        mainPage = new CsvImportWizardPage(Messages.getString("CsvImportWizard.csvimport"), params); //$NON-NLS-1$
    }

    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

    public boolean performFinish() {

        final CoordinateReferenceSystem crs = mainPage.getCrs();
        final File csvFile = mainPage.getCsvFile();
        final String separator = mainPage.getSeparator();
        final LinkedHashMap<String, Integer> fieldsAndTypesIndex = mainPage.getFieldsAndTypesIndex();

        /*
         * run with backgroundable progress monitoring
         */
        IRunnableWithProgress operation = new IRunnableWithProgress(){

            public void run( IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {

                if (!csvFile.exists()) {
                    MessageDialog.openError(getShell(), "Import error",
                            Messages.getString("CsvImportWizard.inputnotexist") + csvFile.getAbsolutePath()); //$NON-NLS-1$ 
                    return;
                }

                try {
                    FeatureCollection<SimpleFeatureType, SimpleFeature> csvFileFeatureCollection = csvFileToFeatureCollection(
                            csvFile, crs, fieldsAndTypesIndex, separator, pm);

                    SimpleFeatureType featureType = csvFileFeatureCollection.getSchema();
                    JGrassCatalogUtilities.removeMemoryServiceByTypeName(featureType.getTypeName());
                    IGeoResource resource = CatalogPlugin.getDefault().getLocalCatalog().createTemporaryResource(featureType);

                    SimpleFeatureStore resolve = (SimpleFeatureStore) resource.resolve(FeatureStore.class, pm);
                    resolve.addFeatures(csvFileFeatureCollection);
                    ApplicationGIS.addLayersToMap(ApplicationGIS.getActiveMap(), Collections.singletonList(resource), -1);

                } catch (Exception e) {
                    e.printStackTrace();
                    String message = Messages.getString("CsvImportWizard.error"); //$NON-NLS-1$
                    ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassToolsPlugin.PLUGIN_ID, e);
                }

            }

        };

        PlatformGIS.runInProgressDialog("Importing data to feature layer", true, operation, true);

        return true;
    }

    public boolean canFinish() {
        return canFinish;
    }

    /**
     * <p>
     * Convert a csv file to a FeatureCollection. 
     * <b>This for now supports only point geometries</b>.<br>
     * For different crs it also performs coor transformation.
     * </p>
     * <p>
     * <b>NOTE: this doesn't support date attributes</b>
     * </p>
     * 
     * @param csvFile the csv file.
     * @param crs the crs to use.
     * @param fieldsAndTypes the {@link Map} of filed names and {@link JGrassConstants#CSVTYPESARRAY types}.
     * @param pm progress monitor.
     * @param separatorthe separator to use, if null, comma is used.
     * @return the created {@link FeatureCollection}
     * @throws Exception
     */
    @SuppressWarnings("nls")
    public static SimpleFeatureCollection csvFileToFeatureCollection( File csvFile, CoordinateReferenceSystem crs,
            LinkedHashMap<String, Integer> fieldsAndTypesIndex, String separator, IProgressMonitor pm ) throws Exception {
        GeometryFactory gf = new GeometryFactory();
        Map<String, Class< ? >> typesMap = JGrassConstants.CSVTYPESCLASSESMAP;
        String[] typesArray = JGrassConstants.CSVTYPESARRAY;

        if (separator == null) {
            separator = ",";
        }

        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
        b.setName("csvimport");
        b.setCRS(crs);
        b.add("the_geom", Point.class);

        int xIndex = -1;
        int yIndex = -1;
        Set<String> fieldNames = fieldsAndTypesIndex.keySet();
        String[] fieldNamesArray = (String[]) fieldNames.toArray(new String[fieldNames.size()]);
        for( int i = 0; i < fieldNamesArray.length; i++ ) {
            String fieldName = fieldNamesArray[i];
            Integer typeIndex = fieldsAndTypesIndex.get(fieldName);

            if (typeIndex == 0) {
                xIndex = i;
            } else if (typeIndex == 1) {
                yIndex = i;
            } else {
                Class<?> class1 = typesMap.get(typesArray[typeIndex]);
                b.add(fieldName, class1);
            }
        }
        SimpleFeatureType featureType = b.buildFeatureType();

        DefaultFeatureCollection newCollection = new DefaultFeatureCollection(); 
        Collection<Integer> orderedTypeIndexes = fieldsAndTypesIndex.values();
        Integer[] orderedTypeIndexesArray = (Integer[]) orderedTypeIndexes.toArray(new Integer[orderedTypeIndexes.size()]);

        BufferedReader bR = null;
        try {
            bR = new BufferedReader(new FileReader(csvFile));
            String line = null;
            int featureId = 0;
            pm.beginTask("Importing raw data", -1);
            while( (line = bR.readLine()) != null ) {
                pm.worked(1);
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }

                SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
                Object[] values = new Object[fieldNames.size() - 1];

                String[] lineSplit = line.split(separator);
                double x = Double.parseDouble(lineSplit[xIndex]);
                double y = Double.parseDouble(lineSplit[yIndex]);
                Point point = gf.createPoint(new Coordinate(x, y));
                values[0] = point;

                int objIndex = 1;
                for( int i = 0; i < lineSplit.length; i++ ) {
                    if (i == xIndex || i == yIndex) {
                        continue;
                    }

                    String value = lineSplit[i];
                    int typeIndex = orderedTypeIndexesArray[i];
                    String typeName = typesArray[typeIndex];
                    if (typeName.equals(typesArray[3])) {
                        values[objIndex] = value;
                    } else if (typeName.equals(typesArray[4])) {
                        values[objIndex] = new Double(value);
                    } else if (typeName.equals(typesArray[5])) {
                        values[objIndex] = new Integer(value);
                    } else {
                        throw new IllegalArgumentException("An undefined value type was found");
                    }
                    objIndex++;
                }
                builder.addAll(values);

                SimpleFeature feature = builder.buildFeature(featureType.getTypeName() + "." + featureId);
                featureId++;
                newCollection.add(feature);
            }
        } finally {
            if (bR != null)
                bR.close();
            pm.done();
        }

        return newCollection;
    }
}
