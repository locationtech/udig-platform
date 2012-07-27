/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.merge.internal.view;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILayerListener;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IMapCompositionListener;
import net.refractions.udig.project.LayerEvent;
import net.refractions.udig.project.MapCompositionEvent;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.commands.selection.BBoxSelectionCommand;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.animation.MessageBubble;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;

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

//import au.com.objectix.jgridshift.Util;

import eu.udig.tools.internal.mediator.PlatformGISMediator;
import eu.udig.tools.internal.i18n.Messages;
import eu.udig.tools.internal.mediator.AppGISAdapter;
import eu.udig.tools.internal.ui.util.StatusBar;
import eu.udig.tools.merge.MergeContext;
import eu.udig.tools.merge.Util;

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
 */
public class MergeView extends ViewPart implements IUDIGView {

    public static final String ID = "eu.udig.tools.merge.internal.view.MergeView"; //$NON-NLS-1$

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

    private boolean operationMode = false;

    /** The layer in which a filter change event has happened at runtime */
    private ILayer currEventTriggeringLayer = null;

    /** The layer to check for pre-selected feature to add to MergeView at load time */
    private ILayer operationLaunchTimeSelectedLayer = null;

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
     * layers. Additionally it initialises the current layer list.
     * 
     * @param map
     */

    private void addListenersTo(final IMap map, final List<ILayer> layerList) {

        assert map != null;
        assert layerList != null;
        assert this.mapListener != null;
        assert this.layerListener != null;

        map.addMapCompositionListener(this.mapListener);

        if (this.isOperationMode()) {
            operationLaunchTimeSelectedLayer = ApplicationGIS.getActiveMap().getEditManager()
                    .getSelectedLayer();
        }

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
                    System.out.print("Layer ADDED");
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
                    System.out.print("Layer REMOVED");
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

                    System.out.print("Layer MANY ADDED-REMOVED");
                }
            });
            break;
        default:
            System.out.print("Layer DEFAULT");
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
        // Skip further steps if event comes from layer not selected
        //      NOTE: this is needed since Box Selection actions issues the same event
        //      on ALL Map layers (why??) and not on just the selected one        
        if (modifiedLayer != getCurrentMap().getEditManager().getSelectedLayer()){
            return;
        }
        
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
        // Since all actions on triggering layer are performed set currEventTriggeringLayer to null
        this.currEventTriggeringLayer = null;
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
        return operationMode;
    }

    /**
     * @return the layer that has triggered the current event. Null if no layer event is running.
     */
    public ILayer getCurrentEventTriggeringLayer() {

        return this.currEventTriggeringLayer;
    }

    // <<<< ###############
    // <<<< ###############
    // <<<< ###############
    // <<<< ###############
    // <<<< ###############
    // <<<< ###############
    // <<<< ###############
    // <<<< ###############
    // <<<< ###############

    @Override
    public void createPartControl(Composite parent) {

        this.mergeComposite = new MergeComposite(parent, SWT.NONE);

        this.mergeComposite.setView(this);

        // If, at this step, MergeView has no context it means that has been started
        // by MergeOperation: this must be traced to prevent call on null objects.
        if (this.getContext() == null) {
            this.operationMode = true;
        }

        createActions();
        createToolbar();

        if (this.isOperationMode()) {
            initializeOperationModeSupport(); // <<<< ############### plug-in Listener stuff in
                                              // previous workflow HERE
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
            UndoableMapCommand clearSelectionCommand = context.getSelectionFactory()
                    .createNoSelectCommand();

            context.sendASyncCommand(clearSelectionCommand);
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

            final ILayer layer;

            // sets the command using the features present in the merge builder
            if (isOperationMode()) {

                layer = getCurrentEventTriggeringLayer();

            } else {

                IToolContext context = getContext();
                layer = context.getSelectedLayer();

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
        // Do nothing...
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
    private void handleError(IToolContext context, MapMouseEvent e) {

        AnimationUpdater.runTimer(context.getMapDisplay(), new MessageBubble(e.x, e.y,
                this.message, PreferenceUtil.instance().getMessageDisplayDelay())); //$NON-NLS-1$
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
    // List<SimpleFeature> sourceFeatures = this.mergeComposite.getSourceFeatures();
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

    }

    @Override
    public void setContext(IToolContext newContext) {

        // ######### THIS IS NEW STUFF - Before editing the setContext method was EMPTY
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
            if (map != null) {

                // add this presenter as listener of the map
                List<ILayer> layerList = getLayerListOf(map);
                addListenersTo(map, layerList);
            }
        }
        this.context = newContext;

        // notifies the change in current map

        // REMOVED WHILE IMPLEMENTING STEP-BY-STEP changedMapActions(map);
        if (map != null) {
            // TODO FIX HERE
            // changedLayerListActions(); <<-- this method is void in AbstractParamsPresenter
            // validateParameters();
        }

        // #############################################

    }

    @Override
    public IToolContext getContext() {
        if (this.mergeContext == null)
            return null;

        return this.mergeContext.getToolContext();
    }

    public void setMergeContext(MergeContext mergeContext) {

        this.mergeContext = mergeContext;
    }

    public boolean isDisposed() {

        return this.mergeComposite.isDisposed();
    }

}
