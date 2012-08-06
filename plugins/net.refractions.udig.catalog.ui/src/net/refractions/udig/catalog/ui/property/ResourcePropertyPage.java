/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.ui.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.project.AdaptableFeature;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.filter.ExpressionInput;
import net.refractions.udig.ui.filter.ExpressionViewer;
import net.refractions.udig.ui.filter.IExpressionViewer;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.expression.Expression;

/**
 * Property page for {@link IGeoResource} items.
 * 
 * @author Naz Chan
 */
public class ResourcePropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

    private IGeoResource resource;
    private IExpressionViewer exViewer;
    
    private static final String FEATURE_LABEL = "FEATURE_LABEL"; //$NON-NLS-1$
    
    public ResourcePropertyPage() {
        // Nothing
    }

    @Override
    protected Control createContents(Composite parent) {
        
        resource = (IGeoResource) getElement().getAdapter(IGeoResource.class);
        
        final Composite page = new Composite(parent, SWT.NONE);
        final String layoutConst = "fill, wrap 1, insets 0"; //$NON-NLS-1$
        final String colConst = ""; //$NON-NLS-1$
        final String rowConst = ""; //$NON-NLS-1$
        page.setLayout(new MigLayout(layoutConst, colConst, rowConst));
        
        final Label exLabel = new Label(page, SWT.NONE);
        exLabel.setText(Messages.ResourcePropertyPage_labelExpression);
        exLabel.setLayoutData(""); //$NON-NLS-1$
        
        exViewer = new ExpressionViewer(page, SWT.MULTI);
        exViewer.getControl().setLayoutData("h 100%!, w 100%!"); //$NON-NLS-1$
        final ExpressionInput exInput = new ExpressionInput(getSchema());
        exInput.setBinding(String.class);
        exViewer.setInput(exInput);
        exViewer.refresh();
        
        setExpression(getLabelProperty());
        
        return page;
        
    }
    
    /**
     * Gets the feature type from the geoResource.
     * 
     * @return feature type
     */
    private SimpleFeatureType getSchema() {
        if (resource.canResolve(SimpleFeatureSource.class)) {
            try {
                final SimpleFeatureSource featureSource = resource.resolve(
                        SimpleFeatureSource.class, new NullProgressMonitor());
                return featureSource.getSchema();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    @Override
    public boolean performOk() {
        setLabelProperty(getExpression());
        return super.performOk();
    }
    
    @Override
    protected void performDefaults() {
        setExpression(getLabelProperty());
        super.performDefaults();
    }

    /**
     * Sets the expression viewer's expression
     * 
     * @param expression
     */
    private void setExpression(String expression) {
        if (expression != null && expression.length() > 0) {
            try {
                exViewer.setExpression(ECQL.toExpression(expression));
            } catch (CQLException e) {
                e.printStackTrace();
            }            
        } else {
            exViewer.setExpression(null);
        }
    }
    
    /**
     * Gets the expression viewer's expression
     * 
     * @return expression
     */
    private String getExpression() {
        final Expression expression = exViewer.getExpression();
        if (expression != null) {
            return ECQL.toCQL(exViewer.getExpression());    
        }
        return null;
    }
    
    /**
     * Sets the label property of the resource
     * 
     * @param value
     */
    private void setLabelProperty(String value) {
        resource.getPersistentProperties().put(FEATURE_LABEL, value);
    }
    
    /**
     * Gets the label property of the resource
     * 
     * @return label property
     */
    private String getLabelProperty() {
        return (String) resource.getPersistentProperties().get(FEATURE_LABEL);
    } 
    
    /**
     * Gets the feature label by running the feature label expression set in
     * {@link ResourcePropertyPage} against the feature.
     * 
     * @param feature
     * @return feature label
     */
    public static String getFeatureLabel(AdaptableFeature feature) {
        
        final ILayer layer = (ILayer) feature.getAdapter(ILayer.class);
        final IGeoResource resource = layer.getGeoResource();
        
        return getFeatureLabel(resource, feature);
        
    }
    
    /**
     * Gets the feature label by running the feature label expression set in
     * {@link ResourcePropertyPage} against the feature.
     * 
     * @param resource
     * @param feature
     * @return feature label
     */
    public static String getFeatureLabel(IGeoResource resource, SimpleFeature feature) {
        
        final String labelExpression = (String) resource.getPersistentProperties().get(
                ResourcePropertyPage.FEATURE_LABEL);
        
        if (labelExpression != null) {
            try {
                final Expression exp = ECQL.toExpression(labelExpression);
                return (String) exp.evaluate(feature);
            } catch (CQLException e) {
                e.printStackTrace();
            }    
        }
        
        return feature.getID();
        
    }
    
}
