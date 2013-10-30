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
package eu.udig.tools.arc.internal;

import java.awt.Shape;
import java.awt.geom.Line2D;

import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.commands.DrawCommandFactory;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import eu.udig.tools.arc.internal.beahaviour.EditToolFeedbackManager;
import eu.udig.tools.arc.internal.ArcBuilder;

/**
 * Feedback manager for the arc creation tool that draws the arc while the mouse moves and clears
 * the shape upon tool's cancelation or acceptance.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.1.0
 */
public class ArcFeedbackManager implements EditToolFeedbackManager {

    private DrawShapeCommand drawShapeCommand;

    private ArcBuilder arcBuilder = new ArcBuilder();

    /**
     * Shape used when no arc is possible either because there aren't still 2 coordinates in the
     * edit shape or the three coordinates (the two in the edit shape plus the one of the mouse
     * event) are collinear. This is just an optimization to avoid creating thousands of lines
     */
    private Line2D              linearArc = new Line2D.Double();

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        if (EventType.MOVED != eventType) {
            return false;
        }

        boolean editting = handler.getCurrentState() == EditState.CREATING;
        PrimitiveShape currentShape = handler.getCurrentShape();
        int nPoints = currentShape == null ? 0 : currentShape.getNumPoints();

        boolean isValid = editting && nPoints <= 2;
        if(!isValid){
            clearFeedback();
        }
        return isValid;
    }

    /**
     * Sets the arc to be drawn on the {@link ViewportPane}
     * 
     * @return <code>null</code>, as no undoable map command is needed
     */
    public UndoableMapCommand getFeedbackCommand( EditToolHandler handler, MapMouseEvent e,
                                                  EventType eventType ) {
        final PrimitiveShape currentShape = handler.getCurrentShape();
        if (currentShape == null) {
            return getCancelCommand(handler);
        }
        final int numPoints = currentShape.getNumPoints();
        assert numPoints == 1 || numPoints == 2;

        Point point1 = currentShape.getPoint(0);
        Shape shape = null;
        if (numPoints == 1) {
            linearArc.setLine(point1.getX(), point1.getY(), e.x, e.y);
            shape = linearArc;
        } else {
            Point point2 = currentShape.getPoint(1);
            double x1 = point1.getX();
            double y1 = point1.getY();
            double x2 = point2.getX();
            double y2 = point2.getY();
            double x3 = e.x;
            double y3 = e.y;

            arcBuilder.setPoints(x1, y1, x2, y2, x3, y3);
            shape = arcBuilder.getArc();
            if (shape == null) {
                double fromX = Math.min(x1, Math.min(x2, x3));
                double fromY = Math.min(y1, Math.min(y2, y3));
                double toX = Math.max(x1, Math.max(x2, x3));
                double toY = Math.max(y1, Math.max(y2, y3));
                linearArc.setLine(fromX, fromY, toX, toY);
                shape = linearArc;
            }
        }

        if (drawShapeCommand == null) {
            DrawCommandFactory dcf = DrawCommandFactory.getInstance();
            drawShapeCommand = dcf.createDrawShapeCommand(shape);
            IToolContext context = handler.getContext();
            ViewportPane viewportPane = context.getViewportPane();
            viewportPane.addDrawCommand(drawShapeCommand);
        } else {
            drawShapeCommand.setShape(shape);
        }
        handler.repaint();
        
//        if(undoFeedback == null){
//            undoFeedback = new UndoFeedbackCommand();
//            System.out.println("returning undo fdbk");
//            return undoFeedback;
//        }
        return null;
    }

    /**
     * @see EditToolFeedbackManager#getCancelCommand(EditToolHandler)
     */
    public UndoableMapCommand getCancelCommand( EditToolHandler handler ) {
        clearFeedback();
        return null;
    }

    /**
     * @see EditToolFeedbackManager#getAcceptCommand(EditToolHandler)
     */
    public UndoableMapCommand getAcceptCommand( EditToolHandler handler ) {
        clearFeedback();
        return null;
    }

    private void clearFeedback() {
        if (drawShapeCommand != null) {
            drawShapeCommand.setValid(false);
        }
        drawShapeCommand = null;
    }

}
