/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.tools.jgrass.featuremovieview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.factory.SelectionCommandFactory;
import net.refractions.udig.project.internal.command.navigation.ZoomCommand;
import net.refractions.udig.project.internal.commands.selection.SelectCommand;
import net.refractions.udig.project.ui.ApplicationGIS;

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
import org.geotools.filter.FidFilterImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import eu.udig.tools.jgrass.JGrassToolsPlugin;

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
    private Label currentFidValue;

    private String previousLayerName = "";

    public FeatureMovieView() {
        ImageDescriptor playImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/play.gif");
        playImage = playImageDescriptor.createImage();
        ImageDescriptor stopImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/stop.gif");
        stopImage = stopImageDescriptor.createImage();

    }

    public void createPartControl( Composite theparent ) {

        Composite parent = new Composite(theparent, SWT.NONE);
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        parent.setLayout(new GridLayout(1, true));

        Group playGroup = new Group(parent, SWT.NONE);
        playGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        playGroup.setLayout(new GridLayout(1, false));
        playGroup.setText("Commands");

        playButton = new Button(playGroup, SWT.PUSH);
        playButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        playButton.setText("start");
        playButton.setImage(playImage);
        playButton.addSelectionListener(new SelectionAdapter(){
            private CoordinateReferenceSystem crs;
            private SimpleFeatureIterator featureIterator;

            public void widgetSelected( SelectionEvent e ) {
                if (isRunning) {
                    // stop it
                    stop();

                } else {
                    // run it
                    start();

                    final IMap activeMap = ApplicationGIS.getActiveMap();
                    final ILayer selectedLayer = activeMap.getEditManager().getSelectedLayer();
                    if (selectedLayer != null) {
                        SimpleFeatureSource featureSource;
                        try {
                            String name = selectedLayer.getName();
                            if (featureIterator == null || !name.equals(previousLayerName) || !featureIterator.hasNext()) {
                                // restart
                                if (featureIterator != null) {
                                    featureIterator.close();
                                }
                                featureSource = (SimpleFeatureSource) selectedLayer.getResource(FeatureSource.class,
                                        new SubProgressMonitor(new NullProgressMonitor(), 1));
                                if (featureSource == null) {
                                    noProperLayerSelected();
                                    return;
                                }
                                SimpleFeatureCollection featureCollection = featureSource.getFeatures();
                                crs = featureCollection.getSchema().getCoordinateReferenceSystem();
                                featureIterator = featureCollection.features();
                                previousLayerName = name;
                            }

                            new Thread(new Runnable(){
                                public void run() {

                                    while( featureIterator.hasNext() && isRunning ) {
                                        SimpleFeature currentFeature = featureIterator.next();

                                        SimpleFeatureType featureType = currentFeature.getFeatureType();
                                        List<AttributeDescriptor> attributeDescriptors = featureType.getAttributeDescriptors();
                                        List<String> attributeNames = new ArrayList<String>();
                                        for( AttributeDescriptor attributeDescriptor : attributeDescriptors ) {
                                            String name = attributeDescriptor.getLocalName();
                                            attributeNames.add(name);
                                        }
                                        final StringBuilder sb = new StringBuilder();
                                        for( String name : attributeNames ) {
                                            Object attribute = currentFeature.getAttribute(name);
                                            if (attribute != null) {
                                                sb.append(name).append(" = ").append(attribute.toString()).append("\n");
                                            }
                                        }
                                        Display.getDefault().asyncExec(new Runnable(){
                                            public void run() {
                                                currentFidValue.setText(sb.toString());
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

                                        UndoableMapCommand selectCommand = SelectionCommandFactory.getInstance()
                                                .createFIDSelectCommand(selectedLayer, currentFeature);
                                        ZoomCommand zoomCommand = new ZoomCommand(ref);
                                        activeMap.sendCommandASync(selectCommand);
                                        activeMap.sendCommandASync(zoomCommand);
                                        activeMap.getRenderManager().refresh(null);
                                        try {
                                            Thread.sleep((long) (timer * 1000));
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

            private void noProperLayerSelected() {
                MessageDialog.openWarning(getSite().getShell(), "NO LAYER SELECTED",
                        "A feature layer needs to be selected to use the tool.");
                stop();
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
        infoGroup.setText("current Feature Info");

        currentFidValue = new Label(infoGroup, SWT.NONE);
        currentFidValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        currentFidValue.setText(" - ");
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

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

}
