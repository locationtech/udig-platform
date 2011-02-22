/**
 *
 */
package net.refractions.udig.catalog;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.core.internal.CorePlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Allows IService connection parameters to be stored
 * into a preference object - and for restoring Service
 * again.
 * <p>
 * IResolves that are not services will be ignored; the default
 * implementation of locateService( URL, Map ) will add
 * the created services into the provided catalog.
 * <p>
 * {@link #locateService(URL, Map)} can be overridden to not create and
 * add the service to the catalog.
 * </p>
 * @author Jesse
 */
public class ServiceParameterPersister {
	private static final String VALUE_ID = "value"; //$NON-NLS-1$
	private static final String TYPE_ID = "type"; //$NON-NLS-1$
    private static final String ENCODING = "UTF-8"; //$NON-NLS-1$

	protected final ICatalog localCatalog;
	protected final IServiceFactory serviceFactory;
	private File reference;

	public ServiceParameterPersister(final ICatalog localCatalog, final IServiceFactory serviceFactory) {
		this(localCatalog, serviceFactory, null);
	}

	public ServiceParameterPersister(final ICatalog localCatalog, final IServiceFactory serviceFactory, File reference) {
		super();
		this.localCatalog = localCatalog;
		this.serviceFactory = serviceFactory;
		this.reference=reference;
	}
	/**
	 * Using the conection parameter information in the preferences node
	 * restore the state of the local catalog.
	 *
	 * @param node
	 */
	public void restore(Preferences node) {
		try {
			for (String id : node.childrenNames()) {
				try {
					URL url = toURL(id);
					Preferences service = node.node(id);
					String[] keys = service.keys();

					// BACKWARDS COMPATIBILITY
					Map<String, Serializable> connectionParams = backwardCompatibleRestore(
							service, keys);

					String[] nodes = service.childrenNames();
					for (String childName : nodes) {
						mapAsObject(service, connectionParams, childName);
					}
					locateService(url, connectionParams);
				} catch (Throwable t) {
					CatalogPlugin.log(null, new Exception(t));
				}
			}

		} catch (Throwable t) {
			CatalogPlugin.log(null, new Exception(t));
		}
	}
	/**
	 * Convert a persisted id string into a URL.
	 * <p>
	 * This method will decode the string based ENCODING
	 * @param id Persisted id string
	 * @return URL based on provided id string
	 * @throws MalformedURLException If the id could not be decoded into a valid URL
	 */
    private URL toURL( String id ) throws MalformedURLException {
        URL url;
        try {
        	url = new URL(null, URLDecoder.decode(id, ENCODING), CorePlugin.RELAXED_HANDLER);
        } catch (UnsupportedEncodingException e) {
        	CatalogPlugin.log("Could not code preferences URL", e); //$NON-NLS-1$
        	throw new MalformedURLException(e.toString());
        }
        return url;
    }

	/**
	 * Helper method that will unpack a servicePreference node
	 * into a map of connection parameters.
	 * @param service
	 * @param keys
	 * @return Connection parameters
	 */
	private Map<String, Serializable> backwardCompatibleRestore(Preferences servicePreference, String[] keys) {
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		for( int j = 0; j < keys.length; j++ ) {
			String currentKey = keys[j];
			map.put(currentKey, servicePreference.get(currentKey, null));
		}
		return map;
	}

	/**
	 * Create an IService from the provided connection parameters
	 * and add them to the provided catalog.
	 *
	 * @Param targetID In the event of a tie favour the provided targetID
	 * @param connectionParameters Used to to ask the ServiceFactory for list of candidates
	 */
	protected void locateService(URL targetID, Map<String, Serializable> connectionParameters) {
		List<IService> newServices = serviceFactory.createService(connectionParameters);
		if( !newServices.isEmpty() ){
			for( IService service : newServices ) {
			    // should we check the local catalog to see if it already
			    // has an entry for this service?
			    IService found = localCatalog.getById( IService.class,service.getIdentifier(), null );
			    if( found == null ){
			        localCatalog.add(service);
			    }
			    else {
			        // Service was already available
			    }
		    }
		} else {
			CatalogPlugin.log("Nothing was able to be loaded from saved preferences: "+connectionParameters, null); //$NON-NLS-1$
		}
	}
	/**
	 * Performs some post processing on the connection parameters to ensure
	 * they are prompted from Strings to Objects (if possible).
	 *
	 * @param servicePreferenceNode
	 * @param connectionParams
	 * @param currentKey
	 * @throws MalformedURLException
	 */
	@SuppressWarnings("unchecked")
    private void mapAsObject(Preferences servicePreferenceNode, Map<String, Serializable> connectionParams, String currentKey) throws MalformedURLException {
		Preferences paramNode = servicePreferenceNode.node(currentKey);
		String value=paramNode.get(VALUE_ID, null);
		try {
			value = URLDecoder.decode(value, ENCODING);
		} catch (UnsupportedEncodingException e) {
			CatalogPlugin.log("error decoding value, using undecoded value", e); //$NON-NLS-1$
		}
		String type=paramNode.get(TYPE_ID, null);
		try{
			Class clazz=Class.forName(type);

			// reference can be null so only decode relative path if reference is not null.
			// ie assume the URL/File is absolute if reference is null
			if( reference !=null && (URL.class.isAssignableFrom(clazz)
					|| File.class.isAssignableFrom(clazz) )){
				URL result = URLUtils.constructURL(this.reference, value);
				if( URL.class.isAssignableFrom(clazz) )
					connectionParams.put(currentKey, (Serializable) result);
				else
					connectionParams.put(currentKey, new File( result.getFile()));
				return;
			}

			try{
				// try finding the constructor that takes a string
				Constructor constructor = clazz.getConstructor(new Class[]{String.class});
				Object object = constructor.newInstance(new Object[]{value});
				connectionParams.put(currentKey, (Serializable) object);
			}catch(Throwable t){
				//failed lets try a setter
				try{
					Method[] methods = clazz.getMethods();
					Method bestMatch = findBestMatch(methods);

					if( bestMatch!=null ){
						Object obj = clazz.newInstance();
						bestMatch.invoke(obj, new Object[]{value});
						connectionParams.put(currentKey, (Serializable) obj);
					}
				}catch (Throwable t2) {
					CatalogPlugin.log("error that occurred when trying use construction with string: "+type+" value= "+value, t ); //$NON-NLS-1$ //$NON-NLS-2$
					CatalogPlugin.log("error that occurred when use a setter: "+type+" value= "+value, t2 );  //$NON-NLS-1$//$NON-NLS-2$
				}
			}

		}catch(ClassNotFoundException cnfe){
			CatalogPlugin.log(type+" was not able find declared type so we're putting it in to the parameters as a String", null); //$NON-NLS-1$
			connectionParams.put(currentKey, value);
		}
	}

	private Method findBestMatch(Method[] methods) {
		Method bestMatch=null;
		for (Method method : methods) {
			Class<?>[] methodParams = method.getParameterTypes();
			if( methodParams.length==1 && methodParams[0].isAssignableFrom(String.class) ){
				// is this a setter or a parse?
				if( method.getName().startsWith("parse") ){ //$NON-NLS-1$
					if( bestMatch==null ){
						bestMatch=method;
						continue;
					}else{
						if( bestMatch.getName().startsWith("set") ){ //$NON-NLS-1$
							bestMatch=method;
							continue;
						}
					}
				}

				if( method.getName().startsWith("set") ){ //$NON-NLS-1$
					if( bestMatch==null ){
						bestMatch=method;
						continue;
					}
				}
			}
		}
		return bestMatch;
	}


	public void store(IProgressMonitor monitor, Preferences node,
			Collection<? extends IResolve> resolves ) throws BackingStoreException, IOException {
		clearPreferences(node);
        for( IResolve member : resolves ) {
            try {
                if( !CatalogPlugin.getDefault().getPreferenceStore().getBoolean("SaveTemporaryDataTypes")  //$NON-NLS-1$
                        && member.canResolve(ITransientResolve.class ) )
                    continue;
                IService service=null ;
                if( member instanceof IGeoResource ){
                	service=((IGeoResource)member).service(monitor);
                }else if( member instanceof IService ){
                	service=(IService)member;
                }
                // its not a type that we know how to get the parameters from
                if( service==null )
                	continue;

                String id;
				try {
                    id = URLEncoder.encode(service.getIdentifier().toExternalForm(), ENCODING);
                } catch (UnsupportedEncodingException e1) {
                    // should never happen
                    CatalogPlugin.log(null, e1);
                    throw new BackingStoreException(e1.toString());
                }

                Preferences serviceNode = node.node(id);

                for ( Map.Entry<String, Serializable> entry : service.getConnectionParams().entrySet()) {
                    String key = entry.getKey().toString();

                    Serializable object = entry.getValue();
                    URL url=null;
                    if( object instanceof URL){
                    	url = (URL) object;
                    }else if( object instanceof File ){
                    	url=((File)object).toURL();
                    }

                    String value;
                    // if reference is null then we can only encode the absolute path
                    if( reference!=null && url !=null ){
                    	URL relativeURL = URLUtils.toRelativePath(this.reference, url);
                    	value = relativeURL.toExternalForm();
                    }else{
                    	value = object == null ? null : object.toString();
                    }

                    if (value != null){
                    	value= URLEncoder.encode( value, ENCODING );
                        Preferences paramNode = serviceNode.node(key);
                        paramNode.put(VALUE_ID, value);
                        paramNode.put(TYPE_ID, object.getClass().getName());
                    }
                }
                if (serviceNode.keys().length > 0)
                    serviceNode.flush();
                monitor.worked(1);
            } catch (RuntimeException e) {
                CatalogPlugin.log("Error storing: "+member.getIdentifier(), e); //$NON-NLS-1$
            }
        }
        node.flush();
	}

    private void clearPreferences( Preferences node ) throws BackingStoreException {
        for( String name : node.childrenNames() ) {
            Preferences child = node.node(name);
            child.removeNode();
        }
    }


}
