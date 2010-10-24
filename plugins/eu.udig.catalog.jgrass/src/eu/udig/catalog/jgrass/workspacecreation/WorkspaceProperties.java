/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
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
package eu.udig.catalog.jgrass.workspacecreation;

import java.util.ArrayList;
import java.util.List;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class WorkspaceProperties {
    public String locationPath = null;
    public CoordinateReferenceSystem crs = null;
    public double north = 100;
    public double south = 0;
    public double east = 100;
    public double west = 0;
    public double xres = 100;
    public double yres = 100;
    public List<String> mapsets = new ArrayList<String>();
}
