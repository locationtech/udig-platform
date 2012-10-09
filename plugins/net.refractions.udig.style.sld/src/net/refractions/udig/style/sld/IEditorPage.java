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

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.swt.graphics.Point;

public interface IEditorPage extends IDialogPage {
    /**
     * Computes a size for this page's UI component. 
     *
     * @return the size of the preference page encoded as
     *   <code>new Point(width,height)</code>, or 
     *   <code>(0,0)</code> if the page doesn't currently have any UI component
     */
    public Point computeSize();

    /**
     * Returns whether this dialog page is in a valid state.
     * 
     * @return <code>true</code> if the page is in a valid state,
     *   and <code>false</code> if invalid
     */
    public boolean isValid();

    /**
     * Checks whether it is alright to leave this page.
     * 
     * @return <code>false</code> to abort page flipping and the
     *  have the current page remain visible, and <code>true</code>
     *  to allow the page flip
     */
    public boolean okToLeave();

    /**
     * Notifies that the container of this preference page has been canceled.
     * 
     * @return <code>false</code> to abort the container's cancel 
     *  procedure and <code>true</code> to allow the cancel to happen
     */
    public boolean performCancel();

    /**
     * Notifies that the OK button of this page's container has been pressed.
     * 
     * @return <code>false</code> to abort the container's OK
     *  processing and <code>true</code> to allow the OK to happen
     */
    public boolean performOk();

    /**
     * Executed immediately before an apply action.
     *
     */
    public boolean performApply();
    
    /**
     * Sets the size of this page's UI component.
     *
     * @param size the size of the preference page encoded as
     *   <code>new Point(width,height)</code>
     */
    public void setSize(Point size);

	public void setContainer(IEditorPageContainer dialog);
	
    public void applyData(Object data);

    public void refresh();
    
}
