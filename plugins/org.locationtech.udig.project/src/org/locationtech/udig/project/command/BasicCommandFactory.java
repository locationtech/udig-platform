/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.commands.AddLayerCommand;
import org.locationtech.udig.project.internal.commands.ChangeCRSCommand;
import org.locationtech.udig.project.internal.commands.DeleteLayerCommand;

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
