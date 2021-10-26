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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.locationtech.udig.project.ILegendItem;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Legend Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.locationtech.udig.project.internal.LegendItem#getName <em>Name</em>}</li>
 *   <li>{@link org.locationtech.udig.project.internal.LegendItem#isShown <em>Shown</em>}</li>
 *   <li>{@link org.locationtech.udig.project.internal.LegendItem#getIcon <em>Icon</em>}</li>
 * </ul>
 *
 * @see org.locationtech.udig.project.internal.ProjectPackage#getLegendItem()
 * @model superTypes="org.locationtech.udig.project.internal.ILegendItem"
 * @generated
 */
public interface LegendItem extends EObject, ILegendItem {

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.locationtech.udig.project.internal.ProjectPackage#getLegendItem_Name()
     * @model
     * @generated
     */
    @Override
    String getName();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.LegendItem#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Icon</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Glyph</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Icon</em>' attribute.
     * @see #setIcon(ImageDescriptor)
     * @see org.locationtech.udig.project.internal.ProjectPackage#getLegendItem_Icon()
     * @model dataType="org.locationtech.udig.project.internal.ImageDescriptor"
     * @generated
     */
    @Override
    ImageDescriptor getIcon();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.LegendItem#getIcon <em>Icon</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Icon</em>' attribute.
     * @see #getIcon()
     * @generated
     */
    void setIcon(ImageDescriptor value);

    /**
     * Returns the value of the '<em><b>Shown</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Shown</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Shown</em>' attribute.
     * @see #setShown(boolean)
     * @see org.locationtech.udig.project.internal.ProjectPackage#getLegendItem_Shown()
     * @model
     * @generated
     */
    @Override
    boolean isShown();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.LegendItem#isShown <em>Shown</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Shown</em>' attribute.
     * @see #isShown()
     * @generated
     */
    void setShown(boolean value);
} // LegendItem
