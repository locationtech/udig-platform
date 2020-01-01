/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.featuremovieview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.command.factory.SelectionCommandFactory;
import org.locationtech.udig.project.internal.command.navigation.ZoomCommand;
import org.locationtech.udig.project.internal.commands.selection.SelectCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.FidFilterImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import org.locationtech.udig.tools.jgrass.JGrassToolsPlugin;

/**
 * A navigation view.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class FeatureMovieView extends ViewPart {

    private Image playImage;
    private Image stopImage;
    private Text zoomBufferText;
    private double zoomBuffer;
    private Text timerText;
    private double timer;

    private boolean isRunning = false;
    private Button playButton;
    private Label currentFeatureInfo;

    private String previousLayerName = "";

    private List<SimpleFeature> featureList;
    private int index = 0;
    private Label featureNumLabel;

    private IMap activeMap;
    private ILayer selectedLayer;
    private CoordinateReferenceSystem crs;
    private Image gotoImage;
    private Text gotoText;
    private Image nextImage;
    private Image previousImage;

    public FeatureMovieView() {
        ImageDescriptor playImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/play.gif");
        playImage = playImageDescriptor.createImage();
        ImageDescriptor stopImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/stop.gif");
        stopImage = stopImageDescriptor.createImage();
        ImageDescriptor gotoImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/goto.gif");
        gotoImage = gotoImageDescriptor.createImage();
        ImageDescriptor nextImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/shift_r_edit.gif");
        nextImage = nextImageDescriptor.createImage();
        ImageDescriptor previousImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/shift_l_edit.gif");
        previousImage = previousImageDescriptor.createImage();

    }

    public void createPartControl( Composite theparent ) {

        Composite parent = new Composite(theparent, SWT.NONE);
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        parent.setLayout(new GridLayout(1, true));

        Group playGroup = new Group(parent, SWT.NONE);
        playGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        playGroup.setLayout(new GridLayout(2, false));
        playGroup.setText("Commands");

        playButton = new Button(playGroup, SWT.PUSH);
        playButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        playButton.setText("start");
        playButton.setImage(playImage);
        playButton.addSelectionListener(new SelectionAdapter(){

            public void widgetSelected( SelectionEvent e ) {
                if (isRunning) {
                    // stop it
                    stop();

                } else {
                    // run it
                    start();

                    activeMap = ApplicationGIS.getActiveMap();
                    selectedLayer = activeMap.getEditManager().getSelectedLayer();
                    if (selectedLayer != null) {
                        try {
                            String name = selectedLayer.getName();
                            if (featureList == null || !name.equals(previousLayerName)) {
                                // restart
                                initLayer();
                            }

                            new Thread(new Runnable(){
                                public void run() {
                                    while( index < featureList.size() && isRunning ) {
                                        try {
                                            goToFeature();
                                            Thread.sleep((long) (timer * 1000));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        noProperLayerSelected();
                    }
                }
            }

        });

        featureNumLabel = new Label(playGroup, SWT.NONE);
        featureNumLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        featureNumLabel.setText(" - ");

        Button gotoButton = new Button(playGroup, SWT.PUSH);
        gotoButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        gotoButton.setText("goto");
        gotoButton.setImage(gotoImage);
        gotoButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                String text = gotoText.getText();
                int gotoInt = 1;
                try {
                    gotoInt = Integer.parseInt(text);
                    index = gotoInt - 1;

                    if (selectedLayer == null) {
                        initLayer();
                    }

                    int size = featureList.size();
                    if (index < 0 || index > size - 1) {
                        MessageDialog.openWarning(getSite().getShell(), "Wrong feature number", "The feature number range is: "
                                + 1 + " - " + size);
                        return;
                    }
                    goToFeature();
                } catch (Exception ex) {
                    gotoText.setText("");
                    ex.printStackTrace();
                }
            }
        });
        gotoText = new Text(playGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData gotoTextGD = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gotoTextGD.widthHint = 20;
        gotoText.setLayoutData(gotoTextGD);
        gotoText.setText("");

        Composite nextPreviousComposite = new Composite(playGroup, SWT.NONE);
        nextPreviousComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        GridLayout npGL = new GridLayout(2, true);
        npGL.marginWidth = 0;
        npGL.marginHeight = 0;
        nextPreviousComposite.setLayout(npGL);

        Button previousButton = new Button(nextPreviousComposite, SWT.PUSH);
        previousButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        previousButton.setText("previous");
        previousButton.setImage(previousImage);
        previousButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                try {
                    if (index == 1) {
                        return;
                    }
                    // -2 because later 1 is summed
                    index = index - 2;
                    if (selectedLayer == null) {
                        initLayer();
                    }
                    int size = featureList.size();
                    if (index < 0) {
                        index = 0;
                    }
                    if (index > size - 1) {
                        index = size - 1;
                    }
                    goToFeature();
                } catch (Exception ex) {
                    gotoText.setText("");
                    ex.printStackTrace();
                }
            }
        });
        Button nextButton = new Button(nextPreviousComposite, SWT.PUSH);
        nextButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        nextButton.setText("next");
        nextButton.setImage(nextImage);
        nextButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                try {
                    if (selectedLayer == null) {
                        initLayer();
                    }
                    int size = featureList.size();
                    if (index == size - 1) {
                        return;
                    }
                    // index stays the same, since later +1 is added
                    // index = index + 1;
                    if (index < 0) {
                        index = 0;;
                    }
                    if (index > size - 1) {
                        index = size - 1;
                    }
                    goToFeature();
                } catch (Exception ex) {
                    gotoText.setText("");
                    ex.printStackTrace();
                }
            }
        });

        Group paramsGroup = new Group(parent, SWT.NONE);
        paramsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        paramsGroup.setLayout(new GridLayout(2, true));
        paramsGroup.setText("Parameters");

        Label zoomBufferLabel = new Label(paramsGroup, SWT.NONE);
        zoomBufferLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        zoomBufferLabel.setText("Zoom buffer around feature");

        zoomBufferText = new Text(paramsGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        zoomBufferText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        zoomBufferText.setText("");
        zoomBufferText.addModifyListener(new ModifyListener(){
            public void modifyText( ModifyEvent e ) {
                updateZoomBuffer();
            }
        });
        updateZoomBuffer();

        Label timerLabel = new Label(paramsGroup, SWT.NONE);
        timerLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        timerLabel.setText("Timer interval in seconds");

        timerText = new Text(paramsGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        timerText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        timerText.setText("");
        timerText.addModifyListener(new ModifyListener(){
            public void modifyText( ModifyEvent e ) {
                updateTimer();
            }
        });
        updateTimer();

        Group infoGroup = new Group(parent, SWT.NONE);
        infoGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        infoGroup.setLayout(new GridLayout(1, true));
        infoGroup.setText("Current Feature Info");

        currentFeatureInfo = new Label(infoGroup, SWT.NONE);
        currentFeatureInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        currentFeatureInfo.setText(" - ");
    }

    private void initLayer() throws IOException {
        activeMap = ApplicationGIS.getActiveMap();
        selectedLayer = activeMap.getEditManager().getSelectedLayer();
        SimpleFeatureSource featureSource = (SimpleFeatureSource) selectedLayer.getResource(FeatureSource.class,
                new SubProgressMonitor(new NullProgressMonitor(), 1));
        if (featureSource == null) {
            noProperLayerSelected();
            return;
        }
        SimpleFeatureCollection featureCollection = featureSource.getFeatures();
        crs = featureCollection.getSchema().getCoordinateReferenceSystem();
        featureList = featureCollectionToList(featureCollection);
        previousLayerName = selectedLayer.getName();
        index = 0;
    }

    private void noProperLayerSelected() {
        MessageDialog.openWarning(getSite().getShell(), "NO LAYER SELECTED",
                "A feature layer needs to be selected to use the tool.");
        stop();
    }

    private void goToFeature() throws IOException {
        if (featureList == null) {
            initLayer();
        }
        SimpleFeature currentFeature = featureList.get(index);

        SimpleFeatureType featureType = currentFeature.getFeatureType();
        List<AttributeDescriptor> attributeDescriptors = featureType.getAttributeDescriptors();
        List<String> attributeNames = new ArrayList<String>();
        for( AttributeDescriptor attributeDescriptor : attributeDescriptors ) {
            String name = attributeDescriptor.getLocalName();
            attributeNames.add(name);
        }
        final StringBuilder infoSb = new StringBuilder();
        for( String name : attributeNames ) {
            Object attribute = currentFeature.getAttribute(name);
            if (attribute != null) {
                infoSb.append(name).append(" = ").append(attribute.toString()).append("\n");
            }
        }
        final StringBuilder numSb = new StringBuilder();
        numSb.append("  (");
        numSb.append(index + 1);
        numSb.append("/");
        numSb.append(featureList.size());
        numSb.append(")");
        index++;
        if (index == featureList.size()) {
            index = 0;
        }

        Display.getDefault().asyncExec(new Runnable(){
            public void run() {
                currentFeatureInfo.setText(infoSb.toString());
                featureNumLabel.setText(numSb.toString());
            }
        });

        Geometry geometry = (Geometry) currentFeature.getDefaultGeometry();
        Envelope envelope = geometry.getEnvelopeInternal();
        envelope.expandBy(zoomBuffer);
        ReferencedEnvelope ref = new ReferencedEnvelope(envelope, crs);
        try {
            ref = ref.transform(activeMap.getViewportModel().getCRS(), true);
        } catch (Exception e1) {
            // ignore
        }

        UndoableMapCommand selectCommand = SelectionCommandFactory.getInstance().createFIDSelectCommand(selectedLayer,
                currentFeature);
        ZoomCommand zoomCommand = new ZoomCommand(ref);
        activeMap.sendCommandASync(selectCommand);
        activeMap.sendCommandASync(zoomCommand);
        activeMap.getRenderManager().refresh(null);
    }

    private synchronized void start() {
        playButton.setImage(stopImage);
        playButton.setText("stop");
        isRunning = true;
    }
    private synchronized void stop() {
        playButton.setImage(playImage);
        playButton.setText("start");
        isRunning = false;
    }

    private void updateTimer() {
        String text = timerText.getText();
        try {
            timer = Double.parseDouble(text);
        } catch (Exception e) {
            timer = 4.0;
            timerText.setText(timer + "");
        }
    }

    private void updateZoomBuffer() {
        String text = zoomBufferText.getText();
        try {
            zoomBuffer = Double.parseDouble(text);
        } catch (Exception e) {
            zoomBuffer = 10.0;
            zoomBufferText.setText(zoomBuffer + "");
        }
    }

    /**
     * Extracts features from a {@link FeatureCollection} into an {@link ArrayList}.
     * 
     * @param collection the feature collection.
     * @return the list with the features or an empty list if no features present.
     */
    private List<SimpleFeature> featureCollectionToList( SimpleFeatureCollection collection ) {
        List<SimpleFeature> featuresList = new ArrayList<SimpleFeature>();
        if (collection == null) {
            return featuresList;
        }
        SimpleFeatureIterator featureIterator = collection.features();
        while( featureIterator.hasNext() ) {
            SimpleFeature feature = featureIterator.next();
            featuresList.add(feature);
        }
        featureIterator.close();
        return featuresList;
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

}
