package net.refractions.udig.project.ui.internal.actions;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.internal.LayersView;
import net.refractions.udig.ui.IDropAction;
import net.refractions.udig.ui.ViewerDropLocation;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Action that moves layers when a layer within a map is dropped on a layer within the same map.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class LayerDropAction extends IDropAction {

    @Override
    public boolean accept( ) {
        if( !(getDestination() instanceof Layer) || !(getData() instanceof Layer))
            return false;
        Layer destination2 = (Layer)getDestination();
        Layer data2 = (Layer)getData();
        if( data2==destination2 && getViewerLocation()!=ViewerDropLocation.NONE )
            return false;

        if( (destination2).getMap()==(data2).getMap())
            return true;
        return false;

    }

    @Override
    public void perform( IProgressMonitor monitor ) {
        Layer layer = (Layer) getData();
        Layer target = (Layer) getDestination();

        ViewerDropLocation location = getViewerLocation();

        if (location == ViewerDropLocation.NONE) {
            layer.setZorder(0);
            return;
        }
        if( Math.abs(layer.getZorder()-target.getZorder())==1 ){
            int tmp=layer.getZorder();
            layer.setZorder(target.getZorder());
            target.setZorder(tmp);
            return;
        }

        // Moving something AFTER a layer is the same as moving something BEFORE a layer.
        // So we will use BEFORE as much as possible to prevent duplication here.
        // This code will retrieve the layer before. Or the first one, if we are at the
        // beginning of the list.
        if (location == ViewerDropLocation.BEFORE  ) {
            int i = target.getZorder();

            if( layer.getZorder()>target.getZorder()){
                i++;
            }

            if (i > layer.getMap().getMapLayers().size() ) {
                layer.setZorder(layer.getMap().getMapLayers().size());
                return;
            }
            if (layer.equals(target)) {
                return;
            }
            layer.setZorder(i);

        }else if (location == ViewerDropLocation.ON  ) {
            int i = target.getZorder();

//            if( layer.getZorder()>target.getZorder()){
//                i--;
//            }

            if (layer.equals(target)) {
                return;
            }
            layer.setZorder(i);

        }else{
            int i = target.getZorder();

          if( layer.getZorder()<target.getZorder()){
              i--;
          }
            if( i<0 )
                i=0;
            layer.setZorder(i);
        }
    }
}
