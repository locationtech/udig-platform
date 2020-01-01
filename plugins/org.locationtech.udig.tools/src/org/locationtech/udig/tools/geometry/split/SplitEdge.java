/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.geometry.split;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Location;
import org.locationtech.jts.geomgraph.Edge;
import org.locationtech.jts.geomgraph.Label;
import org.locationtech.jts.geomgraph.Position;

/**
 * A custom edge class that knows if its an edge from the shell of the polygon
 * being split, one of its holes or the splitting line, by inspecting its label.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
final class SplitEdge extends Edge {

	private int	countVisited	= 0;


	/**
	 * new instance of SplitEdge
	 * @param firstCoord
	 * @param secondCoord
	 * @param onLoc
	 * @param leftLoc
	 * @param rightLoc
	 * @return new instance of SplitEdge
	 */
    public static SplitEdge newInstance(final Coordinate firstCoord, final Coordinate secondCoord, final int onLoc, final int leftLoc, final int rightLoc ) {

        final Label label = new Label(onLoc, leftLoc, rightLoc);

        Coordinate[] edgeCoord = new Coordinate[2];
        edgeCoord[0] = firstCoord;
        edgeCoord[1] = secondCoord;
        
        final SplitEdge edge = new SplitEdge(edgeCoord, label);

        return edge;
    }

    /**
     * new instance of SplitEdge
     * 
     * @param coords
     * @param onLoc
     * @param leftLoc
     * @param rightLoc
     * @return new instance of SplitEdge
     */
    public static SplitEdge newInstance( Coordinate[] coords, int onLoc, int leftLoc, int rightLoc ) {
        Label label = new Label(onLoc, leftLoc, rightLoc);
        SplitEdge splitEdge = new SplitEdge(coords, label);
        
        return splitEdge;
    }
    
	/**
	 * Default constructor.
	 * 
	 * @param pts
	 *            Coordinate of the edge.
	 * @param label
	 *            Label for tag the edge, i.e: as interior, exterior, etc.
	 */
	private SplitEdge(Coordinate[] pts, Label label) {

		super(pts, label);
	}

	/**
	 * If has been visited one time or more, return true.
	 * 
	 * @return True if it was visited.
	 */
	@Override
	public boolean isVisited() {

		return countVisited > 0;
	}

	/**
	 * If has been visited 2 or more times, return true.
	 * 
	 * @return True if was visited twice.
	 */
	public boolean isTwiceVisited() {

		return countVisited >= 2;
	}

	/**
	 * Increase the visited counter.
	 */
	public void countVisited() {

		countVisited++;
	}

	/**
	 * Get the locations of the labels, ON, LEFT and RIGHT.
	 * 
	 * @return The locations of the labels.
	 */
	private int[] getLabelLocations() {

		int[] locations = new int[3];
		Label label = getLabel();
		locations[Position.ON] = label.getLocation(0, Position.ON);
		locations[Position.LEFT] = label.getLocation(0, Position.LEFT);
		locations[Position.RIGHT] = label.getLocation(0, Position.RIGHT);
		return locations;
	}

	/**
	 * Check if the edge belongs to the shell.
	 * 
	 * @return True if the edge belongs to the shell.
	 */
	public boolean isShellEdge() {

		int[] loc = getLabelLocations();
		return (loc[Position.LEFT] == Location.EXTERIOR && loc[Position.RIGHT] == Location.INTERIOR);
	}

	/**
	 * Check if the edge belongs to the hole.
	 * 
	 * @return True if the edge belongs to the hole.
	 */
	public boolean isHoleEdge() {

		int[] loc = getLabelLocations();
		return (loc[Position.LEFT] == Location.INTERIOR && loc[Position.RIGHT] == Location.EXTERIOR);
	}

	/**
	 * Check if the edge belongs to the interior.
	 * 
	 * @return True if the edge belongs to the interior.
	 */
	public boolean isIntersectionEdge() {

		int[] loc = getLabelLocations();
		return (loc[Position.LEFT] == Location.INTERIOR && loc[Position.RIGHT] == Location.INTERIOR);
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer("Edge[label="); //$NON-NLS-1$
		sb.append(getLabel()).append(", "); //$NON-NLS-1$
		Coordinate[] coords = getCoordinates();
		for (int i = 0; i < coords.length; i++) {
			sb.append(coords[i].x).append(",").append(coords[i].y).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("]"); //$NON-NLS-1$
		return sb.toString();
	}

    public boolean equalsCoordinates( Object o ) {
        
        if (!(o instanceof Edge))
            return false;
        Edge e = (Edge) o;

        if (this.getCoordinates().length != e.getCoordinates().length)
            return false;

        for( int i = 0; i < this.getCoordinates().length; i++ ) {
            
            boolean found =false;
            for( int j = 0; j < e.getCoordinates().length; j++ ) {

                if(this.getCoordinate(i).equals2D(e.getCoordinate(j))){
                    found=true;
                    break;
                }
            }
            if(!found){
                return false;
            }
        }
        return true;
    }

}
