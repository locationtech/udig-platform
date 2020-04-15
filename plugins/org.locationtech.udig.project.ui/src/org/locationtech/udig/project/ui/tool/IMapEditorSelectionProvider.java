/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.tool;

import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.internal.MapPart;

/**
 * Modal tools are responsible for providing the selection for the MapEditor.  In order to do so they
 * must provide a IMapEditorSelectionProvider that will provide the selection for a given map.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IMapEditorSelectionProvider extends ISelectionProvider {
    public void setActiveMap(IMap map, MapPart editor );
    public Set<ISelectionChangedListener> getListeners();
}
