/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.northarrow;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.mapgraphic.style.LocationStyleContent;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.ui.graphics.ViewportGraphics;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Coordinate;

/**
 * 
 * a styled north arrow that actually points north!
 * <p>
 *
 * </p>
 * @author Jody Garnett and Brock Anderson
 * @since 1.1.0
 */
public final class NorthArrowMapGraphic implements MapGraphic{

    private static int ARROW_HEIGHT = 35;
    private static int ARROW_WIDTH = 22;
    private static int BOTTOM_INSET = 8;
    private static int SPACE_ABOVE_N = 3;
        
	public NorthArrowMapGraphic() {
	}

	public void draw(MapGraphicContext context) {
		Point end = null;
		Point start = start( context );
		if ( start == null ) return; // bye!

		Point displayPosition = new Point(start);
		if (displayPosition.x < 0){
			displayPosition.x = (int)context.getMapDisplay().getWidth() + displayPosition.x;
		}
		if (displayPosition.y < 0){
			displayPosition.y = (int)context.getMapDisplay().getHeight() + displayPosition.y;
		}
		Coordinate worldStart = context.pixelToWorld( displayPosition.x, displayPosition.y );
		Coordinate groundStart = toGround( context, worldStart );
		if( groundStart == null) return;
			
		Coordinate groundNorth = moveNorth( groundStart ); // move a "little" way north
		Coordinate worldNorth = fromGround( context, groundNorth );
			
		double theta = theta( worldStart, worldNorth );
		double distance = context.getViewportModel().getPixelSize().y * 20.0;

		Coordinate destination = walk( worldStart, theta, distance );
		//Coordinate destination = worldNorth;
		end = context.worldToPixel( destination );
		
		if( start != null && end != null ){			
			drawArrow( context, start, theta );
		}
	}

	private void drawArrow( MapGraphicContext context, Point here, double theta ) {
		ViewportGraphics g = context.getGraphics();
		AffineTransform t = g.getTransform();
		
		try {
			int nTop = ARROW_HEIGHT + SPACE_ABOVE_N;			
			int arrowCenterX = ARROW_WIDTH / 2;
			int totalHeight = ARROW_HEIGHT + SPACE_ABOVE_N + g.getFontAscent();

            AffineTransform t1 = g.getTransform();
            int x = here.x;
            if (x < 0){
            	x = context.getMapDisplay().getWidth() + x - ARROW_WIDTH;
            }else{
            	x = here.x + ARROW_WIDTH;
            }
            
            int y = here.y;
            if (y < 0){
            	y = context.getMapDisplay().getHeight() + y;
            }else{
            	y = here.y + totalHeight;
            }
            
            t1.translate( x, y);
            
            t1.scale( -1.0, -1.0 );
            t1.rotate( Math.PI / 2 );
            t1.rotate( -theta );
            g.setTransform( t1 );            
            g.setStroke(ViewportGraphics.LINE_SOLID, 1);			
			
			Point tip = new Point(arrowCenterX, BOTTOM_INSET);
			Point centerBase = new Point(arrowCenterX, ARROW_HEIGHT);
			Point bottomLeft = new Point(0, 0);
			Point bottomRight = new Point(ARROW_WIDTH, 0);
			
			//This polygon is drawn on the left, but then gets rotated
			//so it actually appears on the right if North is up.
			Polygon left = new Polygon();
			left.addPoint(centerBase.x, centerBase.y);
			left.addPoint(tip.x, tip.y); 
			left.addPoint(bottomLeft.x, bottomLeft.y); 			
			g.setColor( Color.black );
			g.fill(left);
			g.draw(left);
			
			Polygon right = new Polygon();
			right.addPoint(centerBase.x, centerBase.y);
			right.addPoint(tip.x, tip.y);
			right.addPoint(bottomRight.x, bottomRight.y);
			g.setColor( Color.white );
			g.fill(right);
			g.setColor( Color.black );
			g.draw(right);

			//TODO: center the N properly.  presently it relies on the default font and font size
            // to be some particular values
            g.setColor(Color.BLACK);
            g.drawString("N", arrowCenterX-5, nTop, ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_MIDDLE); //$NON-NLS-1$
            
			
		} finally {
			g.setTransform( t );
		}
		
    }

	private Coordinate walk(Coordinate ground, double theta, double d ) {
		double dx = Math.cos(theta)*d;
		double dy = Math.sin(theta)*d;
		return new Coordinate( ground.x+dx, ground.y+dy);  
	}

	private double theta(Coordinate ground, Coordinate north) {
	    return Math.atan2(Math.abs(north.y - ground.y), Math.abs(north.x - ground.x));
	}

	/** A coordinate that is slightly north please */
	private Coordinate moveNorth(Coordinate ground) {
		double up = ground.y+0.1;
		if( up > 90.0 ){
			return new Coordinate( ground.x, 90.0 );
		}
		return new Coordinate( ground.x, up);
	}

	@SuppressWarnings("unused")
	private Coordinate moveNorth(Coordinate ground, Double distance) {
		double up = ground.y + distance;
		if( ground.y < 90.0 ) {
			return new Coordinate( ground.x, up );
		}
		else return null;
	}
	static CoordinateReferenceSystem GROUND;
	static {
		try {
			GROUND = CRS.decode("EPSG:4326"); //$NON-NLS-1$
		} catch (FactoryException e) {
			GROUND = DefaultGeographicCRS.WGS84;
		}
	}
	
	/** Will transform there into ground WGS84 coordinates or die (ie null) trying */
	private Coordinate toGround(MapGraphicContext context, Coordinate there) {
		
		if( GROUND.equals( context.getCRS()) ){
			return there;
		}
		try {
			MathTransform transform = CRS.findMathTransform( context.getCRS(), GROUND );
			return JTS.transform( there, null, transform );			
		} catch (FactoryException e) {
			e.printStackTrace();
			return null;
		} catch (TransformException e) {
			// yes I do
			return null;
		}
	}
	
	private Coordinate fromGround(MapGraphicContext context, Coordinate ground) {
		
		if( GROUND.equals( context.getCRS()) ){
			return ground;
		}
		try {
			MathTransform transform = CRS.findMathTransform( GROUND, context.getCRS() );
			return JTS.transform( ground, null, transform );			
		} catch (FactoryException e) {
			// I hate you
			return null;
		} catch (TransformException e) {
			// yes I do
			return null;
		}
	}
	
	/** Replace w/ lookup to style black board when the time comes */
	private Point start(MapGraphicContext context) {
		Point point = null;		
		IBlackboard style = context.getLayer().getStyleBlackboard();
		
		//lets see if there is a location style; this is set when the move mapgraphic
		//tool is used
		try{
			Rectangle r = (Rectangle)style.get(LocationStyleContent.ID);
			if (r != null){
				point = new Point(r.x, r.y);
			}
		}catch( Exception evil ){
			evil.printStackTrace();
		}
		
		if( point == null ){ // default!
			point = new Point( 25,25 );
			style.put(NorthArrowTool.STYLE_BLACKBOARD_KEY, point );
		}
		return point;
	}
	
}
