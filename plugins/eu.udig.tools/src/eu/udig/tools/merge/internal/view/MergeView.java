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

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IMapCompositionListener;
import net.refractions.udig.project.MapCompositionEvent;
import net.refractions.udig.project.command.UndoableMapCommand;
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

import eu.udig.tools.internal.i18n.Messages;
import eu.udig.tools.internal.ui.util.StatusBar;
import eu.udig.tools.merge.MergeContext;

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

    protected Thread uiThread;

    private boolean wasInitialized = false;

    private IToolContext context = null;

    // ###
    // ###
    // ###

    protected void initialize() {

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

                /*
                 * if (!wasInitialized()) { return; }
                 */
                // if (getCommand().isStopped()) {
                // return;
                // }

                updatedMapLayersActions(event);
            }
        };
    }

    /**
     * Sets the map as current and add the listeners to listen the changes in the map and its
     * layers. Additionally it initializes the current layer list.
     * 
     * @param map
     */

    private void addListenersTo(final IMap map/* , final List<ILayer> layerList */) {

        assert map != null;
        // assert layerList != null;
        assert this.mapListener != null;
        // assert this.layerListener != null;

        map.addMapCompositionListener(this.mapListener);
        /*
         * for (ILayer layer : layerList) {
         * 
         * layer.addListener(this.layerListener); }
         */
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
                    // addedLayerActions(layer);
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
                    // removedLayerActions(layer);
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

    @Override
    public void createPartControl(Composite parent) {

        this.mergeComposite = new MergeComposite(parent, SWT.NONE);

        this.mergeComposite.setView(this);

        createActions();
        createToolbar();
        initialize(); // <<<< ############### plug in Listener previous workflow HERE  #################
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
            /*
            assert this.layerListener != null;

            for (ILayer layer : getCurrentLayerList()) {

                    layer.removeListener(this.layerListener);
            }
            */

            map.removeMapCompositionListener(this.mapListener);
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

            // sets the command using the features present in the merge builder
            IToolContext context = getContext();
            final ILayer layer = context.getSelectedLayer();

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

        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub

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
                // List<ILayer> layerList = getLayerListOf(map);
                addListenersTo(map/* , layerList */);
            }
        }
        this.context = newContext;

        // notifies the change in current map
        /*
         * REMOVED WHILE IMPLEMENTING STEP-BY-STEP
        changedMapActions(map);
        if (map != null) {
            changedLayerListActions();
            validateParameters();
        }
        */
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
        // TODO Auto-generated method stub
        return this.mergeComposite.isDisposed();
    }

}
