package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.IMapCompositionListener;
import net.refractions.udig.project.MapCompositionEvent;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class LegendViewFiltersHandler implements IMapCompositionListener {

    private Action toggleMgLayerAction;
    private Action toggleBgLayerAction;
    
    private MapGraphicLayerFilter mgLayerFilter;
    private BackgroundLayerFilter bgLayerFilter;
    
    private LegendView view;
    private Map map;
    
    public LegendViewFiltersHandler(LegendView view) {
        this.view = view;
        this.mgLayerFilter = new MapGraphicLayerFilter();
        this.bgLayerFilter = new BackgroundLayerFilter();
    }
    
    public void setMap(Map map) {
        cleanHandler();
        initMap(map);
        setToggleLayersActionState();
    }
    
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
    
    private void cleanHandler() {
        if (map != null) {
            map.removeMapCompositionListener(this);
            map = null;
        }
    }
    
    private void initMap(Map map) {
        this.map = map;
        if (map != null) {
            this.map.addMapCompositionListener(this);
        }
    }
    
    public Action getToggleMgAction() {
        
        if (toggleMgLayerAction == null) {
            toggleMgLayerAction = new FilterAction(null, IAction.AS_CHECK_BOX,
                    Messages.LegendView_show_mg_tooltip, Messages.LegendView_hide_mg_tooltip){
                @Override
                public void run() {
                    mgLayerFilter.setShowLayer(!this.isChecked());
                    super.run();
                }
            };
            toggleMgLayerAction.setToolTipText(Messages.LegendView_hide_mg_tooltip);
            toggleMgLayerAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                    ISharedImages.D_ADD_CO));            
        }
        
        return toggleMgLayerAction;
    }

    public Action getToggleBgAction() {
        
        if (toggleBgLayerAction == null) {
            toggleBgLayerAction = new FilterAction(null, IAction.AS_CHECK_BOX,
                    Messages.LegendView_show_bg_tooltip, Messages.LegendView_hide_bg_tooltip){
                @Override
                public void run() {
                    bgLayerFilter.setShowLayer(!this.isChecked());
                    super.run();
                }
            };
            toggleBgLayerAction.setToolTipText(Messages.LegendView_hide_bg_tooltip);
            toggleBgLayerAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                    ISharedImages.D_ADD_CO));            
        }
                
        return toggleBgLayerAction;
    }
    
    public void setToggleLayersActionState() {
        
        if (map == null) {
            setLayerActionsEnabled(false);
        } else {
            if (map.getLayersInternal().size() > 0) {
                setLayerActionsEnabled(true);
            } else {
                setLayerActionsEnabled(false);
            }
        }
        
    }
    
    private void setLayerActionsEnabled(boolean enabled) {
        if (toggleMgLayerAction != null) {
            toggleMgLayerAction.setEnabled(enabled);
        }
        if (toggleBgLayerAction != null) {
            toggleBgLayerAction.setEnabled(enabled);
        }
    }
    
    public ViewerFilter[] getFilters() {
        return new ViewerFilter[]{this.mgLayerFilter, this.bgLayerFilter};
    }
        
    //Action classes
    private class FilterAction extends Action {
        
         private String showTooltip;
         private String hideTooltip;
        
         public FilterAction(String text, int style, String showTooltip, String hideTooltip) {
             super(text, style);
             this.showTooltip = showTooltip;
             this.hideTooltip = hideTooltip;
             setToolTipText(hideTooltip);
         }
         
         @Override
        public void run() {
             final boolean showLayer = this.isChecked();
             if (showLayer) { 
                 setToolTipText(showTooltip);
             } else {
                 setToolTipText(hideTooltip);
             }
             LegendView.getViewer().refresh();
             view.updateCheckboxes();
        }
         
    }
    
    
    //Filter classes
    private abstract class AbstractLayerFilter extends ViewerFilter {

        private boolean showLayer = true;
        
        public void setShowLayer( boolean showLayer ) {
            this.showLayer = showLayer;
        }

        @Override
        public boolean select( Viewer viewer, Object parentElement, Object element ) {
            final Layer layer = (Layer) element;
            if (!this.showLayer && isLayerType(layer)) {
                return false;
                
            }
            return true;
        }

        protected abstract boolean isLayerType(Layer layer);
        
    }
    
    private class MapGraphicLayerFilter extends AbstractLayerFilter {
        @Override
        protected boolean isLayerType( Layer layer ) {
            return LegendViewUtils.isMapGraphicLayer(layer);
        }
    }

    private class BackgroundLayerFilter extends AbstractLayerFilter {
        @Override
        protected boolean isLayerType( Layer layer ) {
            return LegendViewUtils.isBackgroundLayer(layer);
        }
    }

    //IMapCompositionListener method
    @Override
    public void changed( MapCompositionEvent event ) {
        setToggleLayersActionState();
    }
    
}
