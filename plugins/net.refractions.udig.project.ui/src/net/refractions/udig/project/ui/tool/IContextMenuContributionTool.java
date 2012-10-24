/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.tool;

import java.util.List;

import org.eclipse.jface.action.IContributionItem;

/**
 * 
 * If the currently active <code>ModalTool</code> implements this interface
 * the <code>MapEditor</code> asks it to contribute to the context menu programmatically.
 * 
 * 
 * @author Vitalus
 * @since UDIG 1.1.0
 *
 */
public interface IContextMenuContributionTool {
    
    
    
    /**
     * The ModalTool may implement this method to contribute
     * programmatially to the context menu on the MapEditor.
     * 
     * <p>
     * For the contribution of <code>IAction</code> use 
     * <code>contributions.add(new ActionContributionItem(IAction))</code>.
     * 
     * @param contributions
     */
    public void contributeContextMenu(List<IContributionItem> contributions);
    

}
