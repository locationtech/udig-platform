/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.tools.jgrass.orientationview;

import java.awt.Color;
import java.awt.Rectangle;

import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseListener;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseWheelListener;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;

import org.eclipse.core.runtime.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;

/**
 * Arrow drawing class. Adapted from udig's MessageBubble class.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ArrowDrawCommand extends AbstractDrawCommand {

    private static GeometryFactory G = new GeometryFactory();
    private Coordinate start;
    private Coordinate end;
    private Rectangle validArea;
    private Color textColor = new Color(255, 0, 0, 167);

    /**
     * @param row upperLeft of message
     * @param col upperLeft of message
     * @param message message to display
     * @param delay the length of time to show the message
     */
    public ArrowDrawCommand( final Coordinate start, final Coordinate end ) {
        this.start = start;
        this.end = end;
    }

    public void run( IProgressMonitor monitor ) throws Exception {

        display.addMouseListener(mouseListener);
        display.addMouseWheelListener(wheelListener);

        LineSegment l = new LineSegment(start, end);
        end = l.pointAlong(0.95);
        Coordinate along = l.pointAlong(0.8);

        LineString ls = G.createLineString(new Coordinate[]{end, along});
        Geometry buffer = ls.buffer(l.getLength() / 10.0);

        validArea = new Rectangle((int) start.x, (int) start.y, (int) (start.x + (end.x - start.x)),
                (int) (start.y + (end.y - start.y)));

        graphics.setColor(textColor);
        graphics.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);

        Coordinate[] coords = buffer.getCoordinates();
        if (coords.length == 0) {
            return;
        }
        graphics.drawLine((int) end.x, (int) end.y, (int) coords[0].x, (int) coords[0].y);
        // graphics.drawLine((int) end.x, (int) end.y, (int) coords[4].x, (int) coords[4].y);

    }
    public Rectangle getValidArea() {
        return validArea;
    }

    public void setValid( boolean valid ) {
        super.setValid(valid);
        display.removeMouseListener(mouseListener);
        display.removeMouseWheelListener(wheelListener);
    }

    private MapMouseListener mouseListener = new MapMouseListener(){

        public void mouseDoubleClicked( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }

        public void mouseEntered( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }

        public void mouseExited( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }

        public void mousePressed( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }

        public void mouseReleased( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }

    };

    private MapMouseWheelListener wheelListener = new MapMouseWheelListener(){

        public void mouseWheelMoved( MapMouseWheelEvent e ) {
            disable(display, this);
        }

    };

    void disable( ViewportPane pane, Object listener ) {
        if (!isValid(pane)) {
            if (listener instanceof MapMouseMotionListener)
                pane.removeMouseMotionListener((MapMouseMotionListener) listener);
            else if (listener instanceof MapMouseListener) {
                pane.removeMouseListener((MapMouseListener) listener);
            } else if (listener instanceof MapMouseWheelListener) {
                pane.removeMouseWheelListener((MapMouseWheelListener) listener);
            }

            return;
        }

        setValid(false);
        pane.repaint();
    }

    private boolean isValid( IMapDisplay source ) {
        if (!ArrowDrawCommand.this.isValid())
            return false;
        if (source != display)
            return false;
        return true;
    }

}
