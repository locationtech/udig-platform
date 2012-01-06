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
package eu.udig.style.advanced.common;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import eu.udig.style.advanced.common.styleattributeclasses.StyleWrapper;

/**
 * Viewer filter on style names.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class StyleFilter extends ViewerFilter {

    private String searchString;

    @SuppressWarnings("nls")
    public void setSearchText( String s ) {
        // Search must be a substring of the existing value
        this.searchString = ".*" + s + ".*"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public boolean select( Viewer viewer, Object parentElement, Object element ) {
        if (searchString == null || searchString.length() == 0) {
            return true;
        }
        StyleWrapper p = (StyleWrapper) element;
        if (p.getName().matches(searchString)) {
            return true;
        }
        return false;
    }
}
