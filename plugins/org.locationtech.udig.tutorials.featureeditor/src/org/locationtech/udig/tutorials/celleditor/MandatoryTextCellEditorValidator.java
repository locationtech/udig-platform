/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.celleditor;

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

    @Override
    public String isValid( Object value ) {
        final String textValue = (String) value;
        if (BLANK.equals(textValue)) {
            return ERR_MSG;
        }
        return null;
    }

}
