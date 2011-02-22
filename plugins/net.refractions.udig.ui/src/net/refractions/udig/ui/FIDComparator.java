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

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.geotools.feature.Feature;

/**
 * Sorts features according to their FIDS.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class FIDComparator implements Comparator<Feature>, Serializable {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 6541004741916267811L;
    private int dir;

    /**
     * @param dir SWT.UP or SWT.DOWN.  If SWT.UP then the largest fids are at the top (sorted top to bottom).
     */
    public FIDComparator(int dir) {
        if( dir==SWT.UP ){
            this.dir=-1;
        }else if( dir==SWT.DOWN ){
            this.dir=1;
        }else
            throw new IllegalArgumentException("dir must be SWT.UP or SWT.DOWN was: "+dir); //$NON-NLS-1$
    }

    public int compare( Feature o1, Feature o2 ) {

        String id1 = o1.getID();
        String id2 = o2.getID();

        // we're going to try to strip out the same section and see if the remaining is a number
        // this is so that id.2 will be sorted before id.10
        // assumption is that the number is at the end... not always a good assumption.
        int i=1;
        while(  i<id2.length() && i<id1.length() && id2.regionMatches(0, id1, 0, i) ){
            i++;
        }
        String diff1 = id1.substring(i-1);
        String diff2 = id2.substring(i-1);

        try{
            Integer num1=Integer.valueOf(diff1);
            Integer num2=Integer.valueOf(diff2);
            return dir*num1.compareTo(num2);
        }catch (NumberFormatException e) {
            // oh well it was worth a try.
        }
        return dir*id1.compareTo(id2);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + dir;
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
        final FIDComparator other = (FIDComparator) obj;
        if (dir != other.dir)
            return false;
        return true;
    }


}
