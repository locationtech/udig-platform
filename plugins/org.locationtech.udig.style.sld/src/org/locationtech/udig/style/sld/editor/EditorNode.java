/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.registry.KeywordRegistry;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.locationtech.udig.style.IStyleConfigurator;
import org.locationtech.udig.style.sld.IEditorPage;
import org.locationtech.udig.style.sld.IEditorPageContainer;
import org.locationtech.udig.style.sld.IStyleEditorPage;
import org.locationtech.udig.style.sld.SLDPlugin;
import org.locationtech.udig.style.sld.editor.internal.IEditorNode;

/**
 * Abstract IEditorNode used as the abstract class for all page nodes.
 * <p>
 * This class implements IPluginContribution allowing the plug-in defining
 * this EditorNode to be held accountable.
 */
@SuppressWarnings("restriction")
public class EditorNode implements IPluginContribution, IEditorNode {

        private static final String TAG_KEYWORD_REFERENCE = "keywordReference"; //$NON-NLS-1$
        public static final String ATT_CLASS = "class"; //$NON-NLS-1$
        public static final String ATT_ID = "id"; //$NON-NLS-1$
        public static final String ATT_ICON = "icon"; //$NON-NLS-1$
        public static final String ATT_LABEL = "label"; //$NON-NLS-1$
        public static final String ATT_CATEGORY = "category"; //$NON-NLS-1$
        public static final String ATT_REQUIRES = "requires"; //$NON-NLS-1$
        
        private Collection<String> keywordReferences;
        
        private IConfigurationElement configurationElement;

        private ImageDescriptor imageDescriptor;

        private Image image;

        /**
         * Editor page, or <code>null</code> if not yet loaded.
         */
        private IEditorPage page;

        /**
         * The list of subnodes (immediate children) of this node (element type: <code>SLDEditorPageNode</code>).
         */
        private List<EditorNode> subNodes;

        /**
         * Name of class that implements <code>SLDEditorPageNode</code>, or <code>null</code> if none.
         */
        private String classname;

        /**
         * The id of this node.
         */
        private String id;

        /**
         * Text label for this node. Note that this field is only used prior to the creation of the preference page.
         */
        private String label;

        private Collection<String> keywordLabelCache;
        
        /**
         * Create a new instance of the receiver.
         * 
         * @param id
         * @param configurationElement 
         */
        public EditorNode(String id, IConfigurationElement configurationElement) {
        	this(id);
        	this.configurationElement = configurationElement;
        }

        /**
         * Get the ids of the keywords the receiver is bound to.
         * 
         * @return Collection of <code>String</code>.  Never <code>null</code>.
         */
        public Collection<String> getKeywordReferences() {
            if (keywordReferences == null) {
                IConfigurationElement[] references = getConfigurationElement()
                        .getChildren(TAG_KEYWORD_REFERENCE);
                HashSet<String> list = new HashSet<String>(references.length);
                for (int i = 0; i < references.length; i++) {
                    IConfigurationElement page = references[i];
                    String id = page.getAttribute(ATT_ID);
                    if (id != null)
                        list.add(id);
                }

                if (!list.isEmpty())
                    keywordReferences = list;
                else
                    keywordReferences = Collections.emptySet();
                
            }
            return keywordReferences;
        }

        /**
         * Get the labels of all of the keywords of the receiver.
         * 
         * @return Collection of <code>String</code>.  Never <code>null</code>.
         */
        public Collection<String> getKeywordLabels() {
            if (keywordLabelCache != null)
                return keywordLabelCache;
            
            Collection<String> refs = getKeywordReferences();
            
            if(refs == Collections.EMPTY_SET) {
                keywordLabelCache = Collections.emptySet(); 
                return keywordLabelCache;
            }
            
            keywordLabelCache = new ArrayList<String>(refs.size());
            Iterator<String> referenceIterator = refs.iterator();
            while(referenceIterator.hasNext()){
                String label = KeywordRegistry.getInstance().getKeywordLabel(
                        (String) referenceIterator.next());
                if(label != null)
                    keywordLabelCache.add(label);
            }
            
            return keywordLabelCache;
        }
        
        /**
         * Clear the keyword cache, if any.
         */
        public void clearKeywords() {
            keywordLabelCache = null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.SLDEditorPageNode#disposeResources()
         */
        public void disposeResources() {
            if (image != null) {
                image.dispose();
                image = null;
            }
            if (page != null) {
                page.dispose();
                page = null;
            }
            configurationElement = null;
            imageDescriptor = null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.SLDEditorPageNode#getLabelImage()
         */
        public Image getLabelImage() {      
            if (image == null) {
                ImageDescriptor desc = getImageDescriptor();
                if (desc != null)
                    image = imageDescriptor.createImage();
            }
            return image;
        }


        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.SLDEditorPageNode#getLabelText()
         */
        public String getLabelText() {
            return getConfigurationElement().getAttribute(ATT_LABEL);
        }

        /**
         * Returns the image descriptor for this node.
         * 
         * @return the image descriptor
         */
        public ImageDescriptor getImageDescriptor() {
            if (imageDescriptor != null) 
                return imageDescriptor;
            
            String imageName = getConfigurationElement().getAttribute(ATT_ICON);
            if (imageName != null) {
                String contributingPluginId = getConfigurationElement().getNamespaceIdentifier();
                imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(contributingPluginId, imageName);
            }
            return imageDescriptor;
        }
        
        /**
         * Return the configuration element.
         * 
         * @return the configuration element
         */
        public IConfigurationElement getConfigurationElement() {
            return configurationElement;
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.ui.activities.support.IPluginContribution#getLocalId()
         */
        public String getLocalId() {
            return getId();
        }

        /* (non-Javadoc)
         * @see org.eclipse.ui.activities.support.IPluginContribution#getPluginId()
         */
        public String getPluginId() {
            return getConfigurationElement().getNamespaceIdentifier();
        }
        
        /**
         * Creates the page this node stands for.
         * The page is created, but its composite won't exist 
         */
        public void createPage(Composite parent, IEditorPageContainer container) {
            StyleEditorPage page;
            try {
                Object pageInstance = createExtension(getConfigurationElement(), ATT_CLASS);
                if (pageInstance instanceof IStyleConfigurator) {
                    page = new StyleEditorPageAdapter((IStyleConfigurator) pageInstance);
                } else if (pageInstance instanceof StyleEditorPage) {
                    page = (StyleEditorPage) pageInstance; 
                } else {
                    //TODO: log
                    return;
                }
            } catch (CoreException e) {
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
            page.setContainer(container);
            page.init(PlatformUI.getWorkbench());
            if (page.getControl() == null) {
                //create the control
                page.createControl(parent);
            }
            if (getLabelImage() != null)
                page.setImageDescriptor(getImageDescriptor());
            page.setTitle(getLabelText());
            setPage(page);
        }

        /**
         * Return the category name for the node.
         * @return java.lang.String
         */
        public String getCategory() {
            return getConfigurationElement().getAttribute(ATT_CATEGORY);
        }   
        
        /**
         * Return the required class the layer must resolve to.
         *
         * @return java.lang.String (Class)
         */
        public String getRequires() {
            String requires = null;
            try {
                requires = getConfigurationElement().getAttribute(ATT_REQUIRES);
            } catch (InvalidRegistryObjectException e) {
            }
            return requires;
        }
        
        /**
         * Creates a new preference node with the given id. The new node has no
         * subnodes.
         * 
         * @param id
         *            the node id
         */
        public EditorNode(String id) {
            Assert.isNotNull(id);
            this.id = id;
        }

        /**
         * Creates a preference node with the given id, label, and image, and
         * lazily-loaded preference page. The preference node assumes (sole)
         * responsibility for disposing of the image; this will happen when the node
         * is disposed.
         * 
         * @param id
         *            the node id
         * @param label
         *            the label used to display the node in the preference dialog's
         *            tree
         * @param image
         *            the image displayed left of the label in the preference
         *            dialog's tree, or <code>null</code> if none
         * @param className
         *            the class name of the preference page; this class must
         *            implement <code>IPreferencePage</code>
         */
        public EditorNode(String id, String label, ImageDescriptor image,
                String className) {
            this(id);
            this.imageDescriptor = image;
            Assert.isNotNull(label);
            this.label = label;
            this.classname = className;
        }

        /**
         * Creates an editor node with the given id and editor page. The
         * title of the editor page is used for the node label. The node will
         * not have an image.
         * 
         * @param id
         *            the node id
         * @param page
         *            the editor page
         */
        public EditorNode(String id, IEditorPage page) {
            this(id);
            Assert.isNotNull(page);
            this.page = page;
        }

        /*
         * (non-Javadoc) Method declared on SLDEditorPageNode.
         */
        public void add(EditorNode node) {
            if (subNodes == null)
                subNodes = new ArrayList<EditorNode>();
            subNodes.add(node);
        }

        /*
         * (non-Javadoc) Method declared on IContributionNode.
         */
        public EditorNode findSubNode(String id) {
            Assert.isNotNull(id);
            Assert.isTrue(id.length() > 0);
            if (subNodes == null)
                return null;
            int size = subNodes.size();
            for (int i = 0; i < size; i++) {
                EditorNode node = (EditorNode) subNodes.get(i);
                if (id.equals(node.getId()))
                    return node;
            }
            return null;
        }

        /*
         * (non-Javadoc) Method declared on SLDEditorPageNode.
         */
        public String getId() {
            return this.id;
        }

        /*
         * (non-Javadoc) Method declared on SLDEditorPageNode.
         */
        public IEditorPage getPage() {
            return page;
        }

        /*
         * (non-Javadoc) Method declared on SLDEditorPageNode.
         */
        public EditorNode[] getSubNodes() {
            if (subNodes == null)
                return new EditorNode[0];
            return (EditorNode[]) subNodes
                    .toArray(new EditorNode[subNodes.size()]);
        }

        /*
         * (non-Javadoc) Method declared on SLDEditorPageNode.
         */
        public EditorNode remove(String id) {
            EditorNode node = findSubNode(id);
            if (node != null)
                remove(node);
            return node;
        }

        /*
         * (non-Javadoc) Method declared on SLDEditorPageNode.
         */
        public boolean remove(EditorNode node) {
            if (subNodes == null)
                return false;
            return subNodes.remove(node);
        }

        /**
         * Set the current page to be newPage.
         * 
         * @param newPage
         */
        public void setPage(IStyleEditorPage newPage) {
            page = newPage;
        }

		public void add(IEditorNode node) {
			System.out.println("ADD called, but no code here"); //$NON-NLS-1$
		}

		public boolean remove(IEditorNode node) {
			return false;
		}

        /**
         * Creates an extension.  If the extension plugin has not
         * been loaded a busy cursor will be activated during the duration of
         * the load.
         *
         * @param element the config element defining the extension
         * @param classAttribute the name of the attribute carrying the class
         * @return the extension object
         * @throws CoreException if the extension cannot be created
         */
        public static Object createExtension(final IConfigurationElement element,
                final String classAttribute) throws CoreException {
            try {
                // If plugin has been loaded create extension.
                // Otherwise, show busy cursor then create extension.
                if (BundleUtility.isActivated(element.getDeclaringExtension()
                        .getNamespaceIdentifier())) {
                    return element.createExecutableExtension(classAttribute);
                }
                final Object[] ret = new Object[1];
                final CoreException[] exc = new CoreException[1];
                BusyIndicator.showWhile(null, new Runnable() {
                    public void run() {
                        try {
                            ret[0] = element
                                    .createExecutableExtension(classAttribute);
                        } catch (CoreException e) {
                            exc[0] = e;
                        }
                    }
                });
                if (exc[0] != null)
                    throw exc[0];
                return ret[0];

            } catch (CoreException core) {
                throw core;
            } catch (Exception e) {
                throw new CoreException(new Status(IStatus.ERROR, SLDPlugin.ID,
                        IStatus.ERROR, WorkbenchMessages.WorkbenchPlugin_extension,e));
            }
        }

        public String getClassname() {
            return classname;
        }

        public String getLabel() {
            return label;
        }
}
