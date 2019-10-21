/**
 * 
 */
package org.locationtech.udig.project.ui;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;

/**
 * Responsible for updating the viewport when animations need their next animation frame to be displayed.
 * 
 * @author jeichar
 */
public class AnimationUpdater {

    private static boolean testing=false;
    final List<IAnimation> animations=new CopyOnWriteArrayList<IAnimation>();
    private ViewportPane display;
    private short frameInterval;

    public AnimationUpdater( ViewportPane display, short frameInterval) {
        this.display = display;
        this.frameInterval=frameInterval;
    }
    
    public void run() {
        List<IAnimation> toRemove = new LinkedList<IAnimation>();
        for ( Iterator<IAnimation> iter=animations.iterator(); iter.hasNext(); ){
            IAnimation anim=iter.next();
            if( anim.hasNext())
                anim.nextFrame();
            else{
                anim.setValid(false);
                anim.dispose();
                toRemove.add(anim);
            }
            
            animations.removeAll(toRemove);
            if( isTesting() ){
                try {
                    anim.run(new NullProgressMonitor());
                } catch (Exception e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
            }
        }
        if( !isTesting() )
            display.repaint();
            
        if( animations.size()==0 ){
            remove(this);
        }else{
            final RunUpdaters next = next();
            if (next != null) {
                final short elapseTime;
                if (next.frameinterval <= frameInterval) {
                    elapseTime=next.frameinterval;
                }else{
                    elapseTime=(short)(next.frameinterval-frameInterval);
                }
                runTimer(elapseTime, next);
            }
        }
    }

    
    /**
     * Should only be used for testing
     */
    static boolean isTesting() {
        return testing;
    }

    /**
     * should only be used for testing purposes.
     *
     * @param testing
     */
    public static void setTesting(boolean isTesting){
        testing=isTesting;
    }
    private static void runTimer(final short frameInterval, final RunUpdaters next ) {
        final Display display;
        if (Display.getCurrent() == null) {
            display = Display.getDefault();
            display.asyncExec(new Runnable(){
                public void run() {
                    Display.getCurrent().timerExec(frameInterval, next);
                }
            });
        } else {
            display = Display.getCurrent();
            display.timerExec(frameInterval, next);
        }
    }

    private RunUpdaters next() {
        synchronized (AnimationUpdater.class) {
            SortedMap<Short, List<AnimationUpdater>> tailMap = displayToTaskMap.tailMap(frameInterval);
            if( tailMap.isEmpty() ){
                if( displayToTaskMap.size()>0 )
                    return new RunUpdaters( displayToTaskMap.firstKey() );
                return new RunUpdaters(frameInterval);
            }
            
            return new RunUpdaters(tailMap.keySet().iterator().next());
        }
    }
    
    private static class RunUpdaters implements Runnable{
        
        private short frameinterval;

        public RunUpdaters( short frameInterval ) {
            this.frameinterval=frameInterval;
        }

        public void run() {
            synchronized (AnimationUpdater.class) {
                List<AnimationUpdater> updaters = displayToTaskMap.get(frameinterval);
                for( AnimationUpdater updater : updaters ) {
                    if (!updater.display.isDisposed() && updater.display.isVisible()){
                        updater.run();
                    }else{
                    	//we want to remove this updater
                    	for (IAnimation anim: updater.getAnimations()){
                    		anim.setValid(false);
                    		anim.dispose();
                    	}
                    	updater.remove(updater);
                    }
                }
            }
        }
    }

    private void remove( AnimationUpdater updater ) {
        synchronized (AnimationUpdater.class) {
            List<AnimationUpdater> hashMap = displayToTaskMap.get(updater.frameInterval);
            if( hashMap!=null ){
                hashMap.remove(updater);
                if( hashMap.isEmpty() )
                    displayToTaskMap.remove(frameInterval);
            }
        }
    }

    // Locked by AnimationUpdater.class
    private static TreeMap<Short, List<AnimationUpdater>> displayToTaskMap=new TreeMap<Short, List<AnimationUpdater>>();

//    private static final Timer TIMER = new Timer("Animation Timer", true); //$NON-NLS-1$
    private static final short MIN_INTERVAL=100;
    public synchronized static void runTimer( IMapDisplay mapDisplay, IAnimation animation ) {
        if(  !ProjectPlugin.getPlugin().getPreferenceStore().getBoolean(PreferenceConstants.P_SHOW_ANIMATIONS)){
            return;
        }
        
        if (!(mapDisplay instanceof ViewportPane)) {
            ProjectUIPlugin.log("Map Display provided is not a ViewportPane and therefore does not" //$NON-NLS-1$
                    + " support animation", new RuntimeException()); //$NON-NLS-1$
            return;
        }
        
        short frameInterval = calculateFrameInterval(animation);
        ViewportPane viewport=(ViewportPane) mapDisplay;
        
        // Try to get the map from animationInterval to the AnimationUpdater for that interval
        List<AnimationUpdater> tasks=displayToTaskMap.get(frameInterval);
        boolean requiresRun=false;
        
        if (tasks == null) {
            requiresRun=true;
            tasks = new CopyOnWriteArrayList<AnimationUpdater>();
            displayToTaskMap.put(frameInterval, tasks);
        }

        AnimationUpdater updater = findUpdater(frameInterval,  viewport);
        viewport.addDrawCommand(animation);

        if (updater != null) {
            updater.getAnimations().add(animation);
        } else {
            AnimationUpdater task = new AnimationUpdater((ViewportPane) viewport, frameInterval);
            task.getAnimations().add(animation);
            tasks.add(task);
            if( requiresRun ){
                runTimer(frameInterval, new RunUpdaters(frameInterval));
            }
        }
        
        Rectangle bounds = animation.getValidArea();
        if( bounds==null ){
            viewport.repaint();
        }else{
            viewport.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private static AnimationUpdater findUpdater( short frameInterval2, ViewportPane viewport ) {
        List<AnimationUpdater> updaters = displayToTaskMap.get(frameInterval2);
        if( updaters==null)
            return null;
        for( AnimationUpdater updater : updaters ) {
            if( updater.display==viewport)
                return updater;
        }
        return null;
    }

    private static short calculateFrameInterval( IAnimation animation ) {
        short frameInterval = animation.getFrameInterval();

        int i = (frameInterval - (MIN_INTERVAL * (frameInterval / MIN_INTERVAL)));
        frameInterval = (short) (frameInterval - i);
        if (frameInterval < MIN_INTERVAL)
            frameInterval = MIN_INTERVAL;
        return frameInterval;
    }

    /**
     * @return Returns the animations.
     */
    public List<IAnimation> getAnimations() {
        return animations;
    }

    /**
     * @return Returns the frameInterval.
     */
    public short getFrameInterval() {
        return frameInterval;
    }
}
