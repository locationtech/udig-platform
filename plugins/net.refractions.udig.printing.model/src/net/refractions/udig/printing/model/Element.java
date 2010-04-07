/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.printing.model;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;

/**
 * An <code>Element</code> object represents an item of the printing framework
 * and has the following characteristics:
 * <ul>
 * <li>a location</li>
 * <li>a size</li>
 * </ul> 
 * Each object to be included in a Page must implement this interface.
 * 
 * @author Richard Gould
 * @since 0.3
 * @model
 */
public interface Element extends EObject{
    
    /**
     * @return this element's location (x, y)
     * @see Point
     * @model
     */
    public abstract Point getLocation();

	/**
     * Sets the value of the '{@link net.refractions.udig.printing.model.Element#getLocation <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * Sets this element's location to the provided value
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Location</em>' attribute.
     * @see #getLocation()
     * @generated
     */
    void setLocation(Point value);

    /**
     * The size of the paper that the page will be printed on calculated in mm
     * 
     * @return The paper size (width, height) that the page can be printed to
     * @model
     */
    public abstract Dimension getPaperSize();
    /**
     * Sets the value of the '{@link net.refractions.udig.printing.model.Element#getPaperSize <em>Paper Size</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Paper Size</em>' attribute.
     * @see #getPaperSize()
     * @generated
     */
    void setPaperSize(Dimension value);

    /**
     * The size is calculated in pixels, and represent coordinates on a page.
     * 
     * @return this element's size (width, height)
     * @model
     * @see Dimension
     */
    public abstract Dimension getSize();

	/**
     * Sets the value of the '{@link net.refractions.udig.printing.model.Element#getSize <em>Size</em>}' attribute.
     * <!-- begin-user-doc -->
     * Set this element's size to the provided value
     * The size is expected in pixels, and represent coordinates on the page.
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Size</em>' attribute.
     * @see #getSize()
     * @generated
     */
    void setSize(Dimension value);

    /**
     * Returns the page the element is contained within.
     *
     * @return
     */
    Page getPage();
}
