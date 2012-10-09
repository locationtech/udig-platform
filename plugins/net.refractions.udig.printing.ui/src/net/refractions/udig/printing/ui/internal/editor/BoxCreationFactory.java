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
package net.refractions.udig.printing.ui.internal.editor;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.BoxPrinter;
import net.refractions.udig.printing.model.impl.BoxImpl;
import net.refractions.udig.printing.ui.internal.BoxFactory;
import net.refractions.udig.printing.ui.internal.PrintingPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.requests.CreationFactory;

public class BoxCreationFactory implements CreationFactory {

	private BoxFactory factory;

	public BoxCreationFactory(BoxFactory factory) {
		this.factory = factory;
	}

	public Object getNewObject() {
        
		BoxPrinter object;
        try {
            object = factory.createBox();
        } catch (CoreException e) {
            PrintingPlugin.log("",e); //$NON-NLS-1$
            return null;
        }
        if( object instanceof Box ){
            return object;
        }
        
        Box box=new BoxImpl();
        box.setBoxPrinter(object);
        object.setBox(box);
        
        return box;
	}

	public Object getObjectType() {
		return factory.getType();
	}

}
