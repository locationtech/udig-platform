/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

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
