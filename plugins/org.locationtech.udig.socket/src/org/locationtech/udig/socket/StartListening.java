/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.socket;

import org.eclipse.ui.IStartup;

/**
 * If the preferences indicate that it should start listening then this
 * startup will start the MapDaemon
 * @author jesse
 */
public class StartListening implements IStartup {

	public void earlyStartup() {
		new MapDaemon().start();
	}

}
