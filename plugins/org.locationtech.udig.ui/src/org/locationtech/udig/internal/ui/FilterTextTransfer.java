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
import org.geotools.filter.FilterHandler;
import org.geotools.gml.GMLFilterDocument;
import org.geotools.gml.GMLFilterGeometry;
import org.geotools.xml.filter.FilterFilter;
import org.geotools.xml.filter.FilterTransformer;
import org.locationtech.udig.ui.AbstractTextStrategizedTransfer;
import org.opengis.filter.Filter;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * To Transfer Filters in text form.
 * 
 * @author jones
 * @since 1.0.0
 */
public class FilterTextTransfer extends AbstractTextStrategizedTransfer implements UDIGTransfer{
    private static FilterTextTransfer _instance = new FilterTextTransfer();
    private FilterTextTransfer() {
    }

    /**
     * Returns the singleton instance of the TextTransfer class.
     * 
     * @return the singleton instance of the TextTransfer class
     */
    public static FilterTextTransfer getInstance() {
        return _instance;
    }

	private TransferStrategy[] transferStrategies ;

	@Override
    public
	synchronized TransferStrategy[] getAllStrategies() {
		if( transferStrategies==null ){
			transferStrategies=new TransferStrategy[]{new GMLFilterStrategy()};
		}

        TransferStrategy[] copy=new TransferStrategy[transferStrategies.length];
        System.arraycopy(transferStrategies, 0, copy, 0, transferStrategies.length);
		return copy;
	}
    
    @Override
    public String[] getStrategyNames() {
        return new String[]{"GML"}; //$NON-NLS-1$
    }
	
    @Override
    public String getTransferName() {
        return "Filter"; //$NON-NLS-1$
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
    
    public boolean validate( Object object ) {
        return object instanceof Filter;
    }

    public static class SimpleFilterHandler extends DefaultHandler implements FilterHandler {

        private org.opengis.filter.Filter filter;

        public void filter( org.opengis.filter.Filter filter ) {
            this.filter = filter;
        }

        public org.opengis.filter.Filter getFilter() {
            return filter;
        }

    }

    private static class GMLFilterStrategy implements TransferStrategy{
        /**
         * @see Transfer#javaToNative
         */
        public void javaToNative( Object object, TransferData transferData ) {
            Filter filter = (Filter) object;
            FilterTransformer transformer = new FilterTransformer();
            transformer.setIndentation(4);
            try {
                String transform = transformer.transform(filter);
                TextTransfer.getInstance().javaToNative(transform, transferData);
            } catch (TransformerException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }

        /**
         * @see Transfer#nativeToJava
         */
        public Object nativeToJava( TransferData transferData ) {
        	String string = (String) TextTransfer.getInstance().nativeToJava(transferData);
            InputSource input = new InputSource(new StringReader(string));
            SimpleFilterHandler simpleFilterHandler = new SimpleFilterHandler();
            FilterFilter filterFilter = new FilterFilter(simpleFilterHandler, null);
            GMLFilterGeometry filterGeometry = new GMLFilterGeometry(filterFilter);
            GMLFilterDocument filterDocument = new GMLFilterDocument(filterGeometry);

            try {
                // parse xml
                XMLReader reader = XMLReaderFactory.createXMLReader();
                reader.setContentHandler(filterDocument);
                reader.parse(input);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return simpleFilterHandler.getFilter();
        }	
    }
}
