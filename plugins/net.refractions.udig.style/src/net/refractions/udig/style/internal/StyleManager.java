package net.refractions.udig.style.internal;

import java.util.Set;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;

/**
 * Apply/Cancel styled edit and provider of StyleConfigurators.
 *
 * Responsibilities:
 * <ul>
 * <li>Process extention point to acquire StyleConfigurator instances
 * <li>Provided the concept of a current layer
 * </ul>
 * </p>
 * @author jdeolive
 * @since 0.6
 */
public interface StyleManager {

    /**
     * Focus the StyleManager on the provided layer
     *
     * @param layer
     */
    public void setCurrentLayer(Layer layer);

    /**
     * Layer the StyleManager is focused on.
     *
     * @return Layer being edited
     */
    public Layer getCurrentLayer();

    Set<IStyleConfigurator> getStyleConfigurators();

    /**
     * List of configurators, usually filtered against current layer
     * @return *
    public Set<IStyleConfigurator> getCurrentConfigurators();
    */
    /** Apply the style to the Map model
    public void applyStyle();
    */
    /** Throw out current edit, and restore to starting state.
    public void cancelStyle();
    */
    /**
     * TODO summary sentence for createStyleTransaction ...
     *
     * @param layer
     * @return
     */
    //StyleTransaction createStyleTransaction( Layer layer );
}
