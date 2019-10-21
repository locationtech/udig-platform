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
package org.locationtech.udig.project.ui.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.ui.UDIGEditorInput;

/**
 * UDIGEditorInputDescriptor objects create IEditorInput objects that wrap IProjectElements objects.
 * <p>
 * Since IProjectElements are not editor inputs and cannot be since they are model objects and not
 * UI objects an extensible system of turning IProjectElement objects into IEditorInput objects are
 * needed. The mapping between a IProjectElements and IEditorInput types are defined by
 * org.locationtech.udig.project.ui.editorInputs extensions.
 * </p>
 * 
 * @author jones
 * @since 0.3
 */
public class UDIGEditorInputDescriptor {
    protected String editorID;
    protected String name;
    protected Class type;
    protected IConfigurationElement extensionElement;
    protected Map<IProjectElement, UDIGEditorInput> instances = new HashMap<IProjectElement, UDIGEditorInput>();
    /**
     * Creates a UDIGEditorInput
     * 
     * @param element
     * @return the EditorInput
     * @throws CoreException
     */
    public UDIGEditorInput createInput( IProjectElement element ) {
        if (!instances.containsKey(element)) {
            UDIGEditorInput input;
            try {
                input = (UDIGEditorInput) extensionElement.createExecutableExtension("class"); //$NON-NLS-1$
                input.setEditorId(editorID);
            } catch (CoreException e) {
                ProjectUIPlugin.log("Error creating input type", e); //$NON-NLS-1$
                return null;
            } //$NON-NLS-1$
            input.setProjectElement(element);
            instances.put(element, input);
        }
        return (UDIGEditorInput) instances.get(element);

    }

    /**
     * @return Returns the type.
     */
    public Class getType() {
        return type;
    }
    /**
     * @param type The type to set.
     */
    public void setType( Class type ) {
        this.type = type;
    }
    /**
     * @return Returns the editorID.
     */
    public String getEditorID() {
        return editorID;
    }
    /**
     * @param editorID The editorID to set.
     */
    public void setEditorID( String editorID ) {
        this.editorID = editorID;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName( String name ) {
        this.name = name;
    }
    /**
     * @return Returns the extensionElement.
     */
    public IConfigurationElement getExtensionElement() {
        return extensionElement;
    }
    /**
     * @param extensionElement The extensionElement to set.
     */
    public void setExtensionElement( IConfigurationElement extensionElement ) {
        this.extensionElement = extensionElement;
    }

}
