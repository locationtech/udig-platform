/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.measure;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import si.uom.SI;
import systems.uom.common.USCustomary;

import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.MetricPrefix;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.geotools.geometry.jts.JTS;
import org.locationtech.udig.catalog.util.CRSUtil;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.SimpleTool;
import org.locationtech.udig.tool.measure.internal.MeasurementToolPlugin;
import org.locationtech.udig.tool.measure.internal.Messages;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Coordinate;

public class DistanceTool extends SimpleTool implements KeyListener {
    public DistanceTool() {
        super(MOUSE | MOTION);
    }

    List<Coordinate> points = new ArrayList<Coordinate>();
    DistanceFeedbackCommand command;
    private Coordinate now;

    double lastSegmentDistance = 0;
    
    @Override
    protected void onMouseMoved( MapMouseEvent e ) {
        now = getContext().pixelToWorld(e.x, e.y);
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
        Coordinate current = getContext().pixelToWorld(e.x, e.y);
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
            MeasurementToolPlugin.log("", e1); //$NON-NLS-1$
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

        if (active) {
            Control control = getContext().getViewportPane().getControl();
            control.addKeyListener(this);
        }else{
            Control control = getContext().getViewportPane().getControl();
            control.removeKeyListener(this);
        }

    }

    private double distance() throws TransformException {
        if (points.isEmpty())
            return 0;
        Iterator<Coordinate> iter = points.iterator();
        Coordinate start = iter.next();
        double distance = 0;
        double lastSegment = 0;
        while( iter.hasNext() ) {
        	Coordinate current = iter.next();
        	lastSegment = JTS.orthodromicDistance(start, current, getContext().getCRS());
            distance += lastSegment;
            start = current;
        }

        if (now != null) {
        	Coordinate current = now;
        	lastSegment = JTS.orthodromicDistance(start, current, getContext().getCRS());
            distance += lastSegment;
        }
        this.lastSegmentDistance = lastSegment;
        
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

        IPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.locationtech.udig.ui");

        String units = preferenceStore.getString(org.locationtech.udig.ui.preferences.PreferenceConstants.P_DEFAULT_UNITS);
        if (units.equals( org.locationtech.udig.ui.preferences.PreferenceConstants.AUTO_UNITS) && CRSUtil.isCoordinateReferenceSystemImperial(context.getCRS())){
            units = org.locationtech.udig.ui.preferences.PreferenceConstants.IMPERIAL_UNITS;
        }

        final Quantity<Length> distanceInMeter = Quantities.getQuantity(distance, SI.METRE);
        final Quantity<Length> lastSegmentInMeter = Quantities.getQuantity(lastSegmentDistance, SI.METRE);
        
        Quantity<Length> result = null;
        Quantity<Length> resultLastSegment = null;
        if (units.equals( org.locationtech.udig.ui.preferences.PreferenceConstants.IMPERIAL_UNITS)){
        	Quantity<Length> distanceInMiles = distanceInMeter.to(USCustomary.MILE);
        	Quantity<Length> lastSegmentInMiles = lastSegmentInMeter.to(USCustomary.MILE);
            double distInMilesValue = distanceInMiles.getValue().doubleValue();
            double lastSegmentInMilesValue = lastSegmentInMiles.getValue().doubleValue();

            if (distInMilesValue >  Quantities.getQuantity(1, USCustomary.MILE).getValue().doubleValue()) {
                // everything longer than a mile
                result = distanceInMiles;
            } else if (distInMilesValue > Quantities.getQuantity(1, USCustomary.FOOT).to(USCustomary.MILE).getValue().doubleValue()) {
                // everything longer that a foot
                result = distanceInMiles.to(USCustomary.FOOT);
            } else {
                // shorter than a foot
                result = distanceInMiles.to(USCustomary.INCH);
            }
            
            if (lastSegmentInMilesValue >  Quantities.getQuantity(1, USCustomary.MILE).getValue().doubleValue()) {
                // everything longer than a mile
            	resultLastSegment = lastSegmentInMiles;
            } else if (lastSegmentInMilesValue > Quantities.getQuantity(1, USCustomary.FOOT).to(USCustomary.MILE).getValue().doubleValue()) {
                // everything longer that a foot
            	resultLastSegment = lastSegmentInMiles.to(USCustomary.FOOT);
            } else {
                // shorter than a foot
            	resultLastSegment = lastSegmentInMiles.to(USCustomary.INCH);
            }
        } else {
            double distanceInMeterValue = distanceInMeter.getValue().doubleValue();
            double lastSegmentInMeterValue = lastSegmentInMeter.getValue().doubleValue();
            
            if (distanceInMeterValue >  Quantities.getQuantity(1000, SI.METRE).to(SI.METRE).getValue().doubleValue()) {
                result = distanceInMeter.to(MetricPrefix.KILO(SI.METRE));
            } else if (distanceInMeterValue > Quantities.getQuantity(1, SI.METRE).to(SI.METRE).getValue().doubleValue()) {
                result = distanceInMeter.to(SI.METRE);
            } else if (distanceInMeterValue > Quantities.getQuantity(1, MetricPrefix.CENTI(SI.METRE)).to(SI.METRE).getValue().doubleValue()) {
                result = distanceInMeter.to(MetricPrefix.CENTI(SI.METRE));
            } else {
                result = distanceInMeter.to(MetricPrefix.MILLI(SI.METRE));
            }
            
            
            if (lastSegmentInMeterValue >  Quantities.getQuantity(1000, SI.METRE).to(SI.METRE).getValue().doubleValue()) {
            	resultLastSegment = lastSegmentInMeter.to(MetricPrefix.KILO(SI.METRE));
            } else if (lastSegmentInMeterValue > Quantities.getQuantity(1, SI.METRE).to(SI.METRE).getValue().doubleValue()) {
            	resultLastSegment = lastSegmentInMeter.to(SI.METRE);
            } else if (lastSegmentInMeterValue > Quantities.getQuantity(1, MetricPrefix.CENTI(SI.METRE)).to(SI.METRE).getValue().doubleValue()) {
            	resultLastSegment = lastSegmentInMeter.to(MetricPrefix.CENTI(SI.METRE));
            } else {
            	resultLastSegment = lastSegmentInMeter.to(MetricPrefix.MILLI(SI.METRE));
            }

        }

        final String message = MessageFormat.format(Messages.DistanceTool_distance, 
        		round(result.getValue().doubleValue(), 2) + " " + result.getUnit(), 
        		round(resultLastSegment.getValue().doubleValue(), 2) + " " + resultLastSegment.getUnit());

        getContext().updateUI(new Runnable(){
            public void run() {
                statusBar.setErrorMessage(null);
                statusBar.setMessage(message);
            }
        });
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
            Iterator<Coordinate> iter = points.iterator();
            Point start = getContext().worldToPixel(iter.next());
            while( iter.hasNext() ) {
            	Point current = getContext().worldToPixel(iter.next());
                graphics.drawLine(start.x, start.y, current.x, current.y);
                start = current;
            }
            if (start == null || now == null)
                return;
            Point nowPoint = getContext().worldToPixel(now);
            graphics.drawLine(start.x, start.y, nowPoint.x, nowPoint.y);

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

    public void keyPressed( KeyEvent e ) {
    }

    public void keyReleased( KeyEvent e ) {
        if (e.character == SWT.CR){
            // finish on enter key
            disposeCommand();
            displayResult();
            points.clear();
        }
    }
}