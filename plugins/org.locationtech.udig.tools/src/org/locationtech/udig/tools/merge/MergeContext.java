/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.merge;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ui.commands.SelectionBoxCommand;
import org.locationtech.udig.project.ui.tool.IToolContext;

import org.locationtech.jts.geom.Envelope;

import org.locationtech.udig.tools.merge.internal.view.MergeView;

/**
 * Stores the status values of merge interactions.
 * 
 * The inputs for the merge command are grabbed using different user interface techniques like
 * feature selection by bbox drawing and merge feature definition using {@link MergeView}. Thus,
 * this context object provides a site where the merge command's parameters are stored.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @author Marco Foi (www.mcfoi.it)
 */
public class MergeContext {

    private static final MergeContext THIS = new MergeContext();

    /**
     * maintains the interaction selected by the user for merging features 	
     */
	public static final int MERGEMODE_TOOL = 1;
    public static final int MERGEMODE_OPERATION = 2;
    private int mergeMode; 


    private Point bboxStartPoint = null;

    private SelectionBoxCommand selectionBoxCommand = new SelectionBoxCommand();

    private List<Envelope> boundList = new ArrayList<Envelope>();

    private MergeView mergeView = null;

    private IToolContext toolContext = null;

    private List<SimpleFeature> preSelectedFeatures = Collections.emptyList();
    private ILayer preSelectedLayer = null;

    /**
     * Singleton use the getInstance methods
     */
    private MergeContext() {
        // singleton
    }

    /**
     * Singleton
     * 
     * @return return the instance of {@link MergeContext}
     * 
     */
    public static MergeContext getInstance() {
        return THIS;
    }

    public IToolContext getToolContext() {
        return toolContext;
    }

    public void setToolContext(IToolContext toolContext) {
        this.toolContext = toolContext;
    }

    /**
     * Reinitializes the status context
     */
    public void initContext() {

        bboxStartPoint = null;
        selectionBoxCommand = new SelectionBoxCommand();

        mergeView = null;
        toolContext = null;

        boundList.clear();
    }

    /**
     * Set the start point of the bbox.
     * 
     * @param point
     */
    public synchronized void setBBoxStartPoint(Point point) {

        assert point != null;

        this.bboxStartPoint = point;
    }

    /**
     * Returns the start point of the bbox.
     * 
     * @return the left upper corner
     */
    public synchronized Point getBBoxStartPoint() {

        return this.bboxStartPoint;
    }

    /**
     * Returns the instance of {@link SelectionBoxCommand} maintained in this context.
     * 
     * @return {@link SelectionBoxCommand}
     */
    public SelectionBoxCommand getSelectionBoxCommand() {

        return selectionBoxCommand;
    }

    /**
     * Add an bound to the envelope list.
     * 
     * @param bound
     */
    public void addBound(Envelope bound) {

        assert bound != null;

        boundList.add(bound);
    }

    /**
     * Removes the indeed bound from the list of bounds.
     * 
     * @param bound
     */
    public void removeBound(Envelope bound) {

        assert bound != null;

        boundList.remove(bound);
    }

    /**
     * @return the list of bounds
     */
    public List<Envelope> getBoundList() {

        return boundList;
    }

    /**
     * 
     * @return the associated merge view
     */
    public MergeView getMergeView() {
        return mergeView;
    }

    /**
     * 
     * @return true If a merge view is opened, false in other case
     */
    public boolean isMergeViewActive() {

        return (mergeView != null) && !mergeView.isDisposed();
    }

    /**
	 * 
	 */
    public void disposeMergeView() {

        this.mergeView = null;
    }

    /**
     * Set the associated merge view
     */
    public void activeMergeView(MergeView view) {
        this.mergeView = view;

    }

    /**
     * Set the mode in which the tool operates: Used by MergeOperatio.op Used by MergeTool.
     * 
     * @param mode
     */
    public void setMergeMode(int mode) {
        mergeMode = mode;
    }

    /**
     * Return the mode in which the tool is operating Tool mode = 1 (selection by Merge Tool)
     * Operation mode = 2 (with listeners on layers)
     * 
     * @return
     */
    public  int getMergeMode() {
        return mergeMode;
    }

    /**
     * Add pre-selected features to MergeContext class It is used by MergeOperation to store
     * features eventually pre-selected by the user before issuing the UI "Operation -> Merge"
     * command. These features are added to MergeView on opening when running in 'operation mode'
     * 
     * @param preSelectedFeatures
     */
    public void addPreselectedFeatures(List<SimpleFeature> preSelectedFeatures, ILayer preSelectedLayer) {
        this.preSelectedFeatures = preSelectedFeatures;
        this.preSelectedLayer = preSelectedLayer;
    }

    /**
     * Returns pre selected features
     * 
     * @return List<SimpleFeature>. Can be null if no features have been preselected on
     *         MergeOperation launch or if pre-selected features have been cleared after once-only
     *         addition to MergeView
     */
    public List<SimpleFeature> getPreselectedFeatures() {
        return preSelectedFeatures;
    }
    
    public ILayer getPreSelectedLayer() {
        return preSelectedLayer;
    }

    /**
     * Clear list of pre-selected features These features are added by MergeOperatio upon tool
     * activation and are cleared once added to MergeView
     */
    public void clearPreselectedFeatures() {
        preSelectedFeatures.clear();
    }
}
