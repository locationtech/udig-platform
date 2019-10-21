/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.core.AdapterUtil;
import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.core.internal.ExtensionPointUtil;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.style.IStyleConfigurator;
import org.locationtech.udig.style.sld.SLDPlugin;

public class EditorPageManager implements IExtensionChangeHandler {

    public static final String ATT_ID = "id"; //$NON-NLS-1$
    public static final String ATT_CLASS = "class"; //$NON-NLS-1$
    public static final String ATT_NAME = "name"; //$NON-NLS-1$
    public static final String ATT_LABEL = "label"; //$NON-NLS-1$
    public static final String PL_KEYWORDS = "keywords"; //$NON-NLS-1$
    
    /**
     * Pre-order traversal means visit the root first,
     * then the children.
     */
    public static final int PRE_ORDER = 0;

    /**
     * Post-order means visit the children, and then the root.
     */
    public static final int POST_ORDER = 1;

    /**
     * The root node.
     * Note that the root node is a special internal node
     * that is used to collect together all the nodes that
     * have no parent; it is not given out to clients.
     */
    EditorNode root = new EditorNode("");//$NON-NLS-1$

    /**
     * The path separator character.
     */
    String separator;

    /**
     * Creates a new preference manager.
     */
    public EditorPageManager() {
        this('.');
    }
    
    /**
     * Create a new instance of the receiver with the specified seperatorChar
     * 
     * @param separatorChar
     */
    public EditorPageManager(char separatorChar) {
        separator = new String(new char[] { separatorChar });
        
        IExtensionTracker tracker = PlatformUI.getWorkbench().getExtensionTracker();
        tracker.registerHandler(this, ExtensionTracker.createExtensionPointFilter(getExtensionPointFilter()));

        // add a listener for keyword deltas. If any occur clear all page caches
        Platform.getExtensionRegistry().addRegistryChangeListener(
                new IRegistryChangeListener() {

                    public void registryChanged(IRegistryChangeEvent event) {
                        if (event.getExtensionDeltas(StyleEditorPage.XPID, PL_KEYWORDS).length > 0) {
                            for (Iterator<?> j = getElements(
                                    PreferenceManager.POST_ORDER).iterator(); j
                                    .hasNext();) {
                                ((EditorNode) j.next())
                                        .clearKeywords();
                            }
                        }
                    }
                });
    }

    /**
     * Add the pages and the groups to the receiver.
     * 
     * @param pageContributions
     */
    public void addPages(Collection<?> pageContributions) {

        // Add the contributions to the manager
        Iterator<?> iterator = pageContributions.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof EditorNode) {
                EditorNode node = (EditorNode) next;
                addToRoot(node);
                registerNode(node);
            }
        }

    }

    /**
     * Register a node with the extension tracker.
     * 
     * @param node
     *            register the given node and its subnodes with the extension
     *            tracker
     */
    public void registerNode(EditorNode node) {
        PlatformUI.getWorkbench().getExtensionTracker().registerObject(
                node.getConfigurationElement().getDeclaringExtension(), node,
                IExtensionTracker.REF_WEAK);
        EditorNode[] subNodes = node.getSubNodes();
        for (int i = 0; i < subNodes.length; i++) {
            registerNode((EditorNode) subNodes[i]);
        }

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.dynamicHelpers.IExtensionChangeHandler#addExtension(org.eclipse.core.runtime.dynamicHelpers.IExtensionTracker, org.eclipse.core.runtime.IExtension)
     */
    public void addExtension(IExtensionTracker tracker, IExtension extension) {
        
        IConfigurationElement[] elements = extension.getConfigurationElements();
        for (int i = 0; i < elements.length; i++) {
            EditorNode node = null;
            
            boolean nameMissing = elements[i].getAttribute(ATT_NAME) == null;
            String id = elements[i].getAttribute(ATT_ID);       
            boolean classMissing = getClassValue(elements[i], ATT_CLASS) == null;

            //System.out.println(elements[i].id+","+nameMissing+","+classMissing);
            if (!(nameMissing || id == null || classMissing)) {
                node = new EditorNode(id, elements[i]);
            }
            
            if (node == null)
                continue;
            registerNode(node);
            String category = node.getCategory();
            if (category == null) {
                addToRoot(node);
            } else {
                EditorNode parent = null;
                for (Iterator<?> j = getElements(PreferenceManager.POST_ORDER)
                        .iterator(); j.hasNext();) {
                    EditorNode element = (EditorNode) j.next();
                    if (category.equals(element.getId())) {
                        parent = element;
                        break;
                    }
                }
                if (parent == null) {
                    //TODO: log error
                    // Could not find the parent - log
//                    WorkbenchPlugin
//                            .log("Invalid preference page path: " + category); //$NON-NLS-1$
                    addToRoot(node);
                } else {
                    parent.add(node);
                }
            }
        }
    }
    
    public String getClassValue(IConfigurationElement configElement, String classAttributeName) {
        String className = configElement.getAttribute(classAttributeName);
        if (className != null) 
            return className;
        IConfigurationElement [] candidateChildren = configElement.getChildren(classAttributeName);
        if (candidateChildren.length == 0) 
            return null;
    
        return candidateChildren[0].getAttribute(ATT_CLASS);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.dynamicHelpers.IExtensionAdditionHandler#getExtensionPointFilter()
     */
    private IExtensionPoint getExtensionPointFilter() {
        return Platform.getExtensionRegistry().getExtensionPoint(StyleEditorPage.XPID);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.dynamicHelpers.IExtensionChangeHandler#removeExtension(org.eclipse.core.runtime.IExtension, java.lang.Object[])
     */
    public void removeExtension(IExtension extension, Object[] objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof EditorNode) {
                EditorNode node = (EditorNode) objects[i];
                node.disposeResources();
                deepRemove(getRoot(), node);
            }
        }
    }

    /**
     * Removes the node from the manager, searching through all subnodes.
     * 
     * @param parent
     *            the node to search
     * @param nodeToRemove
     *            the node to remove
     * @return whether the node was removed
     */
    private boolean deepRemove(EditorNode parent,
            EditorNode nodeToRemove) {
        if (parent == nodeToRemove)
            if (parent == getRoot()) {
                removeAll(); // we're removing the root
                return true;
            }

        if (parent.remove(nodeToRemove))
            return true;

        EditorNode[] subNodes = parent.getSubNodes();
        for (int i = 0; i < subNodes.length; i++) {
            if (deepRemove(subNodes[i], nodeToRemove))
                return true;
        }
        return false;
    }
    

    /**
     * Adds the given preference node as a subnode of the
     * node at the given path.
     *
     * @param path the path
     * @param node the node to add
     * @return <code>true</code> if the add was successful,
     *  and <code>false</code> if there is no contribution at
     *  the given path
     */
    public boolean addTo(String path, EditorNode node) {
        EditorNode target = find(path);
        if (target == null)
            return false;
        target.add(node);
        return true;
    }

    /**
     * Adds the given preference node as a subnode of the
     * root.
     *
     * @param node the node to add, which must implement 
     *   <code>SLDEditorPageNode</code>
     */
    public void addToRoot(EditorNode node) {
        Assert.isNotNull(node);
        root.add(node);
    }

    /**
     * Recursively enumerates all nodes at or below the given node
     * and adds them to the given list in the given order.
     * 
     * @param node the starting node
     * @param sequence a read-write list of preference nodes
     *  (element type: <code>SLDEditorPageNode</code>)
     *  in the given order
     * @param order the traversal order, one of 
     *	<code>PRE_ORDER</code> and <code>POST_ORDER</code>
     */
    protected void buildSequence(EditorNode node, List<EditorNode> sequence, int order) {
        if (order == PRE_ORDER)
            sequence.add(node);
        EditorNode[] subnodes = node.getSubNodes();
        for (int i = 0; i < subnodes.length; i++) {
            buildSequence(subnodes[i], sequence, order);
        }
        if (order == POST_ORDER)
            sequence.add(node);
    }

    /**
     * Finds and returns the contribution node at the given path.
     *
     * @param path the path
     * @return the node, or <code>null</code> if none
     */
    public EditorNode find(String path) {
       return find(path,root);
    }
    
    /**
     * Finds and returns the preference node directly
     * below the top at the given path.
     *
     * @param path the path
     * @return the node, or <code>null</code> if none
     * 
     * @since 3.1
     */
    protected EditorNode find(String path, EditorNode top){
    	 Assert.isNotNull(path);
         StringTokenizer stok = new StringTokenizer(path, separator);
         EditorNode node = top;
         while (stok.hasMoreTokens()) {
             String id = stok.nextToken();
             node = node.findSubNode(id);
             if (node == null)
                 return null;
         }
         if (node == top)
             return null;
         return node;
    }

    /**
     * Returns all preference nodes managed by this
     * manager.
     *
     * @param order the traversal order, one of 
     *	<code>PRE_ORDER</code> and <code>POST_ORDER</code>
     * @return a list of preference nodes
     *  (element type: <code>SLDEditorPageNode</code>)
     *  in the given order
     */
    public List<?> getElements(int order) {
        Assert.isTrue(order == PRE_ORDER || order == POST_ORDER,
                "invalid traversal order");//$NON-NLS-1$
        ArrayList<EditorNode> sequence = new ArrayList<EditorNode>();
        EditorNode[] subnodes = getRoot().getSubNodes();
        for (int i = 0; i < subnodes.length; i++)
            buildSequence(subnodes[i], sequence, order);
        return sequence;
    }

    /**
     * Returns the root node.
     * Note that the root node is a special internal node
     * that is used to collect together all the nodes that
     * have no parent; it is not given out to clients.
     *
     * @return the root node
     */
    public EditorNode getRoot() {
        return root;
    }

    /**
     * Removes the prefernece node at the given path.
     *
     * @param path the path
     * @return the node that was removed, or <code>null</code>
     *  if there was no node at the given path
     */
    public EditorNode remove(String path) {
        Assert.isNotNull(path);
        int index = path.lastIndexOf(separator);
        if (index == -1)
            return root.remove(path);
        // Make sure that the last character in the string isn't the "."
        Assert.isTrue(index < path.length() - 1, "Path can not end with a dot");//$NON-NLS-1$
        String parentPath = path.substring(0, index);
        String id = path.substring(index + 1);
        EditorNode parentNode = find(parentPath);
        if (parentNode == null)
            return null;
        return parentNode.remove(id);
    }

    /**
     * Removes the given prefreence node if it is managed by
     * this contribution manager.
     *
     * @param node the node to remove
     * @return <code>true</code> if the node was removed,
     *  and <code>false</code> otherwise
     */
    public boolean remove(EditorNode node) {
        Assert.isNotNull(node);

        return root.remove(node);
    }

    /**
     * Removes all contribution nodes known to this manager.
     */
    public void removeAll() {
        root = new EditorNode("");//$NON-NLS-1$
    }
    
    public EditorNode[] getRootSubNodes() {
    	return getRoot().getSubNodes();
    }
    
    /**
     * Returns true if the specified node exists in the manager.
     *
     * @param nodeId Unique identified for a node
     * @return boolean
     */
    public boolean hasNode(String nodeId) {
        EditorNode[] nodes = getRoot().getSubNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].getId().equalsIgnoreCase(nodeId))
                return true;
        }
        return false;
    }

    static boolean meetsRequirement(ILayer selectedLayer, String id, IConfigurationElement element, EditorNode node ) {
        String requires = element.getAttribute(OpenStyleEditorAction.ATT_REQUIRES);
        try {
            Object classInstance = EditorNode.createExtension(element, EditorNode.ATT_CLASS);
            // first try creating required class using extension classloading 
            // if this fails use the same classloader as the configurator
         
            // Failed trying to recover by using the configurators class's class loader
            if (AdapterUtil.instance.canAdaptTo(requires, selectedLayer, classInstance.getClass().getClassLoader())) {
                SLDPlugin.trace("skipped "+id, null); //$NON-NLS-1$
                return true;
            }
            try{
                Object requiredClass = element.createExecutableExtension(OpenStyleEditorAction.ATT_REQUIRES);
                if ( AdapterUtil.instance.canAdaptTo(requires, selectedLayer, requiredClass.getClass().getClassLoader())) {
                    return true;
                }
                
            }catch( Exception ce ){
                SLDPlugin.trace("skipped "+id, null); //$NON-NLS-1$
                
            }
        } catch (Exception e) {
            SLDPlugin.log("extProcConfigurator skipped " //$NON-NLS-1$
                          + id + " (couldn't find " //$NON-NLS-1$
                          + requires + ")", null); //$NON-NLS-1$
        }
        return false;
    }

    /**
     * Creates the default {@link EditorPageManager} implementation and loads the style pages for the layer into
     * the manager.
     * 
     * @param plugin the plug-in to send error messages to.
     * @param selectedLayer the layer to use to filter the style pages 
     * @return the default {@link EditorPageManager} implementation and loads the style pages for the layer into
     * the manager.
     */
    public static EditorPageManager loadManager(Plugin plugin, ILayer selectedLayer) {
        final EditorPageManager[] manager = new EditorPageManager[] {new EditorPageManager('.')};
        
        ExtensionPointProcessor extProcPage = new StyleEditorPageExtensionProcessor(manager, selectedLayer);
        ExtensionPointUtil.process(plugin, StyleEditorPage.XPID, extProcPage);
    
        ExtensionPointProcessor extProcConfigurator = new StyleConfiguratorExtensionProcessor(manager, selectedLayer);
        ExtensionPointUtil.process(plugin, IStyleConfigurator.XPID, extProcConfigurator);
        return manager[0];
    }

}
