package net.refractions.udig.project;

/**
 * The set of layer interaction properties.
 * <p>
 * We have not decided yet if tools should check that the layer supports the required
 * interaction; or if it can be handled by a tool category.
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public enum Interaction {
    VISIBLE("interaction_visible"), //$NON-NLS-1$
    BACKGROUND("interaction_background"), //$NON-NLS-1$
    INFO ("interaction_information"), //$NON-NLS-1$
    SELECT ("interaction_select"), //$NON-NLS-1$
    EDIT ("interaction_edit"), //$NON-NLS-1$
    AOI ("interaction_aoi"); //$NON-NLS-1$
    
    private String key;
    
    private Interaction(String k) {
        key = k;
    }
    
    /**
     * Get the key that is used to store and retrieve values from the layer blackboard
     * @return key
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Gets the layer interaction property relevant to the supplied key (or toolCategoryId).
     * 
     * @param layerInteraction
     * @return interaction
     */
    public static Interaction getInteraction(String layerInteraction) {
        // check for deprecated ProjectBlackboardConstants
        if (layerInteraction.equals(ProjectBlackboardConstants.LAYER__EDIT_APPLICABILITY)
                || layerInteraction.equals(ProjectBlackboardConstants.LAYER__FEATURES_ADD_APPLICABILITY)
                || layerInteraction.equals(ProjectBlackboardConstants.LAYER__FEATURES_MODIFY_APPLICABILITY)
                || layerInteraction.equals(ProjectBlackboardConstants.LAYER__FEATURES_REMOVE_APPLICABILITY)) {
            return Interaction.EDIT;
        }
        for( Interaction interaction : Interaction.values() ){
            if( layerInteraction.equals( interaction.getKey() ) ){
                return interaction;
            }
        }
        return null;
    }
}