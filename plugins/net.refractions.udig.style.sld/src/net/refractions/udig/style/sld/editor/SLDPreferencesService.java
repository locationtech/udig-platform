package net.refractions.udig.style.sld.editor;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.Preferences;

/**
 * TODO Purpose of
 * <p>
 * Yes, I know we aren't supposed to implement IPreferencesService, but this isn't a regular
 * circumstance; since we want to have the same functionality as the preference dialog without being
 * the preference dialog, this seems appropriate.
 * </p>
 * 
 * @author chorner
 * @since 1.1
 */
public class SLDPreferencesService implements IPreferencesService, IRegistryChangeListener {

    private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
    private static final String ELEMENT_SCOPE = "scope"; //$NON-NLS-1$

    private static SLDPreferencesService instance;
    static final SLDRootPreferences root = new SLDRootPreferences();
    private static final Map<String, Object> scopeRegistry = Collections.synchronizedMap(new HashMap<String, Object>());

    
    public SLDPreferencesService() {
        super();
        initializeScopes();
    }
    
    public String get( String key, String defaultValue, Preferences[] nodes ) {
        return null;
    }

    public boolean getBoolean( String qualifier, String key, boolean defaultValue, IScopeContext[] contexts ) {
        return false;
    }

    public byte[] getByteArray( String qualifier, String key, byte[] defaultValue, IScopeContext[] contexts ) {
        return null;
    }

    public double getDouble( String qualifier, String key, double defaultValue, IScopeContext[] contexts ) {
        return 0;
    }

    public float getFloat( String qualifier, String key, float defaultValue, IScopeContext[] contexts ) {
        return 0;
    }

    public int getInt( String qualifier, String key, int defaultValue, IScopeContext[] contexts ) {
        return 0;
    }

    public long getLong( String qualifier, String key, long defaultValue, IScopeContext[] contexts ) {
        return 0;
    }

    public String getString( String qualifier, String key, String defaultValue, IScopeContext[] contexts ) {
        return null;
    }

    public IEclipsePreferences getRootNode() {
        return null;
    }

    public IStatus exportPreferences( IEclipsePreferences node, OutputStream output, String[] excludesList ) throws CoreException {
        return null;
    }

    public IStatus importPreferences( InputStream input ) throws CoreException {
        return null;
    }

    public IStatus applyPreferences( IExportedPreferences preferences ) throws CoreException {
        return null;
    }

    public IExportedPreferences readPreferences( InputStream input ) throws CoreException {
        return null;
    }

    public String[] getDefaultLookupOrder( String qualifier, String key ) {
        return null;
    }

    public String[] getLookupOrder( String qualifier, String key ) {
        return null;
    }

    public void setDefaultLookupOrder( String qualifier, String key, String[] order ) {
    }

    public void exportPreferences( IEclipsePreferences node, IPreferenceFilter[] filters, OutputStream output ) throws CoreException {
    }

    public IPreferenceFilter[] matches( IEclipsePreferences node, IPreferenceFilter[] filters ) throws CoreException {
        return null;
    }

    public void applyPreferences( IEclipsePreferences node, IPreferenceFilter[] filters ) throws CoreException {
    }

    /**
     * See who is plugged into the extension point.
     */
    private void initializeScopes() {
        IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(StyleEditor.ID);
        if (point == null)
            return;
        IExtension[] extensions = point.getExtensions();
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            for (int j = 0; j < elements.length; j++)
                if (ELEMENT_SCOPE.equalsIgnoreCase(elements[j].getName()))
                    scopeAdded(elements[j]);
        }
        Platform.getExtensionRegistry().addRegistryChangeListener(this, StyleEditor.ID);
    }

    /*
     * Abstracted into a separate method to prepare for dynamic awareness.
     */
    static void scopeAdded(IConfigurationElement element) {
        String key = element.getAttribute(ATTRIBUTE_NAME);
        if (key == null) {
//            String message = NLS.bind(Messages.preferences_missingScopeAttribute, element.getDeclaringExtension().getUniqueIdentifier());
//            log(createStatusWarning(message, null));
            return;
        }
        scopeRegistry.put(key, element);
        root.addChild(key, null);
    }

    public void registryChanged( IRegistryChangeEvent event ) {
    }
   
    protected IEclipsePreferences createNode(String name) {
        IScope scope = null;
        Object value = scopeRegistry.get(name);
        if (value instanceof IConfigurationElement) {
            try {
                scope = (IScope) ((IConfigurationElement) value).createExecutableExtension(ATTRIBUTE_CLASS);
                scopeRegistry.put(name, scope);
            } catch (ClassCastException e) {
//                log(createStatusError(Messages.preferences_classCastScope, e));
                return new SLDPreferences(root, name);
            } catch (CoreException e) {
                log(e.getStatus());
                return new SLDPreferences(root, name);
            }
        } else
            scope = (IScope) value;
        return scope.create(root, name);
    }

    /*
     * Return the instance.
     */
    public static SLDPreferencesService getDefault() {
        if (instance == null)
            instance = new SLDPreferencesService();
        return instance;
    }
    
    static void log(IStatus status) {
        //InternalPlatform.getDefault().log(status);
    }
    
}
