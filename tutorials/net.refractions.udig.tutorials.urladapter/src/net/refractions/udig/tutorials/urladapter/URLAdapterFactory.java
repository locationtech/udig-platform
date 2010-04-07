package net.refractions.udig.tutorials.urladapter;

import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.IAdapterFactory;

public class URLAdapterFactory implements IAdapterFactory {

    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if( adapterType==URL.class ){
            Class<?> adaptableClass = adaptableObject.getClass();
            if(IGeoResource.class.isAssignableFrom(adaptableClass)) {
                return ((IGeoResource)adaptableObject).getIdentifier();
            } else if(IService.class.isAssignableFrom(adaptableClass)) {
                return ((IService)adaptableObject).getIdentifier();
            }
        }
        return null;
    }

    public Class<?>[] getAdapterList() {
        return new Class[]{URL.class} ;
    }

}
