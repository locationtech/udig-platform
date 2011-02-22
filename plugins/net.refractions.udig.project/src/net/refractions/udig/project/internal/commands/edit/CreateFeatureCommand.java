/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.commands.edit;

import java.util.Iterator;

import net.refractions.udig.core.internal.GeometryBuilder;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.CodeList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Creates a new feature in the current edit layer.
 *
 * @author jones
 * @since 0.3
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
        if( coordinates!=null )
            i=coordinates.length;
        Coordinate[] c=new Coordinate[i];
        if( coordinates!=null )
            System.arraycopy(coordinates, 0, c, 0, c.length);
        this.coordinates = c;
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#run()
     */
    @SuppressWarnings("unchecked")
    public void run( IProgressMonitor monitor ) throws Exception {
        ILayer editLayer = getMap().getEditManager().getEditLayer();

        editLayer = findEditLayer();

        if (editLayer == null) {
            MessageDialog
                    .openError(
                            Display.getDefault().getActiveShell(),
                            Messages.CreateFeatureCommand_error_title, Messages.CreateFeatureCommand_error_message);
            return;
        }
        FeatureStore store = editLayer.getResource(FeatureStore.class, null);
        transform();
        if (store.getTransaction() == Transaction.AUTO_COMMIT)
            throw new Exception("Error transaction has not been started"); //$NON-NLS-1$
        final FeatureType type = store.getSchema();
        Object[] attrs = new Object[type.getAttributeCount()];
        for( int i = 0; i < attrs.length; i++ ) {
            attrs[i] = setDefaultValue(type.getAttributeType(i));
        }
        final Feature newFeature = type.create(attrs);
        Class geomType = type.getDefaultGeometry().getType();

        Geometry geom = GeometryBuilder.create().safeCreateGeometry(geomType, coordinates);
        newFeature.setDefaultGeometry(geom);

        fid = newFeature.getID();
        map.getEditManagerInternal().addFeature(newFeature, (Layer) editLayer);
    }

    /**
     * @param object
     * @param object2
     */
    private Object setDefaultValue( AttributeType type ) {
        if (type.createDefaultValue() != null)
            return type.createDefaultValue();
        if (Boolean.class.isAssignableFrom(type.getType())
                || boolean.class.isAssignableFrom(type.getType()))
            return Boolean.FALSE;
        if (String.class.isAssignableFrom(type.getType()))
            return ""; //$NON-NLS-1$
        if (Integer.class.isAssignableFrom(type.getType()))
            return Integer.valueOf(0);
        if (Double.class.isAssignableFrom(type.getType()))
            return  Double.valueOf(0);
        if (Float.class.isAssignableFrom(type.getType()))
            return Float.valueOf(0);
        if (CodeList.class.isAssignableFrom(type.getType())) {
            return type.createDefaultValue();
        }
        return null;
    }

    /**
     * TODO summary sentence for findEditLayer ...
     */
    private Layer findEditLayer() {
        Layer layer = null;
        if (map.getEditManagerInternal().getEditLayerInternal() != null)
            return map.getEditManagerInternal().getEditLayerInternal();
        for( Iterator iter = map.getContextModel().getLayers().iterator(); iter.hasNext(); ) {
            layer = (Layer) iter.next();
            if (layer.isType(FeatureStore.class) && layer.isSelectable() && layer.isVisible())
                break;
        }
        return layer;
    }

    /**
     * Transforms coordinates into the layer CRS if nessecary
     *
     * @throws Exception
     */
    private void transform() throws Exception {
        ILayer editLayer = getMap().getEditManager().getEditLayer();
        if (map.getViewportModel().getCRS().equals(editLayer.getCRS(null)))
            return;
        MathTransform mt = CRS.transform(map.getViewportModel().getCRS(), editLayer.getCRS(null),
                true);
        if (mt == null || mt.isIdentity())
            return;
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
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.CreateFeatureCommand_createFeature;
    }

    /**
     * @see net.refractions.udig.project.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {
        ILayer editLayer = getMap().getEditManager().getEditLayer();
        editLayer.getResource(FeatureStore.class, null).removeFeatures(
                FilterFactoryFinder.createFilterFactory().createFidFilter(fid));
    }

}
