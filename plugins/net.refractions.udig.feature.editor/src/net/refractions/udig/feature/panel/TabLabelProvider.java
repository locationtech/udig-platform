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
package net.refractions.udig.feature.panel;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;


public class TabLabelProvider extends LabelProvider {
    ILabelProvider delegate;
    
    public TabLabelProvider(){
        this(null);
    }
    public TabLabelProvider( ILabelProvider delegate ){
        this.delegate = delegate;
    }
    
    @Override
    public String getText( Object element ) {
        if( element instanceof StructuredSelection ){
            StructuredSelection sel = (StructuredSelection) element;
            if (sel.isEmpty()){
                return "Please select a feature";
            }
            element = sel.getFirstElement();
        }
        if( element == null ){
            return null;
        }
        if( element instanceof SimpleFeature ){
            SimpleFeature feature = (SimpleFeature) element;
            if( delegate != null ){
                String text = delegate.getText( feature );
                if( text != null ){
                    return text;
                }
            }
            return feature.getID();
        }
        if( element instanceof FeaturePanelTabDescriptor ){
            FeaturePanelTabDescriptor tabDescriptor = (FeaturePanelTabDescriptor) element;
            String title = tabDescriptor.getEntry().getTitle();
            if( title == null || title.length() == 0 ){
                title = tabDescriptor.getLabel();
            }
            return title;
        }
        else {
            String text = element.toString();
            return text;
        }
    }
}
