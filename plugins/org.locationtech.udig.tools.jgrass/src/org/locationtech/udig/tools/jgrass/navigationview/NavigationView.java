/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.navigationview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.FeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IMapCompositionListener;
import org.locationtech.udig.project.IMapListener;
import org.locationtech.udig.project.MapCompositionEvent;
import org.locationtech.udig.project.MapEvent;
import org.locationtech.udig.project.command.factory.NavigationCommandFactory;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.tools.jgrass.JGrassToolsPlugin;
import org.locationtech.udig.ui.ExceptionDetailsDialog;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * A navigation view.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class NavigationView extends ViewPart implements SelectionListener, IMapListener, IMapCompositionListener {

    private static DateTimeFormatter ISO_DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

    private static DateTimeFormatter ISO_DATE_TIME_PARSER = ISODateTimeFormat.dateTimeParser().withZone(DateTimeZone.UTC);

    private Image worldImage;
    private Text lowerLeftText;
    private Text upperRightText;
    private Canvas canvas;
    private Combo scaleCombo;
    private Combo dateTimeCombo;
    private Combo verticalCombo;

    private DecimalFormat numFormatter = new DecimalFormat("0.00");
    private Button verticalDownButton;
    private Button verticalUpButton;
    private Button scaleDownButton;
    private Button scaleUpButton;
    private Button dtUpButton;
    private Button dtDownButton;
    private Color color;

    private static final String ENTER_SEARCH_STRING = "enter search string";
    private Set<String> keySet;
    private Combo placesCombo;
    private HashMap<String, Coordinate> placesMap;

    private File folderFile;

    private File first;

    private Combo countriesCombo;

    public NavigationView() {
        ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/worldoverview2.png");
        worldImage = imageDescriptor.createImage();

        color = Display.getDefault().getSystemColor(SWT.COLOR_RED);

    }

    public void createPartControl( Composite theparent ) {

        ScrolledComposite scrolledComposite = new ScrolledComposite(theparent, SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        Composite parent = new Composite(scrolledComposite, SWT.NONE);
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        parent.setLayout(new GridLayout(1, true));

        org.eclipse.swt.graphics.Point point = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        parent.setSize(point);
        scrolledComposite.setMinSize(280, 575);
        scrolledComposite.setContent(parent);

        Group boundsGroup = new Group(parent, SWT.NONE);
        boundsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        boundsGroup.setLayout(new GridLayout(2, false));
        boundsGroup.setText("Corners");

        Label lowerLeftLabel = new Label(boundsGroup, SWT.NONE);
        lowerLeftLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        lowerLeftLabel.setText("Lower left (w,s)");
        lowerLeftText = new Text(boundsGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        lowerLeftText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        lowerLeftText.setText("");
        lowerLeftText.setEditable(false);

        Label upperRightLabel = new Label(boundsGroup, SWT.NONE);
        upperRightLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        upperRightLabel.setText("Upper right (e,n)");
        upperRightText = new Text(boundsGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        upperRightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        upperRightText.setText("");
        upperRightText.setEditable(false);

        Group scaleGroup = new Group(parent, SWT.NONE);
        scaleGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        scaleGroup.setLayout(new GridLayout(3, false));
        scaleGroup.setText("Scale");
        scaleDownButton = new Button(scaleGroup, SWT.ARROW | SWT.DOWN);
        scaleDownButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        scaleDownButton.addSelectionListener(this);
        scaleCombo = new Combo(scaleGroup, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
        scaleCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        scaleCombo.addSelectionListener(this);
        scaleUpButton = new Button(scaleGroup, SWT.ARROW | SWT.UP);
        scaleUpButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        scaleUpButton.addSelectionListener(this);

        Group dateTimeGroup = new Group(parent, SWT.NONE);
        dateTimeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        dateTimeGroup.setLayout(new GridLayout(3, false));
        dateTimeGroup.setText("Date and Time");
        dtDownButton = new Button(dateTimeGroup, SWT.ARROW | SWT.DOWN);
        dtDownButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        dtDownButton.addSelectionListener(this);
        dateTimeCombo = new Combo(dateTimeGroup, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
        dateTimeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        dateTimeCombo.addSelectionListener(this);
        dtUpButton = new Button(dateTimeGroup, SWT.ARROW | SWT.UP);
        dtUpButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        dtUpButton.addSelectionListener(this);

        Group verticalGroup = new Group(parent, SWT.NONE);
        verticalGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        verticalGroup.setLayout(new GridLayout(3, false));
        verticalGroup.setText("Vertical axis");
        verticalDownButton = new Button(verticalGroup, SWT.ARROW | SWT.DOWN);
        verticalDownButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        verticalDownButton.addSelectionListener(this);
        verticalCombo = new Combo(verticalGroup, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
        verticalCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        verticalCombo.addSelectionListener(this);
        verticalUpButton = new Button(verticalGroup, SWT.ARROW | SWT.UP);
        verticalUpButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        verticalUpButton.addSelectionListener(this);

        Group overviewGroups = new Group(parent, SWT.NONE);
        GridData gd1 = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        overviewGroups.setLayoutData(gd1);
        overviewGroups.setLayout(new GridLayout(1, false));
        overviewGroups.setText("Overview");

        canvas = new Canvas(overviewGroups, SWT.NONE);
        GridData gd2 = new GridData(SWT.FILL, SWT.FILL, false, false);
        gd2.widthHint = 250;
        gd2.heightHint = 121;
        canvas.setLayoutData(gd2);
        canvas.addPaintListener(new PaintListener(){

            public void paintControl( PaintEvent e ) {
                Rectangle canvasBounds = canvas.getBounds();
                Rectangle imageBounds = worldImage.getBounds();
                int h = (int) ((float) canvasBounds.width * (float) imageBounds.height / (float) imageBounds.width);
                e.gc.drawImage(worldImage, 0, 0, imageBounds.width, imageBounds.height, 0, 0, canvasBounds.width, h);

                IMap activeMap = ApplicationGIS.getActiveMap();
                ViewportModel viewportModel = (ViewportModel) activeMap.getViewportModel();
                ReferencedEnvelope bounds = viewportModel.getBounds();

                CoordinateReferenceSystem mapCrs = viewportModel.getCRS();
                CoordinateReferenceSystem imageCrs = DefaultGeographicCRS.WGS84;
                try {
                    MathTransform transform = CRS.findMathTransform(mapCrs, imageCrs);
                    Envelope targetEnv = JTS.transform(bounds, transform);
                    double west = targetEnv.getMinX();
                    double north = targetEnv.getMaxY();
                    double east = targetEnv.getMaxX();
                    double south = targetEnv.getMinY();

                    if (west < -180) {
                        west = -180;
                    }
                    if (west > 180) {
                        west = 180;
                    }
                    if (north < -90) {
                        north = -90;
                    }
                    if (north > 90) {
                        north = 90;
                    }
                    if (east < -180) {
                        east = -180;
                    }
                    if (east > 180) {
                        east = 180;
                    }
                    if (south < -90) {
                        south = -90;
                    }
                    if (south > 90) {
                        south = 90;
                    }
                    west = 180.0 + west;
                    north = 90.0 + north;
                    east = 180.0 + east;
                    south = 90.0 + south;
                    double width = east - west;
                    double height = north - south;
                    if (width < 1) {
                        width = 1;
                    }
                    if (height < 1) {
                        height = 1;
                    }

                    int x = (int) ((double) canvasBounds.width * west / 360.0);
                    int y = (int) ((double) h * north / 180.0);
                    int fw = (int) ((double) canvasBounds.width * width / 360.0);
                    if (fw <= 1)
                        fw = 2;
                    int fh = (int) ((double) h * height / 180.0);
                    if (fh <= 1)
                        fh = 2;
                    int newy = h - y;
                    e.gc.setForeground(color);
                    e.gc.setBackground(color);
                    e.gc.setAlpha(80);
                    e.gc.fillRectangle(x, newy, fw, fh);

                    e.gc.drawLine(x + fw / 2, 0, x + fw / 2, newy);
                    // e.gc.drawLine(x + fw / 2, newy + fh, x + fw / 2, h);
                    //
                    e.gc.drawLine(0, newy + fh / 2, x, newy + fh / 2);
                    e.gc.drawLine(x + fw, newy + fh / 2, canvasBounds.width, newy + fh / 2);

                } catch (FactoryException e1) {
                    e1.printStackTrace();
                } catch (TransformException e1) {
                    e1.printStackTrace();
                }

            }
        });

        // Label fillLabel = new Label(parent, SWT.NONE);
        // fillLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Group geonamesGroup = new Group(parent, SWT.NONE);
        geonamesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        geonamesGroup.setLayout(new GridLayout(4, false));
        geonamesGroup.setText("Geonames");

        Label availableLabel = new Label(geonamesGroup, SWT.NONE);
        availableLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        availableLabel.setText("Available geonames data");

        URL folderUrl = Platform.getBundle(JGrassToolsPlugin.PLUGIN_ID).getResource("/geonamesfiles");
        String folderPath = null;
        try {
            folderPath = FileLocator.toFileURL(folderUrl).getPath();
            folderFile = new File(folderPath);

            String[] namesArray = loadGeonamesFiles();

            countriesCombo = new Combo(geonamesGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
            countriesCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            countriesCombo.setItems(namesArray);
            countriesCombo.select(0);
            countriesCombo.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected( SelectionEvent e ) {
                    int selectionIndex = countriesCombo.getSelectionIndex();
                    String item = countriesCombo.getItem(selectionIndex);
                    final String file = folderFile.getAbsolutePath() + File.separator + item + ".txt";

                    IRunnableWithProgress operation = new IRunnableWithProgress(){
                        public void run( IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {
                            try {
                                populatePlacesMap(placesMap, file);
                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    };
                    PlatformGIS.runInProgressDialog("Loading geonames data...", true, operation, true);
                }
            });

            Button addNewButton = new Button(geonamesGroup, SWT.PUSH);
            addNewButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
            addNewButton.setText("+");
            addNewButton.setToolTipText("Add a new geonames file (get it at http://download.geonames.org/export/dump/)");
            addNewButton.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected( SelectionEvent e ) {
                    FileDialog fileDialog = new FileDialog(countriesCombo.getShell(), SWT.OPEN);
                    fileDialog.setFilterExtensions(new String[]{"*.txt"});
                    String newFilePath = fileDialog.open();
                    try {
                        if (newFilePath != null) {
                            File newFile = new File(newFilePath);
                            if (newFile.exists()) {
                                File copiedFile = new File(folderFile, newFile.getName());
                                FileUtils.copyFile(newFile, copiedFile);
                            }
                        }
                        String[] geonamesFiles = loadGeonamesFiles();
                        if (geonamesFiles.length > 0) {
                            countriesCombo.setItems(geonamesFiles);
                            countriesCombo.select(0);
                        } else {
                            countriesCombo.setItems(new String[]{"   --   "});
                        }
                    } catch (IOException e1) {
                        String message = "An error occurred while copying the new geonames file into the application.";
                        ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassToolsPlugin.PLUGIN_ID, e1);
                        e1.printStackTrace();
                    }
                }
            });

            Button removeButton = new Button(geonamesGroup, SWT.PUSH);
            removeButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
            removeButton.setText("-");
            removeButton.setToolTipText("Remove a geonames file");
            removeButton.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected( SelectionEvent e ) {
                    int selectionIndex = countriesCombo.getSelectionIndex();
                    String item = countriesCombo.getItem(selectionIndex);

                    try {
                        File namesFile = new File(folderFile, item + ".txt");
                        if (namesFile.exists()) {
                            FileUtils.forceDelete(namesFile);
                        }
                        String[] geonamesFiles = loadGeonamesFiles();
                        if (geonamesFiles.length > 0) {
                            countriesCombo.setItems(geonamesFiles);
                            countriesCombo.select(0);
                        } else {
                            countriesCombo.setItems(new String[]{"   --   "});
                        }
                    } catch (IOException e1) {
                        String message = "An error occurred while removing the old geonames file into the application.";
                        ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassToolsPlugin.PLUGIN_ID, e1);
                        e1.printStackTrace();
                    }
                }
            });

            Group placesGroup = new Group(geonamesGroup, SWT.NONE);
            GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
            layoutData.horizontalSpan = 4;
            placesGroup.setLayoutData(layoutData);
            placesGroup.setLayout(new GridLayout(2, false));
            placesGroup.setText("places");

            placesMap = new HashMap<String, Coordinate>(1000);
            populatePlacesMap(placesMap, first.getAbsolutePath());
            keySet = placesMap.keySet();

            final Text placesText = new Text(placesGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
            placesText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            placesText.setText("");

            Button searchButton = new Button(placesGroup, SWT.PUSH);
            searchButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
            searchButton.setText("search");
            searchButton.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected( SelectionEvent e ) {
                    String text = placesText.getText();
                    if (text.length() < 3) {
                        MessageBox msgBox = new MessageBox(placesText.getShell(), SWT.ICON_WARNING);
                        msgBox.setMessage("At least 3 letters are needed to do a search.");
                        msgBox.open();
                        return;
                    }

                    List<String> matchedList = new ArrayList<String>();
                    for( String name : keySet ) {
                        if (name.toLowerCase().matches(".*" + text.toLowerCase() + ".*")) {
                            matchedList.add(name);
                        }
                    }
                    String[] matchedArray = (String[]) matchedList.toArray(new String[matchedList.size()]);
                    Arrays.sort(matchedArray);
                    placesCombo.setItems(matchedArray);
                    placesCombo.select(0);
                }
            });

            placesCombo = new Combo(placesGroup, SWT.DROP_DOWN);
            placesCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            placesCombo.setItems(new String[]{ENTER_SEARCH_STRING});

            Button goButton = new Button(placesGroup, SWT.PUSH);
            goButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
            goButton.setText("go");
            goButton.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected( SelectionEvent e ) {
                    IMap map = ApplicationGIS.getActiveMap();
                    if (map.getMapLayers().size() < 1) {
                        return;
                    }

                    ReferencedEnvelope bounds = map.getViewportModel().getBounds();
                    CoordinateReferenceSystem mapCrs = map.getViewportModel().getCRS();

                    try {
                        GeometryFactory gF = new GeometryFactory();
                        CoordinateReferenceSystem placeCrs = CRS.decode("EPSG:4326");
                        // transform coordinates before check
                        MathTransform transform = CRS.findMathTransform(placeCrs, mapCrs, true);
                        // jts geometry
                        int selectionIndex = placesCombo.getSelectionIndex();
                        String item = placesCombo.getItem(selectionIndex);
                        Coordinate coordinate = placesMap.get(item);
                        Point pt = gF.createPoint(coordinate);
                        Geometry targetGeometry = JTS.transform(pt, transform);
                        Coordinate position = targetGeometry.getCoordinate();

                        Coordinate centre = bounds.centre();
                        double xTrans = position.x - centre.x;
                        double yTrans = position.y - centre.y;

                        bounds.translate(xTrans, yTrans);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    map.sendCommandASync(NavigationCommandFactory.getInstance().createSetViewportBBoxCommand(bounds));
                }
            });

            Button loadShapeButton = new Button(placesGroup, SWT.PUSH);
            GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
            gridData.horizontalSpan = 2;
            loadShapeButton.setLayoutData(gridData);
            loadShapeButton.setText("Load all places as feature layer");
            loadShapeButton.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected( SelectionEvent e ) {

                    IRunnableWithProgress operation = new IRunnableWithProgress(){

                        public void run( IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {

                            try {
                                GeometryFactory gF = new GeometryFactory();
                                // create the feature type
                                SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
                                b.setName("places");
                                b.setCRS(DefaultGeographicCRS.WGS84);
                                b.add("the_geom", MultiPoint.class);
                                b.add("name", String.class);
                                SimpleFeatureType type = b.buildFeatureType();

                                DefaultFeatureCollection newCollection = new DefaultFeatureCollection();

                                int size = keySet.size();

                                pm.beginTask("Converting geometries of places...", size);
                                int id = 0;
                                for( String name : keySet ) {
                                    Coordinate coordinate = placesMap.get(name);
                                    MultiPoint point = gF.createMultiPoint(new Coordinate[]{coordinate});
                                    SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
                                    Object[] values = new Object[]{point, name};
                                    builder.addAll(values);
                                    SimpleFeature feature = builder.buildFeature(type.getTypeName() + "." + id);
                                    id++;
                                    newCollection.add(feature);
                                    pm.worked(1);
                                }
                                pm.done();

                                IGeoResource resource = CatalogPlugin.getDefault().getLocalCatalog()
                                        .createTemporaryResource(type);

                                resource.resolve(FeatureStore.class, pm).addFeatures(newCollection);
                                ApplicationGIS.addLayersToMap(ApplicationGIS.getActiveMap(), Collections.singletonList(resource),
                                        -1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                String message = "An error occurred while loading the places to map";
                                ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassToolsPlugin.PLUGIN_ID, e);

                            }

                        }

                    };

                    PlatformGIS.runInProgressDialog("Loading places in temporary layer...", true, operation, true);

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        updateData();

    }

    private String[] loadGeonamesFiles() {
        File[] listFiles = folderFile.listFiles();

        List<String> names = new ArrayList<String>();
        for( int i = 0; i < listFiles.length; i++ ) {
            String name = listFiles[i].getName();
            if (name.startsWith(".svn")) {
                continue;
            }
            if (first == null) {
                first = listFiles[i];
            }
            names.add(name.replaceFirst(".txt", ""));
        }
        String[] namesArray = (String[]) names.toArray(new String[names.size()]);
        return namesArray;
    }

    private void updateData() {
        Display.getDefault().asyncExec(new Runnable(){
            public void run() {
                IMap activeMap = ApplicationGIS.getActiveMap();
                ViewportModel viewportModel = (ViewportModel) activeMap.getViewportModel();
                activeMap.addMapListener(NavigationView.this);
                activeMap.addMapCompositionListener(NavigationView.this);

                ReferencedEnvelope bounds = viewportModel.getBounds();

                double west = bounds.getMinX();
                double east = bounds.getMaxX();
                double north = bounds.getMaxY();
                double south = bounds.getMinY();

                if (lowerLeftText.isDisposed()) {
                    return;
                }
                lowerLeftText.setText(numFormatter.format(west) + ", " + numFormatter.format(south));
                upperRightText.setText(numFormatter.format(east) + ", " + numFormatter.format(north));

                canvas.redraw();

                SortedSet<Double> preferredScaleDenominators = viewportModel.getPreferredScaleDenominators();
                Double[] scalesArray = (Double[]) preferredScaleDenominators.toArray(new Double[preferredScaleDenominators.size()]);
                String[] scales = new String[scalesArray.length];
                for( int i = 0; i < scales.length; i++ ) {
                    scales[i] = "1:" + String.valueOf(scalesArray[i]);
                }
                int itemCount = scaleCombo.getItemCount();
                int selectionIndex = scaleCombo.getSelectionIndex();
                scaleCombo.setItems(scales);
                if (scales.length == itemCount) {
                    scaleCombo.select(selectionIndex);
                }

                List<DateTime> availableTimesteps = viewportModel.getAvailableTimesteps();
                if (availableTimesteps != null) {
                    dateTimeCombo.setEnabled(true);
                    dtDownButton.setEnabled(true);
                    dtUpButton.setEnabled(true);
                    String[] dates = new String[availableTimesteps.size()];
                    for( int i = 0; i < dates.length; i++ ) {
                        dates[i] = ISO_DATE_TIME_FORMATTER.print(availableTimesteps.get(i));
                    }
                    itemCount = dateTimeCombo.getItemCount();
                    selectionIndex = dateTimeCombo.getSelectionIndex();
                    dateTimeCombo.setItems(dates);
                    if (dates.length == itemCount) {
                        dateTimeCombo.select(selectionIndex);
                    }
                } else {
                    dateTimeCombo.setEnabled(false);
                    dtDownButton.setEnabled(false);
                    dtUpButton.setEnabled(false);
                }

                List<Double> availableElevation = viewportModel.getAvailableElevation();
                if (availableElevation != null) {
                    verticalCombo.setEnabled(true);
                    verticalDownButton.setEnabled(true);
                    verticalUpButton.setEnabled(true);
                    String[] elev = new String[availableElevation.size()];
                    for( int i = 0; i < elev.length; i++ ) {
                        elev[i] = String.valueOf(availableElevation.get(i));
                    }
                    itemCount = verticalCombo.getItemCount();
                    selectionIndex = verticalCombo.getSelectionIndex();
                    verticalCombo.setItems(elev);
                    if (elev.length == itemCount) {
                        verticalCombo.select(selectionIndex);
                    }
                } else {
                    verticalCombo.setEnabled(false);
                    verticalDownButton.setEnabled(false);
                    verticalUpButton.setEnabled(false);
                }
            }
        });
    }

    public void widgetSelected( SelectionEvent e ) {
        ViewportModel viewportModel = (ViewportModel) ApplicationGIS.getActiveMap().getViewportModel();

        Object source = e.getSource();
        if (source.equals(scaleCombo)) {
            int index = scaleCombo.getSelectionIndex();
            String item = scaleCombo.getItem(index);
            String scaleString = item.split(":")[1];
            double scale = Double.parseDouble(scaleString);
            viewportModel.setScale(scale);
        }
        if (source.equals(dateTimeCombo)) {
            int index = dateTimeCombo.getSelectionIndex();
            String item = dateTimeCombo.getItem(index);
            DateTime date = ISO_DATE_TIME_PARSER.parseDateTime(item);
            viewportModel.setCurrentTimestep(date);
        }
        if (source.equals(verticalCombo)) {
            int index = verticalCombo.getSelectionIndex();
            String item = verticalCombo.getItem(index);
            try {
                double vertical = Double.parseDouble(item);
                viewportModel.setCurrentElevation(vertical);
            } catch (NumberFormatException e1) {
                e1.printStackTrace();
            }
        }
        if (source.equals(verticalDownButton)) {
            int selectionIndex = verticalCombo.getSelectionIndex();
            selectionIndex++;
            if (selectionIndex > verticalCombo.getItemCount() - 1) {
                selectionIndex = verticalCombo.getItemCount() - 1;
            }
            String item = verticalCombo.getItem(selectionIndex);
            double vertical = Double.parseDouble(item);
            viewportModel.setCurrentElevation(vertical);
            verticalCombo.select(selectionIndex);
        }
        if (source.equals(verticalUpButton)) {
            int selectionIndex = verticalCombo.getSelectionIndex();
            selectionIndex--;
            if (selectionIndex < 0) {
                selectionIndex = 0;
            }
            String item = verticalCombo.getItem(selectionIndex);
            double vertical = Double.parseDouble(item);
            viewportModel.setCurrentElevation(vertical);
            verticalCombo.select(selectionIndex);
        }
        if (source.equals(scaleDownButton)) {
            int selectionIndex = scaleCombo.getSelectionIndex();
            selectionIndex--;
            if (selectionIndex < 0) {
                selectionIndex = 0;
            }
            String item = scaleCombo.getItem(selectionIndex);
            double scale = Double.parseDouble(item.split(":")[1]);
            viewportModel.setScale(scale);
            scaleCombo.select(selectionIndex);
        }
        if (source.equals(scaleUpButton)) {
            int selectionIndex = scaleCombo.getSelectionIndex();
            selectionIndex++;
            if (selectionIndex > scaleCombo.getItemCount() - 1) {
                selectionIndex = scaleCombo.getItemCount() - 1;
            }
            String item = scaleCombo.getItem(selectionIndex);
            double scale = Double.parseDouble(item.split(":")[1]);
            viewportModel.setScale(scale);
            scaleCombo.select(selectionIndex);
        }
        if (source.equals(dtDownButton)) {
            int selectionIndex = dateTimeCombo.getSelectionIndex();
            selectionIndex--;
            if (selectionIndex < 0) {
                selectionIndex = 0;
            }
            String item = dateTimeCombo.getItem(selectionIndex);
            DateTime dt = ISO_DATE_TIME_PARSER.parseDateTime(item);
            viewportModel.setCurrentTimestep(dt);
            dateTimeCombo.select(selectionIndex);
        }
        if (source.equals(dtUpButton)) {
            int selectionIndex = dateTimeCombo.getSelectionIndex();
            selectionIndex++;
            if (selectionIndex > dateTimeCombo.getItemCount() - 1) {
                selectionIndex = dateTimeCombo.getItemCount() - 1;
            }
            String item = dateTimeCombo.getItem(selectionIndex);
            DateTime dt = ISO_DATE_TIME_PARSER.parseDateTime(item);
            viewportModel.setCurrentTimestep(dt);
            dateTimeCombo.select(selectionIndex);
        }
        updateData();
    }

    public void changed( MapEvent event ) {
        updateData();
    }

    public void changed( MapCompositionEvent event ) {
        updateData();
    }

    public void setFocus() {
        updateData();
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
    }

    private void populatePlacesMap( HashMap<String, Coordinate> placesMap, String fileToRead ) throws FileNotFoundException,
            IOException {
        BufferedReader bR = new BufferedReader(new FileReader(fileToRead));
        String line = null;
        while( (line = bR.readLine()) != null ) {
            String[] lineSplit = line.split("\t");
            String name = lineSplit[1];
            String lat = lineSplit[4];
            String lon = lineSplit[5];

            Coordinate c = new Coordinate(Double.parseDouble(lon), Double.parseDouble(lat));
            placesMap.put(name, c);
        }
        bR.close();
    }
}
