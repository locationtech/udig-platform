/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2008 IBM Corporation and others
 * ------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 * --------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.locationtech.udig.feature.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.locationtech.udig.feature.editor.FeatureEditorPlugin;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.IFeatureSite;
import org.locationtech.udig.project.ui.feature.FeaturePanelEntry;
import org.locationtech.udig.project.ui.feature.FeaturePanelProcessor;
import org.locationtech.udig.project.ui.feature.FeatureSiteImpl;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.views.properties.tabbed.TabbedPropertyViewStatusCodes;
import org.eclipse.ui.views.properties.tabbed.ISectionDescriptorProvider;
import org.eclipse.ui.views.properties.tabbed.ITypeMapper;
import org.opengis.feature.type.FeatureType;

/**
 * Provides information about the feature panel extension points. Each feature panel registry is
 * associated with a unique FeatureType.
 * 
 * @see TabbedPropertyRegistery
 */
@SuppressWarnings("unused")
public class FeaturePanelRegistry {

    private final static String NO_TAB_ERROR = "Schema {0} declares non-existing tab {1}";

    private final static String CONTRIBUTOR_ERROR = "Contributor {0} cannot be created.";

    private final static String TAB_ERROR = "Tab in {0} declares non-existing category {1}.";

    // extension point constants

    private static final String EXTPT_CONTRIBUTOR = "propertyContributor"; //$NON-NLS-1$

    private static final String EXTPT_TABS = "propertyTabs"; //$NON-NLS-1$

    private static final String EXTPT_SECTIONS = "propertySections"; //$NON-NLS-1$

    private static final String ELEMENT_TAB = "propertyTab"; //$NON-NLS-1$

    private static final String ELEMENT_SECTION = "propertySection"; //$NON-NLS-1$

    private static final String ELEMENT_PROPERTY_CATEGORY = "propertyCategory"; //$NON-NLS-1$

    private static final String ATT_CATEGORY = "category"; //$NON-NLS-1$

    private static final String ATT_CONTRIBUTOR_ID = "contributorId"; //$NON-NLS-1$

    private static final String ATT_TYPE_MAPPER = "typeMapper"; //$NON-NLS-1$	

    private static final String ATT_LABEL_PROVIDER = "labelProvider"; //$NON-NLS-1$

    private static final String ATT_ACTION_PROVIDER = "actionProvider"; //$NON-NLS-1$

    private static final String ATT_SECTION_DESCRIPTOR_PROVIDER = "sectionDescriptorProvider"; //$NON-NLS-1$

    private static final String ATT_TAB_DESCRIPTOR_PROVIDER = "tabDescriptorProvider"; //$NON-NLS-1$

    private static final String ATT_OVERRIDABLE_TAB_LIST_CONTENT_PROVIDER = "overridableTabListContentProvider"; //$NON-NLS-1$

    private static final String TOP = "top"; //$NON-NLS-1$

    protected FeatureType schema;

    // protected IConfigurationElement contributorConfigurationElement;

    protected ILabelProvider labelProvider;

    protected ActionProvider actionProvider;

    protected ITypeMapper typeMapper;

    protected ISectionDescriptorProvider sectionDescriptorProvider;

    protected List<FeaturePanelTabDescriptor> tabDescriptors;

    protected static final List<FeaturePanelTabDescriptor> EMPTY_DESCRIPTOR_ARRAY = Collections
            .emptyList();

    /**
     * There is one details registry for each contributor type.
     */
    protected FeaturePanelRegistry( FeatureType schema ) {
        // this.contributor = contributor;
        // this.schema = contributor.getSchema();
        this.schema = schema;

        FeaturePanelProcessor featurePanelProcessor = ProjectUIPlugin.getDefault()
                .getFeaturePanelProcessor();
        labelProvider = null; // new TabLabelProvider();

        List<FeaturePanelEntry> list = featurePanelProcessor.search(schema);
        for( FeaturePanelEntry entry : list ) {
            ILabelProvider titleProivder = entry.getLabelProvider();
            if (titleProivder != null) {
                labelProvider = new TabLabelProvider(titleProivder);
                break;
            }
        }

        actionProvider = null;
        typeMapper = null;

        if (schema == null) {
            handleConfigurationError(null, null);
            this.schema = null;
        }
    }

    /**
     * Handle the error when an issue is found loading from the configuration element.
     * 
     * @param message log message
     * @param exception an optional CoreException
     */
    private void handleConfigurationError( String message, Throwable t ) {
        IStatus status = new Status(t == null ? IStatus.WARNING : IStatus.ERROR,
                FeatureEditorPlugin.getDefault().getBundle().getSymbolicName(), IStatus.ERROR,
                message, t);
        FeatureEditorPlugin.getDefault().getLog().log(status);
    }

    /**
     * Returns the index of the given element in the array.
     */
    private int getIndex( Object[] array, Object target ) {
        for( int i = 0; i < array.length; i++ ) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return -1; // should never happen
    }

    /**
     * Returns all section descriptors for the provided selection.
     * <p>
     * This is an interesting one; the workbench part may be something have a list of Maps; with the
     * ISelection being a selected Map.
     * 
     * @param part the workbench part containing the selection
     * @param selection the current selection.
     * @return all section descriptors.
     */
    public List<FeaturePanelTabDescriptor> getTabDescriptors( IWorkbenchPart part,
            ISelection selection ) {

        if (selection == null || selection.isEmpty()) {
            return EMPTY_DESCRIPTOR_ARRAY;
        }
        List<FeaturePanelTabDescriptor> allDescriptors = getAllTabDescriptors();

        List<FeaturePanelTabDescriptor> result = filterTabDescriptors(allDescriptors, part,
                selection);
        return result;
    }

    /**
     * Filters out the tab descriptors that do not have any sections for the given input.
     */
    protected List<FeaturePanelTabDescriptor> filterTabDescriptors(
            List<FeaturePanelTabDescriptor> descriptors, IWorkbenchPart part, ISelection selection ) {
        IFeatureSite site = toFeatureSite(selection);
        if (site == null && part.getAdapter(IMap.class) != null) {
            IMap map = (IMap) part.getAdapter(IMap.class);
            site = new FeatureSiteImpl(map);
        } else {
            site = new FeatureSiteImpl(); // represents whatever is current?
        }

        List<FeaturePanelTabDescriptor> result = new ArrayList<FeaturePanelTabDescriptor>();
        for( FeaturePanelTabDescriptor descriptor : descriptors ) {
            FeaturePanelEntry entry = descriptor.getEntry();
            if (entry == null) {
                continue; // that is wrong!
            }
            if (entry.isMatch(schema)) {
                result.add(descriptor);
            } else if (entry.isChecked(site)) {
                result.add(descriptor);
            }
        }
        if (result.size() == 0) {
            return EMPTY_DESCRIPTOR_ARRAY;
        }
        return result;
    }

    private IFeatureSite toFeatureSite( ISelection selection ) {
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            IStructuredSelection selected = (IStructuredSelection) selection;
            for( Iterator< ? > iter = selected.iterator(); iter.hasNext(); ) {
                Object item = iter.next();
                if (item instanceof ILayer) {
                    return new FeatureSiteImpl((ILayer) item);
                } else if (item instanceof IMap) {
                    return new FeatureSiteImpl((IMap) item);
                } else if (item instanceof IAdaptable) {
                    IAdaptable adaptable = (IAdaptable) item;
                    ILayer layer = (ILayer) adaptable.getAdapter(ILayer.class);
                    if (layer != null) {
                        return new FeatureSiteImpl(layer);
                    }
                    IMap map = (IMap) adaptable.getAdapter(IMap.class);
                    if (map != null) {
                        return new FeatureSiteImpl(map);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Reads property tab extensions. Returns all tab descriptors for the current contributor id or
     * an empty array if none is found.
     */
    protected List<FeaturePanelTabDescriptor> getAllTabDescriptors() {
        if (tabDescriptors == null) {
            List<FeaturePanelTabDescriptor> temp = readTabDescriptors();
            temp = sortTabDescriptorsByAfterTab(temp);
            tabDescriptors = temp;
        }
        return tabDescriptors;
    }

    /**
     * Reads property tab extensions. Returns all tab descriptors for the current schema or an empty
     * list if none is found.
     */
    protected List<FeaturePanelTabDescriptor> readTabDescriptors() {
        List<FeaturePanelTabDescriptor> result = new ArrayList<FeaturePanelTabDescriptor>();
        FeaturePanelProcessor featurePanelProcessor = ProjectUIPlugin.getDefault()
                .getFeaturePanelProcessor();

        List<FeaturePanelEntry> list = featurePanelProcessor.entries();
        if (list.isEmpty()) {
            return result; // empty!
        }
        for( FeaturePanelEntry entry : list ) {
            FeaturePanelTabDescriptor descriptor = new FeaturePanelTabDescriptor(entry);
            result.add(descriptor);
        }
        return result;
    }

    /**
     * Sorts the tab descriptors in the given list according to afterTab.
     */
    protected List<FeaturePanelTabDescriptor> sortTabDescriptorsByAfterTab(
            List<FeaturePanelTabDescriptor> tabs ) {
        if (tabs.size() == 0) {
            return tabs;
        }
        List<FeaturePanelTabDescriptor> sorted = new ArrayList<FeaturePanelTabDescriptor>();
        sorted.addAll(tabs);
        Collections.sort(sorted, new Comparator<FeaturePanelTabDescriptor>(){
            public int compare( FeaturePanelTabDescriptor one, FeaturePanelTabDescriptor two ) {
                if (two.getAfterTab().equals(one.getId())) {
                    return -1;
                } else if (one.getAfterTab().equals(two.getId())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return sorted;
    }

    /**
     * Gets the type mapper for the contributor.
     * 
     * @return the type mapper for the contributor.
     */
    public ITypeMapper getTypeMapper() {
        return typeMapper;
    }

    /**
     * Gets the label provider for the contributor.
     * 
     * @return the label provider for the contributor.
     */
    public ILabelProvider getLabelProvider() {
        return labelProvider;
    }

    /**
     * Gets the action provider for the contributor.
     * 
     * @return the action provider for the contributor.
     */
    public ActionProvider getActionProvider() {
        return actionProvider;
    }

    /**
     * Gets the tab list content provider for the contributor.
     * 
     * @return the tab list content provider for the contributor.
     */
    public IStructuredContentProvider getTabListContentProvider() {
        return new FeaturePanelListContentProvider(this);
    }

    /**
     * Handle the tab error when an issue is found loading from the configuration element.
     * 
     * @param configurationElement the configuration element
     */
    private void handleTabError( IConfigurationElement configurationElement, String category ) {
        String pluginId = configurationElement.getDeclaringExtension().getNamespaceIdentifier();
        String message = java.text.MessageFormat
                .format(TAB_ERROR, new Object[]{pluginId, category});
        IStatus status = new Status(IStatus.ERROR, pluginId,
                TabbedPropertyViewStatusCodes.TAB_ERROR, message, null);
        FeatureEditorPlugin.getDefault().getLog().log(status);
    }
}
