/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.behaviour;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Iterator;

import net.refractions.udig.catalog.ITransientResolve;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMapListener;
import net.refractions.udig.project.MapEvent;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.LockingBehaviour;
import net.refractions.udig.tools.edit.MouseTracker;
import net.refractions.udig.tools.edit.animation.MessageBubble;
import net.refractions.udig.tools.edit.commands.CreateEditGeomCommand;
import net.refractions.udig.tools.edit.commands.DeselectEditGeomCommand;
import net.refractions.udig.tools.edit.commands.SetCurrentGeomCommand;
import net.refractions.udig.tools.edit.commands.SetEditStateCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceConstants;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.ShapeType;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.PlatformUI;
import org.h2.constant.SysProperties;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.buffer.BufferOp;

/**
 * Create and draw a Polygon from a Point or Line. This behaviour provides input
 * feedback as well as creating the final geomirty.
 * 
 * Requirements:
 * <ul>
 * <li>EventType==WHEEL</li>
 * <li>button1 is down</li>
 * <li>button2 is down</li>
 * </ul>
 * 
 * <ul>
 * <li>if only one mouse coordinate is added then a buffer point will be created
 * </li>
 * <li>if more than one mouse coordinate is added then a buffer line will be
 * created</li>
 * <li>handler is locked until middle mouse is clicked</li>
 * 
 * @author leviputna
 * @since 1.2.0
 */
public class BufferUpdateBehaviour implements EventBehaviour, LockingBehaviour {

	private Double buffer;
	private int maxBufferSize;

	@Override
	public UndoableMapCommand getCommand(EditToolHandler handler,
			MapMouseEvent e, EventType eventType) {

		checkPreference();
		
		if((eventType == EventType.ENTERED && buffer == null) || buffer == null){
			resetBuffer(handler);
		}
		
		if(eventType == EventType.ENTERED)
			return null;

		if (eventType == EventType.WHEEL) {

			MapMouseWheelEvent event = (MapMouseWheelEvent) e;

			double pxSize = handler.getContext().getPixelSize().x;

			// we want to increase our buffer by one px each mouse scroll so we
			// get a smooth buffer at all zooms levels.
			double dif = event.clickCount * pxSize;
			if ((buffer + dif > 0) && (buffer + dif < pxSize * maxBufferSize)) {
				buffer += dif;
			}
		}

		setMessage(buffer,handler,e);

		return null;
	}

	@Override
	public Object getKey(EditToolHandler handler) {
		return this;
	}

	@Override
	public boolean isValid(EditToolHandler handler, MapMouseEvent e,
			EventType eventType) {
		// As we want the background ScrollZoom tool to remain active use of the
		// Command (on a mac)
		// Alt (on windows) modifiers is required before the scroll buffer can
		// be adjusted.
		return (eventType == EventType.WHEEL && e.modifiersDown()) || (eventType == EventType.ENTERED);
	}

	@Override
	public void handleError(EditToolHandler handler, Throwable error,
			UndoableMapCommand command) {
		EditPlugin.log("", error); //$NON-NLS-1$
	}

	public void resetBuffer(EditToolHandler handler) {
		double pxSize = handler.getContext().getPixelSize().x;
		buffer = pxSize
				* (double) EditPlugin.getDefault().getPreferenceStore()
						.getFloat(PreferenceConstants.P_BUFFER_DEFULT_SIZE);
	}

	public Double getBufferAmount() {
		return buffer;
	}

	protected void checkPreference() {
		maxBufferSize = EditPlugin.getDefault().getPreferenceStore()
				.getInt(PreferenceConstants.P_BUFFER_MAX_SIZE);
	}

	private void setMessage(final double distance, final EditToolHandler handler, MapMouseEvent e) {
		//mouse bubble
		MessageBubble bubble = new MessageBubble(e.getPoint().x + 10,
				e.getPoint().y - 20, formatDistance(distance), 
				PreferenceUtil.instance().getMessageDisplayDelay());
		
		AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), bubble);
		
		Runnable runnable = new Runnable() {
			public void run() {
				if (PlatformUI.getWorkbench().isClosing())
					return;
				IActionBars2 bars = handler.getContext().getActionBars();
				if (bars != null) {
					bars.getStatusLineManager().setErrorMessage(null);
					bars.getStatusLineManager().setMessage(
							"Buffer: " + formatDistance(distance));
				}

			}
		};
		if (Display.getCurrent() != null)
			runnable.run();
		else
			Display.getDefault().asyncExec(runnable);
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
		newVal = (Long.valueOf(Math.round(newVal)).intValue()) / divisor;
		return newVal;
	}

	/**
	 * @param distance
	 * @return
	 */
	private String formatDistance(double distance) {
		String message = "";
		if (distance > 0.01) { // km
			message = message.concat(round((distance * 100), 2) + " km"); //$NON-NLS-1$
		} else { // mm
			message = message.concat(round(distance * 100000, 1) + " m"); //$NON-NLS-1$
		}

		return message;
	}

}