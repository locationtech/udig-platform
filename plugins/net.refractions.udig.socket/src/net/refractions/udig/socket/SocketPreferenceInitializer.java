package net.refractions.udig.socket;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class SocketPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(SocketPreferenceConstants.RUN_ON_STARTUP, false);
		store.setDefault(SocketPreferenceConstants.MAP_DAEMON_PORT, "14921");
	}

}
