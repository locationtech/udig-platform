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

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

/**
 * Delegates to the {@link ICellModifier} object responsible for the modifying the
 * {@link SummaryData} object. (It is obtained from the {@link SummaryData})
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SummaryCellModifier implements ICellModifier {

    private Collection<SummaryData> data;

    public SummaryCellModifier( Collection<SummaryData> data ) {
        this.data = data;
    }

    public boolean canModify( Object element, String property ) {
        for( SummaryData item : data ) {
            if (item == element)
                return item.getModifier().canModify(element, property);
        }
        return false;
    }

    public Object getValue( Object element, String property ) {
        for( SummaryData item : data ) {
            if (item == element)
                return item.getModifier().getValue(element, property);
        }

        return null;
    }

    public void modify( Object element, String property, Object value ) {
        for( SummaryData item : data ) {
            if (item == ((Item)element).getData()){
                item.getModifier().modify(element, property, value);
            }
        }
    }

}
