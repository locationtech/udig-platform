package net.refractions.udig.core.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * PlugIn for net.refractions.udig.core, used by utility classes to access workbench log.
 * 
 * @author jones
 * @since 0.3
 */
public class CorePlugin extends Plugin {

    /** Plugin <code>ID</code> field */
    public static final String ID = "net.refractions.udig.core"; //$NON-NLS-1$
    private static CorePlugin plugin;

    
    
    /**
     * A url stream handler that delegates to the default one but if it doesn't work then it returns null as the stream.
     */
    public final static URLStreamHandler RELAXED_HANDLER=new URLStreamHandler(){

        @Override
        protected URLConnection openConnection( URL u ) throws IOException {
            try{
                URL url=new URL(u.toString());
                return url.openConnection();
            }catch (MalformedURLException e){
                return null;
            }
        }
    };
    
    /**
     * creates a plugin instance
     */
    public CorePlugin() {
        super();
        plugin = this;
    }

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
    }
	
    /**
     * Create a URL from the provided spec; willing to create
     * a URL even if the spec does not have a registered handler.
     * Can be used to create "jdbc" URLs for example.
     *
     * @param spec
     * @return URL if possible
     * @throws RuntimeException of a MalformedURLException resulted
     */
    public static URL createSafeURL( String spec ) {
        try {
            return new URL(null, spec, RELAXED_HANDLER);
        } catch (MalformedURLException e) {
            throw (RuntimeException) new RuntimeException( e );
        }
    }
    /**
     * Create a URI from the provided spec; willing to create
     * a URI even if the spec does not have a registered handler.
     * Can be used to create "jdbc" URLs for example.
     *
     * @param spec
     * @return URI if possible
     * @throws RuntimeException of a URISyntaxException resulted
     */
    public static URI createSafeURI( String spec ){
        try {
            return new URI( spec );
        } catch (URISyntaxException e) {
            throw (RuntimeException) new RuntimeException( e );
        }
    }

    /**
     * Returns the system created plugin object
     * 
     * @return the plugin object
     */
    public static CorePlugin getDefault() {
        return plugin;
    }

    private static volatile MutablePicoContainer blackboard = null;

    /**
     * This is intended to return the top level Pico Container to use for blackBoarding.
     * <p>
     * For most applications, a sub container is required. I recommend the following code be
     * inserted into your main plugin class. <code>
     * private static MutablePicoContainer myContainer = null;
     * 
     * /**
     *  * Gets the container for my plugin.
     *  * 
     *  * Make it 'public' if you want to share ... protected otherwise.
     *  * /
     * public static MutablePicoContainer getMyContainer(){
     *   if(myContainer == null){
     *     // XXXPlugin is the name of the Plugin class
     *     synchronized(XXXPlugin.class){
     *       // check so see that you were not queued for double creation
     *       if(myContainer == null){
     *         // This line does it ... careful to only call it once!
     *         myContainer = CorePlugin.getBlackBoard().makeChildContainer();
     *       }
     *     }
     *   }
     *   return myContainer;
     * }
     * </code>
     * </p>
     * <p>
     * NOTE:<br>
     * Please check to ensure the child you want is not already created (important for two plugins
     * sharing one container).
     * </p>
     * 
     * @return
     */
    public static MutablePicoContainer getBlackBoard() {
        if (blackboard == null) {
            synchronized (CorePlugin.class) {
                if (blackboard == null) {
                    blackboard = new DefaultPicoContainer();
                }
            }
        }
        return blackboard;
    }

    /**
     * Takes a string, and splits it on '\n' and calls stringsToURLs(String[])
     */
    public static List<URL> stringsToURLs( String string ) {
        String[] strings = string.split("\n"); //$NON-NLS-1$

        return stringsToURLs(strings);
    }

    /**
     * Converts each element of an array from a String to a URL. If the String is not a valid URL,
     * it attempts to load it as a File and then convert it. If that fails, it ignores it. It will
     * not insert a null into the returning List, so the size of the List may be smaller than the
     * size of <code>strings</code>
     * 
     * @param strings an array of strings, each to be converted to a URL
     * @return a List of URLs, in the same order as the array
     */
    public static List<URL> stringsToURLs( String[] strings ) {
        List<URL> urls = new ArrayList<URL>();

        for( String string : strings ) {
            try {
                urls.add(new URL(string));
            } catch (MalformedURLException e) {
                // not a URL, maybe it is a file
                try {
                	urls.add( new File(string).toURI().toURL());
                } catch (MalformedURLException e1) {
                    // Not a URL, not a File. nothing to do now.
                }
            }
        }
        return urls;
    }
    
    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log( String message2, Throwable e ) {
        String message=message2;
        if (message == null)
            message = ""; //$NON-NLS-1$
        getDefault().getLog().log(new Status(IStatus.INFO, ID, 0, message, e));
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:
     * 
     * <pre><code>
     * private static final String RENDERING = &quot;net.refractions.udig.project/render/trace&quot;;
     * if (ProjectUIPlugin.getDefault().isDebugging() &amp;&amp; &quot;true&quot;.equalsIgnoreCase(RENDERING)) {
     *     System.out.println(&quot;your message here&quot;);
     * }
     * 
     */
    public static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
    }
    /**
     * Performs the Platform.getDebugOption true check on the provided trace
     * <p>
     * Note: ProjectUIPlugin.getDefault().isDebugging() must also be on.
     * <ul>
     * <li>Trace.RENDER - trace rendering progress
     * </ul>
     * </p>
     * 
     * @param trace currently only RENDER is defined
     */
    public static boolean isDebugging( final String trace ) {
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$    
    }

    public static boolean isDeveloping() {
        return System.getProperty("UDIG_DEVELOPING") != null; //$NON-NLS-1$
    }
}
