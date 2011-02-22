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
package net.refractions.udig.printing.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.ui.Template;

/**
 * An abstract implementation of a Template that provides basic functionality.
 *
 * Classes that extend this should typically create their own boxes and
 * organize them according to their own desires.
 *
 * @author Richard Gould
 */
public abstract class AbstractTemplate implements Template {

	protected List<Box> boxes;

	/**
	 * Create a basic AbstractTemplate. Initalizes the boxes list.
	 */
	public AbstractTemplate() {
		boxes = new ArrayList<Box>();
	}

	/** (non-Javadoc)
	 * @see net.refractions.udig.printing.ui.Template#iterator()
	 */
	public Iterator<Box> iterator() {
		return boxes.iterator();
	}

	public String toString() {
	    return getName();
	}

    public Template clone() {
        try {
            return (Template) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
