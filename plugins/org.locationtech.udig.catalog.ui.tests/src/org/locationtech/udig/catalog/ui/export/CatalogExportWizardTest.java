/* uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2021, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.catalog.ui.export;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;

public class CatalogExportWizardTest {

    @Test
    public void typeObjectAttributeInSimpleFeatureTypeIsIgnored() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

        builder.setName("testType");
        builder.setNamespaceURI("http://www.geotools.org/");
        builder.setSRS("EPSG:4326");

        builder.add("objectTypeAttribute", Object.class);
        builder.add("intAttribute", Integer.class);
        builder.add("pointProperty", Point.class);

        SimpleFeatureType removeUnsupportedTypes = CatalogExportWizard
                .removeUnsupportedTypes(builder.buildFeatureType(), false);
        assertNull(removeUnsupportedTypes.getType("objectTypeAttribute"));
        assertNotNull(removeUnsupportedTypes.getType("intAttribute"));
        assertTrue(removeUnsupportedTypes
                .getDescriptor("pointProperty") instanceof GeometryDescriptor);
    }
}
