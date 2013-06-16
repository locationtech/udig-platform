/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2000, 2008 IBM Corporation and others
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.refractions.udig.style.sld.editor.internal;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A cell editor that manages a color field.
 * The cell editor's value is the color (an SWT <code>RBG</code>).
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * @see ColorCellEditor
 */
public class ColourCellEditor extends CellEditor {

        Object value;
        
        /**
         * Creates a new color cell editor parented under the given control.
         * The cell editor value is black (<code>RGB(0,0,0)</code>) initially, and has no 
         * validator.
         *
         * @param parent the parent control
         */
        public ColourCellEditor(Composite parent) {
            this(parent, SWT.NONE);
        }

        /**
         * Creates a new color cell editor parented under the given control.
         * The cell editor value is black (<code>RGB(0,0,0)</code>) initially, and has no 
         * validator.
         *
         * @param parent the parent control
         * @param style the style bits
         * @since 2.1
         */
        public ColourCellEditor(Composite parent, int style) {
            super(parent, style);
            doSetValue(new RGB(0, 0, 0));
        }

        /* (non-Javadoc)
         * Method declared on CellEditor.
         */
        public void dispose() {
            super.dispose();
        }

        /* (non-Javadoc)
         * Method declared on DialogCellEditor.
         */
        protected Object openDialogBox(Shell shell) {
            ColorDialog dialog = new ColorDialog(shell);
            //Object value = getValue();
            if (value != null)
                dialog.setRGB((RGB) value);
            value = dialog.open();
            return dialog.getRGB();
        }

        @Override
        protected Control createControl( Composite cell ) {
              return null;
        }

        @Override
        protected Object doGetValue() {
            value = openDialogBox(Display.getCurrent().getActiveShell());
            return value;
        }

        @Override
        protected void doSetFocus() {
        }

        @Override
        protected void doSetValue( Object value ) {
            this.value = value;
        }
    }
