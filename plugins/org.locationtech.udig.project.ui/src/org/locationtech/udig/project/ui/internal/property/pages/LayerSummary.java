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
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.summary.SummaryControl;
import org.locationtech.udig.project.ui.summary.SummaryData;
import org.locationtech.udig.ui.ProgressManager;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
        data.add(new SummaryData(Messages.LayerSummary_id, layer.getGeoResource().getDisplayID()));
        data.add(new SummaryData(Messages.LayerSummary_bounds, bounds==null?Messages.LayerSummary_unknownBounds:parseBounds(bounds)));
        data.add(new SummaryData(Messages.LayerSummary_selection,layer.getFilter()));
        data.add(new SummaryData(Messages.LayerSummary_status, layer.getStatusMessage()));
        try {
            if (layer.getGeoResource().canResolve(IService.class)) {
                IService service = layer.getGeoResource().resolve(IService.class, ProgressManager.instance().get());
                ShapefileDataStore store = service.resolve(ShapefileDataStore.class, ProgressManager.instance().get());
                if (store != null) {
                    data.add(new SummaryData(Messages.LayerSummary_charset, store.getCharset()));
                }
            }
        } catch (Exception e) {
            //
        }               
    
        if ( layer.getSchema()!=null ){
            SimpleFeatureType schema = layer.getSchema();
            SummaryData schemaData=new SummaryData(Messages.LayerSummary_featureType, schema.getName().getLocalPart());
            SummaryData[] children=new SummaryData[schema.getAttributeCount()];
            
            for( int i = 0; i < children.length; i++ ) {
                AttributeDescriptor attributeType = schema.getDescriptor(i);
                children[i]=new SummaryData(attributeType.getLocalName(), attributeType.getType().getBinding().getSimpleName());
                children[i].setParent(schemaData);
                List<SummaryData> attTypeChildren=new ArrayList<SummaryData>();
                attTypeChildren.add(new SummaryData(Messages.LayerSummary_nillable, attributeType.isNillable()));
                attTypeChildren.get(0).setParent(children[i]);
                List<Filter> restrictions = attributeType.getType().getRestrictions();
                for (Filter filter : restrictions) {
                    SummaryData summaryData = new SummaryData(Messages.LayerSummary_restriction, filter);
                    attTypeChildren.add(summaryData);
                    summaryData.setParent(children[i]);
                }
                SummaryData summaryData = new SummaryData(Messages.LayerSummary_maxOccurs, attributeType.getMaxOccurs());
                attTypeChildren.add(summaryData);
                summaryData.setParent(children[i]);
                summaryData = new SummaryData(Messages.LayerSummary_minOccurs, attributeType.getMinOccurs());
                attTypeChildren.add(summaryData);
                summaryData.setParent(children[i]);

                children[i].setChildren(attTypeChildren.toArray(new SummaryData[0]));
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
