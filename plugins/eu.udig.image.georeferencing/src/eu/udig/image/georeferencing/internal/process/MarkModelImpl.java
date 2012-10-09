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

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.graphics.Point;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Stores valuable info about each mark or dot.
 * 
 * This class is also an {@link Observable}, so it'll broadcast actions like:
 * New mark, mark modified or mark deleted.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
final class MarkModelImpl extends Observable implements Serializable, MarkModel {


	private static final long	serialVersionUID	= 4462169346430911665L;
	private int					xImage, yImage;
	private Double				xCoord				= Double.NaN, yCoord = Double.NaN;
	private String				ID;

	/**
	 * Constructor. The given string will be the marks ID.
	 * 
	 * @param newMarkID
	 *            The ID of the mark.
	 */
	public MarkModelImpl(String newMarkID) {

		this.ID = newMarkID;
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#getXImage()
	 */
	public int getXImage() {
		return xImage;
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#setXImage(int)
	 */
	public void setXImage(int xImage) {
		this.xImage = xImage;
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#getYImage()
	 */
	public int getYImage() {
		return yImage;
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#setYImage(int)
	 */
	public void setYImage(int yImage) {
		this.yImage = yImage;
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#getXCoord()
	 */
	public Double getXCoord() {
		return xCoord;
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#setXCoord(java.lang.Double)
	 */
	public void setXCoord(Double xCoord) {
		this.xCoord = xCoord;
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#getYCoord()
	 */
	public Double getYCoord() {
		return yCoord;
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#setYCoord(java.lang.Double)
	 */
	public void setYCoord(Double yCoord) {
		this.yCoord = yCoord;
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#getID()
	 */
	public String getID() {

		return this.ID;
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#initializeImagePosition(org.eclipse.swt.graphics.Point)
	 */
	public void initializeImagePosition(Point point) {

		this.xImage = point.x;
		this.yImage = point.y;

		setChanged();
		notifyObservers(MarkModelChange.NEW);
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#updateImagePosition(org.eclipse.swt.graphics.Point)
	 */
	public void updateImagePosition(Point point) {

		this.xImage = point.x;
		this.yImage = point.y;

		setChanged();
		notifyObservers(MarkModelChange.MODIFY);
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#updateCoordinatePosition(com.vividsolutions.jts.geom.Coordinate)
	 */
	public void updateCoordinatePosition(Coordinate coord) {

		this.xCoord = coord.x;
		this.yCoord = coord.y;

		setChanged();
		notifyObservers(MarkModelChange.MODIFY);
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#getImagePosition()
	 */
	public Point getImagePosition() {

		return new Point(xImage, yImage);
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#delete()
	 */
	public void delete() {

		setChanged();
		notifyObservers(MarkModelChange.DELETE);
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#hashCode()
	 */
	@Override
	public int hashCode() {

		return this.getID().hashCode();
	}

	/* (non-Javadoc)
	 * @see eu.udig.image.georeferencing.internal.process.Mark#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder(getID());
		builder.append(";"); //$NON-NLS-1$
		builder.append(getXImage());
		builder.append(";"); //$NON-NLS-1$
		builder.append(getYImage());
		builder.append(";"); //$NON-NLS-1$
		builder.append(getXCoord());
		builder.append(";"); //$NON-NLS-1$
		builder.append(getYCoord());

		return builder.toString();
	}

	public void addObserver(Observer observer) {
		super.addObserver( observer);
	}

	public void deleteObserver(Observer observer) {
		super.deleteObserver( observer);
	}
	
}
