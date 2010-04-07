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

import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

/**
 * Promotes the Features selected by the filter to the top if SWT.UP.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SelectionComparator implements Comparator<SimpleFeature> {

    private static final Comparator<SimpleFeature> NATURAL_ORDER_COMPARATOR = new Comparator<SimpleFeature>(){

        public int compare( SimpleFeature o1, SimpleFeature o2 ) {
            return 0;
        }
    };
    
    private final Filter filter;
    private final int direction;
    private final Comparator<SimpleFeature> subComparator;

    /**
     * 
     * @param filter the filter to use to promote the selected features
     * @param direction SWT.UP to put selected features at the start of the list, SWT.DOWN to put the selected features on the end
     */
    public SelectionComparator(Filter filter, int direction) {
        this( filter, direction, NATURAL_ORDER_COMPARATOR );
    }
    /**
     * 
     * @param filter2 the filter to use to promote the selected features
     * @param direction SWT.UP to put selected features at the start of the list, SWT.DOWN to put the selected features on the end
     * @param subComparator this comparator will sort the same-type items.  IE selection will all be at the top but sorted by this comparator.
     */
    public SelectionComparator(org.opengis.filter.Filter filter2, int direction, Comparator<SimpleFeature> subComparator) {
        this.filter=filter2;
        this.direction=direction==SWT.UP?1:-1;
        this.subComparator=subComparator;
    }
    
    public int compare( SimpleFeature o1, SimpleFeature o2 ) {
        boolean f1Contained=filter.evaluate(o1);
        boolean f2Contained=filter.evaluate(o2);
        
        if( f1Contained && !f2Contained )
            return -1*direction;

        if( f2Contained && !f1Contained )
            return 1*direction;
        
        return subComparator.compare(o1, o2);
    }

}
