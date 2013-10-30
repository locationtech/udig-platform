/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package eu.udig.catalog.teradata;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;

import net.refractions.udig.catalog.service.database.Tab;
import net.refractions.udig.core.Either;

public class ExtraParams implements Tab {

	@Override
	public boolean leavingPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Either<String, Map<String, Serializable>> getParams(
			Map<String, Serializable> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<URL> getResourceIDs(Map<String, Serializable> params) {
		return null;
	}

	@Override
	public void addListener(Listener modifyListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	public Control createControl(TabFolder tabFolder, int none) {
		// TODO Auto-generated method stub
		return null;
	}

}
