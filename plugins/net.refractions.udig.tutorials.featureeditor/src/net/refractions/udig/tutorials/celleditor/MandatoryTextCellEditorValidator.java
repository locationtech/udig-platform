/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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
package net.refractions.udig.tutorials.celleditor;

import org.eclipse.jface.viewers.ICellEditorValidator;

/**
 * A sample implementation of an ICellEditorValidator that checks a TextCellEditor for mandatory
 * input.
 * 
 * @author Naz Chan
 * @since 1.3.1
 */
public class MandatoryTextCellEditorValidator implements ICellEditorValidator {

    private static final String BLANK = ""; //$NON-NLS-1$
    private static final String ERR_MSG = "Must not be blank."; //$NON-NLS-1$
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
     */
    @Override
    public String isValid( Object value ) {
        final String textValue = (String) value;
        if (BLANK.equals(textValue)) {
            return ERR_MSG;
        } 
        return null;
    }

}
