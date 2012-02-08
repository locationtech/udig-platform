/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package net.refractions.udig.project.internal;

import net.refractions.udig.project.ILegendItem;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Legend Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.refractions.udig.project.internal.LegendItem#getName <em>Name</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.LegendItem#getGlyph <em>Glyph</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.LegendItem#isShown <em>Shown</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.refractions.udig.project.internal.ProjectPackage#getLegendItem()
 * @model superTypes="net.refractions.udig.project.internal.ILegendItem"
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
     * @see net.refractions.udig.project.internal.ProjectPackage#getLegendItem_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.LegendItem#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName( String value );

    /**
     * Returns the value of the '<em><b>Glyph</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Glyph</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Glyph</em>' attribute.
     * @see #setGlyph(ImageDescriptor)
     * @see net.refractions.udig.project.internal.ProjectPackage#getLegendItem_Glyph()
     * @model dataType="net.refractions.udig.project.internal.ImageDescriptor"
     * @generated
     */
    ImageDescriptor getGlyph();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.LegendItem#getGlyph <em>Glyph</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Glyph</em>' attribute.
     * @see #getGlyph()
     * @generated
     */
    void setGlyph( ImageDescriptor value );

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
     * @see net.refractions.udig.project.internal.ProjectPackage#getLegendItem_Shown()
     * @model
     * @generated
     */
    boolean isShown();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.LegendItem#isShown <em>Shown</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Shown</em>' attribute.
     * @see #isShown()
     * @generated
     */
    void setShown( boolean value );
} // LegendItem
