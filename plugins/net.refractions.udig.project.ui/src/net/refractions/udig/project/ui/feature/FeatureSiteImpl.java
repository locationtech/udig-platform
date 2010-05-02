package net.refractions.udig.project.ui.feature;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.IFeatureSite;
import net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl;

import org.opengis.feature.simple.SimpleFeature;

/**
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getContextModel
 * <em>Context Model</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getPixelSize <em>
 * Pixel Size</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getViewportModel
 * <em>Viewport Model</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getEditManager <em>
 * Edit Manager</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getRenderManager
 * <em>Render Manager</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getMapDisplay <em>
 * Map Display</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getMap <em>Map
 * </em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getProject <em>
 * Project</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getViewportPane
 * <em>Viewport Pane</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getDrawFactory <em>
 * Draw Factory</em>}</li>
 * <li>{@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getEditFactory <em>
 * Edit Factory</em>}</li>
 * <li>
 * {@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getNavigationFactory
 * <em>Navigation Factory</em>}</li>
 * <li>
 * {@link net.refractions.udig.project.ui.internal.tool.impl.ToolContextImpl#getSelectionFactory
 * <em>Selection Factory</em>}</li>
 * </ul>
 * </p>
 */
public class FeatureSiteImpl extends ToolContextImpl implements IFeatureSite {

    EditFeature editFeature;
    
    public FeatureSiteImpl() {
    }
    
    public FeatureSiteImpl( ILayer layer ) {
        this(layer.getMap());
    }

    public FeatureSiteImpl( IMap map ) {
        setMapInternal( (Map) map );
    }

    public EditFeature getEditFeature(){
        return editFeature;        
    }
    
    public void setMapInternal( Map newMapInternal ) {
        super.setMapInternal(newMapInternal);
        editFeature =  new EditFeature( getEditManager() );
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
