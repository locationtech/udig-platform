/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.select;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.command.factory.EditCommandFactory;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Cell modifier for modifying features by sending commands to the map.  
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class FeatureTypeCellModifier implements ICellModifier {

    private final IMap map;
    private final SimpleFeatureType schema;
    private ILayer layer;

    public FeatureTypeCellModifier( ILayer layer ) {
        this.map=layer.getMap();
        this.schema=layer.getSchema();
        this.layer=layer;
    }

    public boolean canModify( Object element, String property ) {
    	if ( schema.indexOf(property) != -1 ) // the schema has the property in it
    		return true;
    	return false;
    }

    public Object getValue( Object element, String property ) {
        Object attribute = ((SimpleFeature)element).getAttribute(property);
        return attribute;
    }

    public void modify( Object element, String property, Object value ) {
        Item item=(Item) element;
        SimpleFeature feature=(SimpleFeature) item.getData();
        Object oldValue=feature.getAttribute(property);
        if( oldValue==null ){
            if( value==null )
                return;
            else
                makeModification(feature, layer, property, value, item);
        }else{
            if( !oldValue.equals(value) )
                makeModification(feature, layer, property, value, item);
        }
        
        
    }

    /**
     * called to actually make the modification to the feature.
     *
     * @param feature feature to modify
     * @param layer layer feature is has been taken from
     * @param property  name of the attribute to modify
     * @param value new value
     * @param item TODO
     */
    protected void makeModification( SimpleFeature feature, ILayer layer, String property, Object value, Item item ) {
        UndoableMapCommand c = 
            EditCommandFactory.getInstance().createSetAttributeCommand(feature, layer, property, value);
        map.sendCommandASync(c);
    }

}
