/* uDig - User Friendly Desktop Internet GIS
 * http://udig.refractions.net
 * (c) 2010, Vienna City
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.geometry.split;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

/**
 * Maintains the result of {@link UsefulSplitLineBuilder} process. 
 * <p>
 * The sub product of useful split line process is:
 * <ul>
 * <li>a collection of useful split line fragments</li> 
 * <li>remaining split line</li> 
 * </ul>
 * </p>
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
final class UsefulSplitLine {


    /** fragment of split line that intersect with the polygon to split*/
    private List<Geometry> usefulSplitLineFragments;
    
    /** split line which was not processed */ 
    private Coordinate[] remaininSplitLine; 


    public List<Geometry> getUsefulSplitLineFragments() {
        return usefulSplitLineFragments;
    }

    public void setUsefulSplitLineFragments( List<Geometry> usefulSplitLineFragments ) {
        this.usefulSplitLineFragments = usefulSplitLineFragments;
    }

    public Coordinate[] getRemaininSplitLine() {
        return remaininSplitLine;
    }

    public void setRemaininSplitLine( Coordinate[] remaininSplitLine ) {
        this.remaininSplitLine = remaininSplitLine;
    }
    
    public UsefulSplitLine( List<Geometry> usefulSplitLineSegments,
            Coordinate[] splitLineCoordsInPolygon ) {
        
        this.usefulSplitLineFragments = usefulSplitLineSegments;
        this.remaininSplitLine = splitLineCoordsInPolygon;
    }
}
