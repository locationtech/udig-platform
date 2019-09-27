/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.ui;

import java.io.StringReader;

import javax.xml.transform.TransformerException;

import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.geotools.data.DataUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.gml.GMLFilterDocument;
import org.geotools.gml.GMLFilterFeature;
import org.geotools.gml.GMLFilterGeometry;
import org.geotools.gml.GMLReceiver;
import org.geotools.gml.producer.FeatureTransformer;
import org.locationtech.udig.ui.AbstractTextStrategizedTransfer;
import org.locationtech.udig.ui.internal.Messages;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

public class FeatureTextTransfer extends AbstractTextStrategizedTransfer implements
		UDIGTransfer {
    private static FeatureTextTransfer _instance = new FeatureTextTransfer();

    private FeatureTextTransfer() {
    }

    /**
     * Returns the singleton instance of the TextTransfer class.
     * 
     * @return the singleton instance of the TextTransfer class
     */
    public static FeatureTextTransfer getInstance() {
        return _instance;
    }

    private TransferStrategy[] transferStrategies;

    @Override
    public synchronized TransferStrategy[] getAllStrategies() {
        if (transferStrategies == null) {
            transferStrategies = new TransferStrategy[] { new GMLStrategy(), new JtsWktStrategy() };
        }
        TransferStrategy[] copy = new TransferStrategy[transferStrategies.length];
        System.arraycopy(transferStrategies, 0, copy, 0, transferStrategies.length);
        return copy;
    }

    @Override
    public String[] getStrategyNames() {
        return new String[]{Messages.FeatureTextTransfer_strategy_gml_name, Messages.FeatureTextTransfer_strategy_wkt_name};  
    }

    @Override
    public String getTransferName() {
        return Messages.FeatureTextTransfer_transfer_name; 
    }

	
    @Override
    public TransferStrategy getDefaultStrategy() {
        return getAllStrategies()[0];
    }

    @Override
    public TransferData[] getSupportedTypes() {
        return TextTransfer.getInstance().getSupportedTypes();
    }

    @Override
    public boolean isSupportedType(TransferData transferData) {
        return TextTransfer.getInstance().isSupportedType(transferData);
    }

    public boolean validate(Object object) {
        return object instanceof SimpleFeature;
    }

    /**
     * Encodes a SimpleFeature as a GML string.
     * 
     * @author jeichar
     */
    public static class GMLStrategy implements TransferStrategy {

        public void javaToNative(Object object, TransferData transferData) {
            SimpleFeature feature = (SimpleFeature) object;
            DefaultFeatureCollection collection = new DefaultFeatureCollection();
            collection.add(feature);
            FeatureTransformer transformer = new FeatureTransformer();
            transformer.setIndentation(4);
            try {
                TextTransfer.getInstance().javaToNative(transformer.transform(collection),
                        transferData);
            } catch (TransformerException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }

        public Object nativeToJava(TransferData transferData) {
            String string = (String) TextTransfer.getInstance().nativeToJava(transferData);
            InputSource input = new InputSource(new StringReader(string));
            DefaultFeatureCollection collection = new DefaultFeatureCollection();
            GMLReceiver receiver = new GMLReceiver(collection);
            GMLFilterFeature filterFeature = new GMLFilterFeature(receiver);
            GMLFilterGeometry filterGeometry = new GMLFilterGeometry(filterFeature);
            GMLFilterDocument filterDocument = new GMLFilterDocument(filterGeometry);
            try {
                // parse xml
                XMLReader reader = XMLReaderFactory.createXMLReader();
                reader.setContentHandler(filterDocument);
                reader.parse(input);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return collection.features().next();
        }
    }

    /**
     * This strategy exports a feature as Well Known Text as generated by JTS WKTWriter.
     * 
     * @author jeichar
     */
    public static class JtsWktStrategy implements TransferStrategy {
                
        /**
         * @see Transfer#javaToNative
         */
        public void javaToNative(Object object, TransferData transferData) {
            String stringToEncode;

            SimpleFeature feature = (SimpleFeature) object;

            WKTWriter writer = new WKTWriter();
            String geometry = writer.writeFormatted((Geometry)feature.getDefaultGeometry());

            stringToEncode = geometry;
            TextTransfer.getInstance().javaToNative(stringToEncode, transferData);
        }

        /**
         * @see Transfer#nativeToJava
         */
        @SuppressWarnings("deprecation") 
        public Object nativeToJava(TransferData transferData) {
            String string = (String) TextTransfer.getInstance().nativeToJava(
                    transferData);

            WKTReader reader = new WKTReader();
            try {
                Geometry read = reader.read(string);
                SimpleFeatureType ft=DataUtilities.createType("Temp Type", "*geom:"+read.getClass().getName()); //$NON-NLS-1$ //$NON-NLS-2$
                
                return SimpleFeatureBuilder.build( ft, new Object[]{read}, null );
            } catch (Exception e) {
                UiPlugin.log("", e); //$NON-NLS-1$
            }
            return null;
        }

    }
    
}
