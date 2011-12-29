package net.refractions.udig.tool.tests;

import org.eclipse.core.runtime.Plugin;
import org.junit.Ignore;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
@Ignore
public class TestsPlugin extends Plugin {

	//The shared instance.
	private static TestsPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public TestsPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static TestsPlugin getDefault() {
		return plugin;
	}

}
