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
package org.locationtech.udig.project.ui.feature;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.IFeaturePanel;
import org.locationtech.udig.project.ui.IFeaturePanelCheck;
import org.locationtech.udig.project.ui.IFeatureSite;
import org.locationtech.udig.project.ui.internal.FeatureTypeMatch;

/**
 * Represents an feature panel entry configured from the extension point.
 * <p>
 * Can perform several common tasks; and will lazily create a real FeaturePanel if needed. You
 * are responsible for managing the FeaturePanel at the end of the day.
 */
public class FeaturePanelEntry {
    /** Used on logging errors */
    private final String PLUGIN_ID;
    private String EXTENSION_ID;

    private String id;
    private String name;
    private String title;
    private String description;
    private String afterPanel;
    private IConfigurationElement definition;
    private boolean indented;
    private FeatureTypeMatch matcher;
    private String category;
    private IFeaturePanelCheck check;
    private ILabelProvider labelProvider;

    public FeaturePanelEntry( IExtension extension, IConfigurationElement definition ) {
        this.PLUGIN_ID = definition.getDeclaringExtension().getNamespaceIdentifier();
        if (extension.getUniqueIdentifier() == null) {
            this.EXTENSION_ID = "";
        } else {
            this.EXTENSION_ID = "(" + extension.getUniqueIdentifier() + ")";
        }
        this.definition = definition;
        id = definition.getAttribute("id");
        name = definition.getAttribute("name");
        title = definition.getAttribute("title");
        description = definition.getAttribute("description");

        this.definition = definition;

        IConfigurationElement featureTypeDefinition[] = definition.getChildren("featureType");//$NON-NLS-1$ 
        if (featureTypeDefinition.length == 1) {
            matcher = new FeatureTypeMatch(featureTypeDefinition[0]);
        } else {
            matcher = FeatureTypeMatch.ALL;
        }
        if( definition.getAttribute("labelProvider") != null ){
            try {
                labelProvider = (ILabelProvider) definition.createExecutableExtension("labelProvider");
            } catch (CoreException e) {
                String target = definition.getAttribute("panel");
                log("Could not create feature label provider" + target, e);
            }
        }
        // to be configured later
        indented = false;
        category = null;
    }
    /**
     * We are going to check against the FeaturePanelCheck if available.
     * 
     * @param site
     * @return true if the form should be used
     */
    public boolean isChecked( IFeatureSite site ) {
        if (site == null) {
            return false; // cannot check an empty site
        }
        if (check == null) {
            if (definition.getAttribute("check") == null) {
                check = IFeaturePanelCheck.NONE;
            } else {
                try {
                    check = (IFeaturePanelCheck) definition.createExecutableExtension("check");
                } catch (CoreException e) {
                    check = IFeaturePanelCheck.NONE; // fail!
                }
            }
        }
        return check.check(site);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Title to display (long form).
     * 
     * @return title if available, or getName as a backup
     */
    public String getTitle() {
        if (title == null || title.length() == 0) {
            return getName();
        }
        return title;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Used in "sorting" feature panels.
     * 
     * @return reference to another feature panel
     */
    public String getAfterPanel() {
        return afterPanel;
    }

    /**
     * Check the provided element (usually a simple feature) against the featureType
     * declairation for this FeaturePanelEntry. Returns true if the attributes match well enough
     * to display the featurePanel.
     * 
     * @param element
     * @return true if the feature panel can be used
     */
    public boolean isMatch( Object element ) {
        return matcher.isMatch(element);
    }

    public boolean isIndented() {
        return indented;
    }

    public String getCategory() {
        return category;
    }

    /**
     * Create an IFeaturePanel for use.
     * <p>
     * It is your responsibility to dispose the feature panel after creation.
     * </p>
     * Please respect the feature panel lifecycle:
     * <ul>
     * <li>constructor - is called by this method</li>
     * <li>init</li>
     * <li>createPartControl</li>
     * <li>dispose</li>
     * </ul>
     * 
     * @return IFeaturePanel
     */
    public IFeaturePanel createFeaturePanel() {
        try {
            return (IFeaturePanel) definition.createExecutableExtension("panel");
        } catch (CoreException e) {
            String target = definition.getAttribute("panel");
            log("Could not create feature " + target, e);
            return null;
        }
    }

    /**
     * Optional Label Provider if you would like to control
     * the appearance of your feature.
     * @return LabelProvider or null
     */
    public ILabelProvider getLabelProvider() {
        return labelProvider;
    }
    
    
    /**
     * Report an issue, blaming the plugin implementing the feature panel.
     * 
     * @param message
     * @param t
     */
    public void log( String message, Throwable t ) {
        IStatus error = new Status(IStatus.ERROR, PLUGIN_ID, message + EXTENSION_ID, t);
        ProjectPlugin.getPlugin().getLog().log(error);
    }

    public Image getImage() {
        return null;
    }

}
