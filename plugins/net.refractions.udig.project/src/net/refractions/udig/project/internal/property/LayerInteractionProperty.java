/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.property;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.Interaction;
import net.refractions.udig.ui.operations.AbstractPropertyValue;
import net.refractions.udig.ui.operations.PropertyValue;

/**
 * Allows tools or operations to check what kind of interactions a layer
 * supports.
 * <p>
 * The following values are defined by {@link Interaction}:
 * <ul>
 * <li>VISIBLE: interaction_visible</li>
 * <li>BACKGROUND: interaction_background</li>
 * <li>INFO: interaction_information</li>
 * <li>SELECT: interaction_select</li>
 * <li>EDIT: interaction_edit</li>
 * <li>AOI: interaction_aoi</li>
 * </ul>
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.0
 */
public class LayerInteractionProperty extends AbstractPropertyValue<ILayer> {

    @Override
    public boolean isTrue( ILayer layer, String text ) {
        Interaction interaction = Interaction.getInteraction(text);
        if( interaction == null ){
            return false; // unable to figure out Interaction to test
        }        
        return layer.getInteraction(interaction);
    }

    @Override
    public boolean canCacheResult() {
        return false; // do not cache as the user can change this stuff
    }

    @Override
    public boolean isBlocking() {
        return false; 
    }

}
