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
package net.refractions.udig.project.ui.tool;

import java.util.Set;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.internal.MapPart;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;

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
