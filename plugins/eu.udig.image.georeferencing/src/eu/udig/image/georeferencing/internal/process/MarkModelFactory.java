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


/**
 * 
 * Factory used to create mark models. Singleton class.
 * 
 * It's responsible of assigning a unique ID to each mark during the current
 * session.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.0.0
 * 
 */
public final class MarkModelFactory {

	private static int				ID_COUNT	= 0;

	private static MarkModelFactory	THIS		= new MarkModelFactory();

	/**
	 * use the factory method {@link getInstance()}
	 */
	private MarkModelFactory() {
		// singleton
	}

	/**
	 * Singleton class, so it'll return the instance to this class.
	 * 
	 * @return The instance of this factory.
	 */
	public static MarkModelFactory getInstance() {

		return THIS;
	}

	/**
	 * Creates a new MarkModel and set an ID.
	 * 
	 * @return A new mark model.
	 */
	public MarkModel create() {

		return new MarkModelImpl(String.valueOf(getNewMarkID()));
	}

	/**
	 * Creates a new mark model based on established values.
	 * 
	 * @param id	The ID of the mark.
	 * @param xImage X position respect the image.
	 * @param yImage Y position respect the image.
	 * @param xCoord X coordinate respect the map.
	 * @param yCoord Y coordinate respect the map.
	 * 
	 * @return The created mark model.
	 */
	public MarkModel create(String id, Integer xImage, Integer yImage, Double xCoord, Double yCoord) {

		MarkModel mark = new MarkModelImpl(id);

		mark.setXImage(xImage);
		mark.setYImage(yImage);
		mark.setXCoord(xCoord);
		mark.setYCoord(yCoord);

		updateID(id);

		return mark;
	}

	/**
	 * It'll try to set the internal ID counter match with the given ID.
	 * 
	 * @param id
	 *            ID to match.
	 */
	private synchronized void updateID(String id) {

		try {
			// try to update the ID_COUNT
			int actualId = Integer.parseInt(id);
			if (actualId > ID_COUNT) {
				ID_COUNT = actualId;
			}
		} catch (NumberFormatException e) {
			// do nothing, id is not an integer
		}

	}

	/**
	 * Gets the new id.
	 * 
	 * @return
	 */
	private synchronized static int getNewMarkID() {

		ID_COUNT++;
		return ID_COUNT;
	}

	/**
	 * Sets the internal ID counter to 0.
	 */
	public synchronized static void resetIdSecuence() {
		ID_COUNT = 0;
	}
}
