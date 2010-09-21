/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
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