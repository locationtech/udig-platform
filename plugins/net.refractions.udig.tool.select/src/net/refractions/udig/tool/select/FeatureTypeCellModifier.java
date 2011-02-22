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
package net.refractions.udig.tool.select;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.factory.EditCommandFactory;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;

/**
 * Cell modifier for modifying features by sending commands to the map.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class FeatureTypeCellModifier implements ICellModifier {

    private final IMap map;
    private final FeatureType schema;
    private ILayer layer;

    public FeatureTypeCellModifier( ILayer layer ) {
        this.map=layer.getMap();
        this.schema=layer.getSchema();
        this.layer=layer;
    }

    public boolean canModify( Object element, String property ) {
        for( int i = 0; i < schema.getAttributeCount(); i++ ) {
            AttributeType attributeType = schema.getAttributeType(i);
            if (attributeType.getName().equals(property)){
                return true;
            }
        }
        return false;
    }

    public Object getValue( Object element, String property ) {
        Object attribute = ((Feature)element).getAttribute(property);
        return attribute;
    }

    public void modify( Object element, String property, Object value ) {
        Item item=(Item) element;
        Feature feature=(Feature) item.getData();
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
    protected void makeModification( Feature feature, ILayer layer, String property, Object value, Item item ) {
        UndoableMapCommand c =
            EditCommandFactory.getInstance().createSetAttributeCommand(feature, layer, property, value);
        map.sendCommandASync(c);
    }

}
