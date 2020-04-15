/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.behaviour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.commands.DeselectEditGeomCommand;
import org.locationtech.udig.tools.edit.commands.DeselectionStrategy;
import org.locationtech.udig.tools.edit.commands.SelectFeaturesAtPointCommand;
import org.locationtech.udig.tools.edit.commands.SelectionParameter;
import org.locationtech.udig.tools.edit.commands.SelectionStrategy;
import org.locationtech.udig.tools.edit.commands.SetCurrentGeomCommand;
import org.locationtech.udig.tools.edit.commands.SetEditFeatureCommand;
import org.locationtech.udig.tools.edit.commands.selection.SelectFeatureStrategy;
import org.locationtech.udig.tools.edit.commands.selection.WriteModificationsStartEditingStrategy;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.ClosestEdge;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.EditUtils;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.PrimitiveShapeIterator;
import org.locationtech.udig.tools.edit.support.ShapeType;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.spatial.Intersects;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Behaviour to queue up a couple of SelectFeatures commands based on a provided mouse click.
 * <p>
 * Requirements:
 * <ul>
 * <li>state==MODIFYING or NONE</li>
 * <li>event type == RELEASED</li>
 * <li>only a single modifier may be down</li>
 * <li>shift and MOD1 are only legal modifiers</li>
 * <li>button1 must be the button that was released</li>
 * <li>no buttons may be down</li>
 * <li>Not over currently selected geometry</li>
 * </ul>
 * </p>
 * <p>
 * Action:
 * <ul>
 * <li>If no currently selected geometry && mouse is over feature then:
 * <ul>
 * <li>If the feature's geometry isn't of the type specified in constructor return or:</li>
 * <li>Clear Editblackboard and set feature's geometry on blackboard.</li>
 * <li>set the current geom to be the geometry selected</li>
 * <li>set the current Edit feature to be selected feature</li>
 * <li>set mode to be Modified?</li>
 * </ul>
 * </li>
 * <li>If there is a currently selected geometry && mouse is over a different feature:
 * <ul>
 * <li>If no modifiers select feature as described above</li>
 * <li>if control is down add the feature to the Editblackboard or remove it if it is already on
 * the EditBlackboard</li>
 * <li>if shift is down add the feature to the Editblackboard or do nothing if it is already on the
 * EditBlackboard</li>
 * </ul>
 * <li>If there is a currently selected geometry && mouse is not over a different feature:
 * <ul>
 * <li>Run acceptBehaviours</li>
 * <li>deselect EditFeature</li>
 * <li>deselect current EditGeom</li>
 * <li>clear Editblackboard</li>
 * <li>set current state to NONE</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * 
 * @author jones
 * @since 1.1.0
 */
public class SelectFeatureBehaviour implements EventBehaviour {

    private final Set<Class<? extends Geometry>> acceptableClasses = new HashSet<Class<? extends Geometry>>();
    private boolean treatUnknownGeomsAsPolygon;
    private Class<? extends Filter> filterType;
    private boolean permitClear;
    private boolean onlyAdd;
    public final List<SelectionStrategy> selectionStrategies = new LinkedList<SelectionStrategy>();
    public final List<DeselectionStrategy> deselectionStrategies = new LinkedList<DeselectionStrategy>();
    
    /**
     * Create instance
     * 
     * @param acceptableClasses used to determine if a feature can be selected. If point is not in
     *        array then point geometries can not be selected.
     * @param class2 Something like Intersects.class
     */
    @SuppressWarnings("unchecked")
    public SelectFeatureBehaviour( Class<? extends Geometry>[] acceptableClasses, Class<? extends Filter> class2 ) {
        setAcceptableClasses(acceptableClasses);
        this.treatUnknownGeomsAsPolygon = false;
        for( Class<? extends Geometry> class1 : acceptableClasses ) {
            if (class1.isAssignableFrom(Polygon.class)
                    || class1.isAssignableFrom(MultiPolygon.class)) {
                treatUnknownGeomsAsPolygon = true;
                break;
            }
        }
        this.filterType = class2;
        onlyAdd=false;
        permitClear=true;
        initDefaultStrategies(null); 
    }

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean legalState = handler.getCurrentState() == EditState.NONE
                || handler.getCurrentState() == EditState.MODIFYING;
        boolean releaseButtonState = eventType == EventType.RELEASED;
        boolean twoModifiersDown = e.isShiftDown() && e.isModifierDown(MapMouseEvent.MOD1_DOWN_MASK);
        boolean singleModifierDown = !(twoModifiersDown);
        boolean altUp = !e.isAltDown();
        boolean legalButton = e.button == MapMouseEvent.BUTTON1;
        boolean noPressedButtons = e.buttons == MapMouseEvent.NONE;
        if (!(legalState && releaseButtonState && altUp && singleModifierDown && legalButton && noPressedButtons))
            return false;

        if (e.isModifierDown(MapMouseEvent.MOD1_DOWN_MASK))
            return true;

        if (handler.getCurrentGeom() == null)
            return true;
        
        if (!handler.getCurrentShape().contains(Point.valueOf(e.x, e.y), treatUnknownGeomsAsPolygon))
            return true;

        return countOnBlackboard(handler, e) > 1; //click was within the current blackboard selection
    }

    public UndoableMapCommand getCommand( final EditToolHandler handler, final MapMouseEvent e,
            EventType eventType ) {
        if (!isValid(handler, e, eventType)) {
            throw new IllegalArgumentException("Behaviour is not valid for the current state"); //$NON-NLS-1$
        }
        
        
        EditBlackboard editBlackboard = handler.getEditBlackboard(handler.getEditLayer());
        List<EditGeom> intersectingGeoms = EditUtils.instance.getIntersectingGeom(editBlackboard,
                Point.valueOf(e.x, e.y), treatUnknownGeomsAsPolygon);
        
        if (e.isModifierDown(MapMouseEvent.MOD1_DOWN_MASK) && !intersectingGeoms.isEmpty()) {
            return new DeselectEditGeomCommand(handler, intersectingGeoms);
        } else if (e.isShiftDown() && !intersectingGeoms.isEmpty()) {
            return null;
        }
        // Check to see if shape is already on the blackboard
        PrimitiveShape newShape = findOnBlackboard(handler, e);

        if (newShape != null && newShape != handler.getCurrentShape()) {
            List<UndoableMapCommand> commands = new ArrayList<UndoableMapCommand>();
            commands.add(new SetCurrentGeomCommand(handler, newShape));
            commands.add(new SetEditFeatureCommand(handler, Point.valueOf(e.x, e.y), newShape));
            UndoableComposite undoableComposite = new UndoableComposite(commands);
            return undoableComposite;
        }

        SelectionParameter selectionParameter = new SelectionParameter(handler, e, getAcceptableClasses(), filterType, permitClear, onlyAdd);
        selectionParameter.selectionStrategies.addAll(selectionStrategies);
        selectionParameter.deselectionStrategies.addAll(deselectionStrategies);
        
        SelectFeaturesAtPointCommand selectGeometryCommand = new SelectFeaturesAtPointCommand(selectionParameter);
        return selectGeometryCommand;
    }
    /** Find the PrimitiveShape on the blackboard under the mouse event */
    private PrimitiveShape findOnBlackboard( EditToolHandler handler, MapMouseEvent e ) {
        //for overlapping geometries, select a different one on each click
        boolean cycleGeom = false;
        //set when the currently selected feature has been found
        boolean selectedFound = false;
        //for returning to the first match when we have the last match selected and click once more
        PrimitiveShape firstMatch = null; 
        if (handler.getCurrentShape() != null && handler.getCurrentShape().contains(e.x, e.y) && countOnBlackboard(handler, e) > 1) {
            cycleGeom = true;
        }
            
        ILayer editLayer = handler.getEditLayer();
        List<EditGeom> geoms = handler.getEditBlackboard(editLayer).getGeoms();
        for( EditGeom geom : geoms ) {
            PrimitiveShapeIterator iter=PrimitiveShapeIterator.getPathIterator(geom.getShell());
            if( iter.toShape().contains(e.x,e.y) ){
                if (cycleGeom) {
                    if (selectedFound) { //first match after the currently selected one
                        return geom.getShell();
                    }
                    if (geom == handler.getCurrentGeom()) { //currently selected geom
                        selectedFound = true;
                    }
                    if (firstMatch == null) { //first matching geom
                        firstMatch = geom.getShell();
                    }
                } else {
                    return geom.getShell();
                }
            }
            if (!cycleGeom) {
                SimpleFeatureType featureType = editLayer.getSchema();
                GeometryDescriptor defaultGeometryType = featureType.getGeometryDescriptor();
                Class< ? > type = defaultGeometryType.getType().getBinding();
                boolean polygonLayer=Polygon.class.isAssignableFrom(type) || MultiPolygon.class.isAssignableFrom(type);
                ClosestEdge edge = geom.getShell().getClosestEdge(Point.valueOf(e.x, e.y), polygonLayer);
                if (edge != null && 
                        edge.getDistanceToEdge() <= PreferenceUtil.instance().getVertexRadius()){
                    return geom.getShell();
                }
            }
        }
        if (cycleGeom) //selected geom was the last one, therefore select the first match
            return firstMatch;
        else
            return null;
    }

    private int countOnBlackboard( EditToolHandler handler, MapMouseEvent e ) {
        List<EditGeom> geoms = handler.getEditBlackboard(handler.getEditLayer()).getGeoms();
        int count = 0;
        for( EditGeom geom : geoms ) {
            PrimitiveShapeIterator iter=PrimitiveShapeIterator.getPathIterator(geom.getShell());
            if( iter.toShape().contains(e.x,e.y) ){
                count++;
            }
        }
        return count;
    }
    
    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    /**
     * @return Returns the acceptableClasses.
     */
    @SuppressWarnings("unchecked")
    public Class<? extends Geometry>[] getAcceptableClasses() {
        Class< ? extends Geometry>[] array = new Class[acceptableClasses.size()];
        return acceptableClasses.toArray(array );
    }

    /**
     * @param acceptableClasses The acceptableClasses to set.
     */
    public void setAcceptableClasses( Class<? extends Geometry>[] acceptableClasses ) {
        this.acceptableClasses.clear();
        this.acceptableClasses.addAll(Arrays.asList(acceptableClasses));
    }

    /**
     * @return Returns the filterType.
     */
    public Class<? extends Filter> getFilterType() {
        return this.filterType;
    }

    /**
     * @param filterType The filterType to set.
     */
    public void setFilterType( Class<? extends Filter> filterType ) {
        this.filterType = filterType;
    }

    /**
     * @return Returns the onlyAdd.
     */
    public boolean isOnlyAdd() {
        return this.onlyAdd;
    }

    /**
     * @param onlyAdd The onlyAdd to set.
     */
    public void setOnlyAdd( boolean onlyAdd ) {
        this.onlyAdd = onlyAdd;
    }

    /**
     * @return Returns the permitClear.
     */
    public boolean isPermitClear() {
        return this.permitClear;
    }

    /**
     * @param permitClear The permitClear to set.
     */
    public void setPermitClear( boolean permitClear ) {
        this.permitClear = permitClear;
    }

    /**
     * @return Returns the treatUnkownGeomsAsPolygon.
     */
    public boolean isTreatUnknownGeomsAsPolygon() {
        return this.treatUnknownGeomsAsPolygon;
    }

    /**
     * @param treatUnknownGeomsAsPolygon The treatUnknownGeomsAsPolygon to set.
     */
    public void setTreatUnknownGeomsAsPolygon( boolean treatUnknownGeomsAsPolygon ) {
        this.treatUnknownGeomsAsPolygon = treatUnknownGeomsAsPolygon;
    }



    /**
     * Adds a DeselectionStrategy to the strategies that are ran when a selection does not intersect with any features (only counts for
     * features not already on the edit blackboard)
     *
     * @param strategy the strategy to add
     */
    public void addDeselectionStrategy(DeselectionStrategy strategy){
        deselectionStrategies.add(strategy);
    }
    
    /**
     * Adds a DeselectionStrategy to the strategies that are ran when a selection intersects with features (only counts for
     * features not already on the edit blackboard)
     *
     * @param strategy the strategy to add
     */
    public void addSelectionStrategy(SelectionStrategy strategy){
        selectionStrategies.add(strategy);
    }
    
    /**
     * Clears the configuration and Adds the default strategies
     * @param typeToCreate the type of geometry to begin creating if a click does not intersect with anything.  If null then
     * the click will only clear the current selection.
     */
    public void initDefaultStrategies(ShapeType typeToCreate){
        selectionStrategies.add(new SelectFeatureStrategy() );
        deselectionStrategies.add(new WriteModificationsStartEditingStrategy(typeToCreate));
    }
}
