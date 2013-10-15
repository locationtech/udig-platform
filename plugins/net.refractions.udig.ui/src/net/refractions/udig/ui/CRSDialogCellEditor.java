/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A dialog cell editor that opens a CRSChooser dialog.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class CRSDialogCellEditor extends DialogCellEditor {
        public CRSDialogCellEditor( Tree tree ) {
            super(tree);
        }

		@Override
		protected void updateContents(Object value) {
			CoordinateReferenceSystem crs = (CoordinateReferenceSystem) value;
			if( crs != null ){
				super.updateContents(crs.getName());
			}
		}
		
        @Override
        protected Object openDialogBox( Control cellEditorWindow ) {
            
            final CRSChooserDialog d = new CRSChooserDialog(cellEditorWindow
				.getDisplay().getActiveShell(),
				(CoordinateReferenceSystem) getValue());
		d.setBlockOnOpen(true);
		d.open( );
            if( d.getResult()==null || d.getResult().equals(getValue()) )
                return null;
            return d.getResult();
        }
}
