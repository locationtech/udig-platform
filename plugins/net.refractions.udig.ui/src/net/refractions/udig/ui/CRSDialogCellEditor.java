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
