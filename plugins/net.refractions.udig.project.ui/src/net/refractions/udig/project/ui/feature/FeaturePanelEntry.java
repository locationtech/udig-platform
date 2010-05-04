package net.refractions.udig.project.ui.feature;

import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.ui.IFeaturePanel;
import net.refractions.udig.project.ui.IFeaturePanelCheck;
import net.refractions.udig.project.ui.IFeatureSite;
import net.refractions.udig.project.ui.internal.FeatureTypeMatch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;

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