/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.model;

import java.awt.Graphics2D;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

/**
 * Draws the preview for a box and prints the contents of a box.  Must have a default constructor.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface BoxPrinter extends IAdaptable {
    
    /**
     * Passes in a memento object so that the printer can be saved.
     * 
     * @see AbstractBox
     */
    void save(IMemento memento);

    /**
     * @param value the new value of the '<em>IMemento</em>' attribute.
     * @see #save(IMemento)
     */
    void load(IMemento value);
    
    /**
     * This method will be called by Page when the actual printing is being performed.
     * 
     * The graphics object passed in will have its appropriate clipping set,
     * so the drawing code can only draw inside this clip.
     * 
     * @see Page
     * @param graphics A <code>Graphics2D</code> object to perform the drawing on
     * @param monitor
     */
    public void draw(Graphics2D graphics, IProgressMonitor monitor);
    
    /**
     * This method is called by the frame work when it is time to display itself on
     * the screen.  It does not have to be a perfect representation of the actual printed value because
     * It is not called during printing.  It is recommended that caching be used for the preview as
     * much as possible.  This method may take as long as necessary, it is not run in the display thread.
     *
     * @param graphics A <code>Graphics2D</code> object to perform the drawing on.
     * @param monitor 
     */
    public void createPreview( Graphics2D graphics, IProgressMonitor monitor);
    
    /**
     * Returns true if a the preview has changed since last call of {@link #createPreview(Graphics2D, IProgressMonitor)}
     * This method must <b>NOT</b> always return true because as long as it returns true createPreview is
     * called and the screen is not updated.  When it returns false the results of the last createPreview is
     * drawn onto the screen.
     *
     * @return Returns true if a the preview has changed since last call of {@link #createPreview(Graphics2D, IProgressMonitor)}
     */
    public boolean isNewPreviewNeeded();

    /**
     * Must return the id of the extension point where you registered this box printer.
     * <p>
     * <b>Each box printer has to be registered</b> in an extension to the extension point "org.locationtech.udig.printing.ui.boxprinter".<br/>
     * Say you have registered the extension with the id "myPrintBoxes" in the plugin "net.refractions.example.plugin":
     * this method should then return the extension id "net.refractions.example.plugin.myPrintBoxes".
     * </p>
     * Failure to properly configure the extension point id will be noted on the console logs, no user
     * interface notification will be performed.
     *
     * @return the id of the extension point that is used to load the BoxPrinter
     */
    public String getExtensionPointID();
    
    /**
     * Returns the box the box set when the setBox method is called.
     *
     * @return the box the box set when the setBox method is called.
     */
    public Box getBox();
    /**
     * Sets the owning box.  getBox() should return the same box.
     *
     * @param box The box that owns this BoxPrinter.
     */
    public void setBox(Box box);
}
