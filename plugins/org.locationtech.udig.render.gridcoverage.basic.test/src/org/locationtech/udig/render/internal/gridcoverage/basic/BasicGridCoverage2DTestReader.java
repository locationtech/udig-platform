/* uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2018, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.render.internal.gridcoverage.basic;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.parameter.Parameter;
import org.geotools.parameter.ParameterGroup;
import org.opengis.coverage.grid.Format;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;

public class BasicGridCoverage2DTestReader extends AbstractGridCoverage2DReader {

    private int reads;

    public int getReads() {
        return reads;
    }

    public BasicGridCoverage2DTestReader() {
        this.reads = 0;
    }

    @Override
    public Format getFormat() {
        return new Format() {

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public String getDocURL() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public ParameterValueGroup getReadParameters() {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("name", "BasicGridCoverage2DFormat"); //$NON-NLS-1$//$NON-NLS-2$
                paramMap.put(AbstractGridFormat.READ_GRIDGEOMETRY2D.getName().toString(),
                        new GridGeometry2D(new Rectangle(), new Rectangle()));

                GeneralParameterValue[] gpv = new GeneralParameterValue[1];
                gpv[0] = new Parameter<GridGeometry2D>(AbstractGridFormat.READ_GRIDGEOMETRY2D);
                return new ParameterGroup(paramMap, gpv);
            }

            @Override
            public String getVendor() {
                return null;
            }

            @Override
            public String getVersion() {
                return null;
            }

            @Override
            public ParameterValueGroup getWriteParameters() {
                return null;
            }

        };
    }

    @Override
    public GridCoverage2D read(GeneralParameterValue[] arg0)
            throws IllegalArgumentException, IOException {
        reads++;
        return null;
    }

}
