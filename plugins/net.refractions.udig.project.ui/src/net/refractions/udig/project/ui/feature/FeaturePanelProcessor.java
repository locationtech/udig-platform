package net.refractions.udig.project.ui.feature;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.ui.IFeaturePanel;
import net.refractions.udig.project.ui.internal.FeatureTypeMatch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Utility class assisting in processing the feature panel extension point.
 * <p>
 * This class simply processes the extension point; and use used by FeatureView and FeatureWizard
 * who are responsible for actually keeping a hold of the resulting FeaturePanel instances.
 * <p>
 * Internally this class supports lazy creation of actual feature panels.
 * 
 * @author Jody Garnett
 * @since 1.2.0
 */
public class FeaturePanelProcessor {

    private static final String FEATURE_PANEL_ID = "net.refractions.udig.project.ui.featurePanel"; //$NON-NLS-1$
    private static List<FeaturePanelEntry> featurePanelList;

    public FeaturePanelProcessor() {
        featurePanelList = new ArrayList<FeaturePanelEntry>();
        ExtensionPointUtil.process(ProjectPlugin.getPlugin(), FEATURE_PANEL_ID,
                new ExtensionPointProcessor(){
                    public void process( IExtension extension, IConfigurationElement element )
                            throws Exception {
                        FeaturePanelEntry entry = new FeaturePanelEntry(extension, element);
                        featurePanelList.add(entry);
                    }
                }
        );
    }
    /**
     * List of FeaturePanelEntry that can match the provided element (usually a SimpleFeature).
     * <p>
     * You can use these FeaturePanelEntry to make feature panels to edit the indicated element.
     * 
     * @param element
     * @return List matching FeaturePanelEntry
     */
    public List<FeaturePanelEntry> search( Object element ){
        List<FeaturePanelEntry> search = new ArrayList<FeaturePanelEntry>();
        for( FeaturePanelEntry entry : featurePanelList ){
            if( entry.isMatch( element )){
                search.add( entry );
            }
        }
        return search;
    }

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

        private FeatureTypeMatch matcher;

        public FeaturePanelEntry( IExtension extension, IConfigurationElement definition ) {
            this.PLUGIN_ID = extension.getContributor().getName();
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
        boolean isMatch( Object element ) {
            return matcher.isMatch(element);
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
        IFeaturePanel createFeaturePanel() {
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
    }
}
