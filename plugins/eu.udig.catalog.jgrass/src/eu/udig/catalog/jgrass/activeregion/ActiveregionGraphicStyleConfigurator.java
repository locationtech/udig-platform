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
package eu.udig.catalog.jgrass.activeregion;

import static java.lang.Math.round;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.impl.UDIGFeatureStore;
import net.refractions.udig.project.ui.internal.dialogs.ColorEditor;
import net.refractions.udig.style.IStyleConfigurator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

import eu.udig.catalog.jgrass.activeregion.dialogs.CatalogJGrassMapsetTreeViewerDialog;
import eu.udig.catalog.jgrass.activeregion.dialogs.FeatureChooserDialog;
import eu.udig.catalog.jgrass.activeregion.dialogs.JGRasterChooserDialog;
import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

public class ActiveregionGraphicStyleConfigurator extends IStyleConfigurator implements SelectionListener, ModifyListener {

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
    private Button resetToActiveButton = null;

    private ColorEditor backgroundColour;
    private ColorEditor foregroundColor;
    private Text backgroundAlphaText;
    private Text forgroundAlphaText;
    private ActiveRegionStyle style = null;
    private boolean isWorking = false;
    private IBlackboard blackboard;
    private Button gridButton;
    private Composite parent;
    private Text windPathText;

    private JGrassMapsetGeoResource jGrassMapsetGeoResource;

    /*
     * verticalMargin = 3; horizontalMargin = 2; verticalSpacing = 5; horizontalSpacing = 3;
     * indentSize = 10; imageHeight = 16; imageWidth = 16; maxWidth = -1; maxHeight = -1;
     * foregroundColour = Color.BLACK; backgroundColour = Color.WHITE; location = new Point(30, 10);
     */

    @SuppressWarnings("nls")
    public void createControl( Composite parent ) {
        this.parent = parent;
        parent.setLayout(new GridLayout());
        ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrollComposite.setMinHeight(100);
        scrollComposite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
        Composite c = new Composite(scrollComposite, SWT.None);
        c.setLayout(new GridLayout());
        c.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

        // choose mapset
        Group chooseMapsetGroup = new Group(c, SWT.BORDER);
        GridLayout layout1 = new GridLayout(3, false);
        chooseMapsetGroup.setLayout(layout1);
        chooseMapsetGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        chooseMapsetGroup.setText("Ruling / Affected mapset");

        Label mapsetLabel = new Label(chooseMapsetGroup, SWT.NONE);
        mapsetLabel.setText("wind file for active region:  ");
        windPathText = new Text(chooseMapsetGroup, SWT.BORDER);
        GridData gd = new GridData();
        gd.widthHint = 100;
        windPathText.setLayoutData(gd);
        windPathText.setEditable(false);

        final Button mapsetButton = new Button(chooseMapsetGroup, SWT.BORDER | SWT.PUSH);
        GridData gd2 = new GridData();
        gd2.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
        mapsetButton.setLayoutData(gd2);
        mapsetButton.setText("Browse");
        mapsetButton.addSelectionListener(new SelectionAdapter(){

            public void widgetSelected( SelectionEvent e ) {
                CatalogJGrassMapsetTreeViewerDialog cDialog = new CatalogJGrassMapsetTreeViewerDialog();
                cDialog.open(mapsetButton.getShell());
                List<JGrassMapsetGeoResource> selectedLayers = cDialog.getSelectedLayers();
                if (selectedLayers == null || selectedLayers.size() == 0) {
                    return;
                }
                jGrassMapsetGeoResource = selectedLayers.get(0);
                String windPath = jGrassMapsetGeoResource.getActiveRegionWindowPath();
                windPathText.setText(windPath);

                JGrassRegion jgR = jGrassMapsetGeoResource.getActiveRegionWindow();
                CoordinateReferenceSystem crs = jGrassMapsetGeoResource.getLocationCrs();

                commitToBlackboards(jgR, crs, windPath);
            }

        });

        // the group for the region
        Group regionGroup = new Group(c, SWT.BORDER);
        GridLayout layout2 = new GridLayout(2, true);
        regionGroup.setLayout(layout2);
        regionGroup.setText("Region settings");
        regionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        northLabel = new Label(regionGroup, SWT.NONE);
        northLabel.setText("north");
        northLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        northText = new Text(regionGroup, SWT.BORDER);
        northText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        southLabel = new Label(regionGroup, SWT.NONE);
        southLabel.setText("south");
        southLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        southText = new Text(regionGroup, SWT.BORDER);
        southText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        westLabel = new Label(regionGroup, SWT.NONE);
        westLabel.setText("west");
        westLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        westText = new Text(regionGroup, SWT.BORDER);
        westText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        eastLabel = new Label(regionGroup, SWT.NONE);
        eastLabel.setText("east");
        eastLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        eastText = new Text(regionGroup, SWT.BORDER);
        eastText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        rowsLabel = new Label(regionGroup, SWT.NONE);
        rowsLabel.setText("rows");
        rowsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        rowsText = new Text(regionGroup, SWT.BORDER);
        rowsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        colsLabel = new Label(regionGroup, SWT.NONE);
        colsLabel.setText("cols");
        colsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        colsText = new Text(regionGroup, SWT.BORDER);
        colsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        xresLabel = new Label(regionGroup, SWT.NONE);
        xresLabel.setText("xres");
        xresLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        xresText = new Text(regionGroup, SWT.BORDER);
        xresText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        yresLabel = new Label(regionGroup, SWT.NONE);
        yresLabel.setText("yres");
        yresLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        yresText = new Text(regionGroup, SWT.BORDER);
        yresText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        // the group for the style
        Group styleGroup = new Group(c, SWT.BORDER);
        GridLayout layout3 = new GridLayout(2, true);
        styleGroup.setLayout(layout3);
        styleGroup.setText("Style properties");
        styleGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        gridButton = new Button(styleGroup, SWT.BORDER | SWT.CHECK);
        gridButton.setText("visualize active region grid");
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData.horizontalSpan = 2;
        gridButton.setLayoutData(gridData);

        Label backgroundColourLabel = new Label(styleGroup, SWT.NONE);
        backgroundColourLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        backgroundColourLabel.setText("background color");
        backgroundColour = new ColorEditor(styleGroup);
        backgroundColour.getButton().setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Label backgroundAlphaLabel = new Label(styleGroup, SWT.NONE);
        backgroundAlphaLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        backgroundAlphaLabel.setText("background alpha (0-1)");
        backgroundAlphaText = new Text(styleGroup, SWT.BORDER);
        backgroundAlphaText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Label foregroundColourLabel = new Label(styleGroup, SWT.NONE);
        foregroundColourLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        foregroundColourLabel.setText("foreground color");
        foregroundColor = new ColorEditor(styleGroup);
        foregroundColor.getButton().setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Label forgroundAlphaLabel = new Label(styleGroup, SWT.NONE);
        forgroundAlphaLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        forgroundAlphaLabel.setText("foreground alpha (0-1)");
        forgroundAlphaText = new Text(styleGroup, SWT.BORDER);
        forgroundAlphaText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        // the group for the set region to map
        final Group settoGroup = new Group(c, SWT.BORDER);
        GridLayout layout4 = new GridLayout(1, true);
        settoGroup.setLayout(layout4);
        settoGroup.setText("Set region to...");
        settoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        rasterMapSetButton = new Button(settoGroup, SWT.NONE);
        rasterMapSetButton.setText("set region to raster map");
        rasterMapSetButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        rasterMapSetButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                JGRasterChooserDialog tree = new JGRasterChooserDialog(null);
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
        resetToActiveButton = new Button(settoGroup, SWT.NONE);
        resetToActiveButton.setText("reset back to actual region");
        resetToActiveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        resetToActiveButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                String windPath = windPathText.getText();
                try {
                    JGrassRegion jgR = new JGrassRegion(windPath);
                    setWidgetsToWindow(jgR);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

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
        foregroundColor.addSelectionListener(this);
        backgroundColour.addSelectionListener(this);

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
        return aLayer.hasResource(ActiveRegionMapGraphic.class);
    }

    @Override
    protected void refresh() {
        try {
            IBlackboard blackboard = getLayer().getMap().getBlackboard();

            style = (ActiveRegionStyle) blackboard.get(ActiveregionStyleContent.ID);

            if (style == null) {
                style = ActiveregionStyleContent.createDefault();
                blackboard.put(ActiveregionStyleContent.ID, style);
                // ((StyleBlackboard) styleBlackboard).setSelected(new
                // String[]{ActiveregionStyleContent.ID});
            }

            // first time choose the mapset
            if (style.windPath == null) {
                CatalogJGrassMapsetTreeViewerDialog mapsetDialog = new CatalogJGrassMapsetTreeViewerDialog();
                mapsetDialog.open(parent.getShell());
                jGrassMapsetGeoResource = mapsetDialog.getSelectedLayers().get(0);

                JGrassRegion activeRegionWindow = jGrassMapsetGeoResource.getActiveRegionWindow();
                style.windPath = jGrassMapsetGeoResource.getActiveRegionWindowPath();
                style.north = (float) activeRegionWindow.getNorth();
                style.south = (float) activeRegionWindow.getSouth();
                style.west = (float) activeRegionWindow.getWest();
                style.east = (float) activeRegionWindow.getEast();
                style.rows = activeRegionWindow.getRows();
                style.cols = activeRegionWindow.getCols();

                CoordinateReferenceSystem jGrassCrs = jGrassMapsetGeoResource.getLocationCrs();
                try {
                    Integer epsg = CRS.lookupEpsgCode(jGrassCrs, true);
                    style.crsString = "EPSG:" + epsg;
                } catch (Exception e) {
                    // try non epsg
                    style.crsString = CRS.lookupIdentifier(jGrassCrs, true);
                }
            }
            windPathText.setText(style.windPath);

            JGrassRegion tmp = new JGrassRegion(style.west, style.east, style.south, style.north, style.rows, style.cols);
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

            CoordinateReferenceSystem crs = CRS.decode(style.crsString);
            commitToBlackboards(tmp, crs, style.windPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateBlackboard() {
        ActiveRegionStyle style = getActiveRegionStyle();

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

    private void dumpActiveRegionStyle( ActiveRegionStyle style ) {
        blackboard.put(ActiveregionStyleContent.ID, style);
        // ((StyleBlackboard) blackboard).setSelected(new String[]{ActiveregionStyleContent.ID});
    }

    private ActiveRegionStyle getActiveRegionStyle() {
        ActiveRegionStyle style = (ActiveRegionStyle) blackboard.get(ActiveregionStyleContent.ID);
        if (style == null) {
            style = ActiveregionStyleContent.createDefault();
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
            List layers = (List) updatedObject;
            for( Object layer : layers ) {
                if (layer instanceof JGrassMapGeoResource) {
                    JGrassMapGeoResource rasterMapResource = (JGrassMapGeoResource) layer;
                    try {
                        text = rasterMapResource.getInfo(null).getName();

                        JGrassRegion fileWindow = rasterMapResource.getFileWindow();
                        if (fileWindow != null) {
                            setWidgetsToWindow(fileWindow);
                        }
                    } catch (IOException e1) {
                        return;
                    }
                } else if (layer instanceof DataStore || layer instanceof UDIGFeatureStore) {
                    try {
                        DataStore store = ((DataStore) layer);
                        FeatureSource featureStore = store.getFeatureSource(store.getTypeNames()[0]);
                        Envelope envelope = featureStore.getBounds();

                        ActiveRegionStyle style = getActiveRegionStyle();
                        JGrassRegion activeWindow = new JGrassRegion(style.west, style.east, style.south, style.north,
                                style.rows, style.cols);
                        JGrassRegion newWindow = JGrassRegion.adaptActiveRegionToEnvelope(envelope, activeWindow);
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
        JGrassRegion newRegion = new JGrassRegion(w, e, s, n, xr, yr);
        try {
            File mapsetFile = new File(style.windPath).getParentFile();
            File locationFile = mapsetFile.getParentFile();
            JGrassRegion.writeWINDToMapset(mapsetFile.getAbsolutePath(), newRegion);
            CoordinateReferenceSystem locationCrs = JGrassCatalogUtilities.getLocationCrs(locationFile.getAbsolutePath());
            style.fAlpha = Float.parseFloat(forgroundAlphaText.getText());
            style.bAlpha = Float.parseFloat(backgroundAlphaText.getText());
            RGB bg = backgroundColour.getColorValue();
            style.backgroundColor = new Color(bg.red, bg.green, bg.blue);
            bg = foregroundColor.getColorValue();
            style.foregroundColor = new Color(bg.red, bg.green, bg.blue);
            style.doGrid = gridButton.getSelection();

            commitToBlackboards(newRegion, locationCrs, style.windPath);
            super.preApply();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * put all the needed things in the blackboards
     */
    private void commitToBlackboards( JGrassRegion jgR, CoordinateReferenceSystem crs, String windPath ) {
        blackboard = getLayer().getMap().getBlackboard();

        style.north = (float) jgR.getNorth();
        style.south = (float) jgR.getSouth();
        style.east = (float) jgR.getEast();
        style.west = (float) jgR.getWest();
        style.rows = jgR.getRows();
        style.cols = jgR.getCols();

        style.windPath = windPath;

        try {

            try {
                Integer epsg = CRS.lookupEpsgCode(crs, true);
                style.crsString = "EPSG:" + epsg;
            } catch (Exception e) {
                // try non epsg
                style.crsString = CRS.lookupIdentifier(crs, true);
            }
        } catch (FactoryException e) {
            e.printStackTrace();
        }

        blackboard.put(ActiveregionStyleContent.ID, style);
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

    private void setWidgetsToWindow( JGrassRegion window ) {
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
