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
package net.refractions.udig.internal.ui;

import net.refractions.udig.ui.UDIGDropTargetListener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;

public class UDIGControlDropListener extends DropTargetAdapter implements UDIGDropTargetListener{


    UDIGDropHandler handler;
    private IDropTargetProvider destinationProvider;


	public UDIGControlDropListener(IDropTargetProvider destinationProvider, UDIGDropHandler dropHandler) {
		handler = dropHandler;
        if( destinationProvider==null )
            destinationProvider=new NullProvider();
        this.destinationProvider=destinationProvider;		
	}
    private void initHandler(DropTargetEvent event) {
        handler.setTarget(destinationProvider.getTarget(event));
    }

    public void dragEnter( DropTargetEvent event ) {
	    initHandler(event);
        handler.dragEnter(event);
    }

    public void dragOperationChanged( DropTargetEvent event ) {
        initHandler(event);
        handler.dragOperationChanged(event);
    }

    @Override
    public void dragLeave( DropTargetEvent event ) {
        initHandler(event);
        handler.dragLeave(event);
    }
    @Override
    public void dragOver( DropTargetEvent event ) {
        initHandler(event);
        handler.dragOver(event);
    }
    @Override
    public void dropAccept( DropTargetEvent event ) {
        initHandler(event);
        handler.dropAccept(event);
    }
    public void drop(DropTargetEvent event ) {
        initHandler(event);
		handler.drop(event);
	}

    public UDIGDropHandler getHandler() {
    	return handler;
    }
    
    /**
     * Returns nulls
     * @author jones
     * @since 1.0.0
     */
    public static class NullProvider implements ISelectionProvider, IDropTargetProvider {

        public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        }

        public ISelection getSelection() {
            return new StructuredSelection();
        }

        public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        }

        public void setSelection( ISelection selection ) {
        }

        public Object getTarget(DropTargetEvent event  ) {
            return null;
        }

    }

    public IDropTargetProvider getDropTargetProvider() {
        return destinationProvider;
    }
    public void setDropTargetProvider( IDropTargetProvider newProvider ) {
        destinationProvider=newProvider;
    }   
}