/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Wien Government 
 *
 *      http://wien.gov.at
 *      http://www.axios.es 
 *
 * (C) 2009, Vienna City - Municipal Department of Automated Data Processing, 
 * Information and Communications Technologies.
 * Vienna City agrees to license under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.geometry.split;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

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
