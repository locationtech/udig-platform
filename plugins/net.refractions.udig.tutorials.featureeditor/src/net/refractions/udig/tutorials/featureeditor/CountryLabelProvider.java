/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tutorials.featureeditor;

import org.eclipse.jface.viewers.LabelProvider;
import org.opengis.feature.simple.SimpleFeature;

public class CountryLabelProvider extends LabelProvider {
    public CountryLabelProvider() {
        int a=0;
    }
    @Override
    public String getText( Object element ) {
        if( element instanceof SimpleFeature){
            SimpleFeature feature = (SimpleFeature) element;
            return (String) feature.getAttribute("CNTRY_NAME");
        }
        return null;
    }

}
