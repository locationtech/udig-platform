/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios) 
 * Axios agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.image.georeferencing.internal.process;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.graphics.Point;

import com.vividsolutions.jts.geom.Coordinate;

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
 * @since 1.0.0
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
