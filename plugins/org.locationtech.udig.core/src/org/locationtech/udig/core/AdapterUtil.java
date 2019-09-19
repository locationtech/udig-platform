/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.core;

import java.io.IOException;

import org.locationtech.udig.core.internal.CorePlugin;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.geotools.data.shapefile.ShapefileDataStore;

/**
 * Methods to help manage adapters
 * @author jones
 * @since 1.1.0
 */
public class AdapterUtil {
    
    /**
     * Singleton AdapterUtil field: <code>AdapterUtil.instance.adaptTo(
     * ShapefileDataStore.class, object, monitor)</code>
     */
    public static final AdapterUtil instance = new AdapterUtil();
    /**
     * Please use singleton: <code>AdapterUtil.instance</code>
     */
	private AdapterUtil(){}
    
    /**
     * Determines if a class can be adapted based on its string representation.  
     *
     * @param targetClass class name to check if adaptation can go to
     * @param obj source object to adapt from
     * @return
     */
    public boolean canAdaptTo(String targetClass, Object obj) {
        if (obj == null) return false;
        ClassLoader classLoader = obj.getClass().getClassLoader();
        return canAdaptTo(targetClass, obj, classLoader);
    }
    
    /**
     * Determines if a object can be adapted based on its string representation.  
     *
     * @param targetClass class name to check if adaptation can go to
     * @param obj source object to adapt from
     * @param classLoader object to instantiate the class with
     * @return
     */
    public boolean canAdaptTo(String targetClass, Object obj, ClassLoader classLoader) {
        Class< ? > target=null;
        if (obj == null) return false;
        if (classLoader == null) {
            classLoader = obj.getClass().getClassLoader();
        }
        try {
            target = classLoader.loadClass(targetClass);
        } catch (Throwable e) {
            //do nothing this is ok.
            return false;
        }
        if( target.isAssignableFrom(obj.getClass()) )
            return true;
        
        return canAdaptTo(obj, target);
    }

    /**
     * Tests whether the object can be adapted to the provided target class.
     * 
     * @param obj the class to test
     * @param target the target/desired class
     * 
     * @return true if the object is an instanceof or can adapt to
     */
    public boolean canAdaptTo( Object obj, Class< ? > target ) {
        
        if (obj == null) {
            return false;
        }
        
        //see if platform can adapt the object
        if (Platform.getAdapterManager().hasAdapter(obj, target.getName()) )
            return true;
        
        // see if the object is a blocking adaptable object and can adapt to the correct class type
        IBlockingAdaptable blockingAdaptable=getBlockingAdapter(obj);
        if( blockingAdaptable!=null && blockingAdaptable.canAdaptTo(target))
            return true;
        
        // see if the object is adaptable and can adapt to the class type.
        IAdaptable adaptable=getAdaptable(obj);
        if( adaptable!=null ){
            return adaptable.getAdapter(target)!=null;
        }
        return false;
    }
    
    /**
     * Adapt the object to an IAdaptable
     */
    public IAdaptable getAdaptable(Object obj) {
        if( obj instanceof IAdaptable ){
            return (IAdaptable) obj;
        }
        if (Platform.getAdapterManager().hasAdapter(obj, IAdaptable.class.getSimpleName()) )
            return (IAdaptable) Platform.getAdapterManager().getAdapter(obj, IAdaptable.class);
        return null;
    }

    public IBlockingAdaptable getBlockingAdapter(Object obj) {
        if( obj instanceof IBlockingAdaptable ){
            return (IBlockingAdaptable) obj;
        }
        if (Platform.getAdapterManager().hasAdapter(obj, IBlockingAdaptable.class.getName()) )
            return (IBlockingAdaptable) Platform.getAdapterManager().getAdapter(obj, IBlockingAdaptable.class);
        if (obj instanceof IAdaptable) {
            IAdaptable adapter = (IAdaptable) obj;
            return (IBlockingAdaptable) adapter.getAdapter(IBlockingAdaptable.class);
        }
        return null;
    }

    /**
     * Since the target object may not be the object that the operation actually operates on, the
     * getOperationTarget() finds the real object and returns it or null if for some reason the
     * operation can be performed on the target.
     * <p>
     * Example:
     * </p>
     * A SimpleFeatureType readonly operation is called on a IResolve. The getOperationTarget would
     * resolve(FeatureSource.class) and return the SimpleFeature Source for the operation.
     * 
     * @param target the object the action is called on.
     * @param definition the Configuration element definition of the operation.
     * @param monitor The progress monitor for the operation job.
     * @return
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
    public <T> T adapt( String targetClass, Object obj, IProgressMonitor monitor ) throws IOException{
        Class< T > target=null;
        
        if (null == obj) {
            return null;
        }
        try {
            target=(Class<T>) obj.getClass().getClassLoader().loadClass(targetClass);
            return adaptTo(target, obj, monitor);
        } catch (ClassNotFoundException e) {
            //do nothing.
            CorePlugin.log( "This exception is bad.  The framework should not allow this to be reached.",e); //$NON-NLS-1$
            return null;
        }
        
        
         
    }

    /**
     * Since the target object may not be the object that the operation actually operates on, the
     * getOperationTarget() finds the real object and returns it or null if for some reason the
     * operation can be performed on the target.
     * <p>
     * Example:
     * </p>
     * A SimpleFeatureType readonly operation is called on a IResolve. The getOperationTarget would
     * resolve(FeatureSource.class) and return the SimpleFeature Source for the operation.
     * 
     * @param target the object the action is called on.
     * @param definition the Configuration element definition of the operation.
     * @param monitor The progress monitor for the operation job.
     * @return
     * @throws IOException 
     */
    public <T> T adaptTo( Class<T> target, Object obj, IProgressMonitor monitor ) throws IOException{
        
            if( target.isAssignableFrom(obj.getClass()) )
                return target.cast(obj);
        if (Platform.getAdapterManager().hasAdapter(obj, target.getName()) ){
            return target.cast(Platform.getAdapterManager().loadAdapter(obj, target.getName()));
        }
        IBlockingAdaptable blockingAdaptable=getBlockingAdapter(obj);
        if( blockingAdaptable!=null && blockingAdaptable.canAdaptTo(target))
            return blockingAdaptable.getAdapter(target, monitor);
        if (obj instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) obj;
            return target.cast(adaptable.getAdapter(target));
        }
        return null;
    }

}
