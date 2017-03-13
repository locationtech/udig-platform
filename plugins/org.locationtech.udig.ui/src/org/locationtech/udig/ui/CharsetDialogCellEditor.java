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

import java.nio.charset.Charset;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;

/**
 * A dialog cell editor that opens a {@link CharsetSelectionDialog} dialog.
 * 
 * @author nprigour
 */
public class CharsetDialogCellEditor extends DialogCellEditor {
	public CharsetDialogCellEditor( Tree tree ) {
		super(tree);
	}

	@Override
	protected void updateContents(Object value) {
		Charset charset = (Charset) value;
		if( charset != null ){
			super.updateContents(charset);
		}
	}

	@Override
	protected Object openDialogBox( Control cellEditorWindow ) {

		final CharsetSelectionDialog d = new CharsetSelectionDialog(cellEditorWindow
				.getDisplay().getActiveShell(), false);
		d.setBlockOnOpen(true);
		d.open( );
		if( d.getResult()==null || d.getResult()[0].equals(getValue()) )
			return null;
		return d.getResult()[0];
	}
}
