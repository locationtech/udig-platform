/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.render;

import java.awt.Rectangle;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.render.IRenderListener;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Coordinates the starting/stopping of renderers and updating of the 
 * {@link IMapDisplay}
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class RenderingCoordinator {
    /**
     * Adds a listener to the RenderCoordinator.
     *
     * @param listener listener to add.
     */
    public void addRenderListener(IRenderListener listener){
        
    }
    /**
     * Removes a listener from the RenderCoordinator.
     *
     * @param listener listener to remove.
     */
    public void removeRenderListener(IRenderListener listener){
        
    }
    /**
     * This method is inherently thread un-safe.  It will return a state that the renderer was in recently but
     * the renderer may have moved on since.  Use a listener to be notified when the state changes.
     */
    public boolean isRendering(){
        //TODO
        return false;
    }
    /**
     * Requests that the area indicated by the rectangle on the screen be rendered for all layers.
     *
     * @param screen area to render.
     */
    public void render(Rectangle screen){
        
    }
    /**
     * Requests that the area indicated by the envelope be rendered for all layers.
     *
     * @param envelope area to render.
     */
    public void render(ReferencedEnvelope envelope){
        
    }
    /**
     * Requests that the entire viewport and all of the layers be rendered.
     */
    public void render(){
        
    }
    /**
     * Requests that the area indicated by the rectangle on the screen be rendered for layers the indicated layers.
     *
     * @param screen area to render.
     * @param layers layer to render
     */
    public void render(Rectangle screen, List<ILayer> layers){
        
    }
    /**
     * Requests that the area indicated by the rectangle on the screen be rendered for layers the indicated layers.
     *
     * @param envelope area to render.
     * @param layers layer to render
     */
    public void render(ReferencedEnvelope envelope, List<ILayer> layers ){
        
    }
    /**
     * Requests that the entire viewport be rendered for the indicated layers only.
     *
     * @param layers layers to be rendered.
     */
    public void render( List<ILayer> layers){
        
    }
    /**
     * Returns the rendered image.  The return type is determined by the parameter passed in.  The {@link RenderingCoordinator} will
     * try to create an image of the requested type but if it is not possible then null will be returned.  
     *
     * @param <T> The desired type of image to return.  For example org.eclipse.swt.Image or java.awt.BufferedImage.
     * @param imageTypeToReturn the class of the type of the image that is desired.
     * @return The rendered image in the requested type or null if that is not possible with this {@link RenderingCoordinator}
     */
    public <T> T getImage(Class<T> imageTypeToReturn){
       return null; 
    }
    
}
