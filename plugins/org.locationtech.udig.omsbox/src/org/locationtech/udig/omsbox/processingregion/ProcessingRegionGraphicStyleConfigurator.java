/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.processingregion;

import static java.lang.Math.round;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.internal.impl.UDIGFeatureStore;
import org.locationtech.udig.style.IStyleConfigurator;
import org.locationtech.udig.ui.ColorEditor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.coverage.grid.GridCoverage;

import org.locationtech.jts.geom.Envelope;

import org.locationtech.udig.catalog.jgrass.activeregion.dialogs.FeatureChooserDialog;
import org.locationtech.udig.omsbox.ui.CoverageChooserDialog;
import org.locationtech.udig.omsbox.utils.OmsBoxUtils;

public class ProcessingRegionGraphicStyleConfigurator extends IStyleConfigurator implements SelectionListener, ModifyListener {

    private static final int bound_type = 0;
    private static final int row_type = 1;
    private static final int res_type = 2;

    private Label northLabel = null;
    private Text northText = null;
    private Label rowsLabel = null;
    private Text rowsText = null;
    private Label southLabel = null;
    private Text southText = null;
    private Label colsLabel = null;
    private Text colsText = null;
    private Label westLabel = null;
    private Text westText = null;
    private Label xresLabel = null;
    private Text xresText = null;
    private Label eastLabel = null;
    private Text eastText = null;
    private Label yresLabel = null;
    private Text yresText = null;
    private Button rasterMapSetButton = null;
    private Button featuresMapSetButton = null;

    private ColorEditor backgroundColour;
    private ColorEditor foregroundColor;
    private Text backgroundAlphaText;
    private Text forgroundAlphaText;
    private ProcessingRegionStyle style = null;
    private boolean isWorking = false;
    private StyleBlackboard blackboard;

    @SuppressWarnings("nls")
    public void createControl( Composite parent ) {
        parent.setLayout(new GridLayout());
        ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrollComposite.setMinHeight(100);
        scrollComposite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
        Composite c = new Composite(scrollComposite, SWT.None);
        c.setLayout(new GridLayout());
        c.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

        FontData fd = c.getFont().getFontData()[0];
        fd.setStyle(SWT.BOLD);
        final Font boldFont = new Font(c.getDisplay(), fd);
        c.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				boldFont.dispose();
			}
		});
        // the group for the region
        Group regionGroup = new Group(c, SWT.NONE);
        regionGroup.setFont(boldFont);
        GridLayout layout2 = new GridLayout(2, false);
        regionGroup.setLayout(layout2);
        regionGroup.setText("Region Settings");
        regionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        northLabel = new Label(regionGroup, SWT.NONE);
        northLabel.setText("north");
//        northLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        northText = new Text(regionGroup, SWT.BORDER);
        northText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        southLabel = new Label(regionGroup, SWT.NONE);
        southLabel.setText("south");
//        southLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        southText = new Text(regionGroup, SWT.BORDER);
        southText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        westLabel = new Label(regionGroup, SWT.NONE);
        westLabel.setText("west");
//        westLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        westText = new Text(regionGroup, SWT.BORDER);
        westText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        eastLabel = new Label(regionGroup, SWT.NONE);
        eastLabel.setText("east");
//        eastLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        eastText = new Text(regionGroup, SWT.BORDER);
        eastText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        rowsLabel = new Label(regionGroup, SWT.NONE);
        rowsLabel.setText("rows");
//        rowsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        rowsText = new Text(regionGroup, SWT.BORDER);
        rowsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        colsLabel = new Label(regionGroup, SWT.NONE);
        colsLabel.setText("cols");
//        colsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        colsText = new Text(regionGroup, SWT.BORDER);
        colsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        xresLabel = new Label(regionGroup, SWT.NONE);
        xresLabel.setText("xres");
//        xresLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        xresText = new Text(regionGroup, SWT.BORDER);
        xresText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        yresLabel = new Label(regionGroup, SWT.NONE);
        yresLabel.setText("yres");
//        yresLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        yresText = new Text(regionGroup, SWT.BORDER);
        yresText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        // the group for the set region to map
        final Group settoGroup = new Group(regionGroup, SWT.NONE);
        GridLayout layout4 = new GridLayout(1, true);
        settoGroup.setLayout(layout4);
        settoGroup.setText("Set region to...");
        settoGroup.setLayoutData(new GridData( SWT.FILL, SWT.FILL, true, false, 2, 1));

        rasterMapSetButton = new Button(settoGroup, SWT.NONE);
        rasterMapSetButton.setText("set region to raster map");
        rasterMapSetButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        rasterMapSetButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                CoverageChooserDialog tree = new CoverageChooserDialog();
                tree.open(settoGroup.getShell(), SWT.SINGLE);

                update(tree.getSelectedResources());
            }
        });
        featuresMapSetButton = new Button(settoGroup, SWT.NONE);
        featuresMapSetButton.setText("set region to vector map");
        featuresMapSetButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        featuresMapSetButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FeatureChooserDialog tree = new FeatureChooserDialog();
                tree.open(settoGroup.getShell(), SWT.SINGLE);

                update(tree.getSelectedResources());
            }
        });
        
        // the group for the style
        Group styleGroup = new Group(c, SWT.NONE);
        styleGroup.setFont(boldFont);
        GridLayout layout3 = new GridLayout(2, false);
        styleGroup.setLayout(layout3);
        styleGroup.setText("Style Properties");
        styleGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        Label backgroundColourLabel = new Label(styleGroup, SWT.NONE);
//        backgroundColourLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        backgroundColourLabel.setText("background color");
        backgroundColour = new ColorEditor(styleGroup);
        backgroundColour.getButton().setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Label backgroundAlphaLabel = new Label(styleGroup, SWT.NONE);
//        backgroundAlphaLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        backgroundAlphaLabel.setText("background alpha (0-1)");
        backgroundAlphaText = new Text(styleGroup, SWT.BORDER);
        backgroundAlphaText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Label foregroundColourLabel = new Label(styleGroup, SWT.NONE);
//        foregroundColourLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        foregroundColourLabel.setText("foreground color");
        foregroundColor = new ColorEditor(styleGroup);
        foregroundColor.getButton().setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Label forgroundAlphaLabel = new Label(styleGroup, SWT.NONE);
//        forgroundAlphaLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        forgroundAlphaLabel.setText("foreground alpha (0-1)");
        forgroundAlphaText = new Text(styleGroup, SWT.BORDER);
        forgroundAlphaText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

      

        northText.addModifyListener(new org.eclipse.swt.events.ModifyListener(){
            public void modifyText( org.eclipse.swt.events.ModifyEvent e ) {
                if (isWorking)
                    return;
                textModified(bound_type);
            }
        });
        southText.addModifyListener(new org.eclipse.swt.events.ModifyListener(){
            public void modifyText( org.eclipse.swt.events.ModifyEvent e ) {
                if (isWorking)
                    return;
                textModified(bound_type);
            }
        });
        rowsText.addModifyListener(new org.eclipse.swt.events.ModifyListener(){
            public void modifyText( org.eclipse.swt.events.ModifyEvent e ) {
                if (isWorking)
                    return;
                textModified(row_type);
            }
        });
        colsText.addModifyListener(new org.eclipse.swt.events.ModifyListener(){
            public void modifyText( org.eclipse.swt.events.ModifyEvent e ) {
                if (isWorking)
                    return;
                textModified(row_type);
            }
        });
        westText.addModifyListener(new org.eclipse.swt.events.ModifyListener(){
            public void modifyText( org.eclipse.swt.events.ModifyEvent e ) {
                if (isWorking)
                    return;
                textModified(bound_type);
            }
        });
        xresText.addModifyListener(new org.eclipse.swt.events.ModifyListener(){
            public void modifyText( org.eclipse.swt.events.ModifyEvent e ) {
                if (isWorking)
                    return;
                textModified(res_type);
            }
        });
        eastText.addModifyListener(new org.eclipse.swt.events.ModifyListener(){
            public void modifyText( org.eclipse.swt.events.ModifyEvent e ) {
                if (isWorking)
                    return;
                textModified(bound_type);
            }
        });
        yresText.addModifyListener(new org.eclipse.swt.events.ModifyListener(){
            public void modifyText( org.eclipse.swt.events.ModifyEvent e ) {
                if (isWorking)
                    return;
                textModified(res_type);
            }
        });
        foregroundColor.getButton().addSelectionListener(this);
        backgroundColour.getButton().addSelectionListener(this);

        /*
         * layout
         */
        c.layout();
        Point size = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        c.setSize(size);
        scrollComposite.setContent(c);
    }

    @Override
    public boolean canStyle( Layer aLayer ) {
        return aLayer.hasResource(ProcessingRegionMapGraphic.class);
    }

    @Override
    protected void refresh() {
        try {
            StyleBlackboard blackboard = getLayer().getStyleBlackboard();

            style = (ProcessingRegionStyle) blackboard.get(ProcessingRegionStyleContent.ID);

            if (style == null) {
                style = ProcessingRegionStyleContent.createDefault();
                blackboard.put(ProcessingRegionStyleContent.ID, style);
                // ((StyleBlackboard) styleBlackboard).setSelected(new
                // String[]{ActiveregionStyleContent.ID});
            }

            ProcessingRegion tmp = new ProcessingRegion(style.west, style.east, style.south, style.north, style.rows, style.cols);
            // set initial values
            isWorking = true;
            northText.setText(String.valueOf(style.north));
            southText.setText(String.valueOf(style.south));
            westText.setText(String.valueOf(style.west));
            eastText.setText(String.valueOf(style.east));
            xresText.setText(String.valueOf(tmp.getWEResolution()));
            yresText.setText(String.valueOf(tmp.getNSResolution()));
            colsText.setText(String.valueOf(style.cols));
            rowsText.setText(String.valueOf(style.rows));
            isWorking = false;

            forgroundAlphaText.setText(Float.toString(style.fAlpha));
            backgroundAlphaText.setText(Float.toString(style.bAlpha));
            foregroundColor.setColorValue(new RGB(style.foregroundColor.getRed(), style.foregroundColor.getGreen(),
                    style.foregroundColor.getBlue()));
            backgroundColour.setColorValue(new RGB(style.backgroundColor.getRed(), style.backgroundColor.getGreen(),
                    style.backgroundColor.getBlue()));

            commitToBlackboards(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateBlackboard() {
        ProcessingRegionStyle style = getActiveRegionStyle();

        RGB bg = backgroundColour.getColorValue();
        try {
            int bAlpha = Integer.parseInt(backgroundAlphaText.getText());
            style.backgroundColor = new Color(bg.red, bg.green, bg.blue, bAlpha);
        } catch (Exception e) {
            style.backgroundColor = new Color(bg.red, bg.green, bg.blue);
        }
        bg = foregroundColor.getColorValue();
        try {
            int bAlpha = Integer.parseInt(forgroundAlphaText.getText());
            style.foregroundColor = new Color(bg.red, bg.green, bg.blue, bAlpha);
        } catch (Exception e) {
            style.foregroundColor = new Color(bg.red, bg.green, bg.blue);
        }

        dumpActiveRegionStyle(style);
    }

    private void dumpActiveRegionStyle( ProcessingRegionStyle style ) {
        blackboard.put(ProcessingRegionStyleContent.ID, style);
        // ((StyleBlackboard) blackboard).setSelected(new String[]{ActiveregionStyleContent.ID});
    }

    private ProcessingRegionStyle getActiveRegionStyle() {
        ProcessingRegionStyle style = (ProcessingRegionStyle) blackboard.get(ProcessingRegionStyleContent.ID);
        if (style == null) {
            style = ProcessingRegionStyleContent.createDefault();
        }
        return style;
    }

    public void widgetSelected( SelectionEvent e ) {
        updateBlackboard();
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        updateBlackboard();
    }

    public void modifyText( ModifyEvent e ) {
        updateBlackboard();
    }

    public void update( Object updatedObject ) {
        if (updatedObject instanceof List) {
            String text = null;
            List< ? > layers = (List< ? >) updatedObject;
            for( Object layer : layers ) {
                if (layer instanceof IGeoResource) {
                    IGeoResource geoResource = (IGeoResource) layer;
                    try {
                        GridGeometry2D gridGeometry = null;
                        if (geoResource.canResolve(GridGeometry2D.class)) {
                            gridGeometry = geoResource.resolve(GridGeometry2D.class, new NullProgressMonitor());
                        } else if (geoResource.canResolve(GridCoverage.class)) {
                            GridCoverage2D gridCoverage = (GridCoverage2D) geoResource.resolve(GridCoverage.class,
                                    new NullProgressMonitor());
                            gridGeometry = gridCoverage.getGridGeometry();
                        }
                        if (gridGeometry != null)
                            setWidgetsToWindow(OmsBoxUtils.gridGeometry2ProcessingRegion(gridGeometry));

                    } catch (IOException e1) {
                        return;
                    }
                } else if (layer instanceof DataStore || layer instanceof UDIGFeatureStore) {
                    try {
                        DataStore store = ((DataStore) layer);
                        SimpleFeatureSource featureStore = store.getFeatureSource(store.getTypeNames()[0]);
                        Envelope envelope = featureStore.getBounds();

                        ProcessingRegionStyle style = getActiveRegionStyle();
                        ProcessingRegion activeWindow = new ProcessingRegion(style.west, style.east, style.south, style.north,
                                style.rows, style.cols);
                        ProcessingRegion newWindow = ProcessingRegion.adaptActiveRegionToEnvelope(envelope, activeWindow);
                        northText.setText(String.valueOf(newWindow.getNorth()));
                        southText.setText(String.valueOf(newWindow.getSouth()));
                        eastText.setText(String.valueOf(newWindow.getEast()));
                        westText.setText(String.valueOf(newWindow.getWest()));
                        textModified(bound_type);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        return;
                    }

                } else {
                    return;
                }
            }
            if (text == null) {
                return;
            }
        }

    }

    public void preApply() {
        // collect new info for the active region
        double n = Double.parseDouble(northText.getText());
        double s = Double.parseDouble(southText.getText());
        double w = Double.parseDouble(westText.getText());
        double e = Double.parseDouble(eastText.getText());
        double xr = Double.parseDouble(xresText.getText());
        double yr = Double.parseDouble(yresText.getText());

        // write the region to file
        ProcessingRegion newRegion = new ProcessingRegion(w, e, s, n, xr, yr);
        style.fAlpha = Float.parseFloat(forgroundAlphaText.getText());
        style.bAlpha = Float.parseFloat(backgroundAlphaText.getText());
        RGB bg = backgroundColour.getColorValue();
        style.backgroundColor = new Color(bg.red, bg.green, bg.blue);
        bg = foregroundColor.getColorValue();
        style.foregroundColor = new Color(bg.red, bg.green, bg.blue);

        commitToBlackboards(newRegion);
        super.preApply();
    }

    /**
     * put all the needed things in the blackboards
     */
    private void commitToBlackboards( ProcessingRegion jgR ) {
        blackboard = getLayer().getStyleBlackboard();

        style.north = jgR.getNorth();
        style.south = jgR.getSouth();
        style.east = jgR.getEast();
        style.west = jgR.getWest();
        style.rows = jgR.getRows();
        style.cols = jgR.getCols();

        blackboard.put(ProcessingRegionStyleContent.ID, style);
        getLayer().setStatus(ILayer.DONE);
    }
    /**
     * method called every time a text field is changed
     * 
     * @param type type of field, bounds - row - resolution
     */
    private synchronized void textModified( int type ) {
        isWorking = true;

        String northstr = northText.getText();
        String southstr = southText.getText();
        String eaststr = eastText.getText();
        String weststr = westText.getText();
        String rowsstr = rowsText.getText();
        String colsstr = colsText.getText();
        String ewresstr = xresText.getText();
        String nsresstr = yresText.getText();

        // System.out.println("passing through textModified with type = " + type);
        // check if there is text in every window
        if (northstr.length() == 0 || southstr.length() == 0 || eaststr.length() == 0 || weststr.length() == 0
                || rowsstr.length() == 0 || colsstr.length() == 0 || ewresstr.length() == 0 || nsresstr.length() == 0) {
            isWorking = false;
            return;
        }

        if (type == bound_type) {
            // one of the boundaries changed
            int rownum = (int) round((new Double(northstr).doubleValue() - new Double(southstr).doubleValue())
                    / new Double(nsresstr).doubleValue());
            int colnum = (int) round((new Double(eaststr).doubleValue() - new Double(weststr).doubleValue())
                    / new Double(ewresstr).doubleValue());
            double newnsres = (new Double(northstr).doubleValue() - new Double(southstr).doubleValue()) / rownum;
            double newewres = (new Double(eaststr).doubleValue() - new Double(weststr).doubleValue()) / colnum;
            rowsText.setText(String.valueOf(rownum));
            colsText.setText(String.valueOf(colnum));
            yresText.setText(String.valueOf(newnsres));
            xresText.setText(String.valueOf(newewres));

        } else if (type == row_type || type == res_type) {
            int rownum = 0;
            int colnum = 0;
            double newnsres = 0;
            double newewres = 0;
            // if the rows change, the resolution has to be changed
            if (type == row_type) {
                newnsres = (Double.parseDouble(northstr) - Double.parseDouble(southstr)) / Double.parseDouble(rowsstr);
                // double check the thing, since the user could have put a non integer rownumber
                rownum = (int) round((Double.parseDouble(northstr) - Double.parseDouble(southstr)) / newnsres);
                // so finally we gain the resolution at a integer row number
                newnsres = (Double.parseDouble(northstr) - Double.parseDouble(southstr)) / rownum;
                newewres = (Double.parseDouble(eaststr) - Double.parseDouble(weststr)) / Double.parseDouble(colsstr);
                // double check the thing, since the user could have put a non integer colnumber
                colnum = (int) round((Double.parseDouble(eaststr) - Double.parseDouble(weststr)) / newewres);
                // so finally we gain the resolution at a integer col number
                newewres = (Double.parseDouble(eaststr) - Double.parseDouble(weststr)) / colnum;
                rowsText.setText(String.valueOf(rownum));
                yresText.setText(String.valueOf(newnsres));
                colsText.setText(String.valueOf(colnum));
                xresText.setText(String.valueOf(newewres));
            } else if (type == res_type) {
                double n = Double.parseDouble(northstr);
                double s = Double.parseDouble(southstr);
                double nsRes = Double.parseDouble(nsresstr);
                double e = Double.parseDouble(eaststr);
                double w = Double.parseDouble(weststr);
                double ewRes = Double.parseDouble(ewresstr);

                double deltaNS = (n - s) % nsRes;
                if (deltaNS > 0.00001)
                    s = s + deltaNS - nsRes;
                double deltaWE = (e - w) % ewRes;
                if (deltaWE > 0)
                    e = e - deltaWE + ewRes;
                southText.setText(String.valueOf(s));
                eastText.setText(String.valueOf(e));

                rownum = (int) round((n - s) / nsRes);
                colnum = (int) round((e - w) / ewRes);
                rowsText.setText(String.valueOf(rownum));
                colsText.setText(String.valueOf(colnum));
            }

        }
        isWorking = false;

    }

    private void setWidgetsToWindow( ProcessingRegion window ) {
        northText.setText(String.valueOf(window.getNorth()));
        southText.setText(String.valueOf(window.getSouth()));
        westText.setText(String.valueOf(window.getWest()));
        eastText.setText(String.valueOf(window.getEast()));
        rowsText.setText(String.valueOf(window.getRows()));
        colsText.setText(String.valueOf(window.getCols()));
        xresText.setText(String.valueOf(window.getWEResolution()));
        yresText.setText(String.valueOf(window.getNSResolution()));
    }
}
