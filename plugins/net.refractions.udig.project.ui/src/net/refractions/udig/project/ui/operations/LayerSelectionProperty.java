/**
 * 
 */
package net.refractions.udig.project.ui.operations;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.operations.AbstractPropertyValue;
import net.refractions.udig.ui.operations.PropertyValue;

import org.geotools.filter.Filter;

/**
 * Checks if a layer has a selection
 * 
 * @author Jesse
 */
public class LayerSelectionProperty extends AbstractPropertyValue<ILayer>
        implements PropertyValue<ILayer> {

    public boolean canCacheResult() {
        return false;
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isTrue(ILayer object, String value) {
        Boolean hasSelection = object.getFilter() != Filter.ALL;
        return hasSelection.toString().equalsIgnoreCase(value);
    }

}
