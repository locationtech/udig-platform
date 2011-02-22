package net.refractions.udig.project.ui;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.interceptor.ShowViewInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.render.SelectionLayer;

import org.geotools.filter.Filter;

/**
 * Describes how to handle a layer's selection.
 *
 * @author Jesse
 */
public class SelectionStyle{

    /**
     * Don't draw the selections
     */
    public static final SelectionStyle IGNORE = new SelectionStyle(Style.IGNORE);
    /**
     * Overlay the selection on top of the normal rendering
     */
    public static final SelectionStyle OVERLAY = new SelectionStyle(Style.OVERLAY);
    /**
     * Only draw the selection but as normal features
     */
    public static final SelectionStyle EXCLUSIVE = new SelectionStyle(Style.EXCLUSIVE);
    /**
     * Only draw the selection but using the selection style
     */
    public static final SelectionStyle EXCLUSIVE_SELECTION = new SelectionStyle(Style.EXCLUSIVE_SELECTION);
    /**
     * Draw the selection when there is a selection, draw the rest as normal
     */
    public static final SelectionStyle EXCLUSIVE_ALL = new SelectionStyle(Style.EXCLUSIVE_ALL);
    /**
     * Draw the selection when there is a selection, draw the rest as normal
     * <p>
     * The selection is to be drawn using the selection style
     * </p>
     */
    public static final SelectionStyle EXCLUSIVE_ALL_SELECTION = new SelectionStyle(Style.EXCLUSIVE_ALL_SELECTION);

    private Style style;

   protected SelectionStyle() {
       // for extenders
   }

    public SelectionStyle( Style style ) {
        this.style = style;
    }

    protected List<Layer> handleSelection(List<Layer> layersInternal) {

        ArrayList<Layer> toRender = new ArrayList<Layer>(layersInternal);
        for (Layer layer : layersInternal) {
            Filter selectionFilter = layer.getFilter();
            switch (style) {
            case EXCLUSIVE:
                if( selectionFilter!=Filter.ALL){
                    layer.getStyleBlackboard().put(ShowViewInterceptor.KEY, selectionFilter);
                } else {
                    toRender.remove(layer);
                }
                break;
            case EXCLUSIVE_ALL:
                if( selectionFilter!=Filter.ALL){
                    layer.getStyleBlackboard().put(ShowViewInterceptor.KEY, selectionFilter);
                }
                break;
            case EXCLUSIVE_SELECTION:
                if( selectionFilter!=Filter.ALL){
                    SelectionLayer selectionLayer = new SelectionLayer(layer);
                    selectionLayer.getStyleBlackboard().put(ShowViewInterceptor.KEY, selectionFilter);
                    toRender.set(toRender.indexOf(layer), selectionLayer);
                }else{
                    toRender.remove(layer);
                }
                break;
            case EXCLUSIVE_ALL_SELECTION:
                if( selectionFilter!=Filter.ALL){
                    SelectionLayer selectionLayer = new SelectionLayer(layer);
                    selectionLayer.getStyleBlackboard().put(ShowViewInterceptor.KEY, selectionFilter);
                    toRender.set(toRender.indexOf(layer), selectionLayer);
                }
                break;
            case OVERLAY:
                if( selectionFilter!=Filter.ALL){
                    toRender.add(0, new SelectionLayer(layer));
                }
                break;

            default:
                break;
            }

        }
        return toRender;
    }



    private static enum Style{
        IGNORE,
        OVERLAY,
        EXCLUSIVE,
        EXCLUSIVE_SELECTION,
        EXCLUSIVE_ALL,
        EXCLUSIVE_ALL_SELECTION;
    }

    /**
     * Returns a name (and identifier) for this object
     *
     * @return  a name (and identifier) for this object
     */
    public String name() {
        return style.name();
    }

    /**
     * Given a name returned by {@link #name()} this class returns one of the defaults
     *
     * @param name name of the {@link SelectionStyle}
     * @return null or one of the default instances
     */
    public static SelectionStyle valueOf( String name ) {
        Style style2 = Style.valueOf(name);
        if( style2!=null ){
            return new SelectionStyle(style2);
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((style == null) ? 0 : style.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SelectionStyle other = (SelectionStyle) obj;
        if (style == null) {
            if (other.style != null)
                return false;
        } else if (!style.equals(other.style))
            return false;
        return true;
    }

}
