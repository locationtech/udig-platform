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
package net.refractions.udig.tools.edit.support;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.interceptor.MapInterceptor;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.tools.edit.EditBlackboardUtil;

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
