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
