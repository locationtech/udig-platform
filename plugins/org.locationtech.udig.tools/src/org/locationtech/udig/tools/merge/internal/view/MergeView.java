/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.merge.internal.view;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IMapCompositionListener;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.project.MapCompositionEvent;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.IUDIGView;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.tools.edit.animation.MessageBubble;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import org.locationtech.udig.tools.internal.i18n.Messages;
import org.locationtech.udig.tools.internal.mediator.AppGISAdapter;
import org.locationtech.udig.tools.internal.mediator.PlatformGISMediator;
import org.locationtech.udig.tools.internal.ui.util.StatusBar;
import org.locationtech.udig.tools.merge.MergeContext;
import org.locationtech.udig.tools.merge.Util;

/**
 * This view shows the features to merge.
 * 
 * <p>
 * The view allows to select the attributes of the source features that will be merge in the target
 * feature (the merge feature).
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @author Marco Foi (www.mcfoi.it)
 */
public class MergeView extends ViewPart implements IUDIGView {

    public static final String ID = "org.locationtech.udig.tools.merge.internal.view.MergeView"; //$NON-NLS-1$

    private MergeComposite mergeComposite = null;

    private CancelButtonAction cancelButton = null;

    private MergeButtonAction mergeButton = null;

    private MergeContext mergeContext = null;

    private String message;

    /** The merge operation is possible if this variable is sets in true value */
    private boolean canMerge;

    // listeners
    /** Map Listener used to catch the map changes */
    private IMapCompositionListener mapListener = null;

    /** Layer Listener used to catch the layers changes */
    private ILayerListener layerListener = null;

    protected Thread uiThread;

    private boolean wasInitialized = false;

    private IToolContext context = null;

    // Field used to track temporary (during the execution of the code that
    // follows) the layer that
    // has issued an event against a listener
    private ILayer currEventTriggeringLayer = null;

    // ###
    // ###
    // ###

    protected void initializeOperationModeSupport() {

        this.wasInitialized = false;

        this.uiThread = Thread.currentThread();

        // createContents();

        initListeners();

        this.wasInitialized = true;

    }

    /**
     * create the default listeners for spatial operations.
     * 
     */
    private void initListeners() {

        this.mapListener = new IMapCompositionListener() {

            public void changed(MapCompositionEvent event) {

                if (!wasInitialized()) {
                    return;
                }

                updatedMapLayersActions(event);
            }
        };

        this.layerListener = new ILayerListener() {

            public void refresh(LayerEvent event) {

                if (!wasInitialized()) {
                    return;
                }

                updateLayerActions(event);
            }
        };
    }

    /**
     * Sets the map as current and add the listeners to listen the changes in the map and its
     * layers. Additionally it initialises the current layer list. NOTE: the method gets called ALSO
     * in TOOL mode (not OPERATION mode) but, in this case, null listeners are added
     * 
     * @param map
     */

    private void addListenersTo(final IMap map, final List<ILayer> layerList) {

        assert map != null;
        assert layerList != null;
        if (MergeContext.getInstance().getMergeMode() == MergeContext.MERGEMODE_OPERATION) {
            assert this.mapListener != null;
            assert this.layerListener != null;
        }

        map.addMapCompositionListener(this.mapListener);

        for (ILayer layer : layerList) {

            layer.addListener(this.layerListener);
        }

    }

    /**
     * This method is called if the collection of layer is updated (added or removed). This is a
     * template method that calls a specific method by each event type.
     * 
     * @see changedLayerListActions()
     * @see addedLayerActions()
     * @see removedLayerActions()
     * @param event
     */
    private void updatedMapLayersActions(final MapCompositionEvent event) {

        MapCompositionEvent.EventType eventType = event.getType();

        switch (eventType) {

        case ADDED: {
            Display.findDisplay(uiThread).asyncExec(new Runnable() {
                public void run() {

                    final ILayer layer = event.getLayer();
                    addedLayerActions(layer);
                    // validateParameters();
                    System.out.print("Layer ADDED"); //$NON-NLS-1$
                }
            });
            break;
        }
        case REMOVED: {

            Display.findDisplay(uiThread).asyncExec(new Runnable() {
                public void run() {

                    final ILayer layer = event.getLayer();
                    removedLayerActions(layer);
                    // validateParameters();
                    System.out.print("Layer REMOVED"); //$NON-NLS-1$
                }
            });
            break;
        }
        case MANY_ADDED:
        case MANY_REMOVED:
            Display.findDisplay(uiThread).asyncExec(new Runnable() {

                public void run() {

                    // changedLayerListActions();
                    // validateParameters();

                    System.out.print("Layer MANY ADDED-REMOVED"); //$NON-NLS-1$
                }
            });
            break;
        default:
            System.out.print("Layer DEFAULT"); //$NON-NLS-1$
            break;
        }
    }

    /**
     * This method is called when a layer is changed.
     * 
     * @param event
     * 
     * @see {@link #changedFilterSelectionActions(ILayer, Filter)}
     * @see {@link #changedLayerActions(ILayer)}
     */
    private void updateLayerActions(final LayerEvent event) {

        final ILayer modifiedLayer = event.getSource();

        // Execute actions just on currently selected layer
        // NOTE: This is needed as a single Box selection action seems to
        // trigger
        // a FILTER eventType an ALL map layer and not just on the selected one
        if (modifiedLayer == ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer()) {

            this.currEventTriggeringLayer = modifiedLayer;

            PlatformGISMediator.syncInDisplayThread(new Runnable() {

                public void run() {

                    LayerEvent.EventType type = event.getType();
                    switch (type) {
                    case ALL:
                        changedLayerActions(modifiedLayer);
                        break;

                    case FILTER:
                        Filter newFilter = modifiedLayer.getFilter();
                        changedFilterSelectionActions(modifiedLayer, newFilter);
                        break;

                    default:
                        break;
                    }
                }

            });
        }
    }

    /**
     * This is a callback method, It should be used to implement the actions required when a new
     * layer is added to map. The event occurs when a layer is created or added to the map.
     * <p>
     * This method provide a default implementation which add a {@link ILayerListener} Do not forget
     * call this method to maintain the listener list.
     * </p>
     * 
     * @param layer
     */
    protected void addedLayerActions(final ILayer layer) {

        layer.addListener(this.layerListener);

        // changedLayerListActions();

    }

    /**
     * This is a callback method, It should be used to implement the actions required when a layer
     * is deleted from map.
     * <p>
     * This method provide a default implementation which remove the listener. You can override this
     * method to provide specific actions, Do not forget call this method to maintain the listener
     * list.
     * </p>
     * 
     * @param layer
     */
    protected void removedLayerActions(final ILayer layer) {

        layer.removeListener(this.layerListener);
        // TODO implement removeLayerListActions(layer).
        // removeLayerListActions(layer);
        // changedLayerListActions();

    }

    /**
     * This is a callback method, It should be used to implement the actions required when the
     * features selected in layer are changed. The event occurs when a layer is created or added to
     * the map.
     * 
     * @param layer
     * @param newFilter the filter or Filter.ALL if there is no any feature selected
     */
    protected void changedFilterSelectionActions(final ILayer layer, final Filter newFilter) {

        List<SimpleFeature> selectedFeatures = null;
        try {
            selectedFeatures = Util.retrieveFeatures(newFilter, layer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (selectedFeatures != null) {
            this.addSourceFeatures(selectedFeatures);

            this.display();
        }

    }

    /**
     * This is a callback method, It should be used to implement the actions required when a layer
     * is modified. The event occurs when a layer is created or added to the map.
     * 
     * @param modifiedLayer the modified layer
     */
    protected void changedLayerActions(final ILayer modifiedLayer) {

        // nothing by default implementation
    }

    /**
     * @return the current Map; null if there is not any Map.
     */
    public IMap getCurrentMap() {

        if (this.context == null) {
            return null;
        }

        return this.context.getMap();
    }

    /**
     * Removes the listeners from map.
     * 
     * @param currentMap
     */
    private void removeListenerFrom(IMap map) {

        assert map != null;
        assert this.mapListener != null;

        assert this.layerListener != null;

        for (ILayer layer : getCurrentLayerList()) {

            layer.removeListener(this.layerListener);
        }

        map.removeMapCompositionListener(this.mapListener);
    }

    /**
     * gets the layer list from a map
     * 
     * @param map
     * @return the Layer list of map
     */
    protected List<ILayer> getLayerListOf(IMap map) {

        assert map != null;

        return AppGISAdapter.getMapLayers(map);
    }

    /**
     * @return the layer list of current map
     */
    protected List<ILayer> getCurrentLayerList() {

        if (getCurrentMap() == null) {
            return Collections.emptyList();
        }
        return AppGISAdapter.getMapLayers(this.getCurrentMap());
    }

    /**
     * @return true if the presenter is ready to work, false in other case
     */
    public boolean wasInitialized() {

        return (!this.isDisposed()) && (this.wasInitialized);
    }

    /**
     * @return true if the MergeView was NOT started by the MergeTool (and hence has no
     *         ToolContext!)
     */
    public boolean isOperationMode() {
        boolean isOpMode;

        if (MergeContext.getInstance().getMergeMode() == MergeContext.MERGEMODE_OPERATION) {
            isOpMode = true;
        } else {
            isOpMode = false;
        }
        return isOpMode;
    }

    /**
     * @return the layer that has triggered the current event. Null if no layer event is running.
     */
    public ILayer getCurrentEventTriggeringLayer() {
        //
        return this.currEventTriggeringLayer;
    }

    /**
     * Add features available in MergeContext as result of user pre-selection before MergeOperation
     * launch. Features are added once then are cleared from blackboard (MergeContext)
     */
    private void addPreSelectedFeatures() {
        // Use the currEventTriggeringLayer to store the layer that
        // pseudo-triggered (no actual
        // listener, just) this
        MergeContext mergeContextSingleton = MergeContext.getInstance();
        List<SimpleFeature> selectedFeatures = mergeContextSingleton.getPreselectedFeatures();
        this.currEventTriggeringLayer = mergeContextSingleton.getPreSelectedLayer();
        if (selectedFeatures != null) {
            this.addSourceFeatures(selectedFeatures);
            // Clear preselected features to prevent repeated additions
            mergeContextSingleton.clearPreselectedFeatures();
            this.display();
        }
    }

    // <<<< ###############
    // <<<< ###############
    // <<<< #####END#######
    // <<<< #####OF########
    // <<<< ####NEW#STUFF##
    // <<<< ###############
    // <<<< ###############
    // <<<< ###############
    // <<<< ###############

    @Override
    public void createPartControl(Composite parent) {

        this.mergeComposite = new MergeComposite(parent, SWT.NONE);

        this.mergeComposite.setView(this);

        createActions();
        createToolbar();

        if (this.isOperationMode()) {
            initializeOperationModeSupport(); // <<<< ############### plug-in
                                              // Listener stuff for
                                              // supporting operation mode
            // Add pre-selected features eventually available in blackboard
            // (MergeContext)
            addPreSelectedFeatures();
        }

    }

    private void createToolbar() {

        IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.add(mergeButton);
        toolbar.add(cancelButton);

    }

    private void createActions() {

        this.mergeButton = new MergeButtonAction();
        this.cancelButton = new CancelButtonAction();
    }

    private class CancelButtonAction extends Action {

        public CancelButtonAction() {

            setToolTipText(Messages.MergeView_cancel_tool_tip);
            String imgFile = "images/reset_co.gif"; //$NON-NLS-1$
            setImageDescriptor(ImageDescriptor.createFromFile(MergeView.class, imgFile));
        }

        /**
         * closes the view
         */
        @Override
        public void run() {
            close();
        }
    }

    private void close() {
        try {

            ApplicationGIS.getView(false, MergeView.ID);
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage();
            IViewPart viewPart = page.findView(MergeView.ID);
            page.hideView(viewPart);
        } finally {
            IToolContext context = getContext();
            if (context != null) {
                UndoableMapCommand clearSelectionCommand = context.getSelectionFactory()
                        .createNoSelectCommand();

                context.sendASyncCommand(clearSelectionCommand);
            }
        }

    }

    private class MergeButtonAction extends Action {

        public MergeButtonAction() {

            setToolTipText(Messages.MergeView_finish_tool_tip);
            String imgFile = "images/apply_co.gif"; //$NON-NLS-1$
            setImageDescriptor(ImageDescriptor.createFromFile(MergeView.class, imgFile));
        }

        /**
         * Executes the merge command
         */
        @Override
        public void run() {

            ILayer layer = null;
            // Depending on tool mode (so depending on how MergeView was opened)
            // a different access
            // to current layer is adopted
            if (MergeContext.getInstance().getMergeMode() == MergeContext.MERGEMODE_TOOL) {
                // sets the command using the features present in the merge
                // builder
                IToolContext context = getContext();
                layer = context.getSelectedLayer();
            } else if (MergeContext.getInstance().getMergeMode() == MergeContext.MERGEMODE_OPERATION) {
                layer = ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer();
            }

            MergeFeatureBuilder mergeBuilder = mergeComposite.getMergeBuilder();
            final List<SimpleFeature> sourceFeatures = mergeBuilder.getSourceFeatures();
            final SimpleFeatureCollection sourceFeaturesCollection = DataUtilities
                    .collection(sourceFeatures);

            final SimpleFeature mergedFeature = mergeBuilder.buildMergedFeature();

            mergeBuilder.removeFromSourceFeatures(sourceFeatures);

            MergeFeaturesCommand cmd = MergeFeaturesCommand.getInstance(layer,
                    sourceFeaturesCollection, mergedFeature);

            context.getMap().sendCommandASync(cmd);

            StatusBar.setStatusBarMessage(context, Messages.MergeTool_successful);

            context.getViewportPane().repaint();

            close();
        }
    }

    @Override
    public void setFocus() {

        //
    }

    /**
     * Set the mergeBuilder that contains all the data and populate the composite with these data.
     * 
     * @deprecated
     * @param builder
     */
    public void setBuilder(MergeFeatureBuilder builder) {
        throw new UnsupportedOperationException();
        // this.mergeComposite.setBuilder(builder);
    }

    /**
     * Enables the merge button for merge the features depending on the boolean value.
     * <ul>
     * <li>It should be select two or more feature</li>
     * </ul>
     */
    protected void canMerge(boolean bValue) {

        this.canMerge = bValue;
        this.mergeButton.setEnabled(this.canMerge);
    }

    /**
     * displays the error message
     */
    @SuppressWarnings("unused")
    private void handleError(IToolContext context, MapMouseEvent e) {

        AnimationUpdater.runTimer(context.getMapDisplay(), new MessageBubble(e.x, e.y,
                this.message, PreferenceUtil.instance().getMessageDisplayDelay()));
    }

    /**
     * Add the features the merge feature list
     * 
     * @param sourceFeatures
     */
    public void addSourceFeatures(List<SimpleFeature> sourceFeatures) {

        assert sourceFeatures != null;

        this.mergeComposite.addSourceFeatures(sourceFeatures);
    }

    /**
     * Displays the content of this view
     * 
     * @param selectedFeatures
     */
    public void display() {

        this.mergeComposite.display();
    }

    /**
     * Checks if the feature to be deleted from the list could be deleted. If there is no selection
     * or if it's only one feature on the list, will return false.
     * 
     * @param featureToDelete
     * @return
     */
    // private boolean canDelete(SimpleFeature featureToDelete) {
    //
    // boolean isValid = true;
    // this.message = "";
    //
    // if (featureToDelete == null) {
    // // there is any feature to delete.
    // this.message = Messages.MergeFeatureView_no_feature_to_delete;
    // return false;
    // }
    // List<SimpleFeature> sourceFeatures =
    // this.mergeComposite.getSourceFeatures();
    // if (sourceFeatures.size() == 1) {
    //
    // this.message = Messages.MergeFeatureView_cant_remove;
    // isValid = false;
    // }
    //
    // this.mergeComposite.setMessage(this.message, IMessageProvider.WARNING);
    //
    // return isValid;
    // }

    @Override
    public void dispose() {

        if (this.mergeContext != null) {
            this.mergeContext.disposeMergeView();
        }
        super.dispose();
    }

    @Override
    public void editFeatureChanged(SimpleFeature feature) {
        //

    }

    @Override
    public void setContext(IToolContext newContext) {

        // ######### THIS IS NEW STUFF - Before editing the setContext
        // method was EMPTY
        IMap map;
        if (newContext == null) {
            // initialize or reinitialize the Presenter
            map = getCurrentMap();
            if (map != null) {
                removeListenerFrom(map);
            }
        } else {
            // sets maps and its layers as current
            map = newContext.getMap();
            if (MergeContext.getInstance().getMergeMode() == MergeContext.MERGEMODE_OPERATION) {
                if (map != null) {

                    // add this presenter as listener of the map
                    List<ILayer> layerList = getLayerListOf(map);
                    addListenersTo(map, layerList);
                }
            }
        }
        this.context = newContext;

        // notifies the change in current map

        // REMOVED WHILE IMPLEMENTING STEP-BY-STEP changedMapActions(map);
        if (map != null) {
            // changedLayerListActions(); <<-- this method is void in
            // AbstractParamsPresenter
            // validateParameters();
        }

        // #############################################

    }

    @Override
    public IToolContext getContext() {
        if (this.mergeContext == null)
            return null;
        IToolContext itc = this.mergeContext.getToolContext();
        return itc;
    }

    public void setMergeContext(MergeContext mergeContext) {

        this.mergeContext = mergeContext;
    }

    public boolean isDisposed() {

        return this.mergeComposite.isDisposed();
    }

}
