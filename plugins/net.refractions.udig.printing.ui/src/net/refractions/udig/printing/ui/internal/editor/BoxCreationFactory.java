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
