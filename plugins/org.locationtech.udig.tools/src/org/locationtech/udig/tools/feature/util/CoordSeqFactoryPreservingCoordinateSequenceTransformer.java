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
package org.locationtech.udig.tools.feature.util;

// OpenGIS dependencies
import org.geotools.geometry.jts.DefaultCoordinateSequenceTransformer;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;

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
