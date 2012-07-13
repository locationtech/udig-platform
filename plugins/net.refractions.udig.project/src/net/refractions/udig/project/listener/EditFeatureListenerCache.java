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

import net.refractions.udig.project.interceptor.FeatureInterceptor;

/**
 * @author leviputna
 * 
 */
public class EditFeatureListenerCache {

    private List<FeatureInterceptor> interceptors;

    public void addFeatureInterceptor(FeatureInterceptor interceptor) {

    }

    public void removeFeatureInterceptor(FeatureInterceptor interceptor) {

    }

    public List<FeatureInterceptor> getFeatureInterceptor() {
        return interceptors;
    }

}
