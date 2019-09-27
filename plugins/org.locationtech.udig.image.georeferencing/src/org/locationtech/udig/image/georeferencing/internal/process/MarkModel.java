/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.image.georeferencing.internal.process;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.graphics.Point;

import org.locationtech.jts.geom.Coordinate;

/**
 * Stores valuable info about each mark and its associated coordinates.
 * <p>
 * The marks are points (x,y) in the image. The coordinates are point in the map CRS.  
 * </p>
 * 
 * <p>
 * This class is also an {@link Observable}, so it'll broadcast actions like:
 * New mark, mark modified or mark deleted.
 * </p>
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public interface MarkModel{

	public enum MarkModelChange {
		NEW, MODIFY, DELETE
	};
	
	public void addObserver(Observer observer);

	public void deleteObserver(Observer observer);
	
	public int getXImage();

	public void setXImage(int xImage);

	public int getYImage() ;

	public void setYImage(int yImage);

	public Double getXCoord();

	public void setXCoord(Double xCoord);

	public Double getYCoord();

	public void setYCoord(Double yCoord);

	public String getID();

	public void initializeImagePosition(Point point) ;

	public void updateImagePosition(Point point);

	public void updateCoordinatePosition(Coordinate coord);

	public Point getImagePosition();

	public void delete();


}
