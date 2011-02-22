/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project;

import java.io.IOException;
import java.util.List;

import net.refractions.udig.project.command.EditCommand;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.LayerFactory;
import net.refractions.udig.project.render.IRenderManager;
import net.refractions.udig.project.render.IViewportModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * The part of the model that represents a map.
 * <p>
 * A Map consists of a ContextModel, a ViewportModel and a LayerManager
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Provide access to the active controllers and models, but not viewers.</li>
 * <li>Create a new map based on the current map is also included</li>
 * </ul>
 * </p>
 * <p>
 *
 * @author Jesse
 * @since 0.5
 */
public interface IMap extends IProjectElement {

    /**
     * Returns the layer factory used to create layers for this map.
     *
     * @return the layer factory used to create layers for this map.
     */
    public LayerFactory getLayerFactory();

    /**
     * Returns the Viewport model for this map.
     *
     * @return the Viewport model for this map.
     */
    public IViewportModel getViewportModel();

    /**
     * Returns the map's abstract
     *
     * @return the map's abstract
     */
    public String getAbstract();

    /**
     * Gets the Envelope that indicates the maximum bounding box of the map.
     * <p>
     * The bounds returned are in Lat Long and each time the method is called a new object is
     * returned. Therefore the object can be modified as desired without affecting the model.
     * </p>
     * <p>
     * Note: this is a constant for a given map. It is related to the size of the map data, and is
     * not dependent on the viewport.
     * </p>
     * <b>WARNING</b> This may block.
     *
     * @return The Envelope in Lat Long that indicates the maximum bounding box of the map.
     * @throws IOException
     */
    public ReferencedEnvelope getBounds( IProgressMonitor monitor );

    /**
     * Returns the Aspect ratio of the map. It is normally no the same as the aspect ratio of the
     * viewport.
     *
     * @return The aspect ratio of the map.
     */
    public double getAspectRatio( IProgressMonitor monitor );

    /**
     * Returns the EditManager for the current map.
     *
     * @return the EditManager for the current map.
     */
    public IEditManager getEditManager();

    /**
     * Returns the RenderManager for the current map.
     *
     * @return the RenderManager for the current map.
     */
    public IRenderManager getRenderManager();

    /**
     * Returns the list of Layers in the map. The layers are in zorder. The layer at position 0 is
     * that first layer rendered (The bottom layer in the image); This list is immutable.
     *
     * @return An immutable list containing all the Map's layers.
     */
    public List<ILayer> getMapLayers();

    /**
     * Returns a blackboard for the map. The blackboard is used by various plugins in order to store
     * data and collaborate.
     *
     * @return A blackboard used for collaboration among various plugins.
     */
    public IBlackboard getBlackboard();

    /**
     * Returns a unique identifier for a map. Shouldn't change between runs.
     *
     * @return a unique identifier for a map.
     */
    public URI getID();

    /**
     * Executes a {@linkplain MapCommand} synchronously. This method blocks.
     * All commands are ran in a single thread, this is required so that undo/redo
     * makes sense.
     *
     * @param command the {@linkplain EditCommand}to execute.
     */
    public void sendCommandSync( MapCommand command );
    /**
     * Executes a {@linkplain MapCommand} asynchronously with the calling thread.
     * All commands are ran in a single thread, this is required so that undo/redo
     * makes sense.
     *
     * @param command the {@linkplain EditCommand}to execute.
     */
    public void sendCommandASync( MapCommand command );

    /**
     * Executes a {@linkplain MapCommand} synchronously. This method blocks.
     *
     * @param command the {@linkplain EditCommand}to execute.
     */
    public void executeSyncWithoutUndo( MapCommand command );

    /**
     * Executes a {@linkplain MapCommand} asynchronously.
     *
     * @param command the {@linkplain EditCommand}to execute.
     */
    public void executeASyncWithoutUndo( MapCommand command );

    /**
     * Adds a MapListener to this map.  A given listener will only be added once.
     * Events are only fired if the attributes of the Map class are change.  For example
     * name, ViewportModel, Bounds, etc...
     *
     * @param listener Listener to be added
     * @see net.refractions.udig.project.MapEvent.MapEventType
     */
    public void addMapListener( IMapListener listener );

    /**
     * Removes a MapListener from this map.
     *
     * @param listener Listener to be removed
     */
    public void removeMapListener( IMapListener listener);

    /**
     * Adds a IMapCompositionListener to this map.  A given listener will only be added once.
     * Events are fired when the layers of the Map change: added, removed or reordered.
     *
     * @param listener Listener to be added
     * @see net.refractions.udig.project.MapEvent.MapEventType
     */
    public void addMapCompositionListener( IMapCompositionListener listener );

    /**
     * Removes a MapListener from this map.
     *
     * @param listener Listener to be removed
     */
    public void removeMapCompositionListener( IMapCompositionListener listener);

}
