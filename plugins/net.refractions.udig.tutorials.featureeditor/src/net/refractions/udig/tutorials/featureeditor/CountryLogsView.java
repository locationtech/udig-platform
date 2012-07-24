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
package net.refractions.udig.tutorials.featureeditor;

import java.beans.PropertyChangeEvent;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.project.EditFeature;
import net.refractions.udig.project.EditFeature.AttributeStatus;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.listener.EditFeatureListener;
import net.refractions.udig.project.listener.EditFeatureStateChangeEvent;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.opengis.feature.simple.SimpleFeature;

/**
 * This view simulates listening to the same edit feature as the {@link CountryView} and logs the
 * feature changes events that occurs.
 * 
 * @author Naz Chan
 */
public class CountryLogsView extends ViewPart implements IUDIGView, EditFeatureListener {
    
    private Text logs;
    private EditFeature editFeature;
    
    public CountryLogsView() {
        // Nothing
    }

    @Override
    public void createPartControl(Composite parent) {

        final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        final ScrolledForm form = toolkit.createScrolledForm(parent);
        form.setText("Country View Logs");
        toolkit.decorateFormHeading(form.getForm());
        
        final Composite content = form.getBody();
        content.setLayout(new MigLayout("fill", "", ""));
        
        logs = toolkit.createText(content, "", SWT.MULTI | SWT.V_SCROLL);
        logs.setEditable(false);
        logs.setLayoutData("grow");
        
    }

    @Override
    public void setFocus() {
        // Nothing
    }

    @Override
    public void setContext(IToolContext newContext) {
        // Nothing
    }

    @Override
    public IToolContext getContext() {
        return null;
    }

    @Override
    public void editFeatureChanged(SimpleFeature feature) {
        
        if (editFeature != null) {
            if (!editFeature.getIdentifier().getID().equals(feature.getIdentifier().getID())) {
                logs.setText("");
            }
        }
        
        final IEditManager editManager = ApplicationGIS.getActiveMap().getEditManager();
        editFeature = editManager.toEditFeature(feature, null);
        editFeature.addEditFeatureListener(this);
        
    }

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    @Override
    public void attributeValueBeforeChange(PropertyChangeEvent event) {
        final StringBuilder sb = new StringBuilder();
        sb.append(logs.getText());
        sb.append(LINE_SEPARATOR + "---");
        sb.append("attributeValueBeforeChange Event:");
        sb.append(eventToString(event));
        sb.append(LINE_SEPARATOR);
        logs.setText(sb.toString());
    }

    @Override
    public void attributeValueChange(PropertyChangeEvent event) {
        final StringBuilder sb = new StringBuilder();
        sb.append(logs.getText());
        sb.append(LINE_SEPARATOR + "---");
        sb.append("attributeValueChange Event:");
        sb.append(eventToString(event));
        sb.append(LINE_SEPARATOR);
        logs.setText(sb.toString());
    }

    private String eventToString(PropertyChangeEvent event) {
        final StringBuilder sb = new StringBuilder();
        sb.append(LINE_SEPARATOR + " - ");
        sb.append(event.getPropertyName());
        sb.append(LINE_SEPARATOR + " - ");
        sb.append(event.getOldValue());
        sb.append(LINE_SEPARATOR + " - ");
        sb.append(event.getNewValue());
        return sb.toString();
    }
    
    @Override
    public void attributeStateChange(EditFeatureStateChangeEvent stateChangeEvent) {
        final StringBuilder sb = new StringBuilder();
        sb.append(logs.getText());
        sb.append(LINE_SEPARATOR + "---");
        sb.append("attributeStateChange Event:");
        sb.append(eventToString(stateChangeEvent));
        sb.append(LINE_SEPARATOR);
        logs.setText(sb.toString());
    }

    private String eventToString(EditFeatureStateChangeEvent stateChangeEvent) {
        final StringBuilder sb = new StringBuilder();
        sb.append(LINE_SEPARATOR + " - ");
        sb.append(stateChangeEvent.getEventType().toString());
        final AttributeStatus status = stateChangeEvent.getAttributeStatus(); 
        sb.append(LINE_SEPARATOR + " - Dirty: " + status.getDirty());
        sb.append(LINE_SEPARATOR + " - Enabled: " + status.getEnabled());
        sb.append(LINE_SEPARATOR + " - Editable: " + status.getEditable());
        sb.append(LINE_SEPARATOR + " - Visible: " + status.getVisible());
        for (String error : status.getErrors()) {
            sb.append(LINE_SEPARATOR + " - Errors: " + error);    
        }
        for (String warning : status.getWarnings()) {
            sb.append(LINE_SEPARATOR + " - Warnings: " + warning);    
        }
        return sb.toString();
    }
    
}
