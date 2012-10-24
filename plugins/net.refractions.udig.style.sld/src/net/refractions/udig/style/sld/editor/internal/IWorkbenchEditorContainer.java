/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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