/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.locationtech.udig.project.ui.internal.wizard.MapImport;

/**
 * Performs the open action from the file menu of uDig. It is responsible for creating new maps
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
    public static final String ID = "org.locationtech.udig.project.ui.openAction"; //$NON-NLS-1$

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

    }

    public int getAccelerator() {
        return 0;
    }

    public String getActionDefinitionId() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    public ImageDescriptor getDisabledImageDescriptor() {
        return null;
    }

    public HelpListener getHelpListener() {
        return null;
    }

    public ImageDescriptor getHoverImageDescriptor() {
        return null;
    }

    public String getId() {
        return null;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public IMenuCreator getMenuCreator() {
        return null;
    }

    public int getStyle() {
        return 0;
    }

    public String getText() {
        return null;
    }

    public String getToolTipText() {
        return null;
    }

    public boolean isChecked() {
        return false;
    }

    public boolean isEnabled() {
        return false;
    }

    public boolean isHandled() {
        return false;
    }

    public void removePropertyChangeListener( IPropertyChangeListener listener ) {

    }

    public void run() {
        run(this);
    }

    public void runWithEvent( Event event ) {

    }

    public void setActionDefinitionId( String id ) {

    }

    public void setChecked( boolean checked ) {

    }

    public void setDescription( String text ) {

    }

    public void setDisabledImageDescriptor( ImageDescriptor newImage ) {

    }

    public void setEnabled( boolean enabled ) {

    }

    public void setHelpListener( HelpListener listener ) {

    }

    public void setHoverImageDescriptor( ImageDescriptor newImage ) {

    }

    public void setId( String id ) {

    }

    public void setImageDescriptor( ImageDescriptor newImage ) {

    }

    public void setMenuCreator( IMenuCreator creator ) {

    }

    public void setText( String text ) {

    }

    public void setToolTipText( String text ) {

    }

    public void setAccelerator( int keycode ) {

    }

}
