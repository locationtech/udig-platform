package net.refractions.udig.catalog.ui.preferences;

import net.refractions.udig.catalog.ui.wizard.DataBaseRegistryWizardPage;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class AbstractProprietaryDatastoreWizardPage extends DataBaseRegistryWizardPage {

    public AbstractProprietaryDatastoreWizardPage(String wizardPageTitle) {
        super(wizardPageTitle);
    }
    private AbstractProprietaryJarPreferencePage preferences;

    public final void createControl(final Composite parent) {
        preferences = getPreferencePage();
        preferences.setListener(new Listener() {

            public void handleEvent(Event event) {
                getControl().dispose();
                advanced = null;
                advancedKey = null;
                host = null;
                pass = null;
                port = null;
                schema = null;
                user = null;
                createControl(parent);
                parent.layout();
                setMessage(getRestartMessage());
            }

        });
        if (!preferences.installed()) {
            setMessage(getDriversMessage());
            preferences.createControl(parent);
            GridData data = new GridData();
            data.grabExcessHorizontalSpace = true;
            data.grabExcessVerticalSpace = true;
            preferences.getControl().setLayoutData(data);
            setControl(preferences.getControl());
        } else {
            super.createControl(parent);
            doCreateWizardPage(parent);
        }
    }

    @Override
    public final boolean isPageComplete() {
        if (!preferences.installed())
            return false;

        return doIsPageComplete();
    }

    protected abstract boolean doIsPageComplete();

    /**
     * Called by createControl() to create wizard page must NOT call {@link #createControl(Composite)}.
     *
     * @param parent
     */
    protected abstract void doCreateWizardPage(Composite parent);
    protected abstract AbstractProprietaryJarPreferencePage getPreferencePage();

    /**
     * Example: "An error will occur because the Oracle drivers were not loaded.  Please restart application"
     *
     * @return restart message
     */
    protected abstract String getRestartMessage();
    /**
     * Example: "Install Oracle drivers for client"
     *
     * @return message indicating that drivers need to be loaded.
     */
    protected abstract String getDriversMessage();
}
