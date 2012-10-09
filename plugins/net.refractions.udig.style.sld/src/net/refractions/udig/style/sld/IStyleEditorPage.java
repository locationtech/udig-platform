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
package net.refractions.udig.style.sld;


/**
 * 
 * An interface for a page in the SLD Editor. This interface
 * is used primarily by the page's container, is pretty much 
 * identical to IPreferencePage. 
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.0.0
 */
public interface IStyleEditorPage extends IEditorPage {

    public IStyleEditorPageContainer getContainer();
    
}
