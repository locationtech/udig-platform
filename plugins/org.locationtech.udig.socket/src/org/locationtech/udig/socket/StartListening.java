/**
 * 
 */
package org.locationtech.udig.socket;

import org.eclipse.ui.IStartup;

/**
 * If the preferences indicate that it should start listening then this
 * startup will start the MapDaemon
 * @author jesse
 */
public class StartListening implements IStartup {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		new MapDaemon().start();
	}

}
