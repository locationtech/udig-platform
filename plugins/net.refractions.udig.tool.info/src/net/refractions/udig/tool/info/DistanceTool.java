package net.refractions.udig.tool.info;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.util.CRSUtil;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.SimpleTool;
import net.refractions.udig.tool.info.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.geotools.geometry.jts.JTS;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;

public class DistanceTool extends SimpleTool {
    public DistanceTool() {
        super(MOUSE | MOTION);
    }
    
    List<Point> points = new ArrayList<Point>();
    DistanceFeedbackCommand command;
    private Point now;
    
    @Override
    protected void onMouseMoved( MapMouseEvent e ) {
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

    public void onMouseReleased( MapMouseEvent e ) {
        Point current = e.getPoint();
        if (points.isEmpty() || !current.equals(points.get(points.size() - 1)))
            points.add(current);
        if (command == null || !command.isValid()) {
            command = new DistanceFeedbackCommand();
            getContext().sendASyncCommand(command);
        }
    }

    @Override
    protected void onMouseDoubleClicked( MapMouseEvent e ) {
        disposeCommand();
        displayResult();
        points.clear();
    }

    /**
     *
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

    private void displayResult() {
        try {
            double distance = distance();
            displayOnStatusBar(distance);
        } catch (Exception e1) {
            InfoPlugin.log("", e1); //$NON-NLS-1$
            displayError();
        }
    }

    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
        final IStatusLineManager statusBar = getContext().getActionBars().getStatusLineManager();
        
        disposeCommand();
        
        if (statusBar == null)
            return; // shouldn't happen if the tool is being used.
        getContext().updateUI(new Runnable(){
            public void run() {
                statusBar.setErrorMessage(null);
                statusBar.setMessage(null);
            }
        });
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
            start = current;
        }
        
        return distance;
    }

    
    private void displayError() {
        final IStatusLineManager statusBar = getContext().getActionBars().getStatusLineManager();

        if (statusBar == null)
            return; // shouldn't happen if the tool is being used.

        getContext().updateUI(new Runnable(){
            public void run() {
                statusBar.setErrorMessage(Messages.DistanceTool_error); 
            }
        });
    }
    
    private void displayOnStatusBar( double distance ) {
        final IStatusLineManager statusBar = getContext().getActionBars().getStatusLineManager();

        if (statusBar == null)
            return; // shouldn't happen if the tool is being used.
        String units = UiPlugin.getDefault().getPreferenceStore().getString(net.refractions.udig.ui.preferences.PreferenceConstants.P_DEFAULT_UNITS);
        if (units.equals( net.refractions.udig.ui.preferences.PreferenceConstants.AUTO_UNITS) && CRSUtil.isCoordinateReferenceSystemImperial(context.getCRS())){
            units = net.refractions.udig.ui.preferences.PreferenceConstants.IMPERIAL_UNITS;
        }
        final String message;
        if (units.equals( net.refractions.udig.ui.preferences.PreferenceConstants.IMPERIAL_UNITS)){
            message = createMessageImperial(distance);
        }else{
            message = createMessageMetric(distance);
        }

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
    private String createMessageMetric( double distance ) {
        String message = Messages.DistanceTool_distance; 
        
        if (distance > 100000.0) {
            message = message.concat((int) (distance / 1000.0) + "km");    //$NON-NLS-1$
        } else if (distance > 10000.0) { //km + m
            message = message.concat(round(distance / 1000.0, 1) + "km"); //$NON-NLS-1$
        } else if (distance > 1000.0) { //km + m
            message = message.concat(round(distance / 1000.0, 2) + "km"); //$NON-NLS-1$
        } else if (distance > 100.0) { //m
            message = message.concat(round(distance, 1) + "m"); //$NON-NLS-1$
        } else if (distance > 1.0) { //m
            message = message.concat(round(distance, 2) + "m"); //$NON-NLS-1$
        } else { //mm
            message = message.concat(round(distance * 1000.0, 1) + "mm"); //$NON-NLS-1$
        }

        return message;
    }
    
    private String createMessageImperial( double distance ) {
        String message = Messages.DistanceTool_distance; 
        //distance is in meter
        distance = (distance/1000) * 0.621371192;  //convert to miles
        
        if (distance > 1000.0) {
            message = message.concat((int) (distance) + "mi");    //$NON-NLS-1$
        } else if (distance > 100.0) { //mile
            message = message.concat(round(distance, 1) + "mi"); //$NON-NLS-1$
        } else if (distance > 1.0) { //mile
            message = message.concat(round(distance, 2) + "mi"); //$NON-NLS-1$
        } else if (distance > 0.1) { //mile
            message = message.concat((int)(distance*5280) + "ft"); //$NON-NLS-1$
        } else if (distance > 0.0189) { //mile approx.100ft
            message = message.concat(round(distance*5280, 1) + "ft"); //$NON-NLS-1$
        } else { //mile
            message = message.concat(round(distance*5280*12, 1) + "in"); //$NON-NLS-1$
        }

        return message;
    }
    
    /**
     * Truncates a double to the given number of decimal places. Note:
     * truncation at zero decimal places will still show up as x.0, since we're
     * using the double type.
     * 
     * @param value
     *            number to round-off
     * @param decimalPlaces
     *            number of decimal places to leave
     * @return the rounded value
     */
    private double round(double value, int decimalPlaces) {
        double divisor = Math.pow(10, decimalPlaces);
        double newVal = value * divisor;
        newVal =  (Long.valueOf(Math.round(newVal)).intValue())/divisor; 
        return newVal;
    }
    
    class DistanceFeedbackCommand extends AbstractDrawCommand {

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

            displayResult();
        }

    }
    
    public void reset() {
        points.clear();
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
}
