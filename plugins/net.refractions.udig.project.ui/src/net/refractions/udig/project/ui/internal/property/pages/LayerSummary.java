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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.internal.Layer;
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
import org.geotools.feature.AttributeType;
import org.geotools.feature.FeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Shows a summary of the layer
 * @author Jesse
 * @since 1.1.0
 */
public class LayerSummary extends PropertyPage implements IWorkbenchPropertyPage {

    private String newName;
    private SummaryData nameData;
    private SummaryControl summaryControl;
    private String oldName;

    @Override
    protected Control createContents( Composite parent ) {
        final Layer layer = (Layer) getElement();
        final CoordinateReferenceSystem layerCRS = layer.getCRS();
        ReferencedEnvelope bounds = layer.getBounds(ProgressManager.instance().get(), layerCRS);

        final List<SummaryData> data=new ArrayList<SummaryData>();
        String name = layer.getName();
        nameData=new SummaryData(Messages.LayerSummary_name, name==null?"":name); //$NON-NLS-1$
        nameData.setModifier(new NameModifier());
        data.add(nameData);
        newName=oldName=nameData.getInfo();
        data.add(new SummaryData(Messages.LayerSummary_id,layer.getID()));
        data.add(new SummaryData(Messages.LayerSummary_bounds, bounds==null?Messages.LayerSummary_unknownBounds:parseBounds(bounds)));
        data.add(new SummaryData(Messages.LayerSummary_selection,layer.getFilter()));
        data.add(new SummaryData(Messages.LayerSummary_status, layer.getStatusMessage()));
        if ( layer.getSchema()!=null ){
            FeatureType schema = layer.getSchema();
            SummaryData schemaData=new SummaryData(Messages.LayerSummary_featureType, schema.getTypeName());
            SummaryData[] children=new SummaryData[schema.getAttributeCount()];

            for( int i = 0; i < children.length; i++ ) {
                AttributeType attributeType = schema.getAttributeType(i);
                children[i]=new SummaryData(attributeType.getName(), attributeType.getType().getSimpleName());
                children[i].setParent(schemaData);
                SummaryData[] attTypeChildren=new SummaryData[4];

                attTypeChildren[0]=new SummaryData(Messages.LayerSummary_nillable, attributeType.isNillable());
                attTypeChildren[0].setParent(children[i]);
                attTypeChildren[1]=new SummaryData(Messages.LayerSummary_restriction, attributeType.getRestriction());
                attTypeChildren[1].setParent(children[i]);
                attTypeChildren[2]=new SummaryData(Messages.LayerSummary_maxOccurs, attributeType.getMaxOccurs());
                attTypeChildren[2].setParent(children[i]);
                attTypeChildren[3]=new SummaryData(Messages.LayerSummary_minOccurs, attributeType.getMinOccurs());
                attTypeChildren[3].setParent(children[i]);

                children[i].setChildren(attTypeChildren);
            }
            schemaData.setChildren(children);
            data.add(schemaData);
        }

        summaryControl = new SummaryControl(data);
        return summaryControl.createControl(parent);
    }

    public static String parseBounds( Envelope env ){
        String minx = chopDouble( env.getMinX() );
        String maxx = chopDouble( env.getMaxX() );
        String miny = chopDouble( env.getMinY() );
        String maxy = chopDouble( env.getMaxY() );
        return "("+minx+","+miny+") ("+maxx+","+maxy+") ";     //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
    }

    private static String chopDouble( double d ){
        String s=String.valueOf(d);
        int end=s.indexOf('.')+2;
        while( end>s.length() )
            end--;
        return s.substring(0, end);
    }

    @Override
    protected void performApply() {
        summaryControl.applyEdit();
        final Layer layer = (Layer) getElement();

        if (!newName.equals(oldName)) {
            layer.setName(newName);
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
        final Layer layer = (Layer) getElement();
        if( !newName.equals(oldName)){
            newName = oldName;
            nameData.setInfo(oldName);
            summaryControl.refresh(nameData);
            layer.setName(oldName);
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

}
