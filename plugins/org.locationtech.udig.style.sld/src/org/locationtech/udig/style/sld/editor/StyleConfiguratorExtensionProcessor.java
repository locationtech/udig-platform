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
package org.locationtech.udig.style.sld.editor;

import java.util.Iterator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.style.sld.SLDPlugin;

/**
 * Processes the org.locationtech.udig.style.sld.SimpleStyleConfigurator extension points and adds the 
 * ones that work for the layer to the manager.
 * 
 * @author jesse
 * @since 1.1.0
 */
final class StyleConfiguratorExtensionProcessor implements ExtensionPointProcessor {
    private final EditorPageManager[] manager;
    private ILayer selectedLayer;
    StyleConfiguratorExtensionProcessor( EditorPageManager[] manager, ILayer selectedLayer ) {
        this.manager = manager;
        this.selectedLayer = selectedLayer;
    }
    public void process( IExtension extension, IConfigurationElement element ) throws Exception {
        EditorNode node = null;
            
        String id = element.getAttribute(OpenStyleEditorAction.ATT_ID);
        String label = element.getAttribute(OpenStyleEditorAction.ATT_LABEL);
        String requires = element.getAttribute(OpenStyleEditorAction.ATT_REQUIRES);
        boolean classMissing = manager[0].getClassValue(element, OpenStyleEditorAction.ATT_CLASS) == null;
        if (label == null) {
            SLDPlugin.trace("StyleConfigurator extension point attribute 'label' not specified -- instance ignored: " + element.getClass(), null); //$NON-NLS-1$
            return;
        }
        if (id == null) {
            //if the optional id was not defined, skip
            SLDPlugin.trace("StyleConfigurator extension point attribute 'id' not specified -- skipped", null); //$NON-NLS-1$
            return;
        }
        if (classMissing) {
            SLDPlugin.trace("StyleConfigurator extension point class could not be found: " + element.getClass(), null); //$NON-NLS-1$
            return;
        }
        if (manager[0].hasNode(id)) {
            SLDPlugin.trace("Duplicate id found -- skipping '" + id + "' with label '" + label + "'", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return;
        }
        
        node = new EditorNode(id, element);

        if (node == null) {
            SLDPlugin.log("EditorNode creation failed in OpenStyleEditorAction", null); //$NON-NLS-1$
            return;
        }

        if (requires != null && !EditorPageManager.meetsRequirement(selectedLayer, id, element, node) ) {
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
