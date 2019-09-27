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

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + direction;
        result = prime * result + ((filter == null) ? 0 : filter.toString().hashCode());
        result = prime * result + ((subComparator == null) ? 0 : subComparator.hashCode());
        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SelectionComparator other = (SelectionComparator) obj;
        if (direction != other.direction)
            return false;
        if (filter == null) {
            if (other.filter != null)
                return false;
        } else if (!StringUtils.equals(filter.toString(), other.filter.toString()))
            return false;
        if (subComparator == null) {
            if (other.subComparator != null)
                return false;
        } else if (!subComparator.equals(other.subComparator))
            return false;
        return true;
    }
}
