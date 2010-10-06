///*
// * JGrass - Free Open Source Java GIS http://www.jgrass.org 
// * (C) HydroloGIS - www.hydrologis.com 
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package eu.udig.tools.jgrass.coverageinfo;
//
//import static eu.hydrologis.jgrass.libs.utils.JGrassConstants.isNovalue;
//
//import java.awt.Rectangle;
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import java.util.TreeSet;
//
//import net.refractions.udig.project.ILayer;
//import net.refractions.udig.project.internal.impl.LayerImpl;
//import net.refractions.udig.project.render.IViewportModel;
//import net.refractions.udig.project.ui.ApplicationGIS;
//import net.refractions.udig.project.ui.internal.LayersView;
//import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
//import net.refractions.udig.project.ui.tool.SimpleTool;
//import net.refractions.udig.ui.ExceptionDetailsDialog;
//import net.refractions.udig.ui.PlatformGIS;
//
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.IStatus;
//import org.eclipse.core.runtime.NullProgressMonitor;
//import org.eclipse.core.runtime.SubProgressMonitor;
//import org.eclipse.jface.operation.IRunnableWithProgress;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.jface.viewers.TreeSelection;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.dnd.Clipboard;
//import org.eclipse.swt.dnd.TextTransfer;
//import org.eclipse.swt.dnd.Transfer;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.MessageBox;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.ui.PlatformUI;
//import org.geotools.coverage.grid.GridCoverage2D;
//import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
//import org.geotools.coverage.io.CoverageReadRequest;
//import org.geotools.coverage.io.CoverageResponse;
//import org.geotools.coverage.io.impl.DefaultCoverageReadRequest;
//import org.geotools.coverage.io.impl.range.DefaultRangeType;
//import org.geotools.coverage.io.range.FieldType;
//import org.geotools.coverage.io.range.RangeType;
//import org.geotools.data.FeatureSource;
//import org.geotools.factory.CommonFactoryFinder;
//import org.geotools.feature.FeatureCollection;
//import org.geotools.feature.FeatureIterator;
//import org.geotools.geometry.DirectPosition2D;
//import org.geotools.geometry.jts.ReferencedEnvelope;
//import org.joda.time.DateTime;
//import org.opengis.coverage.Coverage;
//import org.opengis.feature.simple.SimpleFeature;
//import org.opengis.feature.simple.SimpleFeatureType;
//import org.opengis.feature.type.AttributeType;
//import org.opengis.filter.FilterFactory2;
//import org.opengis.filter.spatial.BBOX;
//import org.opengis.util.RecordType;
//
//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.GeometryFactory;
//
//import eu.hydrologis.jgrass.charting.datamodels.ChartCreator;
//import eu.hydrologis.jgrass.charting.datamodels.MultiXYTimeChartCreator;
//import eu.hydrologis.jgrass.charting.datamodels.NumericChartData;
//import eu.hydrologis.jgrass.charting.datamodels.NumericChartData.NumericChartDataItem;
//import eu.hydrologis.jgrass.libs.map.JGrassRasterMapReader;
//import eu.hydrologis.jgrass.libs.map.RasterData;
//import eu.hydrologis.jgrass.libs.region.JGrassRegion;
//import eu.hydrologis.jgrass.libs.utils.JGrassUtilities;
//import eu.hydrologis.jgrass.netcdf.service.NetcdfMapGeoResource;
//import eu.hydrologis.jgrass.ui.utilities.widgets.BalloonWindow;
//import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
//import eu.udig.tools.jgrass.JGrassToolsPlugin;
//
///**
// * @author Andrea Antonello - www.hydrologis.com
// */
//public class WhatRastTool extends SimpleTool {
//
//    private List<JGrassMapGeoResource> jgList;
//    private List<NetcdfMapGeoResource> ncList;
//    private List<GridCoverage2D> gcList;
//    private List<FeatureSource> fsList;
//    private Coordinate clickedCoordinate;
//    private NumberFormat formatter = new DecimalFormat("0.000");
//
//    public WhatRastTool() {
//        super(MOUSE | MOTION);
//    }
//
//    public void onMouseReleased( final MapMouseEvent e ) {
//
//        try {
//            final IViewportModel viewportModel = getContext().getViewportModel();
//            clickedCoordinate = viewportModel.pixelToWorld(e.x, e.y);
//
//            ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(LayersView.ID)
//                    .getSite().getSelectionProvider().getSelection();
//
//            jgList = new ArrayList<JGrassMapGeoResource>();
//            ncList = new ArrayList<NetcdfMapGeoResource>();
//            gcList = new ArrayList<GridCoverage2D>();
//            fsList = new ArrayList<FeatureSource>();
//            if (selection instanceof TreeSelection) {
//                TreeSelection treeSelection = (TreeSelection) selection;
//                Iterator iterator = treeSelection.iterator();
//                while( iterator.hasNext() ) {
//                    Object object = iterator.next();
//                    if (object instanceof LayerImpl) {
//                        LayerImpl layer = (LayerImpl) object;
//                        JGrassMapGeoResource resourceJGrass = layer.getResource(JGrassMapGeoResource.class,
//                                new NullProgressMonitor());
//                        if (resourceJGrass != null) {
//                            jgList.add(resourceJGrass);
//                            continue;
//                        }
//                        NetcdfMapGeoResource resourceNetcdf = layer.getResource(NetcdfMapGeoResource.class,
//                                new NullProgressMonitor());
//                        if (resourceNetcdf != null) {
//                            ncList.add(resourceNetcdf);
//                            continue;
//                        }
//                        FeatureSource resourceFeature = layer.getResource(FeatureSource.class, new NullProgressMonitor());
//                        if (resourceFeature != null) {
//                            fsList.add(resourceFeature);
//                            continue;
//                        }
//                        AbstractGridCoverage2DReader resourceGC = layer.getResource(AbstractGridCoverage2DReader.class,
//                                new NullProgressMonitor());
//                        if (resourceGC != null) {
//                            gcList.add(resourceGC.read(null));
//                            continue;
//                        }
//                        gcList.clear();
//                        jgList.clear();
//                        ncList.clear();
//                        break;
//                    }
//                }
//            }
//
//            if (fsList.size() > 0) {
//                showFeatureInfo(e);
//            }
//
//            if (jgList.size() > 0) {
//                showJGrassInfo(e);
//            }
//
//            if (gcList.size() > 0) {
//                showGridCoverageInfo(e);
//            }
//
//            if (ncList.size() > 0) {
//                IRunnableWithProgress operation = new IRunnableWithProgress(){
//                    public void run( IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {
//                        pm.beginTask("Querying netcdf layers...", IProgressMonitor.UNKNOWN);
//                        try {
//                            showNetcdfInfo(e);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            String message = "An error occurred while querying the netcdf data.";
//                            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassToolsPlugin.PLUGIN_ID, e);
//                        } finally {
//                            pm.done();
//                        }
//                    }
//
//                };
//                PlatformGIS.runInProgressDialog("Processing data query...", true, operation, true);
//            }
//
//            // if (jgList.size() < 1) {
//            // Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
//            // MessageBox msgBox = new MessageBox(shell, SWT.ICON_ERROR);
//            // msgBox.setMessage("No suitable layer has been selected.");
//            // msgBox.open();
//            // return;
//            // }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            String message = "An error occurred while querying the data.";
//            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassToolsPlugin.PLUGIN_ID, ex);
//        }
//    }
//
//    private void showNetcdfInfo( MapMouseEvent e ) throws IOException {
//        NetcdfMapGeoResource netcdfMapGeoResource = ncList.get(0);
//
//        List<DateTime> availableTimeSteps = netcdfMapGeoResource.getAvailableTimeSteps();
//        double[] availableElevationLevels = netcdfMapGeoResource.getAvailableElevationLevels();
//
//        CoverageReadRequest readRequest = new DefaultCoverageReadRequest();
//
//        FieldType fieldType = netcdfMapGeoResource.getFieldType();
//        Set<FieldType> fieldTypeSet = new TreeSet<FieldType>();
//        fieldTypeSet.add(fieldType);
//        RangeType rangeType = new DefaultRangeType(fieldType.getName(), fieldType.getDescription(), fieldTypeSet);
//        readRequest.setRangeSubset(rangeType);
//
//        CoverageResponse response = netcdfMapGeoResource.getCoverageSource().read(readRequest, null);
//        if (response == null || response.getStatus() != org.geotools.coverage.io.CoverageResponse.Status.SUCCESS
//                || !response.getExceptions().isEmpty())
//            throw new IOException("Unable to read");
//
//        int size = availableTimeSteps.size();
//        boolean hasVertical = availableElevationLevels != null && availableElevationLevels.length > 0 ? true : false;
//
//        // has time and vertical
//        if (size > 0 && hasVertical) {
//            final Collection< ? extends Coverage> results = response.getResults(null);
//            double[][][] data = new double[availableElevationLevels.length][2][size];
//            int timeIndex = 0;
//            int depthIndex = 0;
//            for( Coverage c : results ) {
//                GridCoverage2D coverage = (GridCoverage2D) c;
//                Object evaluate = coverage.evaluate(new DirectPosition2D(clickedCoordinate.x, clickedCoordinate.y));
//                double value = -1.0;
//                if (evaluate instanceof double[]) {
//                    double[] ev = (double[]) evaluate;
//                    value = ev[0];
//                } else if (evaluate instanceof int[]) {
//                    int[] ev = (int[]) evaluate;
//                    value = ev[0];
//                }
//
//                DateTime dateTime = availableTimeSteps.get(timeIndex);
//                data[depthIndex][0][timeIndex] = dateTime.getMillis();
//                data[depthIndex][1][timeIndex] = value;
//
//                if (depthIndex < availableElevationLevels.length - 1) {
//                    depthIndex++;
//                } else {
//                    timeIndex++;
//                    depthIndex = 0;
//                }
//            }
//            String[] seriesNames = new String[availableElevationLevels.length];
//            for( int i = 0; i < seriesNames.length; i++ ) {
//                seriesNames[i] = String.valueOf(availableElevationLevels[i]);
//            }
//            makeTimeDepthPlot(data, seriesNames);
//        }
//
//        // has time but no vertical
//        if (size > 0 && !hasVertical) {
//            final Collection< ? extends Coverage> results = response.getResults(null);
//            double[][][] data = new double[1][2][size];
//            int timeIndex = 0;
//            for( Coverage c : results ) {
//                GridCoverage2D coverage = (GridCoverage2D) c;
//                Object evaluate = coverage.evaluate(new DirectPosition2D(clickedCoordinate.x, clickedCoordinate.y));
//                double value = -1.0;
//                if (evaluate instanceof double[]) {
//                    double[] ev = (double[]) evaluate;
//                    value = ev[0];
//                } else if (evaluate instanceof int[]) {
//                    int[] ev = (int[]) evaluate;
//                    value = ev[0];
//                }
//
//                DateTime dateTime = availableTimeSteps.get(timeIndex);
//                data[0][0][timeIndex] = dateTime.getMillis();
//                data[0][1][timeIndex] = value;
//
//                timeIndex++;
//            }
//            String[] seriesNames = new String[]{""};
//            makeTimePlot(data, seriesNames);
//        }
//
//        // has vertical but no time
//        if (size == 0 && hasVertical) {
//            final Collection< ? extends Coverage> results = response.getResults(null);
//            double[][][] data = new double[1][2][availableElevationLevels.length];
//            int depthIndex = 0;
//            for( Coverage c : results ) {
//                GridCoverage2D coverage = (GridCoverage2D) c;
//                Object evaluate = coverage.evaluate(new DirectPosition2D(clickedCoordinate.x, clickedCoordinate.y));
//                double value = -1.0;
//                if (evaluate instanceof double[]) {
//                    double[] ev = (double[]) evaluate;
//                    value = ev[0];
//                } else if (evaluate instanceof int[]) {
//                    int[] ev = (int[]) evaluate;
//                    value = ev[0];
//                }
//
//                data[0][0][depthIndex] = availableElevationLevels[depthIndex];
//                data[0][1][depthIndex] = value;
//
//                depthIndex++;
//            }
//            String[] seriesNames = new String[]{""};
//            makeDepthPlot(data, seriesNames);
//        }
//
//        // has no time and no vertical
//        if (size == 0 && !hasVertical) {
//            final Collection< ? extends Coverage> results = response.getResults(null);
//            final StringBuffer text = new StringBuffer();
//            final StringBuffer clipBoardText = new StringBuffer();
//
//            NumberFormat formatter = new DecimalFormat("0.00");
//            text.append("Easting: ").append(formatter.format(clickedCoordinate.x));
//            text.append("\nNorthing: ").append(formatter.format(clickedCoordinate.y));
//            clipBoardText.append(formatter.format(clickedCoordinate.x)).append(",");
//            clipBoardText.append(formatter.format(clickedCoordinate.y));
//
//            for( Coverage c : results ) {
//                GridCoverage2D coverage = (GridCoverage2D) c;
//                Object evaluate = coverage.evaluate(new DirectPosition2D(clickedCoordinate.x, clickedCoordinate.y));
//                double value = -1.0;
//                if (evaluate instanceof double[]) {
//                    double[] ev = (double[]) evaluate;
//                    value = ev[0];
//                } else if (evaluate instanceof int[]) {
//                    int[] ev = (int[]) evaluate;
//                    value = ev[0];
//                }
//
//                text.append("\nLayer: " + fieldType.getName().getLocalPart().toString());
//                if (isNovalue(value)) {
//                    text.append("\n  Value: novalue");
//                } else {
//                    text.append("\n  Value: ").append(formatter.format(value));
//                }
//                clipBoardText.append(",").append(formatter.format(value));
//            }
//
//            Display.getDefault().asyncExec(new Runnable(){
//
//                public void run() {
//
//                    Shell shell = new Shell(Display.getDefault());
//                    BalloonWindow window = new BalloonWindow(shell, SWT.DIALOG_TRIM | SWT.ON_TOP | SWT.TITLE);
//
//                    Point mouse = shell.getDisplay().getCursorLocation();
//                    window.setLocation(mouse.x, mouse.y);
//                    window.setText(text.toString());
//                    window.open();
//                    /*
//                     * put the string on the clipboard
//                     */
//                    Clipboard cb = new Clipboard(shell.getDisplay());
//                    TextTransfer textTransfer = TextTransfer.getInstance();
//                    cb.setContents(new Object[]{clipBoardText.toString()}, new Transfer[]{textTransfer});
//                }
//            });
//
//        }
//
//    }
//
//    private void makeTimeDepthPlot( double[][][] data, String[] seriesNames ) {
//
//        // shell.setLayout(new GridLayout());
//
//        final NumericChartData numericChartData = new NumericChartData(1);
//        NumericChartDataItem tab2 = numericChartData.getChartDataItem(0);
//        tab2.bigTitle = "Netcdf point query";
//        tab2.chartStringExtra = "Text of tab 2";
//        /*
//        * in tab 2: one single chart with 3 series
//        */
//        tab2.chartTitles.add("Netcdf point query");
//        tab2.chartXLabels.add("time");
//        tab2.chartYLabels.add("value");
//        tab2.chartSeriesData.add(data);
//        tab2.seriesNames.add(seriesNames);
//
//        /*
//        * create the chart using a
//        */
//        final ChartCreator creator = new MultiXYTimeChartCreator();
//        /* create all the charts in the list for every tab? Yes. */
//        creator.M_HINT_CREATE_CHART = new boolean[][]{{true, false}};
//        /* create the checkboxes to hide and unhide the series? First no, second yes */
//        creator.M_HINT_CREATE_TOGGLEHIDESERIES = new boolean[][]{{true, false}};
//        /* define the types of chart to create */
//        creator.M_HINT_CHART_TYPE = new int[][]{{ChartCreator.TIMEYLINECHART, -1}};
//        /* define the vertical orientation of the chart */
//        creator.M_HINT_CHARTORIENTATION_UP = new boolean[][]{{true, true}};
//        creator.M_HINT_CHARTSERIESCOLOR = null;
//
//        /*
//        * finally create that plot
//        */
//
//        Display.getDefault().asyncExec(new Runnable(){
//            public void run() {
//                Shell chartShell = new Shell(Display.getDefault(), SWT.DIALOG_TRIM | SWT.RESIZE);
//                chartShell.setSize(800, 600);
//                chartShell.setLayout(new GridLayout(1, false));
//                creator.makePlot(chartShell, numericChartData);
//                chartShell.open();
//            }
//        });
//
//    }
//
//    private void makeTimePlot( double[][][] data, String[] seriesNames ) {
//
//        // shell.setLayout(new GridLayout());
//
//        final NumericChartData numericChartData = new NumericChartData(1);
//        NumericChartDataItem tab2 = numericChartData.getChartDataItem(0);
//        tab2.bigTitle = "Netcdf point query";
//        tab2.chartStringExtra = "Text of tab 2";
//        /*
//         * in tab 2: one single chart with 3 series
//         */
//        tab2.chartTitles.add("Netcdf point query");
//        tab2.chartXLabels.add("time");
//        tab2.chartYLabels.add("value");
//        tab2.chartSeriesData.add(data);
//        tab2.seriesNames.add(seriesNames);
//
//        /*
//         * create the chart using a
//         */
//        final ChartCreator creator = new MultiXYTimeChartCreator();
//        /* create all the charts in the list for every tab? Yes. */
//        creator.M_HINT_CREATE_CHART = new boolean[][]{{true, false}};
//        /* create the checkboxes to hide and unhide the series? First no, second yes */
//        creator.M_HINT_CREATE_TOGGLEHIDESERIES = new boolean[][]{{true, false}};
//        /* define the types of chart to create */
//        creator.M_HINT_CHART_TYPE = new int[][]{{ChartCreator.TIMEYLINECHART, -1}};
//        /* define the vertical orientation of the chart */
//        creator.M_HINT_CHARTORIENTATION_UP = new boolean[][]{{true, true}};
//        creator.M_HINT_CHARTSERIESCOLOR = null;
//
//        /*
//         * finally create that plot
//         */
//
//        Display.getDefault().asyncExec(new Runnable(){
//            public void run() {
//                Shell chartShell = new Shell(Display.getDefault(), SWT.DIALOG_TRIM | SWT.RESIZE);
//                chartShell.setSize(800, 600);
//                chartShell.setLayout(new GridLayout(1, false));
//                creator.makePlot(chartShell, numericChartData);
//                chartShell.open();
//            }
//        });
//
//    }
//
//    private void makeDepthPlot( double[][][] data, String[] seriesNames ) {
//
//        // shell.setLayout(new GridLayout());
//
//        final NumericChartData numericChartData = new NumericChartData(1);
//        NumericChartDataItem tab2 = numericChartData.getChartDataItem(0);
//        tab2.bigTitle = "Netcdf point query";
//        tab2.chartStringExtra = "Text of tab 2";
//        /*
//         * in tab 2: one single chart with 3 series
//         */
//        tab2.chartTitles.add("Netcdf point query");
//        tab2.chartXLabels.add("depth");
//        tab2.chartYLabels.add("value");
//        tab2.chartSeriesData.add(data);
//        tab2.seriesNames.add(seriesNames);
//
//        /*
//         * create the chart using a
//         */
//        final ChartCreator creator = new MultiXYTimeChartCreator();
//        /* create all the charts in the list for every tab? Yes. */
//        creator.M_HINT_CREATE_CHART = new boolean[][]{{true, false}};
//        /* create the checkboxes to hide and unhide the series? First no, second yes */
//        creator.M_HINT_CREATE_TOGGLEHIDESERIES = new boolean[][]{{true, false}};
//        /* define the types of chart to create */
//        creator.M_HINT_CHART_TYPE = new int[][]{{ChartCreator.XYLINECHART, -1}};
//        /* define the vertical orientation of the chart */
//        creator.M_HINT_CHARTORIENTATION_UP = new boolean[][]{{true, true}};
//        creator.M_HINT_CHARTSERIESCOLOR = null;
//
//        /*
//         * finally create that plot
//         */
//
//        Display.getDefault().asyncExec(new Runnable(){
//            public void run() {
//                Shell chartShell = new Shell(Display.getDefault(), SWT.DIALOG_TRIM | SWT.RESIZE);
//                chartShell.setSize(800, 600);
//                chartShell.setLayout(new GridLayout(1, false));
//                creator.makePlot(chartShell, numericChartData);
//                chartShell.open();
//            }
//        });
//
//    }
//
//    private void showJGrassInfo( final MapMouseEvent e ) {
//        final StringBuffer text = new StringBuffer();
//        final StringBuffer clipBoardText = new StringBuffer();
//
//        IRunnableWithProgress operation = new IRunnableWithProgress(){
//
//            public void run( IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {
//                try {
//                    JGrassMapGeoResource tmp = jgList.get(0);
//                    JGrassRegion activeRegion = tmp.getActiveWindow();
//
//                    NumberFormat formatter = new DecimalFormat("0.00");
//                    text.append("Easting: ").append(formatter.format(clickedCoordinate.x));
//                    text.append("\nNorthing: ").append(formatter.format(clickedCoordinate.y));
//                    clipBoardText.append(formatter.format(clickedCoordinate.x)).append(",");
//                    clipBoardText.append(formatter.format(clickedCoordinate.y));
//                    int[] rowCol = JGrassUtilities.coordinateToNearestRowCol(activeRegion, clickedCoordinate);
//
//                    if (rowCol != null) {
//                        text.append("\nRegion row: ").append(rowCol[0]);
//                        text.append("\nRegion col: ").append(rowCol[1]);
//                        text.append("\n");
//
//                        for( int i = 0; i < jgList.size(); i++ ) {
//
//                            JGrassMapGeoResource rasterMap = jgList.get(i);
//
//                            JGrassRegion newRegion = JGrassUtilities.getRectangleAroundPoint(activeRegion, clickedCoordinate.x,
//                                    clickedCoordinate.y);
//
//                            String mapPath = rasterMap.getMapFile().getAbsolutePath();
//                            JGrassRasterMapReader jgR = new JGrassRasterMapReader.BuilderFromMapPath(newRegion, mapPath).maptype(
//                                    rasterMap.getType()).build();
//
//                            RasterData rasterData = null;
//                            if (jgR.open() && jgR.hasMoreData()) {
//                                rasterData = jgR.getNextData();
//                            } else {
//                                throw new IOException("Error: unable to read the raster.");
//                            }
//                            jgR.close();
//                            double queryValue = rasterData.getValueAt(0, 0);
//
//                            text.append("\nLayer: " + rasterMap.getInfo(new NullProgressMonitor()).getName());
//                            if (isNovalue(queryValue)) {
//                                text.append("\n  Value: novalue");
//                            } else {
//                                text.append("\n  Value: ").append(formatter.format(queryValue));
//                            }
//                            clipBoardText.append(",").append(formatter.format(queryValue));
//
//                        }
//                    } else {
//                        text.append("\nClicked point is outside the active region.");
//                    }
//                } catch (Exception ex) {
//                    JGrassToolsPlugin.log(
//                            "JGrassToolsPlugin problem: eu.udig.tools.jgrass.whatrast#WhatRastTool#onMouseReleased", ex); //$NON-NLS-1$
//                    ex.printStackTrace();
//                }
//            }
//        };
//        PlatformGIS.runInProgressDialog("Extracting data...", true, operation, false);
//
//        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
//        BalloonWindow window = new BalloonWindow(shell, SWT.TOOL | SWT.ON_TOP | SWT.TITLE);
//        Point mouse = shell.getDisplay().getCursorLocation();
//        window.setLocation(mouse.x, mouse.y);
//        window.setText(text.toString());
//        window.open();
//
//        /*
//         * put the string on the clipboard
//         */
//        Clipboard cb = new Clipboard(shell.getDisplay());
//        TextTransfer textTransfer = TextTransfer.getInstance();
//        cb.setContents(new Object[]{clipBoardText.toString()}, new Transfer[]{textTransfer});
//
//    }
//
//    private void showGridCoverageInfo( final MapMouseEvent e ) {
//        final StringBuffer text = new StringBuffer();
//        final StringBuffer clipBoardText = new StringBuffer();
//
//        IRunnableWithProgress operation = new IRunnableWithProgress(){
//
//            public void run( IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {
//                try {
//                    NumberFormat formatter = new DecimalFormat("0.00");
//                    text.append("Easting: ").append(formatter.format(clickedCoordinate.x));
//                    text.append("\nNorthing: ").append(formatter.format(clickedCoordinate.y));
//                    clipBoardText.append(formatter.format(clickedCoordinate.x)).append(",");
//                    clipBoardText.append(formatter.format(clickedCoordinate.y));
//
//                    for( int i = 0; i < gcList.size(); i++ ) {
//
//                        GridCoverage2D gridCoverage = gcList.get(i);
//                        Object qyueryResult = gridCoverage
//                                .evaluate(new DirectPosition2D(clickedCoordinate.x, clickedCoordinate.y));
//                        text.append("\nLayer: " + gridCoverage.getName().toString());
//                        text.append("\n  Value: ");
//                        if (qyueryResult instanceof double[]) {
//                            double[] ev = (double[]) qyueryResult;
//                            for( double d : ev ) {
//                                text.append(d).append("\t");
//                                clipBoardText.append(",").append(d);
//                            }
//                        } else if (qyueryResult instanceof float[]) {
//                            float[] ev = (float[]) qyueryResult;
//                            for( float f : ev ) {
//                                text.append(f).append("\t");
//                                clipBoardText.append(",").append(f);
//                            }
//                        } else if (qyueryResult instanceof int[]) {
//                            int[] ev = (int[]) qyueryResult;
//                            for( int in : ev ) {
//                                text.append(in).append("\t");
//                                clipBoardText.append(",").append(in);
//                            }
//                        } else if (qyueryResult instanceof short[]) {
//                            short[] ev = (short[]) qyueryResult;
//                            for( short in : ev ) {
//                                text.append(in).append("\t");
//                                clipBoardText.append(",").append(in);
//                            }
//                        } else if (qyueryResult instanceof byte[]) {
//                            byte[] ev = (byte[]) qyueryResult;
//                            for( byte in : ev ) {
//                                text.append(in).append("\t");
//                                clipBoardText.append(",").append(in);
//                            }
//                        }
//
//                    }
//                } catch (Exception ex) {
//                    JGrassToolsPlugin.log(
//                            "JGrassToolsPlugin problem: eu.udig.tools.jgrass.whatrast#WhatRastTool#onMouseReleased", ex); //$NON-NLS-1$
//                    ex.printStackTrace();
//                }
//            }
//        };
//        PlatformGIS.runInProgressDialog("Extracting data...", true, operation, false);
//
//        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
//        BalloonWindow window = new BalloonWindow(shell, SWT.TOOL | SWT.ON_TOP | SWT.TITLE);
//        Point mouse = shell.getDisplay().getCursorLocation();
//        window.setLocation(mouse.x, mouse.y);
//        window.setText(text.toString());
//        window.open();
//
//        /*
//         * put the string on the clipboard
//         */
//        Clipboard cb = new Clipboard(shell.getDisplay());
//        TextTransfer textTransfer = TextTransfer.getInstance();
//        cb.setContents(new Object[]{clipBoardText.toString()}, new Transfer[]{textTransfer});
//
//    }
//
//    private void showFeatureInfo( final MapMouseEvent e ) {
//        try {
//
//            GeometryFactory gF = new GeometryFactory();
//            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
//
//            java.awt.Point screenPos = e.getPoint();
//            ReferencedEnvelope boundingBox = getContext().getBoundingBox(screenPos, 1);
//
//            for( int l = 0; l < fsList.size(); l++ ) {
//                FeatureSource source = fsList.get(l);
//                SimpleFeatureType ft = (SimpleFeatureType) source.getSchema();
//                String geometryAttributeName = ft.getGeometryDescriptor().getName().toString();
//                BBOX filter = ff.bbox(ff.property(geometryAttributeName), boundingBox);
//
//                List<AttributeType> attributeTypes = ft.getTypes();
//                ArrayList<String> attStrings = new ArrayList<String>();
//                for( int i = 0; i < attributeTypes.size(); i++ ) {
//                    attStrings.add(attributeTypes.get(i).getName().toString());
//                }
//
//                FeatureCollection<SimpleFeatureType, SimpleFeature> SimpleFeatureCollection = source.getFeatures(filter);
//                FeatureIterator<SimpleFeature> fIterator = SimpleFeatureCollection.features();
//
//                StringBuffer buf = new StringBuffer();
//                com.vividsolutions.jts.geom.Point bubblePoint = null;
//
//                SimpleFeature feature = null;
//                if (fIterator.hasNext()) {
//                    feature = fIterator.next();
//                    Geometry fGeometry = (Geometry) feature.getDefaultGeometry();
//                    bubblePoint = fGeometry.getCentroid();
//
//                    if (fGeometry.getGeometryType().equals("LineString") || fGeometry.getGeometryType().equals("MultiLineString")) {
//                        buf.append("length: ").append(formatter.format(fGeometry.getLength())).append("\n");
//                        buf.append("centroid: ").append(
//                                formatter.format(bubblePoint.getX()) + "/" + formatter.format(bubblePoint.getY())).append("\n");
//                        Coordinate[] coords = fGeometry.getCoordinates();
//                        buf.append("number of points: ").append(fGeometry.getNumPoints() + "\n");
//                        buf.append("startpoint: ").append(formatter.format(coords[0].x) + "/" + formatter.format(coords[0].y))
//                                .append("\n");
//                        buf.append("endpoint: ").append(
//                                formatter.format(coords[coords.length - 1].x) + "/"
//                                        + formatter.format(coords[coords.length - 1].y)).append("\n");
//
//                    } else if (fGeometry.getGeometryType().equals("Polygon")
//                            || fGeometry.getGeometryType().equals("MultiPolygon")) {
//                        buf.append("length: ").append(formatter.format(fGeometry.getBoundary().getLength())).append("\n");
//                        buf.append("area: ").append(formatter.format(fGeometry.getArea())).append("\n");
//                        buf.append("centroid: ").append(
//                                formatter.format(bubblePoint.getX()) + "/" + formatter.format(bubblePoint.getY())).append("\n");
//                        buf.append("number of points: ").append(fGeometry.getNumPoints() + "\n");
//
//                    } else if (fGeometry.getGeometryType().equals("Point") || fGeometry.getGeometryType().equals("MultiPoint")) {
//                        Coordinate pos = fGeometry.getCoordinate();
//                        bubblePoint = gF.createPoint(pos);
//                        buf.append("position: ").append(
//                                formatter.format(bubblePoint.getX()) + "/" + formatter.format(bubblePoint.getY())).append("\n");
//                    }
//
//                }
//                /*
//                 * and add the attributes
//                 */
//                buf.append("------------------------\n");
//                buf.append("Attributes: \n");
//                for( int i = 0; i < ft.getAttributeCount(); i++ ) {
//                    AttributeType at = ft.getType(i);
//                    if (!Geometry.class.isAssignableFrom(at.getBinding()))
//                        buf.append(at.getName() + ": ");
//                    Object attribute = feature.getAttribute(i);
//                    if (!(attribute instanceof Geometry))
//                        buf.append(attribute + "\n");
//                }
//
//                // java.awt.Point bubblePointOnScreen = ApplicationGIS.getActiveMap()
//                // .getViewportModel().worldToPixel(
//                // new Coordinate(bubblePoint.getX(), bubblePoint.getY()));
//                //
//                // final MessageBubble drawInfoCommand = new MessageBubble(bubblePointOnScreen.x,
//                // bubblePointOnScreen.y, buf.toString(), (short) 4);
//                // Display.getDefault().asyncExec(new Runnable(){
//                // public void run() {
//                // ApplicationGIS.createContext(ApplicationGIS.getActiveMap())
//                // .sendASyncCommand(drawInfoCommand);
//                // }
//                // });
//
//                Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
//                BalloonWindow window = new BalloonWindow(shell, SWT.TOOL | SWT.ON_TOP | SWT.TITLE);
//                Point mouse = shell.getDisplay().getCursorLocation();
//                window.setLocation(mouse.x, mouse.y);
//                window.setText(buf.toString());
//                window.open();
//            }
//
//        } catch (Exception ex) {
//            JGrassToolsPlugin.log(
//                    "JGrassToolsPlugin problem: eu.udig.tools.jgrass.whatrast#WhatRastTool#onMouseReleased", ex); //$NON-NLS-1$
//            ex.printStackTrace();
//        }
//
//    }
//}
