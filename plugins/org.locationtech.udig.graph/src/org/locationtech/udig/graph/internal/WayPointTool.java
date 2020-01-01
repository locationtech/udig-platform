/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.graph.internal;

import java.util.ArrayList;
import java.util.Collection;

import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.SimpleTool;

import org.eclipse.jface.action.IStatusLineManager;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.Node;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

/**
 * This tool makes use of the <b>graph</b> on the map blackboard
 * and will return allow you to add Nodes to a list <b>waypoints</b>.
 */
public class WayPointTool extends SimpleTool {
    public WayPointTool() {
        super( MOUSE );
    }

    public WayPointTool( int targets ) {
        super( targets );
    }

    @SuppressWarnings("unchecked")
    private Node findClosest( Coordinate coord ) {
        Node closest = null;
        double minDist = 0.0;

        IBlackboard mapboard = context.getMap().getBlackboard();
        if ( !mapboard.contains( "graph" ) ) {
            return null;
        }
        Graph graph = (Graph) mapboard.get( "graph" );
        Collection<Node> nodes = (Collection<Node>) graph.getNodes();
        for ( Node node : nodes ) {
            Point p = (Point ) node.getObject();
            double d = getDist( coord, p );
            if ( closest == null || d < minDist ) {
                minDist = d;
                closest = node;
            }

        }

        return closest;
    }

    /**
     * TODO
     * 
     * @param clickPt
     * @param p
     * @return
     */
    private double getDist( Coordinate clickPt, Point p ) {
        double dx = clickPt.x - p.getX();
        double dy = clickPt.y - p.getY();
        return dx * dx + dy * dy;
    }

    public void onMousePressed( MapMouseEvent e ) {
        Coordinate clickPt = getContext().pixelToWorld( e.x, e.y );
        final Node n = findClosest( clickPt );
        if ( n != null ) {
            IBlackboard mapboard = context.getMap().getBlackboard();
            if ( !mapboard.contains( "waypoints" ) ) {
                mapboard.put( "waypoints", new ArrayList<Node>() );
            }
            ArrayList<Node> list = (ArrayList<Node> ) mapboard.get( "waypoints" );
            list.add( n );
            final int len = list.size();

            getContext().getSelectedLayer().refresh( null );

            final IStatusLineManager statusBar = getContext().getActionBars().getStatusLineManager();
            if ( statusBar == null ) {
                return; // shouldn't happen if the tool is being used.
            }
            getContext().updateUI( new Runnable() {
                public void run() {
                    statusBar.setErrorMessage( null );
                    statusBar.setMessage( Long.toString( len ) + ". waypoint added, node=" + n.toString() );
                }
            } );
        }
    }

    private void displayError() {
        final IStatusLineManager statusBar = getContext().getActionBars().getStatusLineManager();

        if ( statusBar == null ) {
            return; // shouldn't happen if the tool is being used.
        }

        getContext().updateUI( new Runnable() {
            public void run() {
                statusBar.setErrorMessage( "Unable to calculate the distance" );
            }
        } );
    }

    private void displayOnStatusBar( double distance ) {
        final IStatusLineManager statusBar = getContext().getActionBars().getStatusLineManager();

        if ( statusBar == null ) {
            return; // shouldn't happen if the tool is being used.
        }
        int totalmeters = (int ) distance;
        final int km = totalmeters / 1000;
        final int meters = totalmeters - (km * 1000);
        float cm = (float ) (distance - totalmeters) * 10000;
        cm = Math.round( cm );
        final float finalcm = cm / 100;
        getContext().updateUI( new Runnable() {
            public void run() {
                statusBar.setErrorMessage( null );
                statusBar.setMessage( "Distance =  " + km + "," + meters + "m " + finalcm + "cm" );
            }
        } );
    }

}
