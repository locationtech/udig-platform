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

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.core.internal.GeometryBuilder;
import org.locationtech.udig.project.AdaptableFeature;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.interceptor.FeatureInterceptor;
import org.locationtech.udig.project.interceptor.MapInterceptor;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.CodeList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

/**
 * Creates a new feature in the current edit layer.
 * 
 * @author jones
 * @since 0.3
 * @version 1.2
 */
public class CreateFeatureCommand extends AbstractEditCommand implements UndoableMapCommand {

    private Coordinate[] coordinates;

    String fid;

    /**
     * Construct <code>CreateFeatureCommand</code>.
     * 
     * @param coordinates Coordinates in Map coordinates.
     */
    public CreateFeatureCommand( Coordinate[] coordinates ) {
        int i = 0;
        if (coordinates != null) {
            i = coordinates.length;
        }
        Coordinate[] c = new Coordinate[i];
        if (coordinates != null) {
            System.arraycopy(coordinates, 0, c, 0, c.length);
        }
        this.coordinates = c;
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#run()
     */
    @SuppressWarnings("unchecked")
    public void run( IProgressMonitor monitor ) throws Exception {
        ILayer editLayer = getMap().getEditManager().getEditLayer();
        if (editLayer == null) {
            editLayer = findEditLayer();
        }
        if (editLayer == null) {
            MessageDialog.openError(Display.getDefault().getActiveShell(),
                    Messages.CreateFeatureCommand_error_title,
                    Messages.CreateFeatureCommand_error_message);
            return;
        }

        FeatureStore<SimpleFeatureType, SimpleFeature> store = editLayer.getResource(
                FeatureStore.class, null);
        transform();
        if (store.getTransaction() == Transaction.AUTO_COMMIT) {
            throw new Exception("Error transaction has not been started"); //$NON-NLS-1$
        }
        final SimpleFeatureType type = store.getSchema();

        // Object[] attrs = new Object[type.getAttributeCount()];
        // for( int i = 0; i < attrs.length; i++ ) {
        // attrs[i] = toDefaultValue(type.getDescriptor(i));
        // }
        //        
        // final SimpleFeature newFeature = SimpleFeatureBuilder.build(type, attrs, "newFeature"
        // + new Random().nextInt());

        String proposedFid = "newFeature"+"newFeature" + new Random().nextInt();
        final SimpleFeature newFeature = SimpleFeatureBuilder.template(type, proposedFid );

        Class geomType = type.getGeometryDescriptor().getType().getBinding();
        Geometry geom = GeometryBuilder.create().safeCreateGeometry(geomType, coordinates);
        newFeature.setDefaultGeometry(geom);
        fid = newFeature.getID();

        SimpleFeature feature = new AdaptableFeature(newFeature, editLayer);
        
        runFeatureCreationInterceptors(feature);

        map.getEditManagerInternal().addFeature(newFeature, (Layer) editLayer);
    }
    /**
     * Retrieves a default value for the provided descriptor.
     * <p>
     * The descriptor getDefaultValue() is used if available; if not a default value is created base
     * on the descriptor binding. The default values mirror those used by Java; empty string,
     * boolean false, 0 integer, 0.0 double, etc... >p>
     * 
     * @param type attribute descriptor
     */
    private Object toDefaultValue( AttributeDescriptor type ) {
        if (type.getDefaultValue() != null) {
            return type.getDefaultValue();
        }
        if (Boolean.class.isAssignableFrom(type.getType().getBinding())
                || boolean.class.isAssignableFrom(type.getType().getBinding())) {
            return Boolean.FALSE;
        }
        if (String.class.isAssignableFrom(type.getType().getBinding())) {
            return ""; //$NON-NLS-1$
        }
        if (Integer.class.isAssignableFrom(type.getType().getBinding())) {
            return Integer.valueOf(0);
        }
        if (Double.class.isAssignableFrom(type.getType().getBinding())) {
            return Double.valueOf(0);
        }
        if (Float.class.isAssignableFrom(type.getType().getBinding())) {
            return Float.valueOf(0);
        }
        if (CodeList.class.isAssignableFrom(type.getType().getBinding())) {
            return type.getDefaultValue();
        }
        return null;
    }

    /**
     * Go on a little walk and find the edit layer that we will be submitting this feature to.
     */
    private Layer findEditLayer() {
        Layer layer = null;
        if (map.getEditManagerInternal().getEditLayerInternal() != null) {
            return map.getEditManagerInternal().getEditLayerInternal();
        }
        for( Iterator<Layer> iter = map.getLayersInternal().iterator(); iter.hasNext(); ) {
            layer = iter.next();
            if (layer.hasResource(FeatureStore.class) && layer.isSelectable() && layer.isVisible()) {
                break;
            }
        }
        return layer;
    }

    /**
     * Transforms coordinates into the layer CRS if required
     * 
     * @throws Exception
     */
    private void transform() throws Exception {
        ILayer editLayer = getMap().getEditManager().getEditLayer();
        if (map.getViewportModel().getCRS().equals(editLayer.getCRS(null))) {
            return;
        }
        MathTransform mt = CRS.findMathTransform(map.getViewportModel().getCRS(), editLayer
                .getCRS(), true);
        if (mt == null || mt.isIdentity()) {
            return;
        }
        double[] coords = new double[coordinates.length * 2];
        for( int i = 0; i < coordinates.length; i++ ) {
            coords[i * 2] = coordinates[i].x;
            coords[i * 2 + 1] = coordinates[i].y;
        }
        mt.transform(coords, 0, coords, 0, coordinates.length);
        for( int i = 0; i < coordinates.length; i++ ) {
            coordinates[i].x = coords[i * 2];
            coordinates[i].y = coords[i * 2 + 1];
        }
    }

    public MapCommand copy() {
        return new CreateFeatureCommand(coordinates);
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.CreateFeatureCommand_createFeature;
    }

    /**
     * @see org.locationtech.udig.project.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {
        ILayer editLayer = getMap().getEditManager().getEditLayer();
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools
                .getDefaultHints());
        editLayer.getResource(FeatureStore.class, null).removeFeatures(
                filterFactory.id(FeatureUtils.stringToId(filterFactory, fid)));
    }
    public static void runFeatureCreationInterceptors( Feature feature ) {
        List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(FeatureInterceptor.EXTENSION_ID);
        for( IConfigurationElement element : interceptors ) {
            String id = element.getAttribute("id");
            if (FeatureInterceptor.CREATED_ID.equals(element.getName())) {
                try {
                    FeatureInterceptor interceptor = (FeatureInterceptor) element
                            .createExecutableExtension("class");
                    interceptor.run(feature);
                } catch (Exception e) {
                    ProjectPlugin.log("FeatureInterceptor " + id + ":" + e, e);
                }
            }
        }
    }
}
