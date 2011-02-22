package net.refractions.udig.project.ui.internal.wizard.url;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.ServiceFactoryImpl;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Amr Alam TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class URLWizardPage extends WizardPage implements ModifyListener, UDIGConnectionPage {

    final static String[] types = {};
    /** <code>url</code> field */
    protected Combo url;
    private static final String URL_WIZARD = "URL_WIZARD"; //$NON-NLS-1$
    private static final String URL_RECENT = "URL_RECENT"; //$NON-NLS-1$
    private IDialogSettings settings;
    private static final int COMBO_HISTORY_LENGTH = 15;

    /**
     * Construct <code>URLWizardPage</code>.
     */
    public URLWizardPage() {
        super(Messages.URLWizardPage_title);

        settings = ProjectUIPlugin.getDefault().getDialogSettings().getSection(URL_WIZARD);
        if (settings == null) {
            settings = ProjectUIPlugin.getDefault().getDialogSettings().addNewSection(URL_WIZARD);
        }
    }

    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }
    public boolean canProcess( Object object ) {
        URL url = CatalogPlugin.locateURL(object);
        if (url == null) {
            return false;
        }
        return true;
    }

    public Map<String, Serializable> toParams( Object object ) {
        return null;
    }

    public void createControl( Composite parent ) {
        String[] recentURLs = settings.getArray(URL_RECENT);
        if (recentURLs == null) {
            recentURLs = new String[0];
        }

        GridData gridData;
        Composite composite = new Composite(parent, SWT.NULL);

        GridLayout gridLayout = new GridLayout();
        int columns = 1;
        gridLayout.numColumns = columns;
        composite.setLayout(gridLayout);

        gridData = new GridData();

        Label urlLabel = new Label(composite, SWT.NONE);
        urlLabel.setText(Messages.URLWizardPage_label_url_text);
        urlLabel.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.widthHint = 400;

        url = new Combo(composite, SWT.BORDER);
        url.setItems(recentURLs);
        url.setVisibleItemCount(15);
        url.setLayoutData(gridData);
        url.setText("http://"); //$NON-NLS-1$
        url.addModifyListener(this);

        setControl(composite);
        setPageComplete(true);
    }

    public boolean isPageComplete() {
        try {
            new URL(url.getText());
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    public boolean canFlipToNextPage() {
        // return canFlip;
        IWizardPage[] pages = getWizard().getPages();
        return isPageComplete() && !pages[pages.length - 1].equals(this);
    }

    /**
     * Double click in list, or return from url control.
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     * @param e
     */
    public void widgetDefaultSelected( SelectionEvent e ) {
        e.getClass();// kill warning
        if (getWizard().canFinish()) {
            getWizard().performFinish();
        }
    }

    /**
     * This should be called using the Wizard .. job when next/finish is pressed.
     */
    public List<IService> getResources( IProgressMonitor monitor ) throws Exception {
        URL location = new URL(url.getText());
        ServiceFactoryImpl serviceFactory = new ServiceFactoryImpl();
        List<IService> services = serviceFactory.acquire(location);
        /*
         * Success! Store the URL in history.
         */
        saveWidgetValues();
        return services;
    }

    public void modifyText( ModifyEvent e ) {
        try {
            new URL(url.getText());
            setErrorMessage(null);
        } catch (MalformedURLException exception) {
            setErrorMessage(Messages.URLWizardPage_error_invalidURL);
        }
        getWizard().getContainer().updateButtons();
    }

    /**
     * Saves the widget values
     */
    private void saveWidgetValues() {
        // Update history
        if (settings != null) {
            String[] recentURLs = settings.getArray(URL_RECENT);
            if (recentURLs == null) {
                recentURLs = new String[0];
            }
            recentURLs = addToHistory(recentURLs, url.getText());
            settings.put(URL_RECENT, recentURLs);
        }
    }

    /**
     * Adds an entry to a history, while taking care of duplicate history items and excessively long
     * histories. The assumption is made that all histories should be of length
     * <code>COMBO_HISTORY_LENGTH</code>.
     *
     * @param history the current history
     * @param newEntry the entry to add to the history
     * @return the history with the new entry appended Stolen from
     *         org.eclipse.team.internal.ccvs.ui.wizards.ConfigurationWizardMainPage
     */
    private String[] addToHistory( String[] history, String newEntry ) {
        ArrayList<String> l = new ArrayList<String>(Arrays.asList(history));
        addToHistory(l, newEntry);
        String[] r = new String[l.size()];
        l.toArray(r);
        return r;
    }

    /**
     * Adds an entry to a history, while taking care of duplicate history items and excessively long
     * histories. The assumption is made that all histories should be of length
     * <code>COMBO_HISTORY_LENGTH</code>.
     *
     * @param history the current history
     * @param newEntry the entry to add to the history Stolen from
     *        org.eclipse.team.internal.ccvs.ui.wizards.ConfigurationWizardMainPage
     */
    private void addToHistory( List<String> history, String newEntry ) {
        history.remove(newEntry);
        history.add(0, newEntry);

        // since only one new item was added, we can be over the limit
        // by at most one item
        if (history.size() > COMBO_HISTORY_LENGTH)
            history.remove(COMBO_HISTORY_LENGTH);
    }

    public Map<String, Serializable> getParams() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<URL> getURLs() {
        // TODO Auto-generated method stub
        return null;
    }
}
