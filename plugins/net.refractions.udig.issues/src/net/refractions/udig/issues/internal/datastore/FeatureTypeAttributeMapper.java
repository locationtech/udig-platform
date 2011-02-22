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
package net.refractions.udig.issues.internal.datastore;

import java.util.ArrayList;

import net.refractions.udig.issues.internal.Messages;

import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.SchemaException;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * Returns the names of attributes given a FeatureType that map to the different issue properties.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class FeatureTypeAttributeMapper {
    private String extensionId;
    private String viewMemento;
    private String memento;
    private String groupId;
    private String id;
    private String resolution;
    private String priority;
    private String description;
    private String bounds;
    private final FeatureType schema;

    /**
     * Creates a new instance with the "prefered" feature type
     */
    public FeatureTypeAttributeMapper(String featureTypeName) throws SchemaException {
        this( createOptimalSchema(featureTypeName) );
    }

    /**
     * Creates a new instance.  Maps the required fields as good as possible to the fields present in the
     * schema.
     *
     * @param schema the schema to map to
     */
    @SuppressWarnings("unchecked")
    public FeatureTypeAttributeMapper( FeatureType schema ) {
        this.schema=schema;
        ArrayList<AttributeType> notMapped = new ArrayList<AttributeType>();
        if( schema.getDefaultGeometry()!=null )
        bounds=schema.getDefaultGeometry().getName();
        for( int i=0; i<schema.getAttributeCount();i++ ){
            AttributeType att = schema.getAttributeType(i);
            if( att==schema.getDefaultGeometry() )
                continue;
            if( bounds==null && att.getType().isAssignableFrom(MultiPolygon.class) ){
                bounds=att.getName();
                continue;
            }
            if( isExtensionID(att) && extensionId==null ){
                extensionId=att.getName();
                continue;
            }
            if( isViewMemento(att) && viewMemento==null){
                viewMemento=att.getName();
                continue;
            }
            if( isMemento(att) && memento==null){
                memento=att.getName();
                continue;
            }
            if( isGroupId(att) && groupId==null){
                groupId=att.getName();
                continue;
            }
            if( isId(att) && id==null){
                id=att.getName();
                continue;
            }
            if( isResolution(att) && resolution==null){
                resolution=att.getName();
                continue;
            }
            if( isPriority(att) && priority==null){
                priority=att.getName();
                continue;
            }
            if( isDescription(att) && description==null){
                description=att.getName();
                continue;
            }
            notMapped.add(att);
        }

        for( AttributeType type : notMapped ) {
            if( extensionId==null && isStringType(type) ){
                extensionId=type.getName();
                continue;
            }
            if( id==null && isStringType(type) ){
                id=type.getName();
                continue;
            }
            if( groupId==null && isStringType(type) ){
                groupId=type.getName();
                continue;
            }
            if( resolution==null && isStringType(type) ){
                resolution=type.getName();
                continue;
            }
            if( priority==null && isStringType(type) ){
                priority=type.getName();
                continue;
            }
            if( description==null && isStringType(type) ){
                description=type.getName();
                continue;
            }
            if( viewMemento==null && isStringType(type) ){
                viewMemento=type.getName();
                continue;
            }
            if( memento==null && isStringType(type) ){
                memento=type.getName();
                continue;
            }

        }
    }

    private static FeatureType createOptimalSchema(String featureTypeName) throws SchemaException {
        FeatureTypeBuilder builder = FeatureTypeBuilder.newInstance(featureTypeName);
        builder.addType(AttributeTypeFactory.newAttributeType("bounds", MultiPolygon.class, false, -1, null, DefaultGeographicCRS.WGS84));
        builder.addType(AttributeTypeFactory.newAttributeType("description", String.class, true, 128, "", null));
        builder.addType(AttributeTypeFactory.newAttributeType("extensionId", String.class, false, 80, "", null));
        builder.addType(AttributeTypeFactory.newAttributeType("groupId", String.class, true, 80, "", null));
        builder.addType(AttributeTypeFactory.newAttributeType("id", String.class, false, 80, "", null));
        builder.addType(AttributeTypeFactory.newAttributeType("memento", String.class, false, 1024, "", null));
        builder.addType(AttributeTypeFactory.newAttributeType("priority", String.class, true, 20, "", null));
        builder.addType(AttributeTypeFactory.newAttributeType("resolution", String.class, true, 20, "", null));
        builder.addType(AttributeTypeFactory.newAttributeType("viewMemento", String.class, true, 1024, "", null));
        return builder.getFeatureType();
    }


    @SuppressWarnings("unchecked")
    private boolean isStringType( AttributeType att ) {
        return att.getType().isAssignableFrom(String.class);
    }

    private boolean isDescription( AttributeType att ) {
        return compare(att.getName(), Messages.FeatureTypeAttributeMapper_description)&&isStringType(att);
    }


    private boolean isPriority( AttributeType att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_priority)&&isStringType(att);
    }

    private boolean isResolution( AttributeType att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_resolution)&&isStringType(att);
    }

    private boolean isId( AttributeType att ) {
        return ((compare(att.getName(),Messages.FeatureTypeAttributeMapper_id)) || compare(att.getName(), Messages.FeatureTypeAttributeMapper_issue))&&isStringType(att);
    }

    private boolean isGroupId( AttributeType att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_group)&&isStringType(att);
    }

    private boolean isMemento( AttributeType att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_memento)&&isStringType(att);
    }

    private boolean isViewMemento( AttributeType att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_viewmemento)&&isStringType(att);
    }

    private boolean isExtensionID( AttributeType att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_extensionPoint)&&isStringType(att);
    }

    private boolean compare( String arg1, String arg2){
        return arg1.toUpperCase().contains(arg2.toUpperCase());
    }

    public FeatureType getSchema() {
        return schema;
    }

    public String getDescription() {
        return description;
    }

    public String getExtensionId() {
        return extensionId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getId() {
        return id;
    }

    public String getMemento() {
        return memento;
    }

    public String getPriority() {
        return priority;
    }

    public String getResolution() {
        return resolution;
    }

    public String getViewMemento() {
        return viewMemento;
    }

    public String getBounds() {
        return bounds;
    }

    public boolean isValid() {
        if( getBounds()==null )
            return false;
        if( getDescription()==null )
            return false;
        if( getExtensionId()==null )
            return false;
        if( getGroupId()==null )
            return false;
        if( getId()==null )
            return false;
        if( getMemento()==null )
            return false;
        if( getPriority()==null )
            return false;
        if( getResolution()==null )
            return false;
        if( getViewMemento()==null )
            return false;
        return true;
    }

}
