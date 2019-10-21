/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.locationtech.udig.style.internal.StyleLayer;

/**
 * Context used to advertise what is available to a StyleEditorPage.
 * <p>
 * An implementation is responsible for holding on to the Style
 * being edited, StyleLayer that is holding the style blackboard
 * and so on.
 * 
 * @since 1.1.0
 */
public interface IStyleEditorPageContainer extends IEditorPageContainer, IPageChangeProvider {
    
    /**
     * Retrieve the root StyleLayerDescriptor for editing.
     *
     * @return
     */
    public StyledLayerDescriptor getSLD();
    
    /**
     * Retrieve the Style being worked on.
     * <p>
     * In general a StylePage will modify the style in place.
     * </p>
     * @return Style being worked on (often a layer will only be using one)
     */
    public Style getStyle();
    
    /**
     * Replace the style being worked on, this can be called
     * by a StylePage that is *completely* generating its own
     * thing.
     *
     * @param style
     */
    public void setStyle(Style style);
    
    /**
     * StyleLayer wraps around the origional layer and has a
     * duplicate of the StyleBlackboard for the user to edit.
     * <p>
     * StyleLayer has apply and revert actions for interacting
     * with the actual Map.
     * 
     * @return StyleLayer for interacting with the actual Map
     */
    public StyleLayer getSelectedLayer();

    /**
     * Used to enabled the apply action, call this when
     * you have a modification that can be writen out.
     */
    public void setExitButtonState();

    /**
     * Action for applying the current changes.  Can be used to enable or disable the 
     * button.  Also to execute the action.
     *
     * @return apply action
     */
    public IAction getApplyAction();
        
}
