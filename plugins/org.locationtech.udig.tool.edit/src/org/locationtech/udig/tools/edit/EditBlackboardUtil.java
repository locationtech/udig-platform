/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandEvent;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ICommandListener;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.impl.EditManagerImpl;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener;
import org.locationtech.udig.project.render.displayAdapter.MapDisplayEvent;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.project.ui.tool.Tool;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;

/**
 * To delete soon EditManager will have an editblackboard.
 * 
 * @author jones
 * @since 1.1.0
 */
public class EditBlackboardUtil {
    private static final MathTransform IDENTITY;
    static {
        MathTransform tmp = null;
        try {
            tmp = CRS.findMathTransform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);
        } catch (FactoryException e) {
            // can't happen
        }

        IDENTITY = tmp;
    }

    private volatile static ViewportModelListener listener;

    public static final EditBlackboard EMPTY_BLACKBOARD=new EditBlackboard(0,0,AffineTransform.getTranslateInstance(0, 0), 
            IDENTITY);

    public static final String EDIT_BLACKBOARD_KEY = "EDIT_BLACKBOARD_KEY_839834"; //$NON-NLS-1$
    
    private static Lock blackboardLock = new ReentrantLock();
    public static EditBlackboard getEditBlackboard( IToolContext context, ILayer layer2 ) {
        if( layer2==null || !ApplicationGIS.getOpenMaps().contains(layer2.getMap()))
            return EMPTY_BLACKBOARD;
        ILayer layer = layer2;

        EditBlackboard editBlackBoard;
        blackboardLock.lock();
        try {
            EditManager editManager = ((EditManager) context.getEditManager());
            if (editManager.getEditLayer() != null && editManager.isEditLayerLocked()) {
                layer = context.getEditManager().getEditLayer();
            }

            editBlackBoard = getEditBlackBoardFromLayer(layer);
            
            if (editBlackBoard == null) {

                MathTransform layerToMapTransform;
                try {
                    layerToMapTransform = layer.layerToMapTransform();
                } catch (IOException e) {
                    EditPlugin.log("", e); //$NON-NLS-1$
                    layerToMapTransform = IDENTITY;
                }
                editBlackBoard = new EditBlackboard(context.getMapDisplay().getWidth(), context
                        .getMapDisplay().getHeight(), context.worldToScreenTransform(),
                        layerToMapTransform);

                final EditBlackboard bb = editBlackBoard;

                context.getViewportPane().addPaneListener(new IMapDisplayListener(){

                    public void sizeChanged( MapDisplayEvent event ) {
                        if (event.getOldSize() != null
                                && event.getOldSize().width != event.getSize().width)
                            bb.setWidth(event.getSize().width);
                        if (event.getOldSize() != null
                                && event.getOldSize().height != event.getSize().height)
                            bb.setHeight(event.getSize().height);
                    }

                });

                layer.getBlackboard().put(EDIT_BLACKBOARD_KEY, editBlackBoard);

            }
            enableViewportListener((ViewportModel) context.getViewportModel());
            
            //Vitalus: moved to EditToolHandler.enableListeners().
//            enableClearBlackboardCommand(context);
            
            // disabled until I fix the events
//            enableLayerChangeEventListener(layer, editBlackBoard);
        } finally {
            blackboardLock.unlock();
        }

        if (dirtyAreas.get(layer) != null) {
            openDataChangedDialog(layer, dirtyAreas.get(layer));
        }
        editBlackBoard.setToScreenTransform(context.worldToScreenTransform());
        return editBlackBoard;

    }

    private static Map<ILayer, ILayerListener> layerListenerMap = new HashMap<ILayer, ILayerListener>();
    private static Map<ILayer, Envelope> dirtyAreas = new HashMap<ILayer, Envelope>();
    /**
     * Adds a listener so that the editblackboards can be notified when the data in the feature
     * store are modified.
     */
    private static synchronized void enableLayerChangeEventListener( final ILayer layer,
            final EditBlackboard editBlackboard ) {
        ILayerListener listener = layerListenerMap.get(layer);
        if (listener == null) {
            listener = new ILayerListener(){
                public void refresh( LayerEvent event ) {
                    if (event.getSource() != layer) {
                        layer.removeListener(this);
                        return;
                    }
                    if (event.getType() != LayerEvent.EventType.EDIT_EVENT)
                        return;

                    EditManager editManager = (EditManager) layer.getMap().getEditManager();
                    ILayer editlayer = editManager.getSelectedLayer();
                    if (editManager.getEditLayer() != null && editManager.isEditLayerLocked()) {
                        editlayer = editManager.getEditLayer();
                    }
                    EditState currentEditState=(EditState)layer.getMap().getBlackboard().get(EditToolHandler.EDITSTATE);
                    if (editlayer == layer
                            && (currentEditState == EditState.COMMITTING || EditManagerListener.committing==editManager) )
                        return;

                    FeatureEvent editEvent = (FeatureEvent) event.getNewValue();
                    
                    if (editEvent == null )
                        return;
                   
                    Envelope dirtyArea = dirtyAreas.get(editlayer);
                    if (dirtyArea == null) {
                        dirtyArea = editEvent.getBounds();
                    } else {
                        dirtyArea.expandToInclude(editEvent.getBounds());
                    }

                    dirtyAreas.put(editlayer, dirtyArea);

                    if (editlayer == layer && layer.getMap() == ApplicationGIS.getActiveMap()) {
                        openDataChangedDialog(editlayer, dirtyArea);
                    }
                }
            };
            layerListenerMap.put(layer, listener);
        }

        layer.addListener(listener);

    }
    static volatile Dialog dialog;
    public static void openDataChangedDialog( final ILayer layer, final Envelope dirtyArea ) {
        Display d = Display.getCurrent();
        if (d == null)
            d = Display.getDefault();

        Condition condition = blackboardLock.newCondition();
        try {
            blackboardLock.lock();
            if (dialog != null) {
                // we're in the display thread, which means that the viewport is repainting since the dialog has any other input blocked.
                // so just return
                if( Display.getCurrent()!=null )
                    return;
                // the issue is being resolved we should wait for it to be resolved.
                while( dialog != null ) {
                    try{
                            condition.await(500, TimeUnit.MILLISECONDS);
                    }catch(InterruptedException e){
                        return;
                    }
                }
                return;
            }
        } finally {
            blackboardLock.unlock();
        }

        PlatformGIS.syncInDisplayThread(d, new Runnable(){
            public void run() {
                try {
                    blackboardLock.lock();
                    if (dirtyAreas.get(layer) == null)
                        return;

                    dialog = new Dialog(Display.getCurrent().getActiveShell()){
                        private final int UPDATE = 1;
                        private final int CLEAR = 2;
                        private final int IGNORE = 3;

                        public Control createDialogArea( Composite parent ) {
                            Composite comp = new Composite(parent, SWT.NONE);
                            comp.setLayout(new GridLayout(2, false));
                            Display display = Display.getCurrent();
                            Label label = new Label(comp, SWT.NONE);
                            label
                                    .setBackground(display
                                            .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
                            label
                                    .setForeground(display
                                            .getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
                            label.setImage(Dialog.getImage(Dialog.DLG_IMG_WARNING));
                            
                            label.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
                            Text text = new Text(comp, SWT.WRAP | SWT.READ_ONLY);
                            text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                            text.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
                            text.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
                            text
                                    .setText(Messages.EditBlackboardUtil_data_changed);
                            return comp;
                        }

                        public void createButtonsForButtonBar( Composite composite ) {
                            createButton(composite, UPDATE, Messages.EditBlackboardUtil_update, false);
                            createButton(composite, CLEAR, Messages.EditBlackboardUtil_clear, true);
                            createButton(composite, IGNORE, Messages.EditBlackboardUtil_ignore, false);
                        }
                        public void buttonPressed( int button ) {
                            switch( button ) {
                            case UPDATE: {
                                boolean ok = MessageDialog.openConfirm(getParentShell(),
                                        Messages.EditBlackboardUtil_Update_Selection,
                                        Messages.EditBlackboardUtil_update_selection_confirmation);
                                if (ok) {
                                    try {
                                        ProgressMonitorDialog d = new ProgressMonitorDialog(this
                                                .getParentShell());

                                        blackboardLock.unlock();
                                        d.run(false, false, new IRunnableWithProgress(){
                                            public void run( IProgressMonitor monitor ) {
                                                updateFeatures(layer, monitor, dirtyArea);
                                            }
                                        });
                                        okPressed();
                                    } catch (Exception e) {
                                        EditPlugin.log("", e); //$NON-NLS-1$
                                    }
                                }
                                break;
                            }
                            case CLEAR: {
                                boolean ok = MessageDialog
                                        .openConfirm(getParentShell(), Messages.EditBlackboardUtil_clear_selection,
                                                Messages.EditBlackboardUtil_changes_will_be_lost);
                                if (ok) {
                                    blackboardLock.unlock();
                                    (getEditBlackBoardFromLayer(layer)).clear();
                                    layer.getMap().getBlackboard().put(
                                            EditToolHandler.CURRENT_SHAPE, null);
                                    ((EditManager) layer.getMap().getEditManager()).setEditFeature(
                                            null, null);
                                    okPressed();
                                }
                                break;
                            }
                            case IGNORE:
                                boolean ok = MessageDialog
                                        .openConfirm(getParentShell(), Messages.EditBlackboardUtil_ignore_change,
                                                Messages.EditBlackboardUtil_changes_will_be_overwritten);
                                if (ok) {
                                    okPressed();
                                }
                                break;

                            default:
                                cancelPressed();
                                break;
                            }
                        }
                        public void okPressed() {
                            dirtyAreas.remove(layer);
                            super.okPressed();
                        }
                    };
                    dialog.setBlockOnOpen(true);
                    dialog.open();
                } finally {
                    blackboardLock.unlock();
                    dialog = null;
                }

            }
        });
    }

    /**
     * Updates the features in the "dirty Area" so that the {@link EditGeom}s reflect the actual state of the stored features.  
     * Any changes to the {@link EditGeom} will be lost. 
     *
     * @param layer that needs to be updated.
     * @param monitor progress monitor
     * @param dirtyArea area that needs to be updated.
     */
    public static void updateFeatures( ILayer layer, IProgressMonitor monitor, Envelope dirtyArea ) {
        EditBlackboard bb = getEditBlackBoardFromLayer(layer);
        List<EditGeom> geoms = bb.getGeoms();
        monitor.beginTask(Messages.EditBlackboardUtil_updating_selected_features, geoms.size());

        PrimitiveShape shape = (PrimitiveShape) layer.getMap().getBlackboard().get(
                EditToolHandler.CURRENT_SHAPE);
        EditManager editManager = (EditManager) layer.getMap().getEditManager();

        FilterFactory factory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Set<Identifier> ids = new HashSet<Identifier>();
        for( EditGeom geom : geoms ) {
        	ids.add(factory.featureId(geom.getFeatureIDRef().get()));
        }
        Id fidFilter = factory.id(ids);
        Filter filter =factory.and(fidFilter, layer.createBBoxFilter(dirtyArea, new NullProgressMonitor()));
        	
        try {
            FeatureSource<SimpleFeatureType, SimpleFeature> fs = layer.getResource(FeatureSource.class, monitor);
            FeatureCollection<SimpleFeatureType, SimpleFeature>  results = fs.getFeatures(filter);
            FeatureIterator<SimpleFeature> reader = results.features();
            try {
                int read = 0;
                boolean selectedFound = false;
                List<EditGeom> toRemove = new ArrayList<EditGeom>();
                while( reader.hasNext() ) {
                	int count = geoms.size() - read;
                    monitor.setTaskName(MessageFormat.format(Messages.EditBlackboardUtil_count_remaining, new Object[] {count}));
                    read++;
                    SimpleFeature feature = reader.next();

                    for( EditGeom geom : geoms ) {
                        if (feature.getID().equals(geom.getFeatureIDRef().get())) {
                            toRemove.add(geom);
                        }
                    }

                    Map<Geometry, EditGeom> mapping = bb.addGeometry((Geometry) feature.getDefaultGeometry(),
                            feature.getID());
                    if (feature.getID().equals(shape.getEditGeom().getFeatureIDRef().get())) {
                        editManager.setEditFeature(feature, (Layer) layer);
                        layer.getMap().getBlackboard().put(EditToolHandler.CURRENT_SHAPE,
                                mapping.values().iterator().next().getShell());
                        selectedFound = true;
                    }
                    monitor.worked(1);
                }
                if (!selectedFound) {
                    layer.getMap().getBlackboard().put(EditToolHandler.CURRENT_SHAPE, null);
                    editManager.setEditFeature(null, null);
                }
                bb.removeGeometries(toRemove);
            } finally {
                reader.close();
                monitor.done();
            }
        } catch (IOException e) {
            EditPlugin.log("", e); //$NON-NLS-1$
        }
    }

    /**
     * Returns the Editblackboard from the layer if it is on the layer or null if it hasn't been created yet.
     * {@link #getEditBlackboard(IToolContext, ILayer)} will create and initialize the blackbaord
     *
     * @param layer
     * @return
     */
    private static EditBlackboard getEditBlackBoardFromLayer( ILayer layer ) {
        blackboardLock.lock();
        try{
            return (EditBlackboard) layer.getBlackboard().get(EDIT_BLACKBOARD_KEY);
        }finally{
            blackboardLock.unlock();
        }
    }

    /**
     * Command handler for the command "org.locationtech.udig.tool.edit.clearAction"
     *  to clear EditBlackboard.
     */
    static IHandler clearEditBlackboardHandler;
    
    /**
     * Listener for "org.locationtech.udig.tool.edit.clearAction" command.
     */
    static ICommandListener clearEditBlackboardCommandListener;

    /**
     * Sets the command handler for the "ESC" button. The handler clears current edit blackboard.
     *  <p>
     *  Called from <code>EditToolHandler.enableListeners()</code>.
     *  
     * @param context
     */
    static synchronized void enableClearBlackboardCommand( final IToolContext context ) {
        if (clearEditBlackboardHandler == null) {
            clearEditBlackboardHandler = new AbstractHandler(){

                public Object execute( ExecutionEvent event ) throws ExecutionException {
                    Tool tool = ApplicationGIS.getToolManager().getActiveTool();

                    if (tool instanceof AbstractEditTool) {
                        EditToolHandler editToolHandler = ((AbstractEditTool) tool).getHandler();
                        List<Behaviour> behaviours = editToolHandler.getCancelBehaviours();
                        UndoableComposite compositeCommand = new UndoableComposite();
                        for( Behaviour behaviour : behaviours ) {
                            if (behaviour.isValid(editToolHandler)) {
                                UndoableMapCommand command = behaviour.getCommand(editToolHandler);
                                if (command != null)
                                    compositeCommand.getCommands().add(command);
                            }
                        }
                        if (!compositeCommand.getCommands().isEmpty())
                            editToolHandler.getContext().sendASyncCommand(compositeCommand);
                    }

                    return null;
                }
            };

        }
        ICommandService service = (ICommandService) PlatformUI.getWorkbench().getAdapter(
                ICommandService.class);

        Command command = service.getCommand("org.locationtech.udig.tool.edit.clearAction"); //$NON-NLS-1$
        command.setHandler(clearEditBlackboardHandler);
        
        if(clearEditBlackboardCommandListener == null){
            clearEditBlackboardCommandListener  =  new ICommandListener(){

                public void commandChanged( CommandEvent commandEvent ) {
                    if (commandEvent.isHandledChanged()) {
                        commandEvent.getCommand().removeCommandListener(this);
                        clearEditBlackboardCommandListener = null;
                        
                        IMap map = ApplicationGIS.getActiveMap();
                        resetBlackboards(map);
                    }
                }

            };
            command.addCommandListener(clearEditBlackboardCommandListener);
        }
    }
    
    /**
     *  Removes a command handler for the "ESC" button.
     *  <p>
     *  Called from <code>EditToolHandler.disableListeners()</code>.
     */
    static synchronized void disableClearBlackboardCommand() {
        ICommandService service = (ICommandService) PlatformUI.getWorkbench().getAdapter(
                ICommandService.class);
        Command command = service.getCommand("org.locationtech.udig.tool.edit.clearAction"); //$NON-NLS-1$

        if(clearEditBlackboardCommandListener != null){
            command.removeCommandListener(clearEditBlackboardCommandListener);
            clearEditBlackboardCommandListener = null;
        }
        
        command.setHandler(null);

    }

    /**
     * Disables listeners so that they will not get events.
     * Should be called when tool is disabled or when a blackboard is no longer required.
     * 
     * @see #getEditBlackboard(IToolContext, ILayer) (it enables listeners).
     */
    public static void doneListening() {
        disableViewportListener();
//        disableLayerEvents();
    }

    /**
     * Disables the listeners listening to layers for edit events.
     * @see #enableLayerChangeEventListener(ILayer, EditBlackboard)
     */
    public static synchronized void disableLayerEvents() {
        Collection<Map.Entry<ILayer, ILayerListener>> entries = layerListenerMap.entrySet();
        for( Map.Entry<ILayer, ILayerListener> entry : entries ) {
            entry.getKey().removeListener(entry.getValue());
        }
        layerListenerMap.clear();
    }

    /**
     * Returns the selectedLayer or if it is not editable or not visible then the edit layer will be
     * 
     * @return
     */
    public static ILayer findEditLayer( IToolContext context ) {
        ILayer layer = null;
        IMap map = context.getMap();
        // The selected layer will become the edit layer! If it's a featureStore and editable of
        // course.
        ILayer selectedLayer = ((EditManagerImpl) map.getEditManager()).getSelectedLayer();
        if (isEditable(selectedLayer)) {
            return selectedLayer;
        }
        // Otherwise we'll fall back to the current editLayer
        if (isEditable(map.getEditManager().getEditLayer()))
            return map.getEditManager().getEditLayer();
        // If all else fails we'll iterate through the layer and find the first layer eligable for
        // editing.
        // We should really never get here though.
        for( Iterator<ILayer> iter = map.getMapLayers().iterator(); iter.hasNext(); ) {
            layer = iter.next();
            if (isEditable(layer))
                break;
        }
        return layer;
    }

    /**
     * Checks that a layer has a FeatureStore, is editable and is visible.
     */
    private static boolean isEditable( ILayer layer ) {
        return layer != null && layer.hasResource(FeatureStore.class)
                && layer.getInteraction(Interaction.EDIT) && layer.isVisible(); //$NON-NLS-1$
    }
    @SuppressWarnings("unchecked")
    private synchronized static void enableViewportListener( ViewportModel model ) {
        disableViewportListener();
        listener = new ViewportModelListener(model);

        listener.model.eAdapters().add(listener);

    }

    private synchronized static void disableViewportListener() {
        if (listener != null)
            listener.model.eAdapters().remove(listener);
        listener = null;
    }

    /** 
     * Listens for changes to the viewport model and transforms the edit blackboards to the new CRS or bounds
     * 
     */
    static class ViewportModelListener extends AdapterImpl {
        ViewportModel model;
        private ILayer layer;
        ViewportModelListener( ViewportModel model ) {
            this.model = model;
            this.layer = model.getMap().getEditManager().getSelectedLayer();
        }
        @Override
        public void notifyChanged( Notification msg ) {
            if (listener == null) {
                model.eAdapters().remove(listener);
                listener = this;
                return;
            }
			switch( msg.getFeatureID(ViewportModel.class) ) {
            case RenderPackage.VIEWPORT_MODEL__BOUNDS: {
                EditBlackboard editBlackBoard = getEditBlackBoardFromLayer(layer);
                if( editBlackBoard == null ){
                	return; // cannot notify edit blackboard
                }
            	editBlackBoard.setToScreenTransform(model.worldToScreenTransform());
                break;
            }
            case RenderPackage.VIEWPORT_MODEL__CRS: {            	
                EditBlackboard editBlackBoard = getEditBlackBoardFromLayer(layer);
                if( editBlackBoard == null ){
                	return; // cannot notify edit blackboard
                }
                try {
                    editBlackBoard
                            .setMapLayerTransform(layer.mapToLayerTransform());
                } catch (IOException e) {
                    EditPlugin.log("", e); //$NON-NLS-1$
                }
                editBlackBoard.setToScreenTransform(model.worldToScreenTransform());
                break;
            }
			}
        }
    }
    /**
     *  clears edit blackboard for all layers in a given map... Critical to prevent memory leaks
     */
    public static void resetBlackboards(IMap map) {
        if (map != null) {
            blackboardLock.lock();
            try {
                List<ILayer> layers = map.getMapLayers();
                for( ILayer layer : layers ) {
                    EditBlackboard blackboard = getEditBlackBoardFromLayer(layer);
                    if (blackboard != null){
                        blackboard.clear();
                        layer.getBlackboard().put(EDIT_BLACKBOARD_KEY, null);
                    }
                }
            } finally {
                blackboardLock.unlock();
            }
        }
    }

}
