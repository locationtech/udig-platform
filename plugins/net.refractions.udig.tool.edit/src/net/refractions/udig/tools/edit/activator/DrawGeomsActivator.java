/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.activator;

import java.awt.Color;

import net.refractions.udig.core.IProvider;
import net.refractions.udig.project.BlackboardEvent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IBlackboardListener;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.commands.DrawEditGeomsCommand;
import net.refractions.udig.tools.edit.commands.StyleStrategy;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

/**
 * 
 * Adds a DrawGeomsCommand to the draw commands and invalidates it at the end.
 * 
 * @author jones
 * @since 1.1.0
 */
public class DrawGeomsActivator implements Activator {

    protected DrawEditGeomsCommand command;
    private DrawType type;
    private boolean showMouseLocation = true;
    private MapMouseMotionListener listener;
    protected ViewportPane pane;
    protected EditToolHandler handler;
    private IBlackboardListener mapBBListener;

    /**
     * Returns true if the mouse position will be shown. Default is true.
     * 
     * @return Returns true if the mouse position will be shown. Default is true.
     */
    public boolean isShowMouseLocation() {
        return this.showMouseLocation;
    }

    /**
     * @param showMouseLocation The showMouseLocation to set.
     */
    public void setShowMouseLocation( boolean showMouseLocation ) {
        this.showMouseLocation = showMouseLocation;
    }

    /**
     * 
     * @param type
     */
    public DrawGeomsActivator( DrawType type ) {
        this.type = type;
    }

    public void activate( final EditToolHandler handler ) {

        this.handler = handler;
        command = new DrawEditGeomsCommand(handler);
        //handler.getCurrentEditBlackboard().getBuffer();
        System.out.println(handler.toString());

        StyleStrategy colorizationStrategy = command.getColorizationStrategy();
        colorizationStrategy.setFill(new IProvider<Color>(){

            public Color get( Object... params ) {
                return PreferenceUtil.instance().getDrawGeomsFill();
            }

        });
        colorizationStrategy.setLine(new IProvider<Color>(){

            public Color get( Object... params ) {
                return PreferenceUtil.instance().getDrawGeomsLine();
            }

        });
        pane = handler.getContext().getViewportPane();

        addMouseListener();

        handler.getContext().getViewportPane().addDrawCommand(command);
    }

    private void removeMouseListener() {
        if (pane != null && listener != null)
            pane.removeMouseMotionListener(listener);
        listener = null;
        handler.getContext().getMap().getBlackboard().removeListener(mapBBListener);
    }

    protected void addMouseListener() {
        listener = new MapMouseMotionListener(){

            public void mouseMoved( MapMouseEvent event ) {
                if (type == DrawType.POINT || !showMouseLocation)
                    return;
                if (listener != this) {
                    ((ViewportPane) event.source).removeMouseMotionListener(this);
                }
                boolean change = false;
                if (handler.getCurrentState() == EditState.CREATING)
                    change = command.setCurrentLocation(Point.valueOf(event.x, event.y), handler
                            .getCurrentShape());
                else
                    change = command.setCurrentLocation(null, null);
                if (change)
                    handler.repaint();
            }

            public void mouseDragged( MapMouseEvent event ) {
                mouseMoved(event);
            }

            public void mouseHovered( MapMouseEvent event ) {
            }

        };
        pane.addMouseMotionListener(listener);

        mapBBListener = new IBlackboardListener(){

            public void blackBoardCleared( IBlackboard source ) {
                if (mapBBListener != this) {
                    source.removeListener(this);
                }
                command.setCurrentLocation(null, null);
            }

            public void blackBoardChanged( BlackboardEvent event ) {
                if (mapBBListener != this) {
                    event.getSource().removeListener(this);
                }
                if (EditToolHandler.CURRENT_SHAPE.equals(event.getKey())) {
                    command.setCurrentLocation(null, (PrimitiveShape) event.getNewValue());
                }
            }

        };

        handler.getContext().getMap().getBlackboard().addListener(mapBBListener);
    }

    public void deactivate( EditToolHandler handler ) {
        if (command != null)
            command.setValid(false);
        mapBBListener = null;
        listener = null;
        removeMouseListener();
    }

    public void handleActivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("Error creating and sending command", error); //$NON-NLS-1$
    }

    public void handleDeactivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("Error invalidating command", error); //$NON-NLS-1$
    }

    public enum DrawType {
        POLYGON, LINE, POINT
    }

}
