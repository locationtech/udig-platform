/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.select.internal;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.locationtech.udig.aoi.AOIListener;
import org.locationtech.udig.aoi.IAOIService;
import org.locationtech.udig.aoi.IAOIStrategy;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ui.commands.SelectionBoxCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.AbstractModalTool;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.project.ui.tool.options.ToolOptionContributionItem;
import org.locationtech.udig.tool.select.SelectPlugin;
import org.locationtech.udig.tool.select.commands.SetAOILayerCommand;
import org.locationtech.udig.tool.select.preferences.SelectionToolPreferencePage;
import org.locationtech.udig.ui.PlatformGIS;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Provides AOI Navigation functionality for MapViewport, allows the selection and navigation
 * of background layers that are marked as AOI layers.
 * <p>
 * Each time you select a feature from a AOI layer the tool will move to the next available
 * AOI layer in the stack allowing you to make a selection.
 * </p>
 * 
 * @author leviputna
 * @since 1.2.3
 */
public class AOILayerSelectionTool extends AbstractModalTool implements ModalTool {

    private SelectionBoxCommand shapeCommand;
    private boolean selecting;
    private Point start;

    private String CURSORPOINTID = "aoiSelectCursor";
    private String CURSORBOXID = "aoiBoxSelectCursor";

    boolean showContextOnRightClick = false;

    /**
     * 
     */
    public AOILayerSelectionTool() {
        super(MOUSE | MOTION);
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mousePressed(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed( MapMouseEvent e ) {
        shapeCommand = new SelectionBoxCommand();

        if (e.button == MapMouseEvent.BUTTON3 && showContextOnRightClick) {
            ((ViewportPane) e.source).getMapEditor().openContextMenu();
            return;
        }

        if ((e.button == MapMouseEvent.BUTTON1) || (e.button == MapMouseEvent.BUTTON3)) {
            updateCursor(e);
            start = e.getPoint();

            if (e.isShiftDown()) {
                selecting = true;

                shapeCommand.setValid(true);
                shapeCommand.setShape(new Rectangle(start.x, start.y, 0, 0));
                context.sendASyncCommand(shapeCommand);

            } else {

                selecting = false;
                clickFeedback(e);

                Envelope bounds = getBounds(e);
                sendSelectionCommand(e, bounds);
            }
        }

    }

    /**
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseReleased(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseReleased( MapMouseEvent e ) {
        if (selecting) {
            Envelope bounds = getBounds(e);
            sendSelectionCommand(e, bounds);
        }
    }

    private Envelope getBounds( MapMouseEvent e ) {
        Point point = e.getPoint();
        if (start == null || start.equals(point)) {

            return getContext().getBoundingBox(point, 3);
        } else {
            Coordinate c1 = context.getMap().getViewportModel().pixelToWorld(start.x, start.y);
            Coordinate c2 = context.getMap().getViewportModel().pixelToWorld(point.x, point.y);

            return new Envelope(c1, c2);
        }
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.SimpleTool#onMouseDragged(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseDragged( MapMouseEvent e ) {
        if (selecting) {
            Point end = e.getPoint();

            if (start == null)
                return;
            shapeCommand.setShape(new Rectangle(Math.min(start.x, end.x), Math.min(start.y, end.y),
                    Math.abs(start.x - end.x), Math.abs(start.y - end.y)));
            context.getViewportPane().repaint();

        }
    }

    /**
     * @param e
     * @param bounds
     */
    protected void sendSelectionCommand( MapMouseEvent e, Envelope bounds ) {

        SetAOILayerCommand command = new SetAOILayerCommand(e, bounds);

        getContext().sendASyncCommand(command);

        selecting = false;
        shapeCommand.setValid(false);
        getContext().getViewportPane().repaint();
    }

    /**
     * Provides user feedback when box select is disabled.
     * 
     * @param e
     */
    public void clickFeedback( MapMouseEvent e ) {
        Rectangle square = new Rectangle(e.x - 2, e.y - 2, 4, 4);

        shapeCommand.setValid(true);
        shapeCommand.setShape(square);
        context.sendASyncCommand(shapeCommand);
        context.getViewportPane().repaint();
    }

    private void updateCursor( MapMouseEvent e ) {

        if (e.isAltDown()) {
            setCursorID(CURSORBOXID);
        } else {
            setCursorID(CURSORPOINTID);
        }

    }

    /**
     * @see org.locationtech.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        super.dispose();
    }

    public static class OptionContribtionItem extends ToolOptionContributionItem {

        private ComboViewer comboViewer;

        private static String AOI_LAYER_ID = "org.locationtech.udig.tool.select.internal.aoiLayer";

        /**
         * Listens to the user and changes the global IAOIService to the indicated strategy.
         */
        private ISelectionChangedListener comboListener = new ISelectionChangedListener(){
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                IStructuredSelection selectedStrategy = (IStructuredSelection) event.getSelection();
                ILayer layer = (ILayer) selectedStrategy.getFirstElement();
                setActiveLayer(layer);
            }
        };

        /**
         * Watches for a change in AOI layer and sets the combo to the new layer
         */
        protected AOIListener watcher = new AOIListener(){
            public void handleEvent( AOIListener.Event event ) {
                PlatformGIS.asyncInDisplayThread(new Runnable(){

                    @Override
                    public void run() {
                        ILayer activeLayer = getAOILayerStrategy().getActiveLayer();
                        List<ILayer> layers = getAOILayerStrategy().getAOILayers();
                        if( comboViewer == null || comboViewer.getControl() == null || comboViewer.getControl().isDisposed()){
                            return;
                        }
                        comboViewer.setInput(layers);
                        // check if the current layer still exists
                        if (layers.contains(activeLayer)) {
                            setSelected(activeLayer);
                        } else {
                            setSelected(null);
                        }
                    }
                }, true);
            }
        };

        @Override
        protected IPreferenceStore fillFields( Composite parent ) {

            Button nav = new Button(parent, SWT.CHECK);
            nav.setText("Navigate");
            addField(SelectionToolPreferencePage.NAVIGATE_SELECTION, nav);

//            Button zoom = new Button(parent, SWT.CHECK);
//            zoom.setText("Zoom to selection");
//            addField(SelectionToolPreferencePage.ZOOM_TO_SELECTION, zoom);
                        
            setCombo(parent);
            listenAOILayer(true);

            // set the list of layers and the active layer
            List<ILayer> layers = getAOILayerStrategy().getAOILayers();
            ILayer activeLayer = getAOILayerStrategy().getActiveLayer();
            comboViewer.setInput(layers);
            if (!layers.isEmpty()) {
                comboViewer.setInput(layers);
                if (activeLayer == null) {
                    activeLayer = layers.get(0);
                }
            }
            setSelected(activeLayer);
            listenCombo(true);

            return SelectPlugin.getDefault().getPreferenceStore();
        }

        private void setCombo( Composite parent ) {
            comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
            comboViewer.setContentProvider(new ArrayContentProvider());

            comboViewer.setLabelProvider(new LabelProvider(){
                @Override
                public String getText( Object element ) {
                    if (element instanceof ILayer) {
                        ILayer layer = (ILayer) element;
                        return layer.getName();
                    }
                    return super.getText(element);
                }
            });

            comboViewer.setInput(getAOILayers());
        }

        protected void listenCombo( boolean listen ) {
            if (comboViewer == null || comboViewer.getControl().isDisposed()) {
                return;
            }
            if (listen) {
                comboViewer.addSelectionChangedListener(comboListener);
            } else {
                comboViewer.removeSelectionChangedListener(comboListener);
            }
        }

        protected void listenAOILayer( boolean listen ) {
            AOILayerStrategy aOILayerStrategy = getAOILayerStrategy();
            if (aOILayerStrategy == null) {
                return;
            }
            if (listen) {
                aOILayerStrategy.addListener(watcher);
            } else {
                aOILayerStrategy.removeListener(watcher);
            }
        }

        /*
         * This will update the combo viewer (carefully unhooking events while the viewer is
         * updated).
         * @param selected
         */
        private void setSelected( ILayer selected ) {

            boolean disposed = comboViewer.getControl().isDisposed();
            if (comboViewer == null || disposed) {
                listenAOILayer(false);
                return; // the view has shutdown!
            }

            ILayer current = getSelected();
            // check combo
            if (current != selected) {
                try {
                    listenCombo(false);
                    comboViewer.setSelection(new StructuredSelection(selected), true);
                } finally {
                    listenCombo(true);
                }
            }

        }

        /*
         * Get the AOI Layer currently selected in this tool
         * @return ILayer currently selected in this tool
         */
        private ILayer getSelected() {
            if (comboViewer.getSelection() instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
                return (ILayer) selection.getFirstElement();
            }
            return null;
        }

        /*
         * Sets the active layer in the AOI layer strategy
         */
        private void setActiveLayer( ILayer activeLayer ) {
            getAOILayerStrategy().setActiveLayer(activeLayer);
        }

        /*
         * returns a AOILayerStrategy object for quick access
         */
        private AOILayerStrategy getAOILayerStrategy() {
            IAOIService aOIService = PlatformGIS.getAOIService();
            IAOIStrategy aOIStrategy = aOIService.findProxy(AOI_LAYER_ID)
                    .getStrategy();

            if (aOIStrategy instanceof AOILayerStrategy) {
                return (AOILayerStrategy) aOIStrategy;
            }
            return null;
        }

        /*
         * gets a list of AOI layers via the AOI strategy
         */
        private List<ILayer> getAOILayers() {
            return getAOILayerStrategy().getAOILayers();
        }

    };

}
