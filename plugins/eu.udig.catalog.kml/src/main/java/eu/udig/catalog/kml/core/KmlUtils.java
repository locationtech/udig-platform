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
package eu.udig.catalog.kml.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.kml.KML;
import org.geotools.kml.KMLConfiguration;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.Encoder;
import org.geotools.xml.StreamingParser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Geometry;

import eu.udig.catalog.kml.internal.Messages;

/**
 * Utilities to convert kml to features and back (taken from geotools testcases).
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 * @author Frank Gasdorf
 */
public class KmlUtils {

    
    @SuppressWarnings("nls")
    public static final String[] IGNORED_ATTR = {"LookAt", "Style", "Region"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    
    public static final String KML_FILE_EXTENSION = "kml"; //$NON-NLS-1$
    
    public static final String KMZ_FILE_EXTENSION = "kmz"; //$NON-NLS-1$

    public static final String[] SUPPORTED_FILE_EXTENSIONS = new String [] {
                        toFilterExtension(KmlUtils.KML_FILE_EXTENSION), 
                        toFilterExtension(KmlUtils.KMZ_FILE_EXTENSION)};

    /**
     * Transform a kml file in a {@link SimpleFeatureCollection}.
     * 
     * @param kml the file to convert.
     * @return trhe generated feature collection.
     * @throws Exception
     */
    public static SimpleFeatureCollection kmlFile2FeatureCollection( File kml ) throws Exception {
        InputStream inputStream = null;
        if (kml.getName().toLowerCase().endsWith(KMZ_FILE_EXTENSION)) {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(kml));
            ZipEntry entry = zis.getNextEntry();
            while (entry != null && !entry.getName().endsWith(KML_FILE_EXTENSION)) {
                entry = zis.getNextEntry();
            }
            if (entry == null) {
                throw new Exception(Messages.getString("KmlUtils.Error.NoKMLfileInKMZPackage")); //$NON-NLS-1$
            }
            inputStream = zis;
        } else {
            inputStream = new FileInputStream(kml);
        }
        
        StreamingParser parser = new StreamingParser(new KMLConfiguration(), inputStream, KML.Placemark);

        SimpleFeatureCollection newCollection = FeatureCollections.newCollection();
        int index = 0;
        SimpleFeature f;
        DefaultGeographicCRS crs = DefaultGeographicCRS.WGS84;
        while( (f = (SimpleFeature) parser.parse()) != null ) {
            Geometry geometry = (Geometry) f.getDefaultGeometry();

            SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
            b.setName(kml.getName());
            b.setCRS(crs);
            b.add("the_geom", Geometry.class); //$NON-NLS-1$
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

        OutputStream fos = null;
        try {
            if (kmlFile.getName().toLowerCase().endsWith(KMZ_FILE_EXTENSION)) {
                String fileName = kmlFile.getName();
                String entryName = fileName.replace(KMZ_FILE_EXTENSION, KML_FILE_EXTENSION);
                
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(kmlFile));
                zos.putNextEntry(new ZipEntry(entryName)); //$NON-NLS-1$
                fos = zos;
            } else {
                fos = new FileOutputStream(kmlFile);
            }
    
            Encoder encoder = new Encoder(new KMLConfiguration());
            encoder.setIndenting(true);
    
            encoder.encode(newCollection, KML.kml, fos);
        } finally {
            if (fos != null) {
                fos.close();
                fos = null;
            }
        }
    }

    private static String toFilterExtension(String fileExtension) {
        return "*." + fileExtension;  //$NON-NLS-1$
    }

}
