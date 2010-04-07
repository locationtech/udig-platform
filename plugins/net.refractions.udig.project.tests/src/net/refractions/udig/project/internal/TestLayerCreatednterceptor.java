package net.refractions.udig.project.internal;

import net.refractions.udig.project.interceptor.LayerInterceptor;

public class TestLayerCreatednterceptor implements LayerInterceptor {
    public static Layer layerCreated;

    public void run( Layer layer ) {
        layerCreated=layer;
//        System.out.println(layer.getName()+" has been created. This is a test interceptor. "); //$NON-NLS-1$
    }
}
