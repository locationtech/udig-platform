/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.project.ui.internal.property.pages;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.summary.SummaryControl;
import net.refractions.udig.project.ui.summary.SummaryData;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.vividsolutions.jts.geom.Envelope;

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
