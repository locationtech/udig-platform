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
package eu.udig.tools.jgrass.profile;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 * The view that shows the coverage profiles created by the {@link ProfileTool}.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ProfileView extends ViewPart {

    public static final String ID = "eu.udig.tools.jgrass.profileview";
    private XYSeries series;
    private XYItemRenderer renderer;
    private XYPlot plot;

    private double max = Double.NEGATIVE_INFINITY;
    private double min = Double.POSITIVE_INFINITY;

    private List<Marker> markers = new ArrayList<Marker>();

    public ProfileView() {
    }

    public void createPartControl( Composite parent ) {
        series = new XYSeries("profile");
        XYSeriesCollection lineDataset = new XYSeriesCollection();
        lineDataset.addSeries(series);
        JFreeChart result = ChartFactory.createXYAreaChart("", "Progressive distance", "Elevation", lineDataset,
                PlotOrientation.VERTICAL, true, true, false);
        plot = (XYPlot) result.getPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.black);

        new ChartComposite(parent, SWT.None, result);

        Action action = new ExportChartData();
        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager dropDownMenu = actionBars.getMenuManager();
        dropDownMenu.add(action);
    }

    public void setFocus() {
    }

    public void addToSeries( final double x, final double y ) {
        max = Math.max(max, y);
        min = Math.min(min, y);
        series.add(x, y);
    }

    public void setRangeToDataBounds() {
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange(min - 1, max + 1);
    }

    public void clearSeries() {
        series.clear();
    }

    public boolean seriesIsEmpty() {
        return series.isEmpty();
    }

    public void addStopLine( double x ) {

        DecimalFormat formatter = new DecimalFormat("0.0");
        // add a category marker
        ValueMarker marker = new ValueMarker(x, Color.red, new BasicStroke(1.0f));
        marker.setAlpha(0.6f);
        marker.setLabel(formatter.format(x));
        marker.setLabelFont(new Font("Dialog", Font.PLAIN, 8));
        marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        marker.setLabelOffset(new RectangleInsets(2, 5, 2, 5));
        plot.addDomainMarker(marker, Layer.BACKGROUND);
        markers.add(marker);
    }

    public void clearMarkers() {
        for( Marker m : markers ) {
            plot.removeDomainMarker(m, Layer.BACKGROUND);
        }
    }

    private class ExportChartData extends Action {

        /**
         * 
         */
        public ExportChartData() {
            setText("Export chart data to file");
        }

        public void run() {
            double[][] data = series.toArray();

            FileDialog fileDialog = new FileDialog(ProfileView.this.getSite().getShell(), SWT.SAVE);
            fileDialog.setText("Choose the output data file");
            String path = fileDialog.open();
            if (path == null || path.length() < 1) {
                return;
            }

            try {
                BufferedWriter bW = new BufferedWriter(new FileWriter(path));
                bW.write("Progressive\tElevation");
                for( int j = 0; j < data[0].length; j++ ) {
                    for( int i = 0; i < data.length; i++ ) {
                        bW.write(data[i][j] + "\t");
                    }
                    bW.write("\n");
                }
                bW.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
