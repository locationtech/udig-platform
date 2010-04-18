package net.refractions.udig.project.ui;

/**
 * This class is called to check if a feature panel can be enabled; it is passed the
 * same "FeatureSite" information that is provided to a FeaturePanel when initialized.
 * 
 * @since 1.2.0
 */
public abstract class IFeaturePanelCheck {
    /**
     * Check the indicated feature site to see if your feature form
     * can operate.
     *
     * @param site
     * @return true if your feature form can operate.
     */
    public abstract boolean check( IFeatureSite site );

}
