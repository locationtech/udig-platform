/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.feature.util;

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
