/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.catalog.jgrass.core;

import com.vividsolutions.jts.geom.Coordinate;

public class JGTtmsProperties {

    /**
     * Possible schemas
     */
    public static enum TILESCHEMA {
        tms, google
    }

    public String HOST_NAME;
    public String PROTOCOL = "http"; //$NON-NLS-1$
    public int ZOOM_MIN = 0;
    public int ZOOM_MAX = 18;

    public String tilePart;
    public boolean isFile = true;
    public Coordinate centerPoint = null;
    public TILESCHEMA type = TILESCHEMA.google;

}
