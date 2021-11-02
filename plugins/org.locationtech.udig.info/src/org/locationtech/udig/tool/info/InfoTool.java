/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.tool.info;

import java.awt.Rectangle;

import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.commands.SelectionBoxCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.AbstractModalTool;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.tool.info.internal.InfoView2;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * InfoTool is Map Tool used to grab identity information about what is on the screen.
 * <p>
 * InfoTool makes use its ModalTool superclass to access RenderManager; getInfo is a first class
 * request supported by the API. You can however trace through this code as an example for creating
 * your own tools.
 * </p>
 * <p>
 * Workflow:
 * <ul>
 * <li>RenderManger.getInfo( Point ) for each renderer asks ...
 * <li>Renderer.getInfo( Point ) which ...
 * <li>back projects Point to a Extent used with
 * <li>Resource (ie FeatureSource.getFeatures( filter ) to arrive at
 * <li>Information (ie a FeatureCollection) wrapped in an LayerPointInfo providing
 * <li>LayerPointInfo.getObject() & LayerPointInfo.getMimeType()
 * </ul>
 * </p>
 *
 * @author Jody Garnett
 * @version $Revision: 1.9 $
 */
public class InfoTool extends AbstractModalTool implements ModalTool {

    /**
     * ID of the current tool.
     */
    public static final String ID = "org.locationtech.udig.tool.info.infoMode"; //$NON-NLS-1$

    public static final String CATEGORY_ID = "org.locationtech.udig.tool.category.info"; //$NON-NLS-1$

    /**
     * Creates an LayerPointInfo Tool.
     */
    public InfoTool() {
        super(MOUSE | MOTION);
    }

    @Override
    public void mousePressed(MapMouseEvent e) {
        draw.setValid(true); // make sure context.getViewportPane().repaint() knows about us
        context.sendASyncCommand(draw); // should of isValided us
        feedback(e);

    }

    @Override
    public void mouseDragged(MapMouseEvent e) {
        feedback(e);

    }

    SelectionBoxCommand draw = new SelectionBoxCommand();

    /** This is the "previous" square so we can refresh the screen correctly */
    private Rectangle previous;

    /**
     * Provides user feedback
     *
     * @param e
     */
    public void feedback(MapMouseEvent e) {
        Rectangle square = new Rectangle(e.x - 3, e.y - 3, 5, 5);
        draw.setShape(square);
        if (previous != null) {
            context.getViewportPane().repaint(previous.x - 4, previous.y - 4, previous.width + 8,
                    previous.height + 8);
        }
        previous = square;
        context.getViewportPane().repaint(square.x - 4, square.y - 4, square.width + 8,
                square.height + 8);
        // context.getViewportPane().repaint();
    }

    /**
     * What's this then?
     * <p>
     * See class description for intended workflow.
     * </p>
     *
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseReleased(MapMouseEvent)
     */
    @Override
    public void mouseReleased(MapMouseEvent e) {
        try {

            ReferencedEnvelope bbox = context.getBoundingBox(e.getPoint(), 5);

            final InfoView2.InfoRequest request = new InfoView2.InfoRequest();
            request.bbox = bbox;
            request.layers = context.getMapLayers();

            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    InfoView2 infoView = (InfoView2) ApplicationGIS.getView(true,
                            InfoView2.VIEW_ID);

                    // JONES: deselect current feature so it won't flash when view is activated (it
                    // won't be valid
                    // one the new search passes.
                    if (infoView != null)
                        if (infoView.getSite().getSelectionProvider() != null)
                            infoView.getSite().getSelectionProvider()
                                    .setSelection(new StructuredSelection());

                    // JONES: activate view now that there is no current selection.
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getActivePage();
                    if (!page.isPartVisible(infoView))
                        page.bringToTop(infoView);

                    // we got here and info was null? Don't want to fail on first attempt
                    infoView = (InfoView2) ApplicationGIS.getView(false, InfoView2.VIEW_ID);
                    infoView.search(request);
                }
            });
        } catch (Throwable e1) {
            LoggingSupport.log(InfoPlugin.getDefault(), "Could not display information", e1); //$NON-NLS-1$
        } finally {
            draw.setValid(false); // get us off the draw stack for
                                  // context.getViewportPane().repaint();
            context.getViewportPane().repaint();
        }
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.Tool#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
    }

}
