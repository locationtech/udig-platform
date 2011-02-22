package net.refractions.udig.mapgraphic;

import net.refractions.udig.ui.graphics.ViewportGraphics;


/**
 * An object which draws a graphic on a map.
 *
 * @author Justin Deoliveira, Refractions Research Inc.
 * @since 0.6.0
 */
public interface MapGraphic {

    /** extension point id **/
    public static final String XPID = "net.refractions.udig.mapgraphic.mapgraphic"; //$NON-NLS-1$

    /**
     * Draws the graphic.  Check the clip area of the {@link ViewportGraphics} object to determine what
     * area needs to be refreshed.
     *
     * @param context The drawing context.
     */
    void draw(MapGraphicContext context);
}
