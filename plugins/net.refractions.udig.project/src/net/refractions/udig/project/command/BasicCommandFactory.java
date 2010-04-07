/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.command;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.commands.AddLayerCommand;
import net.refractions.udig.project.internal.commands.ChangeCRSCommand;
import net.refractions.udig.project.internal.commands.DeleteLayerCommand;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Creates Edit commands which must be used to modify editable feature data. API internal classes
 * are in the returned API
 * 
 * @author jeichar
 * @deprecated
 * @since 0.3
 */
public class BasicCommandFactory {
    /**
     * Creates a new EditCommandFactory object
     * 
     * @return a new EditCommandFactory object
     */
    public static BasicCommandFactory getInstance() {
        return instance;
    }

    private static final BasicCommandFactory instance = new BasicCommandFactory();
    protected BasicCommandFactory() {
        // no op
    }

    /**
     * Create a delete layer command
     * 
     * @param map the map containing the layer
     * @param layer the layer to delete
     * @return a new {@linkplain DeleteLayerCommand}object that deletes the layer.
     * @see DeleteLayerCommand
     */
    public UndoableMapCommand createDeleteLayer( ILayer layer ) {
        return new DeleteLayerCommand((Layer) layer);
    }

    /**
     * Create an Add Layer command
     * 
     * @param layer the layer to add to the map.
     * @return a new {@linkplain AddLayerCommand}object that deletes the feature.
     * @see AddLayerCommand
     */
    public UndoableMapCommand createAddLayer( ILayer layer ) {
        return new AddLayerCommand((Layer) layer);
    }

    /**
     * Create an Add Layer command
     * 
     * @param layer the layer to add to the map.
     * @param index the zorder where the layer will be added.
     * @return a new {@linkplain AddLayerCommand}object that deletes the feature.
     * @see AddLayerCommand
     */
    public UndoableMapCommand createAddLayer( ILayer layer, int index ) {
        return new AddLayerCommand((Layer) layer, index);
    }

    /**
     * Create a Change CRS command
     * 
     * @param map the map for which the CRS is going to change.
     * @return a new {@linkplain ChangeCRSCommand}object that changes the CRS.
     * @see ChangeCRSCommand
     */
    public UndoableMapCommand createChangeCRS( IMap map, CoordinateReferenceSystem crs ) {
        return new ChangeCRSCommand((Map) map, crs);
    }

}
