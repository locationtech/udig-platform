/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to license under Lesser General Public License (LGPL).
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
package eu.udig.tools.arc.internal.beahaviour;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

/**
 * <p>
 * Requirements:
 * <ul>
 * <li>EventType==RELEASED</li>
 * <li>EditState==MODIFIED or CREATING</li>
 * <li>no modifiers</li>
 * <li>button1 clicked</li>
 * <li>no buttons down</li>
 * </ul>
 * </p>
 * <p>
 * Action:
 * <ul>
 * <li>Adds the point where click occurs <b>if</b> addPoint is true. (Default behaviour)</li>
 * <li>Runs Accept Behaviours</li>
 * <li>If current state is CREATING the changes state to MODIFYING</li>
 * </ul>
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.1.0
 */
public class NumOfPointsRunAcceptBehaviour extends AcceptOnDoubleClickBehaviour
        implements
            EventBehaviour {

    private int numOfPointsToAccept;

    /**
     * @param numOfPointsToAccept number of points, counting the currently being added one, that
     *        must exist in the edit blackboard to accept
     */
    public NumOfPointsRunAcceptBehaviour( int numOfPointsToAccept ) {
        super();
        assert numOfPointsToAccept > 0;
        this.numOfPointsToAccept = numOfPointsToAccept;
    }

    @Override
    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean goodState = handler.getCurrentState() != EditState.NONE;
        boolean releasedEvent = eventType == EventType.RELEASED;
        boolean noModifiers = !(e.modifiersDown());
        boolean button1 = e.button == MapMouseEvent.BUTTON1;
        boolean onlyButton1Down = e.buttons - (e.buttons & MapMouseEvent.BUTTON1) == 0;

        PrimitiveShape currentShape = handler.getCurrentShape();
        boolean shapeIsSet = currentShape != null;

        if (!(shapeIsSet && goodState && releasedEvent && noModifiers && button1 && onlyButton1Down))
            return false;

        int numPoints = currentShape.getNumPoints();
        boolean ready = numPoints == numOfPointsToAccept;

        return ready;
    }

}
