package net.refractions.udig.project.geoselection;

import java.util.ArrayList;
import java.util.Iterator;

import net.refractions.udig.project.ILayer;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Example what is a real IGeoSelection.
 * 
 * @author Vitalus
 */
public class ExampleFeatureGeoSelection extends AbstractGeoSelection {

    final private SimpleFeature feature;

    final private ILayer layer;

    private ArrayList list;

    public ExampleFeatureGeoSelection( SimpleFeature feature, ILayer layer ) {
        super();
        this.feature = feature;
        this.layer = layer;

    }

    public Iterator iterator() {
        if (list == null) {
            list = new ArrayList(2);
            list.add(feature);
            list.add(layer);
        }
        return list.iterator();
    }

    public Object getAdapter( Class adapter ) {
        if (SimpleFeature.class.isAssignableFrom(adapter))
            return feature;
        if (ILayer.class.isAssignableFrom(adapter))
            return layer;

        return null;
    }

}
