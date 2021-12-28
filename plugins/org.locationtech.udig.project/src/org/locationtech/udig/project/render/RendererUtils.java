/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.render;

import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;

/**
 * Class that provides common utility methods for renderer implementations
 *
 */
public final class RendererUtils {

    private RendererUtils() {
    }

    /**
     * @return true if advanced projection support and continuous map wrapping should be enabled for
     *         feature renderings such as shape files so they will be wrapped continuously to the
     *         left and the right.
     */
    public static boolean isAdvancedProjectionSupportEnabled() {
        return ProjectPlugin.getPlugin().getPreferenceStore()
                .getBoolean(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT);
    }

    /**
     * @return true if renderer Jobs should be System-Jobs to avoid continuously showing 'Rendering
     *         Map' in Progress View on streamed updates on Layer Data source.
     */
    public static boolean isRendererJobSystemJob() {
        return ProjectPlugin.getPlugin().getPreferenceStore()
                .getBoolean(PreferenceConstants.P_HIDE_RENDER_JOB);
    }
}
