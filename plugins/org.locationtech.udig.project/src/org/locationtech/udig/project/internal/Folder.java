/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 * 
 */
package org.locationtech.udig.project.internal;

import java.util.List;
import org.locationtech.udig.project.IFolder;

import org.locationtech.udig.project.ILegendItem;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Folder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.locationtech.udig.project.internal.Folder#getItems <em>Items</em>}</li>
 * </ul>
 *
 * @see org.locationtech.udig.project.internal.ProjectPackage#getFolder()
 * @model superTypes="org.locationtech.udig.project.internal.IFolder org.locationtech.udig.project.internal.LegendItem"
 * @generated
 */
public interface Folder extends IFolder, LegendItem {

    /**
     * Returns the value of the '<em><b>Items</b></em>' containment reference list.
     * The list contents are of type {@link org.locationtech.udig.project.ILegendItem}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Items</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Items</em>' containment reference list.
     * @see org.locationtech.udig.project.internal.ProjectPackage#getFolder_Items()
     * @model type="org.locationtech.udig.project.internal.ILegendItem" containment="true"
     * @generated
     */
    @Override
    List<ILegendItem> getItems();
} // Folder
