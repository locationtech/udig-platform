/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.property;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.ui.operations.AbstractPropertyValue;

/**
 * Allows tools or operations to check what kind of interactions a layer supports.
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
 * </p>
 *
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.0
 */
public class LayerInteractionProperty extends AbstractPropertyValue<ILayer> {

    @Override
    public boolean isTrue(ILayer layer, String text) {
        Interaction interaction = Interaction.getInteraction(text);
        if (interaction == null) {
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
