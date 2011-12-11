package net.refractions.udig.project.ui.internal;

import java.util.SortedSet;

import net.refractions.udig.project.interceptor.MapInterceptor;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.internal.util.ScaleConfigUtils;

public class MapPreferredScalesInterceptor implements MapInterceptor {

    @Override
    public void run(Map map) {
        if (map != null && map.getViewportModelInternal() != null) {
            ViewportModel viewportModel = map.getViewportModelInternal();
            SortedSet<Double> preferredMapScales = ScaleConfigUtils.getScaleDenominatorsFromPreferences();
            viewportModel.setPreferredScaleDenominators(preferredMapScales);
        }
    }

}
