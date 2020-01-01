/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.property.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.summary.SummaryControl;
import org.locationtech.udig.project.ui.summary.SummaryData;
import org.locationtech.udig.ui.ProgressManager;

/**
 * Shows a summary of the layer
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class MapSummary extends PropertyPage implements IWorkbenchPropertyPage {
    private String newAbstract, newName;
    private String oldAbstract, oldName;
    private SummaryData nameData;
    private SummaryData abstractData;
    private SummaryControl summaryControl;

    @Override
    protected Control createContents( Composite parent ) {
        final Map map = (Map) getElement();
        Envelope bounds;
        bounds = map.getBounds(ProgressManager.instance().get());

        final List<SummaryData> data = new ArrayList<SummaryData>();

        String name = map.getName();
        nameData = new SummaryData(Messages.LayerSummary_name, name==null?"":name); //$NON-NLS-1$
        nameData.setModifier(new NameModifier());
        data.add(nameData);
        newName=oldName=nameData.getInfo();
        
        String abstract1 = map.getAbstract();
        abstractData = new SummaryData(Messages.MapSummary_abstract, abstract1==null?"":abstract1); //$NON-NLS-1$
        abstractData.setModifier(new AbstractModifier());
        data.add(abstractData);
        newAbstract=oldAbstract=abstractData.getInfo();
        
        data.add(new SummaryData(Messages.LayerSummary_id, map.getID()));
        data.add(new SummaryData(Messages.MapSummary_mapBounds, bounds == null
                ? Messages.LayerSummary_unknownBounds
                : LayerSummary.parseBounds(bounds)));
        data.add(new SummaryData(Messages.MapSummary_viewportBounds, LayerSummary.parseBounds(map
                .getViewportModel().getBounds())));

        summaryControl = new SummaryControl(data);
        return summaryControl.createControl(parent);
    }

    @Override
    protected void performApply() {
        summaryControl.applyEdit();
        final Map map = (Map) getElement();

        if (!newName.equals(oldName)) {
            map.setName(newName);
        }
        if (!newAbstract.equals(oldAbstract)) {
            map.setAbstract(newAbstract);
        }
    }

    @Override
    public boolean performCancel() {
        performDefaults();
        return super.performCancel();
    }
    
    @Override
    public boolean performOk() {
        performApply();
        return super.performOk();
    }
    
    @Override
    protected void performDefaults() {
        summaryControl.cancelEdit();
        final Map map = (Map) getElement();
        if( !newAbstract.equals(oldAbstract)){
            newAbstract = oldAbstract;
            abstractData.setInfo(oldAbstract);
            summaryControl.refresh(abstractData);
            map.setAbstract(oldAbstract);
        }
        if( !newName.equals(oldName) ){
            newName = oldName;
            nameData.setInfo(oldName);
            summaryControl.refresh(nameData);
            map.setName(oldName);
        }
    }

    private class NameModifier implements ICellModifier {

        public boolean canModify( Object element, String property ) {
            if (element == nameData)
                return true;
            return false;
        }

        public Object getValue( Object element, String property ) {

            if( !newName.equals(oldName) ) return newName;
            return ((SummaryData) element).getInfo();
        }

        public void modify( Object element, String property, Object value ) {
            if (((TreeItem)element).getData() == nameData){
                newName = (String) value;
                nameData.setInfo(newName);
                summaryControl.refresh(nameData);
            }
        }
    }

    private class AbstractModifier implements ICellModifier {

        public boolean canModify( Object element, String property ) {
            if (element == abstractData)
                return true;
            return false;
        }

        public Object getValue( Object element, String property ) {
            if( !newAbstract.equals(oldAbstract) ) return newAbstract;
            
            return ((SummaryData) element).getInfo();
        }

        public void modify( Object element, String property, Object value ) {
            if (((TreeItem)element).getData() == abstractData){
                newAbstract = (String) value;
                nameData.setInfo(newName);
                summaryControl.refresh(nameData);
            }
        }
    }
}
