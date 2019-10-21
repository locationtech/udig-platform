/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal.datastore;

import java.util.ArrayList;

import org.locationtech.udig.issues.internal.Messages;

import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;

import org.locationtech.jts.geom.MultiPolygon;

/**
 * Returns the names of attributes given a SimpleFeatureType that map to the different issue properties.
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
    private final SimpleFeatureType schema;

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
    public FeatureTypeAttributeMapper( SimpleFeatureType schema ) {
        this.schema=schema;
        ArrayList<AttributeDescriptor> notMapped = new ArrayList<AttributeDescriptor>();
        if( schema.getGeometryDescriptor()!=null )
        bounds=schema.getGeometryDescriptor().getName().getLocalPart();
        for( int i=0; i<schema.getAttributeCount();i++ ){
            AttributeDescriptor att = schema.getDescriptor(i);
            if( att==schema.getGeometryDescriptor() )
                continue;
            if( bounds==null && att.getType().getBinding().isAssignableFrom(MultiPolygon.class) ){
                bounds=att.getName().getLocalPart();
                continue;
            }
            if( isExtensionID(att) && extensionId==null ){
                extensionId=att.getName().getLocalPart();
                continue;
            }
            if( isViewMemento(att) && viewMemento==null){
                viewMemento=att.getName().getLocalPart();
                continue;
            }
            if( isMemento(att) && memento==null){
                memento=att.getName().getLocalPart();
                continue;
            }
            if( isGroupId(att) && groupId==null){
                groupId=att.getName().getLocalPart();
                continue;
            }
            if( isId(att) && id==null){
                id=att.getName().getLocalPart();
                continue;
            }
            if( isResolution(att) && resolution==null){
                resolution=att.getName().getLocalPart();
                continue;
            }
            if( isPriority(att) && priority==null){
                priority=att.getName().getLocalPart();
                continue;
            }
            if( isDescription(att) && description==null){
                description=att.getName().getLocalPart();
                continue;
            }
            notMapped.add(att);
        }
        
        for( AttributeDescriptor type : notMapped ) {
            if( extensionId==null && isStringType(type) ){
                extensionId=type.getName().getLocalPart();
                continue;
            }
            if( id==null && isStringType(type) ){
                id=type.getName().getLocalPart();
                continue;
            }
            if( groupId==null && isStringType(type) ){
                groupId=type.getName().getLocalPart();
                continue;
            }
            if( resolution==null && isStringType(type) ){
                resolution=type.getName().getLocalPart();
                continue;
            }
            if( priority==null && isStringType(type) ){
                priority=type.getName().getLocalPart();
                continue;
            }
            if( description==null && isStringType(type) ){
                description=type.getName().getLocalPart();
                continue;
            }
            if( viewMemento==null && isStringType(type) ){
                viewMemento=type.getName().getLocalPart();
                continue;
            }
            if( memento==null && isStringType(type) ){
                memento=type.getName().getLocalPart();
                continue;
            }
                
        }
    }
    
    private static SimpleFeatureType createOptimalSchema(String featureTypeName) throws SchemaException {
    	SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    	builder.setName(featureTypeName);
        builder.crs(DefaultGeographicCRS.WGS84).add("bounds", MultiPolygon.class);
        builder.length(128).nillable(true).add("description", String.class);
        builder.length(80).nillable(false).defaultValue("").add("extensionId", String.class);
        builder.length(80).nillable(true).add("groupId", String.class);
        builder.length(80).nillable(false).add("id", String.class);
        builder.length(1024).nillable(false).defaultValue("").add("memento", String.class);
        builder.length(20).nillable(true).add("priority", String.class);
        builder.length(20).nillable(true).add("resolution", String.class);
        builder.length(1024).nillable(true).add("viewMemento", String.class);
        return builder.buildFeatureType();
    }


    @SuppressWarnings("unchecked")
    private boolean isStringType( AttributeDescriptor att ) {
        return att.getType().getBinding().isAssignableFrom(String.class);
    }
    
    private boolean isDescription( AttributeDescriptor att ) {
        return compare(att.getName(), Messages.FeatureTypeAttributeMapper_description)&&isStringType(att); 
    }


    private boolean isPriority( AttributeDescriptor att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_priority)&&isStringType(att);  
    }

    private boolean isResolution( AttributeDescriptor att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_resolution)&&isStringType(att);  
    }

    private boolean isId( AttributeDescriptor att ) {
        return ((compare(att.getName(),Messages.FeatureTypeAttributeMapper_id)) || compare(att.getName(), Messages.FeatureTypeAttributeMapper_issue))&&isStringType(att);  
    }

    private boolean isGroupId( AttributeDescriptor att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_group)&&isStringType(att);  
    }

    private boolean isMemento( AttributeDescriptor att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_memento)&&isStringType(att); 
    }

    private boolean isViewMemento( AttributeDescriptor att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_viewmemento)&&isStringType(att); 
    }

    private boolean isExtensionID( AttributeDescriptor att ) {
        return compare(att.getName(),Messages.FeatureTypeAttributeMapper_extensionPoint)&&isStringType(att); 
    }
    
    private boolean compare( Name name, String arg2){
        return name.getLocalPart().toUpperCase().contains(arg2.toUpperCase());
    }

    public SimpleFeatureType getSchema() {
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
