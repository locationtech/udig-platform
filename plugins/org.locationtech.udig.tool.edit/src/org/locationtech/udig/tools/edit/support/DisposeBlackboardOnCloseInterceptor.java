/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.interceptor.MapInterceptor;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.tools.edit.EditBlackboardUtil;

/**
 * When a map is closed this cleans out all the references to the blackboard so it can be garbage collected.  
 * 
 * @author jesse
 * @since 1.1.0
 */
public class DisposeBlackboardOnCloseInterceptor implements MapInterceptor {

    public void run( Map map ) {
        for( ILayer layer : map.getMapLayers() ) {
            EditBlackboard bb = (EditBlackboard) layer.getBlackboard().get(EditBlackboardUtil.EDIT_BLACKBOARD_KEY);
            if( bb!=null ){
                bb.getListeners().clear();
                bb.selectionClear();
                bb.clear();
            }
            layer.getBlackboard().put(EditBlackboardUtil.EDIT_BLACKBOARD_KEY, null);
        }
    }

}
