/*
 *    Parkinfo
 *    http://qpws/parkinfo
 *
 *    (C) 2011, Department of Environment Resource Management
 *
 *    This code is provided for department use.
 */
package net.refractions.udig.project.listener;

import java.util.List;

import org.eclipse.core.runtime.ListenerList;

import net.refractions.udig.project.interceptor.FeatureInterceptor;

/**
 * @author leviputna
 * 
 */
public class EditFeatureListenerCache {

    private ListenerList listener = new ListenerList();

    public void addListener(EditFeatureListener interceptor) {

    }

    public void removeListener(EditFeatureListener interceptor) {

    }

    public Object[] getListener() {
        return listener.getListeners();
    }

}
