/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
 */
package net.refractions.udig.ui;

/**
 * Maps between the Name of a type in the combo box cell editor and the class of the type.
 * 
 * @author jones
 * @since 1.1.0
 */
public class LegalAttributeTypes {
    private final String name;
    private final Class type;
    
    public LegalAttributeTypes(String nameA, Class typeA) {
        name=nameA;
        type=typeA;
    }
    
    public String getName(){
        return name;
    }
    
    public Class getType(){
        return type;
    }
    
}
