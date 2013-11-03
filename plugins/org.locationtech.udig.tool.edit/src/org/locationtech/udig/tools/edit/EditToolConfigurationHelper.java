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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class provides methods so that the process of initializing an Edit tool's
 * {@link EventBehaviour}s is done declaratively.
 * 
 * @author jones
 * @since 1.1.0
 */
public class EditToolConfigurationHelper {
    
    private List<EventBehaviour> behaviours;
    private Stack<Map> lists=new Stack<Map>();
    private List<EventBehaviour> current;
    private ListType currentType=ListType.NONE;
    private boolean done=false;
    private List<EventBehaviour> elseList;

    public EditToolConfigurationHelper(List<EventBehaviour> behaviours){
        this.behaviours=behaviours;
        current=behaviours;
    }
    /**
     * Starts building a list of behaviours that will be mutually exclusive.  IE only the first behviour
     * that is valid will run.
     */
    public void startMutualExclusiveList(){
        pushCurrentList();
        currentType=ListType.EXCLUSIVE;
        current=new ArrayList<EventBehaviour>();
    }
    /**
     * Signals the end of a Mutually Exclusive list of behaviours
     */
    public void stopMutualExclusiveList(){
        if( currentType!=ListType.EXCLUSIVE )
            throw new IllegalStateException("A mutual exclusive list is not the current type of list being" + //$NON-NLS-1$
                    "created: "+currentType); //$NON-NLS-1$
        
        MutualExclusiveEventBehavior mutualExclusiveBehavior = new MutualExclusiveEventBehavior(current);
        popLastList(mutualExclusiveBehavior);
    }
    /**
     * Starts building a list of behaviours that are ordered but all behaviours are run.
     * @param processAsCommand If true then the behaviours will be processed in the command execution thread instead of the Display thread.
     * This is sometimes necessary if a command from a previous behaviour may change the state that a later command depends on.  
     * However it depends on the implementation of the behaviour, many behaviours pre-execute their commands in the display thread so 
     * do some research before using this as is can cause a slower response to the user.
     */
    public void startOrderedList(boolean processAsCommand){
        pushCurrentList();
        currentType=processAsCommand?ListType.ORDERED_COMMAND:ListType.ORDERED;
        current=new ArrayList<EventBehaviour>();        
    }
    
    public void stopOrderedList(){
        if( currentType!=ListType.ORDERED && currentType!=ListType.ORDERED_COMMAND)
            throw new IllegalStateException("A mutual exclusive list is not the current type of list being" + //$NON-NLS-1$
                    "created: "+currentType); //$NON-NLS-1$
        
        OrderedCompositeEventBehavior orderedCompositeBehaviour = new OrderedCompositeEventBehavior(current,
                currentType==ListType.ORDERED_COMMAND);
        popLastList(orderedCompositeBehaviour);
    }
    /**
     * Add a behaviours to the current list
     *
     * @param behaviour the behaviour to add
     */
    public void add(EventBehaviour behaviour){
        current.add(behaviour);
    }
    /**
     * Signals that the configuration is complete.  It checks that all lists where correctly signalled as done.
     * An exception will be thrown if done is not called
     *
     */
    public void done(){
        done=true;
        if( currentType!=ListType.NONE)
            throw new IllegalStateException("There are still "+lists.size()+"lists not finished, " + //$NON-NLS-1$ //$NON-NLS-2$
                    "the current list is a "+currentType+" list");  //$NON-NLS-1$//$NON-NLS-2$
    }
   private void popLastList( EventBehaviour behavior ) {
       if( lists.isEmpty() ){
           behaviours.add(behavior);
           current=behaviours;
           currentType=ListType.NONE;
       }else{
           Map map=lists.pop();

           map.list.add(behavior);
           current=map.list;
           currentType=map.type;
       }
   }
    private void pushCurrentList() {
        if( current!=behaviours ){
            lists.push(new Map(currentType,current));
        }
    }
    enum ListType{
        EXCLUSIVE, ORDERED, NONE, ADVANCED, ELSE, ORDERED_COMMAND
    }
    
    static class Map{
        final ListType type;
        final List<EventBehaviour> list;
        Map(ListType type, List<EventBehaviour> list){
            this.type=type;
            this.list=list;
        }
    }

    /**
     * Returns true if the done() was called;
     * @return
     */
    public boolean isDone() {
        return done;
    }
    /**
     * Behaviours that are added after this method is called will only be active when the advanced editing 
     * is active.
     * <p> The normal behaviour must either be outside of the Advanced list or in the Else List</p>
     * 
     * @see #startElseFeatures()
     */
    public void startAdvancedFeatures() {
        pushCurrentList();
        currentType=ListType.ADVANCED;
        current=new ArrayList<EventBehaviour>();
    }
    /**
     * Ends Advanced behaviour section
     */
    public void stopAdvancedFeatures() {
        if( currentType!=ListType.ADVANCED )
            throw new IllegalStateException("A advanced behaviours list is not the current type of list being" + //$NON-NLS-1$
                    "created: "+currentType); //$NON-NLS-1$
        
        AdvancedFeaturesEventBehavior behavior = new AdvancedFeaturesEventBehavior(current);
        if( elseList!=null ){
            behavior.setElse(elseList);
            elseList=null;
        }
        popLastList(behavior);
    }
    /**
     * Starts the Else Behaviours of the Advanced configuration.  The Else Behaviours are the behaviours that only run when Not in advanced
     * mode.  The Advanced System has the "Advanced Behaviours", which are ran only when in advanced mode and the "Else Behaviours", which run 
     * only when not in Advanced Mode.  
     * <p>StartElseFeatures() must be called after {@link #startAdvancedFeatures()} has been called but before {@link #stopAdvancedFeatures()}
     * is called </p>
     */
    public void startElseFeatures() {
        if( currentType!=ListType.ADVANCED )
            throw new IllegalStateException("Else Features can only be added to Advanced feature list.  Current" + //$NON-NLS-1$
                    " list is: "+currentType); //$NON-NLS-1$

        pushCurrentList();
        currentType=ListType.ELSE;
        current=new ArrayList<EventBehaviour>();
        elseList=current;
    }
    /**
     * Ends the declaration of the Else Behaviours
     */
    public void stopElseFeatures() {
        if( currentType!=ListType.ELSE )
            throw new IllegalStateException("Else is not the current type of list being" + //$NON-NLS-1$
                    "added. StartElseFeatures() must be called first.  Current type is: "+currentType); //$NON-NLS-1$
        
        Map map=lists.pop();
        currentType=map.type;
        current=map.list;
    }
}
