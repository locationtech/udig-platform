/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands.edit;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.command.provider.EditFeatureProvider;
import org.locationtech.udig.project.command.provider.EditLayerProvider;
import org.locationtech.udig.project.internal.Messages;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;

/**
 * This command modifies an attribute of the current editFeature(the victim that is currently
 * editable).
 * 
 * @author jeichar
 * @since 0.3
 */
public class SetAttributesCommand extends AbstractEditCommand implements UndoableMapCommand {
    protected String xpath[];
    protected final Object value[];

    private Object oldValue[];

    private final IBlockingProvider<SimpleFeature> editFeature;

    protected final IBlockingProvider<ILayer> editLayer;

    /**
     * Creates a new instance of SetAttributeCommand.
     * 
     * @param feature the feature to modify
     * @param layer to ask for object
     * @param xpath the xpath that identifies an attribute in the current edit feature.
     * @param value the value that will replace the old attribute value.
     */
    public SetAttributesCommand(IBlockingProvider<SimpleFeature> feature,
            IBlockingProvider<ILayer> layer, String xpath[], Object value[]) {
        Validate.notNull(xpath);
        Validate.notNull(value);
        Validate.isTrue(xpath.length == value.length, "xpath and values do not have same lenght");
        this.xpath = xpath;
        this.value = value;
        this.oldValue = new Object[xpath.length];
        editFeature = feature;
        editLayer = layer;
    }

    /**
     * Creates a new instance of SetAttributeCommand.
     * 
     * @param feature the feature to modify
     * @param xpath the xpath that identifies an attribute in the current edit feature.
     * @param value the value that will replace the old attribute value.
     */
    public SetAttributesCommand(String xpath[], Object value[]) {
        Validate.notNull(xpath);
        Validate.notNull(value);
        Validate.isTrue(xpath.length == value.length, "xpath and values do not have same lenght");
        editFeature = new EditFeatureProvider(this);
        editLayer = new EditLayerProvider(this);
        this.oldValue = new Object[xpath.length];
        this.xpath = xpath;
        this.value = value;
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        ILayer layer = editLayer.get(monitor);
        if( layer==null ){
            System.err.println("class "+editLayer.getClass().getName()+" is returning null");  //$NON-NLS-1$//$NON-NLS-2$
            return;
        }
        FeatureStore<SimpleFeatureType, SimpleFeature> resource = layer.getResource(FeatureStore.class, null);
        SimpleFeature feature2 = editFeature.get(monitor);

        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
		Id fidFilter = filterFactory.id(
                FeatureUtils.stringToId(filterFactory,feature2.getID()));

		// for loop to get the old values
		for(int i = 0; i < xpath.length; i++){
		    oldValue[i] = feature2.getAttribute(xpath[i]);
		}
		
		// update the new values with another for loop
		for(int i = 0; i < xpath.length; i++){
		    feature2.setAttribute(xpath[i],value[i]);
		}
        
        List<Name> attributeList = new ArrayList<>();
        SimpleFeatureType schema = layer.getSchema();
        for( String name : xpath ){
            attributeList.add( schema.getDescriptor( name ).getName() );
        }
        Name[] array = attributeList.toArray( new Name[attributeList.size()]);
        resource.modifyFeatures(array, value, fidFilter);
    }

    /**
     * @see org.locationtech.udig.project.internal.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {
        SimpleFeature feature = editFeature.get(monitor);
        // need another for loop
        for ( int i = 0; i > xpath.length; i++){
            feature.setAttribute(xpath[i], oldValue[i]);
        }

        ILayer layer = editLayer.get(monitor);
        FeatureStore<SimpleFeatureType, SimpleFeature> resource = layer.getResource(FeatureStore.class, null);
        
        // need another for loop
        
        List<Name> attributeList = new ArrayList<Name>();
        SimpleFeatureType schema = layer.getSchema();
        for( String name : xpath ){
            attributeList.add( schema.getDescriptor( name ).getName());
        }
        Name[] array = attributeList.toArray( new Name[attributeList.size()]);
        
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Id id = filterFactory.id(FeatureUtils.stringToId(filterFactory, feature.getID()));
        resource.modifyFeatures(array, oldValue, id);
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return MessageFormat.format(
                Messages.SetAttributeCommand_setFeatureAttribute, new Object[]{xpath});
    }

    @Override
    public void setMap( IMap map ) {
        super.setMap(map);
    }
}
