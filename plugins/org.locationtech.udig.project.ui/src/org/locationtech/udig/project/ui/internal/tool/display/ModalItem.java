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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.commands.IHandler;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.locationtech.udig.internal.ui.operations.OperationCategory;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.MapEditorWithPalette;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.MapToolEntry;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.viewers.MapEditDomain;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.graphics.Glyph;
import org.locationtech.udig.ui.operations.ILazyOpListener;
import org.locationtech.udig.ui.operations.OpFilter;

/**
 * Item used to represent one mode in a group.
 * <p>
 * Responsibilities:
 * <ul>
 * <li> Maintain that only ModalItem is active at a time. </li>
 * <li> Mark contained ContributionItems as selected or not depending on whether the current
 * ModalItem is active. </li>
 * <li> Contains a set of contributions. </li>
 * </ul>
 * 
 * @author jeichar
 * @since 0.9.0
 * @version 1.3.0
 */
public abstract class ModalItem implements ILazyOpListener {

    private static ImageRegistry IMAGES = ProjectUIPlugin.getDefault().getImageRegistry();

//    protected static final Cursor defaultCursor = PlatformUI.getWorkbench().getDisplay()
//            .getSystemCursor(SWT.CURSOR_ARROW);
    
    private List<CurrentContributionItem> contributions = new ArrayList<CurrentContributionItem>();
    private CopyOnWriteArrayList<MapToolEntry> mapToolEntries = new CopyOnWriteArrayList<MapToolEntry>();
    
    protected String[] commandIds;
    protected String handlerType;
    protected ImageDescriptor imageDescriptor;
    protected String name;
    protected String toolTipText;
    protected String id;
    protected OpFilter enablement;
    protected List<OperationCategory> operationCategories;
    protected boolean isEnabled = true;
    
    private Lock enabledLock=new ReentrantLock();
    private String preferencePageId;

    private ImageDescriptor largeImageDescriptor;

    private List<ContributionItem> optionsContribution = new ArrayList<ContributionItem>();

    /**
     * Gets the image descriptor of the item.
     * 
     * @return the image descripor of the item.
     */
    public ImageDescriptor getImageDescriptor() {
        return imageDescriptor;
    }

    /**
     * Gets the large image descriptor of the item.
     * 
     * @return the image descripor of the item; may be null if not provided
     */
    public ImageDescriptor getLargeImageDescriptor() {
        return largeImageDescriptor;
    }
    /**
     * Marks each contribution item as selected.
     * 
     * @param checked the selected value of each contribution.
     */
    public void setChecked( boolean checked ) {
        List<CurrentContributionItem> toRemove = new ArrayList<CurrentContributionItem>();
        for( CurrentContributionItem item : contributions ) {
            if (item.isDisposed()) {
                toRemove.add(item);
            } else {
                item.setSelection(checked, this);
            }
        }
        contributions.removeAll(toRemove);

        ToolManager tools = (ToolManager) ApplicationGIS.getToolManager();
        MapPart currentEditor = tools.currentEditor;
        if (currentEditor != null) {
            if (currentEditor instanceof MapEditorWithPalette) {
                MapEditorWithPalette editor2 = (MapEditorWithPalette) currentEditor;

                MapEditDomain editDomain = editor2.getEditDomain();

                PaletteViewer paletteViewer = editDomain.getPaletteViewer();
                if( paletteViewer != null ){
                    for( MapToolEntry entry : this.mapToolEntries ) {
    
                        if (paletteViewer.getEditPartRegistry().get(entry) != null) {
                            paletteViewer.setActiveTool(entry);
    
                            EditPart part = (EditPart) paletteViewer.getEditPartRegistry().get(entry);
    
                            paletteViewer.reveal(part);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Responsible for activating this modal item.
     * <p>
     * This method is overriden by the one subclass ModalTool (so that the tool manager
     * is kept informed on what tool is active).
     * 
     * @see org.locationtech.udig.project.ui.tool.ActionTool#run()
     */
    public void run() {
        if( isModeless() ){
            runModeless();
            if (isEnabled() ){
                return; // we are already enabled!
            }
            // not sure about the isEnabled check?
            // do we really need to progress and try and
            // activate this one?
        }
        else {
            runModal();
        }
        // go ahead and activate
        setActive(true);
        
        // a bit of quality assurance here 
        // while we expect the above setActive method to update
        // that active item we will double check now
        ModalItem activeModalItem = getActiveItem();
        if( activeModalItem == this ){
            // good that worked then
        }
        else {
            // okay we will change the active item ourself
            if (activeModalItem != null){
                activeModalItem.setActive(false);
            }
            setActiveItem( this );
        }
    }

    /**
     * Gets the default item.
     * 
     * @return the default item.
     */
    protected abstract ModalItem getDefaultItem();

    /**
     * Returns the currently active item or null if no currently active tool.
     * 
     * @return the currently active item or null if no currently active tool.
     */
    protected abstract ModalItem getActiveItem();
    /**
     * Sets the currently active item.
     */
    protected abstract void setActiveItem( ModalItem item );

    /**
     * Indicate if this modal item needs {@link #runModeless()}.
     * 
     * @return true to use {@link #runModeless()}
     */
    protected boolean isModeless(){
        return false;
    }
    
    /**
     * Called if {@link #isModeless() is true; used to run this item as a
     * "fire and forget" action that does not effect the current active item.
     */
    protected abstract void runModeless();
    
    /**
     * Called if {@link #isModeless()} is false; used to run this item as a modal item
     * (resulting it in it being the active item).
     * <p>
     * Implementations should take responsible for ensuring the item isActiveItem()
     * after this method is called.
     */
    protected abstract void runModal();
    
    /**
     * disposes of any resources held by the item.
     */
    public abstract void dispose();

    /**
     * Returns true if the item is disposed
     *
     * @return Returns true if the item is disposed
     */
    public abstract boolean isDisposed();

    /**
     * Activates the current item. The activeTool item field does not need to be set in this method.
     */
    protected abstract void setActive( boolean active );

    /**
     * Returns the list of contributions controlled by this item.
     */
    public List<CurrentContributionItem> getContributions() {
        return Collections.unmodifiableList(contributions);
    }
    
    public boolean addContribution(CurrentContributionItem contribution) {
        contribution.setEnabled(isEnabled());
        return contributions.add(contribution);
    }
    
    public CurrentContributionItem removeContribution(int index) {
        return contributions.remove(index);
    }
    
    public boolean removeContribution(CurrentContributionItem contribution) {
        return contributions.remove(contribution);
    }
    
    public void clearContributions() {
        contributions.clear();
    }
    /**
     * Provides access to the list of MapToolEntry that are notified when enablement changes.
     * 
     * @return A copy on write array of the MapToolEntry to notify for enablement
     */
    public CopyOnWriteArrayList<MapToolEntry> getMapToolEntries() {
        return mapToolEntries;
    }

    /**
     * Returns an instance of a command handler for the current item.
     * 
     * @param commandId the id of the command to get a handler for.
     * @return an instance of a command handler for the current item.
     */
    public abstract IHandler getHandler( String commandId );

    /**
     * Returns the list desired commands
     * 
     * @return the list of desired commands
     */
    public String[] getCommandIds() {

        String[] c=new String[commandIds.length];
        System.arraycopy(commandIds, 0, c, 0, c.length);
        return c;
    }

    /**
     * ID of item
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * sets the id of the item
     * 
     * @param id the new id.
     */
    public void setId( String id ) {
        this.id = id;
    }
    /**
     * gets the name of the item.
     * 
     * @return the name of the item.
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the name of the item
     * 
     * @param name the new name
     */
    public void setName( String name ) {
        this.name = name;
    }
    /**
     * Gets the tooltip of the item
     * 
     * @return the tooltip of the item.
     */
    public String getToolTipText() {
        return toolTipText;
    }
    /**
     * sets the tooltip of the item
     * 
     * @param toolTipText the new tooltip
     */
    public void setToolTipText( String toolTipText ) {
        this.toolTipText = toolTipText;
    }

    /**
     * Sets the images descriptor of the item.
     * 
     * @param imageDescriptor the new image descriptor.
     */
    public void setImageDescriptor( ImageDescriptor imageDescriptor ) {
        this.imageDescriptor = imageDescriptor;
        IMAGES.remove(getId());
    }

    /**
     * Sets the images descriptor of the item.
     * 
     * @param imageDescriptor the new image descriptor.
     */
    public void setLargeImageDescriptor( ImageDescriptor imageDescriptor ) {
        this.largeImageDescriptor = imageDescriptor;
        IMAGES.remove(getId()+"Large");
    }
    
    /**
     * Gets the icon image of the tool
     * 
     * @return the icon image of the tool.
     */
    public Image getImage() {
         if (IMAGES.get(getId()) == null || IMAGES.get(getId()).isDisposed()) {
            IMAGES.remove(getId());
            IMAGES.put(getId(), getImageDescriptor());
        }

        return IMAGES.get(getId());
    }

    /**
     * Returns the "pushed" look of an active icon.
     * 
     * @return the "pushed" look of an active icon.
     */
    public Image getActiveImage() {
        if( getImageDescriptor()==null ){
            return null;
        }
        if (IMAGES.get(getId() + "pushed") == null || IMAGES.get(getId() + "pushed").isDisposed()) { //$NON-NLS-1$ //$NON-NLS-2$
            IMAGES.put(getId() + "pushed", Glyph.push(getImageDescriptor())); //$NON-NLS-1$
        }

        return IMAGES.get(getId() + "pushed"); //$NON-NLS-1$
    }
    

    public OpFilter getEnablesFor() {
        if( enablement==null )
            return OpFilter.TRUE;
        return enablement;
    }

    /**
     * Returns whether the item is enabled.
     * 
     * @return
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled( final boolean isEnabled ) {
            PlatformGIS.syncInDisplayThread(new Runnable(){
                public void run() {
                    internalSetEnabled(isEnabled);
                }
            });
    }
    
    protected void internalSetEnabled( boolean isEnabled2 ) {
        enabledLock.lock();
        try {
            this.isEnabled = isEnabled2;
            for( CurrentContributionItem contrib : getContributions() ) {
                contrib.setEnabled(isEnabled2);
            }
            for( MapToolEntry entry : mapToolEntries ){
                entry.setVisible(isEnabled2);
            }
        } finally {
            enabledLock.unlock();
        }
    }

    public List<OperationCategory> getOperationCategories() {
        if (operationCategories == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(operationCategories);
    }
    
    /**
     * Sets the Preference Page Id of the item
     * 
     * @param id the new Preference Page Id
     */
    public void setPreferencePageId( String id ) {
        this.preferencePageId = id;
    }
    /**
     * Gets the Preference Page Id of the item
     * 
     * @return the Preference Page Id of the item.
     */
    public String getPreferencePageId() {
        return preferencePageId;
    }
    
    /**
     * Sets the tool option class of the item, the class is not instantiated until  the 
     * <code>getOptionPage()</code> method is called.
     * 
     * @param id the new Preference Page Id
     */
    public void addOptionsContribution( ContributionItem optionsPage ) {
        this.optionsContribution.add(optionsPage);
    }
    /**
     * Gets the tool option page of the item
     * 
     * @return the AbstractMapEditorOptionsPage of the item.
     */
    public List<ContributionItem> getOptionsContribution() {
        return optionsContribution;
    }

}
