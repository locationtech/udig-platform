/**
 * 
 */
package org.locationtech.udig.tutorials.examples;

import java.awt.Rectangle;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.project.ui.IAnimation;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This example is a map graphic that uses an Animation to display itself. It
 * keeps the state information on the layer blackboard and the animation is
 * aware of the state. The MapGraphic's primary responsibility is to start the
 * Animation if it is not running.
 * 
 * @author Jesse
 */
public class AnimationMapGraphic implements MapGraphic {

    private CircleAnimation animation;

    public void draw(MapGraphicContext context) {
        if (this.animation == null) {
            ILayer layer = context.getLayer();
            IBlackboard blackboard = layer.getBlackboard();
            blackboard.putInteger("x", 0);
            blackboard.putInteger("y", 50);
            this.animation = new CircleAnimation(layer, context);
            AnimationUpdater.runTimer(context.getMapDisplay(), animation);
        }

    }

    private class CircleAnimation extends AbstractDrawCommand implements
            IAnimation {

        private ILayer layer;
        private MapGraphicContext context;

        public CircleAnimation(ILayer layer, MapGraphicContext context) {
            this.layer = layer;
            this.context = context;
        }

        public short getFrameInterval() {
            return 100;
        }

        public boolean hasNext() {
            return true;
        }

        public void nextFrame() {
            IBlackboard blackboard = layer.getBlackboard();
            int x = blackboard.getInteger("x");
            x += 30;
            if (x > display.getWidth()) {
                x = 0;
            }
            blackboard.putInteger("x", x);
        }

        public void run(IProgressMonitor monitor) throws Exception {
            // only draw if layer is visible
            if( layer.isVisible() ){
                IBlackboard blackboard = layer.getBlackboard();
                int x = blackboard.getInteger("x");
                int y = blackboard.getInteger("y");
    
                graphics.drawOval(x, y, 30, 30);
            }

        }

        public Rectangle getValidArea() {
            // to be more efficient we could set the area to draw so only the
            // area affected by this animation would be updated in the map
            // display but
            // for simplicity lets just trigger the entire viewport to redraw
            // (note that this is not
            // a re-render.
            return null;
        }

        @Override
        public void dispose() {
                // we don't want to be disposed so lets run again
                AnimationUpdater.runTimer(context.getMapDisplay(), this);
        }

    }

}
