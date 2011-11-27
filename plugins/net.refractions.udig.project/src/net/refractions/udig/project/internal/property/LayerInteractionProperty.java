package net.refractions.udig.project.internal.property;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILayer.Interaction;
import net.refractions.udig.ui.operations.AbstractPropertyValue;
import net.refractions.udig.ui.operations.PropertyValue;

/**
 * Allows tools or operations to check what kind of interactions a layer
 * supports.
 * <p>
 * The following values are defined by {@link ILayer.Interaction}:
 * <ul>
 * <li>VISIBLE: interaction_visible</li>
 * <li>BACKGROUND: interaction_background</li>
 * <li>INFO: interaction_information</li>
 * <li>SELECT: interaction_select</li>
 * <li>EDIT: interaction_edit</li>
 * <li>BOUNDARY: interaction_boundary</li>
 * </ul>
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.0
 */
public class LayerInteractionProperty extends AbstractPropertyValue<ILayer> {

    @Override
    public boolean isTrue( ILayer layer, String text ) {
        Interaction interaction = ILayer.Interaction.getInteraction(text);
        if( interaction == null ){
            return false; // unable to figure out Interaction to test
        }        
        return layer.isApplicable(interaction);
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
