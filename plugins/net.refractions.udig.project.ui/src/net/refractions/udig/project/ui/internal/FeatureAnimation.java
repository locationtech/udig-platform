package net.refractions.udig.project.ui.internal;

import java.awt.Rectangle;
import java.util.List;

import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.commands.IDrawCommand;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Creates an animation that flashes 3 times
 * 
 * @author jesse
 * @since 1.1.0
 */
public class FeatureAnimation extends AbstractDrawCommand implements IAnimation {

    private int runs = 0;
    private final List<? extends IDrawCommand> commands;
    private final Rectangle validArea;

    public FeatureAnimation( List<? extends IDrawCommand> commands, Rectangle validArea ) {
        super();
        this.commands = commands;
        this.validArea = validArea;
    }

    public short getFrameInterval() {
        return 300;
    }

    public void nextFrame() {
        runs++;
    }

    public boolean hasNext() {
        return runs < 6;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        if (runs % 2 == 0) {
            for( IDrawCommand command : commands ) {
                command.setGraphics(graphics, display);
                command.setMap(getMap());
                command.run(monitor);
            }
        }
    }

    public Rectangle getValidArea() {
        return validArea;
    }
    
    public void setValid(boolean valid){
        super.setValid(valid);
        for( IDrawCommand command : commands ) {
            command.setValid(valid);
        }
    }

}