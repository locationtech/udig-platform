/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerLegendItem;
import org.locationtech.udig.project.internal.Map;

/**
 * The Filters Handler of the Legend View. This class is designed to handle the maintenance of the
 * Map Graphic Layers and Background Layers toggle functionality.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class LegendViewFiltersHandler {

    private Action toggleMgLayerAction;
    private Action toggleBgLayerAction;
    
    private MapGraphicLayerFilter mgLayerFilter;
    private BackgroundLayerFilter bgLayerFilter;
    
    private LegendView view;
    private Map map;
    
    /**
     * For test case use only
     * @return MapGraphicLayerFilter
     */
    public MapGraphicLayerFilter getMgLayerFilter() {
        return mgLayerFilter;
    }

    /**
     * For test case use only
     * @return BackgroundLayerFilter
     */
    public BackgroundLayerFilter getBgLayerFilter() {
        return bgLayerFilter;
    }

    public void setBgLayerFilter(boolean isOn) {
        bgLayerFilter.setShowLayer(isOn);
    }
    
    public boolean isBgInViewer(Viewer viewer, Object parent, Object element) {
        return bgLayerFilter.select(viewer, parent, element);
    }
    
    /**
     * Creates a LegendViewFiltersHandler
     * @param view
     */
    public LegendViewFiltersHandler(LegendView view) {
        this.view = view;
        this.mgLayerFilter = new MapGraphicLayerFilter();
        this.bgLayerFilter = new BackgroundLayerFilter();
    }
    
    /**
     * Sets the current map
     * @param map
     */
    public void setMap(Map map) {
        cleanHandler();
        initMap(map);
        setToggleLayersActionState();
    }
    
    /**
     * Cleans up the handler of listeners and objects.
     */
    public void disposeHandler() {
        
        cleanHandler();
        
        if (toggleBgLayerAction != null) {
            toggleBgLayerAction = null;    
        }
        if (toggleMgLayerAction != null) {
            toggleMgLayerAction = null;    
        }
        if (mgLayerFilter != null) {
            mgLayerFilter = null;
        }
        if (bgLayerFilter != null) {
            bgLayerFilter = null;
        }
        if (view != null) {
            view = null;    
        }
    }
    
    /**
     * Cleans the handler of listeners attached to the current map and the current map itself.
     */
    private void cleanHandler() {
        if (map != null) {
            map = null;
        }
    }
    
    /**
     * Initialises the current map and adds listeners to it.
     * @param map
     */
    private void initMap(Map map) {
        this.map = map;
    }
    
    /**
     * Initialises and returns the toggleMgLayerAction action.  
     * @return toggleMgLayerAction
     */
    public Action getToggleMgAction() {
        
        if (toggleMgLayerAction == null) {
            toggleMgLayerAction = new FilterAction(null, IAction.AS_CHECK_BOX){
                @Override
                public void run() {
                    mgLayerFilter.setShowLayer(!this.isChecked());
                    super.run();
                }
            };
            toggleMgLayerAction.setToolTipText(Messages.LegendView_hide_mg_tooltip);
            toggleMgLayerAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                    ISharedImages.TOG_MAP_GRAPHIC_CO));
        }
        
        return toggleMgLayerAction;
    }

    /**
     * Initialises and returns the toggleBgLayerAction action.
     * @return toggleBgLayerAction
     */
    public Action getToggleBgAction() {
        
        if (toggleBgLayerAction == null) {
            toggleBgLayerAction = new FilterAction(null, IAction.AS_CHECK_BOX){
                @Override
                public void run() {
                    bgLayerFilter.setShowLayer(!this.isChecked());
                    super.run();
                }
            };
            toggleBgLayerAction.setToolTipText(Messages.LegendView_hide_bg_tooltip);
            toggleBgLayerAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                    ISharedImages.TOG_BG_LAYER_CO));            
        }
                
        return toggleBgLayerAction;
    }
    
    /**
     * Handles the maintenance of the enabled/disabled state of the toggle actions.
     */
    public void setToggleLayersActionState() {
        
        if (map == null) {
            setLayerActionsEnabled(false);
        } else {
            if (LegendViewUtils.getLayers(map.getLegend(), false).size() > 0) {
                setLayerActionsEnabled(true);
            } else {
                setLayerActionsEnabled(false);
            }
        }
        
    }
    
    /**
     * Handles the enabling/disabling of the toggle actions.
     * @param enabled
     */
    private void setLayerActionsEnabled(boolean enabled) {
        if (toggleMgLayerAction != null) {
            toggleMgLayerAction.setEnabled(enabled);
        }
        if (toggleBgLayerAction != null) {
            toggleBgLayerAction.setEnabled(enabled);
        }
    }
    
    /**
     * Returns the filters for the map graphics and background layers toggling in the viewer.
     * @return
     */
    public ViewerFilter[] getFilters() {
        return new ViewerFilter[]{this.mgLayerFilter, this.bgLayerFilter};
    }
        
    /**
     * This abstract class is designed to be the base implementation of the toggle action buttons.
     * Provides mechanisms to toggle the tooltip of the button and call a method to refresh the
     * viewer.
     */
    private class FilterAction extends Action {
        
         public FilterAction(String text, int style) {
             super(text, style);
         }
         
         @Override
        public void run() {
             //Apply filter on viewer elements
             view.getViewer().refresh();
             //Set checkbox status
             LegendViewCheckboxUtils.updateCheckboxesAsync(view);
        }
         
    }
    
    /**
     * This abstract class provides the base implementation of the viewer filter used to manage
     * filtering the map graphics and background layers.
     */
    private abstract class AbstractLayerFilter extends ViewerFilter {

        private boolean showLayer = true;
        
        public void setShowLayer( boolean showLayer ) {
            this.showLayer = showLayer;
        }

        @Override
        public boolean select( Viewer viewer, Object parentElement, Object element ) {
            if (element instanceof LayerLegendItem) {
                final LayerLegendItem layerItem = (LayerLegendItem) element;
                final Layer layer = layerItem.getLayer();
                if (!this.showLayer && isLayerType(layer)) {
                    return false;

                }
            }
            return true;
        }

        protected abstract boolean isLayerType(Layer layer);
        
    }
    
    /**
     * This class provides the implementation of the map graphics filter.
     */
    private class MapGraphicLayerFilter extends AbstractLayerFilter {
        @Override
        protected boolean isLayerType( Layer layer ) {
            return LegendViewUtils.isMapGraphicLayer(layer);
        }
    }

    /**
     * This class provides the implementation of the background layers filter.
     */
    private class BackgroundLayerFilter extends AbstractLayerFilter {
        @Override
        protected boolean isLayerType( Layer layer ) {
            return LegendViewUtils.isBackgroundLayer(layer);
        }
    }

    /**
     * Refreshes the toggle filter buttons display
     */
    public void refresh() {
        setToggleLayersActionState();
    }
    
}
