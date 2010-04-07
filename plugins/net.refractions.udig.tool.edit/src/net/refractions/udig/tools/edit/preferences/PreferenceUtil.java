/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.preferences;

import java.awt.Color;

import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.support.SnapBehaviour;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * Provides help for obtaining values that are preferences or will be.
 * 
 * @author jones
 * @since 1.1.0
 */
public class PreferenceUtil {

    protected static PreferenceUtil instance= new PreferenceUtil();
    
    protected PreferenceUtil(){
        //singleton class
        // only tests should override this.
    }
    
    public static PreferenceUtil instance() {
        return instance;
    }


    IPreferenceStore store =  EditPlugin.getDefault().getPreferenceStore();
    
    /**
     * Returns the radius of a vertex 
     *
     * @return
     */
    public int getVertexRadius() {
        return store.getInt(PreferenceConstants.P_VERTEX_SIZE);
    }

    /**
     * Returns the color used to draw the outline of geoms on the EditBlackboard.
     *
     * @return Returns the color used to draw the outline of geoms on the EditBlackboard.
     */
    public Color getDrawGeomsLine() {
        return getDrawSelectionFillColor();
    }

    /**
     * Returns the color used to draw the boxes around vertices.
     *
     * @return the color used to draw the boxes around vertices.
     */
    public Color getDrawVertexLineColor() {
        return getColor(ProjectPlugin.getPlugin().getPreferenceStore(),
                net.refractions.udig.project.preferences.PreferenceConstants.P_SELECTION2_COLOR);
        }

    private Color getColor( IPreferenceStore store2, String preferenceID ) {
        RGB rgb=PreferenceConverter.getColor(store2, preferenceID);
        Color color=new Color(rgb.red, rgb.green, rgb.blue);
        return color;
    }

    /**
     * Returns the color used to fill selected vertices.
     *
     * @return the color used to fill selected vertices.
     */
    public Color getDrawSelectionFillColor() {
        return getColor(ProjectPlugin.getPlugin().getPreferenceStore(),
                net.refractions.udig.project.preferences.PreferenceConstants.P_SELECTION_COLOR);
    }

    /**
     * Sets the radius used for post-snapping.
     *
     * @param newRadius
     */
    public void setSnappingRadius( int newRadius ) {
        store.setValue(PreferenceConstants.P_SNAP_RADIUS, newRadius);
    }
    /**
     * Gets the radius used for post-snapping.
     *  @return the radius used for post-snapping
     */
    public int getSnappingRadius(){
        return store.getInt(PreferenceConstants.P_SNAP_RADIUS);
    }
    
    /**
     * Returns the color used to draw the shape to show the PostSnapping area.
     *
     * @return Returns the color used to draw the shape to show the PostSnapping area.
     */
    public Color getFeedbackColor() {
        return getColor(store, PreferenceConstants.P_SNAP_CIRCLE_COLOR);
    }

    /**
     * Returns the color used to draw the selection area.
     * 
     * @return the color used to draw the selection area.
     */
    public Color getSelectionColor() {
        return getColor(ProjectPlugin.getPlugin().getPreferenceStore(), 
                net.refractions.udig.project.preferences.PreferenceConstants.P_SELECTION_COLOR);
    }


    Color drawGeomsFill = new Color( 144,255,144,100);
    /**
     * Returns the color used to fill the geoms on the EditBlackboard.
     *
     * @return Returns the color used to fill the geoms on the EditBlackboard.
     */
    public Color getDrawGeomsFill() {
        Color base = getSelectionColor();
        return reduceTransparency(base,.35f);
    }

    /**
     * Reduces the transparency of the color by some factor.  
     *  
     * @param base the starting color
     * @param factor the amount to reduce by. Should be between 0-1;
     * 
     * @return The color with the same RGB but reduced alpha
     */
    public static Color reduceTransparency( Color base, float factor ) {
        
        float alpha = (float)base.getAlpha();
        int i = Math.round(alpha*factor);
        Color color = new Color(base.getRed(), base.getGreen(), base.getBlue(), i );
        return color;
    }
    /**
     * Returns the color used to fill the non-selected vertices.
     *
     * @return Returns the color used to fill the non-selected vertices.
     */
    public Color getDrawVertexFillColor() {
        Color base = getSelectionColor();
        return reduceTransparency(base,.35f);
    }

    /**
     * Returns the current preference for snap behaviour.
     *
     * @return the current preference for snap behaviour.
     */
    public SnapBehaviour getSnapBehaviour() {
        String preference = store.getString(PreferenceConstants.P_SNAP_BEHAVIOUR);
        if( preference.equals(SnapBehaviour.OFF.toString()) )
            return SnapBehaviour.OFF;
        if( preference.equals(SnapBehaviour.SELECTED.toString()) )
            return SnapBehaviour.SELECTED;
        if( preference.equals(SnapBehaviour.CURRENT_LAYER.toString()) )
            return SnapBehaviour.CURRENT_LAYER;
        if( preference.equals(SnapBehaviour.ALL_LAYERS.toString()) )
            return SnapBehaviour.ALL_LAYERS;
        if( preference.equals(SnapBehaviour.GRID.toString()) )
            return SnapBehaviour.GRID;
        
        return SnapBehaviour.OFF;
    }

    public void setSnapBehaviour( SnapBehaviour behaviour ) {
        store.putValue(PreferenceConstants.P_SNAP_BEHAVIOUR, behaviour.toString());
    }

    /**
     * Returns true if selected features should be hidden upon selection.  This can be quite expensive.
     *
     * @return
     */
    public boolean hideSelectedLayers(){
        return store.getBoolean(PreferenceConstants.P_HIDE_SELECTED_FEATURES);
    }

    public boolean isAdvancedEditingActive() {
        return store.getBoolean(PreferenceConstants.P_ADVANCED_ACTIVE);
    }

    public void setAdvancedEditingActive( boolean b ) {
        store.setValue(PreferenceConstants.P_ADVANCED_ACTIVE, b);
    }

    public short getMessageDisplayDelay() {
        return 2000;
    }
    
}
