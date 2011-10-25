/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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
package net.refractions.udig.tools.internal;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.hsqldb.lib.ArrayUtil;

import net.refractions.udig.boundary.BoundaryProxy;
import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IMapListener;
import net.refractions.udig.project.MapEvent;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.tool.SimpleTool;
import net.refractions.udig.project.ui.tool.options.ToolOptionContributionItem;
import net.refractions.udig.tool.commands.SetBoundaryLayerCommand;
import net.refractions.udig.ui.PlatformGIS;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Provides Boundary Navigation functionality for MapViewport, allows the selection and navigation
 * of background layers that are marked as boundary layers.
 * <p>
 * Each time you select a feature from a boundary layer the tool will move to the next available
 * boundary layer in the stack allowing you to make a selection.
 * </p>
 * 
 * @author leviputna
 * @since 1.2.3
 */
public class BoundaryLayerNavigationTool extends SimpleTool implements ModalTool {

    private SelectionBoxCommand shapeCommand;
    private boolean selecting;
    private Point start;

    private String CURSORPOINTID = "bondatySelectCursor";
    private String CURSORBOXID = "bondatyBoxSelectCursor";

    /** This is the "previous" square so we can refresh the screen correctly */
    private Rectangle previous;

    /**
     * 
     */
    public BoundaryLayerNavigationTool() {
        super(MOUSE | MOTION);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void onMousePressed( MapMouseEvent e ) {
        shapeCommand = new SelectionBoxCommand();

        if (((e.button & MapMouseEvent.BUTTON1) != 0)) {
            updateCursor(e);
            start = e.getPoint();

            if (e.isAltDown()) {
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
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void onMouseReleased( MapMouseEvent e ) {
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
     * @see net.refractions.udig.project.ui.tool.SimpleTool#onMouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    protected void onMouseDragged( MapMouseEvent e ) {
        if (selecting) {
            Point end = e.getPoint();

            if (start == null) return;
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

        SetBoundaryLayerCommand command = new SetBoundaryLayerCommand(bounds);

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
     * @see net.refractions.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        super.dispose();
    }

    public static class OptionContribtionItem extends ContributionItem {

        private ComboViewer comboViewer;
        private IMap map;
        private List<ILayer> boundaryLayers = new ArrayList<ILayer>();

        private static String BOUNDARYSERVICE_ID = "net.refractions.udig.tool.default.BoundaryLayerService";
        
        /**
         * Listens to the user and changes the global IBoundaryService to the indicated strategy.
         */
        private ISelectionChangedListener comboListener = new ISelectionChangedListener(){
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                IStructuredSelection selectedStrategy = (IStructuredSelection) event.getSelection();
                ILayer layer = (ILayer) selectedStrategy.getFirstElement();
                setActiveLayer(layer);
                System.out.println(layer.getName());
            }
        };
        
        public void fill( Composite parent ) {
            map = ApplicationGIS.getActiveMap();
            
            setCombo(parent);
            listenCombo(true);
            // set the current strategy
            comboViewer.setSelection(new StructuredSelection(boundaryLayers.get(0)));
        }
        
        private void setCombo(Composite parent){
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
            
            comboViewer.setInput(getBoundaryLayers());
        }

        private void setActiveLayer( ILayer activeLayer ) {
            IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
            IBoundaryStrategy boundaryStrategy = boundaryService.findProxy(BOUNDARYSERVICE_ID)
                    .getStrategy();

            if (boundaryStrategy instanceof BoundaryLayerStrategy) {
                BoundaryLayerStrategy navigationBoundaryStrategy = (BoundaryLayerStrategy) boundaryStrategy;
                navigationBoundaryStrategy.setActiveLayer(activeLayer);
            }
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

        private List<ILayer> getBoundaryLayers() {

            List<ILayer> layers = map.getMapLayers();
            boundaryLayers.clear();

            for( Iterator<ILayer> i = layers.iterator(); i.hasNext(); ) {
                ILayer layer = i.next();

                if (layer.isApplicable(ILayer.ID_BOUNDARY)) {
                    boundaryLayers.add(layer);
                }
            }
            return boundaryLayers;
        }

    };

}