/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.mapgraphic.scalebar;

import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.mapgraphic.internal.Messages;

public enum UnitPolicy {
    AUTO(Messages.BarStyleConfigurator_AutoUnits),
    METRIC(Messages.BarStyleConfigurator_MetricUnits),
    IMPERIAL(Messages.BarStyleConfigurator_ImperialUnits);
    
    /** Label used to represent this policy in a user interface */
    final private String label;
    
    UnitPolicy(String label){
        this.label = label;
    }
    /** Label used to represent this policy in a user interface */
    public String getLabel() {
        return label;
    }
    /**
     * Method that figures out the "default" UnitPolicy as provided
     * by the user in the preference page.
     *
     * @return UnitPolicy to use by default
     */
    public static UnitPolicy determineDefaultUnits() {
        String previous = UiPlugin.getDefault().getPreferenceStore().getString(net.refractions.udig.ui.preferences.PreferenceConstants.P_DEFAULT_UNITS);
        if(previous.equals( net.refractions.udig.ui.preferences.PreferenceConstants.METRIC_UNITS)){
            return METRIC;
        }
        else if(previous.equals( net.refractions.udig.ui.preferences.PreferenceConstants.IMPERIAL_UNITS)){
            return IMPERIAL;
        }
        else return AUTO;
    }
}