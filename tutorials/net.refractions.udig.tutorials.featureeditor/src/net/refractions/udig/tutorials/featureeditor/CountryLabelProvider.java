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
