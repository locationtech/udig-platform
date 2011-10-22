/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.udig.tools.jgrass.profile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.SimpleTool;
import net.refractions.udig.ui.ExceptionDetailsDialog;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.ViewType;
import org.geotools.geometry.jts.JTS;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;

import eu.udig.tools.jgrass.JGrassToolsPlugin;
import eu.udig.tools.jgrass.profile.borrowedfromjgrasstools.CoverageUtilities;
import eu.udig.tools.jgrass.profile.borrowedfromjgrasstools.ProfilePoint;

/**
 * <p>
 * Tool to draw raster map profiles.
 * </p>
 * <p>
 * NOTE: this is an extention of the DistanceTool
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class ProfileTool extends SimpleTool {

    private int currentPointNumber = 0;

    private List<Point> points = new ArrayList<Point>();
    private ProfileFeedbackCommand command;
    private Point now;
    private double latestProgessiveDistance = 0;
    private boolean doubleClicked = false;

    private GridCoverage2D rasterMapResource;
    private ProfileView chartView;
    private Coordinate begin;

    public ProfileTool() {
        super(MOUSE | MOTION);
    }

    protected void onMouseMoved( MapMouseEvent e ) {
        if (!doubleClicked) {
            // saving value to display the distance
            now = e.getPoint();
            if (command == null || points.isEmpty())
                return;
            Rectangle area = command.getValidArea();
            if (area != null)
                getContext().getViewportPane().repaint(area.x, area.y, area.width, area.height);
            else {
                getContext().getViewportPane().repaint();
            }
        }
    }

    public void onMouseReleased( MapMouseEvent e ) {
        // necessary to restart from begin, having an empty view
        if (currentPointNumber == 0) {
            chartView.clearSeries();
            latestProgessiveDistance = 0;
            points.clear();
            disposeCommand();
            doubleClicked = false;
            chartView.clearMarkers();
        }

        Point current = e.getPoint();
        // if enough points are there, create the profile
        if (points.isEmpty() || !current.equals(points.get(points.size() - 1))) {
            points.add(current);
        }

        /*
         * run with backgroundable progress monitoring
         */
        if (command == null || !command.isValid()) {
            command = new ProfileFeedbackCommand();
            getContext().sendASyncCommand(command);
        }

        try {
            profile(null);
        } catch (Exception ex) {
            ex.printStackTrace();

            String message = "An error occurred while extracting the profile from the map.";
            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassToolsPlugin.PLUGIN_ID, ex);
        }
    }

    protected void onMouseDoubleClicked( MapMouseEvent e ) {
        currentPointNumber = 0;
        doubleClicked = true;
    }

    /**
     * Removes all the line in the map
     */
    private void disposeCommand() {
        if (command != null) {
            command.setValid(false);
            Rectangle area = command.getValidArea();
            if (area != null)
                getContext().getViewportPane().repaint(area.x, area.y, area.width, area.height);
            else {
                getContext().getViewportPane().repaint();
            }
            command = null;
        }
    }

    /**
     * Creates the profile of the raster map.
     * 
     * @param monitor the progress monitor.
     * @throws IOException 
     */
    private void profile( IProgressMonitor monitor ) throws Exception {
        if (points.size() == currentPointNumber && points.size() > 1) {
            // no point added, do not read
            return;
        } else {
            if (!doubleClicked) {
                currentPointNumber = points.size();
            }
        }

        /*
         * need to get the profile of the last two clicked points
         */
        if (points.size() == 1) {
            Point beforeLastPoint = points.get(0);
            begin = getContext().pixelToWorld(beforeLastPoint.x, beforeLastPoint.y);
        } else if (points.size() > 1) {
            // monitor.beginTask("Extracting profile...", IProgressMonitor.UNKNOWN);

            Point lastPoint = points.get(points.size() - 1);
            Coordinate end = getContext().pixelToWorld(lastPoint.x, lastPoint.y);

            final List<ProfilePoint> profile = CoverageUtilities.doProfile(rasterMapResource, begin, end);
            begin = end;

            Display.getDefault().syncExec(new Runnable(){
                public void run() {
                    for( ProfilePoint profilePoint : profile ) {
                        double elevation = profilePoint.getElevation();
                        if (!Double.isNaN(elevation)) {
                            chartView.addToSeries(latestProgessiveDistance + profilePoint.getProgressive(), elevation);
                        } else {
                            // chartView.addToSeries(latestProgessiveDistance +
                            // profilePoint.getProgressive(), Double.NaN);
                        }
                    }
                    ProfilePoint last = profile.get(profile.size() - 1);
                    chartView.addStopLine(latestProgessiveDistance + last.getProgressive());
                    chartView.setRangeToDataBounds();
                    latestProgessiveDistance = latestProgessiveDistance + last.getProgressive();
                }
            });

            // monitor.done();
        }

    }

    public void setActive( boolean active ) {

        if (!active) {
            // on tool deactivation
            rasterMapResource = null;
            disposeCommand();
        } else {
            // on tool activation
            final ILayer selectedLayer = getContext().getSelectedLayer();
            final IGeoResource geoResource = selectedLayer.getGeoResource();
            if (geoResource.canResolve(GridCoverage.class)) {
                IRunnableWithProgress operation = new IRunnableWithProgress(){
                    public void run( IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {
                        try {
                            rasterMapResource = (GridCoverage2D) geoResource.resolve(GridCoverage.class,
                                    new NullProgressMonitor());
                            rasterMapResource = rasterMapResource.view(ViewType.GEOPHYSICS);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                PlatformGIS.runInProgressDialog("Reading map for profile...", false, operation, false);

            }

            if (rasterMapResource == null) {
                getContext().updateUI(new Runnable(){
                    public void run() {
                        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                        MessageBox msgBox = new MessageBox(shell, SWT.ICON_ERROR);
                        msgBox.setMessage("The selected layer can't be read by the available datastores. Unable to create a profile on it.");
                        msgBox.open();
                    }
                });
                super.setActive(false);
                return;
            }

            final IStatusLineManager statusBar = getContext().getActionBars().getStatusLineManager();
            disposeCommand();
            if (statusBar == null)
                return; // shouldn't happen if the tool is being used.

            getContext().updateUI(new Runnable(){
                public void run() {
                    statusBar.setErrorMessage(null);
                    statusBar.setMessage(null);

                    try {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ProfileView.ID);
                        chartView = ((ProfileView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                                .findView(ProfileView.ID));
                    } catch (PartInitException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        super.setActive(active);
    }

    private double distance() throws TransformException {
        if (points.isEmpty())
            return 0;
        Iterator<Point> iter = points.iterator();
        Point start = iter.next();
        double distance = 0;
        while( iter.hasNext() ) {
            Point current = iter.next();
            Coordinate begin = getContext().pixelToWorld(start.x, start.y);
            Coordinate end = getContext().pixelToWorld(current.x, current.y);
            distance += JTS.orthodromicDistance(begin, end, getContext().getCRS());
            start = current;
        }

        if (now != null) {
            Point current = now;
            Coordinate begin = getContext().pixelToWorld(start.x, start.y);
            Coordinate end = getContext().pixelToWorld(current.x, current.y);
            distance += JTS.orthodromicDistance(begin, end, getContext().getCRS());
        }
        return distance;
    }

    private void displayError() {
        final IStatusLineManager statusBar = getContext().getActionBars().getStatusLineManager();

        if (statusBar == null)
            return; // shouldn't happen if the tool is being used.

        getContext().updateUI(new Runnable(){
            public void run() {
                statusBar.setErrorMessage("Profile Tool Error");
            }
        });
    }

    private void displayOnStatusBar( double distance ) {
        final IStatusLineManager statusBar = getContext().getActionBars().getStatusLineManager();

        if (statusBar == null)
            return; // shouldn't happen if the tool is being used.
        final String message = createMessage(distance);
        getContext().updateUI(new Runnable(){
            public void run() {
                statusBar.setErrorMessage(null);
                statusBar.setMessage(message);
            }
        });
    }

    /**
     * @param distance
     * @return
     */
    private String createMessage( double distance ) {
        String message = "";

        if (distance > 100000.0) {
            message = message.concat((int) (distance / 1000.0) + " km"); //$NON-NLS-1$
        } else if (distance > 10000.0) { // km + m
            message = message.concat(round(distance / 1000.0, 1) + " km"); //$NON-NLS-1$
        } else if (distance > 1000.0) { // km + m
            message = message.concat(round(distance / 1000.0, 2) + " km"); //$NON-NLS-1$
        } else if (distance > 100.0) { // m
            message = message.concat(round(distance, 1) + " m"); //$NON-NLS-1$
        } else if (distance > 1.0) { // m
            message = message.concat(round(distance, 2) + " m"); //$NON-NLS-1$
        } else { // mm
            message = message.concat(round(distance * 1000.0, 1) + " mm"); //$NON-NLS-1$
        }

        return message;
    }

    /**
     * Truncates a double to the given number of decimal places. Note: truncation at zero decimal
     * places will still show up as x.0, since we're using the double type.
     * 
     * @param value number to round-off
     * @param decimalPlaces number of decimal places to leave
     * @return the rounded value
     */
    private double round( double value, int decimalPlaces ) {
        double divisor = Math.pow(10, decimalPlaces);
        double newVal = value * divisor;
        newVal = (Long.valueOf(Math.round(newVal)).intValue()) / divisor;
        return newVal;
    }

    /**
     */
    class ProfileFeedbackCommand extends AbstractDrawCommand {

        public Rectangle getValidArea() {
            return null;
        }

        public void run( IProgressMonitor monitor ) throws Exception {
            if (points.isEmpty())
                return;
            graphics.setColor(Color.BLACK);
            Iterator<Point> iter = points.iterator();
            Point start = iter.next();
            while( iter.hasNext() ) {
                Point current = iter.next();
                graphics.drawLine(start.x, start.y, current.x, current.y);
                start = current;
            }
            if (start == null || now == null)
                return;
            graphics.drawLine(start.x, start.y, now.x, now.y);
            double distance = distance();
            displayOnStatusBar(distance);

        }
    }
}
