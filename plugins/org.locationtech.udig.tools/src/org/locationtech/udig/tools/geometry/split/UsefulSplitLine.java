/*******************************************************************************
 * Copyright (c) 2010,2012 City of Vienna.
 *
 * This program and the accompanying materials are made available under the            
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0    
 * which accompanies this distribution.                                                
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at                                
 * http://www.eclipse.org/org/documents/edl-v10.php.                                   
 *                                                                                     
 * Contributors:                                                                       
 *    Aritz Davila (Axios) - initial implementation and documentation                  
 *    Mauricio Pazos (Axios) - initial implementation and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.geometry.split;

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
