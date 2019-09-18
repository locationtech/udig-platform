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

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.opengis.feature.simple.SimpleFeature;

public class AttributeComparator implements Comparator<SimpleFeature>, Serializable {
    /** long serialVersionUID field */
    private static final long serialVersionUID = -3521669094688139495L;
    private int sortDir;
    private String xpath;

    public AttributeComparator( int dir, String xpath ) {
        if( dir==SWT.UP ){
            this.sortDir=1;
        }else if( dir==SWT.DOWN ){
            this.sortDir=-1;
        }else
            throw new IllegalArgumentException("dir must be SWT.UP or SWT.DOWN was: "+dir); //$NON-NLS-1$
        this.xpath=xpath;
    }

    @SuppressWarnings("unchecked")
    public int compare(SimpleFeature f0, SimpleFeature f1) {
        
        Object data0 = f0.getAttribute(xpath);
        Object data1 = f1.getAttribute(xpath);
        int result;

        if( data0==null ){
            if( data1==null )
                return 0;
            else
                return sortDir * 1;
        } else {
            if (data1==null) {
                return sortDir * -1;
            }
        }
        
        if( data0.equals(data1) ){
            result=1;
        }else if( data0 instanceof Comparable && data1 instanceof Comparable ){
            Comparable<Object> comparable0=(Comparable) data0;
            Comparable<Object> comparable1=(Comparable) data1;
            result=comparable0.compareTo(comparable1)>0?1:-1;
        }else{
            result = 1;
        }
        
        return sortDir*result;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + sortDir;
        result = PRIME * result + ((xpath == null) ? 0 : xpath.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AttributeComparator other = (AttributeComparator) obj;
        if (sortDir != other.sortDir)
            return false;
        if (xpath == null) {
            if (other.xpath != null)
                return false;
        } else if (!xpath.equals(other.xpath))
            return false;
        return true;
    }
    
    
}
