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
     * @param text
     * @return interaction
     */
    public static Interaction getInteraction(String text) {
        try {
            Interaction interaction = Interaction.valueOf(text);
            return interaction;
        }
        catch (IllegalArgumentException wrong){
            // ignore for now maybe it is a tool category
        }

        // check for deprecated ProjectBlackboardConstants
        if (text.equals(ProjectBlackboardConstants.LAYER__EDIT_APPLICABILITY)
                || text.equals(ProjectBlackboardConstants.LAYER__FEATURES_ADD_APPLICABILITY)
                || text.equals(ProjectBlackboardConstants.LAYER__FEATURES_MODIFY_APPLICABILITY)
                || text.equals(ProjectBlackboardConstants.LAYER__FEATURES_REMOVE_APPLICABILITY)) {
            return Interaction.EDIT;
        }
        for( Interaction candidate : Interaction.values() ){
            if( text.equals( candidate.getKey() ) ){
                return candidate;
            }
        }
        return null;
    }
}