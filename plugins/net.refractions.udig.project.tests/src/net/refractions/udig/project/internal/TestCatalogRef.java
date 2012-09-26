package net.refractions.udig.project.internal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Ignore;
import org.junit.Test;

public class TestCatalogRef extends AbstractProjectTestCase {
    
    @Ignore
    @Test
    public void testSerialization() throws Exception {
        Layer layer=new LayerDecorator(null){
        	
        	@Override
        	public URL getID() {
        		try {
					return new URL("http://testURL.test"); //$NON-NLS-1$
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
        	}
        	
            @Override
            public CatalogRef getCatalogRef() {
                return new CatalogRef(this);
            }
            
          @Override
        public List<IGeoResource> getGeoResources() {
              List<IGeoResource> list=new ArrayList<IGeoResource>();
              list.add( new IGeoResource(){

                @SuppressWarnings("unchecked") 
                @Override
                public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
                    return (T) service( monitor );
                }
                public IGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
                    return null;
                }
                public <T> boolean canResolve( Class<T> adaptee ) {
                    return false;
                }

                public Status getStatus() {
                    return null;
                }

                public Throwable getMessage() {
                    return null;
                }                
                
                public URL getIdentifier() {
                    try {
                        return new URL("http://testURL.test"); //$NON-NLS-1$
                    } catch (MalformedURLException e) {
                        return null;
                    }
                }
                  
              });
            return list;
        }  
        };
        
        String string=layer.getCatalogRef().toString();
        
        class CatalogRefForTesting extends CatalogRef{
        	public Map<ID, Map<String, Serializable>> getParams(){
        		return connectionParams;
        	}
        };
        CatalogRefForTesting ref=new CatalogRefForTesting();
        ref.parseResourceParameters(string);
        
        assertEquals( 1, ref.getParams().entrySet().size());
        ID url=ref.getParams().keySet().iterator().next();
        Map<String, Serializable> map = ref.getParams().get(url);
        assertEquals( new URL("http://testURL.test").toString(),url.toString()); //$NON-NLS-1$
        assertEquals( 4 ,map.entrySet().size());
        assertEquals( "v1", map.get("k1")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals( "v2", map.get("k2")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals( "v3", map.get("k3")); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
