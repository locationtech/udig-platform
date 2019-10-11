package org.locationtech.udig.project.internal.render.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;

import  org.locationtech.udig.project.ILayer;

import com.vividsolutions.jts.geom.Envelope;

public class RenderTask{
    
    private RenderTaskType taskType;
    private Envelope bounds;
    
    private List<ILayer> layers;
    
    /**
     * Original notification event that caused rendering request.
     */
    private Notification event;
    
    public RenderTask(RenderTask task){
        this.taskType = task.getTaskType();
        this.bounds = task.getBounds();
    }
    
    public RenderTask(RenderTaskType taskType, Envelope bounds){
        this.taskType = taskType;
        this.bounds = bounds;
    }
    
    
    public RenderTask(RenderTaskType taskType, ILayer layer){
        this.taskType = taskType;
        this.layers = new ArrayList<ILayer>(4);
        this.layers.add(layer);
    }
    
    public RenderTask(RenderTaskType taskType, ILayer layer, Envelope bounds){
        this.taskType = taskType;
        this.layers = new ArrayList<ILayer>(4);
        this.layers.add(layer);
        this.bounds = bounds;
    }
    
    public RenderTask(RenderTaskType taskType, List<ILayer> layers){
        this.taskType = taskType;
        this.layers = new ArrayList<ILayer>(layers.size() + 4);
        this.layers.addAll(layers);
    }
    
    public RenderTask(RenderTaskType taskType){
        this.taskType = taskType;
    }
    
    
    public Notification getEvent() {
        return event;
    }

    public void setEvent(Notification event) {
        this.event = event;
    }

    public boolean compress(RenderTask anotherTask){
        
        if(this.taskType == RenderTaskType.MAP_BOUNDS_CHANGED){
            return true;
        }
        
        RenderTaskType anotherTaskType = anotherTask.getTaskType();
        if(anotherTaskType == RenderTaskType.MAP_BOUNDS_CHANGED){
            this.taskType = anotherTaskType;
            this.bounds = anotherTask.getBounds();
        }
        switch(anotherTask.getTaskType()){
        case LAYER_ADDED:
            return false;
//            
//            if(this.taskType == RenderTaskType.LAYER_ADDED){
//                return;
//            }else if(this.taskType == RenderTaskType.LAYER_REMOVED){
//            }
            
        case LAYER_REMOVED:
            return false;
        case LAYER_STATE_CHANGED:
            if(this.taskType == RenderTaskType.LAYER_STATE_CHANGED){
                if(this.bounds == null && anotherTask.getBounds() == null){
                    this.layers.addAll(anotherTask.getLayers());
                    return true;
                }
            }
            break;
        default:
                
        }
        
        return false;
    }
    
    public RenderTaskType getTaskType() {
        return taskType;
    }
    
    
    
    public Envelope getBounds() {
        return bounds;
    }
    
    public List<ILayer> getLayers() {
        return layers;
    }
}