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

    protected static final String ADVANCED_PROJECTION_PROPERTY = "org.locationtech.udig.render.enableAdvancedRendering"; // //$NON-NLS-1$

    private RendererUtils() {
    }

    /**
     * @return true if advanced projection support is enabled by setting property
     *         org.locationtech.udig.render.enableAdvancedRendering
     */
    public static boolean isAdvancedProjectionSupportEnabled() {
        final String property = System.getProperty(ADVANCED_PROJECTION_PROPERTY);
        if (property == null) {
            return false;
        }
        if (property.trim().isEmpty()) {
            return false;
        }
        return Boolean.TRUE.equals(Boolean.valueOf(property.trim()));

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
