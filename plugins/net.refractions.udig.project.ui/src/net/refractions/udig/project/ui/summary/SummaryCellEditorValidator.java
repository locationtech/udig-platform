/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.ui.summary;

import java.util.Collection;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Delegates to the {@link ICellEditorValidator} object responsible for the modifying the
 * {@link SummaryData} object. (It is obtained from the {@link SummaryData})
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SummaryCellEditorValidator implements ICellEditorValidator {

    private Collection<SummaryData> data;
    private Tree tree;

    public SummaryCellEditorValidator( Collection<SummaryData> data, Tree tree ) {
        this.data = data;
        this.tree=tree;
    }

    public String isValid( Object value ) {
        TreeItem[] selection = tree.getSelection();
        Object element = selection[0].getData();
        for( SummaryData item : data ) {
            if (item == element)
                return item.getValidator().isValid(value);
        }
        return null;
    }

}
