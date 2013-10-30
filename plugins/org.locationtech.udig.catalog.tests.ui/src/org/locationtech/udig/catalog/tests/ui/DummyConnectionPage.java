/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.ui;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.ui.AbstractUDIGImportPage;
import org.locationtech.udig.catalog.ui.UDIGConnectionPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DummyConnectionPage extends AbstractUDIGImportPage
	implements UDIGConnectionPage {

	public DummyConnectionPage() {
		super("dummy"); //$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		setControl(c);
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Serializable> getParams() {
		URL url = null;
		try {
			url = new URL("http://dummy.refractions.net"); //$NON-NLS-1$
		} 
		catch (MalformedURLException e) {}
		
		HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("dummy", url); //$NON-NLS-1$
		return map;
	}

	public List<URL> getURLs() {
		try {
			ArrayList<URL> list = new ArrayList<URL>();
			list.add(new URL("http://dummy.refractions.net")); //$NON-NLS-1$
			return list;
		} 
		catch (MalformedURLException e) {
			return null;
		}
	}

}
