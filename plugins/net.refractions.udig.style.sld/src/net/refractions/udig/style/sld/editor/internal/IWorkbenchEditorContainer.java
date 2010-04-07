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