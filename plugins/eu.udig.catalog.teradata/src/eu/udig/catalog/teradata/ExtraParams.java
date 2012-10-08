/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
