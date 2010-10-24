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

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eu.udig.style.advanced.common.styleattributeclasses.FeatureTypeStyleWrapper;
import eu.udig.style.advanced.common.styleattributeclasses.RuleWrapper;

/**
 * A tree view content provider for {@link FeatureTypeStyleWrapper}s with {@link RuleWrapper} childs.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GroupRulesTreeContentProvider implements ITreeContentProvider {

    
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
    }

    
    public void dispose() {
    }

    
    public boolean hasChildren( Object element ) {
        if (element instanceof FeatureTypeStyleWrapper) {
            return true;
        }
        return false;
    }

    
    public Object getParent( Object element ) {
        if (element instanceof RuleWrapper) {
            RuleWrapper ruleWrapper = (RuleWrapper) element;
            return ruleWrapper.getParent();
        }
        return null;
    }

    
    public Object[] getElements( Object inputElement ) {
        if (inputElement instanceof List< ? >) {
            List< ? > list = (List< ? >) inputElement;
            if (list.size() == 0) {
                return null;
            }
            Object object = list.get(0);
            if (object instanceof FeatureTypeStyleWrapper) {
                FeatureTypeStyleWrapper[] array = (FeatureTypeStyleWrapper[]) list.toArray(new FeatureTypeStyleWrapper[list
                        .size()]);
                return array;
            }
            if (object instanceof RuleWrapper) {
                RuleWrapper[] array = (RuleWrapper[]) list.toArray(new RuleWrapper[list.size()]);
                return array;
            }
        }

        return null;
    }

    
    public Object[] getChildren( Object parentElement ) {
        if (parentElement instanceof FeatureTypeStyleWrapper) {
            FeatureTypeStyleWrapper ftsW = (FeatureTypeStyleWrapper) parentElement;
            List<RuleWrapper> rulesWrapperList = ftsW.getRulesWrapperList();
            RuleWrapper[] array = (RuleWrapper[]) rulesWrapperList.toArray(new RuleWrapper[rulesWrapperList.size()]);
            return array;
        }
        return null;
    }
}
