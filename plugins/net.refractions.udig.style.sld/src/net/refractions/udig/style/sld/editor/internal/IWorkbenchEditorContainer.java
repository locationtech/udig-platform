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
package net.refractions.udig.style.sld.editor.internal;

public interface IWorkbenchEditorContainer {
		
		/**
		 * Open the page specified in the org.eclipse.ui.preferencePage
		 * extension point with id pageId. Apply data to it 
		 * when it is opened.
		 * @param preferencePageId String the id specified for a page in
		 *    the plugin.xml of its defining plug-in.
		 * @param data The data to be applied to the page when it 
		 * 		opens.
		 * @return boolean <code>true</code> if the page was
		 * opened successfully and data was applied.
		 */
		public boolean openPage(String preferencePageId, Object data);
		
}