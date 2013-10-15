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
