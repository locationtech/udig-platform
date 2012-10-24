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
package net.refractions.udig.style.sld.editor;

import java.util.Iterator;

import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.project.ILayer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

/**
 * Processes the net.refractions.udig.style.sld.StyleEditorPage extension points and adds the 
 * ones that work for the layer to the manager.
 * 
 * @author jesse
 * @since 1.1.0
 */
final class StyleEditorPageExtensionProcessor implements ExtensionPointProcessor {
    private final EditorPageManager[] manager;
    private ILayer selectedLayer;
    StyleEditorPageExtensionProcessor( EditorPageManager[] manager, ILayer selectedLayer ) {
        this.selectedLayer = selectedLayer;
        this.manager = manager;
    }
    public void process( IExtension extension, IConfigurationElement element ) throws Exception {
        EditorNode node = null;
            
        boolean labelMissing = element.getAttribute(OpenStyleEditorAction.ATT_LABEL) == null;
        String id = element.getAttribute(OpenStyleEditorAction.ATT_ID);
        String requires = element.getAttribute(OpenStyleEditorAction.ATT_REQUIRES);
        boolean classMissing = manager[0].getClassValue(element, OpenStyleEditorAction.ATT_CLASS) == null;

        if (!(labelMissing || id == null || classMissing)) {
            node = new EditorNode(id, element);
        }
        if (node == null)
            return;

        if (requires != null && !EditorPageManager.meetsRequirement( selectedLayer, id, element, node)) {
            return;
        }
        
        manager[0].registerNode(node);
        String category = node.getCategory();
        if (category == null) {
            manager[0].addToRoot(node);
        } else {
            EditorNode parent = null;
            for (Iterator<?> j = manager[0].getElements(EditorPageManager.POST_ORDER).iterator(); j.hasNext();) {
                EditorNode pNode = (EditorNode) j.next();
                if (category.equals(pNode.getId())) {
                    parent = pNode;
                    break;
                }
            }
            if (parent == null) {
                //TODO: log error
                manager[0].addToRoot(node);
            } else {
                parent.add(node);
            }
        }
    }
}