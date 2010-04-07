package net.refractions.udig.style.sld.editor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class SLDRootPreferences extends SLDPreferences {

        /**
         * Default constructor.
         */
        public SLDRootPreferences() {
            super(null, ""); //$NON-NLS-1$
        }

        /*
         * @see org.osgi.service.prefs.Preferences#flush()
         */
        public void flush() throws BackingStoreException {
            // flush all children
            BackingStoreException exception = null;
            String[] names = childrenNames();
            for (int i = 0; i < names.length; i++) {
                try {
                    node(names[i]).flush();
                } catch (BackingStoreException e) {
                    // store the first exception we get and still try and flush
                    // the rest of the children.
                    if (e != null)
                        exception = e;
                }
            }
            if (exception != null)
                throw exception;
        }

        /*
         * @see EclipsePreferences#getChild(String, Plugin)
         */
        protected synchronized IEclipsePreferences getChild(String key, Plugin context) {
            Object value = null;
            IEclipsePreferences child = null;
            if (children != null)
                value = children.get(key);
            if (value != null) {
                if (value instanceof IEclipsePreferences)
                    return (IEclipsePreferences) value;
                //lazy initialization
                child = ((SLDPreferencesService) Platform.getPreferencesService()).createNode(key);
                addChild(key, child);
            }
            return child;
        }

        /*
         * @see EclipsePreferences#getChildren()
         */
        protected synchronized IEclipsePreferences[] getChildren() throws BackingStoreException {
            //must perform lazy initialization of child nodes
            String[] childNames = childrenNames();
            IEclipsePreferences[] childNodes = new IEclipsePreferences[childNames.length];
            for (int i = 0; i < childNames.length; i++)
                childNodes[i] = getChild(childNames[i], null);
            return childNodes;
        }

        /*
         * @see Preferences#node(String)
         */
        public Preferences node(String path) {
            if (path.length() == 0 || (path.length() == 1 && path.charAt(0) == IPath.SEPARATOR))
                return this;
            int startIndex = path.charAt(0) == IPath.SEPARATOR ? 1 : 0;
            int endIndex = path.indexOf(IPath.SEPARATOR, startIndex + 1);
            String scope = path.substring(startIndex, endIndex == -1 ? path.length() : endIndex);
            IEclipsePreferences child = getChild(scope, null);
            if (child == null) {
                child = new SLDPreferences(this, scope);
                addChild(scope, child);
            }
            return child.node(endIndex == -1 ? "" : path.substring(endIndex + 1)); //$NON-NLS-1$
        }

        /*
         * @see org.osgi.service.prefs.Preferences#sync()
         */
        public void sync() throws BackingStoreException {
            // sync all children
            BackingStoreException exception = null;
            String[] names = childrenNames();
            for (int i = 0; i < names.length; i++) {
                try {
                    node(names[i]).sync();
                } catch (BackingStoreException e) {
                    // store the first exception we get and still try and sync
                    // the rest of the children.
                    if (e != null)
                        exception = e;
                }
            }
            if (exception != null)
                throw exception;
        }
}
