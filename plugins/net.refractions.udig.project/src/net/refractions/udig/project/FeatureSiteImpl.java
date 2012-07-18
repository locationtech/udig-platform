package net.refractions.udig.project;

import org.opengis.feature.simple.SimpleFeature;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.IFeatureSite;
import net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl;

/**
 * Facilitate editing of feature content.
 */
public class FeatureSiteImpl extends ToolContextImpl implements IFeatureSite {

    EditFeature editFeature;

    public FeatureSiteImpl() {

    }

    public FeatureSiteImpl( ILayer layer ) {
        this(layer.getMap());
    }

    public FeatureSiteImpl( IMap map ) {
        setMapInternal((Map) map);
    }

    public void setFeature( SimpleFeature feature ) {
        if (feature == null) {
            editFeature = null;
            return;
        }
        if (editFeature != null && feature != null && editFeature.getID().equals(feature.getID())) {
            // they are the same
            return;
        }
        editFeature = new EditFeature(getEditManager(), feature);
    }

    public EditFeature getEditFeature() {
        if( editFeature == null && getEditManager() != null ){
            setFeature( getEditManager().getEditFeature() );
        }
        return editFeature;
    }

    public void setMapInternal( Map map ) {
        if( map == getMap() ){
            return;
        }
        super.setMapInternal(map);
    }

    /**
     * Copy the provided FeatureSite.
     * 
     * @param copy
     */
    public FeatureSiteImpl( FeatureSiteImpl copy ) {
        super(copy);
    }

    public FeatureSiteImpl copy() {
        return new FeatureSiteImpl(this);
    }
}
