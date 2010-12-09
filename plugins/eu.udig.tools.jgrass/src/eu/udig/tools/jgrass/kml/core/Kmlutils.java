/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.tools.jgrass.kml.core;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ui.ApplicationGIS;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.feature.type.GeometryTypeImpl;
import org.geotools.geometry.jts.JTS;
import org.geotools.kml.KML;
import org.geotools.kml.KMLConfiguration;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.Encoder;
import org.geotools.xml.StreamingParser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Utilities to convert kml to features and back (taken from geotools testcases).
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class Kmlutils {

    @SuppressWarnings("nls")
    public static final String[] IGNORED_ATTR = {"LookAt", "Style", "Region"};

    /**
     * Transform a kml file in a {@link SimpleFeatureCollection}.
     * 
     * @param kml the file to convert.
     * @return trhe generated feature collection.
     * @throws Exception
     */
    public static SimpleFeatureCollection kmlFile2FeatureCollection( File kml ) throws Exception {
        StreamingParser parser = new StreamingParser(new KMLConfiguration(), new FileInputStream(kml), KML.Placemark);

        SimpleFeatureCollection newCollection = FeatureCollections.newCollection();
        int index = 0;
        SimpleFeature f;
        DefaultGeographicCRS crs = DefaultGeographicCRS.WGS84;
        while( (f = (SimpleFeature) parser.parse()) != null ) {
            Geometry geometry = (Geometry) f.getDefaultGeometry();

            SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
            b.setName(kml.getName());
            b.setCRS(crs);
            b.add("the_geom", Geometry.class);
            SimpleFeatureType type = b.buildFeatureType();
            SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
            builder.addAll(new Object[]{geometry});
            SimpleFeature feature = builder.buildFeature(type.getTypeName() + "." + index++); //$NON-NLS-1$
            newCollection.add(feature);
        }

        return newCollection;
    }

    /**
     * Writes the {@link FeatureCollection} to disk in KML format.
     * 
     * @param kmlFile the file to write.
     * @param featureCollection the collection to transform.
     * @throws Exception
     */
    public static void writeKml( File kmlFile, SimpleFeatureCollection featureCollection ) throws Exception {
        CoordinateReferenceSystem epsg4326 = DefaultGeographicCRS.WGS84;
        CoordinateReferenceSystem crs = featureCollection.getSchema().getCoordinateReferenceSystem();
        MathTransform mtrans = CRS.findMathTransform(crs, epsg4326, true);

        FeatureCollection<SimpleFeatureType, SimpleFeature> newCollection = FeatureCollections.newCollection();
        FeatureIterator<SimpleFeature> featuresIterator = featureCollection.features();
        while( featuresIterator.hasNext() ) {
            SimpleFeature f = featuresIterator.next();
            Geometry g = (Geometry) f.getDefaultGeometry();
            if (!mtrans.isIdentity()) {
                g = JTS.transform(g, mtrans);
            }
            f.setDefaultGeometry(g);
            newCollection.add(f);
        }

        Encoder encoder = new Encoder(new KMLConfiguration());
        encoder.setIndenting(true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        encoder.encode(newCollection, KML.kml, out);

        String kmlString = new String(out.toByteArray());
        BufferedWriter bW = null;
        try {
            bW = new BufferedWriter(new FileWriter(kmlFile));
            bW.write(kmlString);
        } finally {
            if (bW != null)
                bW.close();
        }
    }

}
