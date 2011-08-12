/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.project.ui.internal.actions;

import net.refractions.udig.project.ui.internal.MapImport;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Performs the open action from the file menu of uDig. It is responseible for creating new maps
 * from selected resources.
 * 
 * @author rgould
 * @since 0.9.0
 */
public class AddLayersAction extends ActionDelegate
        implements
            IWorkbenchWindowActionDelegate,
            IObjectActionDelegate,
            IAction{

    /** <code>ID</code> field */
    public static final String ID = "net.refractions.udig.project.ui.openAction"; //$NON-NLS-1$

    /**
     * @see org.eclipse.ui.actions.ActionDelegate#runWithEvent(org.eclipse.jface.action.IAction,
     *      org.eclipse.swt.widgets.Event)
     */
    @Override
    public void runWithEvent( IAction action, Event event ) {
        MapImport mapImport = new MapImport();
        mapImport.getDialog().open();
    }

    /**
     * @see org.eclipse.ui.actions.ActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action ) {
        runWithEvent(action, null);
    }
    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window ) {
    }

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     *      org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
    }

    public void addPropertyChangeListener( IPropertyChangeListener listener ) {
        // TODO Auto-generated method stub
        
    }

    public int getAccelerator() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getActionDefinitionId() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public ImageDescriptor getDisabledImageDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

    public HelpListener getHelpListener() {
        // TODO Auto-generated method stub
        return null;
    }

    public ImageDescriptor getHoverImageDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    public ImageDescriptor getImageDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

    public IMenuCreator getMenuCreator() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getStyle() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getText() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getToolTipText() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isChecked() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isHandled() {
        // TODO Auto-generated method stub
        return false;
    }

    public void removePropertyChangeListener( IPropertyChangeListener listener ) {
        // TODO Auto-generated method stub
        
    }

    public void run() {
        run(this);
    }

    public void runWithEvent( Event event ) {
        // TODO Auto-generated method stub
        
    }

    public void setActionDefinitionId( String id ) {
        // TODO Auto-generated method stub
        
    }

    public void setChecked( boolean checked ) {
        // TODO Auto-generated method stub
        
    }

    public void setDescription( String text ) {
        // TODO Auto-generated method stub
        
    }

    public void setDisabledImageDescriptor( ImageDescriptor newImage ) {
        // TODO Auto-generated method stub
        
    }

    public void setEnabled( boolean enabled ) {
        // TODO Auto-generated method stub
        
    }

    public void setHelpListener( HelpListener listener ) {
        // TODO Auto-generated method stub
        
    }

    public void setHoverImageDescriptor( ImageDescriptor newImage ) {
        // TODO Auto-generated method stub
        
    }

    public void setId( String id ) {
        // TODO Auto-generated method stub
        
    }

    public void setImageDescriptor( ImageDescriptor newImage ) {
        // TODO Auto-generated method stub
        
    }

    public void setMenuCreator( IMenuCreator creator ) {
        // TODO Auto-generated method stub
        
    }

    public void setText( String text ) {
        // TODO Auto-generated method stub
        
    }

    public void setToolTipText( String text ) {
        // TODO Auto-generated method stub
        
    }

    public void setAccelerator( int keycode ) {
        // TODO Auto-generated method stub
        
    }

}
