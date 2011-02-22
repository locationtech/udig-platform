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
package net.refractions.udig.tools.edit.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.core.internal.GeometryBuilder;
import net.refractions.udig.tools.edit.EditPlugin;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.shapefile.shp.JTSUtilities;
import org.geotools.feature.GeometryAttributeType;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.algorithm.RobustCGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Some helper methods for creating geometries.
 *
 * @author jones
 * @since 1.1.0
 */
public class GeometryCreationUtil {

    public static class Bag{
        public EditGeom geom;
        public List<Geometry> jts=new ArrayList<Geometry>();
        public Bag(EditGeom geom){
            this.geom=geom;
        }
    }

    /**
     * Searches an EditBlackboard and creates 1 geometry for each EditGeom in the
     * Blackboard.  Only simple geoms are created.  IE no MultiPolygons.  That processing
     * must come later.
     *
     * The way the type of geometry is determined is as follows:
     * <ul>
     * <li>If the geometry has the same FID as the currentGeom then its type will be of type geomToCreate.</li>
     * <li>Otherwise the ShapeType of the EditGeom will be compared to the geomAttribute and if that type is legal for that attribute then that is the type of geometry that will be created</li>
     * <li>If ShapeType is UNKOWN and it does not have the same FID as the curentGeom then the geomAttribute is used to determine the geometry type.</li>
     * <li>Finally if the geomType is geometry then a Polygon will be created... unless the endpoints are not the same.  In that case a Line will be created.  Unless it is a single point.</li>
     *</ul>
     *
     * @param currentGeom the shape that will be created as a geomToCreate type.
     * @param geomToCreate the type of geometry that will be created for the currentShape in the handler.  Must be one of Point, LineString, LinearRing or Polygon.
     * @param geomAttribute The AttributeType of the geometry attribute that will be assist in determining the type of geometry created.
     * @return a mapping between the FeatureID of a bag of geoms that map to that id.  This mapping
     * can be used to later create complex geometries such as multigeoms.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, GeometryCreationUtil.Bag> createAllGeoms( EditGeom currentGeom, Class geomToCreate, GeometryAttributeType geomAttribute) {
        EditBlackboard blackboard=currentGeom.getEditBlackboard();
         List<EditGeom> editGeoms = blackboard.getGeoms();
        Map<String, GeometryCreationUtil.Bag> idToGeom=new HashMap<String, GeometryCreationUtil.Bag>();
        for( EditGeom editGeom : editGeoms ) {
            if( editGeom.isChanged() ){
                if( !idToGeom.containsKey(editGeom.getFeatureIDRef().get()) )
                    idToGeom.put(editGeom.getFeatureIDRef().get(), new GeometryCreationUtil.Bag(editGeom));
            }
        }
        for( EditGeom editGeom : editGeoms ) {
            if( !idToGeom.containsKey(editGeom.getFeatureIDRef().get()) )
                continue;

            if( idToGeom.containsKey(editGeom.getFeatureIDRef().get()) ){
                if( editGeom.getShell().getNumPoints()>0 )
                    idToGeom.get(editGeom.getFeatureIDRef().get()).jts.add(createGeom(determineGeometryType(currentGeom, editGeom, geomToCreate, geomAttribute), editGeom.getShell(), true));

            }
        }
        return idToGeom;
    }

    /**
     *  The way the type of geometry is determined is as follows:
     * <ul>
     * <li>If the geometry has the same FID as the currentGeom then its type will be of type geomToCreate.</li>
     * <li>Otherwise the ShapeType of the EditGeom will be compared to the geomAttribute and if that type is legal for that attribute then that is the type of geometry that will be created</li>
     * <li>If ShapeType is UNKOWN and it does not have the same FID as the curentGeom then the geomAttribute is used to determine the geometry type.</li>
     * <li>Finally if the geomType is geometry then a Polygon will be created... unless the endpoints are not the same.  In that case a Line will be created.  Unless it is a single point.</li>
     *</ul>
     *
     * @param currentGeom the handler's current Geom.  If == editGeom then geomToCreate will be returned
     * @param editGeom the editGeom that will be used to create a geometry.
     * @param geomToCreate A default value to use if nothing else can be decided.
     * @param geomAttribute The geometry attribute type of the feature that will be set using the created geometry.
     * @return
     */
    public static Class determineGeometryType( EditGeom currentGeom, EditGeom editGeom, Class geomToCreate, GeometryAttributeType geomAttribute ) {
        if ( (editGeom.getFeatureIDRef().get()==null && currentGeom.getFeatureIDRef().get()==null )
              || editGeom.getFeatureIDRef().get().equals(currentGeom.getFeatureIDRef().get()) )
            return geomToCreate;
        ShapeType attributeDefinedType = ShapeType.valueOf( geomAttribute.getType());
        ShapeType typeToSwitchOn;
        if( attributeDefinedType!=editGeom.getShapeType()
                && attributeDefinedType!=ShapeType.UNKNOWN
                || editGeom.getShapeType()==ShapeType.UNKNOWN){
            typeToSwitchOn=attributeDefinedType;
        }else{
            typeToSwitchOn=editGeom.getShapeType();
        }

        switch( typeToSwitchOn ) {
        case LINE:
            return LineString.class;
        case POINT:
            return Point.class;
        case POLYGON:
            return Polygon.class;
        case UNKNOWN:
            if( geomAttribute.getType()==Geometry.class ||
                    geomAttribute.getType()==GeometryCollection.class){
                PrimitiveShape shell = editGeom.getShell();
                if( shell.getNumPoints()==1)
                    return Point.class;
                else if( shell.getPoint(0).equals(shell.getPoint(shell.getNumPoints()-1))
                        && shell.getNumCoords()!=2)
                    return Polygon.class;
                else
                    return LineString.class;
            }
        }
        return null;
    }

    /**
     * Creates a geometry for a primitive shape.
     *
     * @param geomToCreate the type of geometry to create.  Must be one of Point, LineString, LinearRing or Polygon.
     * @param shape the shape to use.  If type is Polygon the shapes EditGeom is used.
     * @param showError Will show the user a warning if there is a validation error
     * @return A geometry of type geomToCreate
     */
    public static <T extends Geometry> T createGeom( Class<T> geomToCreate, PrimitiveShape shape, boolean showError ) {
     Geometry geom;
        if (geomToCreate == Polygon.class) {
            geom=createPolygon(shape.getEditGeom());
        } else if (geomToCreate == LinearRing.class) {
            geom=GeometryBuilder.create().safeCreateGeometry(LinearRing.class, shape.coordArray());
        } else if (geomToCreate == LineString.class) {
            geom=GeometryBuilder.create().safeCreateGeometry(LineString.class, shape.coordArray());
        } else {
            geom=GeometryBuilder.create().safeCreateGeometry(Point.class, shape.coordArray());
        }

        if( !geom.isValid()){
        	final Display display = Display.getDefault();
        	if( showError ){
	        	display.asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(display.getActiveShell(), "Internal Error", "Apologies an internal error has occurred.  In order to preserve data integrity the last edit sequence is going to be discarded.  Please try again");
					}
				});
        	}
        	throw new IllegalStateException("Geometry constructed from EditGeom: "+shape.getEditGeom().getFeatureIDRef().get()+" resulted in an invalid geometry");
        }
        return geomToCreate.cast(geom);
    }

    public final static RobustCGAlgorithms cga = new RobustCGAlgorithms();

    /**
     * Create JTS Geometry from the provided EditGeom.
     *
     * @param currentGeom
     * @return Polygon
     */
    public static Polygon createPolygon(EditGeom currentGeom) {
		Coordinate[] shellCoords = currentGeom.getShell().coordArray();
		LinearRing shell = GeometryBuilder.create().safeCreateLinearRing(shellCoords);

		try {
			if (CGAlgorithms.isCCW(shellCoords)){
				shell = JTSUtilities.reverseRing(shell);
			}
		} catch (Exception e) {
			EditPlugin.log("Not a critical problem, just an FYI", e); //$NON-NLS-1$
		}
		LinearRing[] holes = new LinearRing[currentGeom.getHoles().size()];
		int i = 0;
		for (PrimitiveShape shape : currentGeom.getHoles()) {
			Coordinate[] coordArray = shape.coordArray();
			if (coordArray.length == 0){
				continue;
			}
			LinearRing hole = GeometryBuilder.create().safeCreateLinearRing(coordArray);
			if (!CGAlgorithms.isCCW(coordArray)) {
				hole = JTSUtilities.reverseRing((LinearRing) hole);
			}
			holes[i] = hole;
			i++;
		}
		GeometryFactory factory = new GeometryFactory();
		return factory.createPolygon(shell, holes);
	}

    /**
	 * Creates a GeometryCollection if the schemaDeclaredType is a subclass of
	 * GeometryCollection. Otherwise it will just return the first of the list,
	 * or null if we are removing content.
	 *
	 * @param geoms
	 *            list of goemtries that will be added to the
	 *            GeometryCollection.
	 * @param schemaDeclaredType
	 *            The type that the resulting geometry has to be compatible with
	 * @return geometry can be assigned to schemaDeclaredType, or null geoms is
	 *         empty
	 */
    public static Geometry ceateGeometryCollection( List<Geometry> geoms, Class<Geometry> schemaDeclaredType) {

        if( geoms.isEmpty() ){
            return null;
        }

        if( schemaDeclaredType.isAssignableFrom(geoms.get(0).getClass()) && geoms.size()==1)
            return geoms.get(0);

        if( !GeometryCollection.class.isAssignableFrom(schemaDeclaredType)
                && !schemaDeclaredType.isAssignableFrom(GeometryCollection.class))
            return geoms.get(0);

        Geometry geom;
        GeometryFactory factory=new GeometryFactory( );
        Class< ? extends Geometry> geomType = geoms.get(0).getClass();
        if (geomType == Polygon.class) {
            geom=factory.createMultiPolygon(geoms.toArray(new Polygon[geoms.size()]));
        } else if (geomType == LinearRing.class) {
            geom=factory.createMultiLineString(geoms.toArray(new LineString[geoms.size()]));
        } else if (geomType == LineString.class) {
            geom=factory.createMultiLineString(geoms.toArray(new LineString[geoms.size()]));
        } else {
            geom=factory.createMultiPoint(geoms.toArray(new Point[geoms.size()]));
        }
        return geom;
    }

}
