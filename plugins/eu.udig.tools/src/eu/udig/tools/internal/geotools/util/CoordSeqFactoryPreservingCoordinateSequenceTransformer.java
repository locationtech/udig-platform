/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to license under Lesser General Public License (LGPL).
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
package eu.udig.tools.internal.geotools.util;

// OpenGIS dependencies
import org.geotools.geometry.jts.DefaultCoordinateSequenceTransformer;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;

/**
 * Coordinate Sequence preserving the Coordinate Sequence Transformer
 * <p>
 * This transformer extends GeoTools
 * {@link DefaultCoordinateSequenceTransformer} The base class uses a user
 * provided CoordinateSequenceFactory. The rational is that
 * DefaultCoordinateSequenceTransformer uses JTS {@code
 * DefaultCoordinateSequenceFactory} then we need to preserve the one used in
 * the GeometryFactory being used by uDig. (aka,
 * {@link LiteCoordinateSequenceFactory}.
 * </p>
 * <p>
 * Note: this issue was solved in GeoTools 2.4. TODO: This class will be
 * deprecated when uDig will be update with GeoTools 2.4
 * </p>
 * 
 * @since 1.1.0
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 */
class CoordSeqFactoryPreservingCoordinateSequenceTransformer extends DefaultCoordinateSequenceTransformer {

	/**
	 * The coordinate sequence factory to use.
	 */
	private final CoordinateSequenceFactory	csFactory;

	/**
	 * Constructs a CoordinateSequenceFactory preserving coordinate sequence
	 * transformer.
	 * 
	 * @param csFactory
	 *            the CoordinateSequenceFactory to use
	 */
	public CoordSeqFactoryPreservingCoordinateSequenceTransformer(CoordinateSequenceFactory csFactory) {
		this.csFactory = csFactory;
	}

	/**
	 * Was re-written to use the factory setted by the client module
	 * {@inheritDoc}
	 */
	@Override
	public CoordinateSequence transform(final CoordinateSequence sequence, final MathTransform transform)
		throws TransformException {

		CoordinateSequence cs = super.transform(sequence, transform);

		return this.csFactory.create(cs);
	}
}
