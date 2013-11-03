/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld;


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
