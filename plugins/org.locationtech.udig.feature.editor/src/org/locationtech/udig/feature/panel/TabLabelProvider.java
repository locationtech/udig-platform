/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.feature.panel;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.opengis.feature.simple.SimpleFeature;

public class TabLabelProvider extends LabelProvider {
    ILabelProvider delegate;

    public TabLabelProvider() {
        this(null);
    }

    public TabLabelProvider(ILabelProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof StructuredSelection) {
            StructuredSelection sel = (StructuredSelection) element;
            if (sel.isEmpty()) {
                return "Please select a feature";
            }
            element = sel.getFirstElement();
        }
        if (element == null) {
            return null;
        }
        if (element instanceof SimpleFeature) {
            SimpleFeature feature = (SimpleFeature) element;
            if (delegate != null) {
                String text = delegate.getText(feature);
                if (text != null) {
                    return text;
                }
            }
            return feature.getID();
        }
        if (element instanceof FeaturePanelTabDescriptor) {
            FeaturePanelTabDescriptor tabDescriptor = (FeaturePanelTabDescriptor) element;
            String title = tabDescriptor.getEntry().getTitle();
            if (title == null || title.length() == 0) {
                title = tabDescriptor.getLabel();
            }
            return title;
        } else {
            String text = element.toString();
            return text;
        }
    }
}
