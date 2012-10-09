/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
