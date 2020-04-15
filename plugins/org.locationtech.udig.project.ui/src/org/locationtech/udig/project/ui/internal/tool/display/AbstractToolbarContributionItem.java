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
package org.locationtech.udig.project.ui.internal.tool.display;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.locationtech.udig.project.ui.internal.Messages;


/**
 * Abstract class for contribution item that represent the current 
 * item selected on the toolbar.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public abstract class AbstractToolbarContributionItem extends CurrentContributionItem {

    ToolItem toolItem;
    
    /**
     * Current modal item that is represented by this contribution in UI.
     */
    protected ModalItem currentTool;
    
    
    private boolean checked;
    
    

    /**
     * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.ToolBar, int)
     */
    public final void fill( final ToolBar parent, int index ) {
        if (getTools().size() == 0)
            return;

        toolItem = createToolItem(parent, index);
        
        ModalItem firstTool = getTools().get(0);
        currentTool = null;
        setCurrentTool(firstTool);

        if (isActiveItem() || (isDefaultItem() 
//        		&& ToolProxy.activeItem == null
        		)) {
            runCurrentTool();
        }
        
        toolItem.addSelectionListener(new SelectionListener(){

        	public void widgetSelected( SelectionEvent e ) {
                widgetDefaultSelected(e);
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                if (e.detail == SWT.ARROW) {
                    final Menu menu = new Menu(parent);
                    for( final ModalItem tool : getTools() ) {
                        MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
                        menuItem.setImage(tool.getImage());
                        menuItem.setText(tool.getName());
                        menuItem.setEnabled(tool.isEnabled());
                        
                        
                        menuItem.addSelectionListener(new SelectionListener(){

                            public void widgetSelected( SelectionEvent e ) {
                                widgetDefaultSelected(e);
                            }

                            public void widgetDefaultSelected( SelectionEvent e ) {
                                setCurrentTool(tool);
                                runCurrentTool();
                                menu.dispose();
                            }
                        });
                    }
                    menu.setVisible(true);
                } else {
                    runCurrentTool();
                }
            }

        });

    }

    /**
     * If the tool is the default tool then run that tool.
     * 
     * @return true if the default tool is in the current category and the default category should
     *         be activated.
     */
    protected abstract boolean isDefaultItem();
    
    
    protected abstract boolean isActiveItem();

    /**
     * Creates the ToolItem to add to the toolbar
     * 
     * @param parent
     * @param index
     */
    protected abstract ToolItem createToolItem( ToolBar parent, int index );

    /**
     * returns the list of items for the category.
     * 
     * @return
     */
    protected abstract List<ModalItem> getTools();

    /**
     * Sets the current items from the category as the current items for the tool item
     * 
     * @param tool the new current tool. It must be one of the items in the category.
     */
    protected void setCurrentTool( final ModalItem tool ) {
        if( currentTool==tool )
            return;
        currentTool = tool;
        if( Display.getCurrent()==null ){
            Display.getDefault().asyncExec(new Runnable(){
                public void run() {
                    setWidgetState(tool);

                }
            });
        }else
            setWidgetState(tool);

    }

    /**
     *
     * @param tool
     */
    private void setWidgetState( ModalItem tool ) {
        if(!isDisposed() && toolItem != null){
            if (toolItem.getImage() == null)
                toolItem.setImage(tool.getImage());
            toolItem.setToolTipText(tool.getToolTipText());
        }
    }

    /**
     * Selects passed modal tool if the <code>toolItem</code> widget has been created
     *  and not disposed. Otherwise just ignore selection until the widget will be created.
     *  
     * 
     * @see org.locationtech.udig.project.ui.internal.tool.display.CurrentContributionItem#setSelection(boolean,
     *      org.locationtech.udig.project.ui.internal.tool.display.ModalItem)
     */
    public void setSelection( boolean checked, ModalItem tool ) {
        if (toolItem !=null && !toolItem.isDisposed()) {
            setCurrentTool(tool);
            if (checked) {
                //if this fails your image may be in the wrong format (only png/gif supported)
                Image activeImage = tool.getActiveImage();
                if( activeImage!=null )
                    toolItem.setImage(activeImage);
            } else {
                Image image = tool.getImage();
                if( image!=null )   
                    toolItem.setImage(image);
            }
            this.checked = checked;
        }
    }

    /**
     * @see org.locationtech.udig.project.ui.internal.tool.display.CurrentContributionItem#isChecked()
     */
    protected boolean isChecked() {
        return checked;
    }

    /**
     * Runs the current tool.
     */
    protected void runCurrentTool() {
        if (currentTool != null) {
//            if( !currentTool.isEnabled() ) {
//                for( ModalItem item: getTools() ) {
//                    if( item.isEnabled() ){
//                        currentTool=item;
//                        break;
//                    }
//                }
//            }
            if(currentTool.isEnabled())
            	currentTool.run();
            else{
            	final Display disp = Display.getDefault();
            	MessageDialog.openWarning(disp.getActiveShell(),
    					Messages.AbstractToolbarContributionItem_warning_title, 
    					MessageFormat.format(
    							Messages.AbstractToolbarContributionItem_warning_message, new Object[] {currentTool.getName()}) 
    					);
            }
        }
    }

    /**
     * @see org.locationtech.udig.project.ui.internal.tool.display.CurrentContributionItem#isDisposed()
     */
    public boolean isDisposed() {
        return toolItem != null && toolItem.isDisposed();
    }

    /**
     * Sets the current to too be the next tool in the list.
     */
    public void incrementSelection() {
        if( !isEnabledTool() )
            return;
        int index = getTools().indexOf(currentTool);
        if (index == getTools().size() - 1)
            index = 0;
        else
            index++;
        ModalItem modalItem = getTools().get(index);
        setCurrentTool(modalItem);
        if( !modalItem.isEnabled() )
            incrementSelection();
    }

    /**
     * Returns true if one of the tools in the category is enabled.
     *
     * @return true if one of the tools in the category is enabled.
     */
    private boolean isEnabledTool() {
        for( ModalItem item : getTools() ) {
            if( item.isEnabled() )
                return true;
        }
        return false;
    }

//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled( boolean enabled ) {
//        this.enabled = enabled;
//    }

}
