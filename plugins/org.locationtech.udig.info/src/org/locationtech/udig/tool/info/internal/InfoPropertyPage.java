/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.info.internal;

import java.io.IOException;

import net.miginfocom.swt.MigLayout;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.ui.filter.ExpressionInput;
import org.locationtech.udig.ui.filter.ExpressionViewer;
import org.locationtech.udig.ui.filter.IExpressionViewer;

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
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.expression.Expression;

/**
 * Configuration of Info properties for {@link IGeoResource} items.
 * <p>
 * We are looking to confiure two things here:
 * <ul>
 * <li>Label Generation: expression used to provide a label for the feature when
 * displayed in a list or tree. This is used by InfoView when listing features</li>
 * <li>(unimplemented) FeatureInfo Generation: expression used to provide "FeatureInfo" summary of
 * a feature for display in InfoView or map tooltip.</li>
 * </ul>
 * @author Naz Chan (LISAsoft)
 */
public class InfoPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

    private IGeoResource resource;
    private IExpressionViewer exViewer;
    
    private static final String FEATURE_LABEL = "FEATURE_LABEL"; //$NON-NLS-1$
    
    public InfoPropertyPage() {
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
        exLabel.setText(Messages.InfoPropertyPage_labelExpression);
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
    
}
