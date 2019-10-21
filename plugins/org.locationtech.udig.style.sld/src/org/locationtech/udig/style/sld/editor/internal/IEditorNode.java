/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor.internal;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.locationtech.udig.style.sld.IEditorPage;
import org.locationtech.udig.style.sld.IEditorPageContainer;

/**
 * Node forming a tree structure allowing the user to select
 * an IEditorPage to interact with.
 * 
 * @since 1.1.0
 */
public interface IEditorNode {
    /**
     * Adds the given preference node as a subnode of this
     * preference node.
     *
     * @param node the node to add
     */
    public void add(IEditorNode node);

    /**
     * Creates the preference page for this node.
     */
    public void createPage(Composite parent, IEditorPageContainer container);

    /**
     * Release the page managed by this node, and any SWT resources
     * held onto by this node (Images, Fonts, etc).  
     *
     * Note that nodes are reused so this is not a call to dispose the
     * node itself.
     */
    public void disposeResources();

    /**
     * Returns the subnode of this contribution node with the given node id.
     *
     * @param id the preference node id
     * @return the subnode, or <code>null</code> if none
     */
    public IEditorNode findSubNode(String id);

    /**
     * Returns the id of this contribution node.
     * This id identifies a contribution node relative to its parent.
     *
     * @return the node id
     */
    public String getId();

    /**
     * Returns the image used to present this node in a preference dialog.
     *
     * @return the image for this node, or <code>null</code>
     *   if there is no image for this node
     */
    public Image getLabelImage();

    /**
     * Returns the text label used to present this node in a preference dialog.
     *
     * @return the text label for this node, or <code>null</code>
     *   if there is no label for this node
     */
    public String getLabelText();

    /**
     * Returns the preference page for this node.
     *
     * @return the preference page
     */
    public IEditorPage getPage();

    /**
     * Returns an iterator over the subnodes (immediate children)
     * of this contribution node.
     *
     * @return an IEditorNode array containing the child nodes
     */
    public IEditorNode[] getSubNodes();

    /**
     * Removes the subnode of this preference node with the given node id.
     *
     * @param id the subnode id
     * @return the removed subnode, or <code>null</code> if none
     */
    public IEditorNode remove(String id);

    /**
     * Removes the given preference node from the list of subnodes
     * (immediate children) of this node.
     *
     * @param node the node to remove
     * @return <code>true</code> if the node was removed,
     *  and <code>false</code> otherwise
     */
    public boolean remove(IEditorNode node);

}
