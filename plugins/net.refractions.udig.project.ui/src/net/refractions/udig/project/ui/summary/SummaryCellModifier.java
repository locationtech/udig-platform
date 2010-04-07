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
