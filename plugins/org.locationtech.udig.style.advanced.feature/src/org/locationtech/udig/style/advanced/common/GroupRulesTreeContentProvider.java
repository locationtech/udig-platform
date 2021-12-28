/**
 * JGrass - Free Open Source Java GIS http://www.jgrass.org
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.common;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.FeatureTypeStyleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;

/**
 * A tree view content provider for {@link FeatureTypeStyleWrapper}s with {@link RuleWrapper}
 * children.
 *
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GroupRulesTreeContentProvider implements ITreeContentProvider {

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof FeatureTypeStyleWrapper) {
            return true;
        }
        return false;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof RuleWrapper) {
            RuleWrapper ruleWrapper = (RuleWrapper) element;
            return ruleWrapper.getParent();
        }
        return null;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List<?>) {
            List<?> list = (List<?>) inputElement;
            if (list.isEmpty()) {
                return null;
            }
            Object object = list.get(0);
            if (object instanceof FeatureTypeStyleWrapper) {
                FeatureTypeStyleWrapper[] array = list
                        .toArray(new FeatureTypeStyleWrapper[list.size()]);
                return array;
            }
            if (object instanceof RuleWrapper) {
                RuleWrapper[] array = list.toArray(new RuleWrapper[list.size()]);
                return array;
            }
        }

        return null;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof FeatureTypeStyleWrapper) {
            FeatureTypeStyleWrapper ftsW = (FeatureTypeStyleWrapper) parentElement;
            List<RuleWrapper> rulesWrapperList = ftsW.getRulesWrapperList();
            RuleWrapper[] array = rulesWrapperList
                    .toArray(new RuleWrapper[rulesWrapperList.size()]);
            return array;
        }
        return null;
    }
}
